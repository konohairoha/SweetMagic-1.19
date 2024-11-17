package sweetmagic.init.entity.monster;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
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
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ItemInit;
import sweetmagic.util.SMDamage;

public class DwarfZombieMaster extends AbstractSMMob {

	private List<BlockPos> posList = new ArrayList <>();
	private static final EntityDataAccessor<Boolean> ISSUMMON = ISMMob.setData(DwarfZombieMaster.class, BOOLEAN);
	private static final EntityDataAccessor<Boolean> ISHALFHEALTH = ISMMob.setData(DwarfZombieMaster.class, BOOLEAN);

	public DwarfZombieMaster(Level world) {
		super(EntityInit.dwarfZombieMaster, world);
	}

	public DwarfZombieMaster(EntityType<DwarfZombieMaster> enType, Level world) {
		super(enType, world);
		this.xpReward = 150;
		this.maxUpStep = 1.25F;
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ISSUMMON, false);
		this.entityData.define(ISHALFHEALTH, false);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.5D, false));
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
				.add(Attributes.MAX_HEALTH, 150D)
				.add(Attributes.MOVEMENT_SPEED, 0.33D)
				.add(Attributes.ATTACK_DAMAGE, 5D)
				.add(Attributes.ARMOR, 6D)
				.add(Attributes.FOLLOW_RANGE, 32D);
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
		Entity attackEntity = src.getDirectEntity();

		if ( attacker != null && attacker instanceof ISMMob) {
			return false;
		}

		if (this.notMagicDamage(attacker, attackEntity)) {
			attacker.hurt(SMDamage.magicDamage, amount);
			attacker.invulnerableTime = 0;
			return false;
		}

		// ダメージ倍処理
		if (!this.isLeader(this)) {
			amount = this.getBossDamageAmount(this.level, this.defTime , src, amount, 10F);
			this.defTime = 2;
		}

		return super.hurt(src, amount);
	}

	protected void customServerAiStep() {

		super.customServerAiStep();

		LivingEntity target = this.getTarget();
		if (target == null || this.tickCount % 100 != 0 || this.isLeader(this)) { return; }

		if (!this.getSummon()) {

			for (int i = 0; i < 3; i++) {
				DwarfZombie entity = new DwarfZombie(this.level);
				entity.setPos(this.getX() + 0.5D, this.getY() + 0.5D, this.getZ() + 0.5D);
				entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getMaxHealth() / 5F);
				entity.setHealth(entity.getMaxHealth());
				entity.setOwnerID(this.getUUID());
				entity.setSummon(true);
				entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
				this.level.addFreshEntity(entity);
				entity.teleport();
				entity.spawnAnim();
	            this.armorDropChances[EquipmentSlot.MAINHAND.getIndex()] = 0F;
			}

			this.setSummon(true);
		}

		if (this.isHalfHealth(this) && !this.isHalfHealth()) {

			int count = 0;
			List<BlockPos> posList = this.getPosList();

			for (BlockPos pos : posList) {
				DwarfZombie entity = new DwarfZombie(this.level);
				entity.setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
				entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getMaxHealth() / 10F);
				entity.setHealth(entity.getMaxHealth());
				entity.spawnAnim();
				entity.setOwnerID(this.getUUID());
				entity.setSummon(true);
				entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
				this.level.addFreshEntity(entity);
				entity.spawnAnim();
	            this.armorDropChances[EquipmentSlot.MAINHAND.getIndex()] = 0F;

				if (count++ >= 3) { break; }
			}

			this.setHalfHealth(true);
			posList.clear();
		}
	}

	public boolean doHurtTarget(Entity entity) {
		boolean flag = super.doHurtTarget(entity);

		float baseDamae = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);

		if (flag && entity instanceof Warden target) {
			float dame = baseDamae + EnchantmentHelper.getDamageBonus(this.getMainHandItem(), target.getMobType());
			entity.hurt(this.getSRC(), dame * 2F);
		}

		entity.invulnerableTime = 0;

		// 向き先に座標を設定
		Vec3 look = this.getViewVector(1F);
		Vec3 dest = new Vec3(this.getX(), this.getY(), this.getZ()).add(0, this.getEyeHeight(), 0).add(look.x * 2, look.y * 2, look.z * 2);
		BlockPos pos = new BlockPos(dest.x, this.getY(), dest.z);

		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this, this.getFilter(this.isPlayer(entity)), pos, 2D);

		for (LivingEntity target : entityList) {
			if (target.is(entity)) { continue; }
			target.hurt(this.getSRC(), baseDamae * 0.25F);
			target.invulnerableTime = 0;
		}

		Vec3 vec3 = new Vec3(this.getX() - entity.getX(), 0.2D, this.getZ() - entity.getZ()).scale(2D);
		this.setDeltaMovement(this.getDeltaMovement().add(vec3));

		return flag;
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putBoolean("isSummon", this.getSummon());
		tags.putBoolean("isHalfHealth", this.isHalfHealth());
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setSummon(tags.getBoolean("isSummon"));
		this.setHalfHealth(tags.getBoolean("isHalfHealth"));
	}

	public boolean getSummon () {
		return this.entityData.get(ISSUMMON);
	}

	public void setSummon (boolean summonCount) {
		this.entityData.set(ISSUMMON, summonCount);
	}

	public boolean isHalfHealth () {
		return this.entityData.get(ISHALFHEALTH);
	}

	public void setHalfHealth (boolean isHalfHealth) {
		this.entityData.set(ISHALFHEALTH, isHalfHealth);
	}

	public List<BlockPos> getPosList () {
		return this.posList;
	}

	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance dif, MobSpawnType spawn, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		data = super.finalizeSpawn(world, dif, spawn, data, tag);
		this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ItemInit.alt_sword));
		return data;
	}

	// 低ランクかどうか
	public boolean isLowRank () {
		return false;
	}

	public MobType getMobType() {
		return MobType.UNDEAD;
	}
}
