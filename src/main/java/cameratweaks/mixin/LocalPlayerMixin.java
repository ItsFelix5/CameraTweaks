package cameratweaks.mixin;

import cameratweaks.Freelook;
import cameratweaks.Keybinds;
import cameratweaks.config.Config;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @WrapOperation(method = "pick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;pick(DFZ)Lnet/minecraft/world/phys/HitResult;"))
    private static HitResult raycast(Entity instance, double d, float f, boolean bl, Operation<HitResult> original) {
        if(Freelook.state == Freelook.State.THIRD_PERSON) {
            Vec3 vec3 = instance.getEyePosition(f);
            return instance.level().clip(new ClipContext(vec3, vec3.add(instance.calculateViewVector(Freelook.pitch, Freelook.yaw).scale(d)),
                    ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, instance));
        }
        return original.call(instance, d, f, bl);
    }

    @Inject(method = "hurtTo", at = @At("HEAD"))
    private void onHurtTo(float f, CallbackInfo ci) {
        if (Config.get().disableFreecamOnDamage) {
            Keybinds.freecam.setEnabled(false);
        }
    }
}
