package net.hollowed.antique.entities.custom;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.client.renderer.cloth.ClothManager;
import net.hollowed.antique.index.AntiqueEntities;
import net.hollowed.antique.entities.parts.MyriadShovelPart;
import net.hollowed.combatamenities.particles.ModParticles;
import net.minecraft.component.DataComponentTypes;
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
import org.joml.Vector3d;

import java.util.Objects;

public class MyriadShovelEntity extends PersistentProjectileEntity {
	public static final TrackedData<Byte> LOYALTY = DataTracker.registerData(MyriadShovelEntity.class, TrackedDataHandlerRegistry.BYTE);
	public static final TrackedData<Integer> COLOR = DataTracker.registerData(MyriadShovelEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static final TrackedData<Boolean> ENCHANTED = DataTracker.registerData(MyriadShovelEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private boolean dealtDamage;
	public int returnTimer;
	public ItemStack shovelStack;

	public boolean canPickup;
	public final ClothManager manager;

	public MyriadShovelEntity(EntityType<MyriadShovelEntity> entityType, World world) {
		super(entityType, world);
		this.shovelStack = Antiquities.getMyriadShovelStack();
		this.manager = new ClothManager(new Vector3d(), 8);
		this.dataTracker.set(COLOR, Objects.requireNonNull(this.shovelStack.get(DataComponentTypes.DYED_COLOR)).rgb());
	}

	public MyriadShovelEntity(World world, LivingEntity owner, ItemStack stack) {
		super(AntiqueEntities.MYRIAD_SHOVEL, owner, world, stack, null);
		this.dataTracker.set(LOYALTY, this.getLoyalty(stack));
		this.dataTracker.set(ENCHANTED, stack.hasGlint());
		this.shovelStack = stack;
		this.manager = new ClothManager(new Vector3d(), 8);
		this.dataTracker.set(COLOR, Objects.requireNonNull(this.shovelStack.get(DataComponentTypes.DYED_COLOR)).rgb());
	}

	public int getDyeColor() {
		return this.dataTracker.get(COLOR);
	}

	public void summonPart() {
		MyriadShovelPart entity1 = new MyriadShovelPart(AntiqueEntities.MYRIAD_SHOVEL_PART, this.getWorld());
		entity1.setOwner(this);
		entity1.setOrderId(1);
		this.getWorld().spawnEntity(entity1);

		MyriadShovelPart entity2 = new MyriadShovelPart(AntiqueEntities.MYRIAD_SHOVEL_PART, this.getWorld());
		entity2.setOwner(this);
		entity2.setOrderId(2);
		this.getWorld().spawnEntity(entity2);

		MyriadShovelPart entity3 = new MyriadShovelPart(AntiqueEntities.MYRIAD_SHOVEL_PART, this.getWorld());
		entity3.setOwner(this);
		entity3.setOrderId(3);
		this.getWorld().spawnEntity(entity3);

		MyriadShovelPart entity4 = new MyriadShovelPart(AntiqueEntities.MYRIAD_SHOVEL_PART, this.getWorld());
		entity4.setOwner(this);
		entity4.setOrderId(4);
		this.getWorld().spawnEntity(entity4);

		MyriadShovelPart entity5 = new MyriadShovelPart(AntiqueEntities.MYRIAD_SHOVEL_PART, this.getWorld());
		entity5.setOwner(this);
		entity5.setOrderId(5);
		this.getWorld().spawnEntity(entity5);

		MyriadShovelPart entity6 = new MyriadShovelPart(AntiqueEntities.MYRIAD_SHOVEL_PART, this.getWorld());
		entity6.setOwner(this);
		entity6.setOrderId(6);
		this.getWorld().spawnEntity(entity6);

		MyriadShovelPart entity7 = new MyriadShovelPart(AntiqueEntities.MYRIAD_SHOVEL_PART, this.getWorld());
		entity7.setOwner(this);
		entity7.setOrderId(7);
		this.getWorld().spawnEntity(entity7);
	}

	public boolean isEnchanted() {
		return this.dataTracker.get(ENCHANTED);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(LOYALTY, (byte)0);
		builder.add(ENCHANTED, false);
		builder.add(COLOR, 1);
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
				if (this.getWorld() instanceof ServerWorld serverWorld && this.pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
					this.dropStack(serverWorld, this.asItemStack(), 0.1F);
				}

				this.discard();
			} else {
				if (!(entity instanceof PlayerEntity) && this.getPos().distanceTo(entity.getEyePos()) < (double)entity.getWidth() + 1.0) {
					this.discard();
					return;
				}

				this.setNoClip(true);
				Vec3d vec3d = entity.getEyePos().subtract(this.getPos());
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

	@Nullable
	@Override
	protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
		return this.dealtDamage ? null : super.getEntityCollision(currentPosition, nextPosition);
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (this.getWorld() instanceof ServerWorld serverWorld) {
			serverWorld.spawnParticles(ModParticles.RING, this.getX(), this.getY(), this.getZ(), 1, 0.0, 0.0, 0.0, 0);
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
		DamageSource damageSource = this.getDamageSources().trident(this, entity2 == null ? this : entity2);
		if (this.getWorld() instanceof ServerWorld serverWorld) {
			f = EnchantmentHelper.getDamage(serverWorld, Objects.requireNonNull(this.getWeaponStack()), entity, damageSource, f);
			this.dealtDamage = true;
			if (entity.damage(serverWorld, damageSource, f)) {
				if (entity.getType() == EntityType.ENDERMAN) {
					return;
				}

				EnchantmentHelper.onTargetDamaged(serverWorld, entity, damageSource, this.getWeaponStack(), item -> this.kill(serverWorld));

				if (entity instanceof LivingEntity livingEntity) {
					this.knockback(livingEntity, damageSource);
					this.onHit(livingEntity);
				}
			}
		}

		this.deflect(ProjectileDeflection.SIMPLE, entity, this.getOwner(), false);
		this.setVelocity(this.getVelocity().multiply(0.02, 0.2, 0.02));
		this.playSound(SoundEvents.ITEM_TRIDENT_HIT, 1.0F, 1.0F);
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
		this.dataTracker.set(COLOR, view.getInt("Color", 0));
	}

	@Override
	protected void writeCustomData(WriteView view) {
		super.writeCustomData(view);
		view.putBoolean("DealtDamage", this.dealtDamage);
		view.putInt("Color", this.getDyeColor());
		view.putBoolean("Glint", this.isEnchanted());
	}

	private byte getLoyalty(ItemStack stack) {
		return this.getWorld() instanceof ServerWorld serverWorld
				? (byte)MathHelper.clamp(EnchantmentHelper.getTridentReturnAcceleration(serverWorld, stack, this), 0, 127)
				: 0;
	}

	@Override
	public void age() {

	}

	@Override
	protected float getDragInWater() {
		return 0.7F;
	}
}
