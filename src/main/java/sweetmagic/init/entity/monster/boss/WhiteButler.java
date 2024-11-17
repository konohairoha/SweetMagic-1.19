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
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.entity.projectile.CycloneMagicShot;
import sweetmagic.init.entity.projectile.DigMagicShot;
import sweetmagic.init.entity.projectile.FireMagicShot;
import sweetmagic.init.entity.projectile.FrostMagicShot;
import sweetmagic.init.entity.projectile.GravityMagicShot;
import sweetmagic.init.entity.projectile.KnifeShot;
import sweetmagic.init.entity.projectile.SickleShot;
import sweetmagic.util.SMDamage;

public class WhiteButler extends AbstractSMBoss {

	private UUID ownerID;
	private LivingEntity owner;

	public int attackCount = 0;
	public int rifleCount = 0;

	private int knifeTick = 0;
	private static final int MAX_KNIFE_TICK = 140;

	private int sickleTick = 0;
	private static final int MAX_SICKLE_TICK = 360;

	private int rifleTick = 0;
	private static final int MAX_RIFLE_TICK = 320;

	private static final EntityDataAccessor<Boolean> IS_ALIVE = ISMMob.setData(WhiteButler.class, BOOLEAN);
	private static final EntityDataAccessor<Boolean> HAS_KNIFE = ISMMob.setData(WhiteButler.class, BOOLEAN);
	private static final EntityDataAccessor<Boolean> HAS_SICKLE = ISMMob.setData(WhiteButler.class, BOOLEAN);
	private static final EntityDataAccessor<Boolean> HAS_RIFLE = ISMMob.setData(WhiteButler.class, BOOLEAN);
	private final ServerBossEvent bossEvent = this.getBossBar(BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.NOTCHED_6);

	public WhiteButler(Level world) {
		super(EntityInit.whiteButler, world);
	}

	public WhiteButler(EntityType<? extends AbstractSMBoss> enType, Level world) {
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
				.add(Attributes.MOVEMENT_SPEED, 0.3D)
				.add(Attributes.ATTACK_DAMAGE, 4D)
				.add(Attributes.ARMOR, 8D)
				.add(Attributes.FOLLOW_RANGE, 64D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 64D);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(IS_ALIVE, false);
		this.entityData.define(HAS_KNIFE, false);
		this.entityData.define(HAS_SICKLE, false);
		this.entityData.define(HAS_RIFLE, false);
		this.entityData.define(HALFHEALTH, false);
	}

	public boolean getAlive () {
		return this.entityData.get(IS_ALIVE);
	}

	public void setAlive(boolean isAlive) {
		this.entityData.set(IS_ALIVE, isAlive);
	}

	public boolean getKnife () {
		return this.entityData.get(HAS_KNIFE);
	}

	public void setKnife(boolean hasKnife) {
		this.entityData.set(HAS_KNIFE, hasKnife);
	}

	public boolean getSickle () {
		return this.entityData.get(HAS_SICKLE);
	}

	public void setSickle(boolean hasSickle) {
		this.entityData.set(HAS_SICKLE, hasSickle);
	}

	public boolean getRifle () {
		return this.entityData.get(HAS_RIFLE);
	}

	public void setRifle(boolean hasRifle) {
		this.entityData.set(HAS_RIFLE, hasRifle);
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putBoolean("isAlive", this.isAlive());
		tags.putBoolean("hasKnife", this.getKnife());
		tags.putBoolean("hasSickle", this.getSickle());
		tags.putBoolean("hasRifle", this.getRifle());
		tags.putBoolean("isHalfHealth", this.getHalfHealth());

		if (this.getOwnerID() != null) {
			tags.putUUID("ownerID", this.getOwnerID());
		}
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setSwimming(tags.getBoolean("isAlive"));
		this.setKnife(tags.getBoolean("hasKnife"));
		this.setSickle(tags.getBoolean("hasSickle"));
		this.setRifle(tags.getBoolean("hasRifle"));
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
        	this.knifeTick++;
        	this.rifleTick++;
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
        	this.knifeTick++;
        	this.sickleTick++;
        	this.rifleTick++;
        }

