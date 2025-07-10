package sweetmagic.init.entity.monster;

import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.ientity.IGolem;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.entity.projectile.ElectricSphere;

public class ElectricGolem extends AbstractSMMob implements IGolem {

	private int recastTime = 0;
	private static final int RAND_RECASTTIME = 40;
	private static final EntityDataAccessor<Integer> ATTACK_TICK = ISMMob.setData(ElectricGolem.class, ISMMob.INT);

	public ElectricGolem(Level world) {
		super(EntityInit.electricGolem, world);
	}

	public ElectricGolem(EntityType<ElectricGolem> enType, Level world) {
		super(enType, world);
		this.xpReward = 200;
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.define(ATTACK_TICK, 0);
	}

	protected void registerGoals() {
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
				.add(Attributes.MAX_HEALTH, 200D)
				.add(Attributes.MOVEMENT_SPEED, 0.2D)
				.add(Attributes.ATTACK_DAMAGE, 5D)
				.add(Attributes.ARMOR, 12D)
				.add(Attributes.FOLLOW_RANGE, 32D);
	}

	protected SoundEvent getHurtSound(DamageSource src) {
		return SoundEvents.IRON_GOLEM_HURT;
	}

	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.IRON_GOLEM_STEP, 1F, 0.5F);
	}

	public void setAttackTick(int attackTick) {
		this.set(ATTACK_TICK, attackTick);
	}

	public int getAttackTick() {
		return this.get(ATTACK_TICK);
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

	public void aiStep() {
		super.aiStep();
		int attackTick = this.getAttackTick();
		if (attackTick > 0) {
			this.setAttackTick(attackTick - 1);
		}
	}

	protected void customServerAiStep() {
		super.customServerAiStep();
		LivingEntity target = this.getTarget();
		if (target == null) { return; }

		this.getLookControl().setLookAt(target, 10F, 10F);
		if (this.recastTime-- > 0) { return; }

		this.setAttackTick(10);
		this.recastTime = this.rand.nextInt(RAND_RECASTTIME) + RAND_RECASTTIME;

		for (int i = 0; i < 5; i++) {
			ElectricSphere entity = new ElectricSphere(this.getLevel(), this);
			double d0 = target.getX() - this.getX();
			double d1 = target.getZ() - this.getZ();
			double d3 = target.getY(0.3333333333333333D) - this.getY() - 1D;
			Vector3f vec = this.getShotVector(this, new Vec3(d0, d3, d1), -30 + i * 15);
			entity.shoot(vec.x(), vec.y(), vec.z(), 1F, 1);
			entity.setAddDamage(entity.getAddDamage() + 15F);
			entity.setHitDead(false);
			entity.setNotDamage(true);
			entity.setRange(3D);
			entity.setCount(2);
			this.addEntity(entity);
		}

		this.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);
	}

	// 低ランクかどうか
	public boolean isLowRank() {
		return false;
	}

	public boolean getShit() {
		return false;
	}
}
