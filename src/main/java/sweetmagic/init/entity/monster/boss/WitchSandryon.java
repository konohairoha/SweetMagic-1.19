package sweetmagic.init.entity.monster.boss;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
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
import sweetmagic.api.ientity.IWitch;
import sweetmagic.init.EntityInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.entity.projectile.BubbleMagicShot;
import sweetmagic.init.entity.projectile.CycloneMagicShot;
import sweetmagic.init.entity.projectile.ElectricMagicShot;
import sweetmagic.init.entity.projectile.ExplosionMagicShot;
import sweetmagic.init.entity.projectile.FireMagicShot;
import sweetmagic.init.entity.projectile.FrostMagicShot;
import sweetmagic.init.entity.projectile.GravityMagicShot;
import sweetmagic.util.PlayerHelper;
import sweetmagic.util.SMDamage;

public class WitchSandryon extends AbstractSMBoss implements IWitch {

	private int magicTime = 350;
	private static final int MAX_MAGIC_TIME = 600;
	private int buffTime = 100;
	private static final int MAX_BUFF_TIME = 700;
	private int specialTime = 200;
	private static final int MAX_SPECIAL_TIME = 700;
	private int infiniteTime = 650;
	private static final int MAX_INFINITE_TIME = 900;
	private int magicCount = 0;
	private int specialType= -1;
	public AnimationState magicAttackAnim = new AnimationState();
	private static final EntityDataAccessor<Boolean> WAND_CHARGE = ISMMob.setData(WitchSandryon.class, BOOLEAN);
	private static final EntityDataAccessor<Boolean> INFINITE_WAND = ISMMob.setData(WitchSandryon.class, BOOLEAN);
	private static final EntityDataAccessor<Boolean> RESURRECTION = ISMMob.setData(WitchSandryon.class, BOOLEAN);
	private static final EntityDataAccessor<Integer> WAND_SIZE = ISMMob.setData(WitchSandryon.class, INT);

	public WitchSandryon(Level world) {
		super(EntityInit.witchSandryon, world);
	}

