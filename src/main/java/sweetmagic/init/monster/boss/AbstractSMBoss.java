package sweetmagic.init.entity.monster.boss;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
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
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.animal.AbstractSummonMob;
import sweetmagic.util.SMDamage;
import sweetmagic.util.WorldHelper;

public abstract class AbstractSMBoss extends Monster implements ISMMob, ISMTip {

	protected int tickTime = 0;	// tickタイム
	protected int defTime = 0;	// ダメージ無効化時間
	protected float healthO = -1;
	protected float healthArmorO = -1;
	private BlockPos spawnPos = null;
	public Random rand = new Random();
	private ServerBossEvent bossEvent = null;
	private static final EntityDataAccessor<Boolean> LECTERN = ISMMob.setData(AbstractSMBoss.class, BOOLEAN);
	private static final EntityDataAccessor<Boolean> HARD = ISMMob.setData(AbstractSMBoss.class, BOOLEAN);
	private static final EntityDataAccessor<Boolean> MAGIC = ISMMob.setData(AbstractSMBoss.class, BOOLEAN);
	protected static final EntityDataAccessor<Boolean> HALF_HEALTH = ISMMob.setData(AbstractSMBoss.class, BOOLEAN);
	private static final EntityDataAccessor<Integer> HEALTH_ARMOR_COUNT = ISMMob.setData(AbstractSMBoss.class, INT);
	private static final EntityDataAccessor<Integer> MAX_HEALTH_ARMOR_COUNT = ISMMob.setData(AbstractSMBoss.class, INT);
	private static final EntityDataAccessor<Float> HEALTH_ARMOR = ISMMob.setData(AbstractSMBoss.class, FLOAT);
	private static final EntityDataAccessor<Float> MAX_HEALTH_ARMOR = ISMMob.setData(AbstractSMBoss.class, FLOAT);

	public AbstractSMBoss(EntityType<? extends AbstractSMBoss> enType, Level world) {
		super(enType, world);
		this.xpReward = 500;
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.define(LECTERN, false);
		this.define(HARD, false);
		this.define(MAGIC, false);
		this.define(HALF_HEALTH, false);
		this.define(HEALTH_ARMOR_COUNT, 0);
		this.define(MAX_HEALTH_ARMOR_COUNT, 0);
		this.define(HEALTH_ARMOR, 0F);
		this.define(MAX_HEALTH_ARMOR, 0F);
	}

