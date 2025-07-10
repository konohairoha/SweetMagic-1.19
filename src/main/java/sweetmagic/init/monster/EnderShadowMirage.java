package sweetmagic.init.entity.monster;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;

public class EnderShadowMirage extends AbstractOwnerMob {

	private static final EntityDataAccessor<Boolean> SUMMON = ISMMob.setData(EnderShadowMirage.class, EntityDataSerializers.BOOLEAN);

	public EnderShadowMirage(Level world) {
		super(EntityInit.enderShadowMirage, world);
	}

	public EnderShadowMirage(EntityType<EnderShadowMirage> enType, Level world) {
		super(enType, world);
		this.xpReward = 50;
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.define(SUMMON, false);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 50D)
				.add(Attributes.MOVEMENT_SPEED, 0.35D)
				.add(Attributes.ATTACK_DAMAGE, 4D)
				.add(Attributes.ARMOR, 8D)
				.add(Attributes.FOLLOW_RANGE, 32D);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.5D, false));
		this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1D));
		this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1D, 0F));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8F));
		this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Warden.class, 8F));
		this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Warden.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Raider.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {
		Entity attacker = src.getEntity();
		if (attacker != null && attacker instanceof ISMMob) {
			this.teleport();
			return false;
		}

		// ダメージ倍処理
		amount = this.getBossDamageAmount(this.getLevel(), this.defTime , src, amount, 20F);
		this.defTime = 2;
		return super.hurt(src, Math.min(amount, 25F));
	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENDERMAN_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource src) {
		return SoundEvents.ENDERMAN_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENDERMAN_DEATH;
	}

	public boolean doHurtTarget(Entity entity) {
		boolean flag = super.doHurtTarget(entity);

		if (flag && entity instanceof Warden target) {
			entity.hurt(this.getSRC(), 10F);
		}

		entity.invulnerableTime = 0;
		return flag;
	}

	protected void customServerAiStep() {
		super.customServerAiStep();
		LivingEntity target = this.getTarget();
		if (target == null) { return; }

		if(this.tickCount % 20 == 0 && !this.hasLineOfSight(target)) {
			this.teleport(target);
		}
	}

	protected boolean teleport(LivingEntity target) {
		if (!this.isClient() && this.isAlive()) {
			double d0 = target.getX() + (this.rand.nextDouble() - 0.5D) * 5D;
			double d1 = target.getY() + (double) (this.rand.nextInt(8) - 4);
			double d2 = target.getZ() + (this.rand.nextDouble() - 0.5D) * 5D;
			return this.teleport(d0, d1, d2);
		}

		return false;
	}

	// 低ランクかどうか
	public boolean isLowRank() {
		return false;
	}
}
