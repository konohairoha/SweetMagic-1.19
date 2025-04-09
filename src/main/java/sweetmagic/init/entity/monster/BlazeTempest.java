package sweetmagic.init.entity.monster;

import javax.annotation.Nullable;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.ai.BlazeAttackGoal;

public class BlazeTempest extends Blaze implements ISMMob {

	private float allowHeight = 0.5F;
	private int nextTick;

	public BlazeTempest(Level world) {
		super(EntityInit.blazeTempest, world);
	}

	public BlazeTempest(EntityType<? extends Blaze> enType, Level world) {
		super(enType, world);
		this.setPathfindingMalus(BlockPathTypes.WATER, 0F);
		this.xpReward = 35;
	}

	public void refreshInfo() {
		this.reapplyPosition();
		this.refreshDimensions();
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(4, new BlazeAttackGoal(this, this.isHard(this.level), 0));
		this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
		this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Warden.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Raider.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 30D)
				.add(Attributes.MOVEMENT_SPEED, 0.225D)
				.add(Attributes.ATTACK_DAMAGE, 4D)
				.add(Attributes.FOLLOW_RANGE, 24D);
	}

	public EntityDimensions getDimensions(Pose pose) {
		float rate = 1F + 0.375F * this.getPotionLevel(this, PotionInit.leader_flag);
		return super.getDimensions(pose).scale(rate);
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {
		Entity attacker = src.getEntity();
		if (attacker != null && attacker instanceof ISMMob) { return false; }

		// ダメージ倍処理
		amount = this.getDamageAmount(this.level , src, amount, 1F);
		return super.hurt(src, amount);
	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.BLAZE_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource src) {
		return SoundEvents.BLAZE_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.BLAZE_DEATH;
	}

	public float getLightLevelDependentMagicValue() {
		return 1F;
	}

	public boolean isSensitiveToWater() {
		return false;
	}

	public void tick() {
		super.tick();

		if (this.level.isClientSide && this.getTarget() != null) {
			RandomSource rand = this.level.random;
			double x = +this.xo + (rand.nextDouble() - 0.5D);
			double y = +this.yo + (rand.nextDouble() + 0.5D);
			double z = +this.zo + (rand.nextDouble() - 0.5D);
			this.level.addParticle(ParticleTypes.SWEEP_ATTACK, x, y, z, 0D, 0D, 0D);
		}
	}

	public void aiStep() {

		if (!this.onGround && this.getDeltaMovement().y < 0.0D) {
			this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
		}

		super.aiStep();
	}

	protected void customServerAiStep() {

		--this.nextTick;
		if (this.nextTick <= 0) {
			this.nextTick = 100;
			this.allowHeight = (float) this.random.triangle(0.5D, 6.891D);
		}

		LivingEntity target = this.getTarget();

		if (target != null && target.getEyeY() > this.getEyeY() + (double) this.allowHeight) {
			Vec3 vec3 = this.getDeltaMovement();
			this.setDeltaMovement(this.getDeltaMovement().add(0.0D, ((double) 0.3F - vec3.y) * (double) 0.4F, 0.0D));
			this.hasImpulse = true;
		}

		super.customServerAiStep();
	}

	public double getRandomX(double x) {
		return 0;
	}

	public double getRandomY() {
		return -64;
	}

	public double getRandomZ(double z) {
		return 0;
	}

	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance dif, MobSpawnType spawn, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		data = super.finalizeSpawn(world, dif, spawn, data, tag);
		this.initMobData(this, dif);
		return data;
	}
}
