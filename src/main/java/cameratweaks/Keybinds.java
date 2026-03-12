package cameratweaks;

import cameratweaks.config.Config;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

import static cameratweaks.Util.client;

public class Keybinds {
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("cameratweaks", "cameratweaks"));

    public static final BetterKeybind freecam = new BetterKeybind("freecam", GLFW.GLFW_KEY_H)
            .toggle().onPress(Freecam::enable, Freecam::disable);

    public static final BetterKeybind saveFreecam = new BetterKeybind("freecam.save", GLFW.GLFW_KEY_J);

    public static final BetterKeybind playerMovement = new BetterKeybind("freecam.movement", GLFW.GLFW_KEY_J)
            .toggle().onPress(Freecam::playerMovement, Freecam::cameraMovement);

    public static final BetterKeybind zoom = new BetterKeybind("zoom", GLFW.GLFW_KEY_C).onPress(Zoom::start, Zoom::stop);

    public static final BetterKeybind thirdPersonModifier = new BetterKeybind("thirdPersonModifier", GLFW.GLFW_KEY_UNKNOWN);

    public static final BetterKeybind freelook = new BetterKeybind("freelook", GLFW.GLFW_KEY_UNKNOWN)
            .toggle().onPress(Freelook::start, Freelook::stop);

    public static void init() {
        new BetterKeybind("freelook.hold", GLFW.GLFW_KEY_UNKNOWN)
                .onPress(() -> freelook.setEnabled(true), () -> freelook.setEnabled(false)).condition(()->Freelook.state != Freelook.State.THIRD_PERSON);
        new BetterKeybind("fullbright", GLFW.GLFW_KEY_Y).toggle().defaultEnabled(Config.get().fullbright).onPress(
                () -> {
                    Config.get().fullbright = true;
                    client.gameRenderer.lightTexture().updateLightTexture = true;
                    Config.HANDLER.save();
                },
                () -> {
                    Config.get().fullbright = false;
                    client.gameRenderer.lightTexture().updateLightTexture = true;
                    Config.HANDLER.save();
                });
        new BetterKeybind("disableFog", GLFW.GLFW_KEY_UNKNOWN).toggle().defaultEnabled(Config.get().disableFog).onPress(
                () -> {
                    Config.get().disableFog = true;
                    Config.HANDLER.save();
                },
                () -> {
                    Config.get().disableFog = false;
                    Config.HANDLER.save();
                });
    }

    public static class BetterKeybind extends KeyMapping {
        private Runnable press = ()->{};
        private Runnable release = ()->{};
        private boolean toggle = false;
        private Supplier<Boolean> condition = ()->true;
        private boolean enabled = false;
        private boolean used = false;

        private BetterKeybind(String translationKey, int keyCode) {
            super("key.cameratweaks." + translationKey, keyCode, CATEGORY);
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
        public void setDown(boolean pressed) {
            if(condition.get()) {
                if (toggle) {
                    if (!pressed && isDown()) {
                        if (used) used = false;
                        else {
                            setEnabled(!enabled);
                            client.player.displayClientMessage(Component.translatable(getName().substring(4) + (enabled ? ".on" : ".off")), true);
                        }
                    }
                } else setEnabled(pressed);
            }
            super.setDown(pressed);
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

        public void setUsed() {
            used = true;
            KeyMapping.forAllKeyMappings(KeyBindingHelper.getBoundKeyOf(this), KeyMapping -> {
                if(KeyMapping instanceof BetterKeybind betterKeybind) betterKeybind.used = true;
            });
        }
    }
}
