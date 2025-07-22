package net.hollowed.antique.mixin.screens;

import net.hollowed.antique.index.AntiqueKeyBindings;
import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.items.SatchelItem;
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
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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
    private static final Identifier HOTBAR_SLOT = Identifier.ofVanilla("textures/gui/sprites/hud/hotbar_offhand_left.png");
    @Unique
    private static final Identifier HOTBAR_SELECTORS = Identifier.ofVanilla("textures/gui/sprites/hud/hotbar_selection.png");

    @Inject(method = "render", at = @At("HEAD"))
    public void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        PlayerEntity player = client.player;
        assert player != null;

        ItemStack satchel = player.getEquippedStack(EquipmentSlot.LEGS);
        if (satchel.getItem() != AntiqueItems.SATCHEL || !AntiqueKeyBindings.showSatchel.isPressed()) return;

        int x = (context.getScaledWindowWidth() / 2);
        int y = (context.getScaledWindowHeight() / 2) + 7;

        if (satchel.getItem() instanceof SatchelItem satchelItem) {
            List<ItemStack> storedStacks = SatchelItem.getStoredStacks(satchel);
            List<ItemStack> allStacks = new ArrayList<>(storedStacks);

            if (storedStacks.isEmpty()) {
                allStacks.add(ItemStack.EMPTY);
            }

            while (allStacks.size() < 8) {
                allStacks.add(ItemStack.EMPTY);
            }

            int maxRows = 2;
            int maxCols = 4;

            for (int row = 0; row < maxRows; row++) {
                for (int col = 0; col < maxCols; col++) {
                    int index = row * 4 + col;

                    ItemStack stack = allStacks.get(index);
                    int slotX = (x - 44) + (22 * col);
                    int slotY = y - 1 + (22 * row);

                    context.drawTexture(
                            RenderPipelines.GUI_TEXTURED,
                            HOTBAR_SLOT,
                            slotX, slotY,
                            0, 1,
                            22, 22,
                            29, 24
                    );
                     if (row == 0 && col < 3) {
                         for (int inRow = 0; inRow < 4; inRow++) {
                             for (int inCol = 0; inCol < 4; inCol++) {
                                 context.drawTexture(
                                         RenderPipelines.GUI_TEXTURED,
                                         HOTBAR_SLOT,
                                         slotX + 20 + inRow, slotY + 20 + inCol,
                                         2, 1,
                                         1, 1,
                                         29, 24
                                 );
                             }
                         }
                     }

                    if (!stack.isEmpty()) {
                        context.drawItem(stack, slotX + 3, slotY + 3);
                        context.drawStackOverlay(textRenderer, stack, slotX + 2, slotY + 2);
                    }
                }
            }

            ItemStack selectedStack = allStacks.get(satchelItem.getIndex());
            if (selectedStack != null && !selectedStack.isEmpty()) {
                Text text = selectedStack.getFormattedName();
                int i = textRenderer.getWidth(text.asOrderedText());
                TooltipComponent tooltipComponent = TooltipComponent.of(text.asOrderedText());
                context.drawTooltipImmediately(textRenderer, List.of(tooltipComponent), x - 12 - i /2, y - 15, HoveredTooltipPositioner.INSTANCE, selectedStack.get(DataComponentTypes.TOOLTIP_STYLE));

            }

            int selectorX = (x - 45) + (22 * (satchelItem.getIndex() <= 3 ? satchelItem.getIndex() : (satchelItem.getIndex() - 4)));
            int selectorY = (y - 2) + (22 * (satchelItem.getIndex() > 3 ? 1 : 0));

            context.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    HOTBAR_SELECTORS,
                    selectorX, selectorY,
                    0, 0,
                    24, 23,
                    24, 23
            );
            context.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    HOTBAR_SELECTORS,
                    selectorX, selectorY + 23,
                    0, 0,
                    24, 1,
                    24, 23
            );
        }
    }
}
