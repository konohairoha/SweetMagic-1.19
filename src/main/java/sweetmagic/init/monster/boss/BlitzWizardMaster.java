package sweetmagic.init.entity.monster.boss;

import java.util.ArrayList;
import java.util.List;

import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.projectile.ElectricMagicShot;
import sweetmagic.init.entity.projectile.LightningRod;
import sweetmagic.init.entity.projectile.TridentThunder;
import sweetmagic.util.SMDamage;

public class BlitzWizardMaster extends AbstractSMBoss {

	private int thunderCount = 0;
	private int thunderTime = 0;
	private static final int MAX_THUNDERTIME = 240;
	private int lightningRodTime = 250;
	private static final int MAX_LIGHTNINGRODTIME = 640;
	private int lightningWebTime = 0;
	private static final int MAX_LIGHTNINGWEBTIME = 600;
	private int tridentReleaseTime = 300;
	private static final int MAX_TRIDENTRELEASETIME = 450;
	public List<BlockPos> posList = new ArrayList<>();
	public List<TridentThunder> tridentList = new ArrayList<>();
	private static final EntityDataAccessor<Boolean> UP_BOOK = ISMMob.setData(BlitzWizardMaster.class, BOOLEAN);
	private static final EntityDataAccessor<Boolean> LIGHNING_WEB = ISMMob.setData(BlitzWizardMaster.class, BOOLEAN);

	public BlitzWizardMaster(Level world) {
		super(EntityInit.blitzWizardMaster, world);
	}

