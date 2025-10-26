package cameratweaks;

import cameratweaks.config.Config;
import com.mojang.datafixers.util.Either;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.input.Input;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.waypoint.EntityTickProgress;
import net.minecraft.world.waypoint.TrackedWaypoint;
import net.minecraft.world.waypoint.Waypoint;

import static cameratweaks.Util.*;

@SuppressWarnings("DataFlowIssue")
public class Freecam {
    private static final Util.Pos[] cameras = new Util.Pos[9];
    public static Util.LerpedPos pos;
    public static float speed;

    public static void enable() {
        client.chunkCullingEnabled = false;
        speed = 1f;
        setPosition();
        client.player.networkHandler.getWaypointHandler().onTrack(new FreecamWaypoint());
        if (!Keybinds.playerMovement.enabled()) cameraMovement();
    }

    public static void disable() {
        client.chunkCullingEnabled = true;
        pos = null;
        client.player.networkHandler.getWaypointHandler().onUntrack(new FreecamWaypoint());
        if (!Keybinds.playerMovement.enabled()) playerMovement();
    }

    public static void playerMovement() {
        client.player.input = input;
        if(pos != null) pos.tick();
    }

    public static void cameraMovement() {
        if (!Keybinds.freecam.enabled()) return;
        client.player.input = new Input();
    }

    public static void loadCamera(int i) {
        if (cameras[i] == null) {
            if(Config.get().alternateFreecam) {
                setPosition();
                cameras[i] = pos.toStatic();
                client.player.sendMessage(Text.translatable("cameratweaks.freecam.camera.saved", i + 1), true);
            } else client.player.sendMessage(Text.translatable("cameratweaks.freecam.camera.unknown", i + 1, Keybinds.playerMovement.getBoundKeyLocalizedText(), i + 1), true);
            return;
        }
        if (cameras[i].dimension != client.world.getRegistryKey()) {
            client.player.sendMessage(Text.translatable("cameratweaks.freecam.camera.incorrect_dimension", i + 1, cameras[i].dimension.getValue().getPath().replace('_', ' ')), true);
            return;
        }
        if (!Keybinds.freecam.enabled()) Keybinds.freecam.setEnabled(true);
        pos = cameras[i].toLerped();
    }

    public static void saveCamera(int i) {
        if(Config.get().alternateFreecam) {
            if(pos != null && pos.equals(cameras[i])) {
                Entity camera = client.getCameraEntity();
                int fov = ThirdPerson.current == null || !ThirdPerson.current.changedFov? client.options.getFov().getValue() : ThirdPerson.current.fov;
                cameras[i] = new Util.Pos(client.world.getRegistryKey(), camera.getEyePos(), camera.getPitch(), camera.getYaw(), fov);
            } else if (cameras[i] != null){
                cameras[i] = null;
                client.player.sendMessage(Text.translatable("cameratweaks.freecam.camera.removed", i + 1), true);
            }
        } else {
            setPosition();
            cameras[i] = pos.toStatic();
            client.player.sendMessage(Text.translatable("cameratweaks.freecam.camera.saved", i + 1), true);
        }
    }

    private static void setPosition() {
        Camera camera = client.gameRenderer.getCamera();
        int fov = ThirdPerson.current == null || !ThirdPerson.current.changedFov? client.options.getFov().getValue() : ThirdPerson.current.fov;
        pos = new Util.LerpedPos(client.world.getRegistryKey(), camera.getPos(), camera.getPitch(), camera.getYaw(), fov);
    }

    public static void update(float delta) {
        if (!Keybinds.freecam.enabled() || Keybinds.playerMovement.enabled()) return;
        pos.tick();
        double vertical = (((input.playerInput.jump() ? 1 : 0) - (input.playerInput.sneak() ? 1 : 0)));
        if(!isMoving() && vertical == 0) return;
        pos.pos = pos.pos.add(Util.rotate(new Vec3d(input.getMovementInput().x, vertical, input.getMovementInput().y * (input.playerInput.sprint() ? 2 : 1)).multiply(delta * speed), pos.yaw));
    }

    public static void reset() {
        Keybinds.freecam.setEnabled(false);
        pos = null;
        for (int i = 0; i < 9; i++) cameras[i] = null;
    }

    private static class FreecamWaypoint extends TrackedWaypoint {
        FreecamWaypoint() {
            super(Either.right("freecamPlayer"), new Waypoint.Config().withTeamColorOf(client.player), null);
        }

        @Override
        public void handleUpdate(TrackedWaypoint waypoint) {}
        @Override
        public void writeAdditionalDataToBuf(ByteBuf buf) {}

        @Override
        public double getRelativeYaw(World world, YawProvider yawProvider, EntityTickProgress tickProgress) {
            Vec3d vec3d = yawProvider.getCameraPos().subtract(client.getCameraEntity().getEntityPos()).rotateYClockwise();
            return MathHelper.subtractAngles(yawProvider.getCameraYaw(),
                    (float) MathHelper.atan2(vec3d.getZ(), vec3d.getX()) * MathHelper.DEGREES_PER_RADIAN);
        }

        @Override
        public Pitch getPitch(World world, PitchProvider cameraProvider, EntityTickProgress tickProgress) {
            Vec3d vec3d = cameraProvider.project(client.getCameraEntity().getEntityPos());
            boolean bl = vec3d.z > 1.0;
            double d = bl ? -vec3d.y : vec3d.y;
            if (d < -1.0 || (bl && vec3d.y < 0.0)) return TrackedWaypoint.Pitch.DOWN;
            if (d > 1.0 || (bl && vec3d.y > 0.0)) return TrackedWaypoint.Pitch.UP;

            return TrackedWaypoint.Pitch.NONE;
        }

        @Override
        public double squaredDistanceTo(Entity receiver) {
            return receiver.squaredDistanceTo(client.getCameraEntity().getEntityPos());
        }
    }
}
