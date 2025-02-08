package net.hollowed.antique.mixin;

import com.mojang.serialization.MapCodec;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.items.properties.BooleanComponentProperty;
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
        ID_MAPPER.put(Identifier.of(Antiquities.MOD_ID, "bool_property"), BooleanComponentProperty.CODEC);
    }
}
