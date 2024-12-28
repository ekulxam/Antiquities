package net.hollowed.antique.client.gui;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.ModKeyBindings;
import net.hollowed.antique.items.ModItems;
import net.hollowed.antique.items.custom.SatchelItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class SatchelOverlay implements HudRenderCallback {

    private static final Identifier SATCHEL_SELECTORS = Antiquities.id("textures/gui/satchel_selectors.png");

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter counter) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        PlayerEntity player = client.player;
        assert player != null;

        ItemStack satchel = player.getEquippedStack(EquipmentSlot.LEGS);
        if (satchel.getItem() != ModItems.SATCHEL || !ModKeyBindings.showSatchel.isPressed()) return;

        int x = context.getScaledWindowWidth() / 2;
        int y = (context.getScaledWindowHeight() / 2) + 10;

        if (satchel.getItem() instanceof SatchelItem satchelItem) {
            // Get the list of stored stacks and ensure it has a length of 8
            List<ItemStack> storedStacks = satchelItem.getStoredStacks(satchel);
            List<ItemStack> allStacks = new ArrayList<>(storedStacks);

            // If the stored stacks are fewer than 8, fill the remaining slots with empty ItemStacks
            while (allStacks.size() < 8) {
                allStacks.add(ItemStack.EMPTY);
            }

            int maxRows = 2; // Always use 2 rows (4 columns each)
            int maxCols = 4;

            // Loop through all 8 possible slots (2 rows x 4 columns)
            for (int row = 0; row < maxRows; row++) {
                for (int col = 0; col < maxCols; col++) {
                    int index = row * 4 + col; // Calculate the correct index for the list

                    ItemStack stack = allStacks.get(index); // Get the stack (could be empty)
                    if (!stack.isEmpty()) {
                        context.drawTexture(
                                RenderLayer::getGuiTextured,
                                SATCHEL_SELECTORS,
                                (x - 43) + (22 * col), y + (22 * row),
                                0, 0,
                                20, 20,
                                64, 64
                        );
                        context.drawItem(stack, (x - 41) + (22 * col), y + (22 * row) + 2);
                        context.drawStackOverlay(textRenderer, stack, (x - 41) + (22 * col), y + (22 * row) + 2);
                    } else {
                        // Render the empty slot (no item)
                        context.drawTexture(
                                RenderLayer::getGuiTextured,
                                SATCHEL_SELECTORS,
                                (x - 43) + (22 * col), y + (22 * row),
                                0, 0,
                                20, 20,
                                64, 64
                        );
                    }
                }
            }

            // Display the selected stack's tooltip (same logic as before)
            ItemStack selectedStack = allStacks.get(satchelItem.getIndex());
            if (selectedStack != null && !selectedStack.isEmpty()) {
                List<Text> textTooltip = selectedStack.getTooltip(Item.TooltipContext.create(client.world), player, TooltipType.BASIC);
                List<OrderedText> orderedTooltip = new ArrayList<>();
                for (Text text : textTooltip) {
                    orderedTooltip.add(text.asOrderedText());
                }
                context.drawTooltip(
                        textRenderer,
                        orderedTooltip,
                        (screenWidth, screenHeight, tipX, tipY, width, height) -> new Vector2i(x - (width / 2), y - (height) - 20),
                        x, y
                );
            }

            // Draw the selected stack's selector
            context.drawTexture(
                    RenderLayer::getGuiTextured,
                    SATCHEL_SELECTORS,
                    (x - 45) + (22 * (satchelItem.getIndex() <= 3 ? satchelItem.getIndex() : (satchelItem.getIndex() - 4))),
                    (y - 2) + (22 * (satchelItem.getIndex() > 3 ? 1 : 0)),
                    20, 0,
                    24, 24,
                    64, 64
            );
        }
    }
}