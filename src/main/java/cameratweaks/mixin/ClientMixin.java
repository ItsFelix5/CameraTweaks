package cameratweaks.mixin;

import cameratweaks.Freecam;
import cameratweaks.Freelook;
import cameratweaks.Keybinds;
import cameratweaks.ThirdPerson;
import cameratweaks.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static cameratweaks.Util.client;

@Mixin(MinecraftClient.class)
public class ClientMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(boolean tick, CallbackInfo ci) {
        Freecam.update(client.getRenderTickCounter().getLastFrameDuration());
    }

    @Redirect(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z", ordinal = 0))
    private boolean preventPerspectiveChange(KeyBinding instance) {
        return !Keybinds.freecam.enabled() && instance.wasPressed();
    }

    @Redirect(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;setPerspective(Lnet/minecraft/client/option/Perspective;)V"))
    private void preventPerspectiveChange(GameOptions instance, Perspective perspective) {
        ThirdPerson.distanceOffset = 0.0F;
        if(perspective == Perspective.THIRD_PERSON_BACK) ThirdPerson.setCurrent(Config.HANDLER.instance().thirdPersons.get(0));
        else if(perspective == Perspective.THIRD_PERSON_FRONT) ThirdPerson.setCurrent(Config.HANDLER.instance().thirdPersons.get(1));
        instance.setPerspective(perspective);
    }

    @Redirect(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z", ordinal = 2))
    private boolean activateCamera(KeyBinding instance) {
        if (!instance.wasPressed()) return false;
        if (Keybinds.freecam.isPressed()) {
            for (int i = 0; i < client.options.hotbarKeys.length; i++)
                if (client.options.hotbarKeys[i].equals(instance)) {
                    Freecam.loadCamera(i);
                    Keybinds.freecam.setUsed();
                    return false;
                }
        }
        if (Keybinds.saveFreecam.isPressed()) {
            for (int i = 0; i < client.options.hotbarKeys.length; i++)
                if (client.options.hotbarKeys[i].equals(instance)) {
                    Freecam.saveCamera(i);
                    Keybinds.saveFreecam.setUsed();
                    return false;
                }
        }
        return true;
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;Z)V", at = @At("HEAD"))
    private void onDisconnect(CallbackInfo ci) {
        Freecam.reset();
    }

    @Inject(method = {"doItemUse", "doItemPick"}, at = @At("HEAD"))
    private void doItemUse(CallbackInfo ci) {
        Freelook.pause();
    }

    @Inject(method = "doAttack", at = @At("HEAD"))
    private void doAttack(CallbackInfoReturnable<Boolean> cir) {
        Freelook.pause();
    }
}
