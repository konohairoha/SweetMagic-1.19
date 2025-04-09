package sweetmagic.init.entity.animal;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack.Pose;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.GolemRandomStrollInVillageGoal;
import net.minecraft.world.entity.ai.goal.JumpGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.entity.projectile.CherryMagicShot;
import sweetmagic.init.entity.projectile.SoulBlazeShot;
import sweetmagic.util.PlayerHelper;
import sweetmagic.util.SMDamage;

public class WitchFox extends AbstractSummonMob {

	private int recastTime = 100;
	protected int magicTime = 150;
	protected int fireTime = 200;
	private float crouchAmount;
	private float crouchAmountO;
	private float interestedAngle;
	private float interestedAngleO;
	private static final EntityDataAccessor<Boolean> IS_POUNCE = ISMMob.setData(WitchFox.class, ISMMob.BOOLEAN);
	private static final EntityDataAccessor<Boolean> IS_FACEPLANT = ISMMob.setData(WitchFox.class, ISMMob.BOOLEAN);
	private static final EntityDataAccessor<Boolean> IS_CROUCH = ISMMob.setData(WitchFox.class, ISMMob.BOOLEAN);
	private static final EntityDataAccessor<Boolean> IS_INTEREST = ISMMob.setData(WitchFox.class, ISMMob.BOOLEAN);
	private static final EntityDataAccessor<Integer> FIRE_COUNT = ISMMob.setData(WitchFox.class, ISMMob.INT);

	public WitchFox(Level world) {
		this(EntityInit.witchFox, world);
	}

