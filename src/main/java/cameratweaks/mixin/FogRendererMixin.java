package cameratweaks.mixin;

import cameratweaks.config.Config;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.MappableRingBuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.world.ClientWorld;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.ByteBuffer;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @Shadow
    protected abstract Vector4f getFogColor(Camera camera, float tickProgress, ClientWorld world, int viewDistance, float skyDarkness, boolean thick);

    @Shadow
    @Final
    private MappableRingBuffer fogBuffer;

    @Shadow
    protected abstract void applyFog(ByteBuffer buffer, int bufPos, Vector4f fogColor, float environmentalStart, float environmentalEnd, float renderDistanceStart, float renderDistanceEnd, float skyEnd, float cloudEnd);

    @Inject(method = "applyFog(Lnet/minecraft/client/render/Camera;IZLnet/minecraft/client/render/RenderTickCounter;FLnet/minecraft/client/world/ClientWorld;)Lorg/joml/Vector4f;", at =
    @At("HEAD"), cancellable = true)
    private void getFogBuffer(Camera camera, int viewDistance, boolean thick, RenderTickCounter tickCounter, float skyDarkness, ClientWorld world, CallbackInfoReturnable<Vector4f> cir) {
        if (!Config.HANDLER.instance().disableFog) return;
        float f = tickCounter.getTickProgress(false);
        Vector4f vector4f = this.getFogColor(camera, f, world, viewDistance, skyDarkness, thick);

        try (GpuBuffer.MappedView mappedView = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.fogBuffer.getBlocking(), false, true)) {
            this.applyFog(
                    mappedView.data(),
                    0,
                    vector4f,
                    viewDistance * 64,
                    viewDistance * 64,
                    viewDistance * 64,
                    viewDistance * 64,
                    viewDistance * 16,
                    MinecraftClient.getInstance().options.getCloudRenderDistance().getValue() * 16
            );
        }

        cir.setReturnValue(vector4f);
    }

    @ModifyConstant(method = "getFogColor", constant = @Constant(floatValue = 0.0F, ordinal = 3))
    private float getFogColor(float constant) {
        return Config.HANDLER.instance().fullbright && Config.HANDLER.instance().nightVisionFullbright? 1F : 0F;
    }
}
