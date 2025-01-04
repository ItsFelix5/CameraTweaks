package cameratweaks.mixin;

import cameratweaks.Freecam;
import cameratweaks.Keybinds;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static cameratweaks.Util.client;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void onChangeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
        if (!Keybinds.freecam.enabled() || !this.equals(client.player) || Keybinds.playerMovement.enabled()) return;
        Freecam.pos.yaw += (float) cursorDeltaX * 0.15F;
        Freecam.pos.pitch = MathHelper.clamp(Freecam.pos.pitch + (float) cursorDeltaY * 0.15F, -90.0F, 90.0F);
        ci.cancel();
    }
}
