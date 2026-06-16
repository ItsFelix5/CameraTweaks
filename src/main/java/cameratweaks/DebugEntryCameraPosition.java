package cameratweaks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Locale;

import static cameratweaks.Util.client;

@Environment(EnvType.CLIENT)
public class DebugEntryCameraPosition implements DebugScreenEntry {
    public static final Identifier GROUP = Identifier.withDefaultNamespace("position");

    @Override
    public void display(@NotNull DebugScreenDisplayer debugScreenDisplayer, @Nullable Level level, @Nullable LevelChunk levelChunk, @Nullable LevelChunk levelChunk2) {
        Camera camera = client.gameRenderer.mainCamera();
        Entity entity = client.getCameraEntity();
        Direction direction = Direction.fromYRot(camera.yRot());
        String string = switch (direction) {
            case NORTH -> "Towards negative Z";
            case SOUTH -> "Towards positive Z";
            case WEST -> "Towards negative X";
            case EAST -> "Towards positive X";
            default -> "Invalid";
        };

        ArrayList<String> list = new ArrayList<>();
        list.add(String.format(Locale.ROOT, "Camera Position: %.3f / %.3f / %.3f", camera.position().x, camera.position().y, camera.position().z));
        if(entity == null || camera.xRot() != entity.getXRot() || camera.yRot() != entity.getYRot()) list.add(String.format(Locale.ROOT, "Camera Facing: %s (%s) (%.1f / %.1f)", direction, string, Mth.wrapDegrees(camera.yRot()), Mth.wrapDegrees(camera.xRot())));
        debugScreenDisplayer.addToGroup(GROUP, list);
    }
}
