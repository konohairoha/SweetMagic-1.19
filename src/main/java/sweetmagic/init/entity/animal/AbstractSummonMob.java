package sweetmagic.init.entity.animal;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.api.iitem.IWand;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.TagInit;
import sweetmagic.init.entity.monster.AbstractSMMob;
import sweetmagic.init.item.magic.EvilArrowItem;
import sweetmagic.init.item.sm.SMFood;
import sweetmagic.init.item.sm.SummonerWand;
import sweetmagic.util.PlayerHelper;
import sweetmagic.util.SMDamage;
import sweetmagic.util.WorldHelper;

public abstract class AbstractSummonMob extends TamableAnimal {

	protected Random rand = new Random();
	private static final EntityDataAccessor<Float> RANGE = ISMMob.setData(AbstractSummonMob.class, ISMMob.FLOAT);
	private static final EntityDataAccessor<Float> HEALTH_ARMOR = ISMMob.setData(AbstractSummonMob.class, ISMMob.FLOAT);
	private static final EntityDataAccessor<Integer> SUMMON_TIME = ISMMob.setData(AbstractSummonMob.class, ISMMob.INT);
	private static final EntityDataAccessor<Integer> WAND_LEVEL = ISMMob.setData(AbstractSummonMob.class, ISMMob.INT);
	private static final EntityDataAccessor<Boolean> IS_SHIT = ISMMob.setData(AbstractSummonMob.class, ISMMob.BOOLEAN);
	private static final EntityDataAccessor<Boolean> ISALAY = ISMMob.setData(AbstractSummonMob.class, ISMMob.BOOLEAN);
	private static final EntityDataAccessor<Boolean> ISGOLEM = ISMMob.setData(AbstractSummonMob.class, ISMMob.BOOLEAN);
	private static final EntityDataAccessor<Boolean> ISFOX = ISMMob.setData(AbstractSummonMob.class, ISMMob.BOOLEAN);
	private static final EntityDataAccessor<Boolean> ISMASTER = ISMMob.setData(AbstractSummonMob.class, ISMMob.BOOLEAN);
	private static final EntityDataAccessor<Boolean> ISIFRIT = ISMMob.setData(AbstractSummonMob.class, ISMMob.BOOLEAN);
	private static final EntityDataAccessor<Boolean> ISWINDINE = ISMMob.setData(AbstractSummonMob.class, ISMMob.BOOLEAN);

	public AbstractSummonMob(EntityType<? extends AbstractSummonMob> eType, Level world) {
		super(eType, world);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.define(SUMMON_TIME, 1200);
		this.define(WAND_LEVEL, 1);
		this.define(RANGE, 0F);
		this.define(HEALTH_ARMOR, 0F);
		this.define(IS_SHIT, false);
		this.define(ISALAY, false);
		this.define(ISGOLEM, false);
		this.define(ISFOX, false);
		this.define(ISMASTER, false);
		this.define(ISIFRIT, false);
		this.define(ISWINDINE, false);
	}

	public <T> T get(EntityDataAccessor<T> value) {
		return this.getEntityData().get(value);
	}

	public <T> void set(EntityDataAccessor<T> value, T par) {
		this.getEntityData().set(value, par);
	}

	public <T> void define(EntityDataAccessor<T> value, T par) {
		this.getEntityData().define(value, par);
	}

	public void setMaxLifeTime(int maxLifeTime) {
		this.set(SUMMON_TIME, maxLifeTime);
	}

	public int getMaxLifeTime() {
		return this.get(SUMMON_TIME);
	}

	public void setWandLevel(int wandLevel) {
		this.set(WAND_LEVEL, wandLevel);
	}

	public int getWandLevel() {
		return this.get(WAND_LEVEL);
	}

	public void setShit(boolean isShit) {
		this.set(IS_SHIT, isShit);
	}

	public boolean getShit() {
		return this.get(IS_SHIT);
	}

	public void setRange(float range) {
		this.set(RANGE, range);
	}

	public float getRange() {
		return this.get(RANGE);
	}

	public void setHealthArmor(float healthArmor) {
		this.set(HEALTH_ARMOR, healthArmor);
	}

	public float getHealthArmor() {
		return this.get(HEALTH_ARMOR);
	}

	public void setAlay(boolean alay) {
		this.set(ISALAY, alay);
	}

	public boolean getAlay() {
		return this.get(ISALAY);
	}

	public void setGolem(boolean golem) {
		this.set(ISGOLEM, golem);
	}

	public boolean getGolem() {
		return this.get(ISGOLEM);
	}

