package cameratweaks;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Config implements ModMenuApi {
    public static ConfigClassHandler<Config> HANDLER = ConfigClassHandler.createBuilder(Config.class)
            .id(Identifier.of("cameratweaks", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("cameratweaks.json5"))
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry
    public boolean disableFog = false;

    @SerialEntry
    public boolean fullbright = false;

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parentScreen -> YetAnotherConfigLib.createBuilder()
                .save(HANDLER::save)
                .title(Text.translatable("category.cameratweaks.cameratweaks"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Options"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("options.disableFog"))
                                .description(OptionDescription.of(Text.translatable("options.disableFog.tooltip")))
                                .binding(false, ()->HANDLER.instance().disableFog, enabled->HANDLER.instance().disableFog = enabled)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("options.fullbright"))
                                .description(OptionDescription.of(Text.translatable("options.fullbright.tooltip")))
                                .binding(false, Keybinds.fullBright::enabled, Keybinds.fullBright::setEnabled)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .build())
                .build()
                .generateScreen(parentScreen);
    }
}
