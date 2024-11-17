package sweetmagic.init.entity.animal;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.api.iitem.IWand;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.TagInit;
import sweetmagic.init.entity.monster.AbstractSMMob;
import sweetmagic.util.PlayerHelper;
import sweetmagic.util.SMDamage;

public abstract class AbstractSummonMob extends TamableAnimal {

	protected Random rand = new Random();
	private static final EntityDataAccessor<Integer> SUMMON_TIME = ISMMob.setData(AbstractSummonMob.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> WAND_LEVEL = ISMMob.setData(AbstractSummonMob.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Float> RANGE = ISMMob.setData(AbstractSummonMob.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Boolean> IS_SHIT = ISMMob.setData(AbstractSummonMob.class, EntityDataSerializers.BOOLEAN);

	public AbstractSummonMob(EntityType<? extends AbstractSummonMob> eType, Level world) {
		super(eType, world);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SUMMON_TIME, 1200);
		this.entityData.define(WAND_LEVEL, 1);
		this.entityData.define(RANGE, 0F);
		this.entityData.define(IS_SHIT, false);
	}

	public void setMaxLifeTime(int maxLifeTime) {
		this.entityData.set(SUMMON_TIME, maxLifeTime);
	}

	public int getMaxLifeTime() {
		return this.entityData.get(SUMMON_TIME);
	}

	public void setWandLevel(int wandLevel) {
		this.entityData.set(WAND_LEVEL, wandLevel);
	}

	public int getWandLevel() {
		return this.entityData.get(WAND_LEVEL);
	}

	public void setShit(boolean isShit) {
		this.entityData.set(IS_SHIT, isShit);
	}

	public boolean getShit() {
		return this.entityData.get(IS_SHIT);
	}

	public void setRange(float range) {
		this.entityData.set(RANGE, range);
	}

	public float getRange() {
		return this.entityData.get(RANGE);
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putInt("summonTime", this.getMaxLifeTime());
		tags.putInt("wandLevel", this.getWandLevel());
		tags.putInt("wandLevel", this.getWandLevel());
		tags.putFloat("range", this.getRange());
		tags.putBoolean("isShit", this.getShit());
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setMaxLifeTime(tags.getInt("summonTime"));
		this.setWandLevel(tags.getInt("wandLevel"));
		this.setRange(tags.getFloat("range"));
		this.setShit(tags.getBoolean("isShit"));
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {

		Entity attacker = src.getEntity();

		if (attacker == null || !(attacker instanceof Enemy) ) {
			return false;
		}

		if (src.getDirectEntity() instanceof Warden entity) {
			entity.hurt(SMDamage.MAGIC, amount);
			entity.invulnerableTime = 0;
			amount *= 0.1F;
		}

		return super.hurt(src, Math.min(20F, amount));
	}

	public boolean doHurtTarget(Entity entity) {

		float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
		DamageSource src = SMDamage.getAddDamage(this, this.getOwner());

		if (entity instanceof Warden) {
			damage *= 4F;
		}

		else if (entity instanceof EnderMan || entity instanceof Witch) {
			src = DamageSource.playerAttack((Player) this.getOwner());
		}

		else if (this.isBoss(entity)) {
			damage *= 0.125F;
		}

		boolean flag = entity.hurt(src, damage);
		entity.invulnerableTime = 0;

		if (flag) {
			this.doEnchantDamageEffects(this, entity);
		}

		return flag;
	}

	public void tamedState(WandInfo wandInfo) {

		int level = wandInfo.getLevel();
		double rate = Math.max(3.5D, (1D + (level - 1) * 0.075D));

		this.setWandLevel(level);
		this.setState(rate);
	}

	public void setState (double rate) {
		this.setAttribute(Attributes.MAX_HEALTH, rate);
		this.setAttribute(Attributes.ATTACK_DAMAGE, rate);
		this.setAttribute(Attributes.MOVEMENT_SPEED, Math.min(2D, rate));
		this.setAttribute(Attributes.ARMOR, rate);
		this.setHealth(this.getMaxHealth());
	}

	public void setAttribute(Attribute att, double rate) {
		this.getAttribute(att).setBaseValue(this.getAttributeValue(att) * rate);
	}

	public InteractionResult mobClick(InteractionResult result, ItemStack stack) {

		if (!result.consumesAction() && !(stack.getItem() instanceof IWand) ) {
			this.setOrderedToSit(!this.isOrderedToSit());
			this.setShit(this.isOrderedToSit());
		}

		return result;
	}

	public void tick() {

		super.tick();
		int maxLifeTime = this.getMaxLifeTime();

		if (maxLifeTime <= 0) {
			this.setMaxLifeTime(6000);
		}

		if (this.tickCount >= maxLifeTime) {

			if (this.level instanceof ServerLevel server) {
				this.discordParticle(server);
			}

			this.discard();
		}
	}

	protected void customServerAiStep() {

		super.customServerAiStep();

		LivingEntity target = this.getTarget();
		if (target == null || this.tickCount % 20 != 0) { return; }

		if ( !(target instanceof Enemy) || !target.isAlive()) {
			this.setTarget(null);
			this.setLastHurtMob(null);
			this.setLastHurtByMob(null);
			return;
		}

		List<TamableAnimal> entityList = this.getEntityList(TamableAnimal.class, e -> e.getTarget() == null && !e.isOrderedToSit(), 32D);
		entityList.forEach(e -> e.setTarget(target));
	}

	// 消滅時のパーティクル
	public void discordParticle(ServerLevel server) {

		this.playSound(SoundEvents.BUBBLE_COLUMN_UPWARDS_INSIDE, 0.4F, 2F);
		BlockPos pos = this.blockPosition().above();

		for (int i = 0; i < 64; ++i) {
			double d0 = this.getRand(this.rand) * 0.3D;
			double d1 = rand.nextFloat() * 0.4D;
			double d2 = this.getRand(this.rand) * 0.3D;
			server.sendParticles(ParticleInit.NORMAL.get(), this.getRandomX(pos), this.getRandomY(), this.getRandomZ(pos), 0, d0, d1 + 0.1D, d2, 1F);
		}
	}

	public double getRand(Random rand) {
		return rand.nextDouble() - rand.nextDouble();
	}

	public double getRandomX(BlockPos pos) {
		return pos.getX() + (this.getRand(this.rand) * 0.25D) + 0.5D;
	}

	public double getRandomY(BlockPos pos) {
		return pos.getY() + this.getRand(this.rand) * 0.5D - 1D;
	}

	public double getRandomZ(BlockPos pos) {
		return pos.getZ() + (this.getRand(this.rand) * 0.25D) + 0.5D;
	}

	public boolean causeFallDamage(float par1, float par2, DamageSource src) {
		return false;
	}

	public float getRand () {
		return this.rand.nextFloat();
	}

	public float getRand (float rate) {
		return ( this.rand.nextFloat() - this.rand.nextFloat() ) * rate;
	}

	// えんちちーリストの取得
	public <T extends Entity> List<T> getEntityList(Class<T> enClass, double range) {
		return this.level.getEntitiesOfClass(enClass, this.getAABB(range));
	}

	// えんちちーリストの取得
	public <T extends Entity> List<T> getEntityList(Class<T> enClass, Predicate<T> filter, AABB aabb) {
		return this.level.getEntitiesOfClass(enClass, aabb).stream().filter(filter).toList();
	}

	// フィルターえんちちーリストの取得
	public <T extends Entity> List<T> getEntityList(Class<T> enClass, Predicate<T> filter, double range) {
		return this.level.getEntitiesOfClass(enClass, this.getAABB(range)).stream().filter(filter).toList();
	}

	// 範囲の取得
	public AABB getAABB (double range) {
		return this.getAABB(range, range / 2, range);
	}

	// 範囲の取得
	public AABB getAABB (double x, double y, double z) {
		return this.getBoundingBox().inflate(x, y, z);
	}

	// 範囲の取得
	public AABB getAABB (BlockPos pos, double range) {
		return new AABB(pos.offset(-range, -range, -range), pos.offset(range, range, range));
	}

	// ブロー耐性を持っていないえんちちーを取得
	public <T extends LivingEntity> Predicate<T> isNotBlow () {
		return e -> e.isAlive() && !e.hasEffect(PotionInit.resistance_blow) && this.canTargetEffect(e, this.getOwner());
	}

	// 射撃者によってえんちちーを取得
	public <T extends LivingEntity> Predicate<T> isTarget () {
		return e -> e.isAlive() && this.canTargetEffect(e, this.getOwner());
	}

	// 範囲内にいる射撃者によってえんちちーを取得
	public <T extends LivingEntity> Predicate<T> isBladTarget (double range) {
		return e -> e.isAlive() && this.canTargetEffect(e, this.getOwner()) && this.checkDistance(e.blockPosition(), range);
	}

	public boolean canTargetEffect (LivingEntity target, Entity owner) {
		return owner instanceof Player ? target instanceof Enemy : target instanceof Player;
	}

	// 範囲内にいるかのチェック
	public boolean checkDistance (BlockPos pos, double range) {
		return this.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= range;
	}

	protected void checkFallDamage(double par1, boolean par2, BlockState state, BlockPos pos) { }

	// パーティクルスポーン
	public void spawnParticleRing(ServerLevel server, ParticleOptions particle, double range, BlockPos pos, double addY, double ySpeed, double moveValue) {

		double x = pos.getX() + 0.5D;
		double y = pos.getY() + 1D + addY;
		double z = pos.getZ() + 0.5D;

		for (double degree = -range * Math.PI; degree < range * Math.PI; degree += 0.25D) {
			double rate = range * 0.75D;
			server.sendParticles(particle, x + Math.cos(degree) * rate, y, z + Math.sin(degree) * rate, 0, 0, ySpeed, 0, 1D);
		}
	}

	public boolean isBoss (Entity entity) {
		return entity.getType().is(TagInit.BOSS) || (entity instanceof AbstractSMMob mob && !mob.isLowRank()) || (entity instanceof LivingEntity liv && liv.hasEffect(PotionInit.leader_flag));
	}

	public void addPotion (LivingEntity entity, MobEffect potion, int time, int level) {

		if (this.isBoss(entity)) {
			time = time / 4;
			level = Math.min(0, level / 2);

			if (potion.equals(PotionInit.bubble)) {
				time = Math.min(10, time);
			}
		}

		PlayerHelper.setPotion(entity, potion, level, time);
	}

	// 火力取得( レベル × 0.2 ) + 最小( (レベル - 1) × 0.175, 5)  + 最小( 最大(5 × (1 - (レベル - 1) × 0.02), 0), 4)
	public float getPower (float level) {
		return ( level * 0.2F ) + Math.min( (level - 1) * 0.255F, 5) + Math.min( Math.max(6 * (1 - (level - 1) * 0.0185F), 0), 5.8F);
	}

	public class NearestAttackSMMobGoal<T extends LivingEntity> extends TargetGoal {

		@Nullable
		protected AbstractSummonMob attacker;
		protected LivingEntity target;
		protected final int randInterval;
		protected final Class<T> targetType;
		protected TargetingConditions targetConditions;

		public NearestAttackSMMobGoal(Mob mob, Class<T> entityClass, boolean flag) {
			this(mob, entityClass, 10, flag, false, (Predicate<LivingEntity>) null);
		}

		public NearestAttackSMMobGoal(Mob mob, Class<T> entityClass, boolean flag, Predicate<LivingEntity> entityPre) {
			this(mob, entityClass, 10, flag, false, entityPre);
		}

		public NearestAttackSMMobGoal(Mob mob, Class<T> entityClass, boolean flag1, boolean flag2) {
			this(mob, entityClass, 10, flag1, flag2, (Predicate<LivingEntity>) null);
		}

		public NearestAttackSMMobGoal(Mob mob, Class<T> entityClass, int par1, boolean flag1, boolean flag2, @Nullable Predicate<LivingEntity> entityPre) {
			super(mob, flag1, flag2);
			this.targetType = entityClass;
			this.randInterval = reducedTickDelay(par1);
			this.setFlags(EnumSet.of(Goal.Flag.TARGET));
			this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(entityPre);
			this.attacker = (AbstractSummonMob) mob;
		}

		public boolean canUse() {

			if (this.attacker.isOrderedToSit()) {
				return false;
			}

			if (this.randInterval > 0 && this.mob.getRandom().nextInt(this.randInterval) != 0) {
				return false;
			}

			this.findTarget();
			return this.target != null;
		}

		protected AABB getTargetSearchArea(double par1) {
			return this.mob.getBoundingBox().inflate(par1, 4D, par1);
		}

		protected void findTarget() {

			if (this.targetType != Player.class && this.targetType != ServerPlayer.class) {
				List<T> entityList = this.mob.level.getEntitiesOfClass(this.targetType, this.getTargetSearchArea(this.getFollowDistance()), e -> e instanceof ISMMob);
				this.target = this.mob.level.getNearestEntity(entityList, this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
			}

			else {
				this.target = this.mob.level.getNearestPlayer(this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
			}
		}

		public void start() {
			this.mob.setTarget(this.target);
			super.start();
		}

		public void setTarget(@Nullable LivingEntity entity) {
			this.target = entity;
		}
	}

	public class AttackTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

		protected AbstractSummonMob attacker;

		public AttackTargetGoal(Mob mob, Class<T> entityClass, boolean flag) {
			this(mob, entityClass, 10, flag, false, (Predicate<LivingEntity>) null);
		}

		public AttackTargetGoal(Mob mob, Class<T> entityClass, boolean flag, Predicate<LivingEntity> entityPre) {
			this(mob, entityClass, 10, flag, false, entityPre);
		}

		public AttackTargetGoal(Mob mob, Class<T> entityClass, boolean flag1, boolean flag2) {
			this(mob, entityClass, 10, flag1, flag2, (Predicate<LivingEntity>) null);
		}

		public AttackTargetGoal(Mob mob, Class<T> entityClass, int par1, boolean flag1, boolean flag2, @Nullable Predicate<LivingEntity> entityPre) {
			super(mob, entityClass, par1, flag1, flag2, entityPre);
			this.attacker = (AbstractSummonMob) mob;
		}

		public boolean canUse() {
			return this.attacker.isOrderedToSit() ? false : super.canUse();
		}
	}

	public class SMOwnerHurtTargetGoal extends OwnerHurtTargetGoal {

		private final TamableAnimal mob;

		public SMOwnerHurtTargetGoal(TamableAnimal mob) {
			super(mob);
			this.mob = mob;
		}

		public boolean canUse() {
			LivingEntity owner = this.mob.getOwner();
			if (owner == null) { return super.canUse(); }

			LivingEntity target = owner.getLastHurtByMob();
			return super.canUse() && target != null && target instanceof Enemy;
		}
	}

	public class SMOwnerHurtByTargetGoal extends OwnerHurtByTargetGoal {

		private final TamableAnimal mob;

		public SMOwnerHurtByTargetGoal(TamableAnimal mob) {
			super(mob);
			this.mob = mob;
		}

		public boolean canUse() {
			LivingEntity owner = this.mob.getOwner();
			if (owner == null) { return super.canUse(); }

			LivingEntity target = owner.getLastHurtByMob();
			return super.canUse() && target != null && target instanceof Enemy;
		}
	}
}
