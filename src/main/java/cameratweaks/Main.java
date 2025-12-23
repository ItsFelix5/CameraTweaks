package cameratweaks;

import cameratweaks.config.Config;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.resources.Identifier;

public class Main implements ModInitializer {
    @Override
    public void onInitialize() {
        DebugScreenEntries.register(Identifier.fromNamespaceAndPath("cameratweaks", "camera_position"), new DebugEntryCameraPosition());
        Config.HANDLER.load();
        Keybinds.init();
        ClientTickEvents.END_CLIENT_TICK.register(c -> {
            Zoom.tick();
            Freelook.tick();
            Util.input.tick();
        });
    }
}
