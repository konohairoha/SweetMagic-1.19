package sweetmagic.init.entity.monster.boss;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.entity.projectile.ElectricMagicShot;
import sweetmagic.init.entity.projectile.ExplosionThunderShot;
import sweetmagic.init.entity.projectile.LigningBulletShot;
import sweetmagic.util.SMDamage;

public class ElshariaCurious extends AbstractSMBoss {

	private int bulletTime = 150;
	private static final int MAX_BULLET_TTIME = 200;
	private int thunderTime = 200;
	private static final int MAX_THUNDER_TTIME = 400;
	private int lightBoltTime = 300;
	private static final int MAX_LIGHTBOL_TTIME = 600;
	private static final EntityDataAccessor<Boolean> ISBOLT = ISMMob.setData(ElshariaCurious.class, BOOLEAN);
	private Map<Integer, BlockPos> posMap = new LinkedHashMap<>();	// サンダーボルテックス

	public ElshariaCurious(Level world) {
		super(EntityInit.elshariaCurious, world);
	}

	public ElshariaCurious(EntityType<? extends AbstractSMBoss> enType, Level world) {
		super(enType, world);
		this.setBossEvent(BC_BLUE, NOTCHED_6);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ISBOLT, false);
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
				.add(Attributes.MAX_HEALTH, 768D)
				.add(Attributes.MOVEMENT_SPEED, 0.4D)
				.add(Attributes.ATTACK_DAMAGE, 4D)
				.add(Attributes.ARMOR, 10D)
				.add(Attributes.FOLLOW_RANGE, 64D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 96D);
	}

	public boolean getBolt() {
		return this.entityData.get(ISBOLT);
	}

	public void setBolt(boolean arrow) {
		this.entityData.set(ISBOLT, arrow);
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putBoolean("bolt", this.getBolt());
		tags.putInt("bulletTime", this.bulletTime);
		tags.putInt("rodSetTime", this.thunderTime);
		tags.putInt("lightBoltTime", this.lightBoltTime);
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setBolt(tags.getBoolean("bolt"));
		this.bulletTime = tags.getInt("bulletTime");
		this.thunderTime = tags.getInt("rodSetTime");
		this.lightBoltTime = tags.getInt("lightBoltTime");
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {
		Entity attacker = src.getEntity();
		Entity attackEntity = src.getDirectEntity();
		if (attacker != null && attacker instanceof ISMMob) { return false; }

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

		return super.hurt(src, amount);
	}

	public boolean causeFallDamage(float par1, float par2, DamageSource src) {
		return false;
	}

	public void aiStep() {
		super.aiStep();
		if (this.deathTime > 0) { return; }

		LivingEntity target = this.getTarget();
		if (target == null) { return; }

		if (this.getY() < target.getY() || this.getY() < target.getY() + 4D) {
			Vec3 vec = this.getDeltaMovement();
			double y = Math.max(0D, vec.y);
			y *= 0.25D;
			y += (0.5D - y) * 0.25D;
			this.setDeltaMovement(new Vec3(vec.x, y, vec.z));
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
	}

	public void firstAttack(LivingEntity target) {

		if (this.bulletTime++ >= MAX_BULLET_TTIME) {
			this.bulletShot(target);
		}

		if (this.thunderTime++ >= MAX_THUNDER_TTIME - 200) {
			this.thunderAction(target);
		}
	}

	public void secondAttack(LivingEntity target) {
		if (this.lightBoltTime++ >= MAX_LIGHTBOL_TTIME - 200) {
			this.lightningBoltShot(target);
		}
	}

	public void bulletShot(LivingEntity target) {

		float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) + 4F;
		List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), 48D);

		for (LivingEntity entity : targetList) {

			double x = entity.getX() - this.getX();
			double y = entity.getY(0.3333333333333333D) - this.getY();
			double z = entity.getZ() - this.getZ();
			double xz = Math.sqrt(x * x + z * z);

			LigningBulletShot magic = new LigningBulletShot(this.level, this);
			magic.shoot(x, y - xz * 0.065D, z, 1F, 0F);
			magic.setAddDamage(magic.getAddDamage() + damage);
			magic.setTarget(entity);
			magic.setMaxLifeTime(120);
			this.level.addFreshEntity(magic);
		}

		this.bulletTime = 0;
	}

	public void thunderAction(LivingEntity target) {

		if (this.thunderTime < MAX_THUNDER_TTIME) {

			if (this.posMap.isEmpty()) {

				List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), 48D);
				int size = targetList.size() + 2;

				for (int i = 0; i < size; i++) {

					while (true) {
						int x = (int) (this.getX() + this.getRandFloat(20F));
						int y = (int) this.getY() + 3 + this.rand.nextInt(8);
						int z = (int) (this.getZ() + this.getRandFloat(20F));
						BlockPos pos = new BlockPos(x, y, z);

						if (!this.posMap.containsValue(pos)) {
							this.posMap.put(i, pos);
							break;
						}
					}
				}
			}

			else if (this.tickCount % 30 == 0) {
				this.posMap.values().forEach(p -> this.spawnParticleCycle(p, 5D));
			}
		}

		else {

			float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) + 8F;

			for (Entry<Integer, BlockPos> map : this.posMap.entrySet()) {
				BlockPos pos = map.getValue().above(3);

				AbstractMagicShot magic = new ElectricMagicShot(this.level, this);
				magic.setAddDamage(magic.getAddDamage());
				magic.setRange(5D);
				magic.setData(2);
				magic.shootFromRotation(this, this.getXRot(), this.getYRot(), 0, 0, 0);
				magic.setPos(pos.getX(), target.getY(), pos.getZ());
				magic.setAddDamage(magic.getAddDamage() + damage);
				magic.setDeltaMovement(new Vec3(0, -10D, 0));
				magic.setHitDead(false);
				this.level.addFreshEntity(magic);
			}

			this.thunderTime = 0;
		}
	}

	public void lightningBoltShot(LivingEntity target) {

		if (this.lightBoltTime <= MAX_LIGHTBOL_TTIME) {

			if (this.tickCount % 4 != 0) { return; }

			float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) + 15F;

			for (int i = 0; i < 3; i++) {
				int x = (int) (this.getX() + this.getRandFloat(20F));
				int y = (int) this.getY() + 3 + this.rand.nextInt(5);
				int z = (int) (this.getZ() + this.getRandFloat(20F));
				BlockPos pos = new BlockPos(x, y, z);

				AbstractMagicShot magic = new ExplosionThunderShot(this.level, this);
				magic.setAddDamage(magic.getAddDamage());
				magic.setRange(7.5D);
				magic.shootFromRotation(this, this.getXRot(), this.getYRot(), 0, 0, 0);
				magic.setPos(pos.getX(), pos.getY(), pos.getZ());
				magic.setAddDamage(magic.getAddDamage() + damage);
				magic.setDeltaMovement(new Vec3(0, -0.75D, 0));
				magic.setHitDead(false);
				this.level.addFreshEntity(magic);
			}
		}

		else {
			this.lightBoltTime = 0;
		}
	}

	@Override
	public boolean isArmorEmpty() {
		return true;
	}

	@Override
	public void clearInfo() {
		this.tickTime = 0;
	}
}
