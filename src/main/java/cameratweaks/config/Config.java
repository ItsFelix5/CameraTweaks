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
    public boolean disableFreecamOnDamage = false;
    @SerialEntry
    public TransitionType zoomTransition = TransitionType.SINE_IN;
    @SerialEntry
    public float zoomInSpeed = 1F;
    @SerialEntry
    public float zoomOutSpeed = 1F;
    @SerialEntry
    public float zoomScrollSpeed = 1F;
    @SerialEntry
    public boolean cinematicZoom = false;
    @SerialEntry
    public boolean rememberZoom = false;
    @SerialEntry
    public int defaultZoom = 5;
    @SerialEntry
    public int cloudHeight = 0;
    @SerialEntry
    public List<ThirdPerson> thirdPersons = List.of(new ThirdPerson(), new ThirdPerson());
    @SerialEntry
    public boolean freelookTogglePerspective = false;

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
                                .name(Component.translatable("cameratweaks.options.zoomInSpeed"))
                                .description(OptionDescription.of(Component.translatable("cameratweaks.options.zoomInSpeed.description")))
                                .binding(1F, ()-> zoomInSpeed, val-> zoomInSpeed = val)
                                .controller(o-> FloatSliderControllerBuilder.create(o).step(0.1F).range(0F, 3F))
                                .build())
                        .option(Option.<Float>createBuilder()
                                .name(Component.translatable("cameratweaks.options.zoomOutSpeed"))
                                .description(OptionDescription.of(Component.translatable("cameratweaks.options.zoomOutSpeed.description")))
                                .binding(1F, ()-> zoomOutSpeed, val-> zoomOutSpeed = val)
                                .controller(o-> FloatSliderControllerBuilder.create(o).step(0.1F).range(0F, 3F))
                                .build())
                        .option(Option.<Float>createBuilder()
                                .name(Component.translatable("cameratweaks.options.zoomScrollSpeed"))
                                .description(OptionDescription.of(Component.translatable("cameratweaks.options.zoomScrollSpeed.description")))
                                .binding(1F, ()-> zoomScrollSpeed, val-> zoomScrollSpeed = val)
                                .controller(o-> FloatSliderControllerBuilder.create(o).step(0.1F).range(0F, 3F))
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("cameratweaks.options.cinematicZoom"))
                                .description(OptionDescription.of(Component.translatable("cameratweaks.options.cinematicZoom.description")))
                                .binding(false, ()-> cinematicZoom, enabled-> cinematicZoom = enabled)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("cameratweaks.options.rememberZoom"))
                                .description(OptionDescription.of(Component.translatable("cameratweaks.options.rememberZoom.description")))
                                .binding(false, ()-> rememberZoom, enabled-> rememberZoom = enabled)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("cameratweaks.options.defaultZoom"))
                                .description(OptionDescription.of(Component.translatable("cameratweaks.options.defaultZoom.description")))
                                .binding(5, ()-> defaultZoom, val-> defaultZoom = val)
                                .controller(o-> IntegerSliderControllerBuilder.create(o).step(1).range(1, 100))
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("cameratweaks.options.freecamSaveBehaviour"))
                                .description(OptionDescription.of(Component.translatable("cameratweaks.options.freecamSaveBehaviour.description")))
                                .binding(false, ()-> alternateFreecam, enabled-> alternateFreecam = enabled)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("cameratweaks.options.disableFreecamOnDamage"))
                                .description(OptionDescription.of(Component.translatable("cameratweaks.options.disableFreecamOnDamage.description")))
                                .binding(false, ()-> disableFreecamOnDamage, enabled-> disableFreecamOnDamage = enabled)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("cameratweaks.options.cloudHeight"))
                                .description(OptionDescription.of(Component.translatable("cameratweaks.options.cloudHeight.description")))
                                .binding(0, ()-> cloudHeight, val-> cloudHeight = val)
                                .controller(o-> IntegerSliderControllerBuilder.create(o).step(2).range(-200, 300))
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("cameratweaks.options.freelookTogglePerspective"))
                                .description(OptionDescription.of(Component.translatable("cameratweaks.options.freelookTogglePerspective.description")))
                                .binding(false, ()-> freelookTogglePerspective, enabled-> freelookTogglePerspective = enabled)
                                .controller(TickBoxControllerBuilder::create)
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
