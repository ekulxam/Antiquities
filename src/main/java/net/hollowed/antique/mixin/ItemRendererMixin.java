package net.hollowed.antique.mixin;

import net.hollowed.antique.component.ModComponents;
import net.hollowed.antique.items.ModItems;
import net.hollowed.antique.util.MyriadStaffTransformData;
import net.hollowed.antique.util.MyriadStaffTransformResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.ItemGuiElementRenderState;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.KeyedItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.RotationAxis;
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
        if (stack.isOf(ModItems.MYRIAD_STAFF)) {
            matrices.pushMatrix();
            this.matrices.translate((float) (x + 10), (float) (y + 6));
            this.matrices.scale(5.0F, -5.0F);
//            this.matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-45));
//            this.matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));

            ItemStack stackToRender = stack.getOrDefault(ModComponents.MYRIAD_STACK, ItemStack.EMPTY);

            KeyedItemRenderState itemRenderState = new KeyedItemRenderState();
            this.client.getItemModelManager().clearAndUpdate(itemRenderState, stack, ItemDisplayContext.GUI, world, entity, seed);

            try {
                this.state.addItem(new ItemGuiElementRenderState(stackToRender.getItem().getName().toString(), new Matrix3x2f(this.matrices), itemRenderState, x, y, this.scissorStack.peekLast()));
            } catch (Throwable var11) {
                Throwable throwable = var11;
                CrashReport crashReport = CrashReport.create(throwable, "Rendering item");
                CrashReportSection crashReportSection = crashReport.addElement("Item being rendered");
                crashReportSection.add("Item Type", () -> String.valueOf(stack.getItem()));
                crashReportSection.add("Item Components", () -> String.valueOf(stack.getComponents()));
                crashReportSection.add("Item Foil", () -> String.valueOf(stack.hasGlint()));
                throw new CrashException(crashReport);
            }

//            matrices.scale(0.875F, 0.875F, 0.875F);
//            matrices.translate(0.0, -0.035, 0.05);
//
//            MyriadStaffTransformData data = MyriadStaffTransformResourceReloadListener.getTransform(Registries.ITEM.getId(stackToRender.getItem()));
//            matrices.scale(data.scale().get(0), data.scale().get(1), data.scale().get(2));
//            matrices.translate(data.translation().get(0), data.translation().get(1), data.translation().get(2));
//            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(data.rotation().getFirst()));
//            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(data.rotation().get(1)));
//            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(data.rotation().get(2)));
//
//            Identifier customModel = stackToRender.getOrDefault(DataComponentTypes.ITEM_MODEL, Registries.ITEM.getId(stackToRender.getItem()));
//            if (!data.model().equals(Identifier.of("default"))) {
//                stackToRender.set(DataComponentTypes.ITEM_MODEL, data.model());
//            }

//            MinecraftClient.getInstance().getItemRenderer().renderItem(
//                    stackToRender,
//                    ItemDisplayContext.NONE,
//                    15728880,
//                    OverlayTexture.DEFAULT_UV,
//                    matrices,
//                    this.vertexConsumers,
//                    MinecraftClient.getInstance().world,
//                    seed
//            );
//
//            stackToRender.set(DataComponentTypes.ITEM_MODEL, customModel);
//
//            this.draw();
            matrices.popMatrix();
        }
    }
}
