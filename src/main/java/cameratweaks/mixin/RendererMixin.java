package cameratweaks.mixin;

import cameratweaks.Freecam;
import cameratweaks.Keybinds;
import cameratweaks.ThirdPerson;
import cameratweaks.Zoom;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.entity.ClientAvatarState;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class RendererMixin {
    @WrapOperation(method = "bobView", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/ClientAvatarState;getInterpolatedBob(F)F"))
    private float disableViewBobbing(ClientAvatarState instance, float tickProgress, Operation<Float> original) {
        if(Keybinds.freecam.enabled()) return 0;
        return original.call(instance, tickProgress) / Mth.lerp(0.2F, 1F, Zoom.zoomDivisor(0F));
    }

    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    private void renderHand(float tickProgress, boolean sleeping, Matrix4f positionMatrix, CallbackInfo ci) {
        if(Keybinds.freecam.enabled()) ci.cancel();
    }

    @WrapOperation(method = "tickFov", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;"))
    public Object updateFovMultiplier(OptionInstance<?> instance, Operation<?> original){
        if(Keybinds.freecam.enabled()) return 0D;
        return original.call(instance);
    }

    @ModifyReturnValue(method = "getFov", at = @At("RETURN"))
    private float applyZoom(float original, @Local(argsOnly = true) float tickDelta) {
        return original / Zoom.zoomDivisor(tickDelta);
    }

    @WrapOperation(method = "getFov", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;", ordinal = 0))
    private Object getFov(OptionInstance<Integer> instance, Operation<Integer> original) {
        if (ThirdPerson.current != null && ThirdPerson.current.changedFov) return ThirdPerson.current.fov;
        if (Keybinds.freecam.enabled()) return Freecam.pos.fov;
        return original.call(instance);
    }
}
