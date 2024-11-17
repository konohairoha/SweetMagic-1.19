package sweetmagic.init.entity.monster;

import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.util.SMDamage;

public class PhantomWolf extends AbstractSMMob {

	private UUID ownerID;
	private LivingEntity owner;

	public PhantomWolf(Level world) {
		super(EntityInit.phantomWolf, world);
	}

	public PhantomWolf(EntityType<PhantomWolf> enType, Level world) {
		super(enType, world);
		this.xpReward = 75;
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(1, new FloatGoal(this));
		this.goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.4F));
		this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1D, true));
		this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1D));
		this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Warden.class, 8.0F));
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Warden.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Raider.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 30D)
				.add(Attributes.MOVEMENT_SPEED, 0.2D)
				.add(Attributes.ATTACK_DAMAGE, 4D)
				.add(Attributes.FOLLOW_RANGE, 32D);
	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.WOLF_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource src) {
		return SoundEvents.WOLF_HURT;
	}

	public float getVoicePitch() {
		return 0.875F;
	}

	protected float getSoundVolume() {
		return 0.5F;
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {
		Entity attacker = src.getEntity();
		if ( attacker != null && attacker instanceof ISMMob) { return false; }

		// ダメージ倍処理
		amount = this.getDamageAmount(this.level , src, amount, 0.25F);
		return super.hurt(src, amount);
	}

	public boolean doHurtTarget(Entity entity) {

		float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
		DamageSource src = SMDamage.getAddDamage(this, this);

		if (entity instanceof Warden) {
			damage *= 4F;
		}

		boolean flag = entity.hurt(src, damage);
		entity.invulnerableTime = 0;

		if (flag) {
			this.doEnchantDamageEffects(this, entity);
		}

		return flag;
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);

		if (this.getOwnerID() != null) {
			tags.putUUID("ownerID", this.getOwnerID());
		}
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);

		if (tags.contains("ownerID")) {
			this.setOwnerID(tags.getUUID("ownerID"));
		}
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
}
