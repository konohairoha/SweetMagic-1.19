package sweetmagic.init.entity.monster.boss;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.entity.monster.PhantomWolf;
import sweetmagic.init.entity.projectile.PoisonMagicShot;
import sweetmagic.init.entity.projectile.ToxicCircle;
import sweetmagic.util.SMDamage;

public class SilverLandRoad extends AbstractSMBoss {

	private UUID ownerID;
	private LivingEntity owner;

	private int toxicTick = 0;
	private static final int MAX_TOXIC_TICK = 400;

	private int poisonTick = 0;
	private static final int MAX_POISON_TICK = 150;

	private int summonTick = 0;
	private static final int MAX_SUMMON_TICK = 600;

	private static final EntityDataAccessor<Boolean> IS_ALIVE = ISMMob.setData(SilverLandRoad.class, BOOLEAN);
	private final ServerBossEvent bossEvent = this.getBossBar(BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.NOTCHED_6);

	public SilverLandRoad(Level world) {
		super(EntityInit.silverLandRoad, world);
	}

	public SilverLandRoad(EntityType<? extends AbstractSMBoss> enType, Level world) {
		super(enType, world);
		this.xpReward = 150;
		this.maxUpStep = 1.25F;
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
		this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Warden.class, 8.0F));
		this.goalSelector.addGoal(10, new SMRandomLookGoal(this));
		this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Warden.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Raider.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 400D)
				.add(Attributes.MOVEMENT_SPEED, 0.45D)
				.add(Attributes.ATTACK_DAMAGE, 7D)
				.add(Attributes.ARMOR, 8D)
				.add(Attributes.FOLLOW_RANGE, 64D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 64D);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(IS_ALIVE, false);
		this.entityData.define(HALFHEALTH, false);
	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.WOLF_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource src) {
		return SoundEvents.WOLF_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.WOLF_DEATH;
	}

	public float getVoicePitch() {
		return 0.75F;
	}

	protected float getSoundVolume() {
		return 0.5F;
	}

	public boolean getAlive () {
		return this.entityData.get(IS_ALIVE);
	}

	public void setAlive(boolean isAlive) {
		this.entityData.set(IS_ALIVE, isAlive);
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putBoolean("isAlive", this.isAlive());
		tags.putBoolean("isHalfHealth", this.getHalfHealth());

		if (this.getOwnerID() != null) {
			tags.putUUID("ownerID", this.getOwnerID());
		}
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setSwimming(tags.getBoolean("isAlive"));
		this.setHalfHealth(tags.getBoolean("isHalfHealth"));

		if (tags.contains("ownerID")) {
			this.setOwnerID(tags.getUUID("ownerID"));
		}

		if (this.hasCustomName()) {
			this.bossEvent.setName(this.getDisplayName());
		}
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

	public void setCustomName(@Nullable Component tip) {
		super.setCustomName(tip);
		this.bossEvent.setName(this.getDisplayName());
	}

	public void startSeenByPlayer(ServerPlayer player) {
		super.startSeenByPlayer(player);
		this.bossEvent.addPlayer(player);
	}

	public void stopSeenByPlayer(ServerPlayer player) {
		super.stopSeenByPlayer(player);
		this.bossEvent.removePlayer(player);
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {

		Entity attacker = src.getEntity();
		Entity attackEntity = src.getDirectEntity();
		if ( attacker != null && attacker instanceof ISMMob) { return false; }

		// ボスダメージ計算
		amount = this.getBossDamageAmount(this.level, this.defTime , src, amount, 8F);
		this.defTime = amount > 0 ? 2 : this.defTime;

		// 魔法攻撃以外なら反撃&ダメージ無効
		if (this.notMagicDamage(attacker, attackEntity)) {
			this.attackDamage(attacker, SMDamage.magicDamage, amount);
			return false;
		}

		if (amount > 1F) {
			this.toxicTick++;
			this.poisonTick++;
		}

		return super.hurt(src, amount);
	}

	protected void customServerAiStep() {

		super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

        if (this.isHalfHealth(this) && !this.getHalfHealth()) {
        	this.bossEvent.setColor(BossBarColor.RED);
        	this.setHalfHealth(true);
        }

		LivingEntity target = this.getTarget();
		if (target == null) { return; }

		this.getLookControl().setLookAt(target, 10F, 10F);
        this.firstAttack(target);

        if (this.isHalfHealth(this) || this.getHalfHealth()) {
        	this.secondAttack(target);
        }

        if (!this.isAlive()) {
        	this.toxicTick++;
			this.poisonTick++;
        	this.summonTick++;
        }

        if (this.hasEffect(PotionInit.recast_reduction)) {
        	this.toxicTick++;
			this.poisonTick++;
        	this.summonTick++;
        }
	}

	public void firstAttack (LivingEntity target) {
		if(this.poisonTick++ >= MAX_POISON_TICK) {
			this.poisonAttack(target);
		}
	}

	public void secondAttack(LivingEntity target) {

		if(this.summonTick++ >= MAX_SUMMON_TICK) {
			this.poisonWolf(target);
		}

		if(this.toxicTick++ >= MAX_TOXIC_TICK) {
			this.toxicCircle(target);
		}

		if (this.tickCount % 10 == 0 && this.getOwnerID() != null && this.getAlive()) {
			LivingEntity entity = this.getEntity();
			if (entity != null && entity.isAlive()) { return; }

			this.setAlive(false);
			this.heal(this.getMaxHealth() * 0.5F);
			this.addPotion(this, MobEffects.DAMAGE_BOOST, 9999, 2);
		}
	}

	public void poisonAttack (LivingEntity target) {

		List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), 48D);

		for (LivingEntity entity : targetList) {

			double x = entity.getX() - this.getX();
			double y = entity.getY(0.3333333333333333D) - this.getY();
			double z = entity.getZ() - this.getZ();
			double xz = Math.sqrt(x * x + z * z);

			PoisonMagicShot magic = new PoisonMagicShot(this.level, this, ItemStack.EMPTY);
			magic.shoot(x, y - xz * 0.035D, z, 1F, 0F);
			magic.setAddDamage(magic.getAddDamage() + 6F);
			magic.setMaxLifeTime(100);
			this.level.addFreshEntity(entity);
		}

		this.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);
		this.poisonTick = 0;
	}

	public void poisonWolf (LivingEntity target) {
		List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), 48D);
		int size = 1 + targetList.size() / 2;

		for (int i = 0; i < size ; i++) {

			PhantomWolf entity = new PhantomWolf(this.level);
			entity.setPos(this.getX() + 0.5D, this.getY() + 0.5D, this.getZ() + 0.5D);
			entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getMaxHealth() / 5F);
			entity.setHealth(entity.getMaxHealth());
			this.level.addFreshEntity(entity);
			entity.spawnAnim();
			entity.setUUID(this.getUUID());
		}

		this.playSound(SoundInit.HORAMAGIC, 0.5F, 1F);
		this.summonTick = 0;
	}

	public void toxicCircle (LivingEntity target) {
		List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), 48D);
		double range = 5D + targetList.size() * 0.5D;
		double x = target.getX() - this.getX();
		double z = target.getZ() - this.getZ();

		ToxicCircle entity = new ToxicCircle(this.level, this, ItemStack.EMPTY);
		entity.setHitDead(false);
		entity.shoot(x, 0D, z, 1F, 0F);
		entity.setAddDamage(entity.getAddDamage() + 6F);
		entity.setRange(range);
		entity.setMaxLifeTime(400);
		this.level.addFreshEntity(entity);
		this.toxicTick = 0;
		this.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);
	}

	@Override
	public boolean isArmorEmpty() {
		return true;
	}

	public void startInfo() {
		this.poisonTick = 120;
		this.toxicTick = 240;
		this.summonTick = 500;
	}

	@Override
	public void clearInfo() { }

	protected void tickDeath() {
		super.tickDeath();
		this.bossEvent.setProgress(0F);
	}
}
