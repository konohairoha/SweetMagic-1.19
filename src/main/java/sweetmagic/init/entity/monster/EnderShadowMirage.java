package sweetmagic.init.entity.monster;

import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
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

public class EnderShadowMirage extends AbstractSMMob {

	private int teleoprtTime = 0;
	private UUID ownerID;
	private LivingEntity owner;

	private static final EntityDataAccessor<Boolean> ISSUMMON = ISMMob.setData(EnderShadowMirage.class, EntityDataSerializers.BOOLEAN);

	public EnderShadowMirage(Level world) {
		super(EntityInit.enderShadowMirage, world);
	}

	public EnderShadowMirage(EntityType<EnderShadowMirage> enType, Level world) {
		super(enType, world);
		this.xpReward = 50;
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ISSUMMON, false);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 50D)
				.add(Attributes.MOVEMENT_SPEED, 0.35D)
				.add(Attributes.ATTACK_DAMAGE, 4D)
				.add(Attributes.ARMOR, 8D)
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
		if ( attacker != null && attacker instanceof ISMMob) { return false; }

		if (!this.level.isClientSide && amount > 2F && this.tickCount > this.teleoprtTime) {
			this.teleoprtTime = this.tickCount + 10;
			this.teleport();
		}

		// ダメージ倍処理
		amount = this.getBossDamageAmount(this.level, this.defTime , src, amount, 20F);
		this.defTime = 2;
		return super.hurt(src, Math.min(amount, 25F));
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

	public boolean doHurtTarget(Entity entity) {
		boolean flag = super.doHurtTarget(entity);

		if (flag && entity instanceof Warden target) {
			entity.hurt(this.getSRC(), 10F);
		}

		entity.invulnerableTime = 0;
		return flag;
	}

	protected void customServerAiStep() {

		super.customServerAiStep();

		LivingEntity target = this.getTarget();
		if (target == null) { return; }

		if (this.tickCount % 10 != 0 || this.random.nextFloat() >= 0.05F) { return; }

		this.teleport();
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putUUID("ownerID", this.getOwnerID());
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setOwnerID(tags.getUUID("ownerID"));
	}

	public UUID getOwnerID () {
		return this.ownerID;
	}

	public void setOwnerID (LivingEntity entity) {
		this.ownerID = entity.getUUID();
	}

	public void setOwnerID (UUID id) {
		this.ownerID = id;
	}

	public LivingEntity getEntity () {

		LivingEntity entity = this.owner;

		if (entity == null && this.level instanceof ServerLevel server) {
			entity = (LivingEntity) server.getEntity(this.getOwnerID());
		}

		return entity;
	}

	// 低ランクかどうか
	public boolean isLowRank () {
		return false;
	}
}
