package net.hollowed.antique.client.renderer.pedestal;

import net.hollowed.antique.blocks.PedestalBlock;
import net.hollowed.antique.blocks.entities.PedestalBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import java.util.List;

public class PedestalTooltipRenderer {

    private static void renderItemTooltip(DrawContext context, ItemStack itemStack, int screenWidth, int screenHeight) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        // Get the tooltip text
        List<Text> textList = itemStack.getTooltip(Item.TooltipContext.DEFAULT, client.player, client.options.advancedItemTooltips ? TooltipType.BASIC : TooltipType.ADVANCED);
        List<TooltipComponent> components = new java.util.ArrayList<>();

        for (Text text : textList) {
            components.add(TooltipComponent.of(text.asOrderedText()));
        }

        // Tooltip positioning (slightly to the side of the crosshair)
        int posX = screenWidth / 2 + 8;
        int posY = screenHeight / 2 - textList.size() * 10 / 2;

        // Render the tooltip
        context.drawTooltipImmediately(client.textRenderer, components, posX, posY, HoveredTooltipPositioner.INSTANCE, itemStack.get(DataComponentTypes.TOOLTIP_STYLE));
    }

    public static void renderTooltip(DrawContext context, int screenWidth, int screenHeight) {

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;

        // Check if player is looking at block
        HitResult hitResult = client.crosshairTarget;
        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) return;

        // Check if the block is a pedestal
        BlockHitResult blockHitResult = (BlockHitResult) hitResult;
        World world = client.world;
        if (world == null) return;

        if (!(world.getBlockState(blockHitResult.getBlockPos()).getBlock() instanceof PedestalBlock)) return;

        // Get the item on the pedestal
        if (world.getBlockEntity(blockHitResult.getBlockPos()) instanceof PedestalBlockEntity pedestalEntity) {
            ItemStack itemStack = pedestalEntity.getStack(0);
            if (itemStack.isEmpty()) return;

            // Render the tooltip
            renderItemTooltip(context, itemStack, screenWidth, screenHeight);
        }
    }
}
