package cameratweaks.mixin;

import cameratweaks.Keybinds;
import cameratweaks.Main;
import cameratweaks.ThirdPerson;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
    @Inject(method = "load", at = @At("TAIL"))
    private void load(CallbackInfo ci) {
        Keybinds.fullBright.setEnabled(Main.fullBright.getValue(), false);
    }

    @Inject(method = "accept", at = @At("TAIL"))
    private void accept(GameOptions.Visitor visitor, CallbackInfo ci) {
        visitor.accept("disableFog", Main.disableFog);
        visitor.accept("fullBright", Main.fullBright);
    }

    @Inject(method = "setPerspective", at = @At("TAIL"))
    private void setPerspective(Perspective perspective, CallbackInfo ci) {
        ThirdPerson.distance = 4.0F;
    }
}
