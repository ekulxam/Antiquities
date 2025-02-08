package net.hollowed.antique.mixin;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.component.ModComponents;
import net.hollowed.antique.items.custom.MyriadToolItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class MyriadDisableShields {

    @Shadow @NotNull public abstract ItemStack getWeaponStack();

    @Inject(method = "disablesShield", at = @At("HEAD"), cancellable = true)
    public void myriadShieldDisable(CallbackInfoReturnable<Boolean> cir) {
        if (this.getWeaponStack().getItem() instanceof MyriadToolItem) {
            if (this.getWeaponStack().get(ModComponents.INTEGER_PROPERTY) != null && Objects.requireNonNull(this.getWeaponStack().get(ModComponents.INTEGER_PROPERTY)) == 2) {
                cir.setReturnValue(true);
            }
        }
        if (this.getWeaponStack().streamTags().toList().contains(TagKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "disables_shields")))) {
            cir.setReturnValue(true);
        }
    }
}
