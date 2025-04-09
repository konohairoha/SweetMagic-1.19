package sweetmagic.init.entity.animal;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.GolemRandomStrollInVillageGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.ientity.IGolem;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.PotionInit;
import sweetmagic.util.PlayerHelper;
import sweetmagic.util.SMDamage;

public class WitchGolem extends AbstractSummonMob implements IGolem {

	private static final EntityDataAccessor<Integer> ATTACK_TICK = ISMMob.setData(WitchGolem.class, ISMMob.INT);

	public WitchGolem(Level world) {
		super(EntityInit.witchGolem, world);
	}

	public WitchGolem(EntityType<? extends AbstractSummonMob> eType, Level world) {
		super(eType, world);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ATTACK_TICK, 0);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 50D)
				.add(Attributes.MOVEMENT_SPEED, 0.15D)
				.add(Attributes.ATTACK_DAMAGE, 8D)
				.add(Attributes.ARMOR, 0.2D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 1D)
				.add(Attributes.FOLLOW_RANGE, 48D);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
		this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
		this.goalSelector.addGoal(3, new GolemRandomStrollInVillageGoal(this, 0.6D));
		this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1D, 10F, 2F, false));
		this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new SMOwnerHurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new SMOwnerHurtTargetGoal(this));
		this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setAlertOthers());
		this.targetSelector.addGoal(4, new NearestAttackSMMobGoal<>(this, Monster.class, false));
		this.targetSelector.addGoal(5, new AttackTargetGoal<>(this, Raider.class, false));
		this.targetSelector.addGoal(6, new AttackTargetGoal<>(this, Warden.class, false));
	}

	protected SoundEvent getHurtSound(DamageSource src) {
		return SoundEvents.IRON_GOLEM_HURT;
	}

	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.IRON_GOLEM_STEP, 1F, 0.5F);
	}

	public void setAttackTick(int attackTick) {
		this.entityData.set(ATTACK_TICK, attackTick);
	}

	public int getAttackTick() {
		return this.entityData.get(ATTACK_TICK);
	}

	public boolean canAttackType(EntityType<?> eType) {
		return eType == EntityType.PLAYER || eType == EntityType.CREEPER ? false : super.canAttackType(eType);
	}

	public void hurtAction(Entity attacker, float amount) {
		if (amount < 2F) { return; }

		DamageSource src = SMDamage.getAddDamage(this, this);

		if (attacker instanceof EnderMan || attacker instanceof Witch) {
			src = DamageSource.playerAttack((Player) this.getOwner());
		}

		int effecsize = PlayerHelper.getEffectList(this, PotionInit.BUFF).size();
		attacker.hurt(src, amount * (0.1F + 0.15F * effecsize));
		attacker.invulnerableTime = 0;

		if (effecsize > 0) {
			this.heal(amount * 0.01F * effecsize);
		}
	}

	public boolean doHurtTarget(Entity entity) {

		this.setAttackTick(10);
		DamageSource src = SMDamage.getAddDamage(this, this);
		float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.isTarget(), 3.5D + this.getRange());
		boolean flag1 = false;

		for (LivingEntity target : entityList) {

			float dame = damage;

			if (target instanceof Warden) {
				dame *= 4F;
			}

			else if (target instanceof EnderMan || target instanceof Witch) {
				src = DamageSource.playerAttack((Player) this.getOwner());
			}

			else if (this.isBoss(target)) {
				dame *= 0.25F;
			}

			boolean flag2 = target.hurt(src, dame);
			target.invulnerableTime = 0;

			if (flag2) {
				this.doEnchantDamageEffects(this, target);
				flag1 = flag2;
			}
		}

		if (this.level instanceof ServerLevel server) {

			BlockPos pos = this.blockPosition();
			BlockState state = this.level.getBlockState(pos.below());
			ParticleOptions par = new BlockParticleOption(ParticleTypes.BLOCK, state);

			for (int i = 0; i < 4; i++) {
				BlockPos pos2 = new BlockPos(pos.getX(), pos.getY() - 2D + 0.25D + i, pos.getZ());
				this.spawnParticleRing(server, par, 1.75D, pos2, 1D, 0.1D, 0D);
				this.spawnParticleRing(server, par, 4D, pos2, 1D, 0.1D, 0D);
			}
		}

		return flag1;
	}

	public void tick() {
		super.tick();
		if ((this.tickCount % 20 != 0 && !this.isAlive()) || this.getShit()) { return; }

		List<Monster> entityList = this.getEntityList(Monster.class, this.isTarget(), 32D + this.getRange());

		for (Monster entity : entityList) {
			if (!(entity.getTarget() instanceof Player)) { continue; }
			entity.setTarget(this);
			entity.setLastHurtByMob(this);
		}
	}

	public void aiStep() {
		super.aiStep();

		int attackTick = this.getAttackTick();
		if (attackTick > 0) {
			this.setAttackTick(attackTick - 1);
		}

		if (this.getDeltaMovement().horizontalDistanceSqr() > (double) 2.5000003E-7F && this.rand.nextInt(5) == 0) {

			int x = Mth.floor(this.getX());
			int y = Mth.floor(this.getY() - (double) 0.2F);
			int k = Mth.floor(this.getZ());

			BlockPos pos = new BlockPos(x, y, k);
			BlockState state = this.level.getBlockState(pos);

			if (!state.isAir()) {
				ParticleOptions par = new BlockParticleOption(ParticleTypes.BLOCK, state).setPos(pos);
				double xP = this.getX() + ((double) this.getRand() - 0.5D) * (double) this.getBbWidth();
				double yP = this.getY() + 0.1D;
				double zP = this.getZ() + ((double) this.getRand() - 0.5D) * (double) this.getBbWidth();

				double xSpeed = 4D * ((double) this.getRand() - 0.5D);
				double zSpeed = 4D * ((double) this.getRand() - 0.5D);
				this.level.addParticle(par, xP, yP, zP, xSpeed, 0.5D, zSpeed);
			}
		}
	}

	public Vec3 getLeashOffset() {
		return new Vec3(0D, (double) (0.875F * this.getEyeHeight()), (double) (this.getBbWidth() * 0.4F));
	}
}
