package cameratweaks.mixin;

import cameratweaks.Freelook;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathConstants;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At("HEAD"))
    private void rotateHand(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light, CallbackInfo ci) {
        if (Freelook.enabled) {
            matrices.multiply(new Quaternionf().rotationAxis((Freelook.yaw - player.getYaw(tickDelta)) * MathConstants.RADIANS_PER_DEGREE,
                    new Vector3f(0f, 1f, 0f).rotateX(Freelook.pitch * MathConstants.RADIANS_PER_DEGREE)));
            matrices.multiply(new Quaternionf().rotationX((Freelook.pitch - player.getPitch(tickDelta)) * MathConstants.RADIANS_PER_DEGREE));
        }
    }
}
