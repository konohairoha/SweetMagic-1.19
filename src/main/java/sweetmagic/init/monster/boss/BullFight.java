package sweetmagic.init.entity.monster.boss;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
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
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.animal.AbstractSummonMob;
import sweetmagic.util.SMDamage;

public class BullFight extends AbstractSMBoss {

	private final List<LivingEntity> targetList = new ArrayList<>();
	private static final EntityDataAccessor<Integer> ATTACK = ISMMob.setData(BullFight.class, INT);
	private int rushAttackChargeTime = 0;						// 突進攻撃のチャージ時間
	private static final int RUSHATTACKCHARGEMAXTIME = 60;	// 突進攻撃の最大チャージ時間
	private int pressAttackChargeTime = 0;						// 地面プレス攻撃のチャージ時間
	private static final int PRESSATTACKCHARGEMAXTIME = 70;	// 地面プレス攻撃の最大チャージ時間

	public BullFight(Level world) {
		super(EntityInit.bullfight, world);
	}

	public BullFight(EntityType<? extends AbstractSMBoss> enType, Level world) {
		super(enType, world);
		this.xpReward = 350;
		this.maxUpStep = 1.25F;
		this.setBossEvent(BC_BLUE, NOTCHED_6);
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

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 256D)
				.add(Attributes.MOVEMENT_SPEED, 0.5D)
				.add(Attributes.ATTACK_DAMAGE, 6D)
				.add(Attributes.ARMOR, 8D)
				.add(Attributes.FOLLOW_RANGE, 64D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 92D);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.define(ATTACK, 0);
	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.COW_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource src) {
		return SoundEvents.COW_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.COW_DEATH;
	}

	public float getVoicePitch() {
		return 1.25F;
	}

	protected float getSoundVolume() {
		return 0.4F;
	}

	public int getAttackType() {
		return this.get(ATTACK);
	}

	public void setAttackType(int attack) {
		this.set(ATTACK, attack);
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {
		Entity attacker = src.getEntity();
		Entity attackEntity = src.getDirectEntity();
		if (attacker != null && attacker instanceof ISMMob) { return false; }

		// ボスダメージ計算
		amount = this.getBossDamageAmount(this.getLevel(), this.defTime , src, amount, 10F);
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

	protected void customServerAiStep() {
		super.customServerAiStep();
		LivingEntity target = this.getTarget();
		if (target == null) { return; }

		if (this.hasEffect(PotionInit.flame) && this.hasEffect(PotionInit.frost)) {
			this.removeEffect(PotionInit.flame);
		}

		this.getLookControl().setLookAt(target, 10F, 10F);

		// 地面プレス
		if (this.getAttackType() == 0 && !this.isHalfHealth(this)) {
			this.groundPress(target);
		}

		// 突進攻撃
		else {
			this.rushAttack(target);
		}
	}

	// 地面プレス
	public void groundPress(LivingEntity target) {
		if (this.pressAttackChargeTime++ < PRESSATTACKCHARGEMAXTIME || this.tickCount % 3 != 0) { return; }

		boolean isPlayer = this.isPlayer(target);
		this.setDeltaMovement(new Vec3(0D, 0D, 0D));
		Level world = this.getLevel();
		BlockPos pos = this.blockPosition();
		BlockPos targetPos = target.blockPosition();

		int maxTickTime = Math.min(10, (int) this.distance(target));
		float rate = ++this.tickTime / (float) maxTickTime;
		double x = (targetPos.getX() - pos.getX()) * rate;
		double y = (targetPos.getY() - pos.getY()) * rate;
		double z = (targetPos.getZ() - pos.getZ()) * rate;
		BlockPos attackPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
		BlockState state = world.getBlockState(attackPos);
		SoundType sound = state.getBlock().getSoundType(state, world, pos, this);
		world.playSound(null, attackPos, sound.getBreakSound(), SoundSource.HOSTILE, 2.5F, 0.9F + this.rand.nextFloat() * 0.2F);

		// 攻撃した人をリストに含まれないプレイヤーリストを取得
		List<LivingEntity> attackList = this.getEntityList(LivingEntity.class, this.getFilterList(isPlayer), attackPos, 3D);
		this.targetList.addAll(attackList);
		float amount = 15F;

		// 対象のえんちちーに攻撃
		for (LivingEntity entity : attackList) {
			this.attackDamage(entity, this.getSRC(), entity instanceof Enemy ? amount * 4F : amount);
			entity.setDeltaMovement(entity.getDeltaMovement().add(entity.getX() - this.getX(), 0.8D, entity.getZ() - this.getZ()));
		}

		if (world instanceof ServerLevel server) {
			ParticleOptions par = new BlockParticleOption(ParticleTypes.BLOCK, state);
			for (int i = 0; i < 4; i++) {
				BlockPos rangePos = new BlockPos(attackPos.getX(), attackPos.getY() - 2D + 0.25D + i, attackPos.getZ());
				this.spawnParticleRing(server, par, 2D, rangePos, 1, 0.25D, 0D);
			}
		}

		if (this.tickTime < maxTickTime) { return; }

		this.clearInfo();
	}

	// 突進攻撃
	public void rushAttack(LivingEntity target) {
		if (this.rushAttackChargeTime++ < RUSHATTACKCHARGEMAXTIME) { return; }

		// 移動速度を取得
		Vec3 vec = this.getDeltaMovement();
		double vX = vec.x;
		double vY = vec.y;
		double vZ = vec.z;

		// 攻撃者の座標取得
		Vec3 src = new Vec3(this.getX(), this.getY(), this.getZ()).add(0, this.getEyeHeight(), 0);
		Vec3 look = this.getViewVector(1F);
		Vec3 dest = src.add(look.x * 2D, this.getY(), look.z * 2D);

		// 移動速度を設定
		vX = dest.x - src.x;
		vZ = dest.z - src.z;
		this.setDeltaMovement(new Vec3(vX * 1.5D, vY, vZ * 1.5D));
		boolean isHalf = ( this.isHalfHealth(this) || this.hasEffect(PotionInit.flame) ) && !this.hasEffect(PotionInit.frost);

		if (isHalf && this.getLevel() instanceof ServerLevel sever) {

			BlockPos pos = this.blockPosition();
			float aX = (float) (-vec.x / 6F);
			float aY = (float) (-vec.y / 6F);
			float aZ = (float) (-vec.z / 6F);

			for (int i = 0; i < 16; i++) {
				float x = (float) (pos.getX() + this.getRandFloat(1.5F) - 0.75F);
				float y = (float) (pos.getY() + this.getRandFloat(2F) + 0.5F);
				float z = (float) (pos.getZ() + this.getRandFloat(1.5F) - 0.75F);
				sever.sendParticles(ParticleTypes.FLAME, x, y, z, 0, aX, aY, aZ, 1F);
			}
		}

		// 攻撃した人をリストに含まれないプレイヤーリストを取得
		List<LivingEntity> attackList = this.getEntityList(LivingEntity.class, this.getFilterList(this.isPlayer(target)), 1D);
		if (attackList.isEmpty()) { return; }

		boolean isFlame = false;
		float amount = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);

		// 対象のえんちちーに攻撃
		for (LivingEntity entity : attackList) {

			float totalAmount = amount;

			if (isHalf) {

				if (entity.hasEffect(PotionInit.flame)) {
					isFlame = true;
				}

				else {

					if (entity.hasEffect(PotionInit.reflash_effect)) {
						totalAmount += 2;
					}

					else {
						this.addPotion(entity, PotionInit.flame, 300, 0);
					}
				}
			}

			this.attackDamage(entity, this.getSRC(), entity instanceof Enemy ? totalAmount * 4F : totalAmount);
			entity.setDeltaMovement(entity.getDeltaMovement().add(entity.getX() - this.getX(), 0.8D, entity.getZ() - this.getZ()));
			this.targetList.add(entity);

			if (this.getLevel() instanceof ServerLevel sever) {
				sever.sendParticles(ParticleTypes.CRIT, entity.getX(), entity.getY() + 1D, entity.getZ(), 0, 0, 0, 0, 1F);
			}
		}

		Vec3 vec3 = new Vec3(this.getX() - target.getX(), 0.2D, this.getZ() - target.getZ()).scale(2.5D);
		this.setDeltaMovement(this.getDeltaMovement().add(vec3));
		this.rushAttackChargeTime = isFlame ? RUSHATTACKCHARGEMAXTIME - 20 : 0;
		this.clearInfo();
		this.playSound(SoundEvents.COW_AMBIENT, 1.5F, 1.2F);
	}

	public float distance(Entity entity) {
		float f = (float) (this.getX() - entity.getX());
		float f2 = (float) (this.getZ() - entity.getZ());
		return Mth.sqrt(f * f + f2 * f2);
	}

	@Override
	public boolean isArmorEmpty() {
		return true;
	}

	@Override
	public void clearInfo() {
		this.targetList.clear();
		this.setAttackType(this.rand.nextInt(3));
		this.tickTime = 0;
		this.pressAttackChargeTime = 0;
	}

	public Predicate<LivingEntity> getFilterList(boolean isPlayer) {
		return e -> !e.isSpectator() && e.isAlive() && e != this && !this.targetList.contains(e) && (isPlayer ? (e instanceof Player || e instanceof AbstractSummonMob) : !(e instanceof Player) ) && !(e instanceof ISMMob);
	}
}
