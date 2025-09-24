package net.hollowed.antique.mixin.items.renderers;

import net.hollowed.antique.index.AntiqueDataComponentTypes;
import net.hollowed.antique.index.AntiqueItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.ItemGuiElementRenderState;
import net.minecraft.client.render.item.KeyedItemRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.World;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class ItemRendererMixin {

    @Shadow @Final private MinecraftClient client;

    @Shadow @Final public GuiRenderState state;

    @Shadow @Final public DrawContext.ScissorStack scissorStack;

    @Shadow @Final private Matrix3x2fStack matrices;

    @Inject(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;III)V", at = @At("TAIL"))
    public void renderItem(LivingEntity entity, World world, ItemStack stack, int x, int y, int seed, CallbackInfo ci) {
        if (stack.isOf(AntiqueItems.MYRIAD_STAFF)) {
            matrices.pushMatrix();
            this.matrices.translate((float) (x + 10), (float) (y + 6));
            this.matrices.scale(5.0F, -5.0F);

            ItemStack stackToRender = stack.getOrDefault(AntiqueDataComponentTypes.MYRIAD_STACK, ItemStack.EMPTY);

            KeyedItemRenderState itemRenderState = new KeyedItemRenderState();
            this.client.getItemModelManager().clearAndUpdate(itemRenderState, stack, ItemDisplayContext.GUI, world, entity, seed);

            try {
                this.state.addItem(new ItemGuiElementRenderState(stackToRender.getItem().getName().toString(), new Matrix3x2f(this.matrices), itemRenderState, x, y, this.scissorStack.peekLast()));
            } catch (Throwable var11) {
                CrashReport crashReport = CrashReport.create(var11, "Rendering item");
                CrashReportSection crashReportSection = crashReport.addElement("Item being rendered");
                crashReportSection.add("Item Type", () -> String.valueOf(stack.getItem()));
                crashReportSection.add("Item Components", () -> String.valueOf(stack.getComponents()));
                crashReportSection.add("Item Foil", () -> String.valueOf(stack.hasGlint()));
                throw new CrashException(crashReport);
            }

            matrices.popMatrix();
        }
    }
}
