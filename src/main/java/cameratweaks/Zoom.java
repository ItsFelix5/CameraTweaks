package cameratweaks;

import cameratweaks.config.Config;
import cameratweaks.config.TransitionType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import static cameratweaks.Util.client;

public class Zoom {
    private static final Util.Lerped initial = new Util.Lerped();
    private static final Util.Lerped scroll = new Util.Lerped();
    private static int zoom = 5;
    private static boolean in = true;

    private static TransitionType transition() {
        return in ? Config.get().zoomTransition : Config.get().zoomTransition.opposite();
    }

    public static void start() {
        if (Config.get().cinematicZoom) client.options.smoothCamera = true;
        if (transition().hasInverse()) initial.modify(transition()::inverse);
    }

    public static void stop() {
        if (!Config.get().rememberZoom) zoom = Config.get().defaultZoom;
        if (Config.get().cinematicZoom) client.options.smoothCamera = false;
        if (transition().hasInverse()) initial.modify(transition()::inverse);
    }

    public static void zoom(boolean in) {
        zoom = Math.clamp(zoom + (in ? 1 : -1) * Math.max(1, zoom / 3), 1, 100);
        client.player.displayClientMessage(Component.translatable("cameratweaks.zoom.set", zoom), true);
    }

    public static void tick() {
        float delta = client.getDeltaTracker().getGameTimeDeltaTicks();
        float targetZoom = Keybinds.zoom.enabled() ? 1F : 0F;
        in = targetZoom > initial.get(1F);

        initial.approach(targetZoom, delta / (in ? Config.get().zoomInSpeed : Config.get().zoomOutSpeed));

        targetZoom = zoom / 100F;
        scroll.approach(targetZoom, Math.abs(targetZoom - scroll.get(1F)) * delta / Config.get().zoomScrollSpeed);
    }

    public static float zoomDivisor(float tickDelta) {
        return Mth.lerp(scroll.get(tickDelta), 0F, 100F) * transition().apply(initial.get(tickDelta)) + 1F;
    }
}
