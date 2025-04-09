package sweetmagic.init.entity.monster;

import javax.annotation.Nullable;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.entity.projectile.FireMagicShot;
import sweetmagic.init.entity.projectile.FrostMagicShot;
import sweetmagic.init.entity.projectile.LightMagicShot;

public class PixeVex extends AbstractOwnerMob {

	private static final EntityDataAccessor<Integer> ELEMENT_TYPE = ISMMob.setData(PixeVex.class, INT);
	private int recastTime = 0;
	private static final int RAND_RECASTTIME = 60;

	public PixeVex(Level world) {
		super(EntityInit.pixeVex, world);
	}

	public PixeVex(EntityType<? extends AbstractSMMob> enType, Level world) {
		super(enType, world);
		this.xpReward = 150;
		this.maxUpStep = 1.25F;
		this.setNoGravity(true);
		this.moveControl = new SMMoveControl(this);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(5, new RandomMoveGoal(this));
		this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Warden.class, 8.0F));
		this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Warden.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Raider.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 30D)
				.add(Attributes.MOVEMENT_SPEED, 0.4D)
				.add(Attributes.ATTACK_DAMAGE, 2D)
				.add(Attributes.ARMOR, 6D)
				.add(Attributes.FOLLOW_RANGE, 32D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 96D);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ELEMENT_TYPE, 0);
	}

	public int getElementType() {
		return this.entityData.get(ELEMENT_TYPE);
	}

	public void setElementType(int elementType) {
		this.entityData.set(ELEMENT_TYPE, elementType);
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putInt("elementType", this.getElementType());
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setElementType(tags.getInt("elementType"));
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {
		Entity attacker = src.getEntity();
		if (attacker != null && attacker instanceof ISMMob) { return false; }

		// ダメージ倍処理
		amount = this.getDamageAmount(this.level , src, amount, 1F);
		return super.hurt(src, Math.min(20F, amount));
	}

	public boolean causeFallDamage(float par1, float par2, DamageSource src) {
		return false;
	}

	public void tick() {
		super.tick();
		if (!this.level.isClientSide || this.tickCount % 30 != 0) { return; }

		Vec3 vec = this.getDeltaMovement();
		SimpleParticleType par = null;

		switch (this.getElementType()) {
		case 1 :
			par = ParticleInit.FROST;
			break;
		case 2 :
			par = ParticleInit.MAGICLIGHT;
			break;
		default:
			par = ParticleTypes.FLAME;
			break;
		}

		for (int i = 0; i < 4; i++) {
			float x = (float) this.getX() - 0.5F + this.rand.nextFloat();
			float y = (float) this.getY() + this.rand.nextFloat() * 2F;
			float z = (float) this.getZ() - 0.5F + this.rand.nextFloat();
			float f1 = (float) (vec.x + 0.5F - this.rand.nextFloat()) * 0.2F;
			float f2 = (float) (vec.y + 0.5F - this.rand.nextFloat()) * 0.2F;
			float f3 = (float) (vec.z + 0.5F - this.rand.nextFloat()) * 0.2F;
			this.level.addParticle(par, x, y, z, f1, f2, f3);
		}
	}

	protected void customServerAiStep() {
		super.customServerAiStep();
		LivingEntity target = this.getTarget();
		if (target == null || this.recastTime-- > 0) { return; }

		boolean isWarden = target instanceof Warden;
		this.recastTime = (int) ((this.rand.nextInt(RAND_RECASTTIME) + RAND_RECASTTIME) * (isWarden ? 0.25F : 1F));
		AbstractMagicShot entity = this.getMagicShot(target, isWarden);
		this.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);
		this.level.addFreshEntity(entity);
	}

	public AbstractMagicShot getMagicShot(LivingEntity target, boolean isWarden) {

		AbstractMagicShot entity = null;
		float dama = isWarden ? 30F : 1.5F;
		float dameRate = isWarden ? 1.25F : 1F;

		double x = target.getX() - this.getX();
		double y = target.getY(0.3333333333333333D) - this.getY();
		double z = target.getZ() - this.getZ();
		double xz = Math.sqrt(x * x + z * z);
		int level = isWarden ? 20 : 3;

		switch (this.getElementType()) {
		case 0:
			entity = new FireMagicShot(this.level, this);
			break;
		case 1:
			entity = new FrostMagicShot(this.level, this);
			break;
		case 2:
			entity = new LightMagicShot(this.level, this);
			break;
		}

		entity.setWandLevel(level);
		entity.shoot(x, y - xz * 0.035D, z, 3.35F, 0F);
		entity.setAddDamage((entity.getAddDamage() + dama) * dameRate);
		return entity;
	}

	public void setMoveControl(SMMoveControl con) {
		this.moveControl = con;
	}

	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance dif, MobSpawnType spawn, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		data = super.finalizeSpawn(world, dif, spawn, data, tag);
		this.setElementType(this.rand.nextInt(3));
		return data;
	}
}
