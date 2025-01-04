package cameratweaks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.SimpleOption;

public class Main implements ModInitializer {
    public static final SimpleOption<Boolean> disableFog = SimpleOption.ofBoolean("options.disableFog", false);
    public static final SimpleOption<Boolean> fullBright = SimpleOption.ofBoolean("options.fullbright", false);

    @Override
    public void onInitialize() {
        Keybinds.init();
        ClientTickEvents.END_CLIENT_TICK.register(c -> {
            Freecam.tick();
            Zoom.tick();
        });
    }
}
