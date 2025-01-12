package cameratweaks;

import static cameratweaks.Util.client;

public class Freelook {
    public static boolean enabled = false;
    public static float yaw, pitch;

    public static void start() {
        enabled = true;
        yaw = client.cameraEntity.getYaw();
        pitch = client.cameraEntity.getPitch();
    }

    public static void stop() {
    }
}
