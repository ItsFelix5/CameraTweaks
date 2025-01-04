package cameratweaks.mixin;

import cameratweaks.Freecam;
import cameratweaks.Keybinds;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
}
