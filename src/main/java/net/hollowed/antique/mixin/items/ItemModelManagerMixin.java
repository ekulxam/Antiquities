package net.hollowed.antique.mixin.items;

import net.hollowed.antique.index.AntiqueDataComponentTypes;
import net.hollowed.antique.items.components.MyriadToolComponent;
import net.minecraft.client.item.ItemAsset;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.HeldItemContext;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(ItemModelManager.class)
public class ItemModelManagerMixin {

    @Shadow @Final private Function<Identifier, ItemModel> modelGetter;

    @Shadow @Final private Function<Identifier, ItemAsset.Properties> propertiesGetter;

    @Inject(method = "update", at = @At("TAIL"))
    private void update(ItemRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, World world, HeldItemContext heldItemContext, int seed, CallbackInfo ci) {
        MyriadToolComponent component = stack.get(AntiqueDataComponentTypes.MYRIAD_TOOL);
        if (component != null) {
            String rawId = Registries.ITEM.getId(component.toolBit().getItem()).toString();
            Identifier identifier = Identifier.of(rawId.substring(0, rawId.lastIndexOf("_")));
            renderState.setOversizedInGui(this.propertiesGetter.apply(identifier).oversizedInGui());
            this.modelGetter.apply(identifier)
                    .update(renderState, stack, (ItemModelManager) (Object) this, displayContext, world instanceof ClientWorld clientWorld ? clientWorld : null, heldItemContext, seed);
        }
    }
}
