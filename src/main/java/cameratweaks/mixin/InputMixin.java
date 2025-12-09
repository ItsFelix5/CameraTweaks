package cameratweaks.mixin;

import cameratweaks.Keybinds;
import cameratweaks.ThirdPerson;
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientInput.class)
public class InputMixin {
    @Shadow protected Vec2 moveVector;

    @Inject(method = "hasForwardImpulse", at = @At("HEAD"), cancellable = true)
    private void hasForwardMovement(CallbackInfoReturnable<Boolean> cir) {
        if(Keybinds.freelook.enabled() && ThirdPerson.current != null && !ThirdPerson.current.rotatePlayer) cir.setReturnValue(moveVector.lengthSquared() > 1.0E-10F);
    }
}
