package cameratweaks.mixin;

import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorseEntity.class)
public class AbstractHorseEntityMixin {
    @Inject(method = "getControlledMovementInput", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    public void getControlledMovementInput(PlayerEntity controllingPlayer, Vec3d movementInput, CallbackInfoReturnable<Vec3d> cir){
        cir.setReturnValue(new Vec3d(controllingPlayer.sidewaysSpeed, 0.0, controllingPlayer.forwardSpeed));
    }
}
