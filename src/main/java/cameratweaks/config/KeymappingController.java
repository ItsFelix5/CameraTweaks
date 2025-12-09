package cameratweaks.config;

import com.mojang.blaze3d.platform.InputConstants;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public record KeymappingController(Option<Integer> option) implements Controller<Integer> {
    @Override
    public Component formatValue() {
        return InputConstants.Type.KEYSYM.getOrCreate(option.pendingValue()).getDisplayName();
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new KeyBindingWidget(this, screen, widgetDimension);
    }

    public static class KeyBindingWidget extends ControllerWidget<KeymappingController> {
        private boolean listening = false;

        public KeyBindingWidget(KeymappingController control, YACLScreen screen, Dimension<Integer> dim) {
            super(control, screen, dim);
        }

        @Override
        protected int getHoveredControlWidth() {
            return getUnhoveredControlWidth();
        }

        @Override
        public boolean onMouseClicked(double mouseX, double mouseY, int button) {
            if (!isMouseOver(mouseX, mouseY) || !isAvailable())
                return false;

            listening = !listening;
            return true;
        }

        @Override
        public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
            if (!listening) return false;
            listening = false;
            if(keyCode != GLFW.GLFW_KEY_ESCAPE) control.option().requestSet(keyCode);
            return true;
        }

        @Override
        protected Component getValueText() {
            if(listening) return Component.literal("> ")
                    .append(control.formatValue().copy().withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE))
                    .append(" <")
                    .withStyle(ChatFormatting.YELLOW);
            return control.formatValue();
        }
    }
}
