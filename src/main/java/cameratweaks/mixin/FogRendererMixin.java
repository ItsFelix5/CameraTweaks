package cameratweaks.mixin;

import cameratweaks.config.Config;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.world.ClientWorld;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @Inject(method = "applyFog(Lnet/minecraft/client/render/Camera;IZLnet/minecraft/client/render/RenderTickCounter;FLnet/minecraft/client/world/ClientWorld;)Lorg/joml/Vector4f;", at =
    @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;getDevice()Lcom/mojang/blaze3d/systems/GpuDevice;"))
    private void getFogBuffer(Camera camera, int viewDistance, boolean thick, RenderTickCounter tickCounter, float skyDarkness, ClientWorld world, CallbackInfoReturnable<Vector4f> cir, @Local FogData data) {
        if (!Config.get().disableFog) return;
        data.environmentalStart = data.environmentalEnd = data.renderDistanceStart = data.renderDistanceEnd
                = viewDistance * 64;
        data.skyEnd = viewDistance * 16;
        data.cloudEnd = MinecraftClient.getInstance().options.getCloudRenderDistance().getValue() * 16;
    }

    @ModifyConstant(method = "getFogColor", constant = @Constant(floatValue = 0.0F, ordinal = 3))
    private float getFogColor(float constant) {
        return Config.get().fullbright && Config.get().nightVisionFullbright? 1F : 0F;
    }
}
