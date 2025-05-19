package net.hollowed.antique.entities.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.antique.Antiquities;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.FlyingItemEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class SmokeBombRenderer<T extends Entity & FlyingItemEntity> extends EntityRenderer<T, FlyingItemEntityRenderState> {
    private final ItemModelManager itemModelManager;
    private final float scale;
    private final boolean lit;

    public SmokeBombRenderer(EntityRendererFactory.Context ctx, float scale, boolean lit) {
        super(ctx);
        this.itemModelManager = ctx.getItemModelManager();
        this.scale = scale;
        this.lit = lit;
    }

    public SmokeBombRenderer(EntityRendererFactory.Context context) {
        this(context, 1.0F, false);
    }

    @Override
    protected int getBlockLight(T entity, BlockPos pos) {
        return this.lit ? 15 : super.getBlockLight(entity, pos);
    }

    public void render(FlyingItemEntityRenderState flyingItemEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.scale(this.scale, this.scale, this.scale);
        matrixStack.multiply(this.dispatcher.getRotation());
        flyingItemEntityRenderState.itemRenderState.render(matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        super.render(flyingItemEntityRenderState, matrixStack, vertexConsumerProvider, i);
    }

    public FlyingItemEntityRenderState createRenderState() {
        return new FlyingItemEntityRenderState();
    }

    public void updateRenderState(T entity, FlyingItemEntityRenderState flyingItemEntityRenderState, float f) {
        super.updateRenderState(entity, flyingItemEntityRenderState, f);
        ItemStack stack = Items.SNOWBALL.getDefaultStack();
        stack.set(DataComponentTypes.ITEM_MODEL, Identifier.of(Antiquities.MOD_ID, "smoke_bomb"));
        this.itemModelManager.updateForNonLivingEntity(flyingItemEntityRenderState.itemRenderState, stack, ItemDisplayContext.GROUND, entity);
    }
}
