package cameratweaks.config;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public record KeybindController(Option<Integer> option) implements Controller<Integer> {
    @Override
    public Text formatValue() {
        return InputUtil.Type.KEYSYM.createFromCode(option.pendingValue()).getLocalizedText();
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new KeyBindingWidget(this, screen, widgetDimension);
    }

    public static class KeyBindingWidget extends ControllerWidget<KeybindController> {
        private boolean listening = false;

        public KeyBindingWidget(KeybindController control, YACLScreen screen, Dimension<Integer> dim) {
            super(control, screen, dim);
        }

        @Override
        protected int getHoveredControlWidth() {
            return getUnhoveredControlWidth();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!isMouseOver(mouseX, mouseY) || !isAvailable())
                return false;

            listening = !listening;
            return true;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (!listening) return false;
            listening = false;
            if(keyCode != GLFW.GLFW_KEY_ESCAPE) control.option().requestSet(keyCode);
            return true;
        }

        @Override
        protected Text getValueText() {
            if(listening) return Text.literal("> ")
                    .append(control.formatValue().copy().formatted(Formatting.WHITE, Formatting.UNDERLINE))
                    .append(" <")
                    .formatted(Formatting.YELLOW);
            return control.formatValue();
        }
    }
}
