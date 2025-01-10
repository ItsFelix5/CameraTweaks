package cameratweaks;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import static cameratweaks.Util.client;

public class Keybinds {
    public static final BetterKeybind fullBright = (BetterKeybind) KeyBindingHelper.registerKeyBinding(new BetterKeybind(
            "fullbright",
            GLFW.GLFW_KEY_Y,
            () -> {
                Main.fullBright.setValue(true);
                client.gameRenderer.getLightmapTextureManager().dirty = true; // Required for Bad Optimizations
                client.options.write();
            },
            () -> {
                Main.fullBright.setValue(false);
                client.gameRenderer.getLightmapTextureManager().dirty = true;
                client.options.write();
            },
            true
    ));

    public static final BetterKeybind freecam = (BetterKeybind) KeyBindingHelper.registerKeyBinding(new BetterKeybind(
            "freecam",
            GLFW.GLFW_KEY_G,
            Freecam::enable,
            Freecam::disable,
            true
    ));

    public static final BetterKeybind playerMovement = (BetterKeybind) KeyBindingHelper.registerKeyBinding(new BetterKeybind(
            "freecam.movement",
            GLFW.GLFW_KEY_H,
            Freecam::playerMovement,
            Freecam::cameraMovement,
            true
    ));

    public static final BetterKeybind zoom = (BetterKeybind) KeyBindingHelper.registerKeyBinding(new BetterKeybind("zoom", GLFW.GLFW_KEY_C, Zoom::start, Zoom::stop));

    public static final BetterKeybind thirdPersonModifier = (BetterKeybind) KeyBindingHelper.registerKeyBinding(new BetterKeybind("thirdPersonModifier", GLFW.GLFW_KEY_J, ()->{}, ()->{}));

    @SuppressWarnings("EmptyMethod")
    public static void init() {}

    public static class BetterKeybind extends KeyBinding {
        private final Util.Callback press;
        private final Util.Callback release;
        private final boolean toggle;
        private boolean enabled = false;

        private BetterKeybind(String translationKey, int keyCode, Util.Callback press, Util.Callback release) {
            this(translationKey, keyCode, press, release, false);
        }

        private BetterKeybind(String translationKey, int keyCode, Util.Callback press, Util.Callback release, boolean toggle) {
            super("key.cameratweaks." + translationKey, keyCode, "category.cameratweaks.cameratweaks");
            this.press = press;
            this.release = release;
            this.toggle = toggle;
        }

        @Override
        public void setPressed(boolean pressed) {
            super.setPressed(pressed);
            if (toggle) {
                if (pressed) {
                    setEnabled(!enabled);
                    client.player.sendMessage(Text.translatable(getTranslationKey().substring(4) + (enabled ? ".on" : ".off")), true);
                }
            } else setEnabled(pressed);
        }

        public boolean enabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            if (enabled == this.enabled) return;
            this.enabled = enabled;
            if (enabled) press.run();
            else release.run();
        }

        public void setEnabled(boolean enabled, boolean b) {
            this.enabled = enabled;
        }
    }
}
