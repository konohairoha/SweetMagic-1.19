package sweetmagic.init.entity.block;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import sweetmagic.init.EntityInit;
import sweetmagic.util.WorldHelper;

public class Chair extends Entity {

	public Chair(Level world) {
		super(EntityInit.chair, world);
		this.noPhysics = true;
	}

	public Chair(EntityType<? extends Chair> enType, Level world) {
		super(enType, world);
	}

	private Chair(Level world, BlockPos pos, double yOffset, Direction face) {
		this(world);
		this.setPos(pos.getX() + 0.5D, pos.getY() + yOffset, pos.getZ() + 0.5D);
		this.setRot(face.getOpposite().toYRot(), 0F);
	}

	@Override
	public void tick() {
		super.tick();
		Level world = this.getLevel();
		if (world.isClientSide()) { return; }

		BlockPos pos = this.blockPosition();
		if (!this.getPassengers().isEmpty() && !world.isEmptyBlock(pos)) { return; }

		this.remove(RemovalReason.DISCARDED);
		world.updateNeighbourForOutputSignal(pos, world.getBlockState(pos).getBlock());
	}

	@Override
	protected void defineSynchedData() { }

	@Override
	protected void readAdditionalSaveData(CompoundTag tags) { }

	@Override
	protected void addAdditionalSaveData(CompoundTag tags) { }

	@Override
	public double getPassengersRidingOffset() {
		return 0D;
	}

	@Override
	protected boolean canRide(Entity entity) {
		return true;
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	public static void create(Level world, BlockPos pos, double yOffset, Player player, Direction face) {
		if (world.isClientSide()) { return; }

		List<Chair> seats = WorldHelper.getEntityList(player, Chair.class, new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1D, pos.getY() + 1D, pos.getZ() + 1D));
		if (!seats.isEmpty()) { return; }

		Chair seat = new Chair(world, pos, yOffset, face);
		world.addFreshEntity(seat);
		player.startRiding(seat, false);
	}

	@Override
	public Vec3 getDismountLocationForPassenger(LivingEntity entity) {

		Direction face = this.getDirection();
		Direction[] faceArray = { face, face.getClockWise(), face.getCounterClockWise(), face.getOpposite() };

		for (Direction dir : faceArray) {
			Vec3 vec = DismountHelper.findSafeDismountLocation(entity.getType(), this.getLevel(), this.blockPosition().relative(dir), false);
			if (vec != null) {
				return vec.add(0D, 0.25D, 0D);
			}
		}
		return super.getDismountLocationForPassenger(entity);
	}

	@Override
	protected void addPassenger(Entity entity) {
		super.addPassenger(entity);
		entity.setYRot(this.getYRot() - 180F);
	}

	@Override
	public void onPassengerTurned(Entity entity) { }
}
