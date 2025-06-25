package sweetmagic.init.entity.animal;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.ientity.ISMMob.SMMoveControl;
import sweetmagic.init.EntityInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.item.sm.SMFood;

public class WitchCat extends AbstractSummonMob {

	protected int coolTime = 0;
	protected int recastTime = 0;

	public WitchCat(Level world) {
		super(EntityInit.witchCat, world);
	}

	public WitchCat(EntityType<? extends AbstractSummonMob> eType, Level world) {
		super(eType, world);
		this.moveControl = new SMMoveControl(this);
		this.setNoGravity(true);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 20D)
				.add(Attributes.FLYING_SPEED, 0.2D)
				.add(Attributes.MOVEMENT_SPEED, 0.1D)
				.add(Attributes.ATTACK_DAMAGE, 3D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 1D)
				.add(Attributes.ARMOR, 0.15D)
				.add(Attributes.FOLLOW_RANGE, 48D);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(1, new FloatGoal(this));
		this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
		this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
		this.goalSelector.addGoal(4, new RandomOwnerMoveGoal(this, 0D));
		this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1D, true));
		this.goalSelector.addGoal(7, new BreedGoal(this, 1D));
		this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1D));
		this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8F));
		this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new SMOwnerHurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new SMOwnerHurtTargetGoal(this));
		this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setAlertOthers());
		this.targetSelector.addGoal(4, new NearestAttackSMMobGoal<>(this, Monster.class, false));
		this.targetSelector.addGoal(5, new AttackTargetGoal<>(this, Raider.class, false));
		this.targetSelector.addGoal(6, new AttackTargetGoal<>(this, Warden.class, false));
	}

	public void setState(double rate) {
		super.setState(rate);
		this.setAttribute(Attributes.FLYING_SPEED, rate);
		this.setAttribute(Attributes.KNOCKBACK_RESISTANCE, rate);
	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.CAT_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource src) {
		return SoundEvents.CAT_HURT;
	}

	protected float getSoundVolume() {
		return 0.4F;
	}

	public float getDamageAmount(float amount) {
		this.heal(amount * 0.1F);
		return Math.min(20F, amount * 0.5F);
	}

	public boolean foodAction(Player player, ItemStack stack) {

		ItemStack foodStack = this.getMainHandItem();

		if (foodStack.isEmpty() && !stack.isEmpty()) {
			this.setItemSlot(EquipmentSlot.MAINHAND, stack.copy());
			stack.shrink(stack.getCount());
			this.playSound(SoundEvents.ITEM_PICKUP, 0.67F, 1F);
		}

		else if (!foodStack.isEmpty()) {
			ItemStack copy = foodStack.copy();

			if (!stack.isEmpty()) {
				this.setItemSlot(EquipmentSlot.MAINHAND, stack.copy());
				stack.shrink(stack.getCount());
			}

			foodStack.shrink(foodStack.getCount());
			this.getLevel().addFreshEntity(new ItemEntity(this.getLevel(), player.getX(), player.getY(), player.getZ(), copy));
			this.playSound(SoundEvents.ITEM_PICKUP, 0.67F, 1F);
		}

		else {
			return false;
		}

		return true;
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putInt("coolTime", this.coolTime);
		tags.putInt("recastTime", this.recastTime);
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.coolTime = tags.getInt("coolTime");
		this.recastTime = tags.getInt("recastTime");
	}

	protected boolean shouldStayCloseToLeashHolder() {
		return false;
	}

	public void travel(Vec3 vec) {

		if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {

			if (this.isInWater()) {
				this.moveRelative(0.02F, vec);
				this.move(MoverType.SELF, this.getDeltaMovement());
				this.setDeltaMovement(this.getDeltaMovement().scale((double) 0.8F));
			}

			else if (this.isInLava()) {
				this.moveRelative(0.02F, vec);
				this.move(MoverType.SELF, this.getDeltaMovement());
				this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
			}

			else {
				this.moveRelative(this.getSpeed(), vec);
				this.move(MoverType.SELF, this.getDeltaMovement());
				this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
			}
		}

		this.calculateEntityAnimation(this, false);
	}

	protected float getStandingEyeHeight(Pose po, EntityDimensions dim) {
		return dim.height * 0.6F;
	}

	public void tick() {
		super.tick();
		this.addPotion();
	}

	public void addPotion() {
		if (this.isClient()) { return; }

		if (this.tickCount % 10 == 0 && this.recastTime-- <= 0) {
			this.recastTime = 10;
			ItemStack food = this.getMainHandItem();
			if (!food.isEmpty()) {
				this.eatFood(food);
			}
		}

		if (this.coolTime > 0) {
			int time = this.getTarget() == null ? 1 : 3;
			this.coolTime -= time;

			// クールタイムが終わってないなら
			if (this.coolTime > 0) { return; }
		}

		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, e -> e.isAlive() && (e instanceof Player || e instanceof AbstractSummonMob), this.getRange() * 2F);

		for (LivingEntity entity : entityList) {

			float maxHealth = entity.getMaxHealth();
			boolean isPlayer = entity instanceof Player;

			if (entity.getHealth() <= maxHealth / 2F) {
				float healRate = isPlayer ? 0.1F : 0.25F;
				entity.heal(entity.getMaxHealth() * healRate);

				if (isPlayer) {
					entity.playSound(SoundInit.HEAL, 0.0625F, 1F);
				}

				double pX = entity.getX();
				double pY = entity.getY();
				double pZ = entity.getZ();

				for (int i = 0; i < 3; i++) {
					this.spawnParticleRing(this.getLevel(), ParticleTypes.HAPPY_VILLAGER, 1D, pX, pY, pZ, 0.75D + i * 0.5D, 1D, 1D);
				}
			}

			else if (entity.getHealth() >= maxHealth) {
				this.addPotion(entity, MobEffects.DAMAGE_BOOST, 1, 1200);

				if (isPlayer) {
					entity.playSound(SoundEvents.BREWING_STAND_BREW, 0.025F, 1.175F);
				}
			}

			else {
				this.addPotion(entity, PotionInit.aether_armor, 1, 1200);

				if (isPlayer) {
					entity.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 0.025F, 1.175F);
				}
			}
		}

		this.coolTime = 1200;
	}

	public void eatFood(ItemStack stack) {

		Item item = stack.getItem();
		List<AbstractSummonMob> entityList = this.getEntityList(AbstractSummonMob.class, e -> e.isAlive() && e.getHealth() <= e.getMaxHealth() * 0.33F, this.getRange() * 2F);

		for (AbstractSummonMob entity : entityList) {
			int foodValue = stack.getFoodProperties(entity).getNutrition();
			entity.heal((float) foodValue);
			entity.gameEvent(GameEvent.EAT, entity);
			stack.shrink(1);
			entity.playSound(SoundEvents.GENERIC_EAT, 0.25F, 0.9F + this.rand.nextFloat(0.2F));

			if (item instanceof SMFood food) {
				food.onFoodEat(this.getLevel(), entity, stack);
				entity.setMaxLifeTime(entity.getMaxLifeTime() + foodValue * 50);
			}

			if (stack.isEmpty()) { break; }
		}
	}

	public void discordAction() {
		ItemStack food = this.getMainHandItem();
		if (food.isEmpty()) { return; }

		LivingEntity owner = this.getOwner();
		BlockPos pos = this.blockPosition();

		if (owner != null) {
			pos = owner.blockPosition().above();
		}

		this.getLevel().addFreshEntity(new ItemEntity(this.getLevel(), pos.getX(), pos.getY(), pos.getZ(), food));
		this.playSound(SoundEvents.ITEM_PICKUP, 0.67F, 1F);
	}

	protected void tickDeath() {
		this.discordAction();
		super.tickDeath();
	}

	// パーティクルスポーンリング
	protected void spawnParticleRing(Level world, ParticleOptions par, double range, double x, double y, double z, double addY, double ySpeed, double moveValue) {
		if (!(world instanceof ServerLevel server)) { return; }

		y += addY;

		for (double degree = -range * Math.PI; degree < range * Math.PI; degree += 1D) {

			double yS = ySpeed;

			if (ySpeed != 0D) {
				yS += this.rand.nextFloat() * 0.025F;
			}

			server.sendParticles(par, x + Math.cos(degree), y, z + Math.sin(degree), 0, 0D, yS, 0D, moveValue);
		}
	}

	public Vec3 getLeashOffset() {
		return new Vec3(0D, (double) this.getEyeHeight() * 0.6D, (double) this.getBbWidth() * 0.1D);
	}
}
