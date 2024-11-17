package sweetmagic.init.entity.monster.boss;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.monster.PixeVex;
import sweetmagic.init.entity.projectile.PoisonMagicShot;
import sweetmagic.util.SMDamage;

public class AncientFairy extends AbstractSMBoss {

	private static final EntityDataAccessor<Boolean> ISSUMMON = ISMMob.setData(AncientFairy.class, BOOLEAN);
	private final ServerBossEvent bossEvent = this.getBossBar(BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.NOTCHED_6);

	private int summonVexTime = 170;
	private final static int MAX_SUMMONVEXTIME = 200;

	private int eatVexTime = 0;
	private final static int MAX_EATVEXTIME = 300;

	private int poisonShotTime = 200;
	private final static int MAX_POISONSHOT = 300;

	private int poisonFogTime = 0;
	private final static int MAX_POISONFOG = 400;

	public AncientFairy(Level world) {
		super(EntityInit.ancientFairy, world);
	}

	public AncientFairy(EntityType<? extends AbstractSMBoss> enType, Level world) {
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
		this.goalSelector.addGoal(10, new SMRandomLookGoal(this));
		this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Warden.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Raider.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 400D)
				.add(Attributes.MOVEMENT_SPEED, 0.4D)
				.add(Attributes.ATTACK_DAMAGE, 4D)
				.add(Attributes.ARMOR, 10D)
				.add(Attributes.FOLLOW_RANGE, 64D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 96D);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ISSUMMON, false);
	}

	public boolean isSummon () {
		return this.entityData.get(ISSUMMON);
	}

	public void setSummon (boolean isSummon) {
		this.entityData.set(ISSUMMON, isSummon);
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putBoolean("isSummon", this.isSummon());
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setSummon(tags.getBoolean("isSummon"));
		if (this.hasCustomName()) {
			this.bossEvent.setName(this.getDisplayName());
		}
	}

	public void setCustomName(@Nullable Component tip) {
		super.setCustomName(tip);
		this.bossEvent.setName(this.getDisplayName());
	}

	public void startSeenByPlayer(ServerPlayer player) {
		super.startSeenByPlayer(player);
		this.bossEvent.addPlayer(player);
	}

	public void stopSeenByPlayer(ServerPlayer player) {
		super.stopSeenByPlayer(player);
		this.bossEvent.removePlayer(player);
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {

		Entity attacker = src.getEntity();
		Entity attackEntity = src.getDirectEntity();
		if ( attacker != null && attacker instanceof ISMMob) { return false; }

		// ボスダメージ計算
		amount = this.getBossDamageAmount(this.level, this.defTime , src, amount, 7F);
		this.defTime = amount > 0 ? 2 : this.defTime;

		// 魔法攻撃以外なら反撃&ダメージ無効
		if (this.notMagicDamage(attacker, attackEntity)) {
			this.attackDamage(attacker, SMDamage.magicDamage, amount);
			return false;
		}

		return super.hurt(src, amount);
	}

	public boolean causeFallDamage(float par1, float par2, DamageSource src) {
		return false;
	}

	protected void customServerAiStep() {

		super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

        if (this.isHalfHealth(this)) {
        	this.bossEvent.setColor(BossBarColor.RED);
        }

        LivingEntity target = this.getTarget();
        if (target == null) { return; }

		this.getLookControl().setLookAt(target, 10F, 10F);
        this.firstAttack(target);

        if (this.isHalfHealth(this)) {
        	this.secondAttack(target);
        }
	}

	public void firstAttack (LivingEntity target) {

		if (!this.isSummon()) {

			if (this.getHealth() >= this.getMaxHealth() * 0.2F && this.summonVexTime++ >= MAX_SUMMONVEXTIME) {
				this.summonVex(target);
				this.summonVexTime -= (this.rand.nextInt(100) + 200);
				this.setHealth(this.getHealth() - this.getMaxHealth() * 0.1F);
			}
		}

		else {
			this.vexAction(target);
		}

		if (this.poisonShotTime++ >= MAX_POISONSHOT) {
			this.poisonShot(target);
			this.poisonShotTime -= (this.rand.nextInt(75) + 150);
		}
	}

	public void secondAttack(LivingEntity target) {
		if (this.poisonFogTime++ >= 0) {
			this.poisonFog(target);
		}
	}

	public void summonVex (LivingEntity target) {

		List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), 48D);
		int summonSize = 1 + targetList.size() * 2;

		for (int i = 0; i < summonSize; i++) {
			PixeVex entity = new PixeVex(this.level);
			entity.setPos(this.getX() + 0.5D, this.getY() + 0.5D + this.getRandFloat(4F), this.getZ() + 0.5D);
			entity.spawnAnim();
			entity.setOwnerID(this);
			entity.setNoGravity(true);
			entity.setMoveControl(new SMMoveControl(entity));
			entity.setElementType(this.rand.nextInt(3));
			this.level.addFreshEntity(entity);
		}

		this.setSummon(true);
	}

	public void eatVex (LivingEntity target) {

		List<PixeVex> targetList = this.getVexList();
		if (targetList.isEmpty()) { return; }

		if (this.level instanceof ServerLevel server) {

			int count = (int) ( ( (float) this.eatVexTime / (float) MAX_EATVEXTIME ) * 8 );
			BlockPos pos = this.blockPosition();
			SimpleParticleType par = ParticleInit.BLOOD.get();

			for (PixeVex vex : targetList) {

				float addY = 0F;
				float entityX = (float) vex.getX();
				float entityY = (float) vex.getY();
				float entityZ = (float) vex.getZ();

				for (int i = 0; i < count; i++) {

					float randX = this.getRandFloat(1F);
					float randY = this.getRandFloat(1F);
					float randZ = this.getRandFloat(1F);

					float x = entityX + 0.5F + randX;
					float y = entityY + 1.5F + randY + addY;
					float z = entityZ + 0.5F + randZ;
					float xSpeed = (pos.getX() - entityX - randX) * 0.115F;
					float ySpeed = (pos.getY() - entityY - randY) * 0.115F;
					float zSpeed = (pos.getZ() - entityZ - randZ) * 0.115F;

					server.sendParticles(par, x, y, z, 0, xSpeed, ySpeed, zSpeed, 1F);
				}
			}
		}

		if (this.eatVexTime >= MAX_EATVEXTIME) {

			int count = 0;

			for (PixeVex vex : targetList) {
				vex.setHealth(0.1F);
				vex.hurt(SMDamage.poisonDamage, 20F);
				count++;
			}

			this.heal(this.getMaxHealth() * 0.01F * count * 5F);
			this.eatVexTime = 0;
			this.removeEffect(PotionInit.aether_armor);
			this.removeEffect(PotionInit.aether_barrier);
		}
	}

	public void poisonShot (LivingEntity target) {

		float dama = 3F;
		List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), 48D);

		for (LivingEntity entity : targetList) {

			PoisonMagicShot magic = new PoisonMagicShot(this.level, this, ItemStack.EMPTY);
			double x = entity.getX() - this.getX();
			double y = entity.getY(0.3333333333333333D) - this.getY();
			double z = entity.getZ() - this.getZ();
			double xz = Math.sqrt(x * x + z * z);
			int level = 5;

			magic.setWandLevel(level);
			magic.shoot(x, y - xz * 0.035D, z, 3.35F, 0F);
			magic.setAddDamage(magic.getAddDamage() + dama);
			this.level.addFreshEntity(magic);
		}

		this.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);
	}

	public void poisonFog (LivingEntity target) {

		if ( !(this.level instanceof ServerLevel server) ) { return; }

		List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), 48D);
		double range = 10D + targetList.size() * 5D;
		BlockPos pos = this.blockPosition();
		Iterable<BlockPos> posList = this.getPosList(pos, range);

		if (this.tickCount % 10 == 0) {

			for (BlockPos p : posList) {

				if (this.rand.nextFloat() >= 0.067F || !this.checkDistances(pos, p, range * range)) { continue; }

				double x = p.getX() + this.rand.nextDouble() * 1.5D - 0.75D;
				double y = p.getY() + this.rand.nextDouble() * 1.5D - 0.75D;
				double z = p.getZ() + this.rand.nextDouble() * 1.5D - 0.75D;
				server.sendParticles(ParticleInit.SMOKY.get(), x, y, z, 0, 67F / 255F, 173F / 255F, 103F / 255F, 1F);
			}
		}

		if (this.poisonFogTime >= MAX_POISONFOG) {

			for (LivingEntity entity : targetList) {

				if (!this.checkDistances(pos, entity.blockPosition(), range * range)) { continue; }

				boolean hasEffect = entity.hasEffect(PotionInit.reflash_effect);
				float rate = hasEffect ? 0.25F : 0.5F;
				entity.setHealth(entity.getHealth() * rate);
				entity.hurt(SMDamage.magicDamage, 0.1F);
			}

			this.poisonFogTime = -(this.rand.nextInt(100) + 200);
		}
	}

	public void vexAction (LivingEntity target) {
		if (this.tickCount % 30 == 0) {
			boolean isEmpty = this.getVexList().isEmpty();
			this.setSummon(isEmpty);

			if (isEmpty) {
				this.eatVexTime = 0;
				this.removeEffect(PotionInit.aether_armor);
				this.removeEffect(PotionInit.aether_barrier);
			}

			else {
				this.addPotion(this, PotionInit.aether_armor, 200, 4);
				this.addPotion(this, PotionInit.aether_barrier, 200, 4);
			}
		}

		if (this.eatVexTime++ >= MAX_EATVEXTIME / 2) {
			this.eatVex(target);
		}
	}

	@Override
	public boolean isArmorEmpty() {
		return true;
	}

	@Override
	public void clearInfo() {
		this.tickTime = 0;
	}

	protected void tickDeath() {
		super.tickDeath();
		this.bossEvent.setProgress(0F);
	}

	public List<PixeVex> getVexList () {
		return this.getEntityList(PixeVex.class, e -> e.isAlive() && e.getOwnerID() == this.getUUID(), 64D);
	}
}
