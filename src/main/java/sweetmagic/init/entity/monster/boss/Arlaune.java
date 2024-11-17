package sweetmagic.init.entity.monster.boss;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.BlockInit;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.monster.CherryPlant;
import sweetmagic.init.entity.projectile.CherryMagicShot;
import sweetmagic.util.SMDamage;

public class Arlaune extends AbstractSMBoss {

	private static final EntityDataAccessor<Boolean> IS_CLOSE = ISMMob.setData(Arlaune.class, BOOLEAN);
	private static final EntityDataAccessor<Boolean> SUMMON = ISMMob.setData(Arlaune.class, BOOLEAN);
	private static final EntityDataAccessor<Integer> CHERRY = ISMMob.setData(Arlaune.class, INT);
	private static final EntityDataAccessor<Integer> PLANT = ISMMob.setData(Arlaune.class, INT);
	private final ServerBossEvent bossEvent = this.getBossBar(BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.NOTCHED_6);

	private int cherryTime = 0;
	private final static int MAX_CHERRYTIME = 100;

	private int plantTime = 0;
	private final static int MAX_PLANTTIME = 100;

	private int shotTime = 140;
	private final static int MAX_SHOTTIME = 150;

	private int summonTime = 600;
	private final static int MAX_SUMMONTIME = 600;

	private int rainTime = 0;
	private final static int MAX_RAINTIME = 220;
	public double oldHealth = 450D;
	public Arlaune(Level world) {
		super(EntityInit.arlaune, world);
	}

