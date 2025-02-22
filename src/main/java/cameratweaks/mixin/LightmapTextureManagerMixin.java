package cameratweaks.mixin;

import cameratweaks.Config;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {
    @Unique
    private static boolean disableLightmap = false;

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Ljava/lang/Double;floatValue()F", ordinal = 1))
    private float updateGamma(Double instance) {
        if (Config.HANDLER.instance().fullbright) return 1250;
        return instance.floatValue();
    }

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void update(CallbackInfo ci) {
        if (Config.HANDLER.instance().fullbright) {
            if(disableLightmap) ci.cancel();
            else disableLightmap = true;
        } else disableLightmap = false;
    }
}
