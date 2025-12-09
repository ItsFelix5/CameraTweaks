package cameratweaks.config;

import cameratweaks.ThirdPerson;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.CyclingListControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.gui.OptionListWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

import static cameratweaks.Util.client;

public class Config {
    public static ConfigClassHandler<Config> HANDLER = ConfigClassHandler.createBuilder(Config.class)
            .id(Identifier.fromNamespaceAndPath("cameratweaks", "config"))
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
    public boolean nightVisionFullbright = false;
    @SerialEntry
    public boolean alternateFreecam = false;
    @SerialEntry
    public TransitionType zoomTransition = TransitionType.SINE_IN;
    @SerialEntry
    public float zoomSpeed = 1F;
    @SerialEntry
    public boolean cinematicZoom = false;
    @SerialEntry
    public int cloudHeight = 0;
    @SerialEntry
    public List<ThirdPerson> thirdPersons = List.of(new ThirdPerson(), new ThirdPerson());

    public Config() {
        thirdPersons.get(1).invert = true;
    }

    public static Config get() {
        return HANDLER.instance();
    }

    public Screen generateScreen(Screen parentScreen) {
        return YetAnotherConfigLib.createBuilder()
                .save(()->{
                    get().thirdPersons = ThirdPerson.pending.stream().map(ThirdPerson::clone).toList();
                    HANDLER.save();
                })
                .title(Component.translatable("category.cameratweaks.cameratweaks"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("cameratweaks.options.general"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("cameratweaks.options.disableFog"))
                                .description(OptionDescription.of(Component.translatable("cameratweaks.options.disableFog.description")))
                                .binding(false, ()->disableFog, enabled->disableFog = enabled)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("cameratweaks.options.fullbright"))
                                .description(OptionDescription.of(Component.translatable("cameratweaks.options.fullbright.description")))
                                .binding(false, ()->fullbright, enabled->{
                                    fullbright = enabled;
                                    client.gameRenderer.lightTexture().updateLightTexture = true;
                                }).controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("cameratweaks.options.fullbright.nightvision"))
                                .description(OptionDescription.of(Component.translatable("cameratweaks.options.fullbright.nightvision.description")))
                                .binding(false, ()->nightVisionFullbright, enabled->nightVisionFullbright = enabled).controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<TransitionType>createBuilder()
                                .name(Component.translatable("cameratweaks.options.zoomAnimation"))
                                .description(OptionDescription.of(Component.translatable("cameratweaks.options.zoomAnimation.description")))
                                .binding(TransitionType.SINE_IN, ()->zoomTransition, val->zoomTransition = val)
                                .controller(o->CyclingListControllerBuilder.create(o).values(TransitionType.values()).formatValue(easing-> Component.literal(easing.name())))
                                .build())
                        .option(Option.<Float>createBuilder()
                                .name(Component.translatable("cameratweaks.options.zoomSpeed"))
                                .description(OptionDescription.of(Component.translatable("cameratweaks.options.zoomSpeed.description")))
                                .binding(1F, ()-> zoomSpeed, val-> zoomSpeed = val)
                                .controller(o-> FloatSliderControllerBuilder.create(o).step(0.1F).range(0F, 2F))
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("cameratweaks.options.cinematicZoom"))
                                .description(OptionDescription.of(Component.translatable("cameratweaks.options.cinematicZoom.description")))
                                .binding(false, ()-> cinematicZoom, enabled-> cinematicZoom = enabled)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("cameratweaks.options.freecam_save_behaviour"))
                                .description(OptionDescription.of(Component.translatable("cameratweaks.options.freecam_save_behaviour.description")))
                                .binding(false, ()-> alternateFreecam, enabled-> alternateFreecam = enabled)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("cameratweaks.options.cloudHeight"))
                                .description(OptionDescription.of(Component.translatable("cameratweaks.options.cloudHeight.description")))
                                .binding(0, ()-> cloudHeight, val-> cloudHeight = val)
                                .controller(o-> IntegerSliderControllerBuilder.create(o).step(2).range(-200, 300))
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("cameratweaks.options.thirdperson"))
                        .option(ButtonOption.createBuilder()
                                .name(Component.translatable("cameratweaks.options.new"))
                                .text(Component.empty())
                                .action((screen, button)->{
                                    ThirdPerson thirdPerson = new ThirdPerson();
                                    ThirdPerson.pending.add(thirdPerson);
                                    if(screen.tabManager.getCurrentTab() instanceof YACLScreen.CategoryTab categoryTab) {
                                        categoryTab.visitChildren(widget -> {
                                            if (widget instanceof OptionListWidget list) list.refreshOptions();
                                        });
                                        ((Runnable) screen).run();
                                    }
                                }).build())
                        .build())
                .build()
                .generateScreen(parentScreen);
    }
}
