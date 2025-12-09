package cameratweaks.mixin;

import cameratweaks.Freecam;
import cameratweaks.Freelook;
import cameratweaks.Keybinds;
import cameratweaks.ThirdPerson;
import cameratweaks.config.Config;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static cameratweaks.Util.client;

@Mixin(Minecraft.class)
public class ClientMixin {
    @Inject(method = "runTick", at = @At("HEAD"))
    private void tick(boolean tick, CallbackInfo ci) {
        Freecam.update(client.getDeltaTracker().getRealtimeDeltaTicks());
    }

    @Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z", ordinal = 0))
    private boolean preventPerspectiveChange(KeyMapping instance) {
        return !Keybinds.freecam.enabled() && instance.consumeClick();
    }

    @Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;setCameraType(Lnet/minecraft/client/CameraType;)V"))
    private void preventPerspectiveChange(Options instance, CameraType cameraType) {
        if (ThirdPerson.current == Config.get().thirdPersons.get(0)) ThirdPerson.setCurrent(Config.get().thirdPersons.get(1));
        else if (ThirdPerson.current == Config.get().thirdPersons.get(1)) ThirdPerson.setCurrent(null);
        else ThirdPerson.setCurrent(Config.get().thirdPersons.get(0));
    }

    @Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z", ordinal = 2))
    private boolean activateCamera(KeyMapping instance) {
        if (!instance.consumeClick()) return false;
        if (Keybinds.freecam.isDown()) {
            for (int i = 0; i < client.options.keyHotbarSlots.length; i++)
                if (client.options.keyHotbarSlots[i].equals(instance)) {
                    Freecam.loadCamera(i);
                    Keybinds.freecam.setUsed();
                    return false;
                }
        }
        if (Keybinds.saveFreecam.isDown()) {
            for (int i = 0; i < client.options.keyHotbarSlots.length; i++)
                if (client.options.keyHotbarSlots[i].equals(instance)) {
                    Freecam.saveCamera(i);
                    Keybinds.saveFreecam.setUsed();
                    return false;
                }
        }
        return true;
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;Z)V", at = @At("HEAD"))
    private void onDisconnect(CallbackInfo ci) {
        Freecam.reset();
    }

    @Inject(method = {"startUseItem", "pickBlock"}, at = @At("HEAD"))
    private void doItemUse(CallbackInfo ci) {
        Freelook.pause();
    }

    @Inject(method = "startAttack", at = @At("HEAD"))
    private void doAttack(CallbackInfoReturnable<Boolean> cir) {
        Freelook.pause();
    }
}
