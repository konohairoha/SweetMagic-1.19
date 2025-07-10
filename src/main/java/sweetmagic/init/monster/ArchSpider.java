package sweetmagic.init.entity.monster;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.PotionInit;

public class ArchSpider extends Spider implements ISMMob {

	public ArchSpider(Level world) {
		super(EntityInit.archSpider, world);
	}

	public ArchSpider(EntityType<? extends Spider> enType, Level world) {
		super(enType, world);
		this.xpReward = 35;
	}

	public void refreshInfo() {
		this.reapplyPosition();
		this.refreshDimensions();
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(1, new FloatGoal(this));
		this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
		this.goalSelector.addGoal(4, new SpiderAttackGoal(this));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8F));
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Warden.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Raider.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 25D)
				.add(Attributes.MOVEMENT_SPEED, 0.3D)
				.add(Attributes.ATTACK_DAMAGE, 3D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 4D)
				.add(Attributes.FOLLOW_RANGE, 24D);
	}

	public SynchedEntityData getData() {
		return this.getEntityData();
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
		amount = this.getDamageAmount(this.getLevel() , src, amount, 1F);
		return super.hurt(src, amount);
	}

	public void tick() {
		super.tick();
		this.fallDistance = 0F;
	}

	public boolean doHurtTarget(Entity target) {
		if (!super.doHurtTarget(target)) { return false; }
		if (!( target instanceof LivingEntity living ) ) { return true; }

		int time = 6;
		int level = 0;

		if (target instanceof Warden) {
			time = 18 * 20;
			level = 4;
		}

		else {
			time *= ((int) 20 * this.getDateRate(this.getLevel(), 0.1F));
		}

		this.addPotion(living, PotionInit.deadly_poison, time, level);
		living.invulnerableTime = 0;
		return true;
	}

	public static class SpiderAttackGoal extends MeleeAttackGoal {

		public SpiderAttackGoal(Spider entity) {
			super(entity, 1D, true);
		}

		public boolean canUse() {
			return super.canUse() && !this.mob.isVehicle();
		}

		public boolean canContinueToUse() {
			float f = this.mob.getLightLevelDependentMagicValue();
			if (f >= 0.5F && this.mob.getRandom().nextInt(100) == 0) {
				this.mob.setTarget((LivingEntity) null);
				return false;
			}

			else {
				return super.canContinueToUse();
			}
		}

		protected double getAttackReachSqr(LivingEntity entity) {
			return (double) (4F + entity.getBbWidth());
		}
	}

	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance dif, MobSpawnType spawn, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		data = super.finalizeSpawn(world, dif, spawn, data, tag);
		this.initMobData(this, dif);
		return data;
	}
}
