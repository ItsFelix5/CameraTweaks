package cameratweaks.mixin;

import cameratweaks.ThirdPerson;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
    @Inject(method = "setPerspective", at = @At("TAIL"))
    private void setPerspective(Perspective perspective, CallbackInfo ci) {
        ThirdPerson.distance = 4.0F;
    }
}
