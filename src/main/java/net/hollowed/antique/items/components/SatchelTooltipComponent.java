package net.hollowed.antique.items.components;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.antique.index.AntiqueComponents;
import net.hollowed.antique.items.SatchelItem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class SatchelTooltipComponent implements TooltipComponent {
	private static final Identifier BUNDLE_PROGRESS_BAR_BORDER_TEXTURE = Identifier.ofVanilla("container/bundle/bundle_progressbar_border");
	private static final Identifier BUNDLE_PROGRESS_BAR_FILL_TEXTURE = Identifier.ofVanilla("container/bundle/bundle_progressbar_fill");
	private static final Identifier BUNDLE_PROGRESS_BAR_FULL_TEXTURE = Identifier.ofVanilla("container/bundle/bundle_progressbar_full");
	private static final Identifier BUNDLE_SLOT_HIGHLIGHT_BACK_TEXTURE = Identifier.ofVanilla("container/bundle/slot_highlight_back");
	private static final Identifier BUNDLE_SLOT_HIGHLIGHT_FRONT_TEXTURE = Identifier.ofVanilla("container/bundle/slot_highlight_front");
	private static final Identifier BUNDLE_SLOT_BACKGROUND_TEXTURE = Identifier.ofVanilla("container/bundle/slot_background");
	private static final Text BUNDLE_FULL = Text.translatable("item.minecraft.bundle.full");
	private static final Text BUNDLE_EMPTY = Text.translatable("item.minecraft.bundle.empty");
	private static final Text BUNDLE_EMPTY_DESCRIPTION = Text.translatable("item.antique.satchel.empty.description");
	private final List<ItemStack> satchelContents;
	private final ItemStack stack;

	public SatchelTooltipComponent(List<ItemStack> bundleContents, ItemStack stack) {
		this.satchelContents = bundleContents;
		this.stack = stack;
	}

	@Override
	public int getHeight(TextRenderer textRenderer) {
		return this.satchelContents.isEmpty() ? getHeightOfEmpty(textRenderer) : this.getHeightOfNonEmpty();
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return 128;
	}

	@Override
	public boolean isSticky() {
		return true;
	}

	private static int getHeightOfEmpty(TextRenderer textRenderer) {
		return getDescriptionHeight(textRenderer) + 13 + 8;
	}

	private int getHeightOfNonEmpty() {
		return this.getRowsHeight() + 13 + 8;
	}

	private int getRowsHeight() {
		return this.getRows() * 24;
	}

	private int getXMargin(int width) {
		return (width - 96) / 2;
	}

	private int getRows() {
		return 2;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {
		x -= 22;
		if (this.satchelContents != SatchelItem.lastContents) {
			SatchelItem.lastContents = this.satchelContents;
			SatchelItem.setInternalIndex(this.stack, -1);
		}
		this.stack.set(AntiqueComponents.COUNTER, 0);
		if (this.satchelContents.isEmpty()) {
			this.drawEmptyTooltip(textRenderer, x, y, width, context);
		} else {
			this.drawNonEmptyTooltip(textRenderer, x, y, width, context);
		}
	}

	private void drawEmptyTooltip(TextRenderer textRenderer, int x, int y, int width, DrawContext context) {
		drawEmptyDescription(x + this.getXMargin(width), y, textRenderer, context);
		this.drawProgressBar(x + this.getXMargin(width), y + getDescriptionHeight(textRenderer) + 4, textRenderer, context);
	}

	private void drawNonEmptyTooltip(TextRenderer textRenderer, int x, int y, int width, DrawContext context) {
		List<ItemStack> list = this.firstStacksInContents();
		int i = x + this.getXMargin(width);
		int k = 0;

		for (int l = 0; l < this.getRows(); l++) {
			for (int m = 0; m < 4; m++) {
				int n = i + m * 24;
				int o = y + l * 24;
				if (shouldDrawItem(list, k)) {
					this.drawItem(k, n, o, list, k, textRenderer, context);
					k++;
				}
			}
		}

		this.drawSelectedItemTooltip(textRenderer, context, x + 22, y, width);
		this.drawProgressBar(x + this.getXMargin(width), y + this.getRowsHeight() + 4, textRenderer, context);
	}

	private List<ItemStack> firstStacksInContents() {
		int i = Math.min(this.satchelContents.size(), 8);
		return this.satchelContents.stream().toList().subList(0, i);
	}

	private static boolean shouldDrawItem(List<ItemStack> items, int itemIndex) {
		return items.size() > itemIndex;
	}

	private void drawItem(int index, int x, int y, List<ItemStack> stacks, int seed, TextRenderer textRenderer, DrawContext drawContext) {
		boolean bl = index == this.selectedIndex();
		ItemStack itemStack = stacks.get(index);
		if (bl) {
			drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BUNDLE_SLOT_HIGHLIGHT_BACK_TEXTURE, x, y, 24, 24);
		} else {
			drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BUNDLE_SLOT_BACKGROUND_TEXTURE, x, y, 24, 24);
		}

		drawContext.drawItem(itemStack, x + 4, y + 4, seed);
		drawContext.drawStackOverlay(textRenderer, itemStack, x + 4, y + 4);
		if (bl) {
			drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BUNDLE_SLOT_HIGHLIGHT_FRONT_TEXTURE, x, y, 24, 24);
		}
	}

	private void drawSelectedItemTooltip(TextRenderer textRenderer, DrawContext drawContext, int x, int y, int width) {
		if (!this.satchelContents.isEmpty() && this.selectedIndex() < this.satchelContents.size() && this.selectedIndex() != -1) {
			ItemStack itemStack = this.satchelContents.get(this.selectedIndex());
			Text text = itemStack.getFormattedName();
			int i = textRenderer.getWidth(text.asOrderedText());
			int j = x + width / 2 - 12;
			TooltipComponent tooltipComponent = TooltipComponent.of(text.asOrderedText());
			drawContext.drawTooltipImmediately(
				textRenderer, List.of(tooltipComponent), j - i / 2, y - 15, HoveredTooltipPositioner.INSTANCE, itemStack.get(DataComponentTypes.TOOLTIP_STYLE)
			);
		}
	}

	private void drawProgressBar(int x, int y, TextRenderer textRenderer, DrawContext drawContext) {
		drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.getProgressBarFillTexture(), x + 1, y, this.getProgressBarFill(), 13);
		drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BUNDLE_PROGRESS_BAR_BORDER_TEXTURE, x, y, 128, 13);
		Text text = this.getProgressBarLabel();
		if (text != null) {
			drawContext.drawCenteredTextWithShadow(textRenderer, text, x + 64, y + 3, Colors.WHITE);
		}
	}

	private static void drawEmptyDescription(int x, int y, TextRenderer textRenderer, DrawContext drawContext) {
		drawContext.drawWrappedTextWithShadow(textRenderer, BUNDLE_EMPTY_DESCRIPTION, x, y, 128, -5592406);
	}

	private static int getDescriptionHeight(TextRenderer textRenderer) {
		return textRenderer.wrapLines(BUNDLE_EMPTY_DESCRIPTION, 128).size() * 9;
	}

	private int getProgressBarFill() {
		return MathHelper.clamp(this.satchelContents.size() * 16, 0, 126);
	}

	private Identifier getProgressBarFillTexture() {
		return this.satchelContents.size() == 8 ? BUNDLE_PROGRESS_BAR_FULL_TEXTURE : BUNDLE_PROGRESS_BAR_FILL_TEXTURE;
	}

	private int selectedIndex() {
		return SatchelItem.getInternalIndex(this.stack);
	}

	@Nullable
	private Text getProgressBarLabel() {
		if (this.satchelContents.isEmpty()) {
			return BUNDLE_EMPTY;
		} else {
			return this.satchelContents.size() == 8 ? BUNDLE_FULL : null;
		}
	}
}
