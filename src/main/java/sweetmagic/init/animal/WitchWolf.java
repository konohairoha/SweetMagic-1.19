package sweetmagic.init.entity.animal;

import com.mojang.blaze3d.vertex.PoseStack.Pose;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.api.ientity.IWolf;
import sweetmagic.init.EntityInit;
import sweetmagic.init.PotionInit;
import sweetmagic.util.SMDamage;

public class WitchWolf extends AbstractSummonMob implements IWolf {

	private boolean isWet;
	private boolean isShaking;
	private float shakeAnim;
	private float shakeAnimO;
	private float interestedAngle;
	private float interestedAngleO;
	private int recastTime = 0;
	private static final EntityDataAccessor<Integer> ATTACK_TICK = ISMMob.setData(WitchWolf.class, ISMMob.INT);

	public WitchWolf(Level world) {
		super(EntityInit.witchWolf, world);
	}

	public WitchWolf(EntityType<? extends AbstractSummonMob> eType, Level world) {
		super(eType, world);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.define(ATTACK_TICK, 0);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 20D)
				.add(Attributes.MOVEMENT_SPEED, 0.3D)
				.add(Attributes.ATTACK_DAMAGE, 3D)
				.add(Attributes.ARMOR, 0.1D)
				.add(Attributes.FOLLOW_RANGE, 48D);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(1, new FloatGoal(this));
		this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
		this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
		this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1D, true));
		this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1D, 10F, 2F, false));
		this.goalSelector.addGoal(6, new BreedGoal(this, 1D));
		this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1D));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8F));
		this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new SMOwnerHurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new SMOwnerHurtTargetGoal(this));
		this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setAlertOthers());
		this.targetSelector.addGoal(4, new NearestAttackSMMobGoal<>(this, Monster.class, false));
		this.targetSelector.addGoal(5, new AttackTargetGoal<>(this, Raider.class, false));
		this.targetSelector.addGoal(6, new AttackTargetGoal<>(this, Warden.class, false));
		this.targetSelector.addGoal(7, new AttackTargetGoal<>(this, AbstractSkeleton.class, false));
		this.targetSelector.addGoal(8, new AttackTargetGoal<>(this, Slime.class, false));
	}

	protected SoundEvent getAmbientSound() {
		if (this.rand.nextInt(3) == 0) {
			return this.isTame() && this.getHealth() < 10F ? SoundEvents.WOLF_WHINE : SoundEvents.WOLF_PANT;
		}

		else {
			return SoundEvents.WOLF_AMBIENT;
		}
	}

	protected SoundEvent getHurtSound(DamageSource src) {
		return SoundEvents.WOLF_HURT;
	}

	protected float getSoundVolume() {
		return 0.4F;
	}

	public void setAttackTick(int attackTick) {
		this.set(ATTACK_TICK, attackTick);
	}

	public int getAttackTick() {
		return this.get(ATTACK_TICK);
	}

	public void hurtAction(Entity attacker, float amount) {
		if(this.getGolem()) {

			DamageSource src = SMDamage.getAddDamage(this, this);

			if (attacker instanceof EnderMan || attacker instanceof Witch) {
				src = DamageSource.playerAttack((Player) this.getOwner());
			}

			attacker.hurt(src, Math.min(20F, amount * 0.1F));
			attacker.invulnerableTime = 0;
		}
	}

	public boolean doHurtTarget(Entity entity) {
		this.setAttackTick(this.tickCount);

		if(entity instanceof LivingEntity target) {

			if (this.getMaster()) {
				this.addAttack(target);
				this.addPotion(target, PotionInit.debuff_extension, 600, 0);
			}

			if (this.getIfrit()) {
				this.addAttack(target);
				this.addPotion(target, PotionInit.flame, 600, 0);
			}

			if (this.getWindine()) {
				this.addAttack(target);
				this.addPotion(target, PotionInit.frost, 600, 0);
			}
		}

		return super.doHurtTarget(entity);
	}

	public void addAttack(LivingEntity target) {
		float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
		target.hurt(SMDamage.getAddDamage(this, this), damage) ;
		target.invulnerableTime = 0;
	}

	public void aiStep() {
		super.aiStep();
		if (!this.isClient() && this.isWet && !this.isShaking && !this.isPathFinding() && this.onGround) {
			this.isShaking = true;
			this.shakeAnim = 0F;
			this.shakeAnimO = 0F;
			this.getLevel().broadcastEntityEvent(this, (byte) 8);
		}

		if (!this.isClient() && this.isAlive() && this.tickCount % 20 == 0 && this.getAlay()) {
			this.heal(1F);
		}

		int attackTick = this.getAttackTick();
		if (this.tickCount > attackTick + 10) {
			this.setAttackTick(0);
		}
	}

	public void tick() {
		super.tick();
		if (!this.isAlive()) { return; }

		this.interestedAngleO = this.interestedAngle;

		if (this.isInWaterRainOrBubble()) {
			this.isWet = true;
			if (this.isShaking && !this.isClient()) {
				this.getLevel().broadcastEntityEvent(this, (byte) 56);
			}
		}

		else if ((this.isWet || this.isShaking) && this.isShaking) {

			if (this.shakeAnim == 0F) {
				this.playSound(SoundEvents.WOLF_SHAKE, this.getSoundVolume(), this.getRand(0.2F) + 1F);
				this.gameEvent(GameEvent.ENTITY_SHAKE);
			}

			this.shakeAnimO = this.shakeAnim;
			this.shakeAnim += 0.05F;

			if (this.shakeAnimO >= 2F) {
				this.isWet = false;
				this.isShaking = false;
				this.shakeAnimO = 0F;
				this.shakeAnim = 0F;
			}

			if (this.shakeAnim > 0.4F) {

				float f = (float) this.getY();
				int i = (int) (Mth.sin((this.shakeAnim - 0.4F) * (float) Math.PI) * 7F);
				Vec3 vec3 = this.getDeltaMovement();

				for (int j = 0; j < i; ++j) {
					float f1 = (this.getRand() * 2F - 1F) * this.getBbWidth() * 0.5F;
					float f2 = (this.getRand() * 2F - 1F) * this.getBbWidth() * 0.5F;
					this.getLevel().addParticle(ParticleTypes.SPLASH, this.getX() + (double) f1, (double) (f + 0.8F), this.getZ() + (double) f2, vec3.x, vec3.y, vec3.z);
				}
			}
		}
	}

	protected void customServerAiStep() {
		super.customServerAiStep();

		if (this.recastTime > 0) {
			this.recastTime--;
		}

		LivingEntity target = this.getTarget();
		if (target == null || !target.isAlive() || this.recastTime > 0 || this.getShit()) { return; }

		if (this.distanceTo(target) > 4D && this.teleportTowards(target)) {
			this.recastTime += 2400;
		}
	}

	public boolean teleportTowards(Entity entity) {
		Vec3 vec3 = new Vec3(this.getX() - entity.getX(), this.getY(0.5D) - entity.getEyeY(), this.getZ() - entity.getZ());
		vec3 = vec3.normalize();
		double d1 = entity.getX() + this.getRand(4F);
		double d2 = entity.getY() + this.getRand(4F);
		double d3 = entity.getZ() + this.getRand(4F);
		return this.teleport(d1, d2, d3);
	}

	private boolean teleport(double x, double y, double z) {

		BlockPos.MutableBlockPos muPos = new BlockPos.MutableBlockPos(x, y, z);

		while (muPos.getY() > this.getLevel().getMinBuildHeight() && !this.getLevel().getBlockState(muPos).getMaterial().blocksMotion()) {
			muPos.move(Direction.DOWN);
		}

		BlockState state = this.getLevel().getBlockState(muPos);
		boolean flag = state.getMaterial().blocksMotion();
		boolean flag1 = state.getFluidState().is(FluidTags.WATER);

		if (flag && !flag1) {

			EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport(this, x, y, z);
			if (event.isCanceled()) { return false; }

			Vec3 vec3 = this.position();
			boolean flag2 = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);

			if (flag2) {
				this.getLevel().gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(this));

				if (!this.isSilent()) {
					this.getLevel().playSound(null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1F, 1F);
					this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1F, 1F);
				}
			}

			return flag2;
		}

		return false;
	}

	public void die(DamageSource src) {
		this.isWet = false;
		this.isShaking = false;
		this.shakeAnimO = 0F;
		this.shakeAnim = 0F;
		super.die(src);
	}

	public boolean isWet() {
		return this.isWet;
	}

	public float getWetShade(float par1) {
		return Math.min(0.5F + Mth.lerp(par1, this.shakeAnimO, this.shakeAnim) / 2F * 0.5F, 1F);
	}

	public float getBodyRollAngle(float par1, float par2) {

		float f = (Mth.lerp(par1, this.shakeAnimO, this.shakeAnim) + par2) / 1.8F;

		if (f < 0F) {
			f = 0F;
		}

		else if (f > 1F) {
			f = 1F;
		}

		return Mth.sin(f * (float) Math.PI) * Mth.sin(f * (float) Math.PI * 11F) * 0.15F * (float) Math.PI;
	}

	public float getHeadRollAngle(float angle) {
		return Mth.lerp(angle, this.interestedAngleO, this.interestedAngle) * 0.15F * (float) Math.PI;
	}

	protected float getStandingEyeHeight(Pose pose, EntityDimensions dim) {
		return dim.height * 0.8F;
	}

	public int getMaxHeadXRot() {
		return this.isInSittingPose() ? 20 : super.getMaxHeadXRot();
	}
}
