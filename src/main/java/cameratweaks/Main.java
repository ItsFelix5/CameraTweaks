package cameratweaks;

import cameratweaks.config.Config;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class Main implements ModInitializer {
    @Override
    public void onInitialize() {
        Config.HANDLER.load();
        Keybinds.init();
        ClientTickEvents.END_CLIENT_TICK.register(c -> {
            if(Config.HANDLER.instance().zoomAnimation) Zoom.tick();
            Freelook.tick();
            Util.input.tick(false, 1);
        });
    }
}
