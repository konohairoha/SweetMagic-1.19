package sweetmagic.init.entity.animal;

import java.util.EnumSet;
import java.util.Random;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.api.ientity.ISMMob.SMMoveControl;
import sweetmagic.init.EntityInit;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.entity.projectile.FireMagicShot;
import sweetmagic.init.entity.projectile.FrostMagicShot;
import sweetmagic.init.entity.projectile.PoisonMagicShot;

public class WitchAllay extends AbstractSummonMob {

	private @Nullable BlockPos jukeboxPos;
	private int recastTime = 0;
	private static final int RAND_RECASTTIME = 70;
	private float danceTick;
	private float spinTick;
	private float spinTick0;
	private final DynamicGameEventListener<WitchAllay.JukeboxListener> jukeboxListener;
	private static final EntityDataAccessor<Boolean> IS_DANCING = ISMMob.setData(AbstractSummonMob.class, EntityDataSerializers.BOOLEAN);

	public WitchAllay(Level world) {
		this(EntityInit.witchAllay, world);
	}

	public WitchAllay(EntityType<? extends AbstractSummonMob> eType, Level world) {
		super(eType, world);
		this.moveControl = new SMMoveControl(this);
		this.setNoGravity(true);
		this.setCanPickUpLoot(this.canPickUpLoot());
		PositionSource pSrc = new EntityPositionSource(this, this.getEyeHeight());
		JukeboxListener lis = new WitchAllay.JukeboxListener(pSrc, GameEvent.JUKEBOX_PLAY.getNotificationRadius());
		this.jukeboxListener = new DynamicGameEventListener<>(lis);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(IS_DANCING, false);
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
		this.goalSelector.addGoal(4, new RandomOwnerMoveGoal(this));
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
		this.targetSelector.addGoal(7, new AttackTargetGoal<>(this, AbstractSkeleton.class, false));
	}

	public void setState (double rate) {
		super.setState(rate);
		this.setAttribute(Attributes.FLYING_SPEED, rate);
		this.setAttribute(Attributes.KNOCKBACK_RESISTANCE, rate);
	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.ALLAY_AMBIENT_WITH_ITEM;
	}

	protected SoundEvent getHurtSound(DamageSource src) {
		return SoundEvents.ALLAY_HURT;
	}

	protected float getSoundVolume() {
		return 0.25F;
	}

	public boolean isDancing() {
		return this.entityData.get(IS_DANCING);
	}

