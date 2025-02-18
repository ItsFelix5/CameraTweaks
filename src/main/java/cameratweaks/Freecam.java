package cameratweaks;

import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

import static cameratweaks.Util.client;

@SuppressWarnings("DataFlowIssue")
public class Freecam {
    private static final Input input = new KeyboardInput(client.options);
    private static final Util.Pos[] cameras = new Util.Pos[9];
    public static Util.Pos prev;
    public static Util.Pos pos;
    public static float speed;

    public static void enable() {
        client.chunkCullingEnabled = false;
        client.gameRenderer.setRenderHand(false);
        speed = 1f;
        setPosition();
        if (!Keybinds.playerMovement.enabled()) cameraMovement();
    }

    public static void disable() {
        client.chunkCullingEnabled = true;
        client.gameRenderer.setRenderHand(true);
        pos = null;
        if (!Keybinds.playerMovement.enabled()) playerMovement();
    }

    public static void playerMovement() {
        client.player.input = input;
        prev = pos;
    }

    public static void cameraMovement() {
        if (!Keybinds.freecam.enabled()) return;
        client.player.input = new Input();
    }

    public static void loadCamera(int i) {
        if (cameras[i] == null) {
            if(Config.HANDLER.instance().alternateFreecam) {
                cameras[i] = pos.clone();
                client.player.sendMessage(Text.translatable("cameratweaks.freecam.camera.saved", i + 1), true);
            } else client.player.sendMessage(Text.translatable("cameratweaks.freecam.camera.unknown", i + 1, Keybinds.playerMovement.getBoundKeyLocalizedText(), i + 1), true);
            return;
        }
        if (cameras[i].dimension != client.world.getRegistryKey()) {
            client.player.sendMessage(Text.translatable("cameratweaks.freecam.camera.incorrect_dimension", i + 1, cameras[i].dimension.getValue().getPath().replace('_', ' ')), true);
            return;
        }
        if (!Keybinds.freecam.enabled()) Keybinds.freecam.setEnabled(true);
        prev = pos = cameras[i].clone();
    }

    public static void saveCamera(int i) {
        if(Config.HANDLER.instance().alternateFreecam) {
            if(pos.equals(cameras[i])) {
                Entity camera = client.cameraEntity;
                int fov = ThirdPerson.current == null || !ThirdPerson.current.changedFov? client.options.getFov().getValue() : ThirdPerson.current.fov;
                cameras[i] = new Util.Pos(client.world.getRegistryKey(), camera.getEyePos(), camera.getPitch(), camera.getYaw(), fov);
            } else if (cameras[i] != null){
                cameras[i] = null;
                client.player.sendMessage(Text.translatable("cameratweaks.freecam.camera.removed", i + 1), true);
            }
        } else {
            cameras[i] = pos.clone();
            client.player.sendMessage(Text.translatable("cameratweaks.freecam.camera.saved", i + 1), true);
        }
    }

    private static void setPosition() {
        Camera camera = client.gameRenderer.getCamera();
        int fov = ThirdPerson.current == null || !ThirdPerson.current.changedFov? client.options.getFov().getValue() : ThirdPerson.current.fov;
        prev = pos = new Util.Pos(client.world.getRegistryKey(), camera.getPos(), camera.getPitch(), camera.getYaw(), fov);
    }

    public static void update(float delta) {
        if (!Keybinds.freecam.enabled() || Keybinds.playerMovement.enabled()) return;
        input.tick();
        prev = pos;
        delta *= speed;
        final double forward = input.movementForward * (input.playerInput.sprint() ? 2 : 1) * delta;
        final double sideways = input.movementSideways * delta;
        final double vertical = (((input.playerInput.jump() ? 1 : 0) - (input.playerInput.sneak() ? 1 : 0))) * delta;
        if (forward == 0 && sideways == 0 && vertical == 0) return;
        final double sin = Math.sin(Math.toRadians(pos.yaw));
        final double cos = Math.cos(Math.toRadians(pos.yaw));
        pos.pos = pos.pos.add(cos * sideways - sin * forward, vertical, cos * forward + sin * sideways);
    }

    public static void reset() {
        Keybinds.freecam.setEnabled(false);
        prev = pos = null;
        for (int i = 0; i < 9; i++) cameras[i] = null;
    }
}
