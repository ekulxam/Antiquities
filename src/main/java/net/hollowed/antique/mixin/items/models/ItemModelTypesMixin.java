package net.hollowed.antique.mixin.items.models;

import com.mojang.serialization.MapCodec;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.util.models.*;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemModelTypes.class)
public class ItemModelTypesMixin {

    @Shadow @Final public static Codecs.IdMapper<Identifier, MapCodec<? extends ItemModel.Unbaked>> ID_MAPPER;

    @Inject(method = "bootstrap", at = @At("HEAD"))
    private static void bootstrap(CallbackInfo ci) {
        ID_MAPPER.put(Identifier.of(Antiquities.MOD_ID, "satchel/selected_item"), SatchelSelectedItemModel.Unbaked.CODEC);
        ID_MAPPER.put(Identifier.of(Antiquities.MOD_ID, "bag/selected_item"), BagOfTricksSelectedItemModel.Unbaked.CODEC);
        ID_MAPPER.put(Identifier.of(Antiquities.MOD_ID, "bag/first_stack"), BagOfTricksFirstStackItemModel.Unbaked.CODEC);
        ID_MAPPER.put(Identifier.of(Antiquities.MOD_ID, "myriad_cloth"), MyriadClothItemModel.Unbaked.CODEC);
        ID_MAPPER.put(Identifier.of(Antiquities.MOD_ID, "cloth"), ClothItemModel.Unbaked.CODEC);
    }
}
