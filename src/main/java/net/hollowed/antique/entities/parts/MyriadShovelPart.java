package net.hollowed.antique.entities.parts;

import net.hollowed.antique.entities.custom.MyriadShovelEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MyriadShovelPart extends Entity implements Ownable {

	private Entity owner;
	private UUID ownerId;

	private int id;

	public MyriadShovelPart(EntityType<MyriadShovelPart> myriadShovelEntityEntityType, World world) {
		super(myriadShovelEntityEntityType, world);
	}

	@Nullable
	protected Entity getEntity(UUID uuid) {
		return this.getWorld() instanceof ServerWorld serverWorld ? serverWorld.getEntity(uuid) : null;
	}

	public void setOrderId(int id) {
		this.id = id;
	}

	public int getOrderId() {
		return this.id;
	}

	public void setOwner(@Nullable Entity entity) {
		if (entity != null) {
			this.ownerId = entity.getUuid();
			this.owner = entity;
		}
	}

	protected void setOwner(String string) {
		UUID uuid = UUID.fromString(string);
		if (this.ownerId != uuid) {
			this.ownerId = uuid;
			this.owner = this.getEntity(uuid);
		}
	}

	@Override
	public void tick() {
		if (this.age >= 20) {
			this.age -= 9;
		}

		super.tick();
		if (this.getWorld() instanceof ServerWorld world) {
			world.getChunkManager().unloadEntity(this);
			world.getChunkManager().loadEntity(this);
		}
		if (this.getOwner() != null) {
			float multiplier = 0.0F;
			this.setPosition(this.getOwner().getPos().x, this.getOwner().getPos().y - 0.25, this.getOwner().getPos().z);

			if (this.getOrderId() != 0) {
				multiplier += this.getOrderId() / 3.5F;
			}

			Vec3d look = this.getOwner().getRotationVec(0);
			this.setPosition(this.getPos().add(look.multiply(multiplier, multiplier, -multiplier)));
		}
	}

	@Override
	public boolean isCollidable() {
        return true;
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		if (nbt.contains("Owner")) {
			this.setOwner(String.valueOf(nbt.getString("Owner")));
		}
		if (nbt.getInt("Id").isPresent()) this.id = nbt.getInt("Id").get();
    }

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		if (this.ownerId != null) {
			nbt.putString("Owner", this.ownerId.toString());
		}
		nbt.putInt("Id", this.id);
	}

	@Override
	public boolean canHit() {
		return true;
	}

	@Override
	public final boolean damage(ServerWorld world, DamageSource source, float amount) {
		if (this.owner != null && this.owner instanceof MyriadShovelEntity entity) {
			entity.canPickup = true;
		}
		return false;
	}

    @Override
	public boolean shouldSave() {
		return true;
	}

	@Override
	public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
		return true;
	}

	@Nullable
	@Override
	public Entity getOwner() {
		if (this.owner != null && !this.owner.isRemoved()) {
			return this.owner;
		} else if (this.ownerId != null) {
			this.owner = this.getEntity(this.ownerId);
			return this.owner;
		} else {
			return null;
		}
	}
}