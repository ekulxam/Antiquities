package net.hollowed.antique.blocks.screens;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.networking.DyePacketPayload;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class DyeingScreen extends HandledScreen<DyeingScreenHandler> implements ScreenHandlerListener {
	private static final Identifier TEXTURE = Antiquities.id("textures/gui/container/dye_table.png");
	private TextFieldWidget colorField;

	public DyeingScreen(DyeingScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Override
	protected void init() {
		super.init();
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		assert this.client != null;
		this.colorField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, i + 63, j + 65, 58, 9, Text.translatable("container.antique.hex"));
		this.colorField.setFocusUnlocked(false);
		this.colorField.setEditableColor(0xFFFFF4BC);
		this.colorField.setUneditableColor(0xFFFFF4BC);
		this.colorField.setDrawsBackground(false);
		this.colorField.setMaxLength(6);
		this.colorField.setChangedListener(this::onColorChanged);
		this.colorField.setText("");
		this.addDrawableChild(this.colorField);
		this.colorField.setEditable(this.handler.getSlot(0).hasStack());
		this.handler.addListener(this);
	}

	private void onColorChanged(String color) {
		Slot slot = this.handler.getSlot(0);
		if (slot.hasStack()) {
            if (this.handler.setHexCode(color)) {
                assert this.client != null;
                assert this.client.player != null;
				ClientPlayNetworking.send(new DyePacketPayload(color));
			}
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		super.render(context, mouseX, mouseY, deltaTicks);
		this.drawMouseoverTooltip(context, mouseX, mouseY);
	}

	@Override
	protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
		context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 0xff6b5e3c, false);
	}

	@Override
	public void removed() {
		super.removed();
		this.handler.removeListener(this);
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String string = this.colorField.getText();
		this.init(client, width, height);
		this.colorField.setText(string);
	}

	@Override
	protected void setInitialFocus() {
		super.setInitialFocus(this.colorField);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            assert this.client != null;
            assert this.client.player != null;
            this.client.player.closeHandledScreen();
		}
		return this.colorField.keyPressed(keyCode, scanCode, modifiers) || this.colorField.isActive() || super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
		int i = this.x;
		int j = this.y;
		context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);

		if (this.colorField.getText() != null && this.colorField.getText().length() == 6) {
			int intValue = 0;
			try {
				intValue = 0xFF000000 | Integer.parseInt(this.colorField.getText(), 16);
			} catch (NumberFormatException e) {
				System.err.println("Invalid hexadecimal string format: " + e.getMessage());
			}
			context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i + 1, j + 3, 0.0F, 176.0F, 174, 80, 256, 256, intValue);
		}
	}

	@Override
	public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
		if (slotId == 0) {
			this.colorField.setText("");
			this.colorField.setEditable(!stack.isEmpty());
			this.setFocused(this.colorField);
		}
	}

	@Override
	public void onPropertyUpdate(ScreenHandler handler, int property, int value) {

	}
}
