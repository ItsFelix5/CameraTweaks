package cameratweaks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.function.Function;

public class Util {
    public static final Minecraft client = Minecraft.getInstance();
    public static final ClientInput input = new KeyboardInput(client.options);

    public static boolean isMoving() {
        return input.keyPresses.forward() || input.keyPresses.backward() || input.keyPresses.left() || input.keyPresses.right();
    }

    public static Vec3 rotate(Vec3 vec, double yaw) {
        final double sin = Math.sin(Math.toRadians(yaw));
        final double cos = Math.cos(Math.toRadians(yaw));
        return new Vec3(cos * vec.x - sin * vec.z, vec.y, cos * vec.z + sin * vec.x);
    }

    public static class Pos {
        public final ResourceKey<Level> dimension;
        public Vec3 pos;
        public float pitch;
        public float yaw;
        public int fov;

        public Pos(ResourceKey<Level> dimension, Vec3 pos, float pitch, float yaw, int fov) {
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
        public Vec3 prevPos;
        public float prevPitch;
        public float prevYaw;

        public LerpedPos(ResourceKey<Level> dimension, Vec3 pos, float pitch, float yaw, int fov) {
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

        public Vec3 getPos(float tickDelta) {
            return prevPos.lerp(pos, tickDelta);
        }

        public float getPitch(float tickDelta) {
            return Mth.rotLerp(tickDelta, prevPitch, pitch);
        }

        public float getYaw(float tickDelta) {
            return Mth.rotLerp(tickDelta, prevYaw, yaw);
        }
    }

    public static class Lerped {
        private float prev = 0F;
        private float current = 0F;

        public void approach(float target, float step) {
            if (Float.isNaN(step) || Float.isInfinite(step)) prev = current = target;
            else if (target > current) tick(Math.min(current + step, target));
            else tick(Math.max(current - step, target));
        }

        public void tick(float newValue) {
            this.prev = this.current;
            this.current = newValue;
        }

        public void modify(Function<Float, Float> func) {
            this.prev = func.apply(this.prev);
            this.current = func.apply(this.current);
        }

        public float get(float tickDelta) {
            return Mth.lerp(tickDelta, prev, current);
        }
    }
}
