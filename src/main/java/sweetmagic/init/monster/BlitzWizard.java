package sweetmagic.init.entity.monster;

import com.mojang.math.Vector3f;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.api.ientity.IWitch;
import sweetmagic.init.EntityInit;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.entity.projectile.BloodMagicShot;
import sweetmagic.init.entity.projectile.ElectricMagicShot;
import sweetmagic.init.entity.projectile.ExplosionMagicShot;
import sweetmagic.init.entity.projectile.GravityMagicShot;

public class BlitzWizard extends AbstractSMMob implements IWitch {

	private int recastTime = 0;
	private static final int RAND_RECASTTIME = 60;
	public AnimationState magicAttackAnim = new AnimationState();
	private static final EntityDataAccessor<Boolean> TARGET = SynchedEntityData.defineId(BlitzWizard.class, BOOLEAN);

	public BlitzWizard(Level world) {
		super(EntityInit.blitzWizard, world);
	}

	public BlitzWizard(EntityType<BlitzWizard> enType, Level world) {
		super(enType, world);
		this.xpReward = 150;
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.define(TARGET, false);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1D));
		this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1D, 0F));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8F));
		this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Warden.class, 8F));
		this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Warden.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Raider.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 250D)
				.add(Attributes.MOVEMENT_SPEED, 0.25D)
				.add(Attributes.ATTACK_DAMAGE, 6D)
				.add(Attributes.ARMOR, 16D)
				.add(Attributes.FOLLOW_RANGE, 32D);
	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.WITCH_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource src) {
		return SoundEvents.WITCH_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.WITCH_DEATH;
	}

	public void setTarget(boolean isTarget) {
		this.set(TARGET, isTarget);
	}

	public boolean isTarget() {
		return this.get(TARGET);
	}

	public AnimationState getAnimaState() {
		return this.magicAttackAnim;
	}

	public boolean isCharge() {
		return false;
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putBoolean("is_target", this.isTarget());
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.set(TARGET, tags.getBoolean("is_target"));
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {
		Entity attacker = src.getEntity();
		Entity attackEntity = src.getDirectEntity();
		if (attacker != null && attacker instanceof ISMMob) { return false; }

		// 魔法攻撃以外ならダメージ減少
		if (this.notMagicDamage(attacker, attackEntity)) {
			amount *= 0.25F;
		}

		// ダメージ倍処理
		if (!this.isLeader(this)) {
			amount = this.getBossDamageAmount(this.getLevel(), this.defTime , src, amount, 12F);
			this.defTime = 1;
		}

		return super.hurt(src, amount);
	}

	public void tick() {
		super.tick();

		if (!this.isClient() && this.tickCount % 10 == 0) {
			this.set(TARGET, this.getTarget() != null);
		}
	}

	protected void customServerAiStep() {
		super.customServerAiStep();
		LivingEntity target = this.getTarget();
		if (target == null) { return; }

		this.getLookControl().setLookAt(target, 10F, 10F);
		if (this.recastTime-- > 0) { return; }

		this.recastTime = this.rand.nextInt(RAND_RECASTTIME) + RAND_RECASTTIME;
		int randValue = this.rand.nextInt(4);
		boolean isWarden = target instanceof Warden;
		float dama = isWarden ? 45F : 7.5F;
		int level = isWarden ? 36 : 10;

		for (int i = 0; i < 3; i++) {
			AbstractMagicShot entity = this.getMagicShot(target, isWarden, randValue);
			double d0 = target.getX() - this.getX();
			double d1 = target.getZ() - this.getZ();
			double d3 = target.getY(0.3333333333333333D) - this.getY() - 1D;
			Vector3f vec = this.getShotVector(this, new Vec3(d0, d3, d1), -20F + i * 20F);
			entity.shoot(vec.x(), vec.y(), vec.z(), randValue == 2 ? 1F : 1.75F, 1);
			entity.setHitDead(false);
			entity.setData(1);
			entity.setWandLevel(level);
			entity.setAddDamage(entity.getAddDamage() + dama);
			entity.setRange(2.5F);
			this.addEntity(entity);
		}

		this.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);
	}

	public AbstractMagicShot getMagicShot(LivingEntity target, boolean isWarden, int randValue) {

		AbstractMagicShot entity = null;

		switch (randValue) {
		case 1:
			entity = new BloodMagicShot(this.getLevel(), this);
			break;
		case 2:
			entity = new ElectricMagicShot(this.getLevel(), this);
			entity.setHitDead(false);
			((ElectricMagicShot) entity).isRangeAttack = true;
			break;
		case 3:
			entity = new ExplosionMagicShot(this.getLevel(), this);
			break;
		default:
			entity = new GravityMagicShot(this.getLevel(), this);
			break;
		}

		return entity;
	}

	// 低ランクかどうか
	public boolean isLowRank() {
		return false;
	}
}
