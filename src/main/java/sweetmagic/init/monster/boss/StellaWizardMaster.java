package sweetmagic.init.entity.monster.boss;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.entity.animal.StellaWizard;
import sweetmagic.init.entity.projectile.CommetBulet;
import sweetmagic.init.entity.projectile.ElectricMagicShot;
import sweetmagic.init.entity.projectile.EvilArrow;
import sweetmagic.init.entity.projectile.KnifeShot;
import sweetmagic.init.entity.projectile.ShootingStar;
import sweetmagic.init.entity.projectile.TripleTornadoShot;
import sweetmagic.util.SMDamage;

public class StellaWizardMaster extends AbstractSMBoss {

	private static final EntityDataAccessor<Integer> ROBE_TYPE = ISMMob.setData(StellaWizardMaster.class, INT);
	private static final EntityDataAccessor<Integer> GIMMICK = ISMMob.setData(StellaWizardMaster.class, INT);
	private static final EntityDataAccessor<Boolean> DEMONS = ISMMob.setData(StellaWizardMaster.class, BOOLEAN);
	private static final ResourceLocation WIND = SweetMagicCore.getSRC("textures/armor/windwitch_master_robe.png");
	private static final ResourceLocation BLIZE = SweetMagicCore.getSRC("textures/armor/blize_robe.png");
	private static final ResourceLocation IGNIS = SweetMagicCore.getSRC("textures/armor/ignisknight_robe.png");
	private static final ResourceLocation BUTLER = SweetMagicCore.getSRC("textures/armor/butler_robe.png");

	private int summonCometTime = 150;
	private static final int MAX_SUMMON_COMET_TIME = 300;
	private int cometShotTime = 0;
	private static final int MAX_COMET_SHOT_TIME = 200;
	private int evilShotTime = 100;
	private static final int MAX_EVIL_SHOT_TIME = 400;
	private int shootingStarTime = 200;
	private static final int MAX_SHOOTINGSTAR_TIME = 400;
	private Map<Integer, BlockPos> posMap = new HashMap<>();
	private int robeCount = 0;
	private int robeTime = 0;
	private int maxRobeTime = 0;

	public StellaWizardMaster(Level world) {
		super(EntityInit.stellaWizardMaster, world);
	}

