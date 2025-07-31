package net.hollowed.antique.mixin.items.properties;

import com.mojang.serialization.MapCodec;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.util.properties.BagOfTricksHasSelectedItemProperty;
import net.hollowed.antique.util.properties.SatchelHasFirstStackItemProperty;
import net.hollowed.antique.util.properties.SatchelHasSelectedItemProperty;
import net.hollowed.antique.util.properties.ScreenOpenItemProperty;
import net.minecraft.client.render.item.property.bool.BooleanProperties;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BooleanProperties.class)
public class BooleanPropertyMixin {
    @Shadow @Final public static Codecs.IdMapper<Identifier, MapCodec<? extends BooleanProperty>> ID_MAPPER;

    @Inject(method = "bootstrap", at = @At("HEAD"))
    private static void bootstrap(CallbackInfo ci) {
        ID_MAPPER.put(Identifier.of(Antiquities.MOD_ID, "satchel/has_selected_item"), SatchelHasSelectedItemProperty.CODEC);
        ID_MAPPER.put(Identifier.of(Antiquities.MOD_ID, "bag/has_selected_item"), BagOfTricksHasSelectedItemProperty.CODEC);
        ID_MAPPER.put(Identifier.of(Antiquities.MOD_ID, "satchel/has_first_stack"), SatchelHasFirstStackItemProperty.CODEC);
        ID_MAPPER.put(Identifier.of(Antiquities.MOD_ID, "screen_open"), ScreenOpenItemProperty.CODEC);
    }
}
