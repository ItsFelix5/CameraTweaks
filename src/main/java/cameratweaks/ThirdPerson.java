package cameratweaks;

import net.minecraft.text.Text;

import static cameratweaks.Util.client;

public class ThirdPerson {
    public static float distance = 4.0F;

    public static void modifyDistance(float amount) {
        distance = Math.max(1, distance - amount);
        client.player.sendMessage(Text.translatable("cameratweaks.thirdperson.distance", Math.round(distance)), true);
    }
}