	public BlitzWizardMaster(EntityType<BlitzWizardMaster> enType, Level world) {
		super(enType, world);
		this.xpReward = 600;
		this.setBossEvent(BC_BLUE, NOTCHED_6);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 768D)
				.add(Attributes.MOVEMENT_SPEED, 0.3D)
				.add(Attributes.ATTACK_DAMAGE, 4D)
				.add(Attributes.ARMOR, 10D)
				.add(Attributes.FOLLOW_RANGE, 64D);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.define(LIGHNING_WEB, false);
		this.define(UP_BOOK, false);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1D));
		this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1D, 0F));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8F));
		this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Warden.class, 8F));
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

	public boolean getLighningWeb() {
		return this.get(LIGHNING_WEB);
	}

	public void setLighningWeb(boolean lighningWeb) {
		this.set(LIGHNING_WEB, lighningWeb);
	}

	public boolean getUpBook() {
		return this.get(UP_BOOK);
	}

	public void setUpBook(boolean upBook) {
		this.set(UP_BOOK, upBook);
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putBoolean("lighningWeb", this.getLighningWeb());
		tags.putBoolean("upBook", this.getUpBook());
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setLighningWeb(tags.getBoolean("lighningWeb"));
		this.setUpBook(tags.getBoolean("upBook"));
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {
		Entity attacker = src.getEntity();
		Entity attackEntity = src.getDirectEntity();
		if (attacker != null && attacker instanceof ISMMob) { return false; }

		// ボスダメージ計算
		float cap = this.isHalfHealth(this) ? 4F : 6F;
		amount = this.getBossDamageAmount(this.getLevel(), this.defTime , src, amount, cap);
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

	protected void customServerAiStep() {
		super.customServerAiStep();

		LivingEntity target = this.getTarget();
		if (target == null) {
			this.clearInfo();
			return;
		}

		this.getLookControl().setLookAt(target, 10F, 10F);
		this.firstAttack(target);

		if (this.isHalfHealth(this)) {
			this.secondAttack(target);
		}
	}

	protected void armorHealthSet(LivingEntity target, int count) {
		this.setMaxHealthArmor((100F + this.getPlayerCount(target) * 12.5F) * count);
		this.setHealthArmor(this.getMaxHealthArmor());
	}

	public void firstAttack(LivingEntity target) {

		if (this.thunderTime++ >= MAX_THUNDERTIME) {
			this.rangeThunder(target);
		}

		if (this.lightningRodTime++ >= MAX_LIGHTNINGRODTIME) {
			this.lightningRod(target);
			this.lightningRodTime = 0;
		}
	}

	public void secondAttack(LivingEntity target) {

		if (this.lightningWebTime++ + 300 >= MAX_LIGHTNINGWEBTIME) {
			this.lightningWeb(target);
		}

		if (this.tridentReleaseTime++ + 200 >= MAX_TRIDENTRELEASETIME) {
			this.tridentRelease(target);
		}
	}

	// 範囲雷
	public void rangeThunder(LivingEntity target) {
		if (this.tickCount % 20 != 0) { return; }

		boolean isWarden = target instanceof Warden;
		float damage = isWarden ? 50F : 7.5F;
		int angle = 15 - this.thunderCount;
		int rangeMax = this.isHalfHealth(this) ? 16 : 8;

		for (int i = 0; i < rangeMax; i++) {

			ElectricMagicShot entity = new ElectricMagicShot(this.getLevel(), this);
			double d0 = target.getX() - this.getX();
			double d1 = target.getZ() - this.getZ();
			double d3 = 0D;
			Vector3f vec = this.getShotVector(this, new Vec3(d0, d3, d1), 360 / rangeMax * (i + 1) + angle);
			entity.shoot(vec.x(), vec.y(), vec.z(), 0.875F, 2);
			entity.setAddDamage(entity.getAddDamage() + damage);
			entity.setMaxLifeTime(40);
			entity.setRange(3.5D);
			entity.setHitDead(false);
			entity.setNotDamage(true);
			entity.isRangeAttack = true;
			this.addEntity(entity);
		}

		this.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);

		if (this.thunderCount++ >= 3) {
			this.thunderCount = 0;
			this.thunderTime = 0;
		}
	}

	// 避雷針落とし
	public void lightningRod(LivingEntity target) {

		List<LivingEntity> targetList = this.getPlayerList(target);

		for (LivingEntity entity : targetList) {

			double range = 4D;
			double x = entity.getX() + (double) this.getRandFloat(4F);
			double z = entity.getZ() + (double) this.getRandFloat(4F);

			LightningRod magic = new LightningRod(this.getLevel(), this);
			magic.setHitDead(false);
			magic.setNotDamage(true);
			magic.shoot(0, 0D, 0, 0F, 0F);
			magic.setPos(x, this.yo, z);
			magic.setAddDamage(magic.getAddDamage() + 30F);
			magic.setRange(range);
			magic.setMaxLifeTime(400 + this.rand.nextInt(60));
			this.addEntity(magic);
		}
	}

	// ライトニングウェブ
	public void lightningWeb(LivingEntity target) {

		this.setLighningWeb(true);

		for (int i = 0; i < 4; i++) {
			float angle = i * 1.5F;
			float addX = (float) Math.sin(angle + this.tickCount / 20F) * ((float) Math.cos(this.tickCount / 20F) * 12.5F + 15F);
			float addZ = -(float) Math.cos(angle + this.tickCount / 20F) * ((float) Math.cos(this.tickCount / 20F) * 12.5F + 15F);
			BlockPos pos = this.blockPosition().offset(addX, 0D, addZ);
			float damage = 8F;
			List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, e -> !(e instanceof ISMMob), pos, 2D);

			for (LivingEntity entity : targetList) {
				boolean hasEffect = entity.hasEffect(PotionInit.lightning_wind_vulnerable);
				float newDamage = hasEffect ? damage * 1.5F : damage;
				this.attackDamage(entity, SMDamage.magicDamage, newDamage);
			}
		}

		if (this.lightningWebTime - 200 >= MAX_LIGHTNINGWEBTIME) {
			this.lightningWebTime = 0;
			this.setLighningWeb(false);
			this.setUpBook(false);
		}
	}

	public void tridentRelease(LivingEntity target) {

		if (this.tridentReleaseTime >= MAX_TRIDENTRELEASETIME && this.tridentList.isEmpty()) {
			this.tridentReleaseTime = 0;
		}

		else if (this.tridentList.isEmpty()) {

			Vec3 look = this.getViewVector(-1F);
			Vec3 src = new Vec3(this.getX(), this.getY(), this.getZ()).add(0, this.getEyeHeight(), 0);
			Vec3 dest = src.add(look.x * 3, this.getY(), look.z * 3);
			List<LivingEntity> targetList = this.getPlayerList(target);
			int count = (int) Math.min(50F, this.getPlayerCount(targetList) * 3F);

			for (int i = 0; i < count; i++) {

				while(true) {
					BlockPos pos = new BlockPos((int) dest.x + this.randRange(20), (int) this.getY() + 4 + this.rand.nextInt(10), (int) dest.z + this.randRange(20));

					if (!this.posList.contains(pos)) {

						LivingEntity entity = targetList.get(this.rand.nextInt(targetList.size()));
						double x = entity.getX() - pos.getX();
						double y = entity.getY(0.3333333333333333D) - pos.getY() + 1.25D;
						double z = entity.getZ() - pos.getZ();
						double xz = Math.sqrt(x * x + z * z);

						TridentThunder knife = new TridentThunder(this.getLevel(), this);
						knife.setHitDead(false);
						knife.shoot(x, y - xz * 0.065D, z, 3F, 2F);
						knife.setPos(pos.getX(), pos.getY(), pos.getZ());
						knife.setAddDamage(knife.getAddDamage() + 20F);
						knife.setRange(3D);
						knife.setCharge(true);
						this.addEntity(knife);
						this.tridentList.add(knife);
						this.posList.add(pos);
						break;
					}
				}
			}
		}

		if (this.tridentReleaseTime + 100 >= MAX_TRIDENTRELEASETIME && !this.tridentList.isEmpty() && this.tickCount % 2 == 0) {

			List<LivingEntity> targetList = this.getPlayerList(target);
			int size = Math.min(targetList.size(), this.rand.nextInt(targetList.size()));
			LivingEntity entity = targetList.get(size);
			TridentThunder knife = this.tridentList.get(0);

			double x = entity.getX() - knife.getX();
			double y = entity.getY(0.3333333333333333D) - knife.getY() + 1.25D;
			double z = entity.getZ() - knife.getZ();
			double xz = Math.sqrt(x * x + z * z);

			knife.shoot(x, y - xz * 0.065D, z, 3F, 2F);
			knife.setCharge(false);
			this.tridentList.remove(0);
			this.posList.remove(0);

			if (this.tridentList.isEmpty()) {
				this.tridentReleaseTime = 0;
			}
		}
	}

	@Override
	public boolean isArmorEmpty() {
		return false;
	}

	public void spawnAction() {
		this.setHealthArmorCount(this.getHealthArmorCount() + 1);
	}

	public void clearInfo() {

		this.thunderTime = 0;
		this.thunderCount = 0;
		this.lightningRodTime = 0;

		if (this.tridentReleaseTime > 0) {
			List<TridentThunder> targetList = this.getEntityList(TridentThunder.class, 48D);
			targetList.forEach(e -> e.discard());
			this.tridentReleaseTime = 0;
		}

		if (this.lightningWebTime == 0) {
			this.lightningWebTime = 0;
			this.setLighningWeb(false);
			this.setUpBook(false);
		}
	}

	public void die(DamageSource src) {
		super.die(src);
		List<TridentThunder> targetList = this.getEntityList(TridentThunder.class, 64D);
		targetList.forEach(e -> e.discard());
	}
}
