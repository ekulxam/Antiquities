package net.hollowed.antique.mixin;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.ModKeyBindings;
import net.hollowed.antique.items.ModItems;
import net.hollowed.antique.items.custom.SatchelItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(InGameHud.class)
public class SatchelOverlayMixin {

    @Unique
    private static final Identifier SATCHEL_SELECTORS = Antiquities.id("textures/gui/satchel_selectors.png");

    @Inject(method = "render", at = @At("HEAD"))
    public void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        PlayerEntity player = client.player;
        assert player != null;

        ItemStack satchel = player.getEquippedStack(EquipmentSlot.LEGS);
        if (satchel.getItem() != ModItems.SATCHEL || !ModKeyBindings.showSatchel.isPressed()) return;

        int x = (context.getScaledWindowWidth() / 2);
        int y = (context.getScaledWindowHeight() / 2) + 7;

        if (satchel.getItem() instanceof SatchelItem satchelItem) {
            // Get the list of stored stacks and ensure it has a length of 8
            List<ItemStack> storedStacks = SatchelItem.getStoredStacks(satchel);
            List<ItemStack> allStacks = new ArrayList<>(storedStacks);

            if (storedStacks.isEmpty()) {
                allStacks.add(ItemStack.EMPTY);
            }

            // If the stored stacks are fewer than 8, fill the remaining slots with empty ItemStacks
            while (allStacks.size() < 8) {
                allStacks.add(ItemStack.EMPTY);
            }

            int maxRows = 2; // Always use 2 rows (4 columns each)
            int maxCols = 4;

            // Render slots and items
            for (int row = 0; row < maxRows; row++) {
                for (int col = 0; col < maxCols; col++) {
                    int index = row * 4 + col; // Calculate the correct index for the list

                    ItemStack stack = allStacks.get(index); // Get the stack (could be empty)
                    int slotX = (x - 43) + (22 * col);
                    int slotY = y + (22 * row);

                    // Render the slot background
                    context.drawTexture(
                            RenderPipelines.GUI_TEXTURED,
                            SATCHEL_SELECTORS,
                            slotX, slotY,
                            0, 0,
                            20, 20,
                            64, 64
                    );

                    // Render the item stack and overlay
                    if (!stack.isEmpty()) {
                        context.drawItem(stack, slotX + 2, slotY + 2);
                        context.drawStackOverlay(textRenderer, stack, slotX + 2, slotY + 2);
                    }
                }
            }

            // Display the tooltip for the selected stack
            ItemStack selectedStack = allStacks.get(satchelItem.getIndex());
            if (selectedStack != null && !selectedStack.isEmpty()) {
                Text text = selectedStack.getFormattedName();
                int i = textRenderer.getWidth(text.asOrderedText());
                TooltipComponent tooltipComponent = TooltipComponent.of(text.asOrderedText());
                context.drawTooltipImmediately(textRenderer, List.of(tooltipComponent), x - 12 - i /2, y - 15, HoveredTooltipPositioner.INSTANCE, selectedStack.get(DataComponentTypes.TOOLTIP_STYLE));

            }

            // Draw the selected stack's selector at the end to render it on top
            int selectorX = (x - 45) + (22 * (satchelItem.getIndex() <= 3 ? satchelItem.getIndex() : (satchelItem.getIndex() - 4)));
            int selectorY = (y - 2) + (22 * (satchelItem.getIndex() > 3 ? 1 : 0));

            context.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    SATCHEL_SELECTORS,
                    selectorX, selectorY,
                    20, 0,
                    24, 24,
                    64, 64
            );
        }
    }
}
