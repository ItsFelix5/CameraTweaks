package cameratweaks.mixin;

import cameratweaks.*;
import net.minecraft.client.Mouse;
import net.minecraft.client.input.Scroller;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static cameratweaks.Util.client;

@Mixin(Mouse.class)
public class MouseMixin {
    @Redirect(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Scroller;update(DD)Lorg/joml/Vector2i;"))
    private Vector2i onScroll(Scroller instance, double horizontal, double vertical) {
        Vector2i vector2i = instance.update(horizontal, vertical);
        if (Keybinds.zoom.enabled()) Zoom.zoom(Zoom.zoom + vector2i.y * 0.1F * Zoom.zoom);
        else if (client.options.getPerspective() != Perspective.FIRST_PERSON && Keybinds.thirdPersonModifier.enabled()) ThirdPerson.modifyDistance(vector2i.y * 0.1F);
        else if (Keybinds.freecam.enabled() && !Keybinds.playerMovement.enabled()) Freecam.speed = MathHelper.clamp(Freecam.speed + (float) vector2i.y * 0.05F, 0.0F, 6F);
        else return vector2i;
        return new Vector2i(0, 0);
    }

    @Redirect(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;", ordinal = 0))
    private Object changeSensitivity(SimpleOption<Double> instance) {
        double sensitivity = instance.getValue();
        if (!Keybinds.zoom.enabled()) return sensitivity;
        return sensitivity * Math.tan(Math.PI / 4 / Zoom.currZoom);
    }

    @Redirect(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
    private void rotateFreelook(ClientPlayerEntity instance, double cursorDeltaX, double cursorDeltaY) {
        if(Keybinds.freelook.enabled()) {
            Freelook.pitch += (float) cursorDeltaY * 0.15F;
            Freelook.yaw += (float) cursorDeltaX * 0.15F;
        } else {
            instance.changeLookDirection(cursorDeltaX, cursorDeltaY);
            if(Freelook.enabled) {
                Entity entity = client.getCameraEntity();
                if(entity == null) return;
                Freelook.pitch = MathHelper.wrapDegrees(MathHelper.lerpAngleDegrees(0.3f, Freelook.pitch, entity.getPitch()));
                Freelook.yaw = MathHelper.wrapDegrees(MathHelper.lerpAngleDegrees(0.3f, Freelook.yaw, entity.getYaw()));

                if (Math.abs((MathHelper.wrapDegrees(entity.getPitch()) - Freelook.pitch) + (MathHelper.wrapDegrees(entity.getYaw()) - Freelook.yaw)) < 0.3f) Freelook.enabled = false;
            }
        }
    }
}
