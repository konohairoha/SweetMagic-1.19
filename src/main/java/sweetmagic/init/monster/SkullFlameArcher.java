package sweetmagic.init.entity.monster;

import javax.annotation.Nullable;

import com.mojang.math.Vector3f;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Wolf;
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
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.entity.projectile.FireMagicShot;

public class SkullFlameArcher extends AbstractSMSkull {

	protected int defTime = 0;

	public SkullFlameArcher(Level world) {
		super(EntityInit.skullFlameArcher, world);
	}

	public SkullFlameArcher(EntityType<? extends AbstractSMSkull> enType, Level world) {
		super(enType, world);
		this.xpReward = 200;
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(2, new RestrictSunGoal(this));
		this.goalSelector.addGoal(3, new FleeSunGoal(this, 1D));
		this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Wolf.class, 6F, 1D, 1.2D));
		this.goalSelector.addGoal(4, new RangedBowAttackGoal<>(this, 1D, 140, 24F));
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

		return super.hurt(src, amount);
	}

	public void tick() {
		super.tick();

		if (this.defTime > 0) {
			this.defTime--;
		}

		if (this.getLevel().isClientSide()) {

			if (this.tickCount % 60 != 0) { return; }

			RandomSource rand = this.getRandom();
			Vec3 vec = this.getDeltaMovement();

			for (int i = 0; i < 6; i++) {
				float x = (float) this.getX() - 0.5F + rand.nextFloat();
				float y = (float) this.getY() + rand.nextFloat() * 2F;
				float z = (float) this.getZ() - 0.5F + rand.nextFloat();
				float f1 = (float) (vec.x + 0.5F - rand.nextFloat()) * 0.2F;
				float f2 = (float) (vec.y + 0.5F - rand.nextFloat()) * 0.2F;
				float f3 = (float) (vec.z + 0.5F - rand.nextFloat()) * 0.2F;
				this.getLevel().addParticle(ParticleTypes.FLAME, x, y, z, f1, f2, f3);
			}
		}

		else if (this.hasEffect(PotionInit.flame)) {
			this.removeEffect(PotionInit.flame);
		}
	}

	public void performRangedAttack(LivingEntity target, float par1) {

		boolean isWarden = target instanceof Warden;
		boolean isHard = this.isHard(this.getLevel());
		float damage = isWarden ? 13F : 2F;
		float shotSpeed = isWarden ? 7.5F : 1.75F;
		int shotRange = isWarden ? 50 : 40;

		// ウォーデン以外でハードなら威力を上昇
		if (!isWarden && isHard) {
			damage += 1.5F;
		}

		for (int i = 0; i < 5; i++) {
			AbstractMagicShot entity = new FireMagicShot(this.getLevel(), this);
			double d0 = target.getX() - this.getX();
			double d1 = target.getZ() - this.getZ();
			double d3 = target.getY(0.3333333333333333D) - this.getY();
			Vector3f vec = this.getShotVector(this, new Vec3(d0, d3, d1), -24 + i * 12);
			entity.shoot(vec.x(), vec.y(), vec.z(), shotSpeed, 1);
			entity.setAddDamage(entity.getAddDamage() + damage);
			entity.setMaxLifeTime(shotRange);
			entity.setArrow(true);
			entity.setRange(3.5D);
			this.getLevel().addFreshEntity(entity);
		}

		this.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);
	}

	// 低ランクかどうか
	public boolean isLowRank() {
		return false;
	}

	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance dif, MobSpawnType spawn, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		data = super.finalizeSpawn(world, dif, spawn, data, tag);
		this.initMobData(this, dif);
		this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
		this.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
		return data;
	}
}
