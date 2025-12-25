package net.hollowed.antique.mixin.items;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BundleContents.class)
public class BundleShowsAllContents {

    @Shadow
    public int size() { return 0; }

    @ModifyReturnValue(method = "getNumberOfItemsToShow", at = @At("RETURN"))
    private int modifyStacksShown(int original) {
        return this.size();
    }
}
