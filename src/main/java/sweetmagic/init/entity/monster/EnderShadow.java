package sweetmagic.init.entity.monster;

import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
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
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.util.SMDamage;

public class EnderShadow extends AbstractSMMob {

	private int recastTime = 0;
	private int tickTime = 0;
	private int maxTickTime = 0;
	private static final EntityDataAccessor<Integer> SUMMON_COUNT = ISMMob.setData(EnderShadow.class, INT);

	public EnderShadow(Level world) {
		super(EntityInit.enderShadow, world);
	}

	public EnderShadow(EntityType<EnderShadow> enType, Level world) {
		super(enType, world);
		this.xpReward = 200;
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SUMMON_COUNT, 0);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 150D)
				.add(Attributes.MOVEMENT_SPEED, 0.3D)
				.add(Attributes.ATTACK_DAMAGE, 4D)
				.add(Attributes.ARMOR, 4D)
				.add(Attributes.FOLLOW_RANGE, 32D);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.5D, false));
		this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
		this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Warden.class, 8.0F));
		this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Warden.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Raider.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {
		Entity attacker = src.getEntity();
		Entity attackEntity = src.getDirectEntity();
		if (attacker != null && attacker instanceof ISMMob) {
			this.teleport();
			return false;
		}

		if (this.notMagicDamage(attacker, attackEntity)) {
			attacker.hurt(SMDamage.magicDamage, amount);
			attacker.invulnerableTime = 0;
			return false;
		}

		// ダメージ倍処理
		if (!this.isLeader(this)) {
			amount = this.getBossDamageAmount(this.level, this.defTime , src, amount, 10F);
			this.defTime = 2;
		}

		return super.hurt(src, amount);
	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENDERMAN_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource src) {
		return SoundEvents.ENDERMAN_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENDERMAN_DEATH;
	}

	protected void tickDeath() {
		super.tickDeath();
		List<EnderShadowMirage> entityList = this.getEntityList(EnderShadowMirage.class, this, e -> e.isAlive() && e.is(this), 256D);
		entityList.forEach(e -> e.setHealth(0F));
	}

	public boolean doHurtTarget(Entity entity) {
		boolean flag = super.doHurtTarget(entity);

		if (flag && entity instanceof Warden target) {
			entity.hurt(this.getSRC(), 20F);
		}

		entity.invulnerableTime = 0;
		this.teleport();
		return flag;
	}

	protected void customServerAiStep() {
		super.customServerAiStep();
		LivingEntity target = this.getTarget();
		if (target == null || this.recastTime-- > 0 || this.isLeader(this)) { return; }

		if (this.maxTickTime <= 0) {
			this.maxTickTime = this.rand.nextInt(150) + 100;
		}

		if (this.tickTime++ >= this.maxTickTime) {

			int summonCount = this.getSummon();

			if (summonCount < 3) {
				this.teleport(target);

				EnderShadowMirage entity = new EnderShadowMirage(this.level);
				entity.setPos(this.getX() + 0.5D, this.getY() + 0.5D, this.getZ() + 0.5D);
				entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getMaxHealth() / 4F);
				entity.setHealth(entity.getMaxHealth());
				this.level.addFreshEntity(entity);
				entity.teleport();
				entity.spawnAnim();
				entity.setUUID(this.getUUID());

				this.setSummon(summonCount + 1);
				this.recastTime = 100;
				this.maxTickTime = this.rand.nextInt(150) + 100;
			}

			else {
				this.maxTickTime = Integer.MAX_VALUE;
			}
		}
	}

	protected boolean teleport() {
		if (!this.level.isClientSide() && this.isAlive()) {
			double d0 = this.getX() + (this.rand.nextDouble() - 0.5D) * 32D;
			double d1 = this.getY() + (double) (this.rand.nextInt(32) - 16);
			double d2 = this.getZ() + (this.rand.nextDouble() - 0.5D) * 32D;
			return this.teleport(d0, d1, d2);
		}

		return false;
	}

	protected boolean teleport(LivingEntity target) {
		if (!this.level.isClientSide() && this.isAlive()) {
			double d0 = target.getX() + (this.rand.nextDouble() - 0.5D) * 5D;
			double d1 = target.getY() + (double) (this.rand.nextInt(8) - 4);
			double d2 = target.getZ() + (this.rand.nextDouble() - 0.5D) * 5D;
			return this.teleport(d0, d1, d2);
		}

		return false;
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putInt("isSummon", this.getSummon());
		tags.putInt("maxTickTime", this.maxTickTime);
		tags.putInt("recastTime", this.recastTime);
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setSummon(tags.getInt("isSummon"));
		this.maxTickTime = tags.getInt("maxTickTime");
		this.recastTime = tags.getInt("recastTime");
	}

	public int getSummon() {
		return this.entityData.get(SUMMON_COUNT);
	}

	public void setSummon(int summonCount) {
		this.entityData.set(SUMMON_COUNT, summonCount);
	}
}