	public void setFox(boolean fox) {
		this.set(ISFOX, fox);
	}

	public boolean getFox() {
		return this.get(ISFOX);
	}

	public void setMaster(boolean master) {
		this.set(ISMASTER, master);
	}

	public boolean getMaster() {
		return this.get(ISMASTER);
	}

	public void setIfrit(boolean ifrit) {
		this.set(ISIFRIT, ifrit);
	}

	public boolean getIfrit() {
		return this.get(ISIFRIT);
	}

	public void setWindine(boolean windine) {
		this.set(ISWINDINE, windine);
	}

	public boolean getWindine() {
		return this.get(ISWINDINE);
	}

	public boolean canAttack() {
		return false;
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putInt("summonTime", this.getMaxLifeTime());
		tags.putInt("wandLevel", this.getWandLevel());
		tags.putInt("wandLevel", this.getWandLevel());
		tags.putFloat("range", this.getRange());
		tags.putFloat("healthArmor", this.getHealthArmor());
		tags.putBoolean("isShit", this.getShit());
		tags.putBoolean("isAlay", this.getAlay());
		tags.putBoolean("isGolem", this.getGolem());
		tags.putBoolean("isFox", this.getFox());
		tags.putBoolean("isMaster", this.getMaster());
		tags.putBoolean("isIfrit", this.getIfrit());
		tags.putBoolean("isWindine", this.getWindine());
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setMaxLifeTime(tags.getInt("summonTime"));
		this.setWandLevel(tags.getInt("wandLevel"));
		this.setRange(tags.getFloat("range"));
		this.setHealthArmor(tags.getFloat("healthArmor"));
		this.setShit(tags.getBoolean("isShit"));
		this.setAlay(tags.getBoolean("isAlay"));
		this.setGolem(tags.getBoolean("isGolem"));
		this.setFox(tags.getBoolean("isFox"));
		this.setMaster(tags.getBoolean("isMaster"));
		this.setIfrit(tags.getBoolean("isIfrit"));
		this.setWindine(tags.getBoolean("isWindine"));
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {
		Entity attacker = src.getEntity();
		if (attacker == null || !(attacker instanceof Enemy)) { return false; }

		if (src.getDirectEntity() instanceof Warden entity) {
			entity.hurt(SMDamage.MAGIC, amount);
			entity.invulnerableTime = 0;
			amount *= 0.1F;
		}

		float armor = this.getHealthArmor();
		if (armor > 0F) {
			this.setHealthArmor(armor - amount);
			amount = 0F;
		}

		this.hurtAction(attacker, amount);
		return super.hurt(src, Math.min(20F, amount));
	}

	public boolean doHurtTarget(Entity entity) {

		if (this.getFox()) {
			List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, this.isTarget(), 3.5D + this.getRange());
			targetList.forEach(e -> this.attackTarget(e));
			return true;
		}

		else {
			return this.attackTarget(entity);
		}
	}

	public boolean attackTarget(Entity entity) {

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

	public void hurtAction(Entity attacker, float amount) { }

	public float getDamageAmount(float amount) {
		return Math.min(20F, amount);
	}

	public void tamedState(WandInfo info) {

		int level = info.getLevel();
		double rate = Math.max(3.5D, (1D + (level - 1) * 0.075D));

		this.setWandLevel(level);
		this.setState(rate);
	}

	public void setState(double rate) {
		this.setAttribute(Attributes.MAX_HEALTH, rate);
		this.setAttribute(Attributes.ATTACK_DAMAGE, rate);
		this.setAttribute(Attributes.MOVEMENT_SPEED, Math.min(2D, rate));
		this.setAttribute(Attributes.ARMOR, rate);
		this.setHealth(this.getMaxHealth());
	}

	public void setAttribute(Attribute att, double rate) {
		this.getAttribute(att).setBaseValue(this.getAttributeValue(att) * rate);
	}

	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		InteractionResult result = InteractionResult.PASS;
		return this.isOwnedBy(player) ? this.mobClick(result, player, stack) : result;
	}

