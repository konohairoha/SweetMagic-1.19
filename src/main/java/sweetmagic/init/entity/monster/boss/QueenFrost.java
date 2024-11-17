package sweetmagic.init.entity.monster.boss;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.entity.animal.AbstractSummonMob;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.entity.projectile.EvilArrow;
import sweetmagic.init.entity.projectile.FrostMagicShot;
import sweetmagic.util.SMDamage;

public class QueenFrost extends AbstractSMBoss {

	private int recastTime = 0;							// リキャストタイム
	private int attackType = 0;							// 攻撃種別

	private int frostRainTime = 0;						// フロストレインの攻撃時間
	private int chargeTime = 0;							// フロストレインの溜め時間
	private int laserTime = 0;							// フロスレ-ザーの攻撃時間

	private static final int FROSTRAIN_MAXTIME = 70;	// フロストレインの最大攻撃時間
	private static final int CHARGE_MAXTIME = 80;		// フロストレインの最大溜め時間
	private static final int LASER_MAXTIME = 15;		// フロスレ-ザーの最大攻撃時間

	private int leserTotalTime = 0;						// フロスレ-ザーの合計攻撃時間

	private BlockPos pos = null;
	private BlockPos targetOldPos = null;
	private Vec3 look = this.getViewVector(1.0F);
	private List<Player> playerList = new ArrayList<>();

	private static final EntityDataAccessor<Integer> ARMOR = ISMMob.setData(QueenFrost.class, INT);
	private static final EntityDataAccessor<Boolean> ISLASER = ISMMob.setData(QueenFrost.class, BOOLEAN);
	private static final EntityDataAccessor<Boolean> ISLECTERN = ISMMob.setData(QueenFrost.class, BOOLEAN);
	private static final EntityDataAccessor<Boolean> ISMAGIC = ISMMob.setData(QueenFrost.class, BOOLEAN);

	public QueenFrost(Level world) {
		super(EntityInit.queenFrost, world);
	}

