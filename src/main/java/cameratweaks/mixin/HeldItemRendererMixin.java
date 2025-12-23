package cameratweaks.mixin;

import cameratweaks.Freelook;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Constants;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static cameratweaks.Util.client;

@Mixin(ItemInHandRenderer.class)
public class HeldItemRendererMixin {
    @Shadow private float mainHandHeight;
    @Shadow private float offHandHeight;

    @Shadow private ItemStack mainHandItem;
    @Shadow private ItemStack offHandItem;

    @Inject(method = "renderHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/player/LocalPlayer;I)V", at = @At("HEAD"))
    private void rotateHand(float tickProgress, PoseStack matrices, SubmitNodeCollector orderedRenderCommandQueue, LocalPlayer player, int light, CallbackInfo ci) {
        if (Freelook.state.active()) {
            matrices.mulPose(new Quaternionf().rotationAxis((Freelook.yaw - player.getViewYRot(tickProgress)) * Constants.DEG_TO_RAD,
                    new Vector3f(0f, 1f, 0f).rotateX(Freelook.pitch * Constants.DEG_TO_RAD)));
            matrices.mulPose(new Quaternionf().rotationX((Freelook.pitch - player.getViewXRot(tickProgress)) * Constants.DEG_TO_RAD));
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isHandsBusy()Z"))
    private void updateHeldItems(CallbackInfo ci) {
        LocalPlayer clientPlayerEntity = client.player;
        if(!clientPlayerEntity.isHandsBusy()) return;

        ItemStack mainStack = clientPlayerEntity.getMainHandItem();
        if (mainStack != ItemStack.EMPTY) {
            float g = this.mainHandItem != mainStack ? 0.0F : 1.0F;
            this.mainHandHeight += Mth.clamp(g - this.mainHandHeight, -0.4F, 0.4F) + 0.4F;
        }

        ItemStack offStack = clientPlayerEntity.getOffhandItem();
        if (offStack != ItemStack.EMPTY) {
            float h = this.offHandItem != offStack ? 0.0F : 1.0F;
            this.offHandHeight += Mth.clamp(h - this.offHandHeight, -0.4F, 0.4F) + 0.4F;
        }
    }
}
