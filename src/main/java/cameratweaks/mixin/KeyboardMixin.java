package cameratweaks.mixin;

import cameratweaks.Keybinds;
import cameratweaks.ThirdPerson;
import cameratweaks.config.Config;
import net.minecraft.client.Keyboard;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static cameratweaks.Util.client;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;setKeyPressed(Lnet/minecraft/client/util/InputUtil$Key;Z)V"), cancellable = true)
    private void onKey(long window, int action, KeyInput input, CallbackInfo ci) {
        if (action == GLFW.GLFW_REPEAT && KeyBinding.KEY_TO_BINDINGS.get(InputUtil.fromKeyCode(input)) instanceof Keybinds.BetterKeybind) ci.cancel();
        if(action == GLFW.GLFW_PRESS && input.getKeycode() != -1) {
            for (ThirdPerson thirdPerson : Config.get().thirdPersons) {
                if (thirdPerson.keyCode == input.getKeycode()) {
                    if(ThirdPerson.current == thirdPerson) {
                        client.options.setPerspective(Perspective.FIRST_PERSON);
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