	public InteractionResult mobClick(InteractionResult result, Player player, ItemStack stack) {
		if (result.consumesAction() || !this.isOwnedBy(player)) { return result; }

		Item item = stack.getItem();
		if (item.isEdible() && this.getHealth() < this.getMaxHealth()) {
			int foodValue = stack.getFoodProperties(this).getNutrition();
			this.heal((float) foodValue);
			this.gameEvent(GameEvent.EAT, this);
			stack.shrink(1);
			this.playSound(SoundEvents.GENERIC_EAT, 0.25F, 0.9F + this.rand.nextFloat(0.2F));

			if (item instanceof SMFood food) {
				food.onFoodEat(this.getLevel(), this, stack);
				this.setMaxLifeTime(this.getMaxLifeTime() + foodValue * 50);

				if (this.isClient()) {
					this.spawnParticles(this.getLevel(), this.blockPosition().above());
				}
			}

			return InteractionResult.SUCCESS;
		}

		else if (!(item instanceof IWand) && !(item instanceof SummonerWand) && !(item instanceof SummonerWand) && !(item instanceof EvilArrowItem)) {

			if ((item.isEdible() || !this.getMainHandItem().isEmpty()) && this.foodAction(player, stack)) {
				return InteractionResult.SUCCESS;
			}

			this.setShitMob(!this.isOrderedToSit());
			return InteractionResult.SUCCESS;
		}

		return result;
	}

	public void setShitMob(boolean isShit) {
		this.setOrderedToSit(isShit);
		this.setShit(this.isOrderedToSit());
		this.getMoveControl().setWantedPosition(0D, 0D, 0D, 0D);
	}

	public boolean foodAction(Player player, ItemStack stack) {
		return false;
	}

	public void tick() {
		super.tick();
		int maxLifeTime = this.getMaxLifeTime();

		if (maxLifeTime <= 0) {
			this.setMaxLifeTime(6000);
		}

		if (this.tickCount >= maxLifeTime) {

			if (this.getLevel() instanceof ServerLevel server) {
				this.discordParticle(server);
			}

			this.discordAction();
			this.discard();
		}
	}

	// パーティクルスポーン
	public void spawnParticles(Level world, BlockPos pos) {
		ParticleOptions par = ParticleTypes.HEART;
		for (int i = 0; i < 6; ++i) {
			double d0 = this.rand.nextGaussian() * 0.02D;
			double d1 = this.rand.nextGaussian() * 0.02D;
			double d2 = this.rand.nextGaussian() * 0.02D;
			world.addParticle(par, this.getX() + this.getRand(1.5F), this.getY() + this.getRand(1F), this.getZ() + this.getRand(1.5F), d0, d1, d2);
		}
	}

