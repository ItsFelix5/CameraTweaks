package cameratweaks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class Main implements ModInitializer {
    @Override
    public void onInitialize() {
        Config.HANDLER.load();
        Keybinds.init();
        ClientTickEvents.END_CLIENT_TICK.register(c -> {
            Freecam.tick();
            Zoom.tick();
        });
    }
}