	public StellaWizardMaster(EntityType<StellaWizardMaster> enType, Level world) {
		super(enType, world);
		this.xpReward = 800;
		this.setBossEvent(BC_BLUE, NOTCHED_6);
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

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.define(ROBE_TYPE, -1);
		this.define(GIMMICK, 0);
		this.define(DEMONS, false);
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

	public int getRobeType() {
		return this.get(ROBE_TYPE);
	}

	public void setRobeType(int robeType) {
		this.set(ROBE_TYPE, robeType);
	}

	public int getGimmick() {
		return this.get(GIMMICK);
	}

	public void setGimmick(int gimmick) {
		this.set(GIMMICK, gimmick);
	}

	public boolean getDemons() {
		return this.get(DEMONS);
	}

	public void setDemons(boolean demons) {
		this.set(DEMONS, demons);
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putInt("robeType", this.getRobeType());
		tags.putInt("gimmick", this.getGimmick());
		tags.putBoolean("demons", this.getDemons());
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setRobeType(tags.getInt("robeType"));
		this.setGimmick(tags.getInt("gimmick"));
		this.setDemons(tags.getBoolean("demons"));
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {
		Entity attacker = src.getEntity();
		Entity attackEntity = src.getDirectEntity();
		if (attacker != null && attacker instanceof ISMMob) { return false; }

		// ボスダメージ計算
		float cap = this.getDemons() ? 3F : 6F;

		if(this.getGimmick() > 0) {
			cap += this.getGimmick() * 1.5F;
		}

		amount = this.getBossDamageAmount(this.getLevel(), this.defTime , src, amount, this.getHealthArmor() > 0F ? cap * 0.75F : cap);
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

	protected void halfHealthAction(LivingEntity target) {
		float healthArmor = this.getHealthArmor();
		if(healthArmor > 0F) { return; }

		int count = this.getHealthArmorCount();

		if(count > 0) {
			this.armorHealthSet(target, 1);
			this.setHealthArmorCount(count - 1);
			this.setRobeType(this.getHealthArmorCount());
		}

		if(count <= 0) {
			this.setHalfHealth(true);
		}
	}

	public MutableComponent getArmorName() {
		String name = "";
		switch(this.getRobeType()) {
		case 0:
			name = "blize_robe";
			break;
		case 1:
			name = "ifrite_robe";
			break;
		case 2:
			name = "windwitch_master_robe";
			break;
		case 3:
			name = "butler_robe";
			break;
		}

		return this.getTip("item.sweetmagic." + name);
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

	public void firstAttack(LivingEntity target) {

		if(this.summonCometTime++ >= MAX_SUMMON_COMET_TIME) {
			this.summonComet(target);
		}

		if(this.cometShotTime++ >= MAX_COMET_SHOT_TIME) {
			this.cometShot(target);
		}

		if(this.evilShotTime++ >= MAX_EVIL_SHOT_TIME) {
			this.evilShot(target);
		}
	}

	public void secondAttack(LivingEntity target) {

		int robeType = this.getRobeValue();

		if(robeType != -1) {
			this.robeAttack(target, robeType);
		}

		else if(this.shootingStarTime++ >= MAX_SHOOTINGSTAR_TIME) {
			this.shootingStarShot(target);
		}
	}

	// コメット召喚
	public void summonComet(LivingEntity target) {

		this.summonCometTime = 0;
		Level world = this.getLevel();
		List<LivingEntity> targetList = this.getPlayerList(target);
		boolean isWarden = target instanceof Warden;
		float dama = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) + (isWarden ? 50F : 30F);
		float dameRate = isWarden ? 1.5F : 1F;
		int level = isWarden ? 50 : 20;
		int summonSize = Math.min(3, Math.max(1, 45 / targetList.size()));

		for (LivingEntity entity : targetList) {

			double x = entity.getX() - this.getX();
			double y = entity.getY(0.3333333333333333D) - this.getY();
			double z = entity.getZ() - this.getZ();
			double xz = Math.sqrt(x * x + z * z);

			for(int i = 0; i < summonSize; i++) {
				CommetBulet magic = new CommetBulet(world, this);
				magic.setWandLevel(level);
				magic.shoot(x, y - xz * 0.065D, z, 2F, 0F);
				magic.setAddDamage((magic.getAddDamage() + dama) * dameRate);
				magic.setTarget(entity);
				magic.setCharge(true);
				magic.setDeltaMovement(new Vec3(0D, 0D, 0D));
				this.addEntity(magic);
			}
		}
	}

	// コメット射出
	public void cometShot(LivingEntity target) {

		this.summonCometTime = 200;
		List<CommetBulet> commetList = this.getEntityList(CommetBulet.class, e -> e.getOwner() == this && e.getCharge(), 64D);

		if(!commetList.isEmpty()) {
			commetList.get(0).setCharge(false);
			this.cometShotTime = MAX_COMET_SHOT_TIME - 4;
		}

		else {
			this.cometShotTime = 0;
		}
	}

	// コメット射出
	public void evilShot(LivingEntity target) {

		this.evilShotTime = 0;
		List<LivingEntity> targetList = this.getPlayerList(target);

		for (LivingEntity entity : targetList) {

			double x = entity.getX() - this.getX();
			double y = entity.getY(0.3333333333333333D) - this.getY();
			double z = entity.getZ() - this.getZ();
			double xz = Math.sqrt(x * x + z * z);
			EvilArrow magic = new EvilArrow(this.getLevel(), this);
			magic.shoot(x, y - xz * 0.065D, z, 2F, 0F);
			this.addEntity(magic);
		}
	}

	// ローブで攻撃種別変更
	public void robeAttack(LivingEntity target, int robeType) {
		switch(robeType) {
		case 0:
			this.maxRobeTime = 240;
			this.blitzAttack(target);
			break;
		case 1:
			this.maxRobeTime = 150;
			this.ignisAttack(target);
			break;
		case 2:
			this.maxRobeTime = 200;
			this.windWitchAttack(target);
			break;
		case 3:
			this.maxRobeTime = 140;
			this.butlerAttack(target);
			break;
		}
	}

	// ブリッツ
	public void blitzAttack(LivingEntity target) {
		if(this.robeTime++ < this.maxRobeTime || this.tickCount % 20 != 0) { return; }

		boolean isWarden = target instanceof Warden;
		float damage = isWarden ? 50F : 7.5F;
		int angle = 15 + this.tickCount / 20;
		int rangeMax = 16;

		for (int i = 0; i < rangeMax; i++) {

			ElectricMagicShot entity = new ElectricMagicShot(this.getLevel(), this);
			double d0 = target.getX() - this.getX();
			double d1 = target.getZ() - this.getZ();
			double d3 = 0D;
			Vector3f vec = this.getShotVector(this, new Vec3(d0, d3, d1), 360 / rangeMax * (i + 1) + angle);
			entity.shoot(vec.x(), vec.y(), vec.z(), 0.875F, 1.75F);
			entity.setAddDamage(entity.getAddDamage() + damage);
			entity.setMaxLifeTime(40);
			entity.setRange(3.5D);
			entity.setHitDead(false);
			entity.setNotDamage(true);
			entity.isRangeAttack = true;
			this.addEntity(entity);
		}

		this.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);

		if (this.robeCount++ >= 3) {
			this.robeCount = 0;
			this.robeTime = 0;
		}
	}

	// イグニス
	public void ignisAttack(LivingEntity target) {
		if(this.robeTime++ < this.maxRobeTime) { return; }

//		this.setBlast(true);
		Level world = this.getLevel();
		List<LivingEntity> entityList = this.getPlayerList(target);

		if (this.robeTime == this.getMaxTime(10)) {
			BlockPos beforePos = this.blockPosition();
			this.teleportTo(target.getX(), target.getY() + 5D, target.getZ());
			this.teleportParticle(ParticleTypes.SOUL_FIRE_FLAME, world, beforePos, this.blockPosition());
		}

		else if (this.robeTime > this.getMaxTime(10) && this.robeTime <= this.getMaxTime(50)) {
			Vec3 vec = this.getDeltaMovement();
			if (vec.y < 0D) {
				this.setDeltaMovement(new Vec3(0D, 0D, 0D));
			}

			if (this.robeTime == this.getMaxTime(50) + this.maxRobeTime) {
//				this.setSwing(true);
			}

			if (world instanceof ServerLevel sever) {

				for (int i = 0; i < 4; i++) {
					float x = (float) this.getX() + this.getRandFloat(0.5F);
					float y = (float) this.getY() + this.getRandFloat(0.5F);
					float z = (float) this.getZ() + this.getRandFloat(0.5F);

					float aX = this.getRandFloat(0.75F);
					float aY = 0.1F + this.rand.nextFloat() * 0.2F;
					float aZ = this.getRandFloat(0.75F);
					sever.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0, aX, aY, aZ, 1F);
				}
			}

			if (this.robeTime == this.getMaxTime(11) || this.robeTime == this.getMaxTime(41)) {
				double range = 15D + this.getPlayerCount(entityList) * 0.5D;
				for (int i = 1; i <= 4; i++) {
					this.spawnParticleCycle(this.blockPosition().below(4), range * 0.25D * i);
				}
			}
		}

		// 一定時間が経ったら
		else if (this.robeTime > this.getMaxTime(50)) {

			// 地面に落下
			if (!this.onGround) {
				this.setDeltaMovement(new Vec3(0D, -1.5D, 0D));
			}

			// 地面に付いたら
			else {

				if (this.robeCount == 0) {

					this.robeCount++;
					float amount = 50F + this.getBuffPower();
					double range = 15D + this.getPlayerCount(entityList) * 0.5D;
					List<LivingEntity> attackList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), range);
					attackList.forEach(e -> this.attackDamage(e, this.getSRC(), e instanceof Enemy ? amount * 8F : amount));
					this.playSound(SoundEvents.GENERIC_EXPLODE, 3F, 1F / (this.rand.nextFloat() * 0.2F + 0.9F));
					if (!(world instanceof ServerLevel sever)) { return; }

					sever.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY() + 1F, this.getZ(), 1, 0F, 0F, 0F, 1F);
					float x = (float) this.getX() + this.getRandFloat(0.5F);
					float y = (float) this.getY() + this.getRandFloat(0.5F);
					float z = (float) this.getZ() + this.getRandFloat(0.5F);

					for (int i = 0; i < 16; i++) {
						sever.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 4, 0F, 0F, 0F, 0.15F);
					}
				}

