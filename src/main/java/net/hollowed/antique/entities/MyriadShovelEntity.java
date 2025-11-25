package net.hollowed.antique.entities;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.index.AntiqueDataComponentTypes;
import net.hollowed.antique.index.AntiqueDamageTypes;
import net.hollowed.antique.index.AntiqueEntities;
import net.hollowed.antique.entities.parts.MyriadShovelPart;
import net.hollowed.combatamenities.index.CAParticles;
import net.hollowed.combatamenities.util.items.CAComponents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MyriadShovelEntity extends PersistentProjectileEntity {
	public static final TrackedData<Byte> LOYALTY = DataTracker.registerData(MyriadShovelEntity.class, TrackedDataHandlerRegistry.BYTE);
	public static final TrackedData<Integer> COLOR = DataTracker.registerData(MyriadShovelEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static final TrackedData<Integer> OVERLAY_COLOR = DataTracker.registerData(MyriadShovelEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static final TrackedData<String> CLOTH = DataTracker.registerData(MyriadShovelEntity.class, TrackedDataHandlerRegistry.STRING);
	public static final TrackedData<String> PATTERN = DataTracker.registerData(MyriadShovelEntity.class, TrackedDataHandlerRegistry.STRING);
	public static final TrackedData<Boolean> ENCHANTED = DataTracker.registerData(MyriadShovelEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	public static final TrackedData<Byte> PIERCE_LEVEL = DataTracker.registerData(MyriadShovelEntity.class, TrackedDataHandlerRegistry.BYTE);
	public static final TrackedData<Boolean> GLOW = DataTracker.registerData(MyriadShovelEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private boolean dealtDamage;
	public int returnTimer;
	@Nullable
	private IntOpenHashSet piercedEntities;

	public boolean canPickup;

	public MyriadShovelEntity(EntityType<MyriadShovelEntity> entityType, World world) {
		super(entityType, world);
		this.setDamage(8);
		this.setStack(Antiquities.getMyriadShovelStack());
		this.dataTracker.set(COLOR, this.getItemStack().getOrDefault(DataComponentTypes.DYED_COLOR, new DyedColorComponent(0xFFFFFF)).rgb());
		this.dataTracker.set(OVERLAY_COLOR, this.getItemStack().getOrDefault(AntiqueDataComponentTypes.SECONDARY_DYED_COLOR, new DyedColorComponent(0xFFFFFF)).rgb());
		this.dataTracker.set(CLOTH, this.getItemStack().getOrDefault(AntiqueDataComponentTypes.CLOTH_TYPE, ""));
		this.dataTracker.set(GLOW, this.getItemStack().getOrDefault(CAComponents.BOOLEAN_PROPERTY, false));
		this.setPierceLevel((byte) 5);
	}

	public MyriadShovelEntity(World world, LivingEntity owner, ItemStack stack) {
		super(AntiqueEntities.MYRIAD_SHOVEL, owner, world, stack, null);
		this.setDamage(8);
		this.dataTracker.set(LOYALTY, this.getLoyalty(stack));
		this.dataTracker.set(ENCHANTED, stack.hasGlint());
		this.dataTracker.set(GLOW, stack.getOrDefault(CAComponents.BOOLEAN_PROPERTY, false));
		this.setStack(stack);
		this.dataTracker.set(COLOR, this.getItemStack().getOrDefault(DataComponentTypes.DYED_COLOR, new DyedColorComponent(0xFFFFFF)).rgb());
		this.dataTracker.set(OVERLAY_COLOR, this.getItemStack().getOrDefault(AntiqueDataComponentTypes.SECONDARY_DYED_COLOR, new DyedColorComponent(0xFFFFFF)).rgb());
		this.dataTracker.set(CLOTH, this.getItemStack().getOrDefault(AntiqueDataComponentTypes.CLOTH_TYPE, ""));
		this.dataTracker.set(PATTERN, this.getItemStack().getOrDefault(AntiqueDataComponentTypes.CLOTH_PATTERN, ""));
		this.setPierceLevel((byte) 5);
	}

	public int getDyeColor() {
		return this.dataTracker.get(COLOR);
	}

	public int getOverlayColor() {
		return this.dataTracker.get(OVERLAY_COLOR);
	}

	public boolean getGlow() {
		return this.dataTracker.get(GLOW);
	}

	public void summonPart() {
		for (int i = 1; i < 9; i++) {
			MyriadShovelPart entity = new MyriadShovelPart(AntiqueEntities.MYRIAD_SHOVEL_PART, this.getEntityWorld());
			entity.setPosition(this.getEntityPos());
			entity.setOwner(this);
			entity.setOrderId(i);
			this.getEntityWorld().spawnEntity(entity);
		}
	}

	public boolean isEnchanted() {
		return this.dataTracker.get(ENCHANTED);
	}

	public String getCloth() {
		return this.dataTracker.get(CLOTH);
	}

	public String getPattern() {
		return this.dataTracker.get(PATTERN);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(LOYALTY, (byte)0);
		builder.add(ENCHANTED, false);
		builder.add(GLOW, false);
		builder.add(COLOR, 1);
		builder.add(OVERLAY_COLOR, 1);
		builder.add(PIERCE_LEVEL, (byte) 0);
		builder.add(CLOTH, "cloth");
		builder.add(PATTERN, "");
	}

	@Override
	public void tick() {
		if (this.inGroundTime > 4) {
			this.dealtDamage = true;
		}

		Entity entity = this.getOwner();
		int i = this.dataTracker.get(LOYALTY);
		if (i > 0 && (this.dealtDamage || this.isNoClip()) && entity != null) {
			if (!this.isOwnerAlive()) {
				if (this.getEntityWorld() instanceof ServerWorld serverWorld && this.pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
					this.dropStack(serverWorld, this.asItemStack(), 0.1F);
				}

				this.discard();
			} else {
				if (!(entity instanceof PlayerEntity) && this.getEntityPos().distanceTo(entity.getEyePos()) < (double)entity.getWidth() + 1.0) {
					this.discard();
					return;
				}

				this.setNoClip(true);
				Vec3d vec3d = entity.getEyePos().subtract(this.getEntityPos());
				this.setPos(this.getX(), this.getY() + vec3d.y * 0.015 * (double)i, this.getZ());
				double d = 0.05 * (double)i;
				this.setVelocity(this.getVelocity().multiply(0.95).add(vec3d.normalize().multiply(d)));
				if (this.returnTimer == 0) {
					this.playSound(SoundEvents.ITEM_TRIDENT_RETURN, 10.0F, 1.0F);
				}

				this.returnTimer++;
			}
		}

		super.tick();
	}

	private boolean isOwnerAlive() {
		Entity entity = this.getOwner();
		return entity != null && entity.isAlive() && (!(entity instanceof ServerPlayerEntity) || !entity.isSpectator());
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
			Vec3d pos = this.getEntityPos().add(this.getRotationVector().multiply(1, 1, -1));
			serverWorld.spawnParticles(CAParticles.RING, pos.getX(), pos.getY(), pos.getZ(), 1, 0.0, 0.0, 0.0, 0);
		}
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		if (this.age >= 2000) {
			this.age -= 2000;
		}

		Entity entity = entityHitResult.getEntity();
		float f = 8.0F;
		Entity entity2 = this.getOwner();
		DamageSource damageSource = AntiqueDamageTypes.of(entity.getEntityWorld(), AntiqueDamageTypes.IMPALE, entity2 == null ? this : entity2);
		if (this.getEntityWorld() instanceof ServerWorld serverWorld) {

			if (this.getPierceLevel() > 0) {
				if (this.piercedEntities == null) {
					this.piercedEntities = new IntOpenHashSet(5);
				}

				this.piercedEntities.add(entity.getId());
			}

			f = EnchantmentHelper.getDamage(serverWorld, this.getWeaponStack() != null ? this.getWeaponStack() : ItemStack.EMPTY, entity, damageSource, f);
			if (entity.damage(serverWorld, damageSource, f)) {
				if (entity.getType() == EntityType.ENDERMAN) {
					return;
				}

				EnchantmentHelper.onTargetDamaged(serverWorld, entity, damageSource, this.getWeaponStack(), item -> this.kill(serverWorld));
				if (entity instanceof LivingEntity) {
					entity.setVelocity(this.getVelocity().multiply(0.6, 0.45, 0.6));
					entity.velocityModified = true;
				}
			}
		}

		if (this.getPierceLevel() <= 0) {
			this.deflect(ProjectileDeflection.SIMPLE, entity, null, false);
			this.setVelocity(this.getVelocity().multiply(0.2, 0.2, 0.02));
			this.playSound(SoundEvents.ITEM_TRIDENT_HIT, 1.0F, 1.0F);
		}
	}

	@Override
	protected void onBlockHitEnchantmentEffects(ServerWorld world, BlockHitResult blockHitResult, ItemStack weaponStack) {
		Vec3d vec3d = blockHitResult.getBlockPos().clampToWithin(blockHitResult.getPos());
		EnchantmentHelper.onHitBlock(
				world,
				weaponStack,
				this.getOwner() instanceof LivingEntity livingEntity ? livingEntity : null,
				this,
				null,
				vec3d,
				world.getBlockState(blockHitResult.getBlockPos()),
				item -> this.kill(world)
		);
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		super.onBlockHit(blockHitResult);
		this.setPierceLevel((byte)0);
		this.clearPiercingStatus();
		summonPart();
	}

	@Override
	public ItemStack getWeaponStack() {
		return this.getItemStack();
	}

	@Override
	protected boolean tryPickup(PlayerEntity player) {
		return this.canPickup && !player.isCreative() && player.getInventory().insertStack(this.asItemStack())
				|| this.isNoClip() && !player.isCreative() && this.isOwner(player) && player.getInventory().insertStack(this.asItemStack())
				|| this.canPickup && player.isCreative();
	}

	@Override
	protected ItemStack getDefaultItemStack() {
		return new ItemStack(Items.TRIDENT);
	}

	@Override
	protected SoundEvent getHitSound() {
		return SoundEvents.ITEM_TRIDENT_HIT_GROUND;
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		if (this.isOwner(player) || this.getOwner() == null) {
			super.onPlayerCollision(player);
		}
	}

	@Override
	protected void readCustomData(ReadView view) {
		super.readCustomData(view);
		this.dealtDamage = view.getBoolean("DealtDamage", false);
		this.dataTracker.set(LOYALTY, this.getLoyalty(this.getItemStack()));
		this.dataTracker.set(ENCHANTED, view.getBoolean("Glint", false));
		this.dataTracker.set(GLOW, view.getBoolean("Glow", false));
		this.dataTracker.set(COLOR, view.getInt("Color", 0));
		this.dataTracker.set(OVERLAY_COLOR, view.getInt("OverlayColor", 0));
		this.dataTracker.set(CLOTH, view.getString("Cloth", "cloth"));
		this.dataTracker.set(PATTERN, view.getString("Pattern", ""));
		this.setPierceLevel(view.getByte("PierceLevel", (byte) 0));
	}

	@Override
	protected void writeCustomData(WriteView view) {
		super.writeCustomData(view);
		view.putBoolean("DealtDamage", this.dealtDamage);
		view.putInt("Color", this.getDyeColor());
		view.putInt("OverlayColor", this.getOverlayColor());
		view.putBoolean("Glint", this.isEnchanted());
		view.putBoolean("Glow", this.getGlow());
		view.putByte("PierceLevel", this.getPierceLevel());
		view.putString("Cloth", this.getCloth());
		view.putString("Pattern", this.getPattern());
	}

	private byte getLoyalty(ItemStack stack) {
		return this.getEntityWorld() instanceof ServerWorld serverWorld
				? (byte)MathHelper.clamp(EnchantmentHelper.getTridentReturnAcceleration(serverWorld, stack, this), 0, 127)
				: 0;
	}

	@Override
	public void age() {

	}

	@Override
	protected boolean canHit(Entity entity) {
		if (entity instanceof PlayerEntity) {
			Entity var3 = this.getOwner();
			if (var3 instanceof PlayerEntity playerEntity) {
                if (!playerEntity.shouldDamagePlayer((PlayerEntity)entity)) {
					return false;
				}
			}
		}

		return super.canHit(entity) && (this.piercedEntities == null || !this.piercedEntities.contains(entity.getId()));
	}

	@Override
	protected float getDragInWater() {
		return 0.7F;
	}

	private void clearPiercingStatus() {
		if (this.piercedEntities != null) {
			this.piercedEntities.clear();
		}

	}

	private void setPierceLevel(byte level) {
		this.dataTracker.set(PIERCE_LEVEL, level);
	}

	public byte getPierceLevel() {
		return this.dataTracker.get(PIERCE_LEVEL);
	}
}
