package net.hollowed.antique.entities.custom;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.client.item.explosive_spear.ClothManager;
import net.hollowed.antique.entities.ModEntities;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.List;
import java.util.Objects;

public class MyriadShovelEntity extends PersistentProjectileEntity {
	private static final TrackedData<Byte> LOYALTY = DataTracker.registerData(MyriadShovelEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Integer> COLOR = DataTracker.registerData(MyriadShovelEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> ENCHANTED = DataTracker.registerData(MyriadShovelEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private boolean dealtDamage;
	public int returnTimer;
	public ItemStack shovelStack;

	public boolean canPickup;
	public final ClothManager manager;
	public int dyeColor;

//	private final MyriadShovelPart[] parts;
//	public final MyriadShovelPart part1;
//	public final MyriadShovelPart part2;

	public MyriadShovelEntity(EntityType<MyriadShovelEntity> entityType, World world) {
		super(entityType, world);
		this.shovelStack = Antiquities.getMyriadShovelStack();
		this.manager = new ClothManager(new Vector3d(), 8);
		this.dataTracker.set(COLOR, Objects.requireNonNull(this.shovelStack.get(DataComponentTypes.DYED_COLOR)).rgb());

//		this.part1 = new MyriadShovelPart(this, "part1", 1F, 1F);
//		this.part2 = new MyriadShovelPart(this, "part2", 1F, 1F);
//		this.parts = new MyriadShovelPart[]{this.part1, this.part2};
	}

	public MyriadShovelEntity(World world, LivingEntity owner, ItemStack stack) {
		super(ModEntities.MYRIAD_SHOVEL, owner, world, stack, null);
		this.dataTracker.set(LOYALTY, this.getLoyalty(stack));
		this.dataTracker.set(ENCHANTED, stack.hasGlint());
		this.shovelStack = stack;
		this.manager = new ClothManager(new Vector3d(), 8);
		this.dataTracker.set(COLOR, Objects.requireNonNull(this.shovelStack.get(DataComponentTypes.DYED_COLOR)).rgb());
		System.out.println(this.dyeColor);

//		this.part1 = new MyriadShovelPart(this, "part1", 0.1F, 0.1F);
//		this.part2 = new MyriadShovelPart(this, "part2", 0.1F, 0.1F);
//		this.parts = new MyriadShovelPart[]{this.part1, this.part2};
	}

	public int getDyeColor() {
		return this.dataTracker.get(COLOR);
	}

//	public MyriadShovelPart[] getBodyParts() {
//		return this.parts;
//	}


//	@Override
//	public void onSpawnPacket(EntitySpawnS2CPacket packet) {
//		super.onSpawnPacket(packet);
//		MyriadShovelPart[] enderDragonParts = this.getBodyParts();
//
//		for (int i = 0; i < enderDragonParts.length; i++) {
//			enderDragonParts[i].setId(i + packet.getEntityId() + 1);
//		}
//	}

	public void summonPart() {
		MyriadShovelPart entity1 = new MyriadShovelPart(ModEntities.MYRIAD_SHOVEL_PART, this.getWorld());
		entity1.setOwner(this);
		entity1.setOrderId(1);
		this.getWorld().spawnEntity(entity1);

		MyriadShovelPart entity2 = new MyriadShovelPart(ModEntities.MYRIAD_SHOVEL_PART, this.getWorld());
		entity2.setOwner(this);
		entity2.setOrderId(2);
		this.getWorld().spawnEntity(entity2);

		MyriadShovelPart entity3 = new MyriadShovelPart(ModEntities.MYRIAD_SHOVEL_PART, this.getWorld());
		entity3.setOwner(this);
		entity3.setOrderId(3);
		this.getWorld().spawnEntity(entity3);

		MyriadShovelPart entity4 = new MyriadShovelPart(ModEntities.MYRIAD_SHOVEL_PART, this.getWorld());
		entity4.setOwner(this);
		entity4.setOrderId(4);
		this.getWorld().spawnEntity(entity4);

		MyriadShovelPart entity5 = new MyriadShovelPart(ModEntities.MYRIAD_SHOVEL_PART, this.getWorld());
		entity5.setOwner(this);
		entity5.setOrderId(5);
		this.getWorld().spawnEntity(entity5);

		MyriadShovelPart entity6 = new MyriadShovelPart(ModEntities.MYRIAD_SHOVEL_PART, this.getWorld());
		entity6.setOwner(this);
		entity6.setOrderId(6);
		this.getWorld().spawnEntity(entity6);

		MyriadShovelPart entity7 = new MyriadShovelPart(ModEntities.MYRIAD_SHOVEL_PART, this.getWorld());
		entity7.setOwner(this);
		entity7.setOrderId(7);
		this.getWorld().spawnEntity(entity7);
	}

	@Override
	public void onRemove(RemovalReason reason) {
		super.onRemove(reason);
		List<Entity> list = this.getWorld().getOtherEntities(null, new Box(this.getX() - 4, this.getY() - 4, this.getZ() -4,
				this.getX() + 4, this.getY() + 4, this.getZ() + 4)).reversed();
		for (Entity entity : list) {
			if (entity instanceof MyriadShovelPart part && (part.getOwner() == this || part.getOwner() == null)) {
				part.discard();
				if (this.getWorld() instanceof ServerWorld world) {
					world.getChunkManager().unloadEntity(entity);
				}
			}
		}
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

//	private void movePart(MyriadShovelPart enderDragonPart, double dx, double dy, double dz) {
//		enderDragonPart.setPosition(this.getX() + dx, this.getY() + dy, this.getZ() + dz);
//	}

	@Override
	public void tick() {

//		for (MyriadShovelPart part : this.getBodyParts()) {
//			this.movePart(part, 0, 0, 0);
//
//			Box box = part.getBoundingBox();
//		}

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
		}

		this.dealtDamage = true;
		if (entity.sidedDamage(damageSource, f)) {
			if (entity.getType() == EntityType.ENDERMAN) {
				return;
			}

			if (this.getWorld() instanceof ServerWorld serverWorld) {
				EnchantmentHelper.onTargetDamaged(serverWorld, entity, damageSource, this.getWeaponStack(), item -> this.kill(serverWorld));
			}

			if (entity instanceof LivingEntity livingEntity) {
				this.knockback(livingEntity, damageSource);
				this.onHit(livingEntity);
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
				|| this.isNoClip() && this.isOwner(player) && player.getInventory().insertStack(this.asItemStack())
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
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.getBoolean("DealtDamage").isPresent()) this.dealtDamage = nbt.getBoolean("DealtDamage").get();
		this.dataTracker.set(LOYALTY, this.getLoyalty(this.getItemStack()));
		if (nbt.getBoolean("Glint").isPresent()) this.dataTracker.set(ENCHANTED, nbt.getBoolean("Glint").get());
		if (nbt.getInt("Color").isPresent()) this.dataTracker.set(COLOR, nbt.getInt("Color").get());
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("DealtDamage", this.dealtDamage);
		nbt.putInt("Color", this.getDyeColor());
		nbt.putBoolean("Glint", this.isEnchanted());
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

	@Override
	public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
		return true;
	}
}
