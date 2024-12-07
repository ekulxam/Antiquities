package net.hollowed.antique.mixin;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityRenderDispatcher.class)
public interface EntityRenderDispatcherAccessor {
    /**
     * Exposes the private render method in EntityRenderDispatcher.
     *
     * @param entity        The entity to render.
     * @param x             The X position.
     * @param y             The Y position.
     * @param z             The Z position.
     * @param tickDelta     The render partial tick delta.
     * @param matrices      The matrix stack for transformations.
     * @param vertexConsumers The vertex consumer provider.
     * @param light         The light value.
     * @param renderer      The entity renderer instance.
     */
    @Invoker("render")
    <E extends Entity, S extends EntityRenderState> void invokeRender(E entity, double x, double y, double z, float tickDelta,
                                                                      MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                                                      int light, EntityRenderer<? super E, S> renderer);
}
