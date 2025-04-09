package sweetmagic.init.entity.monster.boss;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.BlockInit;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.entity.monster.ArchSpider;
import sweetmagic.init.entity.monster.BlazeTempest;
import sweetmagic.init.entity.monster.BlazeTempestTornado;
import sweetmagic.init.entity.monster.CreeperCalamity;
import sweetmagic.init.entity.monster.DwarfZombie;
import sweetmagic.init.entity.monster.DwarfZombieMaster;
import sweetmagic.init.entity.monster.EnderMage;
import sweetmagic.init.entity.monster.EnderShadow;
import sweetmagic.init.entity.monster.SkullFlame;
import sweetmagic.init.entity.monster.SkullFlameArcher;
import sweetmagic.init.entity.monster.SkullFrost;
import sweetmagic.init.entity.monster.SkullFrostRoyalGuard;
import sweetmagic.init.entity.monster.WindWitch;
import sweetmagic.init.entity.projectile.TwiLightShot;
import sweetmagic.util.SMDamage;

public class TwilightHora extends AbstractSMBoss {

	private int summonTime = 500;
	private static final int MAX_SUMMON_TIME = 600;
	private int twilightTime = 0;
	private static final int MAX_TWILIGHT_TIME = 250;
	private int glowTime = 0;
	private List<Player> playerList = new ArrayList<>();
	private static final EntityDataAccessor<Boolean> ISSUMMON = ISMMob.setData(TwilightHora.class, BOOLEAN);
	private static final EntityDataAccessor<Boolean> ISARMOR = ISMMob.setData(TwilightHora.class, BOOLEAN);

	public TwilightHora(Level world) {
		super(EntityInit.twilightHora, world);
	}

