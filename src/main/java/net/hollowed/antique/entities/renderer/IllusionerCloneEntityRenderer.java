//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.hollowed.antique.entities.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.entities.IllusionerCloneEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.IllusionerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

@Environment(EnvType.CLIENT)
public class IllusionerCloneEntityRenderer extends IllagerEntityRenderer<IllusionerCloneEntity, IllusionerEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.of(Antiquities.MOD_ID, "textures/entity/illager/illusioner_clone.png");

    public IllusionerCloneEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new IllagerEntityModel<>(context.getPart(EntityModelLayers.ILLUSIONER)), 0.5F);
        this.addFeature(new HeldItemFeatureRenderer<>(this) {
            public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, IllusionerEntityRenderState illusionerEntityRenderState, float f, float g) {
                if (illusionerEntityRenderState.spellcasting || illusionerEntityRenderState.attacking) {
                    super.render(matrixStack, vertexConsumerProvider, i, illusionerEntityRenderState, f, g);
                }

            }
        });
        this.model.getHat().visible = true;
    }

    public Identifier getTexture(IllusionerEntityRenderState illusionerEntityRenderState) {
        return TEXTURE;
    }

    public IllusionerEntityRenderState createRenderState() {
        return new IllusionerEntityRenderState();
    }

    public void updateRenderState(IllusionerCloneEntity illusionerEntity, IllusionerEntityRenderState illusionerEntityRenderState, float f) {
        super.updateRenderState(illusionerEntity, illusionerEntityRenderState, f);
    }

    public void render(IllusionerEntityRenderState illusionerEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(illusionerEntityRenderState, matrixStack, vertexConsumerProvider, i);
    }

    protected boolean isVisible(IllusionerEntityRenderState illusionerEntityRenderState) {
        return true;
    }

    protected Box getBoundingBox(IllusionerCloneEntity illusionerEntity) {
        return super.getBoundingBox(illusionerEntity).expand(3.0, 0.0, 3.0);
    }
}
