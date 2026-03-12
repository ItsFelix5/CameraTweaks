package cameratweaks;

import cameratweaks.config.Config;

import static cameratweaks.Util.client;

public class Freelook {
    public static State state = State.INACTIVE;
    public static float yaw, pitch;
    private static ThirdPerson thirdPerson;

    public static void start() {
        state = State.FREELOOKING;
        thirdPerson = ThirdPerson.current;
        enable();
        if(Config.get().freelookTogglePerspective) ThirdPerson.setCurrent(Config.get().thirdPersons.getFirst());
    }

    public static void enable() {
        yaw = client.getCameraEntity().getYRot();
        pitch = client.getCameraEntity().getXRot();
        if (client.options.keyUse.isDown()) pause();
    }

    public static void stop() {
        state = State.ANIMATING;
        if(Config.get().freelookTogglePerspective) ThirdPerson.setCurrent(thirdPerson);
        if (ThirdPerson.current != null && !ThirdPerson.current.rotatePlayer) {
            Freelook.state = State.THIRD_PERSON;
            if(client.options.keyUse.isDown()) pause();
        }
    }

    public static void pause() {
        if(Freelook.state == State.THIRD_PERSON) {
            client.player.setXRot(Freelook.pitch);
            client.player.setYRot(Freelook.yaw);
            state = State.PAUSED;
        }
    }

    public static void tick() {
        if(state == State.PAUSED && !client.options.keyUse.isDown()) {
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
