package cameratweaks.mixin;

import cameratweaks.Freecam;
import cameratweaks.Freelook;
import cameratweaks.Keybinds;
import cameratweaks.ThirdPerson;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    private boolean thirdPerson;
    @Shadow
    private float yaw;
    @Shadow
    private float pitch;
    @Shadow protected abstract void setRotation(float yaw, float pitch);
    @Shadow protected abstract void setPos(Vec3d pos);
    @Shadow protected abstract void moveBy(float f, float g, float h);
    @Shadow protected abstract float clipToSpace(float f);

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;hasVehicle()Z"), cancellable = true)
    private void update(BlockView area, Entity focusedEntity, boolean thirdPerson1, boolean inverseView, float tickProgress, CallbackInfo ci) {
        if (!Keybinds.freecam.enabled() || Freecam.pos == null) return;
        ci.cancel();
        this.thirdPerson = true;
        setRotation(Freecam.pos.getYaw(tickProgress), Freecam.pos.getPitch(tickProgress));
        setPos(Freecam.pos.getPos(tickProgress));
    }

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 1))
    private void changeRotation(Camera instance, float yaw, float pitch) {
        if (Freelook.enabled) this.setRotation(Freelook.yaw, Freelook.pitch);
        else this.setRotation(yaw, pitch);
    }

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;clipToSpace(F)F"), cancellable = true)
    private void modifyThirdperson(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        ci.cancel();
        float f = 4.0F;
        if (focusedEntity instanceof LivingEntity livingEntity)
            f = livingEntity.getScale() * (float)livingEntity.getAttributeValue(EntityAttributes.CAMERA_DISTANCE);

        if (focusedEntity.hasVehicle() && focusedEntity.getVehicle() instanceof LivingEntity livingEntity2) {
            float d = livingEntity2.getScale() *  (float)livingEntity2.getAttributeValue(EntityAttributes.CAMERA_DISTANCE);
            if (d > f) f = d;
        }

        float distance = (ThirdPerson.current.xOffset + ThirdPerson.distanceOffset) * f / 4F;
        this.moveBy(0, ThirdPerson.current.yOffset * f, ThirdPerson.current.zOffset * f);
        this.moveBy(ThirdPerson.current.collision? -clipToSpace(distance) : -distance, 0, 0);
        this.setRotation(this.yaw + ThirdPerson.current.yaw, this.pitch + ThirdPerson.current.pitch);
    }

    @WrapOperation(method = "getProjection", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;"))
    private Object getFov(SimpleOption<Integer> instance, Operation<Integer> original) {
        if (ThirdPerson.current != null && ThirdPerson.current.changedFov) return ThirdPerson.current.fov;
        if (Keybinds.freecam.enabled()) return Freecam.pos.fov;
        return original.call(instance);
    }
}
