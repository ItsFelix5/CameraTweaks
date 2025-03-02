package cameratweaks;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;

public class Util {
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static final Input input = new KeyboardInput(client.options);

    public static boolean isMoving() {
        return input.playerInput.forward() || input.playerInput.backward() || input.playerInput.left() || input.playerInput.right();
    }

    public static Vec3d rotate(Vec3d vec, double yaw) {
        final double sin = Math.sin(Math.toRadians(yaw));
        final double cos = Math.cos(Math.toRadians(yaw));
        return new Vec3d(cos * vec.x - sin * vec.z, vec.y, cos * vec.z + sin * vec.x);
    }

    public static class Pos implements Cloneable {
        public final RegistryKey<World> dimension;
        public Vec3d pos;
        public float pitch;
        public float yaw;
        public int fov;

        public Pos(RegistryKey<World> dimension, Vec3d pos, float pitch, float yaw, int fov) {
            this.dimension = dimension;
            this.pos = pos;
            this.pitch = pitch;
            this.yaw = yaw;
            this.fov = fov;
        }

        @Override
        public Pos clone() {
            try {
                return (Pos) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Pos pos1)) return false;
            return Float.compare(pitch, pos1.pitch) == 0 && Float.compare(yaw, pos1.yaw) == 0 && fov == pos1.fov && Objects.equals(dimension, pos1.dimension) && Objects.equals(pos, pos1.pos);
        }
    }
}
