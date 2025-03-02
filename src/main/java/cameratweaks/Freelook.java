package cameratweaks;

import static cameratweaks.Util.client;

public class Freelook {
    public static boolean enabled = false;
    public static float yaw, pitch;
    public static int pauseTicks;

    public static void start() {
        enabled = true;
        yaw = client.cameraEntity.getYaw();
        pitch = client.cameraEntity.getPitch();
    }

    public static void stop() {
        pauseTicks = 0;
    }

    public static void pause() {
        if(Freelook.enabled && ThirdPerson.current != null && !ThirdPerson.current.rotatePlayer) {
            client.player.setPitch(Freelook.pitch);
            client.player.setYaw(Freelook.yaw);
            Keybinds.freelook.setEnabled(false);
            pauseTicks = 60;
        }
    }

    public static void tick() {
        if(pauseTicks > 0) {
            if(--pauseTicks == 0) Keybinds.freelook.setEnabled(true);
        }
    }
}
