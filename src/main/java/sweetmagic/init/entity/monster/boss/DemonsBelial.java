package sweetmagic.init.entity.monster.boss;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.BlockInit;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.entity.animal.AbstractSummonMob;
import sweetmagic.init.entity.animal.StellaWizard;
import sweetmagic.init.entity.projectile.BelialFlameShot;
import sweetmagic.init.entity.projectile.BelialSword;
import sweetmagic.util.SMDamage;

public class DemonsBelial extends AbstractSMBoss {

	private int heartAnimation;
	private int heartAnimationO;
	private int attackType = 0;
	private int attackTime = 0;
	private int recastTime = 0;
	private boolean isGround = false;
	private double aX, aY, aZ;
	public AnimationState defalutAttackAnim = new AnimationState();
	public AnimationState landingAnim = new AnimationState();
	public AnimationState laserAnim = new AnimationState();
	public AnimationState swingAnim = new AnimationState();
	public AnimationState meteorAnim = new AnimationState();
	public AnimationState downAnim = new AnimationState();
	private static final BlockState FLAME = BlockInit.belial_flame.defaultBlockState();
	private static final EntityDataAccessor<Boolean> NOMOVE = ISMMob.setData(DemonsBelial.class, BOOLEAN);
	private static final EntityDataAccessor<Boolean> LASER = ISMMob.setData(DemonsBelial.class, BOOLEAN);

	public DemonsBelial(Level world) {
		super(EntityInit.demonsBelial, world);
	}

