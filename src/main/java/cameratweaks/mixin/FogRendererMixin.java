package cameratweaks.mixin;

import cameratweaks.config.Config;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @Inject(method = "setupFog", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;getDevice()Lcom/mojang/blaze3d/systems/GpuDevice;"))
    private void getFogBuffer(Camera camera, int viewDistance, DeltaTracker deltaTracker, float f, ClientLevel clientLevel, CallbackInfoReturnable<Vector4f> cir, @Local FogData data) {
        if (!Config.get().disableFog) return;
        data.environmentalStart = data.environmentalEnd = data.renderDistanceStart = data.renderDistanceEnd
                = viewDistance * 64;
        data.skyEnd = viewDistance * 16;
        data.cloudEnd = Minecraft.getInstance().options.cloudRange().get() * 16;
    }

    @ModifyConstant(method = "computeFogColor", constant = @Constant(floatValue = 0.0F, ordinal = 3))
    private float getFogColor(float constant) {
        return Config.get().fullbright && Config.get().nightVisionFullbright? 1F : 0F;
    }
}
