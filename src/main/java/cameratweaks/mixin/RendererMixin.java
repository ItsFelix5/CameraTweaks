package cameratweaks.mixin;

import cameratweaks.Freelook;
import cameratweaks.Keybinds;
import cameratweaks.Zoom;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathConstants;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class RendererMixin {
    @Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;", ordinal = 0))
    private Object disableViewBobbing(SimpleOption<?> instance) {
        return Keybinds.freecam.enabled() ? false : instance.getValue();
    }

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void applyZoom(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(cir.getReturnValue() / MathHelper.lerp(tickDelta, Zoom.prevZoom, Zoom.currZoom));
    }

    @Redirect(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"))
    private void rotateHand(HeldItemRenderer instance, float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light) {
        if (Freelook.enabled) {
            matrices.multiply(new Quaternionf().rotationAxis((Freelook.yaw - player.getYaw(tickDelta)) * MathConstants.RADIANS_PER_DEGREE,
                    new Vector3f(0f, 1f, 0f).rotateX(Freelook.pitch * MathConstants.RADIANS_PER_DEGREE)));
            matrices.multiply(new Quaternionf().rotationX((Freelook.pitch - player.getPitch(tickDelta)) * MathConstants.RADIANS_PER_DEGREE));
        }
        instance.renderItem(tickDelta, matrices, vertexConsumers, player, light);
    }
}
