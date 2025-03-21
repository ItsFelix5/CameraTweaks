package cameratweaks.mixin;

import cameratweaks.config.Config;
import net.minecraft.client.render.CloudRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CloudRenderer.class)
public class CloudRendererMixin {
    @ModifyConstant(method = "buildCloudCells", constant = {@Constant(intValue = 32), @Constant(intValue = -32)})
    public int buildCloudCells(int original) {
        return (original < 0? -1:1) * Config.HANDLER.instance().cloudSize;
    }
}
