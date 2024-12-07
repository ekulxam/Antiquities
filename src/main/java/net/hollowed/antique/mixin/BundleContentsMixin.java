package net.hollowed.antique.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.component.type.BundleContentsComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BundleContentsComponent.class)
public class BundleContentsMixin {
    @Shadow
    public int size() { return 0; }

    /**
     * Modifies the return value of getNumberOfStacksShown to match the size() method.
     */
    @ModifyReturnValue(method = "getNumberOfStacksShown", at = @At("RETURN"))
    private int modifyStacksShown(int original) {
        return this.size();
    }
}
