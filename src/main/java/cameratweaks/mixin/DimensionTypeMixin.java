package cameratweaks.mixin;

import cameratweaks.config.Config;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(DimensionType.class)
public class DimensionTypeMixin {
    @Inject(method = "cloudHeight", at = @At("TAIL"), cancellable = true)
    private void getCloudHeight(CallbackInfoReturnable<Optional<Integer>> cir) {
        cir.setReturnValue(cir.getReturnValue().map(height -> height + Config.get().cloudHeight - 192));
    }
}
