package cameratweaks;

import cameratweaks.config.Config;
import com.mojang.datafixers.util.Either;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Camera;
import net.minecraft.client.player.ClientInput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.waypoints.PartialTickSupplier;
import net.minecraft.world.waypoints.TrackedWaypoint;
import net.minecraft.world.waypoints.Waypoint;

import static cameratweaks.Util.*;

@SuppressWarnings("DataFlowIssue")
public class Freecam {
    private static final Util.Pos[] cameras = new Util.Pos[9];
    public static Util.LerpedPos pos;
    public static float speed;

    public static void enable() {
        client.smartCull = false;
        speed = 1f;
        setPosition();
        client.player.connection.getWaypointManager().trackWaypoint(new FreecamWaypoint());
        if (!Keybinds.playerMovement.enabled()) cameraMovement();
    }

    public static void disable() {
        client.smartCull = true;
        pos = null;
        client.player.connection.getWaypointManager().untrackWaypoint(new FreecamWaypoint());
        if (!Keybinds.playerMovement.enabled()) playerMovement();
    }

    public static void playerMovement() {
        client.player.input = input;
        if(pos != null) pos.tick();
    }

    public static void cameraMovement() {
        if (!Keybinds.freecam.enabled()) return;
        client.player.input = new ClientInput();
    }

    public static void loadCamera(int i) {
        if (cameras[i] == null) {
            if(Config.get().alternateFreecam) {
                setPosition();
                cameras[i] = pos.toStatic();
                client.player.displayClientMessage(Component.translatable("cameratweaks.freecam.camera.saved", i + 1), true);
            } else client.player.displayClientMessage(Component.translatable("cameratweaks.freecam.camera.unknown", i + 1, Keybinds.playerMovement.getTranslatedKeyMessage(), i + 1), true);
            return;
        }
        if (cameras[i].dimension != client.level.dimension()) {
            client.player.displayClientMessage(Component.translatable("cameratweaks.freecam.camera.incorrect_dimension", i + 1, cameras[i].dimension.identifier().getPath().replace('_', ' ')), true);
            return;
        }
        if (!Keybinds.freecam.enabled()) Keybinds.freecam.setEnabled(true);
        pos = cameras[i].toLerped();
    }

    public static void saveCamera(int i) {
        if(Config.get().alternateFreecam) {
            if(pos != null && pos.equals(cameras[i])) {
                Entity camera = client.getCameraEntity();
                int fov = ThirdPerson.current == null || !ThirdPerson.current.changedFov? client.options.fov().get() : ThirdPerson.current.fov;
                cameras[i] = new Util.Pos(client.level.dimension(), camera.getEyePosition(), camera.getXRot(), camera.getYRot(), fov);
            } else if (cameras[i] != null){
                cameras[i] = null;
                client.player.displayClientMessage(Component.translatable("cameratweaks.freecam.camera.removed", i + 1), true);
            }
        } else {
            setPosition();
            cameras[i] = pos.toStatic();
            client.player.displayClientMessage(Component.translatable("cameratweaks.freecam.camera.saved", i + 1), true);
        }
    }

    private static void setPosition() {
        Camera camera = client.gameRenderer.getMainCamera();
        int fov = ThirdPerson.current == null || !ThirdPerson.current.changedFov? client.options.fov().get() : ThirdPerson.current.fov;
        pos = new Util.LerpedPos(client.level.dimension(), camera.position(), camera.xRot(), camera.yaw(), fov);
    }

    public static void update(float delta) {
        if (!Keybinds.freecam.enabled() || Keybinds.playerMovement.enabled()) return;
        pos.tick();
        double vertical = (((input.keyPresses.jump() ? 1 : 0) - (input.keyPresses.shift() ? 1 : 0)));
        if(!isMoving() && vertical == 0) return;
        pos.pos = pos.pos.add(Util.rotate(new Vec3(input.getMoveVector().x, vertical, input.getMoveVector().y * (input.keyPresses.sprint() ? 2 : 1)).scale(delta * speed), pos.yaw));
    }

    public static void reset() {
        Keybinds.freecam.setEnabled(false);
        pos = null;
        for (int i = 0; i < 9; i++) cameras[i] = null;
    }

    private static class FreecamWaypoint extends TrackedWaypoint {
        FreecamWaypoint() {
            super(Either.right("freecamPlayer"), new Waypoint.Icon().cloneAndAssignStyle(client.player), null);
        }

        @Override
        public void update(TrackedWaypoint waypoint) {}
        @Override
        public void writeContents(ByteBuf buf) {}

        @Override
        public double yawAngleToCamera(Level level, Camera camera, PartialTickSupplier partialTickSupplier) {
            Vec3 vec3 = camera.position().subtract(client.getCameraEntity().position()).rotateClockwise90();
            return Mth.degreesDifference(camera.yaw(), (float) Mth.atan2(vec3.z(), vec3.x()) * Mth.DEG_TO_RAD);
        }

        @Override
        public PitchDirection pitchDirectionToCamera(Level level, Projector projector, PartialTickSupplier partialTickSupplier) {
            Vec3 vec3 = projector.projectPointToScreen(client.getCameraEntity().position());
            boolean bl = vec3.z > 1.0;
            double d = bl ? -vec3.y : vec3.y;
            if (d < -1.0 || (bl && vec3.y < 0.0)) return TrackedWaypoint.PitchDirection.DOWN;
            if (d > 1.0 || (bl && vec3.y > 0.0)) return TrackedWaypoint.PitchDirection.UP;

            return TrackedWaypoint.PitchDirection.NONE;
        }

        @Override
        public double distanceSquared(Entity entity) {
            return entity.distanceToSqr(client.getCameraEntity().position());
        }
    }
}
