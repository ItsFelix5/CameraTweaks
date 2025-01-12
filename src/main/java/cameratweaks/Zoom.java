package cameratweaks;

import net.minecraft.text.Text;

import static cameratweaks.Util.client;

public class Zoom {
    public static float prevZoom = 1F, currZoom = 1F, zoom = 1F;

    public static void start() {
        zoom = 5F;
    }

    public static void stop() {
        zoom = 1F;
    }

    public static void zoom(float zoom) {
        if(zoom < 0.25f) return;
        Zoom.zoom = zoom;
        client.player.sendMessage(Text.translatable("cameratweaks.zoom.set", (int) zoom), true);
    }

    public static void tick() {
        prevZoom = currZoom;
        if (zoom != currZoom) currZoom += (zoom - currZoom) / 2;
    }
}
