package cameratweaks.mixin;

import cameratweaks.Freecam;
import cameratweaks.Freelook;
import cameratweaks.Keybinds;
import cameratweaks.ThirdPerson;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    private boolean ready;
    @Shadow
    private Entity focusedEntity;
    @Shadow
    private BlockView area;
    @Shadow
    private boolean thirdPerson;
    @Shadow
    private float lastTickDelta;

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Shadow
    protected abstract void setPos(Vec3d pos);

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void update(BlockView area, Entity focusedEntity, boolean thirdPerson1, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (!Keybinds.freecam.enabled() || Freecam.pos == null) return;
        ci.cancel();
        this.ready = true;
        this.area = area;
        this.focusedEntity = focusedEntity;
        this.thirdPerson = true;
        this.lastTickDelta = tickDelta;
        setRotation(MathHelper.lerpAngleDegrees(tickDelta, Freecam.prev.yaw, Freecam.pos.yaw), MathHelper.lerp(tickDelta, Freecam.prev.pitch, Freecam.pos.pitch));
        setPos(Freecam.prev.pos.lerp(Freecam.pos.pos, tickDelta));
    }

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 1))
    private void changeRotation(Camera instance, float yaw, float pitch) {
        if (Freelook.enabled) {
            System.out.println(yaw + " " + pitch);
            this.setRotation(Freelook.yaw, Freelook.pitch);
        }
        else this.setRotation(yaw, pitch);
    }

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 2))
    private void heeh(Camera instance, float yaw, float pitch) {
        if (Freelook.enabled) {
            System.out.println(yaw + " a " + pitch);
        }
        this.setRotation(yaw, pitch);
    }

    @ModifyConstant(method = "update", constant = @Constant(floatValue = 4.0F))
    private float changeDistance(float distance) {
        return ThirdPerson.distance;
    }
}