        if (this.hasEffect(PotionInit.recast_reduction)) {
        	this.knifeTick++;
        	this.sickleTick++;
        	this.rifleTick++;
        }
	}

	public void firstAttack (LivingEntity target) {

		if (this.knifeTick > 40 && !this.getKnife()) {
			this.setKnife(true);
		}

		if (this.knifeTick++ >= MAX_KNIFE_TICK) {
			this.knifeAttack(target);
		}

		if (this.sickleTick > 200 && !this.getSickle()) {
			this.setSickle(true);
		}

		if (this.attackCount > 3 && this.sickleTick++ >= MAX_SICKLE_TICK) {
			this.sickleAttack(target);
		}
	}

	public void secondAttack(LivingEntity target) {

		if (this.rifleTick > 0 && !this.getRifle()) {
			this.setRifle(true);
		}

		if (this.rifleTick++ >= MAX_RIFLE_TICK && this.tickCount % 12 == 0) {
			this.rifleAttack(target);
		}

		if (this.tickCount % 10 == 0 && this.getOwnerID() != null && this.getAlive()) {
			LivingEntity entity = this.getEntity();
			if (entity != null && entity.isAlive()) { return; }

			this.setAlive(false);
			this.heal(this.getMaxHealth() * 0.5F);
			this.addPotion(this, MobEffects.DAMAGE_BOOST, 9999, 2);
		}
	}

	public void knifeAttack (LivingEntity target) {

		List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), 48D);

		for (LivingEntity entity : targetList) {

			double x = entity.getX() - this.getX();
			double y = entity.getY(0.3333333333333333D) - this.getY();
			double z = entity.getZ() - this.getZ();
			double xz = Math.sqrt(x * x + z * z);

			KnifeShot knife = new KnifeShot(this.level, this, ItemStack.EMPTY);
			knife.setHitDead(false);
			knife.shoot(x, y - xz * 0.065D, z, 1.5F, 2F);
			knife.setAddDamage(knife.getAddDamage() + 6F);
			this.level.addFreshEntity(knife);
		}

		this.attackCount += targetList.size();
		this.knifeTick = 0;
		this.setKnife(false);
		this.playSound(SoundInit.KNIFE_SHOT, 0.5F, 0.9F);
	}

	public void sickleAttack (LivingEntity target) {

		List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), 48D);
		double range = 3.5D + this.attackCount * 0.25D + targetList.size() * 0.1D;
		double x = target.getX() - this.getX();
		double z = target.getZ() - this.getZ();

		SickleShot entity = new SickleShot(this.level, this, ItemStack.EMPTY);
		entity.setHitDead(false);
		entity.shoot(x, 0D, z, 1.75F, 2F);
		entity.setAddDamage(entity.getAddDamage() + 12F);
		entity.setRange(range);
		entity.setMaxLifeTime(500);
		this.level.addFreshEntity(entity);
		this.sickleTick = 0;
		this.attackCount = 0;
		this.setSickle(false);
		this.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);
	}

	public void rifleAttack (LivingEntity target) {

		List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), 48D);
		this.playSound(SoundInit.RIFLE_SHOT, 0.2F, 0.85F);

		for (LivingEntity entity : targetList) {
			AbstractMagicShot magic = this.getMagicShot(entity, this.rifleCount, entity instanceof Warden);
			this.level.addFreshEntity(magic);
		}

		if (this.rifleCount++ >= 5) {
			this.rifleTick = 0;
			this.rifleCount = 0;
			this.setRifle(false);
		}
	}

	public AbstractMagicShot getMagicShot (LivingEntity target, int count, boolean isWarden) {

		AbstractMagicShot entity = null;

		float dama = isWarden ? 30F : 5F;
		float dameRate = isWarden ? 1.5F : 1F;

		double x = target.getX() - this.getX();
		double y = target.getY(0.3333333333333333D) - this.getY();
		double z = target.getZ() - this.getZ();
		double xz = Math.sqrt(x * x + z * z);
		int level = isWarden ? 20 : 5;

		switch (count) {
		case 1:
			entity = new FrostMagicShot(this.level, this, ItemStack.EMPTY);
			break;
		case 2:
			entity = new GravityMagicShot(this.level, this, ItemStack.EMPTY);
			break;
		case 3:
			entity = new CycloneMagicShot(this.level, this, ItemStack.EMPTY);
			break;
		case 4:
			entity = new DigMagicShot(this.level, this, ItemStack.EMPTY);
			break;
		default:
			entity = new FireMagicShot(this.level, this, ItemStack.EMPTY);
			break;
		}

		entity.setWandLevel(level);
		entity.shoot(x, y - xz * 0.065D, z, 1.75F, 2F);
		entity.setAddDamage( (entity.getAddDamage() + dama) * dameRate );
		entity.setHitDead(false);
		return entity;
	}

	@Override
	public boolean isArmorEmpty() {
		return true;
	}

	public void startInfo() {
		this.knifeTick = 120;
		this.sickleTick = 400;
		this.rifleTick = 200;
	}

	@Override
	public void clearInfo() { }

	protected void tickDeath() {
		super.tickDeath();
		this.bossEvent.setProgress(0F);
	}
}