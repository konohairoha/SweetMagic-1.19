package sweetmagic.init.entity.monster;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.entity.projectile.FrostMagicShot;
import sweetmagic.util.PlayerHelper;

public class SkullFrostRoyalGuard extends AbstractSMSkull {

	private int tickTime = 0;
	protected int defTime = 0;
	private int recastTime = 300;
	private int guardTime = 0;
	private static final int MAX_GUARDTIME = 300;
	private static final EntityDataAccessor<Boolean> GUARD = ISMMob.setData(SkullFrostRoyalGuard.class, BOOLEAN);
	private static final EntityDataAccessor<Integer> GUARD_POWER = ISMMob.setData(SkullFrostRoyalGuard.class, INT);

	public SkullFrostRoyalGuard(Level world) {
		super(EntityInit.skullFrostRoyalGuard, world);
	}

	public SkullFrostRoyalGuard(EntityType<? extends AbstractSMSkull> enType, Level world) {
		super(enType, world);
		this.xpReward = 200;
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.define(GUARD, false);
		this.define(GUARD_POWER, 0);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(2, new RestrictSunGoal(this));
		this.goalSelector.addGoal(3, new FleeSunGoal(this, 1D));
		this.goalSelector.addGoal(4, new RangedBowAttackGoal<>(this, 1D, 40, 24F));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1D));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8F));
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Warden.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Raider.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 150D)
				.add(Attributes.MOVEMENT_SPEED, 0.35D)
				.add(Attributes.ATTACK_DAMAGE, 4D)
				.add(Attributes.ARMOR, 8D)
				.add(Attributes.FOLLOW_RANGE, 32D);
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

		if (this.getGuard()) {
			float dameCute = 2 + this.getGuardPower();
			amount /= dameCute;
			this.playSound(SoundEvents.BLAZE_HURT, 2F, 0.85F);
		}

		return super.hurt(src, amount);
	}

	public void tick() {
		super.tick();

		if (this.defTime > 0) {
			this.defTime--;
		}

		if (this.getLevel().isClientSide()) {

			this.tickTime++;
			if (this.tickTime % 60 != 0) { return; }

			this.tickTime = 0;
			RandomSource rand = this.getRandom();
			Vec3 vec = this.getDeltaMovement();

			for (int i = 0; i < 6; i++) {
				float x = (float) this.getX() - 0.5F + rand.nextFloat();
				float y = (float) this.getY() + rand.nextFloat() * 2F;
				float z = (float) this.getZ() - 0.5F + rand.nextFloat();
				float f1 = (float) (vec.x + 0.5F - rand.nextFloat()) * 0.2F;
				float f2 = (float) (vec.y + 0.5F - rand.nextFloat()) * 0.2F;
				float f3 = (float) (vec.z + 0.5F - rand.nextFloat()) * 0.2F;
				this.getLevel().addParticle(ParticleInit.FROST, x, y, z, f1, f2, f3);
			}
		}

		else if (this.hasEffect(PotionInit.frost)) {
			this.removeEffect(PotionInit.frost);
		}

		else {
			this.clearFire();
		}
	}

	protected void customServerAiStep() {
		super.customServerAiStep();

		if (this.guardTime++ >= MAX_GUARDTIME && this.getGuard()) {
			this.setGuard(false);
			this.guardTime = 0;
			this.recastTime = this.getRandom().nextInt(150) + 250;
		}

		LivingEntity target = this.getTarget();
		if (target == null || this.recastTime-- > 0 || this.getGuard() || this.isLeader(this)) { return; }

		this.setGuard(true);
		int buffSize = PlayerHelper.getEffectList(target, PotionInit.BUFF).size();
		this.setGuardPower(buffSize);
		this.guardTime = 0;
		this.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 0.5F, 1.175F);
	}

	public void performRangedAttack(LivingEntity target, float par1) {
		if (this.getGuard()) { return; }

		boolean isWarden = target instanceof Warden;
		boolean isHard = this.isHard(this.getLevel());
		float damage = isWarden ? 15F : 3F;
		float shotSpeed = isWarden ? 3.5F : 2F;
		float shake = 0F;
		int shotRange = isWarden ? 40 : 30;

		// ウォーデン以外でハードなら威力を上昇
		if (!isWarden && isHard) {
			damage += 1.5F;
		}

		AbstractMagicShot entity = new FrostMagicShot(this.getLevel(), this);
		double d0 = target.getX() - this.getX();
		double d1 = target.getY(0.3333333333333333D) - this.getY();
		double d2 = target.getZ() - this.getZ();
		double d3 = Math.sqrt(d0 * d0 + d2 * d2);
		entity.shoot(d0, d1 - d3 * (double) 0.065F, d2, shotSpeed, shake);
		entity.setAddDamage(entity.getAddDamage() + damage);
		entity.setMaxLifeTime(shotRange);
		this.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);
		this.getLevel().addFreshEntity(entity);
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putBoolean("isGuard", this.getGuard());
		tags.putInt("guardPower", this.getGuardPower());
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setGuard(tags.getBoolean("isGuard"));
		this.setGuardPower(tags.getInt("guardPower"));
	}

	public boolean getGuard() {
		return this.get(GUARD);
	}

	public void setGuard(boolean isGurd) {
		this.set(GUARD, isGurd);
	}

	public int getGuardPower() {
		return this.get(GUARD_POWER);
	}

	public void setGuardPower(int guardPower) {
		this.set(GUARD_POWER, guardPower);
	}

	// 低ランクかどうか
	public boolean isLowRank() {
		return false;
	}

	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance dif, MobSpawnType spawn, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		data = super.finalizeSpawn(world, dif, spawn, data, tag);
		this.initMobData(this, dif);
		this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
		this.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
		this.armorDropChances[EquipmentSlot.CHEST.getIndex()] = 0F;
		this.armorDropChances[EquipmentSlot.FEET.getIndex()] = 0F;
		return data;
	}
}
