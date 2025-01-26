package cameratweaks.mixin;

import cameratweaks.Config;
import cameratweaks.ThirdPerson;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.utils.OptionUtils;
import dev.isxander.yacl3.gui.OptionListWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.tab.ListHolderWidget;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

@Mixin(value = YACLScreen.class, remap = false)
public abstract class YACLScreenMixin implements Runnable {
    @Shadow private boolean pendingChanges;
    @Shadow protected abstract void onOptionChanged(Option<?> option);

    @Shadow @Final public TabManager tabManager;

    @Redirect(method = "onOptionChanged", at = @At(value = "INVOKE", target = "Ldev/isxander/yacl3/api/utils/OptionUtils;consumeOptions(Ldev/isxander/yacl3/api/YetAnotherConfigLib;Ljava/util/function/Function;)V", ordinal = 0))
    private void onOptionChanged(YetAnotherConfigLib yacl, Function<Option<?>, Boolean> func) {
        if(ThirdPerson.pending == null) return;
        if(ThirdPerson.pending.size() != Config.HANDLER.instance().thirdPersons.size()) {
            pendingChanges = true;
            return;
        }
        AtomicBoolean val = new AtomicBoolean(false);
        if(tabManager.getCurrentTab() instanceof YACLScreen.CategoryTab tab && tab.getTitle().equals(Text.translatable("cameratweaks.options.thirdperson")))
            tab.forEachChild(child -> {
                if(child instanceof ListHolderWidget<?> holder)
                    ((OptionListWidget) holder.getList()).children().forEach(entry -> {
                        if(entry instanceof OptionListWidget.OptionEntry optionEntry && !val.get()) val.set(func.apply(optionEntry.option));
                    });
            });
        if(!val.get()) OptionUtils.consumeOptions(yacl, func);
    }

    @Redirect(method = "finishOrSave", at = @At(value = "INVOKE", target = "Ldev/isxander/yacl3/api/utils/OptionUtils;forEachOptions(Ldev/isxander/yacl3/api/YetAnotherConfigLib;" +
            "Ljava/util/function/Consumer;)V", ordinal = 0))
    private void finishOrSave(YetAnotherConfigLib yacl, Consumer<Option<?>> consumer) {
        if(tabManager.getCurrentTab() instanceof YACLScreen.CategoryTab tab && tab.getTitle().equals(Text.translatable("cameratweaks.options.thirdperson")))
            tab.forEachChild(child -> {
                if(child instanceof ListHolderWidget<?> holder)
                    ((OptionListWidget) holder.getList()).children().forEach(entry -> {
                        if(entry instanceof OptionListWidget.OptionEntry optionEntry) consumer.accept(optionEntry.option);
                    });
            });
        OptionUtils.forEachOptions(yacl, consumer);
    }

    @Redirect(method = "cancelOrReset", at = @At(value = "INVOKE", target = "Ldev/isxander/yacl3/api/utils/OptionUtils;forEachOptions(Ldev/isxander/yacl3/api/YetAnotherConfigLib;" +
            "Ljava/util/function/Consumer;)V", ordinal = 0))
    private void cancel(YetAnotherConfigLib yacl, Consumer<Option<?>> consumer) {
        if(tabManager.getCurrentTab() instanceof YACLScreen.CategoryTab tab && tab.getTitle().equals(Text.translatable("cameratweaks.options.thirdperson")))
            tab.forEachChild(child -> {
                if(child instanceof ListHolderWidget<?> holder)
                    ((OptionListWidget) holder.getList()).children().forEach(entry -> {
                        if(entry instanceof OptionListWidget.OptionEntry optionEntry) consumer.accept(optionEntry.option);
                    });
            });
        OptionUtils.forEachOptions(yacl, consumer);
        ThirdPerson.pending = null;
    }

    @Redirect(method = "cancelOrReset", at = @At(value = "INVOKE", target = "Ldev/isxander/yacl3/api/utils/OptionUtils;forEachOptions(Ldev/isxander/yacl3/api/YetAnotherConfigLib;" +
            "Ljava/util/function/Consumer;)V", ordinal = 1))
    private void reset(YetAnotherConfigLib yacl, Consumer<Option<?>> consumer) {
        if(tabManager.getCurrentTab() instanceof YACLScreen.CategoryTab tab && tab.getTitle().equals(Text.translatable("cameratweaks.options.thirdperson"))){
            ThirdPerson.pending = new ArrayList<>(List.of(new ThirdPerson(), new ThirdPerson()));
            tab.forEachChild(child -> {
                if(child instanceof ListHolderWidget<?> holder) ((OptionListWidget) holder.getList()).refreshOptions();
            });
        } else OptionUtils.forEachOptions(yacl, consumer);
    }

    @Redirect(method = "undo", at = @At(value = "INVOKE", target = "Ldev/isxander/yacl3/api/utils/OptionUtils;forEachOptions(Ldev/isxander/yacl3/api/YetAnotherConfigLib;" +
            "Ljava/util/function/Consumer;)V"))
    private void undo(YetAnotherConfigLib yacl, Consumer<Option<?>> consumer) {
        if(tabManager.getCurrentTab() instanceof YACLScreen.CategoryTab tab && tab.getTitle().equals(Text.translatable("cameratweaks.options.thirdperson"))){
            ThirdPerson.pending = new ArrayList<>(Config.HANDLER.instance().thirdPersons);
            tab.forEachChild(child -> {
                if(child instanceof ListHolderWidget<?> holder) ((OptionListWidget) holder.getList()).refreshOptions();
            });
        } else OptionUtils.forEachOptions(yacl, consumer);
    }

    @Override
    public void run() {
        onOptionChanged(null);
    }
}
