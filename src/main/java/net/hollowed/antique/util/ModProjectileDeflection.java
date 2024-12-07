package net.hollowed.antique.util;

import net.minecraft.entity.ProjectileDeflection;

@FunctionalInterface
public interface ModProjectileDeflection extends ProjectileDeflection {
    ProjectileDeflection CUSTOM_SIMPLE = (projectile, hitEntity, random) -> {
        float f = 170.0F + random.nextFloat() * 20.0F;
        projectile.setYaw(projectile.getYaw() + f);
        projectile.prevYaw += f;
        projectile.velocityDirty = true;
    };
}
