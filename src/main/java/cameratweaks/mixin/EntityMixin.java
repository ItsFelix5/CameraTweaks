package cameratweaks.mixin;

import cameratweaks.Freelook;
import cameratweaks.ThirdPerson;
import cameratweaks.Util;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract void setYaw(float yaw);

    @Shadow @Nullable public abstract LivingEntity getControllingPassenger();

    @SuppressWarnings("ConstantValue")
    @Redirect(method = "updateVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;movementInputToVelocity(Lnet/minecraft/util/math/Vec3d;FF)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
        double d = movementInput.lengthSquared();
        if (d < 1.0E-7) return Vec3d.ZERO;
        boolean isFree = Freelook.enabled && (this.getControllingPassenger() instanceof ClientPlayerEntity || (Object) this instanceof ClientPlayerEntity)
                && ThirdPerson.current != null && !ThirdPerson.current.rotatePlayer;
        if (isFree) yaw = Freelook.yaw;
        Vec3d rotated = Util.rotate((d > 1.0 ? movementInput.normalize() : movementInput).multiply(speed), yaw);
        if (isFree) setYaw((float) Math.toDegrees(Math.atan2(rotated.z, rotated.x)) - 90);
        return rotated;
    }
}
