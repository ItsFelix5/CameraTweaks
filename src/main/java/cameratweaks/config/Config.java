package cameratweaks.config;

import cameratweaks.ThirdPerson;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.gui.OptionListWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.tab.ListHolderWidget;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

import static cameratweaks.Util.client;

public class Config {
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
    @SerialEntry
    public boolean alternateFreecam = false;
    @SerialEntry
    public boolean zoomAnimation = true;
    @SerialEntry
    public int cloudHeight = 192;
    @SerialEntry
    public int cloudSize = 32;
    @SerialEntry
    public List<ThirdPerson> thirdPersons = List.of(new ThirdPerson(), new ThirdPerson());

    Screen generateScreen(Screen parentScreen) {
        return YetAnotherConfigLib.createBuilder()
                .save(()->{
                    HANDLER.instance().thirdPersons = ThirdPerson.pending.stream().map(ThirdPerson::clone).toList();
                    HANDLER.save();
                })
                .title(Text.translatable("category.cameratweaks.cameratweaks"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("cameratweaks.options.general"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("cameratweaks.options.disableFog"))
                                .description(OptionDescription.of(Text.translatable("cameratweaks.options.disableFog.description")))
                                .binding(false, ()->disableFog, enabled->disableFog = enabled)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("cameratweaks.options.fullbright"))
                                .description(OptionDescription.of(Text.translatable("cameratweaks.options.fullbright.description")))
                                .binding(false, ()->fullbright, enabled->{
                                    fullbright = enabled;
                                    client.gameRenderer.getLightmapTextureManager().dirty = true;
                                }).controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("cameratweaks.options.zoomAnimation"))
                                .description(OptionDescription.of(Text.translatable("cameratweaks.options.zoomAnimation.description")))
                                .binding(true, ()->zoomAnimation, enabled->zoomAnimation = enabled)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("cameratweaks.options.freecam_save_behaviour"))
                                .description(OptionDescription.of(Text.translatable("cameratweaks.options.freecam_save_behaviour.description")))
                                .binding(false, ()-> alternateFreecam, enabled-> alternateFreecam = enabled)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Text.translatable("cameratweaks.options.cloudHeight"))
                                .description(OptionDescription.of(Text.translatable("cameratweaks.options.cloudHeight.description")))
                                .binding(192, ()-> cloudHeight, val-> cloudHeight = val)
                                .controller(o-> IntegerSliderControllerBuilder.create(o).step(2).range(150, 400))
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Text.translatable("cameratweaks.options.cloudSize"))
                                .description(OptionDescription.of(Text.translatable("cameratweaks.options.cloudSize.description")))
                                .binding(32, ()-> cloudSize, val-> {
                                    cloudSize = val;
                                    client.worldRenderer.getCloudRenderer().scheduleTerrainUpdate();
                                }).controller(o-> IntegerSliderControllerBuilder.create(o).step(4).range(16, 256))
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("cameratweaks.options.thirdperson"))
                        .option(ButtonOption.createBuilder()
                                .name(Text.translatable("cameratweaks.options.new"))
                                .text(Text.empty())
                                .action((screen, button)->{
                                    ThirdPerson thirdPerson = new ThirdPerson();
                                    ThirdPerson.pending.add(thirdPerson);
                                    if(screen.tabManager.getCurrentTab() instanceof YACLScreen.CategoryTab categoryTab) categoryTab.forEachChild(widget -> {
                                        if(widget instanceof ListHolderWidget<?> holder) ((OptionListWidget) holder.getList()).refreshOptions();
                                        ((Runnable) screen).run();
                                    });
                                }).build())
                        .build())
                .build()
                .generateScreen(parentScreen);
    }
}
