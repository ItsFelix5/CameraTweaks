package cameratweaks;

import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Util {
    public static final MinecraftClient client = MinecraftClient.getInstance();

    @FunctionalInterface
    public interface Callback {
        void run();
    }

    public static class Pos {
        public final RegistryKey<World> dimension;
        public Vec3d pos;
        public float pitch;
        public float yaw;

        public Pos(RegistryKey<World> dimension, Vec3d pos, float pitch, float yaw) {
            this.dimension = dimension;
            this.pos = pos;
            this.pitch = pitch;
            this.yaw = yaw;
        }
    }
}
