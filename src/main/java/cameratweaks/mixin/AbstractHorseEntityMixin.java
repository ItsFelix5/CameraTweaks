package cameratweaks.mixin;

import cameratweaks.Freelook;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public class AbstractHorseEntityMixin {
    @Inject(method = "getRiddenInput", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    public void getControlledMovementInput(Player player, Vec3 vec3, CallbackInfoReturnable<Vec3> cir){
        if(Freelook.state == Freelook.State.THIRD_PERSON) cir.setReturnValue(new Vec3(player.xxa, 0.0, player.zza));
    }
}