	protected void customServerAiStep() {
		super.customServerAiStep();
		LivingEntity target = this.getTarget();
		if (target == null || this.tickCount % 20 != 0) { return; }

		if (!(target instanceof Enemy) || !target.isAlive()) {
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
		ParticleOptions par = ParticleInit.NORMAL;

		for (int i = 0; i < 64; ++i) {
			double d0 = this.getRand(this.rand) * 0.3D;
			double d1 = rand.nextFloat() * 0.4D;
			double d2 = this.getRand(this.rand) * 0.3D;
			server.sendParticles(par, this.getRandomX(pos), this.getRandomY(), this.getRandomZ(pos), 0, d0, d1 + 0.1D, d2, 1F);
		}
	}

	public void discordAction() { }

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

	public float getRand() {
		return this.rand.nextFloat();
	}

	public float getRand(float rate) {
		return (this.rand.nextFloat() - this.rand.nextFloat()) * rate;
	}

	// えんちちーリストの取得
	public <T extends Entity> List<T> getEntityList(Class<T> enClass, double range) {
		return WorldHelper.getEntityList(this, enClass, this.getAABB(range));
	}

	// えんちちーリストの取得
	public <T extends Entity> List<T> getEntityList(Class<T> enClass, Predicate<T> filter, AABB aabb) {
		return WorldHelper.getEntityList(this, enClass, filter, aabb);
	}

	// フィルターえんちちーリストの取得
	public <T extends Entity> List<T> getEntityList(Class<T> enClass, Predicate<T> filter, double range) {
		return WorldHelper.getEntityList(this, enClass, filter, this.getAABB(range));
	}

	// 範囲の取得
	public AABB getAABB(double range) {
		return this.getAABB(range, range / 2, range);
	}

	// 範囲の取得
	public AABB getAABB(double x, double y, double z) {
		return this.getBoundingBox().inflate(x, y, z);
	}

	// 範囲の取得
	public AABB getAABB(BlockPos pos, double range) {
		return new AABB(pos.offset(-range, -range, -range), pos.offset(range, range, range));
	}

	// ブロー耐性を持っていないえんちちーを取得
	public <T extends LivingEntity> Predicate<T> isNotBlow() {
		return e -> e.isAlive() && !e.hasEffect(PotionInit.resistance_blow) && this.canTargetEffect(e, this.getOwner());
	}

	// 射撃者によってえんちちーを取得
	public <T extends LivingEntity> Predicate<T> isTarget() {
		return e -> e.isAlive() && this.canTargetEffect(e, this.getOwner());
	}

	// 範囲内にいる射撃者によってえんちちーを取得
	public <T extends LivingEntity> Predicate<T> isBladTarget(double range) {
		return e -> e.isAlive() && this.canTargetEffect(e, this.getOwner()) && this.checkDistance(e.blockPosition(), range);
	}

	public boolean canTargetEffect(LivingEntity target, Entity owner) {
		return owner instanceof Player ? target instanceof Enemy : target instanceof Player;
	}

	// 範囲内にいるかのチェック
	public boolean checkDistance(BlockPos pos, double range) {
		return this.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= range;
	}

	protected void checkFallDamage(double par1, boolean par2, BlockState state, BlockPos pos) { }

	// パーティクルスポーン
	public void spawnParticleRing(ServerLevel server, ParticleOptions par, double range, BlockPos pos, double addY, double ySpeed, double moveValue) {

		double x = pos.getX() + 0.5D;
		double y = pos.getY() + 1D + addY;
		double z = pos.getZ() + 0.5D;

		for (double degree = -range * Math.PI; degree < range * Math.PI; degree += 0.25D) {
			double rate = range * 0.75D;
			server.sendParticles(par, x + Math.cos(degree) * rate, y, z + Math.sin(degree) * rate, 0, 0, ySpeed, 0, 1D);
		}
	}

	public boolean isBoss(Entity entity) {
		return entity.getType().is(TagInit.BOSS) || (entity instanceof AbstractSMMob mob && !mob.isLowRank()) || (entity instanceof LivingEntity liv && liv.hasEffect(PotionInit.leader_flag));
	}

	public void addPotion(LivingEntity entity, MobEffect potion, int time, int level) {

		if (this.isBoss(entity)) {
			time = time / 4;
			level = Math.min(0, level / 2);

			if (potion.equals(PotionInit.bubble)) {
				time = Math.min(10, time);
			}
		}

		PlayerHelper.setPotion(entity, potion, level, time);
	}

	// 火力取得( レベル × 0.2 ) + 最小( (レベル - 1) × 0.175, 5) + 最小( 最大(5 × (1 - (レベル - 1) × 0.02), 0), 4)
	public float getPower(float level) {
		return ( level * 0.2F ) + Math.min( (level - 1) * 0.255F, 5) + Math.min( Math.max(6 * (1 - (level - 1) * 0.0185F), 0), 5.8F);
	}

	@Override
	public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob mob) {
		return null;
	}

	public class NearestAttackSMMobGoal<T extends Monster> extends TargetGoal {

		@Nullable
		protected AbstractSummonMob attacker;
		protected LivingEntity target;
		protected final int randInterval;
		protected final Class<T> targetType;
		protected TargetingConditions targetConditions;

		public NearestAttackSMMobGoal(AbstractSummonMob mob, Class<T> entityClass, boolean flag) {
			this(mob, entityClass, 10, flag, false, (Predicate<LivingEntity>) null);
		}

		public NearestAttackSMMobGoal(AbstractSummonMob mob, Class<T> entityClass, boolean flag, Predicate<LivingEntity> entityPre) {
			this(mob, entityClass, 10, flag, false, entityPre);
		}

		public NearestAttackSMMobGoal(AbstractSummonMob mob, Class<T> entityClass, boolean flag1, boolean flag2) {
			this(mob, entityClass, 10, flag1, flag2, (Predicate<LivingEntity>) null);
		}

		public NearestAttackSMMobGoal(AbstractSummonMob mob, Class<T> entityClass, int par1, boolean flag1, boolean flag2, @Nullable Predicate<LivingEntity> entityPre) {
			super(mob, flag1, flag2);
			this.targetType = entityClass;
			this.randInterval = reducedTickDelay(par1);
			this.setFlags(EnumSet.of(Goal.Flag.TARGET));
			this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(entityPre);
			this.attacker = mob;
		}

		public boolean canUse() {
			if (this.attacker.isOrderedToSit()) { return false; }
			if (this.randInterval > 0 && this.mob.getRandom().nextInt(this.randInterval) != 0) { return false; }

			this.findTarget();
			return this.target != null;
		}

		protected AABB getTargetSearchArea(double par1) {
			return this.mob.getBoundingBox().inflate(par1, 4D, par1);
		}

