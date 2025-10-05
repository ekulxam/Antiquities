package net.hollowed.antique.mixin.screens;

import com.terraformersmc.modmenu.config.ModMenuConfig;
import com.terraformersmc.modmenu.gui.widget.entries.ModListEntry;
import com.terraformersmc.modmenu.util.mod.Mod;
import net.hollowed.antique.Antiquities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModListEntry.class)
public abstract class ModMenuMixin extends AlwaysSelectedEntryListWidget.Entry<ModListEntry> {

    @Shadow @Final public Mod mod;

    @Shadow @Final protected MinecraftClient client;

    @Shadow public abstract int getXOffset();

    @Shadow public abstract int getYOffset();

    @Inject(
            method = "render",
            at = @At("TAIL")
    )
    private void modifyModNameColor(
            DrawContext drawContext, int mouseX, int mouseY, boolean hovered, float delta, CallbackInfo ci
    ) {
        String modId = this.mod.getId();
        int iconSize = ModMenuConfig.COMPACT_LIST.getValue() ? 19 : 32;

        int x = this.getX() + this.getXOffset();
        int y = this.getContentY() + this.getYOffset();
        int rowWidth = this.getContentWidth();

        // Custom color logic
        int nameColor = 0xFFAA2F54;

        Text name = Text.literal(this.mod.getTranslatedName());
        StringVisitable trimmedName = name;
        int maxNameWidth = rowWidth - iconSize - 3;
        TextRenderer font = this.client.textRenderer;
        if (font.getWidth(name) > maxNameWidth) {
            StringVisitable ellipsis = StringVisitable.plain("...");
            trimmedName = StringVisitable.concat(font.trimToWidth(name, maxNameWidth - font.getWidth(ellipsis)), ellipsis);
        }

        if ("antique".equals(modId)) {
            // Modify the text rendering with a new color
            drawContext.drawTextWithShadow(
                    this.client.textRenderer,
                    Language.getInstance().reorder(trimmedName),
                    x + iconSize + 3,
                    y + 1,
                    nameColor
            );

            // Draws the small icon
            drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of(Antiquities.MOD_ID, "antiquities_small_icon.png"), x + iconSize + 53, y - 3, 0, 0, 16, 16, 16, 16);

            // Draws the colored line below the one line of text
            drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of(Antiquities.MOD_ID, "antiquities_line.png"), x + iconSize + 3, y + 22, 0, 0, 76, 1, 76, 1);

            // Draws the H signature
            drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of(Antiquities.MOD_ID, "h.png"), rowWidth - 2, y, 0, 0, 16, 16, 16, 16);
        }
    }
}