	public void setDancing(boolean dancing) {
		if (!this.level.isClientSide) {
			this.entityData.set(IS_DANCING, dancing);
		}
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putInt("recastTime", this.recastTime);
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.recastTime = tags.getInt("summonTime");
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
				this.setDeltaMovement(this.getDeltaMovement().scale((double) 0.91F));
			}
		}

		this.calculateEntityAnimation(this, false);
	}

	protected float getStandingEyeHeight(Pose po, EntityDimensions dim) {
		return dim.height * 0.6F;
	}

	private boolean shouldStopDancing() {
		return this.jukeboxPos == null || !this.jukeboxPos.closerToCenterThan(this.position(), (double) GameEvent.JUKEBOX_PLAY.getNotificationRadius()) || !this.level.getBlockState(this.jukeboxPos).is(Blocks.JUKEBOX);
	}

	public void setJukeboxPlaying(BlockPos pos, boolean flag) {
		if (flag) {
			if (!this.isDancing()) {
				this.jukeboxPos = pos;
				this.setDancing(true);
			}
		}

		else if (pos.equals(this.jukeboxPos) || this.jukeboxPos == null) {
			this.jukeboxPos = null;
			this.setDancing(false);
		}
	}

	public boolean isSpinning() {
		return this.danceTick % 55F < 15F;
	}

	public float getSpinningProgress(float par1) {
		return Mth.lerp(par1, this.spinTick0, this.spinTick) / 15F;
	}

	protected void playStepSound(BlockPos pos, BlockState state) { }

	public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> bi) {
		if (this.level instanceof ServerLevel server) {
			bi.accept(this.jukeboxListener, server);
		}
	}

	public InteractionResult mobInteract(Player player, InteractionHand hand) {

		ItemStack stack = player.getItemInHand(hand);
		Item item = stack.getItem();

		if (this.level.isClientSide) {

			if (this.isOwnedBy(player)) {
				return InteractionResult.SUCCESS;
			}

			else {
				return !this.isFood(stack) || !(this.getHealth() < this.getMaxHealth()) ? InteractionResult.PASS : InteractionResult.SUCCESS;
			}
		}

		if (this.isOwnedBy(player)) {

			if (item.isEdible() && this.isFood(stack) && this.getHealth() < this.getMaxHealth()) {
				this.heal((float) stack.getFoodProperties(this).getNutrition());
				this.gameEvent(GameEvent.EAT, this);
				return InteractionResult.CONSUME;
			}

			InteractionResult result = super.mobInteract(player, hand);
			return this.mobClick(result, stack);
		}

		InteractionResult result1 = super.mobInteract(player, hand);
		if (result1.consumesAction()) {
			this.setPersistenceRequired();
		}

		return result1;
	}

	public void aiStep() {
		super.aiStep();
		if (!this.level.isClientSide && this.isAlive() && this.tickCount % 10 == 0) {
			this.heal(1F);
		}

		if (this.isDancing() && this.shouldStopDancing() && this.tickCount % 20 == 0) {
			this.setDancing(false);
			this.jukeboxPos = null;
		}
	}

	public void tick() {

		super.tick();
		if (!this.level.isClientSide) { return; }

		if (this.isDancing()) {

			++this.danceTick;
			this.spinTick0 = this.spinTick;
			if (this.isSpinning()) {
				++this.spinTick;
			}

			else {
				--this.spinTick;
			}

			this.spinTick = Mth.clamp(this.spinTick, 0F, 15F);
		}

		else {
			this.danceTick = 0F;
			this.spinTick = 0F;
			this.spinTick0 = 0F;
		}
	}

	protected void customServerAiStep() {

		this.level.getProfiler().push("allayBrain");
		this.level.getProfiler().pop();
		this.level.getProfiler().push("allayActivityUpdate");
		this.level.getProfiler().pop();
		super.customServerAiStep();

		if (this.recastTime > 0) {
			this.recastTime--;
		}

		LivingEntity target = this.getTarget();
		if (target == null || this.recastTime > 0 || this.getShit()) { return; }

		if (!target.isAlive()) {
			this.setTarget(null);
		}

		boolean isWarden = target instanceof Warden;
		this.recastTime = (int) (( this.random.nextInt(RAND_RECASTTIME) + 130 ) * ( isWarden ? 0.25F : 1F ));

		AbstractMagicShot entity = this.getMagicShot(target, isWarden);
		this.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);
		this.level.addFreshEntity(entity);
	}

	public AbstractMagicShot getMagicShot (LivingEntity target, boolean isWarden) {

		AbstractMagicShot entity = null;

		float dama = this.getPower(this.getWandLevel()) * 0.75F;
		float dameRate = isWarden ? 3F : 1F;

		double x = target.getX() - this.getX();
		double y = target.getY(0.3333333333333333D) - this.getY();
		double z = target.getZ() - this.getZ();
		double xz = Math.sqrt(x * x + z * z);
		int level = isWarden ? 20 : 7;

		if (!isWarden && this.isBoss(target)) {
			dama /= 6;
		}

		switch (this.rand.nextInt(3)) {
		case 0:
			entity = new FireMagicShot(this.level, this, ItemStack.EMPTY);
			break;
		case 1:
			entity = new FrostMagicShot(this.level, this, ItemStack.EMPTY);
			break;
		case 2:
			entity = new PoisonMagicShot(this.level, this, ItemStack.EMPTY);
			break;
		}

		entity.setData(1);
		entity.setWandLevel(level);
		entity.setRange(3D + this.getRange());
		entity.shoot(x, y - xz * 0.035D, z, 1.75F, 0F);
		entity.setAddDamage( (entity.getAddDamage() + dama) * dameRate );

		return entity;
	}

	public Vec3 getLeashOffset() {
		return new Vec3(0D, (double) this.getEyeHeight() * 0.6D, (double) this.getBbWidth() * 0.1D);
	}

	@Override
	public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob entity) {
		return null;
	}

	public class JukeboxListener implements GameEventListener {

		private final PositionSource pSrc;
		private final int radius;

		public JukeboxListener(PositionSource pSrc, int radius) {
			this.pSrc = pSrc;
			this.radius = radius;
		}

		public PositionSource getListenerSource() {
			return this.pSrc;
		}

		public int getListenerRadius() {
			return this.radius;
		}

		public boolean handleGameEvent(ServerLevel world, GameEvent.Message event) {

			if (event.gameEvent() == GameEvent.JUKEBOX_PLAY) {
				WitchAllay.this.setJukeboxPlaying(new BlockPos(event.source()), true);
				return true;
			}

			else if (event.gameEvent() == GameEvent.JUKEBOX_STOP_PLAY) {
				WitchAllay.this.setJukeboxPlaying(new BlockPos(event.source()), false);
				return true;
			}

			return false;
		}
	}

	public class RandomOwnerMoveGoal extends Goal {

		private final AbstractSummonMob mob;

		public RandomOwnerMoveGoal(AbstractSummonMob mob) {
			this.setFlags(EnumSet.of(Goal.Flag.MOVE));
			this.mob = mob;
		}

		public boolean canUse() {
			return !this.mob.isOrderedToSit() && this.mob.getOwner() != null;
		}

		public boolean canContinueToUse() {
			return false;
		}

		public void tick() {

			Random rand = this.mob.rand;
			BlockPos pos = this.mob.blockPosition();
			LivingEntity owner = this.mob.getOwner();
			double dis = this.mob.distanceTo(owner);
			boolean flag = owner.isAlive() && dis > 4;

			if (dis > 8) {
				this.teleportToOwner(owner);
				return;
			}

			for (int i = 0; i < 3; ++i) {

				BlockPos pos1 = flag ? owner.blockPosition().above(2) : pos.offset(rand.nextInt(15) - 7, rand.nextInt(11) - 5, rand.nextInt(15) - 7);
				if (!this.mob.level.isEmptyBlock(pos1)) { continue; }

				this.mob.getMoveControl().setWantedPosition((double) pos1.getX() + 0.5D, (double) pos1.getY() + 0.5D, (double) pos1.getZ() + 0.5D, this.mob.getAttributeBaseValue(Attributes.FLYING_SPEED));

				if (this.mob.getTarget() == null) {
					this.mob.getLookControl().setLookAt((double) pos1.getX() + 0.5D, (double) pos1.getY() + 0.5D, (double) pos1.getZ() + 0.5D, 180.0F, 20.0F);
				}

				break;
			}
		}

		private void teleportToOwner(LivingEntity owner) {
			BlockPos pos = owner.blockPosition();

			for (int i = 0; i < 10; ++i) {
				int x = this.randInclusive(-3, 3);
				int y = this.randInclusive(-1, 1);
				int z = this.randInclusive(-3, 3);
				if (this.maybeTeleportTo(owner, pos.getX() + x, pos.getY() + y, pos.getZ() + z)) { return; }
			}
		}

		private boolean maybeTeleportTo(LivingEntity owner, int x, int y, int z) {

			if (Math.abs(x - owner.getX()) < 2D && Math.abs(z - owner.getZ()) < 2D) { return false; }

			this.mob.moveTo(x + 0.5D, y + 2D, z + 0.5D, this.mob.getYRot(), this.mob.getXRot());
			return true;
		}

		private int randInclusive(int par1, int par2) {
			return this.mob.rand.nextInt(par2 - par1 + 1) + par1;
		}
	}
}
