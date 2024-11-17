package sweetmagic.init.entity.monster.boss;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.entity.animal.AbstractSummonMob;
import sweetmagic.init.entity.projectile.EvilArrow;
import sweetmagic.util.SMDamage;

public class IgnisKnight extends AbstractSMBoss {

	private int targetCount = 0;
	private List<LivingEntity> entityList = new ArrayList<>();
	private List<LivingEntity> targetList = new ArrayList<>();
	private List<Player> playerList = new ArrayList<>();
	private static final EntityDataAccessor<Integer> ATTACK = ISMMob.setData(IgnisKnight.class, INT);
	private static final EntityDataAccessor<Integer> ARMOR = ISMMob.setData(IgnisKnight.class, INT);
	private static final EntityDataAccessor<Boolean> RUSH = ISMMob.setData(IgnisKnight.class, BOOLEAN);
	private static final EntityDataAccessor<Boolean> SWING = ISMMob.setData(IgnisKnight.class, BOOLEAN);
	private static final EntityDataAccessor<Boolean> ISATTACK = ISMMob.setData(IgnisKnight.class, BOOLEAN);
	private static final EntityDataAccessor<Boolean> ISBLAST = ISMMob.setData(IgnisKnight.class, BOOLEAN);

	private int recastTime = 0;									// リキャストタイム

	private int firstAttackChargeTime = 0;						// 1st攻撃のチャージ時間
	private static final int FIRSTATTACKCHARGEMAXTIME = 135;	// 1st攻撃の最大チャージ時間
	private int firstAttackTime = 0;							// 1st攻撃の時間

	private boolean isGround = false;							// 着地したかどうか
	private int groundTime = 0;									// 着地時間

	private int secondAttackChargeTime = 0;						// 2nd攻撃のチャージ時間
	private static final int SECONDATTACKCHARGEMAXTIME = 145;	// 2nd攻撃の最大チャージ時間

	public IgnisKnight(Level world) {
		super(EntityInit.ignisKnight, world);
	}

