package cameratweaks.mixin;

import cameratweaks.*;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.client.OptionInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    private boolean detached;

    @Shadow
    protected abstract void setRotation(float f, float g);

    @Shadow
    protected abstract void setPosition(Vec3 vec3);

    @Shadow
    private float yRot;

    @Shadow
    private float xRot;

    @Shadow
    protected abstract float getMaxZoom(float f);

    @Shadow
    protected abstract void move(float f, float g, float h);

    @Shadow
    private @Nullable Entity entity;

    @Inject(method = "alignWithEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isPassenger()Z"), cancellable = true)
    private void update(float partialTicks, CallbackInfo ci) {
        if (!Keybinds.freecam.enabled() || Freecam.pos == null) return;
        this.detached = true;
        ci.cancel();
        setRotation(Freecam.pos.getYaw(partialTicks), Freecam.pos.getPitch(partialTicks));
        setPosition(Freecam.pos.getPos(partialTicks));
    }

    @Redirect(method = "alignWithEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V", ordinal = 1))
    private void changeRotation(Camera instance, float yaw, float pitch) {
        if (Freelook.state.active()) this.setRotation(Freelook.yaw, Freelook.pitch);
        else this.setRotation(yaw, pitch);
    }

    @Inject(method = "alignWithEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(F)F"), cancellable = true)
    private void modifyThirdperson(float partialTicks, CallbackInfo ci) {
        if(ThirdPerson.current == null) return;
        ci.cancel();
        float f = 1.0F;
        if (entity instanceof LivingEntity livingEntity)
            f = livingEntity.getScale() * (float)livingEntity.getAttributeValue(Attributes.CAMERA_DISTANCE) / 4F;

        if (entity.isPassenger() && entity.getVehicle() instanceof LivingEntity livingEntity2) {
            float d = livingEntity2.getScale() *  (float)livingEntity2.getAttributeValue(Attributes.CAMERA_DISTANCE);
            if (d > f) f = d / 4F;
        }

        float distance = (ThirdPerson.current.xOffset + ThirdPerson.distanceOffset) * f;
        this.move(ThirdPerson.current.collision? -getMaxZoom(distance) : -distance, ThirdPerson.current.yOffset * f, ThirdPerson.current.zOffset * f);
        this.setRotation(this.yRot + ThirdPerson.current.yaw, this.xRot + ThirdPerson.current.pitch);
    }

    @WrapOperation(method = "tickFov", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;"))
    public Object updateFovMultiplier(OptionInstance<?> instance, Operation<?> original){
        if(Keybinds.freecam.enabled()) return 0D;
        return original.call(instance);
    }

    @ModifyReturnValue(method = "modifyFovBasedOnDeathOrFluid", at = @At(value = "RETURN"))
    private float applyZoom(float original, @Local(argsOnly = true, ordinal = 0) final float partialTicks) {
        return original / Zoom.zoomDivisor(partialTicks);
    }

    @WrapOperation(method = "calculateFov", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;"))
    private Object getFov(OptionInstance<Integer> instance, Operation<Integer> original) {
        if (ThirdPerson.current != null && ThirdPerson.current.changedFov) return ThirdPerson.current.fov;
        if (Keybinds.freecam.enabled()) return Freecam.pos.fov;
        return original.call(instance);
    }
}
