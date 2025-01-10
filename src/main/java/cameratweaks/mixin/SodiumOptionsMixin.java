package cameratweaks.mixin;

import cameratweaks.Main;
import com.google.common.collect.ImmutableList;
import net.caffeinemc.mods.sodium.client.gui.SodiumGameOptionPages;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Mixin(value = SodiumGameOptionPages.class, remap = false)
public class SodiumOptionsMixin {
    @SuppressWarnings("unchecked")
    @Redirect(method = "general", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;copyOf(Ljava/util/Collection;)Lcom/google/common/collect/ImmutableList;"))
    private static <E> ImmutableList<E> addOptions(Collection<E> list) throws Exception {
        Class<?> Option = Class.forName("net.caffeinemc.mods.sodium.client.gui.options.Option");
        Class<?> OptionGroupBuilder = Class.forName("net.caffeinemc.mods.sodium.client.gui.options.OptionGroup$Builder");
        Class<?> OptionBuilder = Class.forName("net.caffeinemc.mods.sodium.client.gui.options.OptionImpl$Builder");

        Object group = Class.forName("net.caffeinemc.mods.sodium.client.gui.options.OptionGroup").getMethod("createBuilder").invoke(null);
        Object builder = Class.forName("net.caffeinemc.mods.sodium.client.gui.options.OptionImpl").getMethod("createBuilder", Class.class,
                Class.forName("net.caffeinemc.mods.sodium.client.gui.options.storage.OptionStorage")).invoke(null, boolean.class, SodiumOptionsMixin.class.getDeclaredField("vanillaOpts").get(null));
        OptionBuilder.getMethod("setName", Text.class).invoke(builder, Text.translatable("options.disableFog"));
        OptionBuilder.getMethod("setTooltip", Text.class).invoke(builder, Text.translatable("options.disableFog.tooltip"));
        OptionBuilder.getMethod("setControl", Function.class).invoke(builder, (Function<?, ?>) (a)-> {
            try {
                return Class.forName("net.caffeinemc.mods.sodium.client.gui.options.control.TickBoxControl").getConstructor(Option).newInstance(a);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        OptionBuilder.getMethod("setBinding", BiConsumer.class, Function.class).invoke(builder, (BiConsumer<Object, Boolean>) (options, value) -> Main.disableFog.setValue(value), (Function<Object, Boolean>) options -> Main.disableFog.getValue());

        OptionGroupBuilder.getMethod("add", Option).invoke(group, OptionBuilder.getMethod("build").invoke(builder));
        list.add((E) OptionGroupBuilder.getMethod("build").invoke(group));
        return ImmutableList.copyOf(list);
    }
}
