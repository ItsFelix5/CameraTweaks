package cameratweaks;

import cameratweaks.config.Config;
import cameratweaks.config.TransitionType;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import static cameratweaks.Util.client;

public class Zoom {
    private static final Util.Lerped initial = new Util.Lerped();
    private static final Util.Lerped scroll = new Util.Lerped();
    private static int zoom = 0;
    private static boolean in = true;

    private static TransitionType transition() {
        return in ? Config.get().zoomTransition : Config.get().zoomTransition.opposite();
    }

    public static void start() {
        if (Config.get().cinematicZoom) client.options.smoothCameraEnabled = true;
        if (transition().hasInverse()) initial.modify(transition()::inverse);
    }

    public static void stop() {
        zoom = 0;
        if (Config.get().cinematicZoom) client.options.smoothCameraEnabled = false;
        if (transition().hasInverse()) initial.modify(transition()::inverse);
    }

    public static void zoom(boolean in) {
        zoom = Math.clamp(zoom + (in? 1:-1) * Math.max(1, zoom / 3), 0, 95);
        client.player.sendMessage(Text.translatable("cameratweaks.zoom.set", 5 + zoom), true);
    }

    public static void tick() {
        float targetZoom = Keybinds.zoom.enabled() ? 1F : 0F;
        in = targetZoom > initial.get(1F);

        initial.tick(Util.approach(initial.get(1F), targetZoom, client.getRenderTickCounter().getDynamicDeltaTicks() / Config.get().zoomSpeed));

        targetZoom = zoom / 95F;

        scroll.tick(Util.approach(scroll.get(1F), targetZoom, Math.abs(targetZoom - scroll.get(1F)) / 0.5F * client.getRenderTickCounter().getDynamicDeltaTicks()));
    }

    public static float zoomDivisor(float tickDelta) {
        return MathHelper.lerp(transition().apply(initial.get(tickDelta)), 1F, 5F)
                + MathHelper.lerp(scroll.get(tickDelta), 0F, 95F * 6F);
    }
}