	public TwilightHora(EntityType<? extends AbstractSMBoss> enType, Level world) {
		super(enType, world);
		this.setBossEvent(BC_BLUE, NOTCHED_6);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ISSUMMON, false);
		this.entityData.define(ISARMOR, false);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
		this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Warden.class, 8.0F));
		this.goalSelector.addGoal(4, new SMRandomLookGoal(this));
		this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Warden.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Raider.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 512D)
				.add(Attributes.MOVEMENT_SPEED, 0.4D)
				.add(Attributes.ATTACK_DAMAGE, 4D)
				.add(Attributes.ARMOR, 10D)
				.add(Attributes.FOLLOW_RANGE, 64D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 96D);
	}

	public boolean getSummon() {
		return this.entityData.get(ISSUMMON);
	}

	public void setSummon(boolean summon) {
		this.entityData.set(ISSUMMON, summon);
	}

	public boolean getArmor() {
		return this.entityData.get(ISARMOR);
	}

	public void setArmor(boolean armor) {
		this.entityData.set(ISARMOR, armor);
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putBoolean("Summon", this.getSummon());
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setSummon(tags.getBoolean("Summon"));
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {
		Entity attacker = src.getEntity();
		Entity attackEntity = src.getDirectEntity();
		if (attacker != null && attacker instanceof ISMMob) { return false; }

		// ボスダメージ計算
		amount = this.getBossDamageAmount(this.level, this.defTime , src, amount, 7F);
		this.defTime = amount > 0 ? 2 : this.defTime;

		if (attacker instanceof Warden) {
			this.attackDamage(attacker, SMDamage.magicDamage, amount);
			return false;
		}

		// 魔法攻撃以外ならダメージ減少
		if (this.notMagicDamage(attacker, attackEntity)) {
			amount *= 0.25F;
		}

		if (this.getArmor()) {
			amount = Math.min(0.0001F, amount);
			if(src.getEntity() instanceof Player player && !this.playerList.contains(player)) {
				player.sendSystemMessage(this.getText("hora_cut").withStyle(RED));
				this.playerList.add(player);
			}
		}

		return super.hurt(src, amount);
	}

	public boolean causeFallDamage(float par1, float par2, DamageSource src) {
		return false;
	}

	public void tick() {
		super.tick();
		if (this.deathTime > 0 || this.tickCount % 7 != 0) { return; }

		if (this.level.isClientSide) {
			float f1 = (float) this.getX() - 0.5F + this.rand.nextFloat();
			float f2 = (float) this.getY() + 0.25F + this.rand.nextFloat() * 1.5F;
			float f3 = (float) this.getZ() - 0.5F + this.rand.nextFloat();
			this.level.addParticle(ParticleInit.NORMAL, f1, f2, f3, 0, 0, 0);
		}

		else {

			ServerLevel sever = this.level.getServer().getLevel(Level.OVERWORLD);
			int dayTime = 24000;
			long day = sever.getDayTime() / dayTime;
			sever.setDayTime(12500 + (day * dayTime));

			if (sever.getLevelData() instanceof ServerLevelData worldInfo) {
				worldInfo.setRainTime(0);
				worldInfo.setThunderTime(0);
				worldInfo.setThundering(false);
				worldInfo.setRaining(false);
			}
		}
	}

	public void aiStep() {
		super.aiStep();
		if (this.deathTime > 0) { return; }

		LivingEntity target = this.getTarget();
		if (target == null) { return; }

		if (this.getY() < target.getY() || this.getY() < target.getY() + 2D) {
			Vec3 vec = this.getDeltaMovement();
			double y = Math.max(0D, vec.y);
			y *= 0.25D;
			y += (0.5D - y) * 0.25D;
			this.setDeltaMovement(new Vec3(vec.x, y, vec.z));
		}
	}

	protected void customServerAiStep() {
		super.customServerAiStep();
		LivingEntity target = this.getTarget();
		if (target == null) { return; }

		this.checkSpawnPos();
		this.getLookControl().setLookAt(target, 10F, 10F);
		this.firstAttack(target);

		if (this.isHalfHealth(this)) {
			this.secondAttack(target);
			this.checkPotion(PotionInit.reflash_effect, 9999, 0);
		}

		if (this.tickCount % 20 == 0) {
			List<LivingEntity> summonList = this.getSummonList();
			boolean isArmor = !summonList.isEmpty();
			this.setArmor(isArmor);

			if (isArmor && this.glowTime++ >= 20) {
				summonList.forEach(e -> this.addPotion(e, MobEffects.GLOWING, 99999, 0));
			}

			else if (!isArmor) {
				this.glowTime = 0;
			}
		}
	}

	public void firstAttack(LivingEntity target) {

		if (!this.getArmor() && this.summonTime++ >= MAX_SUMMON_TIME) {
			this.summonMob(target);
		}

		if (this.twilightTime++ >= MAX_TWILIGHT_TIME) {
			this.twilightShot(target);
		}

		if (this.summonTime == 300) {
			this.setSummon(true);
		}
	}

	public void secondAttack(LivingEntity target) {
		if (this.tickCount % 2 == 0) {
			this.twilightTime++;
		}
	}

	public void summonMob(LivingEntity target) {
		List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), 48D);
		if (targetList.isEmpty()) { return; }

		List<LivingEntity> summonList = this.getSummonList();
		int summonSize = summonList.isEmpty() ? 0 : summonList.size();

		boolean isHalf = this.isHalfHealth(this);
		float addHealth = 1F + targetList.size() * 0.05F;
		int count = Math.max(1, Math.min(16, 3 + targetList.size() - summonSize));
		Level world = this.level;

		for (int i = 0; i < count; i++) {

			double x = this.getX() + (this.rand.nextDouble() - 0.5D) * 20D;
			double z = this.getZ() + (this.rand.nextDouble() - 0.5D) * 20D;
			int setPosCount = 0;
			BlockPos pos = this.blockPosition();
			BlockPos summonPos = new BlockPos(x, this.getY() + 1D, z);

			while (!world.getBlockState(summonPos).isAir() && !world.getBlockState(summonPos).is(BlockInit.rune_character)) {

				summonPos = new BlockPos(pos.getX() + this.getRand(this.rand, 20), pos.getY(), pos.getZ() + this.getRand(this.rand, 20));

				if (setPosCount++ >= 16) { break; }
			}

			Mob entity = this.getEntity(addHealth, isHalf, i % 4 == 1);
			entity.setPos(summonPos.getX() + 0.5D, summonPos.getY() + 0.5D, summonPos.getZ() + 0.5D);
			entity.setTarget(targetList.get(this.rand.nextInt(targetList.size())));
			this.level.addFreshEntity(entity);
		}

		this.summonTime = 0;
		this.setSummon(false);
		this.playSound(SoundInit.HORAMAGIC, 0.2F, 1F);
	}

	public void twilightShot(LivingEntity target) {

		List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer(target)), 48D);

		for (LivingEntity entity : targetList) {

			double x = entity.getX() - this.getX();
			double y = entity.getY(0.3333333333333333D) - this.getY();
			double z = entity.getZ() - this.getZ();
			double xz = Math.sqrt(x * x + z * z);

			TwiLightShot magic = new TwiLightShot(this.level, this);
			magic.shoot(x, y - xz * 0.065D, z, 1.5F, 0.75F);
			magic.setAddDamage(magic.getAddDamage() + 10F);
			this.level.addFreshEntity(magic);
		}

		this.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);
		this.twilightTime = 0;
	}

	public Mob getEntity(float addHealth, boolean isHalf, boolean isBig) {
		Mob entity = null;

		if (isBig) {
			switch (this.rand.nextInt(5)) {
			case 0:
				entity = new SkullFrostRoyalGuard(this.level);
				entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
				entity.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
				entity.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
				break;
			case 1:
				entity = new SkullFlameArcher(this.level);
				entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
				entity.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
				entity.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
				break;
			case 2:
				entity = new BlazeTempestTornado(this.level);
				break;
			case 3:
				entity = new EnderShadow(this.level);
				entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
				this.addPotion(entity, MobEffects.MOVEMENT_SLOWDOWN, 99999, 0);
				break;
			case 4:
				entity = new DwarfZombieMaster(this.level);
				entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ItemInit.alt_sword));
				this.addPotion(entity, MobEffects.MOVEMENT_SLOWDOWN, 99999, 1);
				break;
			}

			this.addPotion(entity, MobEffects.DAMAGE_BOOST, 99999, 3);
		}

		else {
			switch (this.rand.nextInt(8)) {
			case 0:
				entity = new BlazeTempest(this.level);
				break;
			case 1:
				entity = new EnderMage(this.level);
				entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
				break;
			case 2:
				entity = new SkullFrost(this.level);
				entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
				break;
			case 3:
				entity = new ArchSpider(this.level);
				break;
			case 4:
				entity = new SkullFlame(this.level);
				entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
				break;
			case 5:
				entity = new DwarfZombie(this.level);
				entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ItemInit.alt_pick));
				break;
			case 6:
				entity = new CreeperCalamity(this.level);
				break;
			case 7:
				entity = new WindWitch(this.level);
				break;
			}
		}

		addHealth = isHalf ? addHealth * 1.325F : addHealth;
		entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(Math.min(256D, entity.getMaxHealth() * addHealth));
		entity.setHealth(entity.getMaxHealth());
		this.addPotion(entity, PotionInit.resistance_blow, 99999, 5);
		this.addPotion(entity, PotionInit.darkness_fog, 99999, 0);
		return entity;
	}

	protected void tickDeath() {

		this.deathTime++;

		if (this.deathTime >= 10 && this.deathTime <= 300) {

			if (!this.level.isClientSide) {
				ServerLevel world = this.level.getServer().getLevel(Level.OVERWORLD);
				world.setDayTime(world.getDayTime() + 36);
			}

			if (this.deathTime % 11 == 0) {
				this.playSound(SoundEvents.GENERIC_EXPLODE, 3F, 1F / (this.rand.nextFloat() * 0.2F + 0.9F));
			}

			if (this.deathTime % 5 == 0 && this.level instanceof ServerLevel sever) {
				sever.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY() + 0.5D, this.getZ(), 2, 0D, 0D, 0D, 0D);
			}
		}

		if (this.deathTime >= 300 && !this.level.isClientSide()) {
			this.level.broadcastEntityEvent(this, (byte) 60);
			this.remove(Entity.RemovalReason.KILLED);
			List<LivingEntity> targetList = this.getSummonList();
			targetList.forEach(e -> e.setHealth(0F));
		}

		this.getBossEvent().setProgress(0F);
	}

	public List<LivingEntity> getSummonList() {
		return this.getEntityList(LivingEntity.class, e -> e.hasEffect(PotionInit.darkness_fog), 64D);
	}

	protected int getRand(Random rand, int range) {
		return rand.nextInt(range) - rand.nextInt(range);
	}

	@Override
	public boolean isArmorEmpty() {
		return true;
	}

	@Override
	public void clearInfo() {
		this.tickTime = 0;
	}
}
