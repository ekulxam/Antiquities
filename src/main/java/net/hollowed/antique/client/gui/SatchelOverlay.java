package net.hollowed.antique.client.gui;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.ModKeyBindings;
import net.hollowed.antique.items.ModItems;
import net.hollowed.antique.items.custom.SatchelItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
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

    public static final Identifier SATCHEL_SELECTORS = Antiquities.id("textures/gui/satchel_selectors.png");

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter counter) {

    }
}