	public WitchFox(EntityType<? extends AbstractSummonMob> eType, Level world) {
		super(eType, world);
		this.moveControl = new FoxMoveControl();
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(IS_POUNCE, false);
		this.entityData.define(IS_FACEPLANT, false);
		this.entityData.define(IS_CROUCH, false);
		this.entityData.define(IS_INTEREST, false);
		this.entityData.define(FIRE_COUNT, 0);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 50D)
				.add(Attributes.MOVEMENT_SPEED, 0.3D)
				.add(Attributes.ATTACK_DAMAGE, 10D)
				.add(Attributes.ARMOR, 0.25D)
				.add(Attributes.FOLLOW_RANGE, 48D);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
		this.goalSelector.addGoal(3, new FoxPounceGoal(this));
		this.goalSelector.addGoal(4, new GolemRandomStrollInVillageGoal(this, 0.6D));
		this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1D, 10F, 2F, false) {
			public boolean canUse() {
				return !getFacePlant() && !getPounce() && super.canUse();
			}
		});
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Monster.class, 8F));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8F));
		this.targetSelector.addGoal(1, new SMOwnerHurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new SMOwnerHurtTargetGoal(this));
		this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setAlertOthers());
		this.targetSelector.addGoal(4, new NearestAttackSMMobGoal<>(this, Monster.class, false));
		this.targetSelector.addGoal(5, new AttackTargetGoal<>(this, Raider.class, false));
		this.targetSelector.addGoal(6, new AttackTargetGoal<>(this, Warden.class, false));
	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.FOX_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource src) {
		return SoundEvents.FOX_HURT;
	}

	protected float getSoundVolume() {
		return 0.4F;
	}

	public boolean getPounce() {
		return this.entityData.get(IS_POUNCE);
	}

	public void setPounce(boolean pounce) {
		this.entityData.set(IS_POUNCE, pounce);
	}

	public boolean getFacePlant() {
		return this.entityData.get(IS_FACEPLANT);
	}

	void setFacePlant(boolean facePlant) {
		this.entityData.set(IS_FACEPLANT, facePlant);
	}

	public boolean isCrouching() {
		return this.entityData.get(IS_CROUCH);
	}

	public void setCrouch(boolean crouch) {
		this.entityData.set(IS_CROUCH, crouch);
	}

	public boolean isInterest() {
		return this.entityData.get(IS_INTEREST);
	}

	public void setIsInterest(boolean interest) {
		this.entityData.set(IS_INTEREST, interest);
	}

	public int getFireCount() {
		return this.entityData.get(FIRE_COUNT);
	}

	public void setFireCount(int fireCount) {
		this.entityData.set(FIRE_COUNT, fireCount);
	}

	public boolean isFullyCrouched() {
		return this.crouchAmount == 3F;
	}

	public float getCrouchAmount(float par1) {
		return Mth.lerp(par1, this.crouchAmountO, this.crouchAmount);
	}

	public float getHeadRollAngle(float angle) {
		return Mth.lerp(angle, this.interestedAngleO, this.interestedAngle) * 0.11F * (float) Math.PI;
	}

	public boolean isJumping() {
		return this.jumping;
	}

	public boolean canMove() {
		return !this.getShit() && !this.getFacePlant() && !this.getPounce();
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putInt("fireCount", this.getFireCount());
		tags.putInt("recastTime", this.recastTime);
		tags.putInt("magicTime", this.magicTime);
		tags.putInt("fireTime", this.fireTime);
		tags.putBoolean("Pounce", this.getPounce());
		tags.putBoolean("FacePlant", this.getFacePlant());
		tags.putBoolean("Crouching", this.isCrouching());
		tags.putBoolean("Interest", this.isInterest());
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setFireCount(tags.getInt("fireCount"));
		this.recastTime = tags.getInt("recastTime");
		this.magicTime = tags.getInt("magicTime");
		this.fireTime = tags.getInt("fireCount");
		this.setPounce(tags.getBoolean("Pounce"));
		this.setFacePlant(tags.getBoolean("FacePlant"));
		this.setCrouch(tags.getBoolean("Crouching"));
		this.setIsInterest(tags.getBoolean("Interest"));
	}

	public void tick() {
		super.tick();
		if (!this.isAlive()) { return; }

		this.interestedAngleO = this.interestedAngle;
		this.interestedAngle += this.isInterest() ? (1F - this.interestedAngle) * 0.4F : (0F - this.interestedAngle) * 0.4F;

		this.crouchAmountO = this.crouchAmount;
		if (this.isCrouching()) {
			this.crouchAmount += 0.2F;
			if (this.crouchAmount > 3F) {
				this.crouchAmount = 3F;
			}
		}

		else {
			this.crouchAmount = 0F;
		}

		if (this.tickCount % 20 == 0 && this.level.isClientSide) {
			int count = Math.min(10, this.getFireCount());
			for (int i = 0; i < count; i++) {
				float f1 = (float) (this.getX() + this.getRand(1.5F));
				float f2 = (float) (this.getY() + 0.25F + this.getRand());
				float f3 = (float) (this.getZ() + this.getRand(1.5F));
				float x = this.getRand(0.025F);
				float y = this.getRand(0.025F);
				float z = this.getRand(0.025F);
				this.level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, f1, f2, f3, x, y, z);
			}
		}

		if (this.tickCount % 10 != 0 || this.level.isClientSide || this.getFireCount() >= 16) { return; }

		// 死んでいるえんちちーが居なければ終了
		List<Mob> entityList = this.getEntityList(Mob.class, 24D).stream().filter(t -> !t.isAlive() && !t.getPersistentData().contains("dead_" + this.getUUID())).toList();

		if (!entityList.isEmpty()) {
			this.setFireCount(Math.min(16, this.getFireCount() + entityList.size()));
			entityList.forEach(e -> e.getPersistentData().putBoolean("dead_" + this.getUUID(), true));
		}

		if (this.getFireCount() > 0 && this.getHealth() <= this.getMaxHealth() * 0.5F) {
			int count = Math.min(5, this.getFireCount());
			this.heal(count * 0.1F * this.getMaxHealth());
			this.setFireCount(this.getFireCount() - count);
		}
	}

	protected void customServerAiStep() {
		super.customServerAiStep();
		LivingEntity target = this.getTarget();

		if (target == null || !target.isAlive()) {
			this.setCrouch(false);
			this.setIsInterest(false);
			this.setTarget(null);
			return;
		}

		this.getLookControl().setLookAt(target, 10F, 10F);
		if (this.getShit() || this.getPounce()) { return; }

		// 桜攻撃
		if (this.magicTime-- <= 0) {
			this.magicShot(target, this.cherryShot());
		}

		// 狐火攻撃
		if (this.getFireCount() > 0) {
			if (this.fireTime-- <= 0) {
				this.magicShot(target, this.fireShot());
			}
		}
	}

	public void magicShot (LivingEntity target, AbstractMagicShot entity) {

		boolean isWarden = target instanceof Warden;
		float dama = this.getPower(this.getWandLevel()) + (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.35F;
		float dameRate = isWarden ? 4F : 1;

		double x = target.getX() - this.getX();
		double y = target.getY(0.3333333333333333D) - this.getY();
		double z = target.getZ() - this.getZ();
		double xz = Math.sqrt(x * x + z * z);
		int level = isWarden ? this.getWandLevel() + 20 : this.getWandLevel();
		if (!isWarden && this.isBoss(target)) { dama /= 5; }

		entity.setWandLevel(level);
		entity.shoot(x, y - xz * 0.035D, z, 2.25F, 0F);
		entity.setAddDamage( (entity.getAddDamage() + dama) * dameRate );
		this.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);
		this.level.addFreshEntity(entity);
	}

	public AbstractMagicShot cherryShot () {
		this.magicTime = 125 + this.rand.nextInt(75);
		AbstractMagicShot entity = new CherryMagicShot(this.level, this);
		entity.setData(2);
		entity.setRange(2.5D + this.getRange());
		return entity;
	}

	public AbstractMagicShot fireShot () {
		this.fireTime = 225 + this.rand.nextInt(75);
		AbstractMagicShot entity = new SoulBlazeShot(this.level, this);
		entity.setRange(6.5D + this.getRange());
		entity.setAddAttack(this.getFireCount());
		return entity;
	}

	protected float getStandingEyeHeight(Pose pose, EntityDimensions dim) {
		return dim.height * 0.8F;
	}

	public int getMaxHeadXRot() {
		return this.isInSittingPose() ? 20 : super.getMaxHeadXRot();
	}

	public class FoxPounceGoal extends JumpGoal {

		private final WitchFox fox;
		private BlockPos pos;

		public FoxPounceGoal(WitchFox fox) {
			this.fox = fox;
		}

		public boolean canUse() {
			LivingEntity target = this.fox.getTarget();
			if (target != null && target.isAlive()) {

				if (this.fox.recastTime <= 60 && !this.fox.getShit()) {
					this.fox.setCrouch(true);
					this.fox.setPounce(true);
					this.fox.setIsInterest(false);
				}
				return this.fox.recastTime-- <= 1;
			}

			return false;
		}

		public boolean isInterruptable() {
			return false;
		}

		public void start() {
			this.fox.setJumping(true);
			LivingEntity target = this.fox.getTarget();
			if (target != null) {
				this.pos = this.fox.blockPosition();
				this.fox.teleportTo(target.getX(), target.getY() + 6D, target.getZ());
			}

			this.fox.getNavigation().stop();
		}

		public void stop() {
			this.fox.recastTime = 120 + this.fox.rand.nextInt(75);
			this.fox.setCrouch(false);
			this.fox.crouchAmount = 0F;
			this.fox.crouchAmountO = 0F;
			this.fox.setIsInterest(false);
			this.fox.setPounce(false);
		}

		public void tick() {

			if (!this.fox.getFacePlant()) {

				Vec3 vec3 = this.fox.getDeltaMovement();
				if (vec3.y * vec3.y < (double) 0.03F && this.fox.getXRot() != 0F) {
					this.fox.setXRot(Mth.rotlerp(this.fox.getXRot(), 0F, 0.2F));
				}

				else {
					double d0 = vec3.horizontalDistance();
					double d1 = Math.signum(-vec3.y) * Math.acos(d0 / vec3.length()) * (double) (180F / (float) Math.PI);
					this.fox.setXRot((float) d1);
				}
			}

			LivingEntity target = this.fox.getTarget();
			if ((target != null && this.fox.isOnGround() && this.fox.recastTime < 0) || this.fox.distanceTo(target) <= 2F) {
				this.targetAttack(target);
				this.fox.teleportTo(this.pos.getX() + 0.5D, this.pos.getY() + 1.5D, this.pos.getZ() + 0.5D);
			}
		}

		public void targetAttack (LivingEntity target) {
			this.stop();
			double range = 2D + this.fox.getRange();
			float damage = this.fox.getPower(this.fox.getWandLevel()) + (float) this.fox.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.67F;

			List<Mob> entityList = this.fox.getEntityList(Mob.class, this.fox.isTarget(), range);
			entityList.forEach(e -> this.attackDamage(e, this.fox, this.fox, damage));
			this.fox.playSound(SoundEvents.FIRECHARGE_USE, 0.5F, 1F);
			if (!(this.fox.level instanceof ServerLevel server)) { return; }

			double ySpeed = -0.25D;
			double yRate = -0.6D;
			double inRate = 0.25D;
			BlockPos pos = this.fox.blockPosition();

			for (int i = 0; i < 4; i++) {
				this.spawnParticleRing(server, ParticleInit.GRAVITY, range * (1 - 0.14D * i), pos.above(i + 1), ySpeed + i * yRate, inRate);
			}
		}

		// パーティクルスポーン
		protected void spawnParticleRing(ServerLevel server, ParticleOptions particle, double range, BlockPos pos, double ySpeed, double moveValue) {

			double x = pos.getX() + 0.5D;
			double y = pos.getY() + 0.5D;
			double z = pos.getZ() + 0.5D;

			for (double degree = 0D; degree < range * Math.PI; degree += 0.1D) {
				double rate = range;
				server.sendParticles(particle, x + Math.cos(degree) * rate, y, z + Math.sin(degree) * rate, 0, -Math.cos(degree), ySpeed, -Math.sin(degree), moveValue);
			}
		}

		public void attackDamage (Mob target, Entity attacker, Entity magic, float damage) {

			target.invulnerableTime = 0;

			// エンダーマン以外ならターゲットに攻撃
			if (!(target instanceof EnderMan) && !(target instanceof Witch)) {
				target.hurt(SMDamage.getMagicDamage(magic, attacker), target instanceof Warden ? damage * 5F : damage);
			}

			// エンダーマンの場合
			else {

				if (attacker instanceof Player payer) {
					target.hurt(DamageSource.playerAttack(payer), damage);
				}
			}

			target.invulnerableTime = 0;
			PlayerHelper.setPotion(target, PotionInit.gravity, 3, 100);
			Vec3 vec3 = (new Vec3(target.getX() - this.fox.getX(), (target.getY() - this.fox.getY()) * 1D, target.getZ() - this.fox.getZ())).scale(-0.25D);
			target.setDeltaMovement(target.getDeltaMovement().add(vec3));
			target.fallDistance += 0.5D;
		}
	}

	public boolean isPathClear(WitchFox fox, LivingEntity target) {
		double d0 = target.getZ() - fox.getZ();
		double d1 = target.getX() - fox.getX();
		double d2 = d0 / d1;

		for (int i = 0; i < 6; ++i) {
			double d3 = d2 == 0D ? 0D : d0 * (double) ((float) i / 6F);
			double d4 = d2 == 0D ? d1 * (double) ((float) i / 6F) : d3 / d2;

			for (int k = 1; k < 4; ++k) {
				if (!fox.level.getBlockState(new BlockPos(fox.getX() + d4, fox.getY() + (double) k, fox.getZ() + d3)).getMaterial().isReplaceable()) { return false;}
			}
		}

		return true;
	}

	public class FoxMoveControl extends MoveControl {
		public FoxMoveControl() {
			super(WitchFox.this);
		}

		public void tick() {
			if (WitchFox.this.canMove()) {
				super.tick();
			}
		}
	}
}
