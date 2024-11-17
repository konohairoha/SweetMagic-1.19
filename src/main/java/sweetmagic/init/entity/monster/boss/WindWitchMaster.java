package sweetmagic.init.entity.monster.boss;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
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
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.entity.projectile.EvilArrow;
import sweetmagic.init.entity.projectile.TripleTornadoShot;
import sweetmagic.init.entity.projectile.WindStormShot;
import sweetmagic.util.SMDamage;

public class WindWitchMaster extends AbstractSMBoss {

	private int tickTime = 0;
	private int recastTime = 0;
	private int recastSecondTime = 100;
	private List<Player> playerList = new ArrayList<>();
	private static final EntityDataAccessor<Boolean> IS_TARGET = ISMMob.setData(WindWitchMaster.class, BOOLEAN);
	private static final EntityDataAccessor<Integer> ATTACK = ISMMob.setData(WindWitchMaster.class, INT);
	private static final EntityDataAccessor<Integer> ARMOR = ISMMob.setData(WindWitchMaster.class, INT);

	private int windBlastTime = 0;									// ウィンドブラストの時間
	private int windBlastCount = 2;									// ウィンドブラストの時間
	private Map<Integer, BlockPos> posMap = new LinkedHashMap<>();	// トリトル対象座標

	public WindWitchMaster(Level world) {
		super(EntityInit.windWitchMaster, world);
	}

	public WindWitchMaster(EntityType<WindWitchMaster> enType, Level world) {
		super(enType, world);
		this.xpReward = 300;
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 512D)
				.add(Attributes.MOVEMENT_SPEED, 0.3D)
				.add(Attributes.ATTACK_DAMAGE, 3D)
				.add(Attributes.ARMOR, 8D)
				.add(Attributes.FOLLOW_RANGE, 64D);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(IS_TARGET, false);
		this.entityData.define(ATTACK, 0);
		this.entityData.define(ARMOR, 0);
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

	protected SoundEvent getAmbientSound() {
		return SoundEvents.WITCH_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource src) {
		return SoundEvents.WITCH_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.WITCH_DEATH;
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

		int oldArmor = this.getArmor();
		this.entityData.set(ARMOR, armor);

		if (oldArmor > 0 && armor <= 0) {

			if (!this.level.isClientSide && !this.playerList.isEmpty()) {
				this.sendMSG(this.playerList, this.getText("wind_break").withStyle(GREEN));
			}

			this.removeEffect(PotionInit.aether_barrier);
			this.setHealth(Math.max(1F, this.getHealth() - 100F));
		}
	}

	public boolean isTarget () {
		return this.entityData.get(IS_TARGET);
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {

		Entity attacker = src.getEntity();
		Entity attackEntity = src.getDirectEntity();
		if ( attacker != null && attacker instanceof ISMMob) { return false; }

		// ボスダメージ計算
		amount = this.getBossDamageAmount(this.level, this.defTime , src, amount, 7.5F);
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

		if (amount >= 2F && src.getDirectEntity() instanceof AbstractMagicShot && this.rand.nextFloat() >= 0.67F) {
			this.teleport();
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
				amount *= 0.25F;
				this.specialDamageCut(src, this.playerList, "wind_damecut");
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
		amount = 25F / this.getEntityList(Player.class, 80F).size();
		return amount;
	}

	protected boolean teleport() {
		BlockPos spawnPos = this.getSpawnPos();
		if (!this.level.isClientSide() && this.isAlive() && spawnPos != null) {
			double d0 = spawnPos.getX() + (this.rand.nextDouble() - 0.5D) * 10D;
			double d1 = spawnPos.getY() + 1.5D;
			double d2 = spawnPos.getZ() + (this.rand.nextDouble() - 0.5D) * 10D;
			return this.teleport(d0, d1, d2);
		}
		return false;
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putBoolean("is_target", this.isTarget());
		tags.putInt("armor", this.getArmor());
		tags.putInt("attackType", this.getAttackType());
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.entityData.set(IS_TARGET, tags.getBoolean("is_target"));
		this.setArmor(tags.getInt("armor"));
		this.setAttackType(tags.getInt("attackType"));
	}

	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance dif, MobSpawnType spawn, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		data = super.finalizeSpawn(world, dif, spawn, data, tag);
		this.setArmor(this.getPlayer(Player.class).size() * 4);
		return data;
	}

	public void tick() {
		super.tick();

		if (!this.level.isClientSide && this.tickTime++ > 10) {
			this.tickTime = 0;
			this.entityData.set(IS_TARGET, this.getTarget() != null);
		}
	}

	protected void customServerAiStep() {

		super.customServerAiStep();

		LivingEntity target = this.getTarget();
		if (target == null) { return; }

		this.getLookControl().setLookAt(target, 10F, 10F);

		// 召喚した杖の分エーテルバリアー付与
		if (this.tickCount % 20 == 0 && !this.isArmorEmpty()) {
			this.setAetherBattier();
		}

		// 2ndフェーズの攻撃
		if (this.isHalfHealth(this) && this.recastSecondTime-- <= 0) {
			this.secondPhaseSttack(target);
		}

		if (this.recastTime-- > 0) { return; }

		// 1stフェーズの攻撃
		this.firstPhaseSttack(target);
	}

	// 1stフェーズの攻撃
	public void firstPhaseSttack (LivingEntity target) {

		if (this.getAttackType() == 0) {
			this.tornadoExplosion(target);
		}

		else {
			this.tripleTornado(target);
		}
	}

	// 2ndフェーズの攻撃
	public void secondPhaseSttack (LivingEntity target) {
		this.windStorm(target);
	}

	// 竜巻爆発
	public void tornadoExplosion (LivingEntity target) {

		boolean isPlayer = this.isPlayer(target);
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.getFilter(isPlayer), 64D);

		if (entityList.isEmpty() || entityList.size() <= 0) {
			this.clearInfo();
			return;
		}

		double range = 14D + entityList.size() * 2D;
		BlockPos pos = entityList.stream().sorted( (s1, s2) -> this.sortEntity(this, s1, s2) ).toList().get(0).blockPosition();

		TripleTornadoShot entity = new TripleTornadoShot(this.level, this, ItemStack.EMPTY);
		entity.shootFromRotation(this, this.getXRot(), this.getYRot(), 0, 1F, 0);
		entity.shoot(0D, 0D, 0D, 0F, 0F);
		entity.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
		entity.setHitDead(false);
		entity.setNotDamage(true);
		entity.setRange(range);
		entity.setAddDamage( (this.isHard() ? 60F : 30F) + this.getBuffPower());
		entity.isPlayer = isPlayer;

		if (!this.level.isClientSide) {
			this.level.addFreshEntity(entity);
		}

		// 情報の初期化
		this.clearInfo();
	}

