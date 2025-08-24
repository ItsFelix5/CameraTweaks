package cameratweaks.mixin;

import cameratweaks.Keybinds;
import cameratweaks.ThirdPerson;
import net.minecraft.client.input.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Input.class)
public class InputMixin {
    @Shadow
    public float movementSideways;

    @Inject(method = "hasForwardMovement", at = @At("HEAD"), cancellable = true)
    private void hasForwardMovement(CallbackInfoReturnable<Boolean> cir) {
        if(Keybinds.freelook.enabled() && ThirdPerson.current != null && !ThirdPerson.current.rotatePlayer && movementSideways > 1.0E-5F) cir.setReturnValue(true);
    }
}
