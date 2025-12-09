package cameratweaks.mixin;

import cameratweaks.Freecam;
import cameratweaks.Freelook;
import cameratweaks.Keybinds;
import cameratweaks.ThirdPerson;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Camera;
import net.minecraft.client.OptionInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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

    @Inject(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isPassenger()Z"), cancellable = true)
    private void update(Level level, Entity entity, boolean bl, boolean bl2, float tickProgress, CallbackInfo ci) {
        if (!Keybinds.freecam.enabled() || Freecam.pos == null) return;
        ci.cancel();
        this.detached = true;
        setRotation(Freecam.pos.getYaw(tickProgress), Freecam.pos.getPitch(tickProgress));
        setPosition(Freecam.pos.getPos(tickProgress));
    }

    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V", ordinal = 1))
    private void changeRotation(Camera instance, float yaw, float pitch) {
        if (Freelook.enabled) this.setRotation(Freelook.yaw, Freelook.pitch);
        else this.setRotation(yaw, pitch);
    }

    @Inject(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(F)F"), cancellable = true)
    private void modifyThirdperson(Level level, Entity entity, boolean bl, boolean bl2, float tickProgress, CallbackInfo ci) {
        ci.cancel();
        float f = 4.0F;
        if (entity instanceof LivingEntity livingEntity)
            f = livingEntity.getScale() * (float)livingEntity.getAttributeValue(Attributes.CAMERA_DISTANCE);

        if (entity.isPassenger() && entity.getVehicle() instanceof LivingEntity livingEntity2) {
            float d = livingEntity2.getScale() *  (float)livingEntity2.getAttributeValue(Attributes.CAMERA_DISTANCE);
            if (d > f) f = d;
        }

        float distance = (ThirdPerson.current.xOffset + ThirdPerson.distanceOffset) * f / 4F;
        this.move(0, ThirdPerson.current.yOffset * f, ThirdPerson.current.zOffset * f);
        this.move(ThirdPerson.current.collision? -getMaxZoom(distance) : -distance, 0, 0);
        this.setRotation(this.yRot + ThirdPerson.current.yaw, this.xRot + ThirdPerson.current.pitch);
    }

    @WrapOperation(method = "getNearPlane", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;"))
    private Object getFov(OptionInstance<?> instance, Operation<Object> original) {
        if (ThirdPerson.current != null && ThirdPerson.current.changedFov) return ThirdPerson.current.fov;
        if (Keybinds.freecam.enabled()) return Freecam.pos.fov;
        return original.call(instance);
    }
}
