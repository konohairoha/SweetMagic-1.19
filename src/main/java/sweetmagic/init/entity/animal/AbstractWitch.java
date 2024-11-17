package sweetmagic.init.entity.animal;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.GolemRandomStrollInVillageGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import sweetmagic.init.EntityInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.entity.projectile.AbstractMagicShot;

public abstract class AbstractWitch extends AbstractSummonMob {

	protected int coolTime = 0;
	protected int recastTime = 0;
	protected int damageCoolTime = 0;

	public AbstractWitch(Level world) {
		super(EntityInit.witchGolem, world);
	}

	public AbstractWitch(EntityType<? extends AbstractSummonMob> eType, Level world) {
		super(eType, world);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 30D)
				.add(Attributes.MOVEMENT_SPEED, 0.25D)
				.add(Attributes.ATTACK_DAMAGE, 4D)
				.add(Attributes.ARMOR, 0.5D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
				.add(Attributes.FOLLOW_RANGE, 48D);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
		this.goalSelector.addGoal(4, new GolemRandomStrollInVillageGoal(this, 0.6D));
		this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1D, 10F, 2F, false));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Monster.class, 8F));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.targetSelector.addGoal(1, new SMOwnerHurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new SMOwnerHurtTargetGoal(this));
		this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setAlertOthers());
		this.targetSelector.addGoal(4, new NearestAttackSMMobGoal<>(this, Monster.class, false));
		this.targetSelector.addGoal(5, new AttackTargetGoal<>(this, Raider.class, false));
		this.targetSelector.addGoal(6, new AttackTargetGoal<>(this, Warden.class, false));
		this.targetSelector.addGoal(7, new AttackTargetGoal<>(this, AbstractSkeleton.class, false));
	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.WITCH_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource src) {
		return SoundEvents.WITCH_HURT;
	}

	protected float getSoundVolume() {
		return 0.4F;
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putInt("coolTime", this.coolTime);
		tags.putInt("recastTime", this.recastTime);
		tags.putInt("damageCoolTime", this.damageCoolTime);
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.coolTime = tags.getInt("coolTime");
		this.recastTime = tags.getInt("recastTime");
		this.damageCoolTime = tags.getInt("damageCoolTime");
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {
		this.damageCoolTime = 400;
		return super.hurt(src, amount);
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

	public void tick() {

		super.tick();
		if (this.damageCoolTime > 0) { this.damageCoolTime--; }

		// 体力最大以外かつ最後にダメージを受けたのが一定時間たてば
		if (this.tickCount % 80 == 0 && this.getMaxHealth() > this.getHealth() && this.damageCoolTime <= 0) {
			this.setHealth(this.getHealth() + 2);
		}

		this.addPotion();
	}

	protected void customServerAiStep() {

		super.customServerAiStep();

		if (this.recastTime > 0) {
			this.recastTime--;
		}

		LivingEntity target = this.getTarget();
		if (target == null || this.getShit()) { return; }

		this.getLookControl().setLookAt(target, 10.0F, 10.0F);
		if (this.recastTime > 0) { return; }

		boolean isWarden = target instanceof Warden;
		this.recastTime = this.getRecastTime();
		float dama = this.getPower(this.getWandLevel()) + (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.33F;
		float dameRate = isWarden ? 4F : this.getDamageRate();

		double x = target.getX() - this.getX();
		double y = target.getY(0.3333333333333333D) - this.getY();
		double z = target.getZ() - this.getZ();
		double xz = Math.sqrt(x * x + z * z);
		int level = isWarden ? this.getWandLevel() + 20 : this.getWandLevel();

		if (!isWarden && this.isBoss(target)) {
			dama /= 5;
		}

		AbstractMagicShot entity = this.getMagicShot(target, isWarden);
		entity.setData(1);
		entity.setRange(4.5D + this.getRange());
		entity.setWandLevel(level);
		entity.shoot(x, y - xz * 0.035D, z, 2.25F, 0F);
		entity.setAddDamage( (entity.getAddDamage() + dama) * dameRate );
		this.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);
		this.level.addFreshEntity(entity);
	}

	public int getRecastTime () {
		return 150;
	}

	public int getWandLevel () {
		return 10;
	}

	public float getDamageRate () {
		return 1F;
	}

	public void addPotion () {

		if (this.level.isClientSide || this.getTarget() == null) { return; }

		if (this.coolTime > 0) {
			this.coolTime--;

			// クールタイムが終わってないなら
			if (this.coolTime > 0) { return; }
		}

		// 体力半分以下なら
		if (this.getHealth() <= this.getMaxHealth() / 2F) {

			// エーテルバリアー
			if (!this.hasEffect(PotionInit.aether_barrier)) {
				this.addPotion(this, PotionInit.aether_barrier, 400, 2);
				this.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1F, 1.175F);
				this.coolTime += 200;
				return;
			}

			// switch文で回復量を変える予定
			this.setHealth(this.getHealth() + this.getHealValue());
			this.coolTime += 200;
			this.playSound(SoundInit.HEAL, 0.0625F, 1F);

			if (this.level instanceof ServerLevel server) {
				this.spawnParticleRing(server, ParticleTypes.HAPPY_VILLAGER, 0.75D, this.blockPosition(), 1D, 0.1D, 0D);
			}

			return;
		}

		// 体力半分以上
		else {

			int time = 400;
			int coolTime = 300;

			switch (this.rand.nextInt(4)) {
			case 0:
				// リフレッシュ・エフェクト
				if (!this.hasEffect(PotionInit.reflash_effect)) {
					this.addPotion(this, PotionInit.reflash_effect, time, 0);
					this.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1F, 1.175F);
					this.coolTime += coolTime * 1.25F;
				}
				return;
			case 1:
				// ルナッティクムーン
				if (!this.hasEffect(PotionInit.magic_damage_cause)) {
					this.addPotion(this, PotionInit.magic_damage_cause, time, 4);
					this.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1F, 1.175F);
					this.coolTime += coolTime;
				}
				return;
			case 2:
				// エーテルバリアー
				if (!this.hasEffect(PotionInit.aether_barrier)) {
					this.addPotion(this, PotionInit.aether_barrier, time, 1);
					this.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1F, 1.175F);
					this.coolTime += coolTime;
				}
				return;
			case 3:
				// 攻撃力強化
				if (!this.hasEffect(MobEffects.DAMAGE_BOOST)) {
					this.addPotion(this, MobEffects.DAMAGE_BOOST, time, 1);
					this.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1F, 1.175F);
					this.coolTime += coolTime;
				}
				return;
			}
		}
	}

	// 魔法攻撃
	public abstract AbstractMagicShot getMagicShot (LivingEntity target, boolean isWarden);

	// 回復量
	public abstract float getHealValue ();

	// 杖の取得
	public abstract ItemStack getStack();

	@Override
	public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob mob) {
		return null;
	}
}