				else {

					if (this.tickTime++ < 19 || this.tickTime % 20 != 0) { return; }

					this.robeCount++;
					float amount = 15F + this.getBuffPower();
					double ran = 20D + this.getPlayerCount(entityList) * 2D;
					List<LivingEntity> attackList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), ran);
					attackList.forEach(e -> this.attackDamage(e, this.getSRC(), e instanceof Enemy ? amount * 8F : amount));
					this.playSound(SoundEvents.BLAZE_SHOOT, 1F, 1F / (this.rand.nextFloat() * 0.2F + 0.9F));

					if(this.robeCount > 3) {
						this.robeTime = 0;
						this.robeCount = 0;
						this.tickTime = 0;
					}

					if (!(world instanceof ServerLevel sever)) { return; }

					BlockPos pos = this.blockPosition();

					for (int range = 0; range < 3; range++) {
						for (int i = 0; i < 3; i++) {
							this.spawnParticleRing(sever, ParticleTypes.SOUL_FIRE_FLAME, 2 + range * 4, pos, -0.25D + i * 0.5D);
						}
					}
				}
			}
		}
	}

	// ウィンドウィッチ
	public void windWitchAttack(LivingEntity target) {
		if(this.robeTime++ < this.maxRobeTime) { return; }

		Level world = this.getLevel();
		boolean isPlayer = this.isPlayer(target);
		Map<Integer, BlockPos> posMap = new HashMap<>();
		List<LivingEntity> entityList = this.getPlayerList(target);
		double range = Math.min(15D, 8D + entityList.size() * 0.325D);
		int count = 0;
		int maxCount = 5;

		for (LivingEntity entity : entityList) {
			posMap.put(count, entity.blockPosition());
			if (++count >= maxCount) { break; }
		}

		// 座標が5つ登録できていないなら
		if (count < maxCount) {

			int mapRange = 32;
			BlockPos basePos = this.blockPosition();

			for (int i = count; i < 3; i++) {

				// 被っていない座標をposMapに登録
				while (true) {
					BlockPos pos = basePos.offset(this.randRange(mapRange), 0, this.randRange(mapRange));

					if (!posMap.containsValue(pos) && !world.isEmptyBlock(pos.below())) {
						posMap.put(i, pos);
						break;
					}
				}
			}
		}

		// 登録した座標分トルネードを召喚
		for (Entry<Integer, BlockPos> map : posMap.entrySet()) {

			BlockPos pos = map.getValue();
			TripleTornadoShot entity = new TripleTornadoShot(world, this);
			entity.shootFromRotation(this, this.getXRot(), this.getYRot(), 0, 1F, 0);
			entity.shoot(0D, 0D, 0D, 0F, 0F);
			entity.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
			entity.setHitDead(false);
			entity.setNotDamage(true);
			entity.setRange(range);
			entity.setAddDamage(40F + this.getBuffPower());
			entity.isPlayer = isPlayer;
			this.addEntity(entity);
		}

		this.robeTime = 0;
	}

	// バトラー
	public void butlerAttack(LivingEntity target) {
		if(this.robeTime++ < this.maxRobeTime) { return; }

		List<LivingEntity> targetList = this.getPlayerList(target);

		for (LivingEntity entity : targetList) {

			double x = entity.getX() - this.getX();
			double y = entity.getY(0.3333333333333333D) - this.getY() - 0.25D;
			double z = entity.getZ() - this.getZ();
			double xz = Math.sqrt(x * x + z * z);

			KnifeShot knife = new KnifeShot(this.getLevel(), this);
			knife.setHitDead(false);
			knife.shoot(x, y - xz * 0.065D, z, 1.5F, 1.5F);
			knife.setAddDamage(knife.getAddDamage() + 6F);
			this.addEntity(knife);
		}

		this.playSound(SoundInit.KNIFE_SHOT, 0.5F, 0.9F);
		this.robeTime = 0;
	}

	public void shootingStarShot(LivingEntity target) {
		if(this.shootingStarTime++ < MAX_SHOOTINGSTAR_TIME) { return; }

		Level world = this.getLevel();

		if (this.posMap.isEmpty() && this.shootingStarTime <= MAX_SHOOTINGSTAR_TIME + 2) {
			this.teleportTo(this.getX(), this.getY() + 6D, this.getZ());
			int mapRange = 48;
			BlockPos basePos = this.blockPosition();
			int count = (int) Math.max(10, 6F * this.getPlayerCount(target) * 0.5F);

			for (int i = 0; i < count; i++) {

				// 被っていない座標をposMapに登録
				while (true) {
					BlockPos pos = basePos.offset(this.randRange(mapRange), -6D, this.randRange(mapRange));
					if (this.posMap.containsValue(pos) || world.isEmptyBlock(pos.below())) { continue; }

					this.posMap.put(i, pos);
					break;
				}
			}
		}

		else if (this.shootingStarTime > MAX_SHOOTINGSTAR_TIME && this.shootingStarTime <= MAX_SHOOTINGSTAR_TIME + 160) {

			this.setDeltaMovement(new Vec3(0D, 0D, 0D));

			if (this.shootingStarTime % 30 == 0) {
				double range = 12.5D;
				for (BlockPos pos : this.posMap.values()) {
					for (int i = 1; i <= 3; i++) {
						this.spawnParticleCycle(pos, range * i / 3);
					}
				}
			}

			if(!this.posMap.isEmpty()) {
				double range = 12.5D;
				float damage = 30F;

				for (Entry<Integer, BlockPos> map : this.posMap.entrySet()) {

					BlockPos pos = map.getValue().above(15);
					ShootingStar magic = new ShootingStar(world, this);
					magic.setAddDamage(magic.getAddDamage());
					magic.setRange(range);
					magic.shootFromRotation(this, this.getXRot(), this.getYRot(), 0, 0, 0);
					magic.setPos(pos.getX(), pos.getY(), pos.getZ());
					magic.setAddDamage(magic.getAddDamage() + damage);
					magic.setHitDead(false);
					magic.setCharge(true);
					this.addEntity(magic);
				}

				this.posMap.clear();
			}
		}

		else if(this.shootingStarTime > MAX_SHOOTINGSTAR_TIME + 160 && this.shootingStarTime % 3 == 0) {

			this.setDeltaMovement(new Vec3(0D, 0D, 0D));

			List<ShootingStar> commetList = this.getEntityList(ShootingStar.class, e -> e.getOwner() == this && e.getCharge(), 64D);

			if(!commetList.isEmpty()) {
				Vec3 vec = new Vec3(0, -1.5D, 0);
				ShootingStar magic = commetList.get(0);
				magic.setCharge(false);
				magic.setDeltaMovement(vec);
			}

			else {
				this.shootingStarTime = 0;
			}
		}
	}

	public int getMaxTime(int time) {
		return this.maxRobeTime + time;
	}

	public int getRobeValue() {
		return this.getHealthArmor() > 0F ? this.getRobeType() : -1;
	}

	@Override
	public boolean isArmorEmpty() {
		return false;
	}

	public void spawnAction() {
		this.setHealthArmorCount(4);
	}

	public ResourceLocation getTEX() {
		switch(this.getRobeType()) {
		case 1: return IGNIS;
		case 2: return WIND;
		case 3: return BUTLER;
		default: return BLIZE;
		}
	}

	public void clearInfo() {
		this.summonCometTime = 0;
		this.cometShotTime = 0;
		this.evilShotTime = 0;
		this.shootingStarTime = 0;
		this.robeCount = 0;
		this.robeTime = 0;
		this.posMap.clear();
	}

	protected void deathFinish() {
		if(!this.getDemons()) { return; }
		List<Player> playerList = this.getEntityList(Player.class, 48D);

		if(!playerList.isEmpty()) {
			Player player = playerList.get(0);
			Vec3 src = new Vec3(player.getX(), player.getY(), player.getZ()).add(0, player.getEyeHeight(), 0);
			Vec3 look = player.getViewVector(5F);
			Vec3 dest = src.add(look.x * 3, player.getY(), look.z * 3);

			BlockPos pos = new BlockPos(dest.x, player.getY() + 1, dest.z);
			StellaWizard entity = new StellaWizard(this.getLevel());
			entity.setPos(pos.getX() + 0.5D, pos.getY() + 1.5D, pos.getZ() + 0.5D);
			entity.tame(player);

			int level = 60;
			double rate = Math.max(3.5D, (1D + (level - 1) * 0.075D));

			entity.setWandLevel(level);
			entity.setState(rate);
			entity.setMaxLifeTime(99999);
			entity.setCrystal(true);
			entity.setDemons(true);
			entity.setCrystalHealth(128F);
			entity.setMaxCrystalHealth(128F);
			entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(entity.getMaxHealth() * 2F);
			entity.setHealth(entity.getMaxHealth());
			this.addEntity(entity);

			if(!this.isClient()) {
				this.sendMSG(playerList, this.getText("stella_wizard_crystal").withStyle(GREEN));
			}
		}

		DemonsBelial entity = new DemonsBelial(this.getLevel());
		entity.setPos(this.getX(), this.getY() + 0.5F, this.getZ());
		entity.setHealthArmorCount(1);
		entity.startInfo();
		entity.startInfo();
		this.addEntity(entity);
	}
}
