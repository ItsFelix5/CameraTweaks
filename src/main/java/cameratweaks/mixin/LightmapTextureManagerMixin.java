package cameratweaks.mixin;

import cameratweaks.config.Config;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.minecraft.client.renderer.state.LightmapRenderState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightmapRenderStateExtractor.class)
public class LightmapTextureManagerMixin {
    @Redirect(method = "extract", at = @At(value = "INVOKE", target = "Ljava/lang/Double;floatValue()F", ordinal = 0))
    private float updateGamma(Double instance) {
        if (Config.get().fullbright && !Config.get().nightVisionFullbright) return 1250;
        return instance.floatValue();
    }

    @WrapOperation(method = "extract", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/state/LightmapRenderState;nightVisionEffectIntensity:F", opcode = Opcodes.PUTFIELD))
    private void putFloat(LightmapRenderState instance, float value, Operation<Void> original) {
        original.call(instance, Config.get().fullbright && Config.get().nightVisionFullbright?1F:value);
    }
}
