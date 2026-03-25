package cameratweaks.mixin;

import cameratweaks.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.ScrollWheelHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static cameratweaks.Util.input;

@Mixin(MouseHandler.class)
public class MouseMixin {
    @Shadow @Final private Minecraft minecraft;

    @Redirect(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/ScrollWheelHandler;onMouseScroll(DD)Lorg/joml/Vector2i;"))
    private Vector2i onScroll(ScrollWheelHandler instance, double horizontal, double vertical) {
        Vector2i vector2i = instance.onMouseScroll(horizontal, vertical);
        if (Keybinds.zoom.enabled()) Zoom.zoom(vector2i.y > 0);
        else if (ThirdPerson.current != null && Keybinds.thirdPersonModifier.enabled()) ThirdPerson.modifyDistance(vector2i.y / 3F);
        else if (Keybinds.freecam.enabled() && !Keybinds.playerMovement.enabled()) minecraft.player.sendOverlayMessage(Component.translatable("cameratweaks.freecam.speed",
                (int) (20 * (Freecam.speed = Mth.clamp(Freecam.speed + (float) vector2i.y * 0.05F, 0.0F, 6F)))));
        else return vector2i;
        return new Vector2i(0, 0);
    }

    @Redirect(method = "turnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;", ordinal = 0))
    private Object changeSensitivity(OptionInstance<Double> instance) {
        return instance.get() * Math.pow(1.0 / Zoom.zoomDivisor(0F), 0.6);
    }

    @Redirect(method = "turnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"))
    private void changeLookDirection(LocalPlayer instance, double cursorDeltaX, double cursorDeltaY) {
        Entity entity = minecraft.getCameraEntity();
        if(entity == null) entity = instance;
        if (Keybinds.freecam.enabled() && !Keybinds.playerMovement.enabled()) {
            Freecam.pos.yaw += (float) cursorDeltaX * 0.15F;
            Freecam.pos.pitch = Mth.clamp(Freecam.pos.pitch + (float) cursorDeltaY * 0.15F, -90.0F, 90.0F);
        } else if(Freelook.state == Freelook.State.FREELOOKING || Freelook.state == Freelook.State.THIRD_PERSON) {
            Freelook.pitch += (float) cursorDeltaY * 0.15F;
            Freelook.yaw += (float) cursorDeltaX * 0.15F;
            if (Freelook.state == Freelook.State.THIRD_PERSON) {
                if(instance.isFallFlying()) instance.turn(cursorDeltaX, cursorDeltaY);
                else {
                    Freelook.pitch = Math.clamp(Freelook.pitch, -90, 90);
                    if (Util.isMoving()) minecraft.player.setXRot(Freelook.pitch * (input.keyPresses.backward() ? -1 : 1));
                }
            }
        } else {
            instance.turn(cursorDeltaX, cursorDeltaY);
            if(Freelook.state == Freelook.State.ANIMATING) {
                Freelook.pitch = Mth.wrapDegrees(Mth.rotLerp(0.35f, Freelook.pitch, entity.getXRot()));
                Freelook.yaw = Mth.wrapDegrees(Mth.rotLerp(0.35f, Freelook.yaw, entity.getYRot()));

                if (Math.abs((Mth.wrapDegrees(entity.getXRot()) - Freelook.pitch) + (Mth.wrapDegrees(entity.getYRot()) - Freelook.yaw)) < 0.4f) Freelook.state = Freelook.State.INACTIVE;
            }
        }
    }
}
