package cameratweaks;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.Map;
import java.util.Set;

import static cameratweaks.Util.client;

public class Keybinds {
    public static final BetterKeybind fullBright = (BetterKeybind) KeyBindingHelper.registerKeyBinding(new BetterKeybind(
            "fullbright",
            GLFW.GLFW_KEY_Y,
            () -> {
                Config.HANDLER.instance().fullbright = true;
                client.gameRenderer.getLightmapTextureManager().dirty = true;
                Config.HANDLER.save();
            },
            () -> {
                Config.HANDLER.instance().fullbright = false;
                client.gameRenderer.getLightmapTextureManager().dirty = true;
                Config.HANDLER.save();
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

    public static final BetterKeybind saveFreecam = (BetterKeybind) KeyBindingHelper.registerKeyBinding(new BetterKeybind(
            "freecam.save",
            GLFW.GLFW_KEY_H
    ));

    public static final BetterKeybind playerMovement = (BetterKeybind) KeyBindingHelper.registerKeyBinding(new BetterKeybind(
            "freecam.movement",
            GLFW.GLFW_KEY_H,
            Freecam::playerMovement,
            Freecam::cameraMovement,
            true
    ));

    public static final BetterKeybind zoom = (BetterKeybind) KeyBindingHelper.registerKeyBinding(new BetterKeybind("zoom", GLFW.GLFW_KEY_C, Zoom::start, Zoom::stop));

    public static final BetterKeybind thirdPersonModifier = (BetterKeybind) KeyBindingHelper.registerKeyBinding(new BetterKeybind("thirdPersonModifier", GLFW.GLFW_KEY_UNKNOWN));

    public static final BetterKeybind freelook = (BetterKeybind) KeyBindingHelper.registerKeyBinding(new BetterKeybind("freelook", GLFW.GLFW_KEY_UNKNOWN, Freelook::start, Freelook::stop, true));

    static {
        KeyBindingHelper.registerKeyBinding(new BetterKeybind("freelook.hold", GLFW.GLFW_KEY_UNKNOWN, () -> freelook.setEnabled(true), () -> freelook.setEnabled(false)));
    }

    public static void init() {
        fullBright.setEnabledNoCB(Config.HANDLER.instance().fullbright);
    }

    public static class BetterKeybind extends KeyBinding {
        private final Runnable press;
        private final Runnable release;
        private final boolean toggle;
        private boolean enabled = false;
        private boolean used = false;

        private BetterKeybind(String translationKey, int keyCode) {
            this(translationKey, keyCode, ()->{}, ()->{}, false);
        }
        private BetterKeybind(String translationKey, int keyCode, Runnable press, Runnable release) {
            this(translationKey, keyCode, press, release, false);
        }

        private BetterKeybind(String translationKey, int keyCode, Runnable press, Runnable release, boolean toggle) {
            super("key.cameratweaks." + translationKey, keyCode, "category.cameratweaks.cameratweaks");
            this.press = press;
            this.release = release;
            this.toggle = toggle;
        }

        @Override
        public void setPressed(boolean pressed) {
            if (toggle) {
                if (!pressed && isPressed()) {
                    if(used) used = false;
                    else {
                        setEnabled(!enabled);
                        client.player.sendMessage(Text.translatable(getTranslationKey().substring(4) + (enabled ? ".on" : ".off")), true);
                    }
                }
            } else setEnabled(pressed);
            super.setPressed(pressed);
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

        public void setEnabledNoCB(boolean enabled) {
            this.enabled = enabled;
        }

        @SuppressWarnings("unchecked")
        public void setUsed() {
            used = true;
            if(FabricLoader.getInstance().isModLoaded("stfu")) {
                try {
                    ((Map<InputUtil.Key, Set<KeyBinding>>) Class.forName("stfu.KeybindHolder").getDeclaredField("KEY_TO_BINDINGS").get(null)).get(boundKey)
                            .forEach(keyBinding -> {
                                if(keyBinding instanceof BetterKeybind betterKeybind) betterKeybind.used = true;
                            });
                } catch (Exception ignored) {}
            }
        }
    }
}
