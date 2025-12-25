package net.hollowed.antique.mixin.items;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;

@Mixin(ClientBundleTooltip.class)
public abstract class BundleTooltipShowsAll {
    @Unique
    private static final int slotSize = 13;
    @Unique
    private static final int columns = 8;

    @Mutable
    @Final
    @Shadow
    private final BundleContents contents;

    protected BundleTooltipShowsAll(BundleContents bundleContents) {
        this.contents = bundleContents;
    }

    @Shadow
    private List<ItemStack> getShownItems(int numberOfStacksShown) {
        return null;
    }

    @Shadow
    private int getContentXOffset(int i) {
        return 0;
    }

    @Shadow
    private void drawSelectedItemTooltip(Font textRenderer, GuiGraphics drawContext, int x, int y, int width) {

    }

    @Shadow
    private void drawProgressbar(int x, int y, Font textRenderer, GuiGraphics drawContext) {

    }

    @Shadow
    private int gridSizeY() {
        return 0;
    }

    @Shadow
    private int itemGridHeight() {
        return 0;
    }

    @Shadow
    private void renderSlot(int index, int x, int y, List<ItemStack> stacks, int seed, Font textRenderer, GuiGraphics drawContext) {

    }

    @Shadow @Final private static Identifier SLOT_HIGHLIGHT_BACK_SPRITE;

    @Shadow @Final private static Identifier SLOT_BACKGROUND_SPRITE;

    @Shadow @Final private static Identifier SLOT_HIGHLIGHT_FRONT_SPRITE;

    @Inject(method = "slotCount", at = @At("HEAD"), cancellable = true)
    private void modifySlotCount(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(this.contents.size());
    }

    @ModifyConstant(method = {"itemGridHeight", "renderSlot"}, constant = @Constant(intValue = 24))
    private int slotHeight(int original) {
        return BundleTooltipShowsAll.slotSize + 4;
    }

    @ModifyConstant(method = "renderSlot", constant = @Constant(intValue = 4))
    private int modifyPadding(int original) {
        return 0;
    }

    @ModifyConstant(method = "gridSizeY", constant = @Constant(intValue = 4))
    private int numColumns(int original) {
        return BundleTooltipShowsAll.columns;
    }

    @ModifyConstant(method = {"getWidth", "getContentXOffset", "drawProgressbar"}, constant = @Constant(intValue = 96))
    private int tooltipWidth(int original) {
        return (BundleTooltipShowsAll.slotSize + 4) * BundleTooltipShowsAll.columns;
    }

    @ModifyConstant(method = "getProgressBarFill", constant = @Constant(intValue = 94))
    private int barProgress(int original) {
        int totalWidth = (BundleTooltipShowsAll.columns * (BundleTooltipShowsAll.slotSize + 4));
        return totalWidth - 2;
    }


    @ModifyConstant(method = "drawProgressbar", constant = @Constant(intValue = 48))
    private int fillText(int original) {
        int totalWidth = (BundleTooltipShowsAll.columns * (BundleTooltipShowsAll.slotSize + 4));
        return totalWidth / 2;
    }

    @Inject(method = "renderBundleWithItemsTooltip", at = @At("HEAD"), cancellable = true)
    private void drawNonEmptyTooltip(Font textRenderer, int x, int y, int width, int height, GuiGraphics context, CallbackInfo ci) {
        List<ItemStack> list = this.getShownItems(this.contents.getNumberOfItemsToShow());
        int o = 1;

        for (int p = 0; p < this.gridSizeY(); ++p) {
            for (int q = 0; q < BundleTooltipShowsAll.columns; ++q) {
                if (list == null) return;
                if (o > list.size()) {
                    break;
                }
                int r = x + q * (BundleTooltipShowsAll.slotSize + 4);
                int s = y + p * (BundleTooltipShowsAll.slotSize + 4);
                this.renderSlot(o, r, s, list, o, textRenderer, context);
                ++o;
            }
        }

        this.drawSelectedItemTooltip(textRenderer, context, x, y, width);
        this.drawProgressbar(x + this.getContentXOffset(width), y + this.itemGridHeight() + 4, textRenderer, context);

        ci.cancel();
    }

    @Inject(method = "renderSlot", at = @At("HEAD"), cancellable = true)
    private void adjustBackgroundTexture(int index, int x, int y, List<ItemStack> stacks, int seed, Font textRenderer, GuiGraphics drawContext, CallbackInfo ci) {
        int adjustedX = x - 3;
        int adjustedY = y - 3;

        int i = stacks.size() - index;
        boolean bl = i == this.contents.getSelectedItem();

        ItemStack itemStack = stacks.get(i);
        if (bl) {
            drawContext.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK_SPRITE, adjustedX, adjustedY, 20, 20);
        } else {
            drawContext.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_BACKGROUND_SPRITE, adjustedX, adjustedY, 20, 20);
        }

        drawContext.renderItem(itemStack, x - 1, y - 1, seed);
        drawContext.renderItemDecorations(textRenderer, itemStack, x -1, y -1);
        if (bl) {
            drawContext.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT_SPRITE, adjustedX, adjustedY, 20, 20);
        }

        ci.cancel();
    }
}
