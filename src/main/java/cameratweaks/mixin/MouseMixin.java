package cameratweaks.mixin;

import cameratweaks.Freecam;
import cameratweaks.Keybinds;
import cameratweaks.Zoom;
import net.minecraft.client.Mouse;
import net.minecraft.client.input.Scroller;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Mouse.class)
public class MouseMixin {
    @Redirect(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Scroller;update(DD)Lorg/joml/Vector2i;"))
    private Vector2i onScroll(Scroller instance, double horizontal, double vertical) {
        Vector2i vector2i = instance.update(horizontal, vertical);
        if (Keybinds.zoom.enabled()) {
            Zoom.zoom(Zoom.zoom + vector2i.y * 0.1F * Zoom.zoom);
        } else if (Keybinds.freecam.enabled() && !Keybinds.playerMovement.enabled()) {
            Freecam.speed = MathHelper.clamp(Freecam.speed + (float) vector2i.y * 0.05F, 0.0F, 6F);
        } else return vector2i;
        return new Vector2i(0, 0);
    }

    @Redirect(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;", ordinal = 0))
    private Object changeSensitivity(SimpleOption<Double> instance) {
        double sensitivity = instance.getValue();
        if (!Keybinds.zoom.enabled()) return sensitivity;
        return sensitivity * Math.tan(Math.PI / 4 / Zoom.currZoom);
    }
}
