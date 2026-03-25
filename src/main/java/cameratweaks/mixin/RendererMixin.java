package cameratweaks.mixin;

import cameratweaks.Keybinds;
import cameratweaks.Zoom;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.level.CameraEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import org.joml.Matrix4fc;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class RendererMixin {
    @WrapOperation(method = "bobView", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/state/level/CameraEntityRenderState;bob:F", opcode = Opcodes.GETFIELD))
    private float bobView(CameraEntityRenderState instance, Operation<Float> original) {
        if(Keybinds.freecam.enabled()) return 0;
        return original.call(instance) / Mth.lerp(0.2F, 1F, Zoom.zoomDivisor(0F));
    }

    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    private void renderHand(CameraRenderState cameraState, float deltaPartialTick, Matrix4fc modelViewMatrix, CallbackInfo ci) {
        if(Keybinds.freecam.enabled()) ci.cancel();
    }
}
