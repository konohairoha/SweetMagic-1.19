package sweetmagic.init.entity.monster;

import java.util.List;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.entity.ai.BlazeAttackGoal;
import sweetmagic.util.SMDamage;

public class BlazeTempestTornado extends AbstractSMMob {

	private float allowHeight = 0.5F;
	private int nextTick;
	private int tickTime = 0;
	private int windTime = 50;

	private static final EntityDataAccessor<Integer> CANDLE = ISMMob.setData(BlazeTempestTornado.class, INT);

	public BlazeTempestTornado(Level world) {
		super(EntityInit.blazeTempestTornado, world);
	}

	public BlazeTempestTornado(EntityType<BlazeTempestTornado> enType, Level world) {
		super(enType, world);
		this.xpReward = 200;
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(CANDLE, 0);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 150D)
				.add(Attributes.MOVEMENT_SPEED, 0.4D)
				.add(Attributes.ATTACK_DAMAGE, 6D)
				.add(Attributes.ARMOR, 8D)
				.add(Attributes.FOLLOW_RANGE, 32D);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(4, new BlazeAttackGoal(this, this.isHard(this.level), 1));
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
		if (attacker != null && attacker instanceof ISMMob) { return false; }

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

		if (!this.level.isClientSide && amount >= 2F && this.tickCount > this.tickTime) {
			int count = this.getCandole();
			if (count < 4) {
				this.setCandole(count + 1);
				this.tickTime = this.tickCount + 5;
			}
		}

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

	public void tick() {
		super.tick();

		if (this.level.isClientSide && this.getTarget() != null) {
			double x = + this.xo + (this.rand.nextDouble() - 0.5D);
			double y = + this.yo + (this.rand.nextDouble() + 0.5D);
			double z = + this.zo + (this.rand.nextDouble() - 0.5D);
			this.level.addParticle(ParticleTypes.SWEEP_ATTACK, x, y, z, 0D, 0D, 0D);
		}
	}

	public void aiStep() {

		if (!this.onGround && this.getDeltaMovement().y < 0XD) {
			this.setDeltaMovement(this.getDeltaMovement().multiply(1D, 0.6D, 1D));
		}

		super.aiStep();
	}

	protected void customServerAiStep() {
		super.customServerAiStep();

		--this.nextTick;
		if (this.nextTick <= 0) {
			this.nextTick = 100;
			this.allowHeight = (float) this.random.triangle(0.5D, 6.891D);
		}

		LivingEntity target = this.getTarget();
		if (target == null || this.getMaxHealth() <= 50D) { return; }

		if (target.getEyeY() > this.getEyeY() + (double) this.allowHeight) {
			Vec3 vec3 = this.getDeltaMovement();
			this.setDeltaMovement(this.getDeltaMovement().add(0D, ((double) 0.3F - vec3.y) * (double) 0.4F, 0D));
			this.hasImpulse = true;
		}

		if (this.tickCount % 20 != 0 || this.getCandole() < 4 || this.windTime++ < 7) { return; }

		boolean isPlayer = this.isPlayer(target);
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.getFilter(isPlayer), 8D);
		if (entityList.isEmpty()) { return; }

		for (LivingEntity entity : entityList) {
			entity.hurt(this.getSRC(), 10F);
			entity.invulnerableTime = 0;
			entity.yo += 10D;

			LivingKnockBackEvent event = ForgeHooks.onLivingKnockBack(entity, 2F, entity.getX() - this.getX(), entity.getZ() - this.getZ());
			if (event.isCanceled()) { continue; }

			Vec3 vec3 = new Vec3(event.getRatioX(), 0.75D, event.getRatioZ()).scale(event.getStrength());
			entity.setDeltaMovement(entity.getDeltaMovement().add(vec3));
			entity.fallDistance += 1D;
		}

		this.setCandole(0);
		this.windTime = 0;
		if ( !( this.level instanceof ServerLevel sever ) ) { return; }

		float x = (float) (this.getX() + this.rand.nextFloat() - 0.5F);
		float y = (float) (this.getY() + this.rand.nextFloat() - 0.5F);
		float z = (float) (this.getZ() + this.rand.nextFloat() - 0.5F);

		for (int i = 0; i < 16; i++) {
			sever.sendParticles(ParticleTypes.CLOUD, x, y, z, 4, 0F, 0F, 0F, 0.15F);
		}
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putInt("candole", this.getCandole());
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setCandole(tags.getInt("candole"));
	}

	public int getCandole() {
		return this.entityData.get(CANDLE);
	}

	public void setCandole(int size) {
		this.entityData.set(CANDLE, size);
	}

	// 低ランクかどうか
	public boolean isLowRank() {
		return false;
	}
}
