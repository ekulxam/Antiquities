package net.hollowed.antique.mixin;

import net.hollowed.antique.items.ModItems;
import net.hollowed.antique.util.ModProjectileDeflection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin {

    @Redirect(
            method = "hitOrDeflect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getProjectileDeflection(Lnet/minecraft/entity/projectile/ProjectileEntity;)Lnet/minecraft/entity/ProjectileDeflection;"
            )
    )
    private ProjectileDeflection redirectProjectileDeflection(Entity entity, ProjectileEntity projectile) {
//        if (entity instanceof LivingEntity livingEntity) {
//            // Check if the entity is wearing Netherite Pauldrons
//            ItemStack chestArmor = livingEntity.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST);
//            if (chestArmor.getItem() == ModItems.NETHERITE_PAULDRONS) {
//                // Return a custom deflection logic
//                return (projectileEntity, hitEntity, random) -> {
//                    assert hitEntity != null;
//                    hitEntity.getWorld().playSoundFromEntity(null, hitEntity, SoundEvents.ENTITY_BREEZE_DEFLECT, hitEntity.getSoundCategory(), 1.0F, 1.0F);
//
//                    // Adjust the velocity if the projectile has no gravity
//                    if (!projectileEntity.hasNoGravity()) {
//                        Vec3d velocity = projectileEntity.getVelocity();
//                        // Add a slight downward vector
//                        projectileEntity.setVelocity(velocity.add(0, 0.2, 0));
//                    }
//                    ModProjectileDeflection.SIMPLE.deflect(projectileEntity, hitEntity, random);
//                };
//            }
//        }

        // Default behavior if not wearing Netherite Pauldrons
        return entity.getProjectileDeflection(projectile);
    }
}
