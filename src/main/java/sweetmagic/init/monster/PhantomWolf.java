package sweetmagic.init.entity.monster;

import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.util.SMDamage;
import sweetmagic.util.WorldHelper;

public class PhantomWolf extends AbstractSMMob {

	public PhantomWolf(Level world) {
		super(EntityInit.phantomWolf, world);
	}

	public PhantomWolf(EntityType<PhantomWolf> enType, Level world) {
		super(enType, world);
		this.xpReward = 200;
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

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 120D)
				.add(Attributes.MOVEMENT_SPEED, 0.3D)
				.add(Attributes.ATTACK_DAMAGE, 7D)
				.add(Attributes.FOLLOW_RANGE, 32D);
	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.WOLF_GROWL;
	}

	public float getVoicePitch() {
		return 0.8F;
	}

	protected float getSoundVolume() {
		return 0.5F;
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {
		Entity attacker = src.getEntity();
		Entity attackEntity = src.getDirectEntity();
		if (attacker != null && attacker instanceof ISMMob) { return false; }

		// 魔法攻撃以外ならダメージ減少
		if (this.notMagicDamage(attacker, attackEntity)) {
			amount *= 0.25F;
		}

		// ダメージ倍処理
		if (!this.isLeader(this)) {
			amount = this.getBossDamageAmount(this.getLevel(), this.defTime , src, amount, 12F);
			this.defTime = 1;
		}

		return super.hurt(src, amount);
	}

	public boolean doHurtTarget(Entity entity) {
		if (!(entity instanceof LivingEntity target)) { return true; }

		double range = 3F;
		float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) + 3F;
		Vec3 vec3 = new Vec3(this.getX() - entity.getX(), 0.2D, this.getZ() - entity.getZ()).scale(2D);
		this.setDeltaMovement(this.getDeltaMovement().add(vec3));
		List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), 4D);

		for (LivingEntity living : targetList) {

			float newDamage = damage;

			if (!living.hasEffect(PotionInit.deadly_poison)) {
				int time = 100;
				int level = 1;
				this.addPotion(target, PotionInit.deadly_poison, time, level);
			}

			else {
				newDamage += 4F;
			}

			if (entity instanceof Warden) { newDamage *= 3F; }

			living.hurt(SMDamage.magicDamage, newDamage);
			living.invulnerableTime = 0;
		}

		if (this.getLevel() instanceof ServerLevel server) {

			// 範囲の座標取得
			Random rand = this.rand;
			BlockPos bPos = this.blockPosition();
			double effectRange = range * range;
			Iterable<BlockPos> pList = WorldHelper.getRangePos(bPos, -range, 0, -range, range, 0, range);

			for (BlockPos pos : pList) {
				if(!this.checkDistance(pos, effectRange)) { continue; }
				double x = pos.getX() + rand.nextDouble() * 1.5D - 0.75D;
				double y = pos.getY() + rand.nextDouble() * 1.5D - 0.75D;
				double z = pos.getZ() + rand.nextDouble() * 1.5D - 0.75D;
				server.sendParticles(ParticleInit.SMOKY, x, y, z, 0, 67F / 255F, 173F / 255F, 103F / 255F, 1F);
			}
		}

		return true;
	}

	// 範囲内にいるかのチェック
	public boolean checkDistance(BlockPos pos, double range) {
		return this.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= range;
	}

	// 低ランクかどうか
	public boolean isLowRank() {
		return false;
	}
}
