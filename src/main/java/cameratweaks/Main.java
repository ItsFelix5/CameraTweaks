package cameratweaks;

import cameratweaks.config.Config;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class Main implements ModInitializer {
    @Override
    public void onInitialize() {
        Config.HANDLER.load();
        Keybinds.init();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(Config.HANDLER.instance().zoomAnimation) Zoom.tick();
            Freelook.tick();
            if (Keybinds.freecam.enabled() && !Keybinds.playerMovement.enabled()) Util.input.tick(false, 0F);
        });
    }
}
