package cameratweaks.mixin;

import cameratweaks.Freelook;
import cameratweaks.Keybinds;
import cameratweaks.ThirdPerson;
import cameratweaks.Util;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract @Nullable LivingEntity getControllingPassenger();

    @Shadow
    public abstract void setYRot(float f);

    @Redirect(method = "moveRelative", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getInputVector(Lnet/minecraft/world/phys/Vec3;FF)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 movementInputToVelocity(Vec3 movementInput, float speed, float yaw) {
        double d = movementInput.lengthSqr();
        if (d < 1.0E-7) return Vec3.ZERO;
        boolean isFree = Keybinds.freelook.enabled() && (this.getControllingPassenger() instanceof LocalPlayer || (Object) this instanceof LocalPlayer)
                && ThirdPerson.current != null && !ThirdPerson.current.rotatePlayer;
        if (isFree) yaw = Freelook.yaw;
        Vec3 rotated = Util.rotate((d > 1.0 ? movementInput.normalize() : movementInput).scale(speed), yaw);
        if (isFree) setYRot((float) Math.toDegrees(Math.atan2(rotated.z, rotated.x)) - 90);
        return rotated;
    }
}
