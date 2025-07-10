package sweetmagic.init.entity.projectile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.TagInit;

public class ElectricMagicShot extends AbstractMagicShot {

	public boolean isRangeAttack = false;
	public List<LivingEntity> targetList = new ArrayList<>();
	private static final EntityDataAccessor<Integer> TICKTIME = setEntityData(ISMMob.INT);
	private static final EntityDataAccessor<Integer> MAX_COUNT = setEntityData(ISMMob.INT);

	public ElectricMagicShot(EntityType<? extends ElectricMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public ElectricMagicShot(double x, double y, double z, Level world) {
		this(EntityInit.electricMagic, world);
		this.setPos(x, y, z);
	}

	public ElectricMagicShot(Level world, LivingEntity entity, WandInfo info) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(info);
	}

	public ElectricMagicShot(Level world, LivingEntity entity) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = ItemStack.EMPTY;
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.define(TICKTIME, 0);
		this.define(MAX_COUNT, 0);
	}

	public void addTick() {
		this.set(TICKTIME, this.getTick() + 1);
	}

	public int getTick() {
		return this.get(TICKTIME);
	}

	public void setMaxCount(int maxCount) {
		this.set(MAX_COUNT, maxCount);
	}

	public int getMaxCount() {
		return this.get(MAX_COUNT);
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putInt("maxCount", this.getMaxCount());
	}

	// NBTの読み込み
	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setMaxCount(tags.getInt("maxCount"));
	}

	public void tick() {
		super.tick();

		if (this.isClient()) {
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

		entityList = entityList.stream().sorted((s1, s2) -> this.sortEntity(this, s1, s2)).toList();

		dame *= 1F + (this.isInWater() ? 0.5F : 0F);
		boolean isTier3 = this.getData() >= 2;
		int addAttack = this.getAddAttack();

		for(int i = 0; i < Math.min(this.getMaxCount(), entityList.size()); i++) {

			LivingEntity entity = entityList.get(i);
			this.attackDamage(entity, dame, false);

			if (isTier3) {
				this.addPotion(entity, PotionInit.lightning_wind_vulnerable, 1200, this.getData() - 2);
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
	}

	// えんちちーソート
	public int sortEntity(Entity mob, Entity entity1, Entity entity2) {
		if (entity1 == null || entity2 == null) { return 0; }

		boolean isBoss1 = entity1.getType().is(TagInit.BOSS);
		boolean isBoss2 = entity2.getType().is(TagInit.BOSS);
		if (isBoss1 && isBoss2) { return 0; }
		if (isBoss1) { return -1; }
		if (isBoss2) { return 1; }

		double distance1 = mob.distanceToSqr(entity1);
		double distance2 = mob.distanceToSqr(entity2);

		if (distance1 > distance2) { return 1; }
		else if (distance1 < distance2) { return -1; }

		return 0;
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