	public QueenFrost(EntityType<QueenFrost> enType, Level world) {
		super(enType, world);
		this.xpReward = 300;
		this.maxUpStep = 1.25F;
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 300D)
				.add(Attributes.MOVEMENT_SPEED, 0.3D)
				.add(Attributes.ATTACK_DAMAGE, 4D)
				.add(Attributes.ARMOR, 6D)
				.add(Attributes.FOLLOW_RANGE, 64D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 64D);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ARMOR, 0);
		this.entityData.define(ISLASER, false);
		this.entityData.define(ISLECTERN, false);
		this.entityData.define(ISMAGIC, false);
	}

	public boolean isWithinRestriction() {
		return super.isWithinRestriction() || this.isLaser();
	}

	protected SoundEvent getAmbientSound() {
		return SoundInit.QUEEN_VOICE;
	}

	protected SoundEvent getHurtSound(DamageSource src) {
		return SoundInit.QUEEN_DAME;
	}

	protected SoundEvent getDeathSound() {
		return SoundInit.QUEEN_DAME;
	}

	public float getSoundVolume() {
		return 0.25F;
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {

		Entity attacker = src.getEntity();
		Entity attackEntity = src.getDirectEntity();
		if ( attacker != null && attacker instanceof ISMMob) { return false; }

		// ボスダメージ計算
		amount = this.getBossDamageAmount(this.level, this.defTime , src, amount, 10F);
		this.defTime = amount > 0 ? 2 : this.defTime;

		// 魔法攻撃以外なら反撃&ダメージ無効
		if (this.notMagicDamage(attacker, attackEntity)) {
			this.attackDamage(attacker, SMDamage.magicDamage, amount);
			return false;
		}

		// 魔導士の召喚台座による召喚の場合
		if (this.isLectern()) {
			this.getLecternAction(src, amount, this.getArmor());
		}

		return super.hurt(src, amount);
	}

	// 召喚台座時のダメージ処理
	public float getLecternAction (DamageSource src, float amount, int armorSize) {

		if (armorSize > 0) {

			if (src.getDirectEntity() instanceof EvilArrow) {
				this.getEvilDamage(amount, armorSize);
			}

			else {
				amount *= 0.25F;
				this.specialDamageCut(src, this.playerList, "queen_damecut");
			}
		}

		// バリアが張られていない場合、体力半分時確率テレポート
		else {
			this.halfHealthTeleport();
		}

		return amount;
	}

	// 危険な果実でのダメージ計算
	public float getEvilDamage (float amount, int armorSize) {

		this.setArmor(armorSize - 1);
		amount = 25F / this.getEntityList(Player.class, 80F).size();
		this.playSound(SoundEvents.GLASS_BREAK, 3F, 1.1F);

		if (!this.level.isClientSide && this.getArmor() <= 0 && !this.playerList.isEmpty()) {
			this.sendMSG(this.playerList, this.getText("queen_break").withStyle(GREEN));
		}

		return amount;
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putInt("armor", this.getArmor());
		tags.putInt("recastTime", this.recastTime);
		tags.putInt("attackType", this.attackType);
		tags.putInt("frostRainTime", this.frostRainTime);
		tags.putInt("chargeTime", this.chargeTime);
		tags.putInt("laserTime", this.laserTime);
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setArmor(tags.getInt("armor"));
		this.recastTime = tags.getInt("recastTime");
		this.attackType = tags.getInt("attackType");
		this.frostRainTime = tags.getInt("frostRainTime");
		this.chargeTime = tags.getInt("chargeTime");
		this.laserTime = tags.getInt("laserTime");
	}

	public int getArmor () {
		return this.entityData.get(ARMOR);
	}

	public void setArmor (int size) {
		this.entityData.set(ARMOR, size);
	}

	public boolean isLaser () {
		return this.entityData.get(ISLASER);
	}

	// アーマーがないなら
	public boolean isArmorEmpty () {
		return this.getArmor() <= 0;
	}

	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance dif, MobSpawnType spawn, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		data = super.finalizeSpawn(world, dif, spawn, data, tag);
		this.setArmor(3 * this.getEntityList(Player.class, 80D).size());
		return data;
	}

	public void tick() {
		super.tick();

		if (this.level.isClientSide) {

			Vec3 vec = this.getDeltaMovement();
			float x = (float) (this.getX() - 0.5F + this.rand.nextFloat());
			float y = (float) (this.getY() + 1F);
			float z = (float) (this.getZ() - 0.5F + this.rand.nextFloat());

			float f1 = (float) ( (vec.x + 0.5F - this.rand.nextFloat() ) * 0.2F);
			float f2 = 0F;
			float f3 = (float) ( (vec.z + 0.5F - this.rand.nextFloat() ) * 0.2F);
			this.level.addParticle(ParticleInit.FROST.get(), x, y, z, f1, f2, f3);
		}

		BlockPos spawnPos = this.getSpawnPos();
		if (this.tickCount % 20 == 0 && spawnPos != null) {

			double distance = this.distanceToSqr(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());

			if (distance >= 2000) {
				this.teleport();
			}
		}
	}

	protected boolean teleport() {
		BlockPos spawnPos = this.getSpawnPos();
		if (!this.level.isClientSide() && this.isAlive() && spawnPos != null) {
			double d0 = spawnPos.getX() + (this.rand.nextDouble() - 0.5D) * 32D;
			double d1 = spawnPos.getY() + (double) (this.rand.nextInt(8) - 4);
			double d2 = spawnPos.getZ() + (this.rand.nextDouble() - 0.5D) * 32D;
			return this.teleport(d0, d1, d2);
		}
		return false;
	}

	protected void customServerAiStep() {

		super.customServerAiStep();
		LivingEntity target = this.getTarget();
		if (target == null) { return; }

		this.getLookControl().setLookAt(target, 10F, 10F);
		if (this.recastTime-- > 0) { return; }

		// 体力が半分以上なら1stフェーズ攻撃
		if (!this.isHalfHealth(this)) {
			this.firstPhaseSttack(target);
		}

		// 体力が半分以下なら2ndフェーズ攻撃
		else {
			this.secondPhaseSttack(target);
		}
	}

	// 1stフェーズの攻撃
	public void firstPhaseSttack (LivingEntity target) {

		boolean isPlayer = this.isPlayer(target);

		// 攻撃種別がフロアストスプアの場合
		if (this.attackType == 0) {
			this.frostSpia(isPlayer);
		}

		// 攻撃種別がフロストレインの場合
		else {
			this.frostRain(target, isPlayer);
		}
	}

	// 2ndフェーズの攻撃
	public void secondPhaseSttack (LivingEntity target) {

		// 向きを強制設定（これしないと変な方向になるときがある）
        double d1 = target.getX() - this.getX();
		double d2 = target.getZ() - this.getZ();
		this.setYRot(-((float) Math.atan2(d1, d2)) * (180F / (float) Math.PI));
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();
		float damage = this.getBuffPower() * 0.25F;
		boolean isPlayer = target instanceof Player;

        // 時間の経過と射撃中の状態設定
		this.tickTime++;
		this.entityData.set(ISLASER, true);
		List<Player> targetPlayerList = this.getPlayer(Player.class);

		if (this.leserTotalTime == 0) {
			this.leserTotalTime = targetPlayerList.size() * LASER_MAXTIME;
		}

		if (this.leserTotalTime % LASER_MAXTIME == 0 && targetPlayerList.size() >= 2) {
			target = targetPlayerList.get(rand.nextInt(targetPlayerList.size() - 1));
			this.setTarget(target);
		}

		// 一定時間ごとにターゲット座標の設定
		if (this.tickTime % 15 == 0 || this.targetOldPos == null) {
			this.targetOldPos = target.blockPosition();
			this.look = this.getViewVector(1.0F);
		}

		// 攻撃者の座標取得
		Vec3 attackerPos = new Vec3(this.getX(), this.getY(), this.getZ());
		Vec3 src = attackerPos.add(0, this.getEyeHeight(), 0);

		if (this.tickTime % 6 == 0) {
			this.playSound(SoundInit.LASER, 0.3F, 1F);
		}

		// 一定時間ごとにダメージを発生させる
		if (this.tickTime % 8 == 0) {

			// 攻撃した人をリストに入れる。
			List<LivingEntity> entityAllList = new ArrayList<>();
			this.laserTime++;

			// 30ブロック先まで
			for (double i = 0; i < 30D; i += 0.5D) {

				// 攻撃した人をリストに含まれないプレイヤーリストを取得
				Vec3 dest = src.add(this.look.x * i, this.look.y * i, this.look.z * i);
				List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.getFilter(isPlayer, entityAllList), dest, 1D);
				entityAllList.addAll(entityList);
				this.attackDamage(entityList, SMDamage.flostDamage, 1F +damage);
	        }
		}

		// パーティクルを出す
		if (this.level instanceof ServerLevel server) {

			BlockPos p = this.targetOldPos.below();

			for (int i = 0; i < 20; i++) {

				if (this.rand.nextFloat() >= 0.65F) { continue; }

				Vec3 dest = src.add(this.look.x * i, this.look.y, this.look.z * i);
				float f1 = (float) (dest.x - 0.5F + this.rand.nextFloat() );
				float f2 = (float) (dest.y - 1F + this.rand.nextFloat() );
				float f3 = (float) (dest.z - 0.5F + this.rand.nextFloat() );
				float x = (float) ( ( p.getX() - this.getX() ) / 10F);
				float y = (float) ( ( p.getY() - this.getY() ) / 10F);
				float z = (float) ( ( p.getZ() - this.getZ() ) / 10F);
				server.sendParticles(ParticleInit.FROST_LASER.get(), f1, f2, f3, 0, x, y, z, 1F);
			}
		}

		// 一定時間経ったらステータスの初期化
		if (this.laserTime >= LASER_MAXTIME) {
			this.clearInfo();
		}
	}

	public void frostSpia (boolean isPlayer) {

		// 周囲の攻撃可能なプレイヤーの取得
		int level = 5;
		float damage = this.getBuffPower();
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.getFilter(isPlayer, false), 32D);

		for (LivingEntity living : entityList) {

			double x = living.getX() - this.getX();
			double y = living.getY(0.3333333333333333D) - this.getY() - this.getEyeHeight() / 2D;
			double z = living.getZ() - this.getZ();
			double xz = Math.sqrt(x * x + z * z);

			AbstractMagicShot entity = new FrostMagicShot(this.level, this, ItemStack.EMPTY);
			entity.setHitDead(false);
			entity.setData(1);
			entity.setWandLevel(level);
			entity.shoot(x, y - xz * 0.035D, z, 3F, 0F);
			entity.setAddDamage( entity.getAddDamage() - 3F + damage);
			this.level.addFreshEntity(entity);
		}

		// リキャストタイムと攻撃種別の再設定
		this.recastTime = (int) (( this.rand.nextInt(40) + 40 ) * ( this.isHalfHealth(this) ? 0.75F : 1F));
		this.attackType = this.rand.nextInt(2);
	}

	public void frostRain (LivingEntity target, boolean isPlayer) {

		// パーティクルを出すように
		if (this.chargeTime % 10 == 0) {
			this.spawnParticleCycle(target);
		}

		// フロストレインのチャージ時間を満たした場合
		if (this.chargeTime++ >= CHARGE_MAXTIME) {

			this.frostRainTime++;
			int level = 2;
			float damage = this.getBuffPower();

			if (this.pos == null) {
				this.pos = target.blockPosition();
			}

			for (int i = 0; i < 4; i++) {
				BlockPos targetPos = this.pos.offset(this.rand.nextInt(10) - this.rand.nextInt(10), 10, this.rand.nextInt(10) - this.rand.nextInt(10));
				AbstractMagicShot entity = new FrostMagicShot(this.level, this, ItemStack.EMPTY);
				entity.setWandLevel(level);
				entity.shoot(0D, -0.35D, 0D, 1.35F, 0F);
				entity.setPos(targetPos.getX() + 0.5D, targetPos.getY() + 10.5D, targetPos.getZ() + 0.5D);
				entity.setAddDamage( entity.getAddDamage() - 4F + damage);
				entity.setChangeParticle(true);
				this.level.addFreshEntity(entity);
			}

			// フロストレインの最大時間を満たしていないなら
			if (this.frostRainTime < FROSTRAIN_MAXTIME) { return; }

			// 情報の初期化
			this.frostRainTime = 0;
			this.chargeTime = 0;
			this.pos = null;
			this.recastTime = (int) (( this.rand.nextInt(60) + 60 ) * ( this.isHalfHealth(this) ? 0.75F : 1F));
			this.attackType = this.rand.nextInt(2);

			// 現在のタゲ以外を対象に設定する
			List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.getFilter(isPlayer, target), 32D);

			if (entityList.size() >= 1) {
				this.setTarget(entityList.get(rand.nextInt(entityList.size())));
			}
		}
	}

	// 範囲内にいるかのチェック
	public boolean checkDistance (BlockPos targetPos, BlockPos pos, double range) {
		return Math.abs(targetPos.getX() - pos.getX() + targetPos.getZ() - pos.getZ()) <= range;
	}

	protected void spawnParticleCycle (LivingEntity target) {

		if ( !(this.level instanceof ServerLevel server) ) { return; }

		this.pos = this.pos == null ? target.blockPosition() : this.pos;

		for (double range = 2.5D; range < 10D; range += 3D) {

			int count = (int) (range / 3D) + 1;
			boolean isReverse = count % 2 == 0;

			for (int i = 0; i < 6 * count; i++) {
				this.spawnParticleCycle(server, ParticleInit.CYCLE_FROST.get(), this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D, Direction.UP, range, (i * 60 / count) + this.rand.nextFloat() * 10D, isReverse);
			}
		}
	}

	public void clearInfo() {
		this.frostRainTime = 0;
		this.chargeTime = 0;
		this.tickTime = 0;
		this.laserTime = 0;
		this.recastTime = this.random.nextInt(200) + 150;
		this.attackType = this.random.nextInt(2);
		this.entityData.set(ISLASER, false);
	}

	public Predicate<LivingEntity> getFilter (boolean isPlayer, boolean flag) {
		return e -> !e.isSpectator() && e.isAlive() && (isPlayer ? (e instanceof Player || e instanceof AbstractSummonMob) : !(e instanceof Player) ) && !(e instanceof ISMMob) && this.hasLineOfSight(e);
	}

	public Predicate<LivingEntity> getFilter (boolean isPlayer, LivingEntity target) {
		return e -> !e.isSpectator() && e.isAlive() && (isPlayer ? (e instanceof Player || e instanceof AbstractSummonMob) : !(e instanceof Player) ) && target != e;
	}

	public Predicate<LivingEntity> getFilter (boolean isPlayer, List<LivingEntity> entityAllList) {
		return e -> !e.isSpectator() && e.isAlive() && (isPlayer ? (e instanceof Player || e instanceof AbstractSummonMob) : !(e instanceof Player) ) && !entityAllList.contains(e);
	}
}
