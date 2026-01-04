package net.hollowed.antique.entities.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.antique.entities.parts.MyriadShovelPart;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class MyriadShovelPartRenderer extends EntityRenderer<@NotNull MyriadShovelPart, @NotNull MyriadShovelRenderState> {

	public MyriadShovelPartRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	public MyriadShovelRenderState createRenderState() {
		return new MyriadShovelRenderState();
	}
}