	public Arlaune(EntityType<? extends AbstractSMBoss> enType, Level world) {
		super(enType, world);
		this.xpReward = 300;
		this.maxUpStep = 1.25F;
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 450D)
				.add(Attributes.MOVEMENT_SPEED, 0.3D)
				.add(Attributes.ATTACK_DAMAGE, 4D)
				.add(Attributes.ARMOR, 8D)
				.add(Attributes.FOLLOW_RANGE, 64D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 64D);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SUMMON, false);
		this.entityData.define(IS_CLOSE, false);
		this.entityData.define(CHERRY, 0);
		this.entityData.define(PLANT, 0);
	}

	public boolean isClose () {
		return this.entityData.get(IS_CLOSE);
	}

	public void setClose (boolean isClose) {
		this.entityData.set(IS_CLOSE, isClose);
	}

	public boolean isSummon () {
		return this.entityData.get(SUMMON);
	}

	public void setSummon (boolean summon) {
		this.entityData.set(SUMMON, summon);
	}

	public int getCherry () {
		return this.entityData.get(CHERRY);
	}

	public void setCherry (int cherry) {
		this.entityData.set(CHERRY, cherry);
	}

	public int getPlant () {
		return this.entityData.get(PLANT);
	}

	public void setPlant (int plant) {
		this.entityData.set(PLANT, plant);
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putBoolean("isClose", this.isClose());
		tags.putInt("cherry", this.getCherry());
		tags.putInt("plant", this.getPlant());
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setClose(tags.getBoolean("isClose"));
		this.setCherry(tags.getInt("cherry"));
		this.setPlant(tags.getInt("plant"));

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

		if (amount > 1F && !this.level.isClientSide) {
			this.setCherry(this.getCherry() + 1);
		}

		return super.hurt(src, amount);
	}

	public boolean causeFallDamage(float par1, float par2, DamageSource src) {
		return false;
	}

	protected void customServerAiStep() {

		if (this.oldHealth != this.getHealth()) {
			this.oldHealth = this.getHealth();
		}

		super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

        if (this.isHalfHealth(this)) {
        	this.bossEvent.setColor(BossBarColor.RED);
        }

		if (this.tickCount % 10 == 0 && this.getCherry() > 0 && this.level instanceof ServerLevel server) {

			RandomSource rand = this.random;
			Vec3 vec = this.getDeltaMovement();

			for (int i = 0; i < this.getCherry(); i++) {

				float x = (float) (this.getX() + this.getRandFloat(1.5F));
				float y = (float) (this.getY() + this.getRandFloat(0.5F) + 2F);
				float z = (float) (this.getZ() + this.getRandFloat(1.5F));

				float f1 = (float) ( (vec.x + 0.5F - rand.nextFloat() ) * 0.2F);
				float f2 = (float) ( (vec.y + 0.5F - rand.nextFloat() ) * 0.2F);
				float f3 = (float) ( (vec.z + 0.5F - rand.nextFloat() ) * 0.2F);
				server.sendParticles(ParticleInit.CHERRY_BLOSSOMS_LARGE.get(), x, y, z, 0, f1, f2, f3, 1F);
			}
		}

		LivingEntity target = this.getTarget();
		if (target == null) {
			this.clearInfo();
			return;
		}

        if (!this.isSummon() && this.summonTime++ >= MAX_SUMMONTIME) {
        	this.summonCherry(target);
        }

		if (!this.hasEffect(MobEffects.SLOW_FALLING)) {
			this.addPotion(this, MobEffects.SLOW_FALLING, 9999, 0);
		}

		this.getLookControl().setLookAt(target, 10F, 10F);
        this.firstAttack(target);

        if (this.isHalfHealth(this)) {
        	this.secondAttack(target);
        }
	}

	public void firstAttack (LivingEntity target) {

		if (this.getCherry() >= 6) {
			this.cherryAttack(target);
		}

		if (this.getPlant() > 0) {
			this.plantAttack(target);
		}

		if (this.shotTime++ >= MAX_SHOTTIME) {
			this.shotAttack(target);
		}

		if (this.tickCount % 100 == 0 && this.isSummon()) {
			this.checkSummon(target);
		}
	}

	public void secondAttack(LivingEntity target) {
		this.rainAttack(target);

		if (this.tickCount % 10 == 0 && !this.hasEffect(PotionInit.reflash_effect)) {
			this.addPotion(this, PotionInit.reflash_effect, 999999, 0);
		}
	}

	public void summonCherry (LivingEntity target) {

		List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), 48D);
		int count = 3 + (targetList.size() - 1) * 2;
		BlockPos pos = this.blockPosition();
		RandomSource rand = this.level.random;

		for (int i = 0; i < count; i++) {

			int setPosCount = 0;
			BlockPos targetPos = new BlockPos(pos.getX() + this.getRand(rand, 16), pos.getY(), pos.getZ() + this.getRand(rand, 16));
			CherryPlant crystal = new CherryPlant(this.level);

			// 座標がブロックだった場合は再設定
			while (!this.level.getBlockState(targetPos).isAir() && !this.level.getBlockState(targetPos).is(BlockInit.rune_character)) {
				targetPos = new BlockPos(targetPos.getX() + this.getRand(rand, 3), targetPos.getY(), targetPos.getZ() + this.getRand(rand, 3));
				if (setPosCount++ >= 16) { break; }
			}

			crystal.setPos(targetPos.getX() + 0.5D, targetPos.getY() + 0.5D, targetPos.getZ() + 0.5D);
			crystal.setOwnerID(this);

			if (!this.level.isClientSide) {
				this.level.addFreshEntity(crystal);
				crystal.playSound(SoundEvents.GRASS_PLACE, 1F, 0.8F + rand.nextFloat() * 0.4F);
			}

			this.addPotion(this, PotionInit.resistance_blow, 999999, 5);
			this.addPotion(this, PotionInit.aether_armor, 200, 4);
			this.addPotion(this, PotionInit.aether_barrier, 200, 4);
		}

		this.setSummon(true);
		this.summonTime = 0;
	}

	public void cherryAttack (LivingEntity target) {

		int count = this.getCherry();
		double range = 8D + count * 1.5D;

		if (this.cherryTime++ >= MAX_CHERRYTIME) {

			float amount = 20F + this.getBuffPower();
			List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), range);

			for (LivingEntity entity : targetList) {
				this.attackDamage(entity, this.getSRC(), entity instanceof Enemy ? amount * 5F : amount);
				entity.playSound(SoundEvents.FIREWORK_ROCKET_BLAST_FAR, 0.25F, 1F);
			}

			this.cherryTime = 0;
			this.setCherry(0);

			if (!(this.level instanceof ServerLevel server)) { return; }

			BlockPos pos = this.blockPosition();
			for (double eRange = 0D; eRange < range; eRange++) {
				for (int i = 0; i < 2; i++) {
					this.spawnParticleRing2(server, ParticleInit.CHERRY_BLOSSOMS_LARGE.get(), 1 + eRange - i, pos.above(i), -0.05D, -0.35D);
				}
			}
		}
	}

	public void plantAttack (LivingEntity target) {

		if (this.plantTime++ >= MAX_PLANTTIME) {

			int count = this.getPlant();
			float amount = 15F + (count - 1) * 5F;
			List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), 48D);

			for (LivingEntity entity : targetList) {
				this.attackDamage(entity, this.getSRC(), entity instanceof Enemy ? amount * 4F : amount);

				List<MobEffectInstance> potionList =  entity.getActiveEffects().stream().filter(e -> e.getEffect().getCategory() == MobEffectCategory.BENEFICIAL).toList();
				if (potionList.isEmpty()) { continue; }

				MobEffect potion = potionList.get(this.rand.nextInt(potionList.size())).getEffect();
				entity.removeEffect(potion);
			}

			this.addPotion(this, MobEffects.DAMAGE_BOOST, 2400, count + 1);
			this.plantTime = 0;
			this.setPlant(0);

			this.setSummon(false);
			this.summonTime = 0;
		}
	}

	public void shotAttack (LivingEntity target) {

		List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), 48D);

		for (LivingEntity entity : targetList) {

			CherryMagicShot magic = new CherryMagicShot(this.level, this, ItemStack.EMPTY);
			double x = entity.getX() - this.getX();
			double y = entity.getY(0.3333333333333333D) - this.getY();
			double z = entity.getZ() - this.getZ();
			double xz = Math.sqrt(x * x + z * z);
			int level = 4;
			float dama = 8F;

			magic.setWandLevel(level);
			magic.shoot(x, y - xz * 0.065D, z, 2F, 0F);
			magic.setAddDamage(magic.getAddDamage() + dama);
			magic.setRange(3D);
			magic.setData(1);
			this.level.addFreshEntity(magic);
		}

		this.shotTime = 0;
	}

	public void checkSummon (LivingEntity target) {
		List<CherryPlant> targetList = this.getEntityList(CherryPlant.class, e -> e.isAlive() && e.getOwnerID() == this.getUUID(), 80D);
		this.setSummon(!targetList.isEmpty());
	}

	public void rainAttack (LivingEntity target) {

		if (this.rainTime >= 0 && this.rainTime < 30) {
			this.setDeltaMovement(this.getDeltaMovement().add(0D, 0.1D, 0D));
		}

		else if (this.rainTime >= 30D) {
			this.setDeltaMovement(new Vec3(0D, 0D, 0D));
		}

		if (this.rainTime++ >= MAX_RAINTIME) {

			BlockPos pos = target.blockPosition();
			boolean isPlayer = this.isPlayer(target);
			List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, this.getFilter(isPlayer), 48D);
			double range = 12.5D + targetList.size() * 5D;

			if (this.level instanceof ServerLevel server) {

				Iterable<BlockPos> posList = this.getPosRangeList(pos, range);

				for (BlockPos p : posList) {

					if (this.rand.nextFloat() >= 0.04F || !this.checkDistances(pos, p, range * range)) { continue; }

					double x = p.getX() + this.rand.nextDouble() * 1.5D - 0.75D;
					double y = p.getY() + this.rand.nextDouble() * 1.5D - 0.75D;
					double z = p.getZ() + this.rand.nextDouble() * 1.5D - 0.75D;
					float f1 = this.getRandFloat(0.1F);
					float f2 = 0.2F + this.getRandFloat(0.1F);
					float f3 = this.getRandFloat(0.1F);
					server.sendParticles(ParticleInit.CHERRY_BLOSSOMS_LARGE.get(), x, y, z, 0, f1, f2, f3, 1F);
				}
			}

			if (this.rainTime % 10 == 0) {

				range += 2.5D;
				float amount = 4F + this.getBuffPower();
				boolean isRainMax = this.rainTime >= MAX_RAINTIME;
				AABB aabb = new AABB(pos.offset(-range, -range, -range), pos.offset(range, range, range));
				List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.getFilter(isPlayer), aabb);

				for (LivingEntity entity : entityList) {

					if (isRainMax) {
						amount *= 3F;
						entity.playSound(SoundEvents.FIREWORK_ROCKET_BLAST_FAR, 0.25F, 1F);
					}

					this.attackDamage(entity, this.getSRC(), entity instanceof Enemy ? amount * 4F : amount);
				}
			}

			if (this.rainTime >= MAX_RAINTIME + 120) {
				this.rainTime = -(260 + this.rand.nextInt(100));
			}
		}
	}

	@Override
	public boolean isArmorEmpty() {
		return false;
	}

	public void clearInfo() {
		this.rainTime = -(50 + this.rand.nextInt(100));
	}

	protected void tickDeath() {
		super.tickDeath();
		this.bossEvent.setProgress(0F);
	}

	protected int getRand (RandomSource rand, int range) {
		return rand.nextInt(range) - rand.nextInt(range);
	}

	public void spawnParticleRing2(ServerLevel server, ParticleOptions par, double range, BlockPos pos, double addY, double speed) {

		double x = pos.getX() + 0.5D;
		double y = pos.getY() + 1D + addY;
		double z = pos.getZ() + 0.5D;
		Random rand = this.rand;

		for (double degree = -range * Math.PI; degree < range * Math.PI; degree += 0.25D) {
			if (rand.nextFloat() >= 0.3F) { continue; }
			double rate = range * 0.75D;
			server.sendParticles(par, x + Math.cos(degree) * rate, y, z + Math.sin(degree) * rate, 0, Math.cos(degree) * 0.65D, 0, Math.sin(degree) * 0.65D, speed);
		}
	}
}