	public void refreshInfo() {
		this.reapplyPosition();
		this.refreshDimensions();
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1D));
		this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1D, 0F));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8F));
		this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Warden.class, 8F));
		this.goalSelector.addGoal(10, new SMRandomLookGoal(this));
		this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Warden.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Raider.class, true));
	}

	public SynchedEntityData getData() {
		return this.getEntityData();
	}

	public boolean getLectern() {
		return this.get(LECTERN);
	}

	public void setLectern(boolean isLectern) {
		this.set(LECTERN, isLectern);
	}

	public boolean getMagic() {
		return this.get(MAGIC);
	}

	public void setMagic(boolean isMagic) {
		this.set(MAGIC, isMagic);
	}

	public boolean getHard() {
		return this.get(HARD);
	}

	public void setHard(boolean isHard) {
		this.set(HARD, isHard);
	}

	public boolean getHalfHealth() {
		return this.get(HALF_HEALTH);
	}

	public void setHalfHealth(boolean halfHealth) {
		this.set(HALF_HEALTH, halfHealth);
	}

	public int getHealthArmorCount() {
		return this.get(HEALTH_ARMOR_COUNT);
	}

	public void setHealthArmorCount(int armor) {
		this.set(HEALTH_ARMOR_COUNT, armor);

		if(armor > 0 && this.getMaxHealthArmorCount() == 0) {
			this.setMaxHealthArmorCount(armor);
		}
	}

	public int getMaxHealthArmorCount() {
		return this.get(MAX_HEALTH_ARMOR_COUNT);
	}

	public void setMaxHealthArmorCount(int armor) {
		this.set(MAX_HEALTH_ARMOR_COUNT, armor);
	}

	public float getHealthArmor() {
		return this.get(HEALTH_ARMOR);
	}

	public void setHealthArmor(float armor) {
		this.set(HEALTH_ARMOR, armor);
	}

	public float getMaxHealthArmor() {
		return this.get(MAX_HEALTH_ARMOR);
	}

	public void setMaxHealthArmor(float armor) {
		this.set(MAX_HEALTH_ARMOR, armor);
	}

	public BlockPos getSpawnPos() {
		return this.spawnPos;
	}

	public void setSpawnPos(BlockPos spawnPos) {
		this.spawnPos = spawnPos;
	}

	public boolean isInvisible() {
		return this.getMagic();
	}

	// 周囲のプレイヤー取得
	public <T extends Player> List<T> getPlayer (Class<T> enClass) {
		return this.getEntityList(enClass, this, p -> !p.isSpectator() && p.isAlive(), 64D);
	}

	public <T extends Entity> List<T> getEntityList(Class<T> enClass, double range) {
		return this.getEntityList(enClass, this, p -> !p.isSpectator() && p.isAlive(), range);
	}

	public <T extends Entity> List<T> getEntityList(Class<T> enClass, Predicate<T> filter, double range) {
		return this.getEntityList(enClass, this, filter, range);
	}

	public <T extends Entity> List<T> getEntityList(Class<T> enClass, AABB aabb) {
		return WorldHelper.getEntityList(this, enClass, p -> !p.isSpectator() && p.isAlive(), aabb);
	}

	public <T extends Entity> List<T> getEntityList(Class<T> enClass, Predicate<T> filter, AABB aabb) {
		return WorldHelper.getEntityList(this, enClass, filter, aabb);
	}

	public <T extends Entity> List<T> getEntityList(Class<T> enClass, Vec3 vec, double range) {
		return WorldHelper.getEntityList(this, enClass, this.getAABB(vec, range));
	}

	public <T extends Entity> List<T> getEntityList(Class<T> enClass, Predicate<T> filter, Vec3 vec, double range) {
		return WorldHelper.getEntityList(this, enClass, filter, this.getAABB(vec, range));
	}

	public <T extends Entity> List<T> getEntityList(Class<T> enClass, BlockPos pos, double range) {
		return WorldHelper.getEntityList(this, enClass, this.getAABB(pos, range));
	}

	public <T extends Entity> List<T> getEntityList(Class<T> enClass, Predicate<T> filter, BlockPos pos, double range) {
		return WorldHelper.getEntityList(this, enClass, filter, this.getAABB(pos, range));
	}

	public void addEntity(Entity entity) {
		this.getLevel().addFreshEntity(entity);
	}

	// 範囲の取得
	public AABB getAABB(double range) {
		BlockPos pos = this.blockPosition();
		return new AABB(pos.offset(-range, -range, -range), pos.offset(range, range, range));
	}

	// 範囲の取得
	public AABB getAABB(BlockPos pos, double range) {
		return new AABB(pos.offset(-range, -range, -range), pos.offset(range, range, range));
	}

	// 範囲の取得
	public AABB getAABB(Vec3 vec, double range) {
		return new AABB(vec.x - range, vec.y - range, vec.z - range, vec.x + range, vec.y + range, vec.z + range);
	}

	public List<LivingEntity> getPlayerList(LivingEntity target) {
		return this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), 48D);
	}

	public float getPlayerCount(LivingEntity target) {
		return this.getPlayerCount(this.getPlayerList(target));
	}

	public float getPlayerCount(List<LivingEntity> targetList) {

		float count = 0F;

		for (LivingEntity target : targetList) {
			count += target instanceof Player ? 1F : 0.5F;
		}

		return count;
	}

	public void tick() {
		super.tick();

		if (this.defTime > 0) {
			this.defTime--;
		}

		if (this.tickCount % 20 == 0 && !this.isClient()) {
			this.checkSpawnPos();
		}
	}

	public void checkSpawnPos() {
		BlockPos pos = this.getSpawnPos();
		if (pos == null) { return; }

		double dis = this.distanceToSqr(pos.getX(), pos.getY(), pos.getZ());

		if (dis >= this.getDistance() || (this.getTarget() != null && !this.hasLineOfSight(this.getTarget()) && dis >= this.getDistance() * 0.5D)) {
			this.teleportSpawnPos(pos);
		}
	}

	public double getDistance() {
		return 1500D;
	}

	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance dif, MobSpawnType spawn, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		data = super.finalizeSpawn(world, dif, spawn, data, tag);
		this.clearInfo();
		this.spawnAction();
		this.addPotion(this, PotionInit.resistance_blow, 99999, 10);
		this.addPotion(this, PotionInit.reflash_effect, 99999, 0);
		this.setSpawnPos(this.blockPosition().above());
		return data;
	}

	public boolean isBossBarView() {
		return this.getBossEvent() != null;
	}

	public void setBossEvent(BossEvent.BossBarColor color, BossEvent.BossBarOverlay overlay) {
		this.bossEvent = (ServerBossEvent) (new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.NOTCHED_6)).setDarkenScreen(true);
	}

	public ServerBossEvent getBossEvent() {
		return this.bossEvent;
	}

	public void setCustomName(@Nullable Component tip) {
		super.setCustomName(tip);

		if (this.isBossBarView()) {
			this.getBossEvent().setName(this.getDisplayName());
		}
	}

	public void startSeenByPlayer(ServerPlayer player) {
		super.startSeenByPlayer(player);

		ServerBossEvent event = this.getBossEvent();
		if (this.isBossBarView() && event != null) {
			event.addPlayer(player);
		}
	}

	public void stopSeenByPlayer(ServerPlayer player) {
		super.stopSeenByPlayer(player);

		if (this.isBossBarView()) {
			this.getBossEvent().removePlayer(player);
		}
	}

	public void initBossBar() {
		if (!this.isBossBarView()) { return; }

		ServerBossEvent event = this.getBossEvent();
		float armor = this.getHealthArmor();

		if(armor > 0F) {

			if (this.healthArmorO == -1) {
				this.healthArmorO = armor;
			}

			else if(this.healthArmorO != armor) {
				this.healthArmorO = Math.max(armor, this.healthArmorO - this.getMaxHealthArmor() * 0.025F);
			}

			event.setProgress(this.healthArmorO / this.getMaxHealthArmor());
			event.setColor(BossBarColor.YELLOW);
			event.setName(this.getTipArray(this.getDisplayName(), " (", this.getArmorName(), ")"));
			return;
		}

		float health = this.getHealth();

		if (this.healthO == -1) {
			this.healthO = health;
		}

		else if(this.healthO != health) {
			this.healthO = Math.max(health, this.healthO - this.getMaxHealth() * 0.015F);
		}

		event.setProgress(this.healthO / this.getMaxHealth());

		if (this.isHalfHealth(this)) {
			event.setColor(BossBarColor.RED);
			event.setName(this.getDisplayName());
		}
	}

	public int getHealthArmorProgress(int value) {
		return this.isHalfHealth(this) ? (int) (value * (this.getHealthArmor() / this.getMaxHealthArmor())) : 0;
	}

	public MutableComponent getArmorName() {
		return this.getText("damage_armor");
	}

	public void checkPotion(MobEffect potion, int time, int level) {
		if (!this.hasEffect(potion)) {
			this.addPotion(this, potion, time, level);
		}
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putInt("tickTime", this.tickTime);
		tags.putBoolean("isLectern", this.getLectern());
		tags.putBoolean("isHard", this.getHard());
		tags.putBoolean("isMagic", this.getMagic());
		tags.putInt("health_armor_count", this.getHealthArmorCount());
		tags.putInt("max_health_armor_count", this.getMaxHealthArmorCount());
		tags.putFloat("health_armor", this.getHealthArmor());
		tags.putFloat("max_health_armor", this.getMaxHealthArmor());

		BlockPos spawnPos = this.getSpawnPos();

		if (spawnPos != null) {
			tags.putInt("spawnPosX", spawnPos.getX());
			tags.putInt("spawnPosY", spawnPos.getY());
			tags.putInt("spawnPosZ", spawnPos.getZ());
		}
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.tickTime = tags.getInt("tickTime");
		this.setLectern(tags.getBoolean("isLectern"));
		this.setHard(tags.getBoolean("isHard"));
		this.setMagic(tags.getBoolean("isMagic"));
		this.setHealthArmorCount(tags.getInt("health_armor_count"));
		this.setMaxHealthArmorCount(tags.getInt("max_health_armor_count"));
		this.setHealthArmor(tags.getFloat("health_armor"));
		this.setMaxHealthArmor(tags.getFloat("max_health_armor"));

		if (tags.contains("spawnPosX")) {
			this.setSpawnPos(new BlockPos(tags.getInt("spawnPosX"), tags.getInt("spawnPosY"), tags.getInt("spawnPosZ")));
		}

		if (this.hasCustomName() && this.isBossBarView()) {
			this.getBossEvent().setName(this.getDisplayName());
		}
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {

		float armor = this.getHealthArmor();
		if (armor > 0F) {
			this.setHealthArmor(armor - amount);
			amount = 0F;

			if(this.getHealthArmor() > 0) {
				this.playSound(SoundEvents.BLAZE_HURT, 0.67F, 1.25F);
			}

			else {
				this.playSound(SoundEvents.ITEM_BREAK, 2F, 1F);
			}
		}

		return super.hurt(src, amount);
	}

	protected void customServerAiStep() {
		super.customServerAiStep();
		this.initBossBar();

		LivingEntity target = this.getTarget();
		if(target != null) {
			this.checkPotion(PotionInit.resistance_blow, 99999, 10);

			if (this.isHalfHealth(this) && !this.getHalfHealth()) {
				this.halfHealthAction(target);
			}
		}
	}

	protected void halfHealthAction(LivingEntity target) {
		this.setHalfHealth(true);
		int count = this.getHealthArmorCount();

		if(count > 0) {
			this.armorHealthSet(target, count);
			this.setHealthArmorCount(0);
		}
	}

	protected void armorHealthSet(LivingEntity target, int count) {
		this.setMaxHealthArmor(this.getMaxHealth() * (0.2F + this.getPlayerCount(target) * 0.01F) * count);
		this.setHealthArmor(this.getMaxHealthArmor());
	}

	protected void teleportSpawnPos(BlockPos pos) {
		if (this.isAlive()) {
			double d0 = pos.getX() + (this.rand.nextDouble() - 0.5D) * 10D;
			double d1 = pos.getY();
			double d2 = pos.getZ() + (this.rand.nextDouble() - 0.5D) * 10D;

			if (this.getLevel().isEmptyBlock(pos)) {
				this.setPos(d0, d1, d2);
			}
		}
	}

	protected boolean teleport() {
		BlockPos spawnPos = this.getSpawnPos();
		if (!this.isClient() && this.isAlive() && spawnPos != null) {
			double d0 = spawnPos.getX() + (this.rand.nextDouble() - 0.5D) * 20D;
			double d1 = spawnPos.getY();
			double d2 = spawnPos.getZ() + (this.rand.nextDouble() - 0.5D) * 20D;
			return this.teleport(d0, d1, d2);
		}
		return false;
	}

	protected boolean teleport(double x, double y, double z) {
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);
		boolean flag = this.getLevel().getBlockState(pos).getMaterial().blocksMotion();
		if (!flag) { return false; }

		EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport(this, x, y, z);
		if (event.isCanceled()) { return false; }

		BlockPos beforePos = this.blockPosition();
		boolean flag2 = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);

		if (flag2) {
			this.getLevel().gameEvent(GameEvent.TELEPORT, this.position(), GameEvent.Context.of(this));
			if (!this.isSilent()) {
				this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1F, 1F);
			}

			this.teleportParticle(ParticleTypes.PORTAL, this.getLevel(), beforePos, this.blockPosition());
		}

		return flag2;
	}

	public void teleportParticle(ParticleOptions par, Level world, BlockPos beforePos, BlockPos afterPos) {
		if (!(this.getLevel() instanceof ServerLevel sever)) { return; }

		float pX = afterPos.getX() - beforePos.getX();
		float pY = afterPos.getY() - beforePos.getY();
		float pZ = afterPos.getZ() - beforePos.getZ();
		int count = Math.abs((int) (pX + pZ));

		for (int i = 0; i < count; i++) {
			for (int k = 0; k < 16; k++) {

				float randX = this.getRandFloat(1.5F);
				float randY = this.getRandFloat(1.5F);
				float randZ = this.getRandFloat(1.5F);
				float ax = beforePos.getX() + 0.5F + randX+ pX * (i / (float) count);
				float ay = beforePos.getY() + 1.25F + randY + pY * (i / (float) count);
				float az = beforePos.getZ() + 0.5F + randZ + pZ * (i / (float) count);

				sever.sendParticles(par, ax, ay, az, 0, 0F, 0F, 0F, 1F);
			}
		}
	}

	protected void spawnParticleCycle(BlockPos pos, double range) {
		if (!(this.getLevel() instanceof ServerLevel server) || pos == null) { return; }

		int count = 18;

		for (int i = 0; i < count; i++) {
			this.spawnParticleCycle(server, ParticleInit.CYCLE_ELECTRIC, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, Direction.UP, range, i * 20F, false);
		}
	}

	// パーティクルスポーンサイクル
	protected void spawnParticleCycle (ServerLevel server, ParticleOptions par, double x, double y, double z, Direction face, double range, double angle, boolean isRevese) {
		int way = isRevese ? -1 : 1;
		server.sendParticles(par, x, y, z, 0, face.get3DDataValue() * way, range, angle + way * 1 * 6F, 1F);
	}

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

	// パーティクルスポーン
	public void spawnParticleRing(ServerLevel server, ParticleOptions par, double range, BlockPos pos, double addY) {

		double x = pos.getX() + 0.5D;
		double y = pos.getY() + 1D + addY;
		double z = pos.getZ() + 0.5D;

		for (double degree = -range * Math.PI; degree < range * Math.PI; degree += 0.25D) {
			double rate = range * 0.75D;
			server.sendParticles(par, x + Math.cos(degree) * rate, y, z + Math.sin(degree) * rate, 0, Math.cos(degree) * 0.65D, 0, Math.sin(degree) * 0.65D, 1D);
		}
	}

	// 範囲内にいるかのチェック
	public boolean checkDistances(BlockPos basePos, BlockPos pos, double range) {
		double d0 = basePos.getX() - pos.getX();
		double d1 = basePos.getY() - pos.getY();
		double d2 = basePos.getZ() - pos.getZ();
		return (d0 * d0 + d1 * d1 + d2 * d2) <= range;
	}

	public int randRange(int range) {
		return range - this.rand.nextInt(range) * 2;
	}

	public float getRandFloat(float rate) {
		return (this.rand.nextFloat() - this.rand.nextFloat()) * rate;
	}

	// 範囲内にいるかのチェック
	public boolean checkDistance(BlockPos targetPos, BlockPos pos, double range) {
		return Math.abs(targetPos.getX() - pos.getX() + targetPos.getZ() - pos.getZ()) <= range;
	}

	// デスポーンしないように
	public void checkDespawn() {

		if (this.getLevel().getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
			this.discard();
		}

		else {
			this.noActionTime = 0;
		}
	}

	protected void tickDeath() {

		this.deathTime++;
		this.deathEffect();

		if (this.deathTime >= this.getMaxDeathTime() && !this.isClient()) {
			this.deathFinish();
			this.getLevel().broadcastEntityEvent(this, (byte) 60);
			this.remove(Entity.RemovalReason.KILLED);
		}

		if (this.isBossBarView()) {
			this.getBossEvent().setProgress(0F);
		}
	}

	public void deathEffect() {
		if (this.deathTime % 11 == 0 && this.deathTime < this.getMaxDeathTime()) {
			this.playSound(SoundEvents.GENERIC_EXPLODE, 3F, 1F / (this.rand.nextFloat() * 0.2F + 0.9F));

			if (this.getLevel() instanceof ServerLevel sever) {
				sever.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY() + 0.5D, this.getZ(), 2, 0D, 0D, 0D, 0D);
			}
		}
	}

	public int getMaxDeathTime() {
		return 66;
	}

	protected void deathFinish() {}

	// 落下ダメージ
	public boolean causeFallDamage(float par1, float par2, DamageSource src) {
		return false;
	}

	public DamageSource getSRC() {
		return SMDamage.getMagicDamage(this, this);
	}

	// 危険な果実でのダメージカット時
	public void specialDamageCut(DamageSource src, List<Player> playerList, String text) {
		this.teleport();
		this.playSound(SoundEvents.BLAZE_HURT, 2F, 0.85F);

		if (!this.isClient() && src.getEntity() instanceof Player player && !playerList.contains(player)) {
			playerList.add(player);
			player.sendSystemMessage(this.getText(text).withStyle(RED));
		}
	}

	// 体力半分時確率テレポート
	public void halfHealthTeleport() {
		if (this.isHalfHealth(this) && this.rand.nextFloat() >= 0.34F) {
			this.teleport();
		}
	}

	// 低ランクかどうか
	public boolean isLowRank() {
		return false;
	}

	public Predicate<LivingEntity> getFilter(boolean isPlayer) {
		return e -> !e.isSpectator() && e.isAlive() && (isPlayer ? (e instanceof Player || e instanceof AbstractSummonMob) : !(e instanceof Player) ) && !(e instanceof ISMMob);
	}

	// バフによるダメージ増減
	public float getBuffPower() {

		float damage = 0F;

		// 攻撃力上昇
		if (this.hasEffect(MobEffects.DAMAGE_BOOST)) {
			int level = this.getEffect(MobEffects.DAMAGE_BOOST).getAmplifier() + 1;
			damage += (level * 2);
		}

		// 弱体化
		if (this.hasEffect(MobEffects.WEAKNESS)) {
			int level = this.getEffect(MobEffects.WEAKNESS).getAmplifier() + 1;
			damage -= level;
		}

		return damage;
	}

	public void sendMSG(List<Player> playerList, Component com) {
		playerList.forEach(p -> p.sendSystemMessage(com));
	}

	public void attackDamage(List<LivingEntity> entityList, DamageSource src, float damage) {
		entityList.forEach(e -> this.attackDamage(e, src, damage));
	}

	public void attackDamage(Entity entity, DamageSource src, float damage) {
		entity.hurt(src, entity instanceof Warden ? damage * 5F : damage);
		entity.invulnerableTime = 0;
	}

	public boolean isClient() {
		return this.getLevel().isClientSide();
	}

	public void setOwnerID(LivingEntity entity) {}

	public void startInfo() {
		if (this.getBossEvent() == null) {
			this.setBossEvent(BC_BLUE, NOTCHED_6);
		}

		else {
			this.setSpawnPos(this.blockPosition().above());
		}
	}

	public void spawnAction() {}

	public abstract void clearInfo();

	public abstract boolean isArmorEmpty();

	public class SMRandomLookGoal extends RandomLookAroundGoal {

		private final AbstractSMBoss mob;

		public SMRandomLookGoal(AbstractSMBoss mob) {
			super(mob);
			this.mob = mob;
		}

		public boolean canUse() {
			return super.canUse() && this.mob.getTarget() == null;
		}
	}
}