	public WitchSandryon(EntityType<? extends AbstractSMBoss> enType, Level world) {
		super(enType, world);
		this.xpReward = 750;
		this.setBossEvent(BC_BLUE, NOTCHED_6);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.define(WAND_CHARGE, false);
		this.define(INFINITE_WAND, false);
		this.define(RESURRECTION, false);
		this.define(WAND_SIZE, 0);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1D, 0F));
		this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8F));
		this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Warden.class, 8F));
		this.goalSelector.addGoal(4, new SMRandomLookGoal(this) {
			public boolean canUse() {
				return !getWandCharge() && !getInfiniteWand() && super.canUse();
			}
		});
		this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Warden.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Raider.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 768D)
				.add(Attributes.MOVEMENT_SPEED, 0.4D)
				.add(Attributes.ATTACK_DAMAGE, 4D)
				.add(Attributes.ARMOR, 10D)
				.add(Attributes.FOLLOW_RANGE, 64D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 96D);
	}

	public void setWandCharge(boolean isWandCharge) {
		this.set(WAND_CHARGE, isWandCharge);
	}

	public boolean getWandCharge() {
		return this.get(WAND_CHARGE);
	}

	public void setInfiniteWand(boolean isInfiniteWand) {
		this.set(INFINITE_WAND, isInfiniteWand);
	}

	public boolean getInfiniteWand() {
		return this.get(INFINITE_WAND);
	}

	public void setResurrection(boolean isResurrection) {
		this.set(RESURRECTION, isResurrection);
	}

	public boolean getResurrection() {
		return this.get(RESURRECTION);
	}

	public void setWandSize(int wandSize) {
		this.set(WAND_SIZE, wandSize);
	}

	public int getWandSize() {
		return this.get(WAND_SIZE);
	}

	public boolean isCharge() {
		return this.getWandCharge() || this.getInfiniteWand();
	}

	public AnimationState getAnimaState() {
		return this.magicAttackAnim;
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putBoolean("WandCharge", this.getWandCharge());
		tags.putBoolean("InfiniteWand", this.getInfiniteWand());
		tags.putBoolean("Resurrection", this.getResurrection());
		tags.putInt("WandSize", this.getWandSize());
		tags.putInt("magicTime", this.magicTime);
		tags.putInt("buffTime", this.buffTime);
		tags.putInt("specialTime", this.specialTime);
		tags.putInt("infiniteTime", this.infiniteTime);
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setWandCharge(tags.getBoolean("WandCharge"));
		this.setInfiniteWand(tags.getBoolean("InfiniteWand"));
		this.setResurrection(tags.getBoolean("Resurrection"));
		this.setWandSize(tags.getInt("WandSize"));
		this.magicTime = tags.getInt("magicTime");
		this.buffTime = tags.getInt("buffTime");
		this.specialTime = tags.getInt("specialTime");
		this.infiniteTime = tags.getInt("infiniteTime");
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {
		Entity attacker = src.getEntity();
		Entity attackEntity = src.getDirectEntity();
		if (attacker != null && attacker instanceof ISMMob) { return false; }

		// ボスダメージ計算
		amount = this.getBossDamageAmount(this.getLevel(), this.defTime , src, amount, 7F);
		this.defTime = amount > 0 ? 2 : this.defTime;

		if (attacker instanceof Warden) {
			this.attackDamage(attacker, SMDamage.magicDamage, amount);
			return false;
		}

		// 魔法攻撃以外ならダメージ減少
		if (this.notMagicDamage(attacker, attackEntity)) {
			amount *= 0.25F;
		}

		if (this.getWandCharge()) {
			amount = Math.min(0.5F, amount);
			this.playSound(SoundEvents.BLAZE_HURT, 1F, 0.85F);
		}

		else if (this.getInfiniteWand()) {
			amount = Math.min(0.1F, amount);
			this.playSound(SoundEvents.BLAZE_HURT, 1F, 0.85F);
		}

		return super.hurt(src, amount);
	}

	public void die(DamageSource src) {
		super.die(src);

		if (!this.getResurrection()) {
			this.setResurrection(true);
			this.addPotion(this, PotionInit.magic_damage_cause, 99999, 3);
			this.addPotion(this, MobEffects.DAMAGE_BOOST, 99999, 3);
		}
	}

	protected void customServerAiStep() {
		super.customServerAiStep();

		LivingEntity target = this.getTarget();
		if (target == null) {
			this.setWandCharge(false);
			this.setInfiniteWand(false);
			return;
		}

		this.checkSpawnPos();
		this.getLookControl().setLookAt(target, 10F, 10F);
		this.firstAttack(target);

		if (this.isHalfHealth(this)) {
			this.secondAttack(target);
			this.checkPotion(PotionInit.reflash_effect, 9999, 0);
		}

		if (!this.getResurrection() && !this.hasEffect(PotionInit.resurrection)) {
			this.clearInfo();
		}
	}

	public void firstAttack(LivingEntity target) {

		if (!this.getInfiniteWand() && this.magicTime++ >= MAX_MAGIC_TIME - 150) {

			this.setWandCharge(true);
			this.setWandSize(6);

			if(this.magicTime >= MAX_MAGIC_TIME && this.magicTime % 10 == 0) {
				this.magicShot(target);
			}
		}

		if (this.buffTime++ >= MAX_BUFF_TIME) {
			this.addPotion(target);
		}
	}

	public void secondAttack(LivingEntity target) {

		if (!this.getWandCharge() && this.infiniteTime++ >= MAX_INFINITE_TIME - 180) {
			this.setInfiniteWand(true);
			this.infiniteWand(target);
		}

		if (this.specialTime++ >= MAX_SPECIAL_TIME - 120) {
			this.specialMagic(target);
		}
	}

	public void magicShot(LivingEntity target) {

		List<LivingEntity> targetList = this.getPlayerList(target);
		this.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);
		boolean isResurrection = this.getResurrection();

		for (LivingEntity entity : targetList) {
			AbstractMagicShot magic = this.getMagicShot(entity, this.magicCount, entity instanceof Warden, isResurrection);
			this.addEntity(magic);
		}

		this.setWandSize(this.getWandSize() - 1);

		if (this.magicCount++ > 5) {
			this.magicTime = this.isHalfHealth(this) ? -150 : 0;
			this.magicCount = 0;
			this.setWandCharge(false);
		}
	}

	public AbstractMagicShot getMagicShot(LivingEntity target, int count, boolean isWarden, boolean isResurrection) {

		AbstractMagicShot entity = null;
		float dama = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) + (isWarden || isResurrection ? 30F : 15F);
		float dameRate = isWarden ? 1.5F : 1F;
		double x = target.getX() - this.getX();
		double y = target.getY(0.3333333333333333D) - this.getY();
		double z = target.getZ() - this.getZ();
		double xz = Math.sqrt(x * x + z * z);
		int level = isWarden ? 50 : 20;

		switch (count) {
		case 1:
			entity = new FireMagicShot(this.getLevel(), this);
			break;
		case 2:
			entity = new CycloneMagicShot(this.getLevel(), this);
			break;
		case 3:
			entity = new FrostMagicShot(this.getLevel(), this);
			break;
		case 4:
			entity = new BubbleMagicShot(this.getLevel(), this);
			break;
		case 5:
			entity = new GravityMagicShot(this.getLevel(), this);
			break;
		default:
			entity = new ExplosionMagicShot(this.getLevel(), this);
			break;
		}

		entity.setWandLevel(level);
		entity.shoot(x, y - xz * 0.065D, z, 2F, 0F);
		entity.setAddDamage( (entity.getAddDamage() + dama) * dameRate );
		entity.setHitDead(false);
		return entity;
	}

	public void addPotion(LivingEntity target) {

		// 体力半分以下なら
		if (this.isHalfHealth(this) || this.getResurrection()) {
			switch (this.rand.nextInt(5)) {
			case 0:
				this.addPotion(this, PotionInit.regeneration, 700, 0);
				this.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1F, 1.175F);
				break;
			case 1:
				this.addPotion(this, PotionInit.aether_shield, 700, 1);
				this.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1F, 1.175F);
				break;
			case 2:
				this.addPotion(this, PotionInit.aether_barrier, 700, 3);
				this.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1F, 1.175F);
				break;
			case 3:
				this.heal(this.getMaxHealth() * 0.0375F);
				this.playSound(SoundInit.HEAL, 0.0625F, 1F);
				if (this.getLevel() instanceof ServerLevel server) {
					this.spawnParticleRing(server, ParticleTypes.HAPPY_VILLAGER, 0.75D, this.blockPosition(), 1D, 0.1D, 0D);
				}
				break;
			case 4:
				this.addPotion(this, PotionInit.recast_reduction, 600, 1);
				this.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1F, 1.175F);
				break;
			}
		}

		// 体力半分以上
		else {

			switch (this.rand.nextInt(5)) {
			case 0:
				this.addPotion(this, PotionInit.magic_damage_cause, 600, 1);
				this.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1F, 1.175F);
				break;
			case 1:
				this.addPotion(this, MobEffects.DAMAGE_BOOST, 600, 1);
				this.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1F, 1.175F);
				break;
			case 2:
				this.addPotion(this, PotionInit.aether_barrier, 600, 1);
				this.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1F, 1.175F);
				break;
			case 3:
				this.heal(this.getMaxHealth() * 0.025F);
				this.playSound(SoundInit.HEAL, 0.0625F, 1F);
				if (this.getLevel() instanceof ServerLevel server) {
					this.spawnParticleRing(server, ParticleTypes.HAPPY_VILLAGER, 0.75D, this.blockPosition(), 1D, 0.1D, 0D);
				}
				break;
			case 4:
				this.addPotion(this, PotionInit.recast_reduction, 600, 0);
				this.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1F, 1.175F);
				break;
			}
		}

		this.buffTime = 0;
	}

	public void infiniteWand(LivingEntity target) {
		if (this.infiniteTime < MAX_INFINITE_TIME) { return; }

		float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) + 35F + (this.getResurrection() ? 10F : 0F);
		List<LivingEntity> targetList = this.getPlayerList(target);

		for (LivingEntity entity : targetList) {
			List<MobEffectInstance> effecList = PlayerHelper.getEffectList(entity, PotionInit.BUFF);
			this.attackDamage(entity, SMDamage.magicDamage, damage);
			effecList.forEach(p -> entity.removeEffect(p.getEffect()));
			if (entity instanceof Player player) {
				entity.playSound(SoundEvents.GLASS_BREAK, 0.33F, 1.15F);
				player.sendSystemMessage(this.getText("buf_release").withStyle(RED));
			}
		}

		this.setInfiniteWand(false);
		this.infiniteTime = 0;
	}

	public void specialMagic(LivingEntity target) {

		if (this.specialType == -1) {
			this.specialType = this.rand.nextInt(3);
		}

		int x = (int) (this.getX() + this.getRandFloat(20F));
		int y = (int) this.getY() + 5;
		int z = (int) (this.getZ() + this.getRandFloat(20F));
		BlockPos pos = new BlockPos(x, y, z);
		float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) + 7F;

		AbstractMagicShot magic = this.getSpecialMagic(target, this.specialType);
		magic.setAddDamage(magic.getAddDamage());
		magic.setRange(3D);
		magic.setData(3);
		magic.shootFromRotation(this, this.getXRot(), this.getYRot(), 0, 0, 0);
		magic.setPos(pos.getX(), pos.getY(), pos.getZ());
		magic.setAddDamage(magic.getAddDamage() + damage);
		magic.setDeltaMovement(new Vec3(0, -0.67D, 0));
		magic.setHitDead(false);
		magic.setChangeParticle(true);
		this.addEntity(magic);

		if (this.specialTime >= MAX_SPECIAL_TIME) {
			this.specialTime = 0;
			this.specialType = this.rand.nextInt(3);
		}
	}

	public AbstractMagicShot getSpecialMagic(LivingEntity target, int type) {
		switch(type) {
		case 1:  return new FireMagicShot(this.getLevel(), this);
		case 2:  return new FrostMagicShot(this.getLevel(), this);
		default: return new ElectricMagicShot(this.getLevel(), this);
		}
	}

	@Override
	public boolean isArmorEmpty() {
		return true;
	}

	@Override
	public void clearInfo() {
		this.tickTime = 0;
		this.addPotion(this, PotionInit.resurrection, 99999, 0);
	}
}