	public DemonsBelial(EntityType<DemonsBelial> enType, Level world) {
		super(enType, world);
		this.xpReward = 1200;
		this.setBossEvent(BC_BLUE, NOTCHED_6);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 1024D)
				.add(Attributes.MOVEMENT_SPEED, 0.375D)
				.add(Attributes.ATTACK_DAMAGE, 20D)
				.add(Attributes.ARMOR, 40D)
				.add(Attributes.FOLLOW_RANGE, 64D);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.define(NOMOVE, false);
		this.define(LASER, false);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1D) {

			   public boolean canContinueToUse() {
				   return super.canContinueToUse() && !getNoMove();
			   }
		});
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
		return SoundEvents.WARDEN_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource src) {
		return SoundEvents.WARDEN_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.WARDEN_DEATH;
	}

	public boolean getNoMove() {
		return this.get(NOMOVE);
	}

	public void setNoMove(boolean noMove) {
		this.set(NOMOVE, noMove);
	}

	public boolean getLaser() {
		return this.get(LASER);
	}

	public void setLaser(boolean laser) {
		this.set(LASER, laser);
	}

	public boolean isPushable() {
		return false;
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putInt("attackType", this.attackType);
		tags.putInt("attackTime", this.attackTime);
		tags.putInt("recastTime", this.recastTime);
		tags.putBoolean("noMove", this.getNoMove());
		tags.putBoolean("laser", this.getLaser());
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.attackType = tags.getInt("attackType");
		this.attackTime = tags.getInt("attackTime");
		this.recastTime = tags.getInt("recastTime");
		this.setNoMove(tags.getBoolean("noMove"));
		this.setLaser(tags.getBoolean("laser"));
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {
		Entity attacker = src.getEntity();
		Entity attackEntity = src.getDirectEntity();
		if (attacker != null && attacker instanceof ISMMob) { return false; }

		// ボスダメージ計算
		float cap = this.isHalfHealth(this) ? 3F : 4F;
		amount = this.getBossDamageAmount(this.getLevel(), this.defTime , src, amount, this.getHealthArmor() > 0F ? cap * 0.5F : cap);
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

	public void handleEntityEvent(byte par1) {
		switch(par1) {
		case 4:
			this.defalutAttackAnim.stop();
			this.landingAnim.stop();
			this.laserAnim.stop();
			this.swingAnim.stop();
			this.meteorAnim.stop();
			break;
		case 5:
			this.defalutAttackAnim.start(this.tickCount);
			break;
		case 6:
			this.landingAnim.start(this.tickCount);
			break;
		case 7:
			this.laserAnim.start(this.tickCount);
			break;
		case 8:
			this.meteorAnim.start(this.tickCount);
			break;
		case 9:
			this.swingAnim.start(this.tickCount);
			break;
		case 10:
			this.downAnim.start(this.tickCount);
			break;
		default:
			super.handleEntityEvent(par1);
			break;
		}
	}

	public void checkSpawnPos() {
		BlockPos pos = this.getSpawnPos();
		if (pos == null) { return; }

		double dis = this.distanceToSqr(pos.getX(), pos.getY(), pos.getZ());

		if (dis >= 1500 && !this.getNoMove()) {
			this.teleportSpawnPos(pos);
		}
	}

	public void tick() {

		super.tick();
		if (this.isClient()) {

			int heartInterval = this.isHalfHealth(this) ? 15 : 30;

			this.heartAnimationO = this.heartAnimation;
			if (this.heartAnimation > 0) {
				--this.heartAnimation;
			}

			else if (this.tickCount % heartInterval == 0) {
				this.heartAnimation = 10;
			}

			if(this.getLaser()) {
				this.heartAnimation = 10;
			}
		}
	}

	protected void customServerAiStep() {
		super.customServerAiStep();

		LivingEntity target = this.getTarget();
		if (target == null) {
			if(this.tickCount % 20 == 0) {
				this.clearInfo();
			}
			return;
		}

		this.getLookControl().setLookAt(target, 10F, 10F);
		if(this.recastTime-- > 0) { return; }

		this.firstAttack(target);
	}

	public void firstAttack(LivingEntity target) {

		this.attackTime++;

		if(this.attackTime == 1) {

			this.attackType = this.rand.nextInt(4);

			if(this.attackType == 3 && this.rand.nextFloat() > 0.5F) {
				this.attackType = this.rand.nextInt(3);
			}

			this.getLevel().broadcastEntityEvent(this, (byte) 4);
		}

		switch(this.attackType) {
		case 0:
			this.defalutAttack(target);
			break;
		case 1:
			this.landingAttack(target);
			break;
		case 2:
			this.meteorAttack(target);
			break;
		case 3:
			this.swingAttack(target);
			break;
		}
	}

	public void defalutAttack(LivingEntity target) {

		if(this.checkTime(1, 201)) {
			if(this.attackTime == 1) {
				this.teleportTo(target.getX(), target.getY() + 8D, target.getZ());
				this.getLevel().broadcastEntityEvent(this, (byte) 5);
				this.setNoMove(true);
				this.isGround = false;
			}

			if(this.attackTime % 5 == 0) {
				double x = this.getX() + Math.min(0.1D, target.getX() - this.getX());
				double y = this.getY() + Math.min(0.1D, target.getY() + 8D - this.getY());
				double z = this.getZ() + Math.min(0.1D, target.getZ() - this.getZ());
				this.teleportTo(x, y, z);
			}

			this.setDeltaMovement(new Vec3(0D, 0D, 0D));
		}

		else if(this.checkTime(201, 241)) {
			this.setDeltaMovement(new Vec3(0D, 0D, 0D));
		}

		else if(this.checkTime(241, 522)) {

			if(this.attackTime == 241) {
				this.setDeltaMovement(new Vec3(0D, -1D, 0D));
			}

			else if(this.onGround && !this.isGround) {

				Level world = this.getLevel();
				float effectRange = 64;
				Iterable<BlockPos> posList = this.getPosRangeList(this.blockPosition(), 8);

				for(BlockPos pos : posList) {
					if(this.rand.nextFloat() <= 0.67F || !world.isEmptyBlock(pos) || world.isEmptyBlock(pos.below()) || !this.checkDistance(pos, effectRange)) { continue; }
					world.setBlock(pos, FLAME, 3);
				}

				this.isGround = true;
				this.aX = this.getX();
				this.aY = this.getY();
				this.aZ = this.getZ();
				float amount = 60F + this.getBuffPower();
				double range = 24D + this.getPlayerCount(target) * 0.5D;
				List<LivingEntity> attackList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), range);
				this.attackDamage(attackList, this.getSRC(), amount);
				this.playSound(SoundEvents.GENERIC_EXPLODE, 3F, 1F / (this.rand.nextFloat() * 0.2F + 0.9F));
				if(!(this.getLevel() instanceof ServerLevel server)) { return; }

				server.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY() + 1F, this.getZ(), 1, 0F, 0F, 0F, 1F);
				int count = 128;
				float rate = 0.5F;
				BlockPos pos = this.blockPosition();
				float x = pos.getX() + this.getRandFloat(0.5F);
				float y = pos.getY() + this.getRandFloat(0.5F);
				float z = pos.getZ() + this.getRandFloat(0.5F);

				for (int i = 0; i < count; i++) {
					server.sendParticles(ParticleInit.BELIAL_FLAME, x, y, z, 8, 0F, 0F, 0F, rate);
				}
			}

			else if(this.isGround) {

				if(this.aX !=  this.getX() || this.aY !=  this.getY() || this.aZ !=  this.getZ()) {
					this.teleportTo(this.aX, this.aY, this.aZ);
				}

				if(this.attackTime == 464) {
					this.playSound(SoundEvents.WARDEN_SONIC_BOOM, 2F, 1.25F);
					if(!(this.getLevel() instanceof ServerLevel server)) { return; }

					BlockPos pos = this.blockPosition();
					for (int range = 0; range < 3; range++) {
						for (int i = 0; i < 4; i++) {
							this.spawnParticleRing(server, ParticleInit.BELIAL_FLAME, 2 + range * 4, pos, -0.25D + i * 0.5D);
						}

						if(this.rand.nextFloat() >= 0.67F) {
							this.spawnParticleRing(server, ParticleTypes.CLOUD, 2 + range * 4, pos, 0D);
						}
					}

					Level world = this.getLevel();
					Iterable<BlockPos> posList = this.getPosRangeList(this.blockPosition(), 32);

					for(BlockPos p : posList) {
						if(world.getBlockState(p).getBlock() != FLAME.getBlock()) { continue; }
						world.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
					}
				}

				else if(this.attackTime == 469) {
					List<LivingEntity> entityList = this.getPlayerList(target);

					for(LivingEntity entity : entityList) {
						entity.setHealth(Math.max(0.1F, entity.getHealth() - entity.getMaxHealth() * 0.1F));
						this.attackDamage(entity, this.getSRC(), 30F);
					}
				}

				else if(this.attackTime == 521) {
					this.clearInfo();
					this.recastTime = 50;
				}
			}
		}
	}

	public void landingAttack(LivingEntity target) {

		if(this.checkTime(1, 282)) {
			if(this.attackTime == 1) {
				this.getLevel().broadcastEntityEvent(this, (byte) 6);
				this.setNoMove(true);
				this.isGround = false;
			}

			else if(this.attackTime == 11) {
				this.teleportTo(target.getX(), target.getY() + 8D, target.getZ());
				this.setDeltaMovement(new Vec3(0D, 0D, 0D));
			}

			else if(!this.isGround && this.attackTime < 51) {
				this.setDeltaMovement(new Vec3(0D, 0D, 0D));
			}

			else if(this.attackTime == 51) {
				this.setDeltaMovement(new Vec3(0D, -1D, 0D));
			}

			else if(this.onGround && !this.isGround) {
				this.isGround = true;
				this.aX = this.getX();
				this.aY = this.getY();
				this.aZ = this.getZ();

				this.playSound(SoundEvents.LIGHTNING_BOLT_IMPACT, 1F, 1F);
				double range = 32D + this.getPlayerCount(target) * 0.75D;
				float damage = 35F + this.getBuffPower() * 0.5F;
				List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), range);
				if (entityList.isEmpty()) { return; }

				for (LivingEntity entity : entityList) {
					int addDamage = (int) (Math.max(1D, 24D - entity.distanceTo(this)) / 3D) + 1;

					for (int i = 0; i < addDamage; i++) {
						this.attackDamage(entity, this.getSRC(), damage);
					}
				}

				if(!(this.getLevel() instanceof ServerLevel server)) { return; }

				BlockPos pos = this.blockPosition();
				for (int ran = 0; ran < 3; ran++) {
					this.spawnParticleRing(server, ParticleTypes.CLOUD, 2 + ran * 4, pos, 0D);
				}
			}

			else if(this.isGround) {

				if(this.aX !=  this.getX() || this.aY !=  this.getY() || this.aZ !=  this.getZ()) {
					this.teleportTo(this.aX, this.aY, this.aZ);
				}

				if(this.attackTime == 121) {
					this.getLevel().broadcastEntityEvent(this, (byte) 7);
				}

				else if(this.checkTime(122, 180)) {

					if(this.attackTime == 122) {
						this.playSound(SoundEvents.WARDEN_SONIC_CHARGE, 1F, 1F);
					}

					if(!(this.getLevel() instanceof ServerLevel server)) { return; }

					int count = 1 + (this.attackTime - 120) / 10;
					for (int i = 0; i < count; i++) {

						float randX = this.randRange(4);
						float randY = this.getRandFloat(3F);
						float randZ = this.randRange(4);
						float x = (float) this.getX() + randX;
						float y = (float) this.getY() + 1.5F + randY;
						float z = (float) this.getZ() + randZ;
						float xSpeed = -randX * 0.115F;
						float ySpeed = -randY * 0.115F;
						float zSpeed = -randZ * 0.115F;

						server.sendParticles(ParticleInit.BLOOD, x, y, z, 0, xSpeed, ySpeed, zSpeed, 1F);
					}
				}

				else if(this.checkTime(190, 280)) {

					if(this.attackTime == 200) {
						this.playSound(SoundEvents.WARDEN_SONIC_BOOM, 1F, 1F);
					}

					double d1 = target.getX() - this.getX();
					double d2 = target.getZ() - this.getZ();
					this.setYRot(-((float) Math.atan2(d1, d2)) * (180F / (float) Math.PI));
					Vec3 look = this.getViewVector(1F);
					Vec3 src = new Vec3(this.getX(), this.getY(), this.getZ()).add(0, this.getEyeHeight() / 2F, 0);

					// パーティクルを出す
					if (this.getLevel() instanceof ServerLevel server) {

						for (int i = 0; i < 20; i++) {

							Vec3 dest = src.add(look.x * i, look.y, look.z * i);
							float f1 = (float) dest.x - 0.5F + this.rand.nextFloat();
							float f2 = (float) dest.y - 1F + this.rand.nextFloat();
							float f3 = (float) dest.z - 0.5F + this.rand.nextFloat();
							float x = (float) (dest.x - this.getX()) / 10F;
							float y = (float) (dest.y - this.getY()) / 10F;
							float z = (float) (dest.z - this.getZ()) / 10F;
							server.sendParticles(ParticleInit.BELIAL_FLAME, f1, f2, f3, 0, x, y, z, 1F);

							if(i % 2 != 0 || this.attackTime != 200) { continue; }
							server.sendParticles(ParticleInit.CYCLONE, dest.x, dest.y, dest.z, 0, 0F, 0F, 0F, 1F);
						}
					}

					if (this.attackTime % 5 == 0) {
						float damage = 10F + this.getBuffPower() * 0.5F;
						boolean isPlayer = this.isPlayer(target);
						List<LivingEntity> entityAllList = new ArrayList<>();

						// 30ブロック先まで
						for (double i = 0; i < 30D; i += 0.5D) {

							Vec3 dest = src.add(look.x * i, look.y * i, look.z * i);
							List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.getFilter(isPlayer, entityAllList), dest, 1D);
							entityAllList.addAll(entityList);
							this.attackDamage(entityList, SMDamage.flameDamage, damage);
						}
					}
				}

				else if(this.attackTime == 281) {
					this.clearInfo();
					this.recastTime = 50;
				}
			}
		}
	}

	public void meteorAttack(LivingEntity target) {

		if(this.checkTime(1, 120)) {
			if(this.attackTime == 1) {
				this.getLevel().broadcastEntityEvent(this, (byte) 8);
				this.setNoMove(true);
				this.aX = this.getX();
				this.aY = this.getY();
				this.aZ = this.getZ();
			}

			else if(this.checkTime(40, 101)) {

				if(this.attackTime == 40) {
					this.playSound(SoundEvents.BLAZE_SHOOT, 1F, 0.67F);
				}

				float damage = 15F;
				int countUp = 0;
				BlockPos targetPos = this.blockPosition().offset(this.getRandFloat(32F), 24D, this.getRandFloat(32F));

				while(true) {
					countUp++;
					targetPos = targetPos.above();
					if(this.getLevel().isEmptyBlock(targetPos) && countUp < 24) { continue; }
					break;
				}

				BelialFlameShot magic = new BelialFlameShot(this.getLevel(), this);
				magic.shootFromRotation(this, this.getXRot(), this.getYRot(), 0, 0, 0);
				magic.setRange(5D);
				magic.setPos(targetPos.getX(), targetPos.getY(), targetPos.getZ());
				magic.setAddDamage(magic.getAddDamage() + damage);
				magic.setDeltaMovement(new Vec3(0, -1D, 0));
				magic.setAddDamage(magic.getAddDamage() + 30F + this.getBuffPower() * 0.67F);
				magic.setHitDead(false);
				this.addEntity(magic);
				if(!(this.getLevel() instanceof ServerLevel server) || this.attackTime % 6 != 0) { return; }

				int count = 16;
				BlockPos pos = this.blockPosition();
				ParticleOptions par = ParticleInit.CYCLE_BELIAL_TORNADO;

				for (int y = -40; y < 8; y++) {
					for (int i = 0; i < count; i++) {
						this.spawnParticleCycle(server, par, pos.getX() + 0.5D, pos.getY() - 0.5D + this.rand.nextDouble() * 1.5D + y * 0.5D, pos.getZ() + 0.5D, Direction.UP, 2, i * 16F + y * 15, false);
					}
				}
			}

			double d1 = target.getX() - this.getX();
			double d2 = target.getZ() - this.getZ();
			this.setYRot(-((float) Math.atan2(d1, d2)) * (180F / (float) Math.PI));

			if(this.aX !=  this.getX() || this.aY !=  this.getY() || this.aZ !=  this.getZ()) {
				this.teleportTo(this.aX, this.aY, this.aZ);
			}
		}

		else {
			this.clearInfo();
			this.recastTime = 30;
		}
	}

	public void swingAttack(LivingEntity target) {

		if(this.checkTime(1, 80)) {
			if(this.attackTime == 1) {
				this.getLevel().broadcastEntityEvent(this, (byte) 9);
				this.setNoMove(true);
				this.aX = this.getX();
				this.aY = this.getY();
				this.aZ = this.getZ();
			}

			else if(this.attackTime == 35) {

				List<BelialSword> swordList = this.getEntityList(BelialSword.class, e -> e.getOwner() == this, 64D);
				swordList.forEach(e -> e.discard());

				double x = target.getX() - this.getX();
				double y = target.getY(0.3333333333333333D) - this.getY() - 5D;
				double z = target.getZ() - this.getZ();
				double xz = Math.sqrt(x * x + z * z);

				BelialSword sword = new BelialSword(this.getLevel(), this);
				sword.shoot(x, y - xz * 0.065D, z, 2F, 0F);
				sword.setPos(this.getX(), this.getY() + 6D, this.getZ());
				sword.setAddDamage(sword.getAddDamage() + 60F + this.getBuffPower());
				sword.setHitDead(false);
				sword.setCharge(true);
				this.addEntity(sword);
			}

			else if(this.attackTime == 72) {
				List<BelialSword> swordList = this.getEntityList(BelialSword.class, e -> e.getOwner() == this && e.getCharge(), 64D);
				swordList.get(0).setCharge(false);
				this.playSound(SoundInit.KNIFE_SHOT, 0.5F, 0.9F);
			}

			double d1 = target.getX() - this.getX();
			double d2 = target.getZ() - this.getZ();
			this.setYRot(-((float) Math.atan2(d1, d2)) * (180F / (float) Math.PI));

			if(this.aX !=  this.getX() || this.aY !=  this.getY() || this.aZ !=  this.getZ()) {
				this.teleportTo(this.aX, this.aY, this.aZ);
			}
		}

		else {
			this.clearInfo();
			this.recastTime = 20;
		}
	}

	public void attackDamage(Entity entity, DamageSource src, float damage) {

		if(entity instanceof LivingEntity living) {
			damage = living.hasEffect(PotionInit.belial_flame) ? 1.5F : 1F;
		}

		entity.hurt(src, entity instanceof Warden ? damage * 5F : damage);
		entity.invulnerableTime = 0;
	}

	public float getBuffPower() {
		float baseDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
		baseDamage = this.getHalfHealth() ? baseDamage * 2F : baseDamage;
		return super.getBuffPower() + baseDamage;
	}

	public boolean checkTime(int start, int end) {
		return this.attackTime >= start && this.attackTime < end;
	}

	public float getHeartAnimation(float patTick) {
		return Mth.lerp(patTick, (float) this.heartAnimationO, (float) this.heartAnimation) / 10F;
	}

	// パーティクルスポーン
	public void spawnParticleRing(ServerLevel server, ParticleOptions par, double range, BlockPos pos, double addY) {

		double x = pos.getX() + 0.5D;
		double y = pos.getY() + 1D + addY;
		double z = pos.getZ() + 0.5D;

		for (double degree = -range * Math.PI; degree < range * Math.PI; degree += 0.25D) {
			double rate = range * 0.75D;
			server.sendParticles(par, x + Math.cos(degree) * rate, y, z + Math.sin(degree) * rate, 0, Math.cos(degree) * 0.85D, this.rand.nextFloat() * 0.25F, Math.sin(degree) * 0.85D, 1D);
		}
	}

	@Override
	public boolean isArmorEmpty() {
		return false;
	}

	public void deathEffect() {
		if(this.deathTime == 1) {
			this.recastTime = 12;
			this.getLevel().broadcastEntityEvent(this, (byte) 10);
		}

		if(this.deathTime >= this.getMaxDeathTime() - 10) { return; }

		float rate = ((float) this.deathTime / (float) this.getMaxDeathTime());

		if(this.recastTime-- <= 0) {
			this.playSound(SoundEvents.WARDEN_HEARTBEAT, 3F * (1.25F - rate), 1F);
			this.recastTime = this.deathTime <= 90 ? 12 : 12 + this.deathTime / 10;
		}

		if(this.deathTime % 5 != 0 || !(this.getLevel() instanceof ServerLevel sever)) { return; }

		ParticleOptions par = ParticleInit.DIVINE;
		BlockPos pos = this.blockPosition();

		for (int i = 0; i < 16; i++) {
			float x = (float) pos.getX() + 0.5F + this.getRandFloat(2.25F);
			float y = (float) pos.getY() + 0.5F + this.getRandFloat(1F);
			float z = (float) pos.getZ() + 0.5F + this.getRandFloat(2.25F);
			sever.sendParticles(par, x, y, z, 0, this.getRandFloat(0.3F), 0.25F + this.rand.nextFloat() * 0.5F, this.getRandFloat(0.3F), 1F);
		}
	}

	public int getMaxDeathTime() {
		return 240;
	}

	public void clearInfo() {
		this.setNoMove(false);
		this.attackTime = 0;
		this.isGround = false;
		this.getLevel().broadcastEntityEvent(this, (byte) 4);
	}

	// 範囲内にいるかのチェック
	public boolean checkDistance(BlockPos pos, double range) {
		return this.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= range;
	}

	public Predicate<LivingEntity> getFilter(boolean isPlayer, List<LivingEntity> entityAllList) {
		return e -> !e.isSpectator() && e.isAlive() && !(e instanceof ISMMob) && (isPlayer ? (e instanceof Player || e instanceof AbstractSummonMob) : !(e instanceof Player)) && !entityAllList.contains(e);
	}

	public void die(DamageSource src) {
		super.die(src);
		List<BelialSword> targetList = this.getEntityList(BelialSword.class, 64D);
		targetList.forEach(e -> e.discard());

		List<StellaWizard> wizardList = this.getEntityList(StellaWizard.class, e -> e.getDemons(), 64D);
		wizardList.forEach(e -> e.setMaxLifeTime(e.tickCount));
	}
}
