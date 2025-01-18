package cameratweaks.mixin;

import cameratweaks.Config;
import cameratweaks.Keybinds;
import cameratweaks.ThirdPerson;
import net.minecraft.client.Keyboard;
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
    private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (action == GLFW.GLFW_REPEAT && KeyBinding.KEY_TO_BINDINGS.get(InputUtil.fromKeyCode(key, scancode)) instanceof Keybinds.BetterKeybind) ci.cancel();
        if(action == GLFW.GLFW_PRESS) {
            for (ThirdPerson thirdPerson : Config.HANDLER.instance().thirdPersons) {
                if (thirdPerson.keyCode == key) {
                    if(ThirdPerson.current == thirdPerson) {
                        client.options.setPerspective(Perspective.FIRST_PERSON);
                        ThirdPerson.current = null;
                        break;
                    }
                    client.options.setPerspective(thirdPerson.invert?Perspective.THIRD_PERSON_FRONT:Perspective.THIRD_PERSON_BACK);
                    ThirdPerson.distanceOffset = 0.0F;
                    ThirdPerson.current = thirdPerson;
                    break;
                }
            }
        }
    }
}
