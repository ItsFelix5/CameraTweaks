package cameratweaks.mixin;

import cameratweaks.Freecam;
import cameratweaks.Keybinds;
import cameratweaks.ThirdPerson;
import cameratweaks.Zoom;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class RendererMixin {
    @Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;", ordinal = 0))
    private Object disableViewBobbing(SimpleOption<?> instance) {
        return Keybinds.freecam.enabled() || Zoom.zoom > 5 ? false : instance.getValue();
    }

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void applyZoom(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(cir.getReturnValue() / MathHelper.lerp(tickDelta, Zoom.prevZoom, Zoom.currZoom));
    }

    @WrapOperation(method = "getFov", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;", ordinal = 0))
    private Object getFov(SimpleOption<Integer> instance, Operation<Integer> original) {
        if (ThirdPerson.current != null && ThirdPerson.current.changedFov) return ThirdPerson.current.fov;
        if (Keybinds.freecam.enabled()) return Freecam.pos.fov;
        return original.call(instance);
    }

    @WrapOperation(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;", ordinal = 2))
    private Object renderWorld(SimpleOption<Integer> instance, Operation<Integer> original) {
        if (ThirdPerson.current != null && ThirdPerson.current.changedFov) return ThirdPerson.current.fov;
        if (Keybinds.freecam.enabled()) return Freecam.pos.fov;
        return original.call(instance);
    }
}
