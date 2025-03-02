package cameratweaks;

import cameratweaks.config.KeybindController;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionFlag;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;

import java.util.ArrayList;

import static cameratweaks.Util.client;

public class ThirdPerson implements Cloneable {
    public static float distanceOffset = 0F;
    public static ThirdPerson current;
    public static ArrayList<ThirdPerson> pending;

    @SerialEntry(required = false)
    public int keyCode = -1;
    @SerialEntry
    public float xOffset = 4.0F;
    @SerialEntry
    public float yOffset = 0.0F;
    @SerialEntry
    public float zOffset = 0.0F;
    @SerialEntry
    public float pitch = 0.0F;
    @SerialEntry
    public float yaw = 0.0F;
    @SerialEntry
    public boolean changedFov = false;
    @SerialEntry
    public int fov;
    @SerialEntry
    public boolean rotatePlayer = true;
    @SerialEntry
    public boolean invert = false;
    @SerialEntry
    public boolean collision = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ThirdPerson that)) return false;
        return (keyCode == that.keyCode && Float.compare(xOffset, that.xOffset) == 0 && Float.compare(yOffset, that.yOffset) == 0 && Float.compare(zOffset, that.zOffset) == 0 &&
                Float.compare(pitch, that.pitch) == 0 && Float.compare(yaw, that.yaw) == 0 && Float.compare(fov, that.fov) == 0 && invert == that.invert && collision == that.collision);
    }

    @Override
    public ThirdPerson clone() {
        try {
            return (ThirdPerson) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void setCurrent(ThirdPerson current) {
        if(ThirdPerson.current != null && !ThirdPerson.current.rotatePlayer) {
            Keybinds.freelook.setEnabled(false);
            Freelook.enabled = false;// Bye bye animation :(
        }
        if(current != null && !current.rotatePlayer) Keybinds.freelook.setEnabled(true);
        ThirdPerson.current = current;
    }

    public static void modifyDistance(float amount) {
        distanceOffset += amount;
        client.player.sendMessage(Text.translatable("cameratweaks.thirdperson.distance", Math.round(current.xOffset + distanceOffset)), true);
    }

    public OptionGroup toGroup(int i) {
        OptionGroup.Builder builder = OptionGroup.createBuilder()
                .name(Text.translatable("cameratweaks.options.thirdperson."+ (i == 0? "back" : i == 1? "front" : "custom"), i - 1))
                .collapsed(true);
        if(i > 1) builder.option(Option.<Integer>createBuilder()
                .name(Text.translatable("cameratweaks.options.thirdperson.key"))
                .description(OptionDescription.of(Text.translatable("cameratweaks.options.thirdperson.key.description")))
                .binding(-1, ()->keyCode, key->keyCode = key)
                .customController(KeybindController::new)
                .build());

        builder.option(Option.<Float>createBuilder()
                .name(Text.translatable("cameratweaks.options.thirdperson.x"))
                .description(OptionDescription.of(Text.translatable("cameratweaks.options.thirdperson.x.description")))
                .binding(4.0F, ()->xOffset, val->xOffset = val)
                .controller(o-> FloatSliderControllerBuilder.create(o).range(0F, 50.0F).step(0.5F))
                .flag(OptionFlag.WORLD_RENDER_UPDATE)
                .build());
        builder.option(Option.<Float>createBuilder()
                .name(Text.translatable("cameratweaks.options.thirdperson.y"))
                .description(OptionDescription.of(Text.translatable("cameratweaks.options.thirdperson.y.description")))
                .binding(0.0F, ()->yOffset, val->yOffset = val)
                .controller(o->FloatSliderControllerBuilder.create(o).range(-15F, 15F).step(0.5F))
                .flag(OptionFlag.WORLD_RENDER_UPDATE)
                .build());
        builder.option(Option.<Float>createBuilder()
                .name(Text.translatable("cameratweaks.options.thirdperson.z"))
                .description(OptionDescription.of(Text.translatable("cameratweaks.options.thirdperson.z.description")))
                .binding(0.0F, ()->zOffset, val->zOffset = val)
                .controller(o->FloatSliderControllerBuilder.create(o).range(-15F, 15F).step(0.5F))
                .flag(OptionFlag.WORLD_RENDER_UPDATE)
                .build());

        builder.option(Option.<Float>createBuilder()
                .name(Text.translatable("cameratweaks.options.thirdperson.pitch"))
                .description(OptionDescription.of(Text.translatable("cameratweaks.options.thirdperson.pitch.description")))
                .binding(0.0F, ()->pitch, val->pitch = val)
                .controller(o->FloatSliderControllerBuilder.create(o).range(-90F, 90F).step(1F))
                .flag(OptionFlag.WORLD_RENDER_UPDATE)
                .build());
        builder.option(Option.<Float>createBuilder()
                .name(Text.translatable("cameratweaks.options.thirdperson.yaw"))
                .description(OptionDescription.of(Text.translatable("cameratweaks.options.thirdperson.yaw.description")))
                .binding(0.0F, ()->yaw, val->yaw = val)
                .controller(o->FloatSliderControllerBuilder.create(o).range(-180F, 180F).step(1F))
                .flag(OptionFlag.WORLD_RENDER_UPDATE)
                .build());

        builder.option(Option.<Integer>createBuilder()
                .name(Text.translatable("cameratweaks.options.thirdperson.fov"))
                .description(OptionDescription.of(Text.translatable("cameratweaks.options.thirdperson.fov.description")))
                .binding(client.options.getFov().getValue(), ()->changedFov?fov:client.options.getFov().getValue(), val->{fov = val; changedFov = fov != client.options.getFov().getValue();})
                .controller(o-> IntegerSliderControllerBuilder.create(o).range(10, 135).step(1))
                .build());

        builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("cameratweaks.options.thirdperson.rotatePlayer"))
                .description(OptionDescription.of(Text.translatable("cameratweaks.options.thirdperson.rotatePlayer.description")))
                .binding(true, ()->rotatePlayer, val->rotatePlayer = val)
                .controller(BooleanControllerBuilder::create)
                .build());

        if(i > 1) builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("cameratweaks.options.thirdperson.invert"))
                .description(OptionDescription.of(Text.translatable("cameratweaks.options.thirdperson.invert.description")))
                .binding(false, ()->invert, val->invert = val)
                .controller(BooleanControllerBuilder::create)
                .flag(OptionFlag.WORLD_RENDER_UPDATE)
                .build());
        builder.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("cameratweaks.options.thirdperson.collision"))
                .description(OptionDescription.of(Text.translatable("cameratweaks.options.thirdperson.collision.description")))
                .binding(true, ()->collision, val->collision = val)
                .controller(BooleanControllerBuilder::create)
                .flag(OptionFlag.WORLD_RENDER_UPDATE)
                .build());
        return builder.build();
    }
}
