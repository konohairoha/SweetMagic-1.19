package sweetmagic.init.entity.monster;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.entity.projectile.RockBlastMagicShot;

public class DwarfZombie extends AbstractSMMob {

	private int recastTime = 100;
	private static final int RAND_RECASTTIME = 80;
	private UUID ownerID;
	private LivingEntity owner;
	private static final EntityDataAccessor<Boolean> ISSUMMON = ISMMob.setData(DwarfZombie.class, BOOLEAN);

	public DwarfZombie(Level world) {
		super(EntityInit.dwarfZombie, world);
	}

	public DwarfZombie(EntityType<DwarfZombie> enType, Level world) {
		super(enType, world);
		this.xpReward = 75;
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ISSUMMON, false);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
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
				.add(Attributes.MOVEMENT_SPEED, 0.225D)
				.add(Attributes.ATTACK_DAMAGE, 2D)
				.add(Attributes.ARMOR, 1D)
				.add(Attributes.FOLLOW_RANGE, 16D);
	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.ZOMBIE_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource src) {
		return SoundEvents.ZOMBIE_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ZOMBIE_DEATH;
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {

		Entity attacker = src.getEntity();
		if ( attacker != null && attacker instanceof ISMMob) { return false; }

		// ダメージ倍処理
		amount = this.getDamageAmount(this.level , src, amount, 0.25F);
		return super.hurt(src, amount);
	}

	protected void customServerAiStep() {

		super.customServerAiStep();

		LivingEntity target = this.getTarget();
		if (target == null) { return; }

		this.getLookControl().setLookAt(target, 10F, 10F);
		if (this.recastTime-- > 0) { return; }

		boolean isWarden = target instanceof Warden;
		this.recastTime = (int) (( this.random.nextInt(RAND_RECASTTIME) * 0.5F + RAND_RECASTTIME ) * ( isWarden ? 0.25F : 1F ));

		AbstractMagicShot entity = this.getMagicShot(target, isWarden);
		this.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);
		this.level.addFreshEntity(entity);
	}

	public AbstractMagicShot getMagicShot (LivingEntity target, boolean isWarden) {

		AbstractMagicShot entity = null;

		float damage = isWarden ? 13F : 2F;
		float shotSpeed = isWarden ? 5F : 1.5F;

		double x = target.getX() - this.getX();
		double y = target.getY(0.3333333333333333D) - this.getY();
		double z = target.getZ() - this.getZ();
		double xz = Math.sqrt(x * x + z * z);
		int level = isWarden ? 20 : 7;

		entity = new RockBlastMagicShot(this.level, this, ItemStack.EMPTY);
		entity.setWandLevel(level);
		entity.shoot(x, y - xz * 0.065D, z, shotSpeed, 0F);
		entity.setAddDamage( entity.getAddDamage() + damage );

		return entity;
	}

	protected void tickDeath() {

		super.tickDeath();
		if (!this.getSummon()) { return; }

		LivingEntity entity = this.getEntity();
		if ( entity == null || !(entity instanceof DwarfZombieMaster zombie) ) { return; }

		zombie.getPosList().add(this.blockPosition());
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);

		if (this.getSummon()) {
			tags.putBoolean("isSummon", this.getSummon());

			if (this.getOwnerID() != null) {
				tags.putUUID("ownerID", this.getOwnerID());
			}
		}
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setSummon(tags.getBoolean("isSummon"));

		if (this.getSummon()) {
			this.setOwnerID(tags.getUUID("ownerID"));
		}
	}

	public boolean getSummon () {
		return this.entityData.get(ISSUMMON);
	}

	public void setSummon (boolean summonCount) {
		this.entityData.set(ISSUMMON, summonCount);
	}

	public UUID getOwnerID () {
		return this.ownerID;
	}

	public void setOwnerID (LivingEntity entity) {
		this.ownerID = entity.getUUID();
	}

	public void setOwnerID (UUID id) {
		this.ownerID = id;
	}

	public LivingEntity getEntity () {

		LivingEntity entity = this.owner;

		if (entity == null && this.level instanceof ServerLevel server) {
			entity = (LivingEntity) server.getEntity(this.getOwnerID());
		}

		return entity;
	}

	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance dif, MobSpawnType spawn, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		data = super.finalizeSpawn(world, dif, spawn, data, tag);
		this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ItemInit.alt_pick));
        this.armorDropChances[EquipmentSlot.MAINHAND.getIndex()] = 0F;
		return data;
	}

	public MobType getMobType() {
		return MobType.UNDEAD;
	}
}
