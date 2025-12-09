package cameratweaks.mixin;

import cameratweaks.Freelook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @WrapOperation(method = "pick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;pick(DFZ)Lnet/minecraft/world/phys/HitResult;"))
    private static HitResult raycast(Entity instance, double d, float f, boolean bl, Operation<HitResult> original) {
        if(Freelook.enabled) {
            Vec3 vec3 = instance.getEyePosition(f);
            return instance.level().clip(new ClipContext(vec3, vec3.add(instance.calculateViewVector(Freelook.pitch, Freelook.yaw).scale(d)),
                    ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, instance));
        }
        return original.call(instance, d, f, bl);
    }
}
