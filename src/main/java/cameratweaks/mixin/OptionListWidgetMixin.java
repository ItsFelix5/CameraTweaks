package cameratweaks.mixin;

import cameratweaks.Config;
import cameratweaks.ThirdPerson;
import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

@Mixin(value = OptionListWidget.class, remap = false)
public abstract class OptionListWidgetMixin extends ElementListWidgetExt<OptionListWidget.Entry> {
    @Shadow @Final private ConfigCategory category;
    @Shadow @Final private YACLScreen yaclScreen;

    @Shadow public abstract Dimension<Integer> getDefaultEntryDimension();

    public OptionListWidgetMixin(MinecraftClient client, int x, int y, int width, int height, boolean smoothScrolling) {
        super(client, x, y, width, height, smoothScrolling);
    }

    @Inject(method = "refreshOptions", at = @At(value = "INVOKE", target = "Ldev/isxander/yacl3/gui/OptionListWidget;recacheViewableChildren()V"))
    private void addThirdPersons(CallbackInfo ci) throws Exception {
        if(!category.name().equals(Text.translatable("cameratweaks.options.thirdperson"))) return;

        Constructor<OptionListWidget.GroupSeparatorEntry> groupSeparatorEntryConstructor = OptionListWidget.GroupSeparatorEntry.class.getDeclaredConstructor(OptionListWidget.class, OptionGroup.class, Screen.class);

        if(ThirdPerson.pending == null) ThirdPerson.pending = new ArrayList<>(Config.HANDLER.instance().thirdPersons);
        for (int i = 0; i < ThirdPerson.pending.size(); i++) {
            OptionGroup group = ThirdPerson.pending.get(i).toGroup(i);

            OptionListWidget.GroupSeparatorEntry groupSeparatorEntry = groupSeparatorEntryConstructor.newInstance(this, group, yaclScreen);
            addEntry(groupSeparatorEntry);

            List<OptionListWidget.Entry> optionEntries = new ArrayList<>();

            for (Option<?> option : group.options()) {
                option.addEventListener((opt, event) -> ((Runnable) yaclScreen).run());
                OptionListWidget.OptionEntry entry = ((OptionListWidget) (Object) this).new OptionEntry(option, category, group, groupSeparatorEntry,
                        option.controller().provideWidget(yaclScreen, getDefaultEntryDimension()));
                addEntry(entry);
                optionEntries.add(entry);
            }

            groupSeparatorEntry.setChildEntries(optionEntries);
        }
    }

    @Mixin(value = OptionListWidget.GroupSeparatorEntry.class, remap = false)
    public static class GroupSeparatorEntryMixin {
        @Unique private TooltipButtonWidget removeListButton;
        @Shadow @Final protected LowProfileButtonWidget expandMinimizeButton;

        @Inject(method = "<init>", at = @At("TAIL"))
        public void init(OptionListWidget this$0, OptionGroup group, Screen screen, CallbackInfo ci) {
            TranslatableTextContent name = (TranslatableTextContent) group.name().getContent();
            if(name.getKey().equals("cameratweaks.options.thirdperson.custom")) removeListButton = new TooltipButtonWidget(screen, this$0.getRowRight() - 20, -50, 20, 20,
                    Text.literal("X"), Text.translatable("yacl.list.remove"), btn -> {
                ThirdPerson.pending.remove((int) name.getArgs()[0] + 1);
                this$0.refreshOptions();
                ((Runnable) screen).run();
            });
        }

        @Inject(method = "render", at = @At("TAIL"))
        public void render(DrawContext graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
            if(removeListButton != null) {
                removeListButton.setY(expandMinimizeButton.getY());
                removeListButton.render(graphics, mouseX, mouseY, tickDelta);
            }
        }

        @Inject(method = "children", at = @At("HEAD"), cancellable = true)
        public void children(CallbackInfoReturnable<List<? extends Element>> cir) {
            if(removeListButton != null) cir.setReturnValue(ImmutableList.of(expandMinimizeButton, removeListButton));
        }
    }
}
