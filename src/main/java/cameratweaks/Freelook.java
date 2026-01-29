package cameratweaks;

import cameratweaks.config.Config;

import static cameratweaks.Util.client;

public class Freelook {
    public static State state = State.INACTIVE;
    public static float yaw, pitch;
    public static int pauseTicks;

    public static void start() {
        state = State.FREELOOKING;
        enable();
        if(Config.get().freelookTogglePerspective) ThirdPerson.setCurrent(Config.get().thirdPersons.getFirst());
    }

    public static void enable() {
        yaw = client.getCameraEntity().getYRot();
        pitch = client.getCameraEntity().getXRot();
    }

    public static void stop() {
        state = State.ANIMATING;
        pauseTicks = 0;
        if(Config.get().freelookTogglePerspective) ThirdPerson.setCurrent(null);
    }

    public static void pause() {
        if(Freelook.state == State.THIRD_PERSON) {
            client.player.setXRot(Freelook.pitch);
            client.player.setYRot(Freelook.yaw);
            state = State.PAUSED;
            pauseTicks = 60;
        }
    }

    public static void tick() {
        if(state == State.PAUSED && --pauseTicks == 0) {
            state = State.THIRD_PERSON;
            enable();
        }
    }

    public enum State {
        INACTIVE,
        FREELOOKING,
        PAUSED,
        ANIMATING,
        THIRD_PERSON;

        public boolean active() {
            return this != INACTIVE && this != PAUSED;
        }
    }
}
