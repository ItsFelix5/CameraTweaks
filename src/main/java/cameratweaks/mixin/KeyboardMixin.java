package cameratweaks.mixin;

import cameratweaks.Keybinds;
import cameratweaks.ThirdPerson;
import cameratweaks.config.Config;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static cameratweaks.Util.client;

@Mixin(KeyboardHandler.class)
public class KeyboardMixin {
    @Inject(method = "keyPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;set(Lcom/mojang/blaze3d/platform/InputConstants$Key;Z)V"), cancellable = true)
    private void onKey(long window, int action, KeyEvent input, CallbackInfo ci) {
        if (action == GLFW.GLFW_REPEAT) {
            KeyMapping.forAllKeyMappings(InputConstants.getKey(input), keyMapping -> {
                if(keyMapping instanceof Keybinds.BetterKeybind) ci.cancel();
            });
        }
        if(action == GLFW.GLFW_PRESS && input.input() != -1) {
            for (ThirdPerson thirdPerson : Config.get().thirdPersons) {
                if (thirdPerson.keyCode == input.input()) {
                    if(ThirdPerson.current == thirdPerson) {
                        client.options.setCameraType(CameraType.FIRST_PERSON);
                        ThirdPerson.setCurrent(null);
                        break;
                    }
                    ThirdPerson.setCurrent(thirdPerson);
                    break;
                }
            }
        }
    }
}