	// トリプルトルネード
	public void tripleTornado (LivingEntity target) {

		boolean isPlayer = this.isPlayer(target);

		// 範囲にいるえんちちーを取得
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.getFilter(isPlayer), 48D);
		double range = 8D + entityList.size();
		int count = 0;

		for (LivingEntity entity : entityList) {
			this.posMap.put(count, entity.blockPosition());

			if (++count >= 3) {
				break;
			}
		}

		// 座標が3つ登録できていないなら
		if (count < 3) {

			BlockPos basePos = this.blockPosition();
			int mapRange = 32;

			for (int i = count; i < 3; i++) {

				// 被っていない座標をposMapに登録
				while (true) {
					BlockPos pos = basePos.offset(this.randRange(mapRange), 0, this.randRange(mapRange));

					if (!this.posMap.containsValue(pos) && !this.level.getBlockState(pos.below()).isAir()) {
						this.posMap.put(i, pos);
						break;
					}
				}
			}
		}

		// 登録した座標分トルネードを召喚
		for (Entry<Integer, BlockPos> map : this.posMap.entrySet()) {

			BlockPos pos = map.getValue();
			TripleTornadoShot entity = new TripleTornadoShot(this.level, this, ItemStack.EMPTY);
			entity.shootFromRotation(this, this.getXRot(), this.getYRot(), 0, 1F, 0);
			entity.shoot(0D, 0D, 0D, 0F, 0F);
			entity.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
			entity.setHitDead(false);
			entity.setNotDamage(true);
			entity.setRange(range);
			entity.setAddDamage( (this.isHard() ? 37.5F : 17.5F) + this.getBuffPower());
			entity.isPlayer = isPlayer;

			if (!this.level.isClientSide) {
				this.level.addFreshEntity(entity);
			}
		}

		this.clearInfo();
	}

	// ウィンドストーム
	public void windStorm (LivingEntity target) {

		if (this.windBlastTime-- > 0) { return; }

		boolean isPlayer = this.isPlayer(target);
		float damage = target instanceof Warden ? 80F : 20F;

		for (int i = 0; i < 2; i++) {

			WindStormShot entity = new WindStormShot(this.level, this, ItemStack.EMPTY);

			double d0 = target.getX() - this.getX();
			double d1 = target.getZ() - this.getZ();
			double d3 = target.getY(0.3333333333333333D) - this.getY();
			Vector3f vec = this.getShotVector(this, new Vec3(d0, d3, d1), i == 0 ? 25 : -25);
			entity.shoot(vec.x(), vec.y(), vec.z(), 1F, 0);
			entity.setAddDamage(entity.getAddDamage() + damage);
			entity.setMaxLifeTime(120);
			entity.setRange(1.5D);
			entity.setBlockPenetration(true);
			entity.isPlayer = isPlayer;
			this.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);
			if (!this.level.isClientSide) {
				this.level.addFreshEntity(entity);
			}
		}

		WindStormShot entity = new WindStormShot(this.level, this, ItemStack.EMPTY);
		double x = target.getX() - this.getX();
		double y = target.getY(0.3333333333333333D) - this.getY();
		double z = target.getZ() - this.getZ();
		double xz = Math.sqrt(x * x + z * z);

		entity.shoot(x, y - xz * 0.035D, z, 1F, 0F);
		entity.setAddDamage(entity.getAddDamage() + damage + 10F);
		entity.setMaxLifeTime(120);
		entity.setRange(2D);
		entity.setBlockPenetration(true);
		entity.isPlayer = isPlayer;
		this.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);

		if (!this.level.isClientSide) {
			this.level.addFreshEntity(entity);
		}

		if (--this.windBlastCount > 0) {
			this.windBlastTime = 30;
		}

		else {
			this.clearInfo();
			this.recastSecondTime = 250 + this.random.nextInt(100);
			this.windBlastCount = 2;
		}
	}

	public void setAetherBattier() {
		this.addPotion(this, PotionInit.aether_barrier, 200, this.getArmor() + 4);
	}

	@Override
	public boolean isArmorEmpty() {
		return this.getArmor() <= 0;
	}

	@Override
	public void clearInfo() {
		this.windBlastTime = 0;
		this.setAttackType(this.random.nextInt(2));
		this.posMap.clear();
		this.recastTime = 150 + this.random.nextInt(50);
	}

	// えんちちーソート
	public int sortEntity (Entity mob, Entity entity1, Entity entity2) {

		if (entity1 == null || entity2 == null) { return 0; }

		double distance1 = mob.distanceToSqr(entity1);
		double distance2 = mob.distanceToSqr(entity2);

		if (distance1 < distance2) { return 1; }

		if (distance1 > distance2) { return -1; }

		return 0;
	}
}
