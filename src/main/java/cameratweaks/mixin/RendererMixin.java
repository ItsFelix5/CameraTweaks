package cameratweaks.mixin;

import cameratweaks.*;
import cameratweaks.config.Config;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class RendererMixin {
    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void disableViewBobbing(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if(Keybinds.freecam.enabled() || Zoom.currZoom > 5) ci.cancel();
    }

    @WrapOperation(method = "updateFovMultiplier", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;"))
    public Object updateFovMultiplier(SimpleOption<?> instance, Operation<?> original){
        if(Keybinds.freecam.enabled()) return 0D;
        return original.call(instance);
    }

    @ModifyReturnValue(method = "getFov", at = @At("RETURN"))
    private float applyZoom(float original, @Local(argsOnly = true) float tickDelta) {
        return original / (Config.HANDLER.instance().zoomAnimation?MathHelper.lerp(tickDelta, Zoom.prevZoom, Zoom.currZoom) : Zoom.zoom);
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

    @Redirect(method = "findCrosshairTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getRotationVec(F)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d getRotationVec(Entity instance, float tickDelta) {
        if(Freelook.enabled) return instance.getRotationVector(Freelook.pitch, Freelook.yaw);
        return instance.getRotationVec(tickDelta);
    }

    @Redirect(method = "findCrosshairTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;raycast(DFZ)Lnet/minecraft/util/hit/HitResult;"))
    private HitResult raycast(Entity instance, double maxDistance, float tickDelta, boolean includeFluids) {
        if(Freelook.enabled) {
            Vec3d vec3d = instance.getCameraPosVec(tickDelta);
            return instance.getWorld().raycast(new RaycastContext(vec3d, vec3d.add(instance.getRotationVector(Freelook.pitch, Freelook.yaw).multiply(maxDistance)),
                    RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, instance));
        }
        return instance.raycast(maxDistance, tickDelta, false);
    }
}
