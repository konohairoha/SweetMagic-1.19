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
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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

public abstract class AbstractSMBoss extends Monster implements ISMMob, ISMTip {

	protected int tickTime = 0;							// tickタイム
	protected int defTime = 0;							// ダメージ無効化時間
	private BlockPos spawnPos = null;
	protected Random rand = new Random();
	private static final EntityDataAccessor<Boolean> ISLECTERN = ISMMob.setData(AbstractSMBoss.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> ISHARD = ISMMob.setData(AbstractSMBoss.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> ISMAGIC = ISMMob.setData(AbstractSMBoss.class, EntityDataSerializers.BOOLEAN);
	protected static final EntityDataAccessor<Boolean> HALFHEALTH = ISMMob.setData(AbstractSMBoss.class, BOOLEAN);

	public AbstractSMBoss(EntityType<? extends AbstractSMBoss> enType, Level world) {
		super(enType, world);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ISLECTERN, false);
		this.entityData.define(ISHARD, false);
		this.entityData.define(ISMAGIC, false);
	}

	public void refreshInfo() {
		this.reapplyPosition();
		this.refreshDimensions();
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
		this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Warden.class, 8.0F));
		this.goalSelector.addGoal(10, new SMRandomLookGoal(this));
		this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Warden.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Raider.class, true));
	}

	public boolean isLectern () {
		return this.entityData.get(ISLECTERN);
	}

	public void setLectern (boolean isLectern) {
		this.entityData.set(ISLECTERN, isLectern);
	}

	public boolean isMagic () {
		return this.entityData.get(ISMAGIC);
	}

	public void setMagic (boolean isMagic) {
		this.entityData.set(ISMAGIC, isMagic);
	}

	public boolean isHard () {
		return this.entityData.get(ISHARD);
	}

	public void setHard (boolean isHard) {
		this.entityData.set(ISHARD, isHard);
	}

	public boolean getHalfHealth () {
		return this.entityData.get(HALFHEALTH);
	}

	public void setHalfHealth (boolean isHalfHealth) {
		this.entityData.set(HALFHEALTH, isHalfHealth);
	}

	public BlockPos getSpawnPos () {
		return this.spawnPos;
	}

	public void setSpawnPos (BlockPos spawnPos) {
		this.spawnPos = spawnPos;
	}

	public boolean isInvisible() {
		return this.isMagic();
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
		return this.level.getEntitiesOfClass(enClass, aabb).stream().filter(p -> !p.isSpectator() && p.isAlive()).toList();
	}

	public <T extends Entity> List<T> getEntityList(Class<T> enClass, Predicate<T> filter, AABB aabb) {
		return this.level.getEntitiesOfClass(enClass, aabb).stream().filter(filter).toList();
	}

	public <T extends Entity> List<T> getEntityList(Class<T> enClass, Vec3 vec, double range) {
		return this.level.getEntitiesOfClass(enClass, this.getAABB(vec, range));
	}

	public <T extends Entity> List<T> getEntityList(Class<T> enClass, Predicate<T> filter, Vec3 vec, double range) {
		return this.level.getEntitiesOfClass(enClass, this.getAABB(vec, range)).stream().filter(filter).toList();
	}

	public <T extends Entity> List<T> getEntityList(Class<T> enClass, BlockPos pos, double range) {
		return this.level.getEntitiesOfClass(enClass, this.getAABB(pos, range));
	}

	// 範囲の取得
	public AABB getAABB (double range) {
		BlockPos pos = this.blockPosition();
		return new AABB(pos.offset(-range, -range, -range), pos.offset(range, range, range));
	}

	// 範囲の取得
	public AABB getAABB (BlockPos pos, double range) {
		return new AABB(pos.offset(-range, -range, -range), pos.offset(range, range, range));
	}

	// 範囲の取得
	public AABB getAABB (Vec3 vec, double range) {
		return new AABB(vec.x - range, vec.y - range, vec.z - range, vec.x + range, vec.y + range, vec.z + range);
	}

	public void tick() {
		super.tick();

		if (this.defTime > 0) {
			this.defTime--;
		}
	}

	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance dif, MobSpawnType spawn, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		data = super.finalizeSpawn(world, dif, spawn, data, tag);
		this.clearInfo();
		this.addPotion(this, PotionInit.resistance_blow, 99999, 4);
		this.addPotion(this, PotionInit.reflash_effect, 99999, 0);
		this.setSpawnPos(this.blockPosition().above());
		return data;
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putInt("tickTime", this.tickTime);
		tags.putBoolean("isLectern", this.isLectern());
		tags.putBoolean("isHard", this.isHard());
		tags.putBoolean("isMagic", this.isMagic());

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

		if (tags.contains("spawnPosX")) {
			this.setSpawnPos(new BlockPos(tags.getInt("spawnPosX"), tags.getInt("spawnPosY"), tags.getInt("spawnPosZ")));
		}
	}

	protected boolean teleport() {
		BlockPos spawnPos = this.getSpawnPos();
		if (!this.level.isClientSide() && this.isAlive() && spawnPos != null) {
			double d0 = spawnPos.getX() + (this.random.nextDouble() - 0.5D) * 20D;
			double d1 = spawnPos.getY() + (double) (this.random.nextInt(4) - 2);
			double d2 = spawnPos.getZ() + (this.random.nextDouble() - 0.5D) * 20D;
			return this.teleport(d0, d1, d2);
		}
		return false;
	}

	protected boolean teleport(double x, double y, double z) {

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);
		boolean flag = this.level.getBlockState(pos).getMaterial().blocksMotion();
		if (!flag) { return false; }

		EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport(this, x, y, z);
		if (event.isCanceled()) { return false; }

		BlockPos beforePos = this.blockPosition();
		boolean flag2 = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);

		if (flag2) {
			this.level.gameEvent(GameEvent.TELEPORT, this.position(), GameEvent.Context.of(this));
			if (!this.isSilent()) {
				this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1F, 1F);
			}

			this.teleportParticle(ParticleTypes.PORTAL, this.level, beforePos, this.blockPosition());
		}

		return flag2;
	}

	public void teleportParticle (ParticleOptions par, Level world, BlockPos beforePos, BlockPos afterPos) {

		if ( !(this.level instanceof ServerLevel sever) ) { return; }

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

	protected void spawnParticleCycle (BlockPos pos, double range) {

		if ( !(this.level instanceof ServerLevel server) || pos == null) { return; }

		int count = 18;

		for (int i = 0; i < count; i++) {
			this.spawnParticleCycle(server, ParticleInit.CYCLE_ELECTRIC.get(), pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, Direction.UP, range, i * 20F, false);
		}
	}

	// パーティクルスポーンサイクル
	protected void spawnParticleCycle (ServerLevel server, ParticleOptions particle, double x, double y, double z, Direction face, double range, double angle, boolean isRevese) {
		int way = isRevese ? -1 : 1;
		server.sendParticles(particle, x, y, z, 0, face.get3DDataValue() * way, range, angle + way * 1 * 6F, 1F);
	}

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
	public boolean checkDistances (BlockPos basePos, BlockPos pos, double range) {
		double d0 = basePos.getX() - pos.getX();
		double d1 = basePos.getY() - pos.getY();
		double d2 = basePos.getZ() - pos.getZ();
		return (d0 * d0 + d1 * d1 + d2 * d2) <= range;
	}

	public int randRange (int range) {
		return range - this.rand.nextInt(range) * 2;
	}

	public float getRandFloat (float rate) {
		return (this.rand.nextFloat() - this.rand.nextFloat()) * rate;
	}

	// 範囲内にいるかのチェック
	public boolean checkDistance (BlockPos targetPos, BlockPos pos, double range) {
		return Math.abs(targetPos.getX() - pos.getX() + targetPos.getZ() - pos.getZ()) <= range;
	}

	// デスポーンしないように
	public void checkDespawn() {

		if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
			this.discard();
		}

		else {
			this.noActionTime = 0;
		}
	}

	protected void tickDeath() {

		this.deathTime++;

		if (this.deathTime % 11 == 0 && this.deathTime < 66) {

			this.playSound(SoundEvents.GENERIC_EXPLODE, 3F, 1F / (this.random.nextFloat() * 0.2F + 0.9F));

			if (this.level instanceof ServerLevel sever) {
				sever.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY() + 0.5D, this.getZ(), 2, 0D, 0D, 0D, 0D);
			}
		}

		if (this.deathTime >= 66 && !this.level.isClientSide()) {
			this.level.broadcastEntityEvent(this, (byte) 60);
			this.remove(Entity.RemovalReason.KILLED);
		}
	}

	// 落下ダメージ
	public boolean causeFallDamage(float par1, float par2, DamageSource src) {
		return false;
	}

	public DamageSource getSRC() {
		return SMDamage.getMagicDamage(this, this);
	}

	// 危険な果実でのダメージカット時
	public void specialDamageCut (DamageSource src, List<Player> playerList, String text) {
		this.teleport();
		this.playSound(SoundEvents.BLAZE_HURT, 2F, 0.85F);

		if (!this.level.isClientSide && src.getEntity() instanceof Player player && !playerList.contains(player)) {
			playerList.add(player);
			player.sendSystemMessage(this.getText(text).withStyle(RED));
		}
	}

	// 体力半分時確率テレポート
	public void halfHealthTeleport () {
		if (this.isHalfHealth(this) && this.rand.nextFloat() >= 0.34F) {
			this.teleport();
		}
	}

	// 低ランクかどうか
	public boolean isLowRank () {
		return false;
	}

	public Predicate<LivingEntity> getFilter (boolean isPlayer) {
		return e -> !e.isSpectator() && e.isAlive() && (isPlayer ? (e instanceof Player || e instanceof AbstractSummonMob) : !(e instanceof Player) ) && !(e instanceof ISMMob);
	}

	// バフによるダメージ増減
	public float getBuffPower () {

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

	public ServerBossEvent getBossBar (BossEvent.BossBarColor color, BossEvent.BossBarOverlay overlay) {
		return (ServerBossEvent) (new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.NOTCHED_6)).setDarkenScreen(true);
	}

	public void sendMSG (List<Player> playerList, Component com) {
		playerList.forEach(p -> p.sendSystemMessage(com));
	}

	public void attackDamage (List<LivingEntity> entityList, DamageSource src, float damage) {
		entityList.forEach(e -> this.attackDamage(e, src, damage));
	}

	public void attackDamage (Entity entity, DamageSource src, float damage) {
		entity.hurt(src, damage);
		entity.invulnerableTime = 0;
	}

	public void setOwnerID (LivingEntity entity) { }

	public void startInfo() { }

	public abstract void clearInfo();

	public abstract boolean isArmorEmpty();

	public class SMRandomLookGoal extends RandomLookAroundGoal {

		private final Mob mob;

		public SMRandomLookGoal(Mob mob) {
			super(mob);
			this.mob = mob;
		}

		public boolean canUse() {
			return super.canUse() && this.mob.getTarget() == null;
		}
	}
}
