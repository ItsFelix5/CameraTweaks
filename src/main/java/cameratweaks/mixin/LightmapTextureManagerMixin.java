package cameratweaks.mixin;

import cameratweaks.config.Config;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.buffers.Std140Builder;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightTexture.class)
public class LightmapTextureManagerMixin {
    @Unique
    private static boolean disableLightmap = false;

    @Redirect(method = "updateLightTexture", at = @At(value = "INVOKE", target = "Ljava/lang/Double;floatValue()F", ordinal = 1))
    private float updateGamma(Double instance) {
        if (Config.get().fullbright && !Config.get().nightVisionFullbright) return 1250;
        return instance.floatValue();
    }

    @Inject(method = "updateLightTexture", at = @At("HEAD"), cancellable = true)
    private void update(CallbackInfo ci) {
        if (Config.get().fullbright && !Config.get().nightVisionFullbright) {
            if(disableLightmap) ci.cancel();
            else disableLightmap = true;
        } else disableLightmap = false;
    }

    @WrapOperation(method = "updateLightTexture", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/buffers/Std140Builder;putFloat(F)Lcom/mojang/blaze3d/buffers/Std140Builder;", ordinal = 3))
    private Std140Builder putFloat(Std140Builder instance, float value, Operation<Std140Builder> original) {
        return original.call(instance, Config.get().fullbright && Config.get().nightVisionFullbright?1F:value);
    }
}
