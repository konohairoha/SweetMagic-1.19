package sweetmagic.init.entity.monster.boss;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.entity.projectile.BraveShot;
import sweetmagic.init.entity.projectile.FireMagicShot;
import sweetmagic.util.SMDamage;

public class BraveSkeleton extends AbstractSMBoss {

	private int firstTick = 25;
	private static final int MAX_FIRST_TICK = 150;
	private int secondTick = 400;
	private static final int MAX_SECOND_TICK = 600;
	private int firstCount = 0;
	private int arrowTick = 0;
	private static final int MAX_ARROW_TICK = 600;
	private static final EntityDataAccessor<Boolean> ISARROW = ISMMob.setData(BraveSkeleton.class, BOOLEAN);

	public BraveSkeleton(Level world) {
		super(EntityInit.braveSkeleton, world);
	}

	public BraveSkeleton(EntityType<? extends AbstractSMBoss> enType, Level world) {
		super(enType, world);
		this.maxUpStep = 1.25F;
		this.setBossEvent(BC_BLUE, NOTCHED_6);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ISARROW, false);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
		this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Warden.class, 8.0F));
		this.goalSelector.addGoal(4, new SMRandomLookGoal(this));
		this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Warden.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Raider.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 512D)
				.add(Attributes.MOVEMENT_SPEED, 0.4D)
				.add(Attributes.ATTACK_DAMAGE, 16D)
				.add(Attributes.ARMOR, 12D)
				.add(Attributes.FOLLOW_RANGE, 64D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 96D);
	}

	public boolean getArrow() {
		return this.entityData.get(ISARROW);
	}

	public void setArrow(boolean arrow) {
		this.entityData.set(ISARROW, arrow);
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putBoolean("Arrow", this.getArrow());
		tags.putInt("firstTick", this.firstTick);
		tags.putInt("secondTick", this.secondTick);
		tags.putInt("firstCount", this.firstCount);
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setArrow(tags.getBoolean("Arrow"));
		this.firstTick = tags.getInt("firstTick");;
		this.secondTick = tags.getInt("secondTick");;
		this.firstCount = tags.getInt("firstCount");
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {
		Entity attacker = src.getEntity();
		Entity attackEntity = src.getDirectEntity();
		if ( attacker != null && attacker instanceof ISMMob) { return false; }

		// ボスダメージ計算
		amount = this.getBossDamageAmount(this.level, this.defTime , src, amount, 7F);
		this.defTime = amount > 0 ? 2 : this.defTime;

		if (attacker instanceof Warden) {
			this.attackDamage(attacker, SMDamage.magicDamage, amount);
			return false;
		}

		// 魔法攻撃以外ならダメージ減少
		if (this.notMagicDamage(attacker, attackEntity)) {
			amount *= 0.25F;
		}

		if (this.isRiding()) {
			SkeletonHorse horse = (SkeletonHorse) this.getVehicle();
			horse.setHealth(horse.getHealth() - amount);
			amount = Math.min(0F, amount);
		}

		return super.hurt(src, amount);
	}

	public boolean causeFallDamage(float par1, float par2, DamageSource src) {
		return false;
	}

	public void initBossBar() {
		ServerBossEvent event = this.getBossEvent();
		if (event == null) { return; }

		if (this.isRiding()) {
			if (this.getVehicle() instanceof SkeletonHorse horse) {
				event.setProgress(horse.getHealth() / horse.getMaxHealth());
				event.setColor(BossEvent.BossBarColor.GREEN);
			}
		}

		else {
			event.setProgress(this.getHealth() / this.getMaxHealth());
			event.setColor(BossEvent.BossBarColor.BLUE);
		}

		if (this.isHalfHealth(this)) {
			event.setColor(BossBarColor.RED);
		}
	}

	protected void customServerAiStep() {
		super.customServerAiStep();
		LivingEntity target = this.getTarget();
		if (target == null) { return; }

		this.checkSpawnPos();
		this.getLookControl().setLookAt(target, 10F, 10F);
		this.firstAttack(target);

		if (this.isHalfHealth(this)) {
			this.secondAttack(target);
			this.checkPotion(PotionInit.reflash_effect, 9999, 0);
		}

		if (this.arrowTick++ >= MAX_ARROW_TICK) {
			this.setArrow(!this.getArrow());
			this.arrowTick = 0;
		}

		if (this.isRiding() && !this.hasEffect(MobEffects.DAMAGE_BOOST)) {
			this.addPotion(this, MobEffects.DAMAGE_BOOST, 9999, 2);
		}
	}

	public void firstAttack (LivingEntity target) {
		if (this.firstTick++ >= MAX_FIRST_TICK) {
			if (this.getArrow()) {
				this.arrowAttak(target);
			}

			else {
				this.swordAttak(target);
			}
		}
	}

	public void secondAttack(LivingEntity target) {

		if (this.secondTick++ >= MAX_SECOND_TICK) {
			this.blazeAttak(target);
		}

		else if (this.secondTick >= MAX_SECOND_TICK - 200 && this.tickCount % 10 == 0) {
			this.blazeParticle(target);
		}
	}

	public void arrowAttak(LivingEntity target) {

		float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
		List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), 48D);

		for (LivingEntity entity : targetList) {

			AbstractMagicShot magic = new FireMagicShot(this.level, this);
			double d0 = entity.getX() - this.getX();
			double d1 = entity.getY(0.3333333333333333D) - this.getY() - 1D;
			double d2 = entity.getZ() - this.getZ();
			double d3 = Math.sqrt(d0 * d0 + d2 * d2);
			magic.shoot(d0, d1 - d3 * (double) 0.00F, d2, 2.0F, 0);
			magic.setAddDamage(magic.getAddDamage() + damage);
			magic.setMaxLifeTime(100 + this.rand.nextInt(30));
			magic.setArrow(true);
			magic.setRange(3.75D);
			this.level.addFreshEntity(magic);
		}

		this.playSound(SoundEvents.ARROW_SHOOT, 0.5F, 0.875F);
		this.firstTick = Math.max(0, 100 - targetList.size() * 10);
	}

	public void swordAttak(LivingEntity target) {

		float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5F;
		AbstractMagicShot magic = new BraveShot(this.level, this);
		double d0 = target.getX() - this.getX();
		double d1 = target.getY(0.3333333333333333D) - this.getY() - 1.5D;
		double d2 = target.getZ() - this.getZ();
		double d3 = Math.sqrt(d0 * d0 + d2 * d2);
		magic.shoot(d0, d1 - d3 * (double) 0.00F, d2, 1.875F, 0);
		magic.setAddDamage(magic.getAddDamage() + damage);
		magic.setMaxLifeTime(100 + this.rand.nextInt(30));
		magic.setRange(7D);
		this.level.addFreshEntity(magic);

		if (this.firstCount++ >= 5) {
			this.firstCount = 0;
			this.firstTick = 0;
		}

		else {
			this.firstTick -= 15;
		}
	}

	public void blazeAttak(LivingEntity target) {

		float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2F;
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), 48D);
		double range = 10D + Math.min(15D, entityList.size() * 2D);
		BlockPos pos = this.blockPosition();
		List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), range);

		for (LivingEntity entity : targetList) {
			if (!this.checkDistances(pos, entity.blockPosition(), range * range)) { continue; }

			boolean hasEffect = entity.hasEffect(PotionInit.reflash_effect);
			damage = hasEffect ? damage * 1.5F : damage;
			entity.hurt(SMDamage.magicDamage, damage);
		}

		this.secondTick = 0;
	}

	public void blazeParticle(LivingEntity target) {
		if (!(this.level instanceof ServerLevel server)) { return; }

		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), 48D);
		double range = 10D + Math.min(15D, entityList.size() * 2D);
		BlockPos pos = this.blockPosition();
		Iterable<BlockPos> posList = this.getPosList(pos, range);

		for (BlockPos p : posList) {

			if (this.rand.nextFloat() >= 0.067F || !this.checkDistances(pos, p, range * range)) { continue; }

			double x = p.getX() + this.rand.nextDouble() * 1.5D - 0.75D;
			double y = p.getY() + this.rand.nextDouble() * 1.5D - 0.75D;
			double z = p.getZ() + this.rand.nextDouble() * 1.5D - 0.75D;
			double xSpeed = (double) this.getRandFloat(0.1F);
			double ySpeed = (double) this.getRandFloat(0.05F);
			double zSpeed = (double) this.getRandFloat(0.1F);
			server.sendParticles(ParticleTypes.FLAME, x, y, z, 0, xSpeed, ySpeed + 0.2F, zSpeed, 1F);
		}

		this.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 1F);
	}

	protected void teleportSpawnPos(BlockPos pos) {
		if (this.isRiding()) {
			double d0 = pos.getX() + (this.rand.nextDouble() - 0.5D) * 10D;
			double d1 = pos.getY();
			double d2 = pos.getZ() + (this.rand.nextDouble() - 0.5D) * 10D;

			if (this.level.getBlockState(pos).isAir()) {
				this.getVehicle().setPos(d0, d1, d2);
			}
		}

		else {
			super.teleportSpawnPos(pos);
		}
	}

	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance dif, MobSpawnType spawn, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		SpawnGroupData sp = super.finalizeSpawn(world, dif, spawn, data, tag);
		this.spawnHorse(world, this.blockPosition().above());
		return sp;
	}

	public SkeletonHorse spawnHorse(LevelAccessor world, BlockPos pos) {
		SkeletonHorse horse = EntityType.SKELETON_HORSE.create(this.level);
		horse.setOwnerUUID(this.getUUID());
		horse.setTemper(100);
		horse.setTamed(true);
		horse.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getMaxHealth() * 0.25D);
		horse.getAttribute(Attributes.ARMOR).setBaseValue(10D);
		horse.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.4D);
		horse.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(10D);
		horse.setHealth(horse.getMaxHealth());
		horse.setAge(0);
		this.level.addFreshEntity(horse);
		horse.setPos(pos.getX(), pos.getY(), pos.getZ());
		this.startRiding(horse);
		this.addPotion(horse, PotionInit.aether_barrier, 1200, 4);
		return horse;
	}

	public boolean isRiding() {
		Entity entity = this.getVehicle();
		return entity != null && entity.isAlive();
	}

	@Override
	public boolean isArmorEmpty() {
		return true;
	}

	@Override
	public void clearInfo() { }

	public double getMyRidingOffset() {
		return -0.7D;
	}
}
