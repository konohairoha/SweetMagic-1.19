package sweetmagic.init.entity.projectile;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;

public class ElectricSphere extends AbstractMagicShot {

	private static final EntityDataAccessor<Integer> COUNT = setEntityData(ISMMob.INT);

	public ElectricSphere(EntityType<? extends AbstractMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public ElectricSphere(double x, double y, double z, Level world) {
		this(EntityInit.electricSphere, world);
		this.setPos(x, y, z);
	}

	public ElectricSphere(Level world, LivingEntity entity) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = ItemStack.EMPTY;
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.define(COUNT, 0);
	}

	public void setCount(int count) {
		this.set(COUNT, count);
	}

	public int getCount() {
		return this.get(COUNT);
	}

	public void tick() {
		super.tick();

		if (!this.inGround) {
			Vec3 vec = this.getDeltaMovement();
			float down = (float) (this.getCount() == 2 ? 0.075F : 0.015F);
			this.setDeltaMovement(new Vec3(vec.x, vec.y - down, vec.z));
		}
	}

	// えんちちーに当たった時の処理
	protected void entityHit(LivingEntity target) {
		double range = this.getRange() / 0.67D;
		float dame = 0.5F + 0.67F * this.getWandLevel();
		this.rangeAttack(target.blockPosition(), dame, range);
	}

	// ブロック着弾
	protected void onHitBlock(BlockHitResult result) {

		if (this.isClient()) {
			this.setHitDead(false);
		}

		this.setLifeTime(0);
		double range = this.getRange();
		float dame = 0.5F + 0.5F * this.getWandLevel();

		if (this.getCount() > 0) {
			this.setCount(this.getCount() - 1);
			Vec3 look = this.getDeltaMovement();
			Vec3 dest = this.getCount() == 1 ? new Vec3(look.x * 0.25F, 0.1F, look.z * 0.25F) : new Vec3(look.x, 0.1F, look.z);
			this.setDeltaMovement(dest);
			this.playSound(SoundEvents.SLIME_ATTACK, 0.5F, 1F);
			float rate = 0.33F + (1 - this.getCount()) * 0.34F;
			this.rangeAttack(result.getBlockPos().above(), dame, (float) (range * rate));
		}

		else {

			this.rangeAttack(result.getBlockPos().above(), dame, (float) (range * 2F));
			this.playSound(SoundEvents.SLIME_ATTACK, 0.5F, 1F);
			this.discard();
		}
	}

	public void rangeAttack(BlockPos bPos, float dame, double range) {
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.isTarget(), range);
		entityList.forEach(e -> this.attackDamage(e, dame, false));
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.LIGHTNING;
	}
}
