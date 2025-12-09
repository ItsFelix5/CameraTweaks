package cameratweaks.mixin;

import cameratweaks.ThirdPerson;
import cameratweaks.config.Config;
import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.gui.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = OptionListWidget.class, remap = false)
public abstract class OptionListWidgetMixin extends YACLSelectionList<OptionListWidget.Entry> {
    @Shadow @Final private ConfigCategory category;

    @Shadow
    @Final
    private YACLScreen yaclScreen;

    public OptionListWidgetMixin(Minecraft minecraft, int width, int height, int y) {
        super(minecraft, width, height, y);
    }

    @WrapOperation(method = "refreshOptions", at = @At(value = "INVOKE", target = "Ldev/isxander/yacl3/api/ConfigCategory;groups()Lcom/google/common/collect/ImmutableList;"))
    private ImmutableList<OptionGroup> groups(ConfigCategory instance, Operation<ImmutableList<OptionGroup>> original) {
        if(!category.name().equals(Component.translatable("cameratweaks.options.thirdperson"))) return original.call(instance);
        if(ThirdPerson.pending == null) ThirdPerson.pending = new ArrayList<>(Config.get().thirdPersons);
        ImmutableList.Builder<OptionGroup> builder = ImmutableList.builder();
        builder.addAll(original.call(instance));
        for (int i = 0; i < ThirdPerson.pending.size(); i++) {
            OptionGroup group = ThirdPerson.pending.get(i).toGroup(i);
            group.options().forEach(o->o.addEventListener((opt, event) -> ((Runnable) yaclScreen).run()));
            builder.add(group);
        }
        return builder.build();
    }

    @Mixin(value = OptionListWidget.GroupSeparatorEntry.class, remap = false)
    public static class GroupSeparatorEntryMixin {
        @Unique private TooltipButtonWidget removeListButton;
        @Shadow @Final protected LowProfileButtonWidget expandMinimizeButton;

        @Inject(method = "<init>", at = @At("TAIL"))
        public void init(OptionListWidget this$0, OptionGroup group, Screen screen, CallbackInfo ci) {
            if(!(group.name().getContents() instanceof TranslatableContents name)) return;
            if(name.getKey().equals("cameratweaks.options.thirdperson.custom")) removeListButton = new TooltipButtonWidget(screen, this$0.getRowRight() - 20, -50, 20, 20,
                    Component.literal("X"), Component.translatable("yacl.list.remove"), btn -> {
                ThirdPerson.pending.remove((int) name.getArgs()[0] + 1);
                this$0.refreshOptions();
                ((Runnable) screen).run();
            });
        }

        @Inject(method = "renderContent", at = @At("TAIL"), remap = true)
        public void render(GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float deltaTicks, CallbackInfo ci) {
            if(removeListButton != null) {
                removeListButton.setY(expandMinimizeButton.getY());
                removeListButton.render(graphics, mouseX, mouseY, deltaTicks);
            }
        }

        @Inject(method = "children", at = @At("HEAD"), cancellable = true, remap = true)
        public void children(CallbackInfoReturnable<List<? extends GuiEventListener>> cir) {
            if(removeListButton != null) cir.setReturnValue(ImmutableList.of(expandMinimizeButton, removeListButton));
        }
    }
}
