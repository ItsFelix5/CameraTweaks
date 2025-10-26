package cameratweaks;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.function.Function;

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

    public static float approach(float value, float target, float step) {
        if (target > value) return Math.min(value + step, target);
        if (target < value) return Math.max(value - step, target);
        return target;
    }

    public static class Pos {
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

        public LerpedPos toLerped() {
            return new LerpedPos(dimension, pos, pitch, yaw, fov);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Pos pos1)) return false;
            return Float.compare(pitch, pos1.pitch) == 0 && Float.compare(yaw, pos1.yaw) == 0 && fov == pos1.fov && Objects.equals(dimension, pos1.dimension) && Objects.equals(pos, pos1.pos);
        }
    }

    public static class LerpedPos extends Pos {
        public Vec3d prevPos;
        public float prevPitch;
        public float prevYaw;

        public LerpedPos(RegistryKey<World> dimension, Vec3d pos, float pitch, float yaw, int fov) {
            super(dimension, pos, pitch, yaw, fov);
            this.prevPos = pos;
            this.prevPitch = pitch;
            this.prevYaw = yaw;
        }

        public Pos toStatic() {
            return new Pos(dimension, prevPos, prevPitch, prevYaw, fov);
        }

        public void tick() {
            this.prevPos = this.pos;
            this.prevPitch = this.pitch;
            this.prevYaw = this.yaw;
        }

        public Vec3d getPos(float tickDelta) {
            return prevPos.lerp(pos, tickDelta);
        }

        public float getPitch(float tickDelta) {
            return MathHelper.lerpAngleDegrees(tickDelta, prevPitch, pitch);
        }

        public float getYaw(float tickDelta) {
            return MathHelper.lerpAngleDegrees(tickDelta, prevYaw, yaw);
        }
    }

    public static class Lerped {
        private float prev = 0F;
        private float current = 0F;

        public void tick(float newValue) {
            this.prev = this.current;
            this.current = newValue;
        }

        public void modify(Function<Float, Float> func) {
            this.prev = func.apply(this.prev);
            this.current = func.apply(this.current);
        }

        public float get(float tickDelta) {
            return MathHelper.lerp(tickDelta, prev, current);
        }
    }
}
