package sweetmagic.init.entity.block;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ItemInit;

public class Cushion extends Entity implements ISMTip {

	private static final EntityDataAccessor<ItemStack> STACK = SynchedEntityData.defineId(Cushion.class, EntityDataSerializers.ITEM_STACK);

	public Cushion(Level world) {
		super(EntityInit.cushion, world);
	}

	public Cushion(EntityType<? extends Cushion> enType, Level world) {
		super(enType, world);
		this.noPhysics = false;
	}

	public Cushion(Level world, ItemStack stack) {
		this(world);
		this.setStack(stack);
	}

	@Override
	protected Entity.MovementEmission getMovementEmission() {
		return Entity.MovementEmission.EVENTS;
	}

	@Override
	protected void defineSynchedData() {
		this.getEntityData().define(STACK, ItemStack.EMPTY);
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean isPickable() {
		return !this.isRemoved();
	}

	public ItemStack getStack() {
		ItemStack stack = this.getEntityData().get(STACK);
		return stack.isEmpty() ? new ItemStack(ItemInit.cushion_s) : stack;
	}

	public void setStack(ItemStack stack) {
		this.getEntityData().set(STACK, stack);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tags) {
		this.setStack(ItemStack.of(tags.getCompound("stack")));
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tags) {
		tags.put("stack", this.getStack().save(new CompoundTag()));
	}

	@Override
	public void tick() {
		super.tick();
		float f = this.getEyeHeight() - 0.03F;
		this.move(MoverType.SELF, this.getDeltaMovement());

		if (this.isInWater() && this.getFluidHeight(FluidTags.WATER) > f) {
			this.setDeltaMovement(this.getDeltaMovement().scale(0D).add(0D, 0.0075D, 0D));
		}

		else if (!this.onGround || this.getDeltaMovement().length() > 0.0001F) {

			if (!this.isInWater() && this.getFluidHeight(FluidTags.WATER) <= f + 0.02F) {
				this.setDeltaMovement(this.getDeltaMovement().scale(0.8D).add(0D, -0.05D, 0D));
			}

			else {
				this.setDeltaMovement(this.getDeltaMovement().scale(0D));
			}
		}

		if (this.onGround) {
			List<Entity> list = this.getLevel().getEntities(this, this.getBoundingBox().inflate(0.02D, 0D, 0.02D), EntitySelector.pushableBy(this));
			if (list.isEmpty()) { return; }

			this.move(MoverType.SELF, this.getDeltaMovement());
			for (int l = 0; l < list.size(); ++l) {
				Entity entity = list.get(l);
				double d0 = entity.xo - this.xo;
				double d1 = entity.zo - this.zo;
				double rate = 0.1D * Math.abs(0.25D - d0 + d1);
				Vec3 vec3 = new Vec3(-d0 * rate, 0D, -d1 * rate);
				this.setDeltaMovement(vec3);
				this.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)));
				this.setXRot((float) (Mth.atan2(vec3.y, d0) * (double) (180F / (float) Math.PI)));
			}
		}
	}

	public final InteractionResult interact(Player player, InteractionHand hand) {

		if(player.isShiftKeyDown()) {
			this.dropItem(player.blockPosition());
		}

		else {
			player.startRiding(this);
		}

		return InteractionResult.SUCCESS;
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {
		Entity attacker = src.getEntity();
		if (attacker == null || !(attacker instanceof Player)) { return false; }
		this.dropItem(this.blockPosition());
		return true;
	}

	public void dropItem(BlockPos pos) {
		if(this.getLevel().isClientSide()) { return; }
		this.getLevel().addFreshEntity(new ItemEntity(this.getLevel(), pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, this.getStack()));
		this.discard();
	}

	public Component getName() {
		return this.getStack().getDisplayName();
	}

	@Override
	public double getPassengersRidingOffset() {
		return -0.2D;
	}

	@Override
	protected boolean canRide(Entity entity) {
		return true;
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void addPassenger(Entity entity) {
		super.addPassenger(entity);
		entity.setYRot(this.getYRot() - 180F);
	}

	@Override
	public void onPassengerTurned(Entity entity) { }
}
