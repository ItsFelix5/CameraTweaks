package cameratweaks.mixin;

import cameratweaks.Freelook;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathConstants;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static cameratweaks.Util.client;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @Shadow private float equipProgressMainHand;
    @Shadow private float equipProgressOffHand;

    @Shadow private ItemStack mainHand;
    @Shadow private ItemStack offHand;

    @Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At("HEAD"))
    private void rotateHand(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light, CallbackInfo ci) {
        if (Freelook.enabled) {
            matrices.multiply(new Quaternionf().rotationAxis((Freelook.yaw - player.getYaw(tickDelta)) * MathConstants.RADIANS_PER_DEGREE,
                    new Vector3f(0f, 1f, 0f).rotateX(Freelook.pitch * MathConstants.RADIANS_PER_DEGREE)));
            matrices.multiply(new Quaternionf().rotationX((Freelook.pitch - player.getPitch(tickDelta)) * MathConstants.RADIANS_PER_DEGREE));
        }
    }

    @Inject(method = "updateHeldItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isRiding()Z"))
    private void updateHeldItems(CallbackInfo ci) {
        ClientPlayerEntity clientPlayerEntity = client.player;
        if(!clientPlayerEntity.isRiding()) return;

        ItemStack mainStack = clientPlayerEntity.getMainHandStack();
        if (mainStack != ItemStack.EMPTY) {
            float g = this.mainHand != mainStack ? 0.0F : 1.0F;
            this.equipProgressMainHand += MathHelper.clamp(g - this.equipProgressMainHand, -0.4F, 0.4F) + 0.4F;
        }

        ItemStack offStack = clientPlayerEntity.getOffHandStack();
        if (offStack != ItemStack.EMPTY) {
            float h = this.offHand != offStack ? 0.0F : 1.0F;
            this.equipProgressOffHand += MathHelper.clamp(h - this.equipProgressOffHand, -0.4F, 0.4F) + 0.4F;
        }
    }
}