		protected void findTarget() {
			List<T> entityList = this.mob.getLevel().getEntitiesOfClass(this.targetType, this.getTargetSearchArea(this.getFollowDistance()), e -> e.getTarget() != null && e.getTarget() instanceof Player);
			this.target = this.mob.getLevel().getNearestEntity(entityList, this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
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

		public AttackTargetGoal(AbstractSummonMob mob, Class<T> entityClass, boolean flag) {
			this(mob, entityClass, 10, flag, false, (Predicate<LivingEntity>) null);
		}

		public AttackTargetGoal(AbstractSummonMob mob, Class<T> entityClass, boolean flag, Predicate<LivingEntity> entityPre) {
			this(mob, entityClass, 10, flag, false, entityPre);
		}

		public AttackTargetGoal(AbstractSummonMob mob, Class<T> entityClass, boolean flag1, boolean flag2) {
			this(mob, entityClass, 10, flag1, flag2, (Predicate<LivingEntity>) null);
		}

		public AttackTargetGoal(AbstractSummonMob mob, Class<T> entityClass, int par1, boolean flag1, boolean flag2, @Nullable Predicate<LivingEntity> entityPre) {
			super(mob, entityClass, par1, flag1, flag2, entityPre);
			this.attacker = mob;
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

	public class RandomOwnerMoveGoal extends Goal {

		private final AbstractSummonMob mob;
		private final double addY;
		private int coolTime = 0;
		private double targetOldX = 0D;
		private double targetOldZ = 0D;

		public RandomOwnerMoveGoal(AbstractSummonMob mob, double addY) {
			this.setFlags(EnumSet.of(Goal.Flag.MOVE));
			this.mob = mob;
			this.addY = addY;
		}

		public boolean canUse() {
			return !this.mob.isOrderedToSit() && this.mob.getOwner() != null;
		}

		public boolean canContinueToUse() {
			return false;
		}

		public void tick() {

			BlockPos pos = this.mob.blockPosition();
			LivingEntity owner = this.mob.getOwner();
			double dis = this.mob.distanceTo(owner);
			boolean flag = owner.isAlive() && dis > 4;
			MoveControl move = this.mob.getMoveControl();

			if (dis > 8) {
				this.mob.teleportToOwner(owner);
				move.setWantedPosition(0D, 0D, 0D, 0D);
				this.targetOldX = move.getWantedX();
				this.targetOldZ = move.getWantedZ();
				this.coolTime = 3;
				return;
			}

			if (this.coolTime-- > 0) { return; }

			for (int i = 0; i < 3; ++i) {

				BlockPos pos1 = flag ? owner.blockPosition().above(2) : pos.offset(this.mob.randInt(7, 15), this.mob.randInt(5, 11), this.mob.randInt(7, 15));
				if (!this.mob.getLevel().isEmptyBlock(pos1)) { continue; }

				move.setWantedPosition((double) pos1.getX() + 0.5D, (double) pos1.getY() + 0.5D + this.addY, (double) pos1.getZ() + 0.5D, this.mob.getAttributeBaseValue(Attributes.FLYING_SPEED));

				if (this.mob.getTarget() == null) {
					this.mob.getLookControl().setLookAt((double) pos1.getX() + 0.5D, (double) pos1.getY() + 0.5D, (double) pos1.getZ() + 0.5D, 180F, 20F);
				}

				if(this.targetOldX != 0 || this.targetOldZ != 0) {
					move.setWantedPosition(this.mob.xo, this.mob.yo + 2D, this.mob.zo, this.mob.getAttributeBaseValue(Attributes.FLYING_SPEED));
					this.targetOldX = 0D;
					this.targetOldZ = 0D;
				}

				break;
			}
		}
	}

	public void teleportToOwner(LivingEntity owner) {
		BlockPos pos = owner.blockPosition();

		for (int i = 0; i < 10; ++i) {
			int x = this.randInt(-3, 3);
			int y = this.randInt(-1, 1);
			int z = this.randInt(-3, 3);
			if (this.maybeTeleport(owner, pos.getX() + x, pos.getY() + y, pos.getZ() + z)) { return; }
		}
	}

	public boolean maybeTeleport(LivingEntity owner, int x, int y, int z) {
		if (Math.abs(x - owner.getX()) < 2D && Math.abs(z - owner.getZ()) < 2D) { return false; }
		this.moveTo(x + 0.5D, y + 2D, z + 0.5D, this.getYRot(), this.getXRot());
		return true;
	}

	public int randInt(int min, int max) {
		return this.rand.nextInt(max - min + 1) + min;
	}

	public boolean isClient() {
		return this.getLevel().isClientSide();
	}
}
