package sweetmagic.init.entity.monster;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.PotionInit;
import sweetmagic.util.SMDamage;

public class ElectricCube extends Slime implements ISMMob {

	private static final EntityDataAccessor<Integer> ID_SIZE = SynchedEntityData.defineId(Slime.class, EntityDataSerializers.INT);
	public static final int MIN_SIZE = 1;
	public static final int MAX_SIZE = 127;

	public ElectricCube(Level world) {
		super(EntityInit.electricCube, world);
	}

	public ElectricCube(EntityType<? extends Slime> enType, Level world) {
		super(enType, world);
		this.xpReward = 15;
	}

	public EntityType<? extends Slime> getType() {
		return (EntityType<? extends ElectricCube>) super.getType();
	}

	public void refreshInfo() {
		this.reapplyPosition();
		this.refreshDimensions();
	}

	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Warden.class, true));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 30D)
				.add(Attributes.MOVEMENT_SPEED, 0.45D)
				.add(Attributes.ATTACK_DAMAGE, 4D)
				.add(Attributes.FOLLOW_RANGE, 24D);
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {

		Entity attacker = src.getEntity();
		if ( attacker != null && attacker instanceof ISMMob) { return false; }

		// ダメージ倍処理
		amount = this.getDamageAmount(this.level , src, amount, 1F);
		return super.hurt(src, amount);
	}

	public void tick() {
		super.tick();
		this.fallDistance = 0F;
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ID_SIZE, 1);
	}

	@VisibleForTesting
	public void setSize(int x, boolean y) {
		int i = Mth.clamp(x, 1, 255);
		this.setSize(i);
		this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double) (i * i));
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2D + 0.1D * (double) i);
		this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue( this.getAttack(i) * ( this.isHard(this.level) ? 1.25D : 1D ) );
		if (y) {
			this.setHealth(this.getMaxHealth());
		}

		this.xpReward = i;
	}

	public double getAttack (int i) {
		switch (i) {
		case 2:  return 2F;
		case 4:  return 3F;
		default: return 1F;
		}
	}

	public void setSize (int size) {
		this.entityData.set(ID_SIZE, size);
		this.refreshInfo();
	}

	public void push(Entity entity) {
		super.push(entity);
		if (entity instanceof Warden living) {
			entity.hurt(SMDamage.MAGIC, 13F * this.entityData.get(ID_SIZE));
			living.invulnerableTime = 0;
		}
	}

	public int getSize() {
		return this.entityData.get(ID_SIZE);
	}

	public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
		if (ID_SIZE.equals(data)) {
			this.refreshDimensions();
			this.setYRot(this.yHeadRot);
			this.yBodyRot = this.yHeadRot;
			if (this.isInWater() && this.random.nextInt(20) == 0) {
				this.doWaterSplashEffect();
			}
		}

		super.onSyncedDataUpdated(data);
	}

	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance dif, MobSpawnType spawn, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		data = super.finalizeSpawn(world, dif, spawn, data, tag);
		this.initMobData(this, dif);
		return data;
	}

	public void remove(Entity.RemovalReason remova) {

		if (this.hasEffect(PotionInit.resistance_blow)) {
			this.setRemoved(remova);
			this.invalidateCaps();
			return;
		}

		super.remove(remova);
	}
}
