package cameratweaks.mixin;

import cameratweaks.config.Config;
import net.minecraft.client.render.DimensionEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionEffects.class)
public class DimensionEffectsMixin {
    @Inject(method = "getCloudsHeight", at = @At("TAIL"), cancellable = true)
    private void getCloudHeight(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(cir.getReturnValue() + Config.HANDLER.instance().cloudHeight - 192);
    }
}
