package net.hollowed.antique.client.gui;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.ModKeyBindings;
import net.hollowed.antique.component.ModComponents;
import net.hollowed.antique.component.SatchelInventoryComponent;
import net.hollowed.antique.items.ModItems;
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
        SatchelInventoryComponent component = satchel.get(ModComponents.SATCHEL_INVENTORY);

        int x = context.getScaledWindowWidth() / 2;
        int y = (context.getScaledWindowHeight() / 2) + 10;

        int maxRows = 2;
        int maxCols = 4;

        for (int row = 0; row < maxRows; row++) {
            for (int col = 0; col < maxCols; col++) {

                context.drawTexture(
                        RenderLayer::getGuiTextured,
                        SATCHEL_SELECTORS,
                        (x - 43) + (22 * col), y + (22 * row),
                        0, 0,
                        20, 20,
                        64, 64
                );

                if (component != null) {
                ItemStack stack = component.getStack(row == 1 ? col + 4 : col);
                if (stack != null) {
                    context.drawItem(stack, (x - 41) + (22 * col), y + (22 * row) + 2);
                    context.drawStackOverlay(textRenderer, stack, (x - 41) + (22 * col), y + (22 * row) + 2);
                }
}
            }
        }

        if (component != null) {
            context.drawTexture(
                    RenderLayer::getGuiTextured,
                    SATCHEL_SELECTORS,
                    (x - 45) + (22 * (component.getSelectedStack() <= 3 ? component.getSelectedStack() : (component.getSelectedStack() - 4))), (y - 2) + (22 * (component.getSelectedStack() > 3 ? 1 : 0)),
                    20, 0,
                    24, 24,
                    64, 64
            );


            ItemStack stack = component.getStack(component.getSelectedStack());
            if (stack != null && stack != ItemStack.EMPTY) {
                List<Text> textTooltip = stack.getTooltip(Item.TooltipContext.create(client.world), player, TooltipType.BASIC);
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
        }
    }
}
