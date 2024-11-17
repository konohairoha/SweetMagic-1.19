package sweetmagic.init.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.config.SMConfig;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.entity.projectile.BubbleMagicShot;
import sweetmagic.init.entity.projectile.CycloneMagicShot;
import sweetmagic.init.entity.projectile.FrostMagicShot;
import sweetmagic.init.entity.projectile.LightMagicShot;

public class WindWitch extends AbstractSMMob {

	private int tickTime = 0;
	private int recastTime = 0;
	private static final int RAND_RECASTTIME = 50;
	private static final EntityDataAccessor<Boolean> IS_TARGET = SynchedEntityData.defineId(WindWitch.class, BOOLEAN);
	private static final ItemStack WAND = new ItemStack(ItemInit.divine_wand_b);

	public WindWitch(Level world) {
		super(EntityInit.windWitch, world);
	}

	public WindWitch(EntityType<WindWitch> enType, Level world) {
		super(enType, world);
		this.xpReward = 75;
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
				.add(Attributes.MOVEMENT_SPEED, 0.265D)
				.add(Attributes.ATTACK_DAMAGE, 2D)
				.add(Attributes.ARMOR, 3D)
				.add(Attributes.FOLLOW_RANGE, 32D);
	}

	// スポーンまでの日数
	public int getMaxSpawnDate () {
		return SMConfig.spawnDate.get() * 3;
	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.WITCH_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource src) {
		return SoundEvents.WITCH_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.WITCH_DEATH;
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {

		Entity attacker = src.getEntity();
		if ( attacker != null && attacker instanceof ISMMob) { return false; }

		// ダメージ倍処理
		amount = this.getDamageAmount(this.level , src, amount, 0.25F);

		if (!this.level.isClientSide && !src.isMagic() && this.recastTime > 0) {
			this.recastTime = (int) (this.recastTime * 0.75F);
		}

		return super.hurt(src, amount);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(IS_TARGET, false);
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putBoolean("is_target", this.isTarget());
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.entityData.set(IS_TARGET, tags.getBoolean("is_target"));
	}

	public boolean isTarget () {
		return this.entityData.get(IS_TARGET);
	}

	public void tick() {
		super.tick();

		if (!this.level.isClientSide && this.tickTime++ > 10) {
			this.tickTime = 0;
			this.entityData.set(IS_TARGET, this.getTarget() != null);
		}
	}

	protected void customServerAiStep() {

		super.customServerAiStep();

		LivingEntity target = this.getTarget();
		if (target == null) { return; }

		this.getLookControl().setLookAt(target, 10.0F, 10.0F);
		if (this.recastTime-- > 0) { return; }

		boolean isWarden = target instanceof Warden;
		this.recastTime = (int) (( this.rand.nextInt(RAND_RECASTTIME) + RAND_RECASTTIME ) * ( isWarden ? 0.25F : 1F ) * ( this.isHalfHealth(this) ? 0.75F : 1F));

		AbstractMagicShot entity = this.getMagicShot(target, isWarden);
		this.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);
		this.level.addFreshEntity(entity);
	}

	public AbstractMagicShot getMagicShot (LivingEntity target, boolean isWarden) {

		AbstractMagicShot entity = null;

		float dama = isWarden ? 30F : 1.25F;
		float dameRate = isWarden ? 1.25F : this.getDateRate(this.level, 0.1F);

		double x = target.getX() - this.getX();
		double y = target.getY(0.3333333333333333D) - this.getY();
		double z = target.getZ() - this.getZ();
		double xz = Math.sqrt(x * x + z * z);
		int level = isWarden ? 20 : 7;

		switch (this.rand.nextInt(4)) {
		case 0:
			entity = new CycloneMagicShot(this.level, this, ItemStack.EMPTY);
			break;
		case 1:
			entity = new FrostMagicShot(this.level, this, ItemStack.EMPTY);
			break;
		case 2:
			entity = new LightMagicShot(this.level, this, ItemStack.EMPTY);
			break;
		case 3:
			entity = new BubbleMagicShot(this.level, this, ItemStack.EMPTY);
			level = 4;
			break;
		default:
			entity = new CycloneMagicShot(this.level, this, ItemStack.EMPTY);
			level = 3;
			break;
		}

		entity.setWandLevel(level);
		entity.shoot(x, y - xz * 0.035D, z, 3.35F, 0F);
		entity.setAddDamage( (entity.getAddDamage() + dama) * dameRate );

		return entity;
	}

	public ItemStack getStack () {
		return WAND;
	}
}
