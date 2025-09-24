package net.hollowed.antique.util.properties;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record ProjectileTypeProperty() implements SelectProperty<Identifier> {
	public static final SelectProperty.Type<ProjectileTypeProperty, Identifier> TYPE = SelectProperty.Type.create(
			MapCodec.unit(new ProjectileTypeProperty()), Identifier.CODEC
	);

	public Identifier getValue(
		ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext
	) {
		ChargedProjectilesComponent chargedProjectilesComponent = itemStack.get(DataComponentTypes.CHARGED_PROJECTILES);
		if (chargedProjectilesComponent == null || chargedProjectilesComponent.isEmpty()) {
			return Identifier.of("none");
		} else {
			return Registries.ITEM.getId(chargedProjectilesComponent.getProjectiles().getFirst().getItem());
		}
	}

	@Override
	public Type<ProjectileTypeProperty, Identifier> getType() {
		return TYPE;
	}

	@Override
	public Codec<Identifier> valueCodec() {
		return Identifier.CODEC;
	}
}
