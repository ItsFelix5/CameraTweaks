package cameratweaks.mixin;

import cameratweaks.Keybinds;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class PacketHandlerMixin {
    @Inject(method = "handleRespawn", at = @At("HEAD"))
    private void onPlayerRespawn(ClientboundRespawnPacket packet, CallbackInfo ci) {
        Keybinds.freecam.setEnabled(false);
    }
}
