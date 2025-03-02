package cameratweaks;

import cameratweaks.config.Config;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static cameratweaks.Util.client;

public class Keybinds {
    public static final BetterKeybind freecam = new BetterKeybind("freecam", GLFW.GLFW_KEY_G)
            .toggle().onPress(Freecam::enable, Freecam::disable);

    public static final BetterKeybind saveFreecam = new BetterKeybind("freecam.save", GLFW.GLFW_KEY_H);

    public static final BetterKeybind playerMovement = new BetterKeybind("freecam.movement", GLFW.GLFW_KEY_H)
            .toggle().onPress(Freecam::playerMovement, Freecam::cameraMovement);

    public static final BetterKeybind zoom = new BetterKeybind("zoom", GLFW.GLFW_KEY_C).onPress(Zoom::start, Zoom::stop);

    public static final BetterKeybind thirdPersonModifier = new BetterKeybind("thirdPersonModifier", GLFW.GLFW_KEY_UNKNOWN);

    public static final BetterKeybind freelook = new BetterKeybind("freelook", GLFW.GLFW_KEY_UNKNOWN)
            .toggle().onPress(Freelook::start, Freelook::stop).condition(()->ThirdPerson.current == null || ThirdPerson.current.rotatePlayer);

    public static void init() {
        new BetterKeybind("freelook.hold", GLFW.GLFW_KEY_UNKNOWN)
                .onPress(() -> freelook.setEnabled(true), () -> freelook.setEnabled(false)).condition(()->ThirdPerson.current == null || ThirdPerson.current.rotatePlayer);
        new BetterKeybind("fullbright", GLFW.GLFW_KEY_Y).defaultEnabled(Config.HANDLER.instance().fullbright).toggle().onPress(
                () -> {
                    Config.HANDLER.instance().fullbright = true;
                    client.gameRenderer.getLightmapTextureManager().dirty = true;
                    Config.HANDLER.save();
                },
                () -> {
                    Config.HANDLER.instance().fullbright = false;
                    client.gameRenderer.getLightmapTextureManager().dirty = true;
                    Config.HANDLER.save();
                });
    }

    public static class BetterKeybind extends KeyBinding {
        private Runnable press = ()->{};
        private Runnable release = ()->{};
        private boolean toggle = false;
        private Supplier<Boolean> condition = ()->true;
        private boolean enabled = false;
        private boolean used = false;

        private BetterKeybind(String translationKey, int keyCode) {
            super("key.cameratweaks." + translationKey, keyCode, "category.cameratweaks.cameratweaks");
            KeyBindingHelper.registerKeyBinding(this);
        }
        
        public BetterKeybind onPress(Runnable press, Runnable release) {
            this.press = press;
            this.release = release;
            return this;
        }
        
        public BetterKeybind toggle() {
            this.toggle = true;
            return this;
        }
        
        public BetterKeybind defaultEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }
        
        public BetterKeybind condition(Supplier<Boolean> condition) {
            this.condition = condition;
            return this;
        }

        @Override
        public void setPressed(boolean pressed) {
            if(condition.get()) {
                if (toggle) {
                    if (!pressed && isPressed()) {
                        if (used) used = false;
                        else {
                            setEnabled(!enabled);
                            client.player.sendMessage(Text.translatable(getTranslationKey().substring(4) + (enabled ? ".on" : ".off")), true);
                        }
                    }
                } else setEnabled(pressed);
            }
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

        @SuppressWarnings("unchecked")
        public void setUsed() {
            used = true;
            if(FabricLoader.getInstance().isModLoaded("stfu")) {
                try {
                    ((Map<InputUtil.Key, Set<KeyBinding>>) Class.forName("stfu.KeybindHolder").getDeclaredField("KEY_TO_BINDINGS").get(null))
                            .get(KeyBindingHelper.getBoundKeyOf(this)).forEach(keyBinding -> {
                                if(keyBinding instanceof BetterKeybind betterKeybind) betterKeybind.used = true;
                            });
                } catch (Exception ignored) {}
            }
        }
    }
}
