package cameratweaks.mixin;

import cameratweaks.*;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Mouse.class)
public class MouseMixin {
    @Redirect(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V"))
    private void onScroll(PlayerInventory instance, double scrollAmount) {
        if (Keybinds.zoom.enabled()) Zoom.zoom(Zoom.zoom + (float) scrollAmount * 0.1F * Zoom.zoom);
        else if (ThirdPerson.current != null && Keybinds.thirdPersonModifier.enabled()) ThirdPerson.modifyDistance((float) scrollAmount * 0.1F);
        else if (Keybinds.freecam.enabled() && !Keybinds.playerMovement.enabled()) client.player.sendMessage(Text.translatable("cameratweaks.freecam.speed",
                (int) (20 * (Freecam.speed = MathHelper.clamp(Freecam.speed + (float) vector2i.y * 0.05F, 0.0F, 6F)))), true);
        else instance.scrollInHotbar(scrollAmount);
    }

    @Redirect(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;", ordinal = 0))
    private Object changeSensitivity(SimpleOption<Double> instance) {
        return instance.getValue() * (Zoom.currZoom <= 1?1:Math.tan(Math.PI / 4 / Zoom.currZoom));
    }

    @Redirect(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
    private void changeLookDirection(ClientPlayerEntity instance, double cursorDeltaX, double cursorDeltaY) {
        Entity entity = client.getCameraEntity();
        if(entity == null) entity = instance;
        if (Keybinds.freecam.enabled() && !Keybinds.playerMovement.enabled()) {
            Freecam.pos.yaw += (float) cursorDeltaX * 0.15F;
            Freecam.pos.pitch = MathHelper.clamp(Freecam.pos.pitch + (float) cursorDeltaY * 0.15F, -90.0F, 90.0F);
        } else if(Keybinds.freelook.enabled()) {
            Freelook.pitch += (float) cursorDeltaY * 0.15F;
            Freelook.yaw += (float) cursorDeltaX * 0.15F;
            if (ThirdPerson.current != null && !ThirdPerson.current.rotatePlayer) {
                Freelook.pitch = Math.clamp(Freelook.pitch, -90, 90);
                if(Util.isMoving()) client.player.setPitch(Freelook.pitch);
            }
        } else {
            instance.changeLookDirection(cursorDeltaX, cursorDeltaY);
            if(Freelook.enabled) {
                Freelook.pitch = MathHelper.wrapDegrees(MathHelper.lerpAngleDegrees(0.35f, Freelook.pitch, entity.getPitch()));
                Freelook.yaw = MathHelper.wrapDegrees(MathHelper.lerpAngleDegrees(0.35f, Freelook.yaw, entity.getYaw()));

                if (Math.abs((MathHelper.wrapDegrees(entity.getPitch()) - Freelook.pitch) + (MathHelper.wrapDegrees(entity.getYaw()) - Freelook.yaw)) < 0.4f) Freelook.enabled = false;
            }
        }
    }
}
