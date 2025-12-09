package cameratweaks;

import static cameratweaks.Util.client;

public class Freelook {
    public static boolean enabled = false;
    public static float yaw, pitch;
    public static int pauseTicks;

    public static void start() {
        enabled = true;
        yaw = client.getCameraEntity().getYRot();
        pitch = client.getCameraEntity().getXRot();
    }

    public static void stop() {
        pauseTicks = 0;
    }

    public static void pause() {
        if(Freelook.enabled && ThirdPerson.current != null && !ThirdPerson.current.rotatePlayer) {
            client.player.setXRot(Freelook.pitch);
            client.player.setYRot(Freelook.yaw);
            Keybinds.freelook.setEnabled(false);
            pauseTicks = 60;
        }
    }

    public static void tick() {
        if(pauseTicks > 0 && --pauseTicks == 0 && ThirdPerson.current != null && !ThirdPerson.current.rotatePlayer) Keybinds.freelook.setEnabled(true);
    }
}
