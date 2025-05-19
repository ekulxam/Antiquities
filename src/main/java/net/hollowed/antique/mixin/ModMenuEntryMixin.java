package net.hollowed.antique.mixin;

import com.terraformersmc.modmenu.gui.ModsScreen;
import com.terraformersmc.modmenu.gui.widget.entries.ModListEntry;
import com.terraformersmc.modmenu.util.mod.Mod;
import net.hollowed.antique.Antiquities;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModsScreen.class)
public abstract class ModMenuEntryMixin extends Screen {

    @Shadow private ModListEntry selected;

    @Shadow private int rightPaneX;

    protected ModMenuEntryMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(DrawContext DrawContext, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ModListEntry selectedEntry = this.selected;
        if (selectedEntry != null) {
            Mod mod = selectedEntry.getMod();
            int imageOffset = 36;
            Text name = Text.literal(mod.getTranslatedName());
            StringVisitable trimmedName = name;
            int maxNameWidth = this.width - (this.rightPaneX + imageOffset);
            if (this.textRenderer.getWidth(name) > maxNameWidth) {
                StringVisitable ellipsis = StringVisitable.plain("...");
                trimmedName = StringVisitable.concat(this.textRenderer.trimToWidth(name, maxNameWidth - this.textRenderer.getWidth(ellipsis)), ellipsis);
            }

            // Custom color logic
            int nameColor = 0xaa2f54;

            if ("antique".equals(mod.getId())) {
                DrawContext.drawText(this.textRenderer, Language.getInstance().reorder(trimmedName), this.rightPaneX + imageOffset, 49, nameColor, true);
                DrawContext.drawTexture(RenderLayer::getGuiTexturedOverlay, Identifier.of(Antiquities.MOD_ID, "antiquities_small_icon.png"), this.rightPaneX + imageOffset + 50, 45, 0, 0, 16, 16, 16, 16);
            }
        }
    }
}
