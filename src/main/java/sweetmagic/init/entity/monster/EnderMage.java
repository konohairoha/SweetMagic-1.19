package sweetmagic.init.entity.monster;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.entity.projectile.EnderBall;
import sweetmagic.util.PlayerHelper;

public class EnderMage extends AbstractSMMob {

	private int tickTime = 0, coolTime = 0;

	public EnderMage(Level world) {
		super(EntityInit.enderMage, world);
	}

	public EnderMage(EntityType<EnderMage> enType, Level world) {
		super(enType, world);
		this.xpReward = 35;
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 30D)
				.add(Attributes.MOVEMENT_SPEED, 0.33D)
				.add(Attributes.ATTACK_DAMAGE, 4D)
				.add(Attributes.ARMOR, 4D)
				.add(Attributes.FOLLOW_RANGE, 24D);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(1, new EnderAttack(this));
		this.goalSelector.addGoal(2, new RestrictSunGoal(this));
		this.goalSelector.addGoal(3, new FleeSunGoal(this, 1D));
		this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Wolf.class, 6F, 1D, 1.2D));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1D));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8F));
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Warden.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Raider.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	public EntityDimensions getDimensions(Pose pose) {
		float rate = 1F + 0.375F * this.getPotionLevel(this, PotionInit.leader_flag);
		return super.getDimensions(pose).scale(rate);
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {
		Entity attacker = src.getEntity();
		if (attacker != null && attacker instanceof ISMMob) { return false; }

		// ダメージ倍処理
		amount = this.getDamageAmount(this.getLevel(), src, amount, 1F);
		Entity attackEntity = src.getDirectEntity();

		if (!this.isSMDamage(src) || (attackEntity instanceof AbstractMagicShot magic && !(magic.getOwner() instanceof Player))) {
			if (this.getRandom().nextBoolean()) {
				this.teleport();
			}
		}

		return super.hurt(src, amount);
	}

	public void aiStep() {
		super.aiStep();
		if (this.tickTime++ < 20 || this.isClient() || this.getTarget() == null) { return; }

		this.tickTime = 0;

		if (this.coolTime > 0) {
			this.coolTime--;
			return;
		}

		// えんちちーが見つからなかったら終了
		List<Monster> entityList = this.getEntityList(Monster.class, this, e -> e.isAlive() && !(e instanceof EnderMage) && e instanceof ISMMob, 7.5D);

		if (entityList.isEmpty()) {

			if (this.getTarget() == null) {
				this.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1F, 1.175F);
				this.addPotion(this, MobEffects.DAMAGE_BOOST, 400, 1);
			}

			return;
		}

		for (Monster entity : entityList) {
			this.addPotion(entity, MobEffects.DAMAGE_BOOST, 400, 0);

			try {
				PlayerHelper.getEffectList(entity, PotionInit.DEBUFF).forEach(p -> entity.removeEffect(p.getEffect()));
			}

			catch (Throwable e) { }
		}

		// クールタイムの設定
		this.coolTime = 30;
		this.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1F, 1.175F);
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

	private static class EnderAttack extends Goal {

		private final EnderMage ender;
		private int attackStep;
		private int attackTime;
		private int lastSeen;

		public EnderAttack(EnderMage ender) {
			this.ender = ender;
			this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		}

		public boolean canUse() {
			LivingEntity entity = this.ender.getTarget();
			return entity != null && entity.isAlive() && this.ender.canAttack(entity);
		}

		public void start() {
			this.attackStep = 0;
		}

		public void stop() {
			this.lastSeen = 0;
		}

		public boolean requiresUpdateEveryTick() {
			return true;
		}

		public void tick() {
			--this.attackTime;
			LivingEntity target = this.ender.getTarget();
			if (target == null) { return;}

			boolean flag = this.ender.getSensing().hasLineOfSight(target);
			this.lastSeen = flag ? 0 : ++this.lastSeen;
			double d0 = this.ender.distanceToSqr(target);

			if (d0 < 4D) {

				if (!flag) { return; }

				if (this.attackTime <= 0) {
					this.attackTime = 20;
					this.ender.doHurtTarget(target);
				}

				this.ender.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1D);
			}

			else if (d0 < this.getFollowDistance() * this.getFollowDistance() && flag) {

				Level world = this.ender.getLevel();
				double x = target.getX() - this.ender.getX();
				double y = target.getY(0.3333333333333333D) - this.ender.getY();
				double z = target.getZ() - this.ender.getZ();
				double xz = Math.sqrt(x * x + z * z);

				if (this.attackTime <= 0) {

					++this.attackStep;
					if (this.attackStep == 1) {
						this.attackTime = 40;
					}

					else if (this.attackStep <= 4) {
						this.attackTime = 13;
					}

					else {
						this.attackTime = 130;
						this.attackStep = 0;
					}

					if (this.attackStep > 1) {

						if (!this.ender.isSilent()) {
							world.levelEvent((Player) null, 1018, this.ender.blockPosition(), 0);
						}

						boolean isWarden = target instanceof Warden;
						float shotSpeed = isWarden ? 5F : 1F;
						float damage = isWarden ? 16F : 0F;

						if (isWarden || this.ender.isHard(world)) {
							shotSpeed += 0.5F;
							damage += 2F;
						}

						EnderBall entity = new EnderBall(world, this.ender);
						entity.shoot(x, y - xz * 0.065D, z, shotSpeed, 2F);
						entity.setAddDamage(entity.getAddDamage() + damage);
						entity.isTeleport = !this.ender.hasEffect(PotionInit.resistance_blow);
						this.ender.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);
						world.addFreshEntity(entity);
					}
				}

				this.ender.getLookControl().setLookAt(target, 10F, 10F);
			}

			else if (this.lastSeen < 5) {
				this.ender.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1D);
			}

			super.tick();
		}

		private double getFollowDistance() {
			return this.ender.getAttributeValue(Attributes.FOLLOW_RANGE);
		}
	}
}
