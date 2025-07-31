package net.hollowed.antique.entities.parts;

import net.hollowed.antique.entities.MyriadShovelEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class MyriadShovelPart extends Entity implements Ownable {

	private Entity owner;
	private UUID ownerUuid;
	private boolean leftOwner = false;

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

	@Override
	public void tick() {
		if (this.getOwner() == null && this.getWorld() instanceof ServerWorld) {
			this.discard();
			return;
		}

		if (this.age >= 20) {
			this.age -= 9;
		}

		if (!this.leftOwner) {
			this.leftOwner = this.shouldLeaveOwner();
		}

		super.tick();
		if (this.getWorld() instanceof ServerWorld world) {
			world.getChunkManager().unloadEntity(this);
			world.getChunkManager().loadEntity(this);
		}

		if (this.getOwner() instanceof MyriadShovelEntity shovel && shovel.canPickup) {
			this.discard();
			return;
		}

		if (this.getOwner() != null) {
			float multiplier = 0.25F;
			this.setPosition(Objects.requireNonNull(this.getOwner()).getPos().x, this.getOwner().getPos().y - 0.25, this.getOwner().getPos().z);
			multiplier += (this.getOrderId() / 3.5F) - 0.2F;
			Vec3d look = this.getOwner().getRotationVec(0);
			this.setPosition(this.getPos().add(look.multiply(multiplier, multiplier, -multiplier)));
		}
	}

	@Override
	public boolean isCollidable(@Nullable Entity entity) {
        return true;
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
	}

	@Override
	protected void readCustomData(ReadView view) {
		this.setOwner(view.read("Owner", Uuids.INT_STREAM_CODEC).orElse(null));
		this.leftOwner = view.getBoolean("LeftOwner", false);
		this.id = view.getInt("Id", 0);
	}

	@Override
	protected void writeCustomData(WriteView view) {
		view.putNullable("Owner", Uuids.INT_STREAM_CODEC, this.ownerUuid);
		if (this.leftOwner) {
			view.putBoolean("LeftOwner", true);
		}
		view.putInt("Id", this.id);
	}

	@Override
	public boolean canHit() {
		return true;
	}

	@Override
	public final boolean damage(ServerWorld world, DamageSource source, float amount) {
		if (this.owner != null && this.owner instanceof MyriadShovelEntity entity) {
			entity.canPickup = true;
			entity.getDataTracker().set(MyriadShovelEntity.LOYALTY, (byte) 3);
			entity.returnTimer = 1;
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

	public void setOwner(@Nullable Entity entity) {
		if (entity != null) {
			this.ownerUuid = entity.getUuid();
			this.owner = entity;
		}
	}

	@Nullable
	@Override
	public Entity getOwner() {
		if (this.owner != null && !this.owner.isRemoved()) {
			return this.owner;
		} else if (this.ownerUuid != null) {
			this.owner = this.getEntity(this.ownerUuid);
			return this.owner;
		} else {
			return null;
		}
	}

	protected void setOwner(@Nullable UUID ownerUuid) {
		if (!Objects.equals(this.ownerUuid, ownerUuid)) {
			this.ownerUuid = ownerUuid;
			this.owner = ownerUuid != null ? this.getEntity(ownerUuid) : null;
		}
	}

	private boolean shouldLeaveOwner() {
		Entity entity = this.getOwner();
		if (entity != null) {
			Box box = this.getBoundingBox().stretch(this.getVelocity()).expand(1.0);
			return entity.getRootVehicle().streamSelfAndPassengers().filter(EntityPredicates.CAN_HIT).noneMatch(entityx -> box.intersects(entityx.getBoundingBox()));
		} else {
			return true;
		}
	}
}