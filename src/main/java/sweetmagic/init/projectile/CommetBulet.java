package sweetmagic.init.entity.projectile;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;

public class CommetBulet extends AbstractMagicShot {

	private static final EntityDataAccessor<Integer> TARGET = setEntityData(ISMMob.INT);
	private static final EntityDataAccessor<Boolean> CHARGE = setEntityData(ISMMob.BOOLEAN);
	private Direction moveFace;
	private int step;
	private double targetX;
	private double targetY;
	private double targetZ;

	public CommetBulet(EntityType<? extends AbstractMagicShot> entityType, Level world) {
		super(entityType, world);
		this.setBlockPenetration(true);
		this.setMaxLifeTime(200);
	}

	public CommetBulet(double x, double y, double z, Level world) {
		this(EntityInit.commetBulet, world);
		this.setPos(x, y, z);
	}

	public CommetBulet(Level world, LivingEntity entity) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = ItemStack.EMPTY;
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.define(TARGET, -1);
		this.define(CHARGE, false);
	}

	// ブロック着弾
	protected void onHitBlock(BlockHitResult result) {
		this.rangeAttack(null, 0.75D);
		super.onHitBlock(result);
	}

	// えんちちーに当たった時の処理
	protected void entityHit(LivingEntity living) {
		this.rangeAttack(living, 1.5D);
	}

	public void rangeAttack(LivingEntity living, double range) {

		this.playSound(SoundEvents.GENERIC_EXPLODE, 2F, 1F / (this.rand.nextFloat() * 0.2F + 0.9F));
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.isTarget(), range);
		entityList.forEach(e -> this.attackDamage(e, 5F, false));

		if (this.getLevel() instanceof ServerLevel sever) {
			sever.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY() + 0.5D, this.getZ(), 2, 0D, 0D, 0D, 0D);
		}
	}

	public void tick() {

		super.tick();

		if (this.getCharge()) {
			this.ownerMove();
			return;
		}

		if (!this.isClient()) {
			this.updateTarget();
		}

		Entity target = this.getTarget();
		if (target != null) {
			this.moveComet(target);
		}

		this.spawnParticleSever();
	}

	public void ownerMove() {
		Entity owner = this.getOwner();
		if(owner == null) { return; }
		this.checkOwner();

		if (this.step-- <= 0) {
			float size = 0.015F;
			Vec3 vec3 = this.getDeltaMovement();
			Vec3 newVec = new Vec3(vec3.x + this.getRandFloat(size), vec3.y + this.getRandFloat(size), vec3.z + this.getRandFloat(size));

			if(this.distanceTo(owner) > 4D) {
				float range = 0.1F;
				newVec = new Vec3((owner.xo - this.xo + this.getRandFloat(range)) * size, (owner.yo + 3.5D - this.yo + this.getRandFloat(range)) * size, (owner.zo - this.zo + this.getRandFloat(range)) * size);
			}

			this.setDeltaMovement(newVec);
			this.step = 3;
		}

		this.lifeTime = 0;
	}

	public void moveComet(Entity target) {

		if (this.step >= 0 && this.step-- == 0) {
			this.selectNextMoveDirection(target, this.moveFace == null ? null : this.moveFace.getAxis());
		}

		if (this.moveFace == null) { return; }

		BlockPos pos = this.blockPosition();
		Direction.Axis face = this.moveFace.getAxis();
		if (this.level.loadedAndEntityCanStandOn(pos.relative(this.moveFace), this)) {
			this.selectNextMoveDirection(target, face);
		}

		else {
			BlockPos pos1 = target.blockPosition();
			if (face == Direction.Axis.X && pos.getX() == pos1.getX() ||
				face == Direction.Axis.Z && pos.getZ() == pos1.getZ() || face == Direction.Axis.Y && pos.getY() == pos1.getY()) {
				this.selectNextMoveDirection(target, face);
			}
		}

        this.targetX = Mth.clamp(this.targetX * 1.1D, -1.25D, 1.25D);
        this.targetY = Mth.clamp(this.targetY * 1.1D, -1.25D, 1.25D);
        this.targetZ = Mth.clamp(this.targetZ * 1.1D, -1.25D, 1.25D);
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.add((this.targetX - vec3.x) * 1.25D, (this.targetY - vec3.y) * 1.25D, (this.targetZ - vec3.z) * 1.25D));
	}

	private void updateTarget() {

		Entity target = this.getTarget();

		if (target != null && !target.isAlive()) {
			target = null;
			this.setTarget(null);
		}

		if (target != null) { return; }

		Entity owner = this.getOwner();
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, e -> this.canTargetEffect(e, owner) && e.isAlive(), 32D);
		if (entityList.isEmpty()) { return; }

		double distance = 33D;

		for (LivingEntity entity : entityList) {

			if (distance >= entity.distanceTo(this)) {
				this.setTarget(entity);
			}
		}
	}

	private void selectNextMoveDirection(Entity target, Direction.Axis axis) {

		double d0 = (double) target.getBbHeight() * 0.5D;
		BlockPos pos = new BlockPos(target.getX(), target.getY() + d0, target.getZ());
		double d1 = (double) pos.getX() + 0.5D;
		double d2 = (double) pos.getY() + d0;
		double d3 = (double) pos.getZ() + 0.5D;
		RandomSource rand = this.getRandom();
		Level world = this.getLevel();
		Direction face = null;

		if (!pos.closerToCenterThan(this.position(), 2D)) {

			BlockPos pos1 = this.blockPosition();
			List<Direction> facelist = Lists.newArrayList();

			if (axis != Direction.Axis.X) {

				if (pos1.getX() < pos.getX() && world.isEmptyBlock(pos1.east())) {
					facelist.add(Direction.EAST);
				}

				else if (pos1.getX() > pos.getX() && world.isEmptyBlock(pos1.west())) {
					facelist.add(Direction.WEST);
				}
			}

			if (axis != Direction.Axis.Y) {

				if (pos1.getY() < pos.getY() && world.isEmptyBlock(pos1.above())) {
					facelist.add(Direction.UP);
				}

				else if (pos1.getY() > pos.getY() && world.isEmptyBlock(pos1.below())) {
					facelist.add(Direction.DOWN);
				}
			}

			if (axis != Direction.Axis.Z) {

				if (pos1.getZ() < pos.getZ() && world.isEmptyBlock(pos1.south())) {
					facelist.add(Direction.SOUTH);
				}

				else if (pos1.getZ() > pos.getZ() && world.isEmptyBlock(pos1.north())) {
					facelist.add(Direction.NORTH);
				}
			}

			face = Direction.getRandom(rand);

			if (facelist.isEmpty()) {
				for (int i = 5; !world.isEmptyBlock(pos1.relative(face)) && i > 0; --i) {
					face = Direction.getRandom(rand);
				}
			}

			else {
				face = facelist.get(rand.nextInt(facelist.size()));
			}

			d1 = this.getX() + (double) face.getStepX();
			d2 = this.getY() + (double) face.getStepY();
			d3 = this.getZ() + (double) face.getStepZ();
		}

		this.setFace(face);
		double d6 = d1 - this.getX();
		double d7 = d2 - this.getY();
		double d4 = d3 - this.getZ();
		double d5 = Math.sqrt(d6 * d6 + d7 * d7 + d4 * d4);

		if (d5 == 0D) {
			this.targetX = 0D;
			this.targetY = 0D;
			this.targetZ = 0D;
		}

		else {
			this.targetX = d6 / d5 * 0.15D;
			this.targetY = d7 / d5 * 0.15D;
			this.targetZ = d4 / d5 * 0.15D;
		}

		this.hasImpulse = true;
		this.step = 10 + rand.nextInt(4) * 6;
	}

	@Nullable
	private Entity getTarget() {
		return this.getLevel().getEntity(this.get(TARGET));
	}

	public void setTarget(Entity entity) {
		this.set(TARGET, entity == null ? -1 : entity.getId());
	}

	public void setCharge(boolean isCharge) {
		this.set(CHARGE, isCharge);
	}

	public boolean getCharge() {
		return this.get(CHARGE);
	}

	@Nullable
	private Direction getFace() {
		return this.moveFace;
	}

	private void setFace(Direction face) {
		this.moveFace = face;
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);

		if (this.moveFace != null) {
			tags.putInt("Dir", this.moveFace.get3DDataValue());
		}

		tags.putInt("Steps", this.step);
		tags.putDouble("TXD", this.targetX);
		tags.putDouble("TYD", this.targetY);
		tags.putDouble("TZD", this.targetZ);
		tags.putBoolean("isCharge", this.getCharge());
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.step = tags.getInt("Steps");
		this.targetX = tags.getDouble("TXD");
		this.targetY = tags.getDouble("TYD");
		this.targetZ = tags.getDouble("TZD");
		this.setCharge(tags.getBoolean("isCharge"));
		if (tags.contains("Dir", 99)) {
			this.moveFace = Direction.from3DDataValue(tags.getInt("Dir"));
		}
	}

	// パーティクルスポーン
	protected void spawnParticleSever() {
		if (this.tickCount < 3 || this.tickCount % 2 != 0 || !(this.getLevel() instanceof ServerLevel sever)) { return; }
		sever.sendParticles(ParticleInit.ORB, this.getX(), this.getY(), this.getZ(), 0, 1F, 231F / 255F, 113F / 255F, 1F);
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.SHINE;
	}
}
