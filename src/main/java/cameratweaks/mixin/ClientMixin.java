package cameratweaks.mixin;

import cameratweaks.Freecam;
import cameratweaks.Keybinds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static cameratweaks.Util.client;

@Mixin(MinecraftClient.class)
public class ClientMixin {
    @Redirect(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z", ordinal = 0))
    private boolean preventPerspectiveChange(KeyBinding instance) {
        return !Keybinds.freecam.enabled() && instance.wasPressed();
    }

    @Redirect(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z", ordinal = 2))
    private boolean activateCamera(KeyBinding instance) {
        if (!instance.wasPressed()) return false;
        if (Keybinds.freecam.isPressed()) {
            for (int i = 0; i < client.options.hotbarKeys.length; i++) if (client.options.hotbarKeys[i].equals(instance)) {
                Freecam.loadCamera(i);
                return false;
            }
        }
        if (Keybinds.playerMovement.isPressed()) {
            for (int i = 0; i < client.options.hotbarKeys.length; i++) if (client.options.hotbarKeys[i].equals(instance)) {
                Freecam.saveCamera(i);
                return false;
            }
        }
        return true;
    }
}