	public IgnisKnight(EntityType<IgnisKnight> enType, Level world) {
		super(enType, world);
		this.moveControl = new IKMoveControl(this);
		this.xpReward = 300;
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 400D)
				.add(Attributes.MOVEMENT_SPEED, 0.32D)
				.add(Attributes.ATTACK_DAMAGE, 6D)
				.add(Attributes.ARMOR, 10D)
				.add(Attributes.FOLLOW_RANGE, 64D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 64D);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ATTACK, 0);
		this.entityData.define(ARMOR, 0);
		this.entityData.define(RUSH, false);
		this.entityData.define(SWING, false);
		this.entityData.define(ISATTACK, false);
		this.entityData.define(ISBLAST, false);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(5, new MoveTowards(this, 1.0D));
		this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Warden.class, 8.0F));
		this.goalSelector.addGoal(10, new SMRandomLookGoal(this));
		this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Warden.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Raider.class, true));
	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.FIRE_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource src) {
		return SoundEvents.FIRE_AMBIENT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.FIRE_AMBIENT;
	}

	public float getSoundVolume() {
		return 0.75F;
	}

	public int getAttackType () {
		return this.entityData.get(ATTACK);
	}

	public void setAttackType (int attack) {
		this.entityData.set(ATTACK, attack);
	}

	public int getArmor () {
		return this.entityData.get(ARMOR);
	}

	public void setArmor (int armor) {
		this.entityData.set(ARMOR, armor);
	}

	public boolean isRush () {
		return this.entityData.get(RUSH);
	}

	public void setRush(boolean rush) {
		this.entityData.set(RUSH, rush);
	}

	public boolean isSwing () {
		return this.entityData.get(SWING);
	}

	public void setSwing(boolean swing) {
		this.entityData.set(SWING, swing);
	}

	public boolean isAttack () {
		return this.entityData.get(ISATTACK);
	}

	public void setAttack(boolean isAttack) {
		this.entityData.set(ISATTACK, isAttack);
	}

	public boolean isBlast () {
		return this.entityData.get(ISBLAST);
	}

	public void setBlast(boolean isBlast) {
		this.entityData.set(ISBLAST, isBlast);
	}

	// アーマーがないなら
	public boolean isArmorEmpty () {
		return this.getArmor() <= 0;
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

		// 魔導士の召喚台座による召喚の場合
		if (this.isLectern()) {
			amount = this.getLecternAction(src, amount, this.getArmor());
		}

		return super.hurt(src, amount);
	}

	// 召喚台座時のダメージ処理
	public float getLecternAction (DamageSource src, float amount, int armorSize) {

		if (armorSize > 0) {

			// 危険な果実の場合
			if (src.getDirectEntity() instanceof EvilArrow) {
				amount = this.getEvilDamage(amount, armorSize);
			}

			// 危険な果実以外のダメージなら
			else {
				amount *= 0.125F;
				this.specialDamageCut(src, this.playerList, "ignis_damecut");
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
			this.sendMSG(this.playerList, this.getText("ignis_break").withStyle(GREEN));
		}

		return amount;
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putInt("armor", this.getArmor());
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setArmor(tags.getInt("armor"));
	}

	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance dif, MobSpawnType spawn, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		data = super.finalizeSpawn(world, dif, spawn, data, tag);
		this.setArmor(this.getPlayer(Player.class).size() * 4);
		return data;
	}

	protected void customServerAiStep() {

		super.customServerAiStep();

		LivingEntity target = this.getTarget();
		if (target == null) {
			int attackType = this.getAttackType();
			this.clearInfo();
			this.setAttackType(attackType);
			return;
		}

		this.getLookControl().setLookAt(target, 10F, 10F);
		if (this.recastTime-- > 0) { return; }

		// 1stフェーズの攻撃
		if (!this.isHalfHealth(this)) {
			this.firstPhaseSttack(target);
		}

		// 2ndフェーズの攻撃
		else {
			this.secondPhaseSttack(target);
		}
	}

	// 1stフェーズの攻撃
	public void firstPhaseSttack (LivingEntity target) {

		this.setAttack(true);
		boolean isPlayer = this.isPlayer(target);

		// ターゲットがないなら設定
		if (this.entityList.isEmpty()) {
			this.entityList = this.getEntityList(LivingEntity.class, this.getFilter(isPlayer), 32D);
		}

		// 向きを強制設定（これしないと変な方向になるときがある）
        double d1 = target.getX() - this.getX();
		double d2 = target.getZ() - this.getZ();
		this.setYRot(-((float) Math.atan2(d1, d2)) * (180F / (float) Math.PI));
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();
		if (this.firstAttackChargeTime++ < FIRSTATTACKCHARGEMAXTIME) { return; }

		// 最大チャージが終わったら攻撃タイプを設定
		if (this.firstAttackChargeTime == FIRSTATTACKCHARGEMAXTIME) {
			this.setAttackType(this.rand.nextInt(2));

			if (this.entityList.size() > 1) {
				this.entityList = this.entityList.stream().sorted( (s1, s2) -> this.sortEntity(this, s1, s2) ).toList();
			}
		}

		// 攻撃種別によって攻撃を変える
		switch (this.getAttackType()) {
		case 0:
			// 突進攻撃
			this.rushAttack(target, isPlayer);
			break;
		case 1:
			// ハンマー攻撃
			this.hammerGroundwork(target, isPlayer);
			break;
		}
	}

	// 2ndフェーズの攻撃
	public void secondPhaseSttack (LivingEntity target) {

		this.setAttackType(2);
		this.setAttack(true);
		boolean isPlayer = this.isPlayer(target);

		// 向きを強制設定（これしないと変な方向になるときがある）
        double d1 = target.getX() - this.getX();
		double d2 = target.getZ() - this.getZ();
		this.setYRot(-((float) Math.atan2(d1, d2)) * (180F / (float) Math.PI));
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();
		if (this.secondAttackChargeTime++ < SECONDATTACKCHARGEMAXTIME) { return; }

		// ターゲットがないなら設定
		if (this.entityList.isEmpty()) {
			this.entityList = this.getEntityList(LivingEntity.class, this.getFilter(isPlayer), 24D);

			if (this.entityList.size() > 1) {
				this.entityList = this.entityList.stream().sorted( (s1, s2) -> this.sortEntity(this, s1, s2) ).toList();
			}
		}

		// 最大チャージが終わったら攻撃タイプを設定
		if (this.secondAttackChargeTime >= SECONDATTACKCHARGEMAXTIME) {
			this.hammerBlast(target, isPlayer);
		}
	}

	// 突進攻撃
	public void rushAttack (LivingEntity target, boolean isPlayer) {

		// 移動速度を取得
		Vec3 vec = this.getDeltaMovement();
		double vX = vec.x;
		double vY = vec.y;
		double vZ = vec.z;

		// 突進中以外なら突進開始
		if (!this.isRush()) {

			// 攻撃者の座標取得
			Vec3 src = new Vec3(this.getX(), this.getY(), this.getZ()).add(0, this.getEyeHeight(), 0);
			Vec3 look = this.getViewVector(1.0F);

			// 向き先に座標を設定
			Vec3 dest = src.add(look.x * 4, this.getY(), look.z * 4);
			vX = (dest.x - src.x) * 1.5D;
			vZ = (dest.z - src.z) * 1.5D;

			// 移動速度を設定
			this.setDeltaMovement(new Vec3(vX, vY, vZ));
			this.setRush(true);
			this.setSwing(true);
		}

		// 突進中
		else {

			// 突進が終わっていたら
			if (this.firstAttackTime++ >= 10 || vX == 0 && vZ == 0) {

				this.setSwing(false);
				this.setRush(false);

				// ターゲットがいなくなったら
				if (this.entityList.isEmpty()) {
					this.clearInfo();
				}

				// ターゲットがいるなら
				else {

					// 生存しているえんちちーだけに絞る
					this.entityList = this.entityList.stream().filter(e -> e.isAlive()).toList();

					if (this.entityList.isEmpty() || this.targetCount++ >= this.entityList.size()) {
						this.clearInfo();
					}

					else {
						this.setTarget(this.entityList.get(0));
						this.firstAttackTime = 0;
					}
				}
			}

			// 突進中なら
			else {

				// 接触攻撃
				this.contactDamage(isPlayer, this.setDamage(15F), 2D);
				if ( !(this.level instanceof ServerLevel sever) ) { return; }

				BlockPos pos = this.blockPosition();
				float aX = (float) (-vec.x / 6F);
				float aY = (float) (-vec.y / 6F);
				float aZ = (float) (-vec.z / 6F);

				for (int i = 0; i < 8; i++) {
					float x = (float) (pos.getX() + this.getRandFloat(1.5F) - 0.75F);
					float y = (float) (pos.getY() + this.getRandFloat(2F) + 0.5F);
					float z = (float) (pos.getZ() + this.getRandFloat(1.5F) - 0.75F);
					sever.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0, aX, aY, aZ, 1F);
				}
			}
		}
	}

	// ハンマー攻撃
	public void hammerGroundwork (LivingEntity target, boolean isPlayer) {

		this.tickTime++;

		if (this.tickTime == 1) {
			this.setSwing(true);
		}

		else if (this.tickTime == 2) {
			this.setSwing(false);
		}

		else if (this.tickTime == 6) {
			this.playSound(SoundEvents.ANVIL_PLACE, 1F, 1F);
		}

		if (this.tickTime < 15 || this.tickTime % 3 != 0) { return; }

		this.playSound(SoundEvents.FIRECHARGE_USE, 1F, 1F);
		float rate = (this.tickTime - 15) / 15F;
		BlockPos pos = this.blockPosition();
		BlockPos targetPos = target.blockPosition();

		double x = ( targetPos.getX() - pos.getX() ) * rate;
		double y = ( targetPos.getY() - pos.getY() ) * rate;
		double z = ( targetPos.getZ() - pos.getZ() ) * rate;
		BlockPos attackPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
		this.level.playSound(null, attackPos, SoundEvents.FIRECHARGE_USE, SoundSource.HOSTILE, 1F, 1F);

		// 攻撃した人をリストに含まれないプレイヤーリストを取得
		List<LivingEntity> attackList = this.getEntityList(LivingEntity.class, attackPos, 2D).stream().filter(this.getFilterList(isPlayer)).toList();
		this.targetList.addAll(attackList);
		float amount = this.setDamage(this.tickTime >= 30 ? 8.5F : 6F);

		// 対象のえんちちーに攻撃
		for (LivingEntity entity : attackList) {
			this.attackDamage(entity, this.getSRC(), entity instanceof Enemy ? amount * 4F : amount);
		}

		if (this.tickTime >= 30 || !target.isAlive()) {

			// 生存しているえんちちーだけに絞る
			this.entityList = this.entityList.stream().filter(e -> e.isAlive()).toList();

			if (this.entityList.isEmpty() || this.targetCount++ >= this.entityList.size()) {
				this.clearInfo();
			}

			else {
				this.setTarget(this.entityList.get(0));
				this.firstAttackTime = 0;
			}
		}

		if (!(this.level instanceof ServerLevel server) ) { return; }

		for (int i = 0; i < 4; i++) {
			this.spawnParticleRing(server, ParticleTypes.SOUL_FIRE_FLAME, 1D, new BlockPos(attackPos.getX(), attackPos.getY() - 2D + 0.25D + i, attackPos.getZ()), 1, 0.25D, 0D);
		}
	}

	// ハンマー攻撃
	public void hammerBlast (LivingEntity target, boolean isPlayer) {

		this.tickTime++;
		this.setBlast(true);

		if (this.tickTime == 10) {
			BlockPos beforePos = this.blockPosition();
			this.teleportTo(target.getX(), target.getY() + 5D, target.getZ());
			this.teleportParticle(ParticleTypes.SOUL_FIRE_FLAME, this.level, beforePos, this.blockPosition());
		}

		else if (this.tickTime > 10 && this.tickTime <= 50) {
			Vec3 vec = this.getDeltaMovement();
			if (vec.y < 0D) {
				this.setDeltaMovement(new Vec3(0D, 0D, 0D));
			}

			if (this.tickTime == 50) {
				this.setSwing(true);
			}

			if (this.level instanceof ServerLevel sever) {

				for (int i = 0; i < 4; i++) {
					float x = (float) (this.getX() + this.rand.nextFloat() - 0.5F);
					float y = (float) (this.getY() + this.rand.nextFloat() - 0.5F);
					float z = (float) (this.getZ() + this.rand.nextFloat() - 0.5F);

					float aX = (rand.nextFloat() - rand.nextFloat()) * 0.75F;
					float aY = 0.1F + rand.nextFloat() * 0.2F;
					float aZ = (rand.nextFloat() - rand.nextFloat()) * 0.75F;
					sever.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0, aX, aY, aZ, 1F);
				}
			}

			if (this.tickTime == 11 || this.tickTime == 41) {
				double range = 10D + this.entityList.size() * 0.5D;
				for (int i = 1; i <= 4; i++) {
					this.spawnParticleCycle(this.blockPosition().below(4), range * 0.25D * i);
				}
			}
		}

		// 一定時間が経ったら
		else if (this.tickTime > 50) {

			// 地面に落下
			if (!this.onGround) {
				this.setDeltaMovement(new Vec3(0D, -1.5D, 0D));
			}

			// 地面に付いたら
			else {

				if (!this.isGround) {

					float amount = this.setDamage(10F);
					double range = 10D + this.entityList.size() * 0.5D;
					List<LivingEntity> attackList = this.getEntityList(LivingEntity.class, this.getFilter(isPlayer, range), range);
					attackList.forEach(e -> this.attackDamage(e, this.getSRC(), e instanceof Enemy ? amount * 4F : amount));

					this.isGround = true;
					this.playSound(SoundEvents.GENERIC_EXPLODE, 3F, 1F / (this.random.nextFloat() * 0.2F + 0.9F));

					// 生存しているえんちちーだけに絞る
					this.entityList = this.entityList.stream().filter(e -> e.isAlive()).toList();

					if (this.entityList.isEmpty()) {
						this.clearInfo();
					}

					else {
						this.setTarget(this.entityList.get(0));
						this.firstAttackTime = 0;
					}

					if ( !(this.level instanceof ServerLevel sever) ) { return; }

					sever.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY() + 1F, this.getZ(), 1, 0F, 0F, 0F, 1F);

					float x = (float) (this.getX() + this.rand.nextFloat() - 0.5F);
					float y = (float) (this.getY() + this.rand.nextFloat() - 0.5F);
					float z = (float) (this.getZ() + this.rand.nextFloat() - 0.5F);

					for (int i = 0; i < 16; i++) {
						sever.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 4, 0F, 0F, 0F, 0.15F);
					}
				}

				else {

					if (this.groundTime++ < 19 || this.groundTime % 20 != 0) { return; }

					float amount = this.setDamage(3F);
					double ran = 14D + this.entityList.size() * 2D;
					List<LivingEntity> attackList = this.getEntityList(LivingEntity.class, this.getFilter(isPlayer, ran), ran);
					attackList.forEach(e -> this.attackDamage(e, this.getSRC(), e instanceof Enemy ? amount * 4F : amount));
					this.playSound(SoundEvents.BLAZE_SHOOT, 1F, 1F / (this.random.nextFloat() * 0.2F + 0.9F));

					// 生存しているえんちちーだけに絞る
					this.entityList = this.entityList.stream().filter(e -> e.isAlive()).toList();

					if (this.entityList.isEmpty()) {
						this.clearInfo();
					}

					else {
						this.setTarget(this.entityList.get(0));
						this.firstAttackTime = 0;
					}

					if (this.level instanceof ServerLevel sever) {

						BlockPos pos = this.blockPosition();

						for (int range = 0; range < 3; range++) {
							for (int i = 0; i < 3; i++) {
								this.spawnParticleRing(sever, ParticleTypes.SOUL_FIRE_FLAME, 2 + range * 4, pos, -0.25D + i * 0.5D);
							}
						}
					}

					if (this.groundTime >= 60) {
						this.clearInfo();
					}
				}
			}
		}
	}

	// 接触ダメージ
	public void contactDamage (boolean isPlayer, float amount, double range) {

		// 範囲のモブを取得
		List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, this.getFilterList(isPlayer), range);
		if (targetList.isEmpty()) { return; }

		Random rand = this.rand;

		for (LivingEntity target : targetList) {

			this.attackDamage(target, this.getSRC(), target instanceof Enemy ? amount * 3F : amount);

			if (this.level instanceof ServerLevel sever) {

				BlockPos pos = target.blockPosition();
				float x = (float) (pos.getX() + rand.nextFloat() - 0.5F);
				float y = (float) (pos.getY() + rand.nextFloat() + 1F);
				float z = (float) (pos.getZ() + rand.nextFloat() - 0.5F);

				for (int i = 0; i < 16; i++) {
					sever.sendParticles(ParticleTypes.CRIT, x, y, z, 4, 0F, 0F, 0F, 1F);
				}
			}
		}

		this.targetList.addAll(targetList);
		this.playSound(SoundEvents.ANVIL_PLACE, 1F, 1F);
	}

	public float setDamage (float amount) {
		amount = this.isArmorEmpty() ? amount * 1.25F : amount;
		return ( this.isHard() ? amount * 2F : amount ) + this.getBuffPower();
	}

	public void clearInfo () {
		this.setAttackType(this.isHalfHealth(this) ? 2 : this.random.nextInt(2));
		this.recastTime = this.isHard() ? 25 : 75 + this.random.nextInt(50);
		this.firstAttackChargeTime = 0;
		this.secondAttackChargeTime = 0;
		this.targetCount = 0;
		this.firstAttackTime = 0;
		this.tickTime = 0;
		this.groundTime = 0;
		this.entityList = new ArrayList<>();
		this.targetList = new ArrayList<>();
		this.isGround = false;
		this.setSwing(false);
		this.setRush(false);
		this.setAttack(false);
		this.setBlast(false);
	}

	public Predicate<LivingEntity> getFilterList (boolean isPlayer) {
		return e -> !e.isSpectator() && e.isAlive() && !this.targetList.contains(e) && (isPlayer ? (e instanceof Player || e instanceof AbstractSummonMob) : !(e instanceof Player) ) && !(e instanceof ISMMob);
	}

	public Predicate<LivingEntity> getFilter (boolean isPlayer, double range) {
		return e -> !e.isSpectator() && e.isAlive() && this.checkDistances(this.blockPosition(), e.blockPosition(), range * range) && (isPlayer ? (e instanceof Player || e instanceof AbstractSummonMob) : !(e instanceof Player) ) && !(e instanceof ISMMob);
	}

	public class MoveTowards extends MoveTowardsRestrictionGoal {

		public IgnisKnight ignis;

		public MoveTowards(IgnisKnight ignis, double par1) {
			super(ignis, par1);
			this.ignis = ignis;
		}

		public boolean canUse() {
			return super.canUse() && !this.ignis.isAttack();
		}
	}

	public class IKMoveControl extends MoveControl {

		public IgnisKnight ignis;

		public IKMoveControl(Mob mob) {
			super(mob);
			this.ignis = (IgnisKnight) mob;
		}

		public void tick() {
			if (this.ignis.isAttack()) { return; }
			super.tick();
		}
	}
}
