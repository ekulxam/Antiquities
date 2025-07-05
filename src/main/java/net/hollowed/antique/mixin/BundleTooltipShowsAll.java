package net.hollowed.antique.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.BundleTooltipData;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BundleTooltipComponent.class)
public abstract class BundleTooltipShowsAll {
    @Unique
    private static final int slotSize = 13;
    @Unique
    private static final int columns = 8;

    @Mutable
    @Final
    @Shadow
    private final BundleContentsComponent bundleContents;

    protected BundleTooltipShowsAll(BundleContentsComponent bundleContents) {
        this.bundleContents = bundleContents;
    }

    @Shadow
    private List<ItemStack> firstStacksInContents(int numberOfStacksShown) {
        return null;
    }

    @Shadow
    private int getXMargin(int i) {
        return 0;
    }

    @Shadow
    private void drawSelectedItemTooltip(TextRenderer textRenderer, DrawContext drawContext, int x, int y, int width) {

    }

    @Shadow
    private void drawProgressBar(int x, int y, TextRenderer textRenderer, DrawContext drawContext) {

    }

    @Shadow
    private int getRows() {
        return 0;
    }

    @Shadow
    private int getRowsHeight() {
        return 0;
    }

    @Shadow
    private void drawItem(int index, int x, int y, List<ItemStack> stacks, int seed, TextRenderer textRenderer, DrawContext drawContext) {

    }

    @Shadow @Final private static Identifier BUNDLE_SLOT_HIGHLIGHT_BACK_TEXTURE;

    @Shadow @Final private static Identifier BUNDLE_SLOT_BACKGROUND_TEXTURE;

    @Shadow @Final private static Identifier BUNDLE_SLOT_HIGHLIGHT_FRONT_TEXTURE;

    @Inject(method = "getNumVisibleSlots", at = @At("HEAD"), cancellable = true)
    private void modifySlotCount(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(this.bundleContents.size());
    }

    @ModifyConstant(method = {"getRowsHeight", "drawItem"}, constant = @Constant(intValue = 24))
    private int slotHeight(int original) {
        return BundleTooltipShowsAll.slotSize + 4;
    }

    @ModifyConstant(method = "drawItem", constant = @Constant(intValue = 4))
    private int modifyPadding(int original) {
        return 0;
    }

    @ModifyConstant(method = "getRows", constant = @Constant(intValue = 4))
    private int numColumns(int original) {
        return BundleTooltipShowsAll.columns;
    }

    @ModifyConstant(method = {"getWidth", "getXMargin", "drawProgressBar"}, constant = @Constant(intValue = 96))
    private int tooltipWidth(int original) {
        return (BundleTooltipShowsAll.slotSize + 4) * BundleTooltipShowsAll.columns;
    }

    @ModifyConstant(method = "getProgressBarFill", constant = @Constant(intValue = 94))
    private int barProgress(int original) {
        // Calculate based on exact tooltip width
        int totalWidth = (BundleTooltipShowsAll.columns * (BundleTooltipShowsAll.slotSize + 4));
        return totalWidth - 2; // Subtract a small margin if needed
    }


    @ModifyConstant(method = "drawProgressBar", constant = @Constant(intValue = 48))
    private int fillText(int original) {
        int totalWidth = (BundleTooltipShowsAll.columns * (BundleTooltipShowsAll.slotSize + 4));
        return totalWidth / 2; // Subtract a small margin if needed
    }

    @Inject(method = "drawNonEmptyTooltip", at = @At("HEAD"), cancellable = true)
    private void drawNonEmptyTooltip(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context, CallbackInfo ci) {
        List<ItemStack> list = this.firstStacksInContents(this.bundleContents.getNumberOfStacksShown());
        int o = 1;

        for (int p = 0; p < this.getRows(); ++p) {
            for (int q = 0; q < BundleTooltipShowsAll.columns; ++q) {
                assert list != null;
                if (o > list.size()) {
                    break;
                }
                // Adjust render position by 3 pixels to the right and down
                int r = x + q * (BundleTooltipShowsAll.slotSize + 4); // Add 3 pixels horizontally
                int s = y + p * (BundleTooltipShowsAll.slotSize + 4); // Add 3 pixels vertically
                this.drawItem(o, r, s, list, o, textRenderer, context);
                ++o;
            }
        }

        this.drawSelectedItemTooltip(textRenderer, context, x, y, width);
        this.drawProgressBar(x + this.getXMargin(width), y + this.getRowsHeight() + 4, textRenderer, context);

        ci.cancel();
    }

    @Inject(method = "drawItem", at = @At("HEAD"), cancellable = true)
    private void adjustBackgroundTexture(
            int index, int x, int y, List<ItemStack> stacks, int seed, TextRenderer textRenderer, DrawContext drawContext, CallbackInfo ci) {
        // Adjust the background texture position
        int adjustedX = x - 3;
        int adjustedY = y - 3;

        int i = stacks.size() - index;
        boolean bl = i == this.bundleContents.getSelectedStackIndex(); // Assuming access to bundleContents is handled

        ItemStack itemStack = stacks.get(i);
        // Modify the drawing of the background textures
        if (bl) {
            drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BUNDLE_SLOT_HIGHLIGHT_BACK_TEXTURE, adjustedX, adjustedY, 20, 20);
        } else {
            drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BUNDLE_SLOT_BACKGROUND_TEXTURE, adjustedX, adjustedY, 20, 20);
        }

        drawContext.drawItem(itemStack, x - 1, y - 1, seed);
        drawContext.drawStackOverlay(textRenderer, itemStack, x -1, y -1);
        if (bl) {
            drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BUNDLE_SLOT_HIGHLIGHT_FRONT_TEXTURE, adjustedX, adjustedY, 20, 20);
        }

        ci.cancel(); // Prevents the original background render
    }
}
