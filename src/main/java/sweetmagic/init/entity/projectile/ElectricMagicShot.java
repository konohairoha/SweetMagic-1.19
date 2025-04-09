package sweetmagic.init.entity.projectile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.PotionInit;
import sweetmagic.util.SMDebug;

public class ElectricMagicShot extends AbstractMagicShot {

	public boolean isRangeAttack = false;
	public List<LivingEntity> targetList = new ArrayList<>();
	private static final EntityDataAccessor<Integer> TICKTIME = SynchedEntityData.defineId(ElectricMagicShot.class, EntityDataSerializers.INT);

	public ElectricMagicShot(EntityType<? extends ElectricMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public ElectricMagicShot(double x, double y, double z, Level world) {
		this(EntityInit.electricMagic, world);
		this.setPos(x, y, z);
	}

	public ElectricMagicShot(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
	}

	public ElectricMagicShot(Level world, LivingEntity entity) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = ItemStack.EMPTY;
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(TICKTIME, 0);
	}

	public void addTick() {
		this.entityData.set(TICKTIME, this.getTick() + 1);
	}

	public int getTick() {
		return this.entityData.get(TICKTIME);
	}

	public void tick() {
		super.tick();

		if (this.level.isClientSide) {
			this.addTick();
		}

		if (this.isRangeAttack) {
			this.rangeAttack();
		}
	}

	// えんちちーに当たった時の処理
	protected void entityHit(LivingEntity living) {
		this.rangeAttack((float) this.getDamage() * this.getDamageRate() * 0.75F, this.getRange());
	}

	// ブロック着弾
	protected void onHitBlock(BlockHitResult result) {
		this.rangeAttack(this.getDamage() * this.getDamageRate() * 0.5F, this.getRange() * 0.67F);
		this.discard();
	}

	public void rangeAttack(float dame, double range) {
		if (this.isRangeAttack) { return; }

		boolean change = this.getChangeParticle();
		float size = change ? 0.15F : 1.5F;

		if (change && this.rand.nextFloat() <= 0.15F) {
			size = 0F;
		}

		this.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, size, 1F);
		this.playSound(SoundEvents.LIGHTNING_BOLT_IMPACT, 1.5F, 1F);

		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.isTarget(), range);
		if (entityList.isEmpty()) { return; }

		Entity owner = this.getOwner();
		dame *= 1F + (this.isInWater() ? 0.5F : 0F);
		boolean isTier3 = this.getData() >= 2;
		int addAttack = this.getAddAttack();

		for (LivingEntity entity : entityList) {

			if (!this.canTargetEffect(entity, owner)) { continue; }
			this.attackDamage(entity, dame, false);

			if (isTier3) {
				this.addPotion(entity, PotionInit.lightning_wind_vulnerable, 1200, 0);
			}

			this.addAttack(entity, dame, addAttack);
		}
	}

	public void rangeAttack() {

		Entity owner = this.getOwner();
		float damage = this.getDamage();
		double range = this.getRange();
		List<LivingEntity> attackList = new ArrayList<>();
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.isTarget(), range);

		for (LivingEntity entity : entityList) {
			if (!this.canTargetEffect(entity, owner) || attackList.contains(entity)) { continue; }
			if (this.getHitDead() && this.targetList.contains(entity)) { continue; }

			this.attackDamage(entity, damage, false);
			attackList.add(entity);
		}

		this.targetList.addAll(attackList);
		SMDebug.info(this.targetList);
	}

	// ダメージレートの取得
	public float getDamageRate() {
		switch (this.getData()) {
		case 0: return 1F;
		case 1: return 1.25F;
		default: return 2F;
		}
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.LIGHTNING;
	}
}
