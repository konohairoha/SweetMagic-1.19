package sweetmagic.init.entity.projectile;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.IMagicBook;
import sweetmagic.api.iitem.IPorch;
import sweetmagic.api.iitem.IWand;
import sweetmagic.api.iitem.info.BookInfo;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.BlockInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.TagInit;
import sweetmagic.init.block.sm.MirageGlass;
import sweetmagic.init.entity.animal.AbstractSummonMob;
import sweetmagic.util.PlayerHelper;
import sweetmagic.util.SMDamage;
import sweetmagic.util.WorldHelper;

public abstract class AbstractMagicShot extends Projectile {

	@Nullable
	private BlockState lastState;				// ブロックステート
	public int wandLevel = 1;					// 杖レベル
	public int lifeTime;						// 生存時間
	public int maxLifeTime = 50;				// 最大生存時間
	public int tickTime = 0;					// 継続時間
	public int shakeTime;						// 発射から被弾しない時間
	public int knockback = 1;					// ノックバック
	public int addAttack = 0;					// 追加攻撃
	public int maxBreak = 0;					// 最大破壊回数
	public float baseDamage = 2F;				// 基本ダメージ
	public float addDamage = 0F;				// 追加ダメージ
	public boolean inGround;					// 地面についているかどうか
	private boolean isHitDead = true;			// えんちちー着弾時に弾を消滅させるか
	public boolean isCritical = false;			// クリティカルかどうか
	public boolean isPlayerThrower = true;		// 射撃者がプレイヤー
	public boolean isBlockPenetration = false;	// ブロック貫通
	public boolean isNotDamage = false;			// 戦闘ダメージ無効化
	public boolean isSilk = false;				// シルクタッチ
	public IWand wand;							// 杖
	public CompoundTag tags;					// NBT
	public ItemStack stack = ItemStack.EMPTY;	// アイテムスタック
	public Random rand = new Random();			// 乱数

	private static final EntityDataAccessor<Boolean> ISCHANGETPARTICLE = setEntityData(EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> DATA = setEntityData(EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> TIER = setEntityData(EntityDataSerializers.INT);
	private static final EntityDataAccessor<Float> RANGE = setEntityData(EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> SHOTSPEED = setEntityData(EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Boolean> ISARROW = setEntityData(EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> ISHOURGLASS = setEntityData(EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> ISWARRIOR_CARD = setEntityData(EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> ISQUILLPEN = setEntityData(EntityDataSerializers.BOOLEAN);

	public AbstractMagicShot(EntityType<? extends AbstractMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public AbstractMagicShot(EntityType<? extends AbstractMagicShot> entityType, double x, double y, double z, Level world) {
		this(entityType, world);
		this.setPos(x, y, z);
	}

	public AbstractMagicShot(EntityType<? extends AbstractMagicShot> entityType, LivingEntity entity, Level world) {
		this(entityType, entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
	}

	public static <T> EntityDataAccessor<T> setEntityData(EntityDataSerializer<T> seria) {
		return SynchedEntityData.defineId(AbstractMagicShot.class, seria);
	}

	public boolean shouldRenderAtSqrDistance(double dis) {

		double d0 = this.getBoundingBox().getSize() * 10D;
		if (Double.isNaN(d0)) { d0 = 1.0D; }

		d0 *= 64.0D * getViewScale();
		return dis < d0 * d0;
	}

	protected void defineSynchedData() {
		this.entityData.define(ISCHANGETPARTICLE, false);
		this.entityData.define(DATA, 0);
		this.entityData.define(TIER, 1);
		this.entityData.define(RANGE, 0F);
		this.entityData.define(SHOTSPEED, 1F);
		this.entityData.define(ISARROW, false);
		this.entityData.define(ISHOURGLASS, false);
		this.entityData.define(ISWARRIOR_CARD, false);
		this.entityData.define(ISQUILLPEN, false);
	}

	public void shoot(double x, double y, double z, float vec, float inaccuracy) {
		super.shoot(x, y, z, vec, inaccuracy);
		this.setLifeTime(0);
	}

	public void tick() {
		super.tick();
		Vec3 vec3 = this.getDeltaMovement();

		if (this.xRotO == 0F && this.yRotO == 0F) {
			double d0 = vec3.horizontalDistance();
			this.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)));
			this.setXRot((float) (Mth.atan2(vec3.y, d0) * (double) (180F / (float) Math.PI)));
			this.yRotO = this.getYRot();
			this.xRotO = this.getXRot();
		}

		BlockPos pos = this.blockPosition();
		BlockState state = this.level.getBlockState(pos);

		// ブロックの中にいる行うなら
		if (!state.isAir() && !this.getBlockPenetration()) {

			VoxelShape voxel = state.getCollisionShape(this.level, pos);

			// 当たり判定があるなら
			if (!voxel.isEmpty()) {

				Vec3 vec31 = this.position();

				// 地面着弾判定
				for (AABB aabb : voxel.toAabbs()) {
					if (!aabb.move(pos).contains(vec31)) { continue; }

					this.inGround = true;
					break;
				}
			}
		}

		// 被弾無効時間の減少
		if (this.shakeTime > 0) {
			--this.shakeTime;
		}

		++this.lifeTime;

		if (!this.level.isClientSide) {
			this.tickDespawn();
		}

		// 特定条件の場合火を消す
		if (this.isInWaterOrRain() || state.is(Blocks.POWDER_SNOW) || this.isInFluidType((fluidType, height) -> this.canFluidExtinguish(fluidType))) {
			this.clearFire();
		}

		// パーティクルをスポーンするメソッド
		if (this.level.isClientSide && this.tickCount >= this.getMinParticleTick()) {
			this.spawnParticle();
		}

		// 地面についている場合
		if (this.inGround) {
			this.inGround();
		}

		// 地面についていない場合
		else {

			Vec3 vec32 = this.position();
			Vec3 vec33 = vec32.add(vec3);
			HitResult result = this.level.clip(new ClipContext(vec32, vec33, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

			if (result.getType() != HitResult.Type.MISS) {
				vec33 = result.getLocation();
			}

			while (!this.isRemoved()) {

				EntityHitResult entityHit = this.findHitEntity(vec32, vec33);
				if (entityHit != null) {
					result = entityHit;
				}

				if (result != null && result.getType() == HitResult.Type.ENTITY) {
					Entity entity = ((EntityHitResult) result).getEntity();
					Entity entity1 = this.getOwner();
					if (entity instanceof Player && entity1 instanceof Player && !((Player) entity1).canHarmPlayer((Player) entity)) {
						result = null;
						entityHit = null;
					}

					else if (this.canHitTarget(entity) && this.canHitTarget(entity1)) {
						result = null;
						entityHit = null;
					}
				}

				// 何かしらに着弾
				if (result != null && result.getType() != HitResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, result)) {
					this.onHit(result);
					this.hasImpulse = true;
					break;
				}

				// えんちちーにヒットできなければ終了
				if (entityHit == null) { break; }

				result = null;
			}

			vec3 = this.getDeltaMovement();
			double d5 = vec3.x;
			double d6 = vec3.y;
			double d1 = vec3.z;
			double d7 = this.getX() + d5;
			double d2 = this.getY() + d6;
			double d3 = this.getZ() + d1;
			double d4 = vec3.horizontalDistance();

			this.setYRot((float) (Mth.atan2(d5, d1) * (double) (180F / (float) Math.PI)));
			this.setXRot((float) (Mth.atan2(d6, d4) * (double) (180F / (float) Math.PI)));
			this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
			this.setYRot(lerpRotation(this.yRotO, this.getYRot()));

			// 水中にいる場合
			if (this.isInWater()) { }

			this.setPos(d7, d2, d3);
			this.checkInsideBlocks();

			if (this.getArrow()) {
				Vec3 vec = this.getDeltaMovement().scale(0.99D);
				this.setDeltaMovement(vec.x, vec.y - 0.02D, vec.z);
			}
		}
	}

	protected void onHit(HitResult hit) {

		HitResult.Type type = hit.getType();

		if (type == HitResult.Type.ENTITY) {
			this.onHitEntity((EntityHitResult) hit);
		}

		else if (type == HitResult.Type.BLOCK) {

			// ブロック貫通しないならブロック着弾処理
			if (!this.getBlockPenetration() || this instanceof DigMagicShot) {

				BlockHitResult result = (BlockHitResult) hit;
				BlockPos pos = result.getBlockPos();
				BlockState state = this.level.getBlockState(pos);

				if (this.getArrow()) {
					this.setLifeTime(0);
					this.setMaxLifeTime(80);

					if (!this.level.isClientSide) {
						this.playSound(SoundEvents.ARROW_HIT, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
					}

					if (result.getDirection() == Direction.UP) {
						this.setPos(this.getX(), this.getY() + 0.25D, this.getZ());
					}

					return;
				}

				if (state.is(BlockInit.mirage_wall_glass) && !state.getValue(MirageGlass.ISVIEW)) {
					MirageGlass mirage = (MirageGlass) state.getBlock();
					mirage.setValue(this.level, pos);
				}

				this.onHitBlock(result);

				if (this.tickCount <= this.getMinParticleTick() && this.level instanceof ServerLevel sever) {
					this.spawnParticleShort(sever, result.getBlockPos().relative(result.getDirection()));
				}

				this.level.gameEvent(GameEvent.PROJECTILE_LAND, pos, GameEvent.Context.of(this, this.level.getBlockState(pos)));
			}
		}
	}

	// パーティクルスポーン
	protected void spawnParticle() {}

	// パーティクルスポーン
	protected void spawnParticleShort(ServerLevel sever, BlockPos pos) {}

	// パーティクルスポーン
	protected void spawnParticleRing(ServerLevel server, ParticleOptions par, double range, BlockPos pos, double ySpeed, double moveValue) {

		double x = pos.getX() + 0.5D;
		double y = pos.getY() + 0.5D;
		double z = pos.getZ() + 0.5D;

		for (double degree = 0D; degree < range * Math.PI; degree += 0.1D) {
			double rate = range;
			server.sendParticles(par, x + Math.cos(degree) * rate, y, z + Math.sin(degree) * rate, 0, -Math.cos(degree), ySpeed, -Math.sin(degree), moveValue);
		}
	}

	// パーティクルスポーン
	public void spawnParticleRing(ServerLevel server, ParticleOptions par, double range, BlockPos pos, double addY) {

		double x = pos.getX() + 0.5D;
		double y = pos.getY() + 1D + addY;
		double z = pos.getZ() + 0.5D;

		for (double degree = -range * Math.PI; degree < range * Math.PI; degree += 0.25D) {
			double rate = range * 0.75D;
			server.sendParticles(par, x + Math.cos(degree) * rate, y, z + Math.sin(degree) * rate, 0, Math.cos(degree) * 0.65D, 0, Math.sin(degree) * 0.65D, -0.67D);
		}
	}

	// パーティクルスポーン
	public void spawnParticleRingY(ServerLevel server, ParticleOptions par, double range, BlockPos pos, double speed, double addY) {

		double x = pos.getX() + 0.5D;
		double y = pos.getY() + 0.5D;
		double z = pos.getZ() + 0.5D;

		for (double degree = -range * Math.PI; degree < range * Math.PI; degree += 0.25D) {
			double rate = range * 0.75D;
			server.sendParticles(par, x + Math.cos(degree) * rate, y, z + Math.sin(degree) * rate, 0, Math.cos(degree) * speed, addY, Math.sin(degree) * speed, 1D);
		}
	}

	public BlockParticleOption getParticle(BlockState state) {
		return new BlockParticleOption(ParticleTypes.BLOCK, state);
	}

	// 時間経過
	protected void tickDespawn() {

		// 最大生存時間を超えたらエンティティを削除
		if (this.getLifeTime() >= this.getMaxLifeTime()) {
			this.despawnAction();
			this.discard();
		}
	}

	// デスポーン時効果
	public void despawnAction() { }

	public void shootFromRotation(Entity entity, float x, float y, float z, float shotSpeed, float par1) {
		float pi = (float) Math.PI / 180F;
		float f = -Mth.sin(y * pi) * Mth.cos(x * pi);
		float f1 = -Mth.sin((x + z) * pi);
		float f2 = Mth.cos(y * pi) * Mth.cos(x * pi);
		this.shoot((double) f, (double) f1, (double) f2, shotSpeed, par1);
		this.setShotSpeed(shotSpeed);
	}

	// エンティティ被弾時
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		if (this.getNotDamage()) { return; }

		Entity target = result.getEntity();
		Entity attacker = this.getOwner();
		float dame = attacker instanceof Player && target instanceof Player ? (float) this.getDamage() * 0.00001F : (float) this.getDamage();

		// エンダーマン以外
		if (!(target instanceof EnderMan) && !(target instanceof Witch)) {

			DamageSource src = this.damageSource();

			// LivingEntityの場合
			if (target instanceof LivingEntity living) {

				// ノックバック
				this.onKnockBack(living);

				if (!this.level.isClientSide && attacker instanceof LivingEntity attackkerLiv) {

					try {
						EnchantmentHelper.doPostHurtEffects(living, attacker);
						EnchantmentHelper.doPostDamageEffects(attackkerLiv, living);
						attackkerLiv.setLastHurtByMob(living);
					}

					catch (Throwable e) { }
				}

				// ウォーデンならダメージ増加
				dame = target instanceof Warden ? dame * 4F : dame;

				// ターゲットに攻撃
				if (target.hurt(src, dame)) {

					this.entityHit(living);
					living.invulnerableTime = 0;

					if (attacker instanceof LivingEntity entity) {
						this.vulnerableAttack(living, entity, dame);
						this.addAttackEntity(living, entity, dame, this.getAddAttack());
					}

					if (this.tickCount <= this.getMinParticleTick() && this.level instanceof ServerLevel sever) {
						this.spawnParticleShort(sever, this.blockPosition());
					}
				}

				// プレイヤー同士の被弾なら
				if (living instanceof Player && attacker instanceof ServerPlayer server && !this.isSilent()) {
					server.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0F));
				}
			}

			// LivingEntity以外で攻撃したのがプレイヤーなら
			else if (attacker instanceof Player player) {

				if (target.hurt(DamageSource.playerAttack(player), dame) ) {
					target.invulnerableTime = 0;
					this.addAttackEntity(target, player, dame, this.getAddAttack());
				}
			}
		}

		// エンダーマンの場合
		else {

			DamageSource src = null;

			// ノックバック
			this.onKnockBack((LivingEntity) target);

			// プレイヤーの攻撃の場合
			if (attacker instanceof Player player) {
				src = DamageSource.playerAttack(player);
			}

			// モブの攻撃の場合
			else if (attacker instanceof LivingEntity entity) {
				src = DamageSource.mobAttack(entity);
			}

			if (src != null) {

				LivingEntity living = (LivingEntity) target;
				target.hurt(src, dame);
				this.entityHit(living);

				if (this.tickCount <= this.getMinParticleTick() && this.level instanceof ServerLevel sever) {
					this.spawnParticleShort(sever, this.blockPosition());
				}

				living.invulnerableTime = 0;

				if (attacker instanceof LivingEntity entity) {
					this.vulnerableAttack(living, entity, dame);
					living.setLastHurtByMob(living);
				}

				this.addAttack(living, dame, this.getAddAttack());
			}
		}

		// クリティカル発生時
		if (this.getCritical()) {
			target.playSound(SoundInit.CRITICAL, 1F, 1F);

			if (this.level instanceof ServerLevel sever) {

				BlockPos pos = target.blockPosition();
				float x = (float) (target.xo + this.rand.nextFloat() * 0.5F);
				float y = (float) (pos.getY() + this.rand.nextFloat() * 0.5F + 0.5F);
				float z = (float) (target.zo + this.rand.nextFloat() * 0.5F);

				for (int i = 0; i < 16; i++) {
					sever.sendParticles(ParticleTypes.FIREWORK, x, y, z, 2, 0F, 0F, 0F, 0.1F);
				}
			}

			if (attacker instanceof Player player) {

				ItemStack leg = player.getItemBySlot(EquipmentSlot.LEGS);

				// 攻撃回数
				if (!leg.isEmpty() && leg.getItem() instanceof IPorch porch && porch.hasAcce(leg, ItemInit.fairy_wing)) {
					int attackCount = Math.max(1, 6 - (int) (this.getRange() / 3D) );
					this.addAttackEntity(target, player, dame, attackCount);
				}
			}
		}

		// 着弾時に消滅
		if (this.getHitDead() && !this.level.isClientSide) {
			this.discard();
		}
	}

	// 追加攻撃
	public void addAttack(LivingEntity entity, float dame, int addAttackCount) {
		if (addAttackCount <= 0) { return; }

		LivingEntity owner = this.getLiving();
		if (owner != null) {
			if (owner.hasEffect(PotionInit.arlaune_bless)) {
				addAttackCount++;
			}
		}

		float rate = this.getQuillpen() ? 0.5F : 0.1F;
		dame = this.isBoss(entity) ? dame * rate : dame * 0.25F;

		if (entity.hasEffect(PotionInit.flame) && this.getWarriorCard()) {
			dame *= 1.25F;
		}

		for (int i = 0; i < addAttackCount; i++) {
			this.acceAddAttack(entity, (LivingEntity) this.getOwner(), dame, true);
			this.attackDamage(entity, dame, true, true);
			this.vulnerableParticle(entity);
		}

		this.setAddAttack(0);
	}

	// 追加攻撃
	public void addAttackEntity(Entity target, LivingEntity attacker, float dame, int addAttackCount) {
		if (addAttackCount <= 0) { return; }

		float rate = this.getQuillpen() ? 0.5F : 0.1F;
		dame = this.isBoss(target) ? dame * rate : dame * 0.25F;

		if (target instanceof LivingEntity entity && entity.hasEffect(PotionInit.flame) && this.getWarriorCard()) {
			dame *= 1.25F;
		}

		for (int i = 0; i < addAttackCount; i++) {

			this.acceAddAttack(target, attacker, dame, false);
			if (target.hurt(SMDamage.getAddDamage(this, attacker), dame)) {
				target.invulnerableTime = 0;
			}
			this.vulnerableParticle(target);
		}

		this.setAddAttack(0);
	}

	public void acceAddAttack(Entity target, LivingEntity attacker, float dame, boolean isNotEnder) {
		if (this.rand.nextFloat() > 0.5F || attacker == null || !(attacker instanceof Player player)) { return; }

		ItemStack leg = attacker.getItemBySlot(EquipmentSlot.LEGS);
		if (!leg.isEmpty() && leg.getItem() instanceof IPorch porch && porch.hasAcce(leg, ItemInit.cherry_ornate_hairpin)) {

			DamageSource src = isNotEnder ? SMDamage.getAddDamage(this, attacker) : DamageSource.playerAttack(player);
			if (target.hurt(src, dame * 0.25F) ) {
				target.invulnerableTime = 0;
			}
		}
	}

	// ノックバック
	public void onKnockBack(LivingEntity living) {

		if (this.knockback > 0D) {
			float knock = (float) Math.max(0F, 1F - living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));

			LivingKnockBackEvent event = ForgeHooks.onLivingKnockBack(living, this.knockback * 0.6F * knock, 1F, 1F);

			if (!event.isCanceled()) {
				Vec3 vec3 = this.getDeltaMovement().multiply(event.getRatioX(), 0.01D, event.getRatioZ()).normalize().scale(event.getStrength());
				if (vec3.lengthSqr() > 0D) {
					living.push(vec3.x, 0.1D, vec3.z);
				}
			}
		}
	}

	public void attackDamage(LivingEntity target, float dame, boolean isExtend) {
		this.attackDamage(target, dame, isExtend, false);
	}

	// ターゲットへ攻撃
	public void attackDamage(LivingEntity target, float dame, boolean isExtend, boolean isAddAttack) {

		// ノックバック
		if (!isExtend) {
			this.onKnockBack(target);
		}

		// 脆弱ダメージ
		this.vulnerableAttack(target, (LivingEntity) this.getOwner(), dame);

		// エンダーマン以外ならターゲットに攻撃
		if (!(target instanceof EnderMan) && !(target instanceof Witch)) {
			target.hurt(isAddAttack ? SMDamage.getAddDamage(this, this.getOwner()) : this.damageSource(), target instanceof Warden ? dame * 4F : dame);
			target.invulnerableTime = 0;
		}

		// エンダーマンの場合
		else {

			DamageSource src = null;
			LivingEntity attacker = (LivingEntity) this.getOwner();

			// プレイヤーの攻撃の場合
			if (attacker instanceof Player payer) {
				src = DamageSource.playerAttack(payer);
			}

			// モブの攻撃の場合
			else if (attacker instanceof Monster entity) {
				src = DamageSource.mobAttack(entity);
			}

			if (src != null) {
				target.hurt(src, dame);
				target.invulnerableTime = 0;
			}
		}
	}

	// えんちちーに当たった時の処理
	protected void entityHit(LivingEntity living) { }

	// ブロック着弾
	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);
		if (this.getBlockPenetration()) { return; }

		if (this.getHitDead() && !this.level.isClientSide) {
			this.discard();
		}
	}

	public void inGround() { }

	public boolean canHitTarget(Entity entity) {
		return entity instanceof Player || entity instanceof AbstractSummonMob;
	}

	@Nullable
	protected EntityHitResult findHitEntity(Vec3 vec1, Vec3 vec2) {
		return ProjectileUtil.getEntityHitResult(this.level, this, vec1, vec2, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1D), this::canHitEntity);
	}

	protected Entity.MovementEmission getMovementEmission() {
		return Entity.MovementEmission.NONE;
	}

	protected float getEyeHeight(Pose pose, EntityDimensions dime) {
		return 0.13F;
	}

	// 杖レベルの設定
	public void setWandLevel(int wandLevel) {
		this.wandLevel = wandLevel;
	}

	// 杖レベルの取得
	public int getWandLevel() {
		return this.wandLevel;
	}

	// 生存時間の設定
	public void setLifeTime(int lifeTime) {
		this.lifeTime = lifeTime;
	}

	// 生存時間の取得
	public int getLifeTime() {
		return this.lifeTime;
	}

	// 最大生存時間の設定
	public void setMaxLifeTime(int maxLifeTime) {
		this.maxLifeTime = maxLifeTime;
	}

	// 最大生存時間の取得
	public int getMaxLifeTime() {
		return this.maxLifeTime;
	}

	// 最低パーティクル描画時間
	public int getMinParticleTick() {
		return 0;
	}

	// 基本ダメージの設定
	protected void setBaseDamage(float damage) {
		this.baseDamage = damage;
	}

	// 基本ダメージの取得
	public float getBaseDamage() {
		return this.baseDamage;
	}

	// 追加ダメージの設定
	public void setAddDamage(float addDamage) {
		this.addDamage = addDamage;
	}

	// 追加ダメージの取得
	public float getAddDamage() {
		return this.addDamage;
	}

	// 範囲の設定
	public void setRange(double range) {
		this.entityData.set(RANGE, (float) range);
	}

	// 範囲の取得
	public double getRange() {
		return (double) this.entityData.get(RANGE);
	}

	// 射撃速度の設定
	public void setShotSpeed(float shotSpeed) {
		this.entityData.set(SHOTSPEED, shotSpeed);
	}

	// 射撃速度の取得
	public float getShotSpeed() {
		return this.entityData.get(SHOTSPEED);
	}

	// 追加攻撃回数の設定
	public void setAddAttack(int addAttack) {
		this.addAttack = addAttack;
	}

	// 追加攻撃回数の取得
	public int getAddAttack() {
		return this.addAttack;
	}

	// 最大破壊回数の設定
	public void setMaxBreak(int maxBreak) {
		this.maxBreak = maxBreak;
	}

	// 矢の取得
	public boolean getArrow() {
		return this.entityData.get(ISARROW);
	}

	// 矢の設定
	public void setArrow(boolean isArrow) {
		this.entityData.set(ISARROW, isArrow);
	}

	// 砂時計の取得
	public boolean getHourGlass() {
		return this.entityData.get(ISHOURGLASS);
	}

	// 砂時計の設定
	public void setHourGlass(boolean isGlass) {
		this.entityData.set(ISHOURGLASS, isGlass);
	}

	// 戦士のカードの取得
	public boolean getWarriorCard() {
		return this.entityData.get(ISWARRIOR_CARD);
	}

	// 戦士のカードの設定
	public void setWarriorCard(boolean isCard) {
		this.entityData.set(ISWARRIOR_CARD, isCard);
	}

	// 戦士のカードの取得
	public boolean getQuillpen() {
		return this.entityData.get(ISQUILLPEN);
	}

	// 戦士のカードの設定
	public void setQuillpen(boolean isCard) {
		this.entityData.set(ISQUILLPEN, isCard);
	}

	// 最大破壊回数の取得
	public int getMaxBreak() {
		return this.maxBreak;
	}

	// ブロック貫通の設定
	public void setBlockPenetration(boolean isBlockPenetration) {
		this.isBlockPenetration = isBlockPenetration;
	}

	// ブロック貫通の取得
	public boolean getBlockPenetration() {
		return this.isBlockPenetration;
	}

	// ダメージ無効化の設定
	public void setNotDamage(boolean isNotDamage) {
		this.isNotDamage = isNotDamage;
	}

	// ダメージ無効化の取得
	public boolean getNotDamage() {
		return this.isNotDamage;
	}

	// シルクタッチの設定
	public void setSilk(boolean isSilk) {
		this.isSilk = isSilk;
	}

	// シルクタッチの取得
	public boolean getSilk() {
		return this.isSilk;
	}

	// 通常と違うパーティクルの設定
	public void setChangeParticle(boolean isChangetParticle) {
		this.entityData.set(ISCHANGETPARTICLE, isChangetParticle);
	}

	// 通常と違うパーティクルの取得
	public boolean getChangeParticle() {
		return this.entityData.get(ISCHANGETPARTICLE);
	}

	// 着弾時消滅の設定
	public void setHitDead(boolean isHitDead) {
		this.isHitDead = isHitDead;
	}

	// 着弾時消滅の取得
	public boolean getHitDead() {
		return this.isHitDead;
	}

	// クリティカルの設定
	public void setCritical(boolean isCritical) {
		this.isCritical = isCritical;
	}

	// クリティカルの取得
	public boolean getCritical() {
		return this.isCritical;
	}

	// データ値の設定
	public void setData(int data) {
		this.entityData.set(DATA, data);
	}

	// データ値の取得
	public int getData() {
		return this.entityData.get(DATA);
	}

	// tierの取得
	public int getTier() {
		return this.entityData.get(TIER);
	}

	// tierの設定
	public void setTier(int tier) {
		this.entityData.set(TIER, tier);
	}

	// 射撃者がプレイヤーの設定
	public void setPlayerThrower(boolean isPlayerThrower) {
		this.isPlayerThrower = isPlayerThrower;
	}

	// 射撃者がプレイヤーの取得
	public boolean getPlayerThrower() {
		return this.isPlayerThrower;
	}

	// 杖の設定
	public void setWand(IWand wand) {
		this.wand = wand;
	}

	// 属性の取得
	public abstract SMElement getElement();

	// ダメージソース(誰が攻撃したかをわかるために)
	protected DamageSource damageSource() {
		Entity owner = this.getOwner();
		if (!(owner instanceof Monster entity)) { return SMDamage.getMagicDamage(this, owner); }

		return entity.getTarget() instanceof Player ? DamageSource.mobAttack(entity) : SMDamage.getMagicDamage(this, owner);
	}

	public int addPotion(LivingEntity entity, MobEffect potion, int time, int level) {

		if (this.isBoss(entity)) {

			if (!potion.equals(PotionInit.bleeding)) {
				time = time / 4;
				level = Math.max(0, level - 1);
			}

			else {
				time = time / 2;
			}

			time = time / 4;
			level = potion.equals(PotionInit.bleeding) ? level : Math.max(0, level - 1);

			if (potion.equals(PotionInit.bubble)) {
				time = Math.min(10 + 20 * level, time);
			}
		}

		PlayerHelper.setPotion(entity, potion, level, time);
		return time;
	}

	public boolean isBoss(Entity entity) {
		return entity.getType().is(TagInit.BOSS);
	}

	public boolean isNotSpecial(Entity entity) {
		return entity.getType().is(TagInit.NOT_SPECIAL);
	}

	/*
	 * =========================================================
	 * 				情報の設定と取得　Start
	 * =========================================================
	 */

	// 杖の取得
	public IWand getWand() {
		return this.wand;
	}

	// NBTの設定
	public void setNBT(CompoundTag tags) {
		this.tags = tags;
	}

	// NBTの取得
	public CompoundTag getNBT() {
		return this.tags;
	}

	// 杖情報の設定
	public void setWandInfo(WandInfo wandInfo) {
		this.setStack(wandInfo.getStack());
		this.setWandLevel(wandInfo.getLevel());
		this.setNBT(wandInfo.getNBT());
	}

	// アクセサリー効果発動
	public void acceEffect() {
		if (!(this.getOwner() instanceof Player player)) { return; }

		float chance = 0F;
		ItemStack leg = player.getItemBySlot(EquipmentSlot.LEGS);
		IPorch porch = IPorch.getPorch(leg);

		if (porch != null) {

			int bloodCount = porch.acceCount(leg, ItemInit.blood_sucking_ring, 5);
			if (bloodCount > 0 && player.getHealth() > 1 + bloodCount) {
				float addDameRate = 1F + bloodCount * 0.2F;
				this.setAddDamage(this.getAddDamage() * addDameRate);

				if (!player.isCreative()) {
					player.setHealth(Math.max(1F, player.getHealth() - bloodCount));
				}
			}

			if (porch.hasAcce(leg, ItemInit.wizard_gauntlet)) {

				if (this.getRange() > 0D) {
					this.setRange(this.getRange() * 1.125F);
				}

				if (this.getElement().is(SMElement.LIGHTNING)) {
					this.setAddAttack(this.getAddAttack() + 2);
					this.setAddDamage(this.getAddDamage() * 1.5F);
				}
			}

			int extensionCount = porch.acceCount(leg, ItemInit.extension_ring, 8);
			if (extensionCount > 0) {
				float addRangeRate = 1F + extensionCount * 0.125F;

				if (this.getRange() > 0D) {
					this.setRange(this.getRange() * addRangeRate);
				}

				else {
					this.setAddAttack(this.getAddAttack() + extensionCount);
				}

				if (this.getMaxBreak() > 0) {
					this.setMaxBreak(this.getMaxBreak() + extensionCount / 2);
				}
			}

			int frostChainCount = porch.acceCount(leg, ItemInit.frosted_chain, 1);
			if (frostChainCount > 0 && this.getElement().is(SMElement.FROST)) {
				float addDameRate = 1.25F;
				this.setAddDamage(this.getAddDamage() * addDameRate);

				List<Monster> entityList = this.getEntityList(Monster.class, 7.5D);
				entityList.forEach(e -> this.addPotion(e, PotionInit.frost, 200, 1));
				this.setAddAttack(this.getAddAttack() + 1);
			}

			int hollyCharmCount = porch.acceCount(leg, ItemInit.holly_charm, 1);
			if (hollyCharmCount > 0 && this.getElement().is(SMElement.SHINE)) {
				float addDameRate = 1.25F;
				this.setAddDamage(this.getAddDamage() * addDameRate);
				this.setAddAttack(this.getAddAttack() + 1);
			}

			int ignisSoulCount = porch.acceCount(leg, ItemInit.ignis_soul, 1);
			if (ignisSoulCount > 0 && this.getElement().is(SMElement.FLAME)) {
				float addDameRate = 1.25F;
				this.setAddDamage(this.getBaseDamage() * addDameRate);
				this.setAddAttack(this.getAddAttack() + 1);
			}

			if (porch.hasAcce(leg, ItemInit.warrior_card) && player.hasEffect(MobEffects.DAMAGE_BOOST)) {
				this.setAddAttack(this.getAddAttack() + player.getEffect(MobEffects.DAMAGE_BOOST).getAmplifier() + 1);
				this.setWarriorCard(true);
			}

			if (porch.hasAcce(leg, ItemInit.magician_quillpen)) {
				this.setQuillpen(true);
			}

			if (porch.hasAcce(leg, ItemInit.twilight_hourglass)) {
				this.setHourGlass(true);
			}

			int windReliefCount = porch.acceCount(leg, ItemInit.wind_relief, 1);
			if (windReliefCount > 0 && ( this instanceof CycloneMagicShot || (this instanceof MagicSquareMagic && this.getData() == 1) ) ) {

				float addDameRate = 1.25F;
				boolean isSquare = this instanceof MagicSquareMagic;

				if (!isSquare) {
					this.setAddDamage(this.getBaseDamage() * addDameRate);
					this.setAddAttack(this.getAddAttack() + 1);
				}

				List<Monster> entityList = this.getEntityList(Monster.class, 7.5D);
				float damage = isSquare ? 20 : this.getDamage();
				entityList.forEach(e -> this.attackDamage(e, damage * 0.25F, true));
			}

			if (porch.hasAcceIsActive(leg, ItemInit.earth_ruby_ring) && this instanceof DigMagicShot) {
				this.setSilk(true);
			}

			int fairyWingCount = porch.acceCount(leg, ItemInit.fairy_wing, 1);
			if (fairyWingCount > 0) {
				chance += 0.1F;
			}
		}

		List<ItemStack> stackBookList = IMagicBook.getBookList(player);

		if (!stackBookList.isEmpty()) {
			BookInfo info = new BookInfo(stackBookList.get(0));
			IMagicBook book = info.getBook();
			chance += book.getChance(book.getAttackPage(info));
		}

		if (chance > 0F && player.hasEffect(MobEffects.LUCK)) {
			chance += (player.getEffect(MobEffects.LUCK).getAmplifier() + 1) * 0.025F;
		}

		if (chance > 0F && chance >= this.rand.nextFloat()) {
			this.setCritical(true);
		}

		if (player.hasEffect(PotionInit.holy_bless)) {
			this.setRange(this.getRange() + 3F);
		}

		if (player.hasEffect(PotionInit.knight_bless)) {
			this.setAddDamage(this.getAddDamage() + 6F);
		}
	}

	// アイテムスタックの設定
	public void setStack(ItemStack stack) {
		this.stack = stack;
	}

	// アイテムスタックの取得
	public ItemStack getStack() {
		return this.stack;
	}

	public LivingEntity getLiving() {
		return (LivingEntity) this.getOwner();
	}

	// ダメージの取得
	protected float getDamage() {

		// 基礎ダメージの取得
		float damage = this.getBaseDamage();
		LivingEntity entity = this.getLiving();
		if (entity == null) { return damage; }

		// 攻撃力上昇
		if (entity.hasEffect(MobEffects.DAMAGE_BOOST)) {
			int level = entity.getEffect(MobEffects.DAMAGE_BOOST).getAmplifier() + 1;
			damage += (level * 2);
		}

		// 弱体化
		if (entity.hasEffect(MobEffects.WEAKNESS)) {
			int level = entity.getEffect(MobEffects.WEAKNESS).getAmplifier() + 1;
			damage -= level;
		}

		// 攻撃力上昇
		if (entity.hasEffect(PotionInit.blood_curse)) {
			int level = entity.getEffect(PotionInit.blood_curse).getAmplifier() + 1;
			damage += (level * 3);
		}

		// 攻撃力上昇
		if (entity.hasEffect(PotionInit.magic_damage_cause)) {
			int level = entity.getEffect(PotionInit.magic_damage_cause).getAmplifier() + 1;
			damage += level;
		}

		// ダメージ加算
		damage += !this.getCritical() ? this.getAddDamage() : this.getAddDamage() * 1.5F;
		return damage;
	}


	protected double wrap180Radian(double radian) {

		radian %= 2 * Math.PI;

		while (radian >= Math.PI) {
			radian -= 2 * Math.PI;
		}

		while (radian < -Math.PI) {
			radian += 2 * Math.PI;
		}

		return radian;
	}

	protected double clampAbs(double param, double maxMagnitude) {
		if (Math.abs(param) > maxMagnitude) {
			param =param < 0 ? -Math.abs(maxMagnitude) : Math.abs(maxMagnitude);
		}
		return param;
	}

	protected double angleBetween(Vec3 v1, Vec3 v2) {

		double vDot = v1.dot(v2) / (v1.length() * v2.length());

		if (vDot < -1D) {
			vDot = -1D;
		}

		if (vDot > 1D) {
			vDot = 1D;
		}

		return Math.acos(vDot);
	}

	protected Vec3 transform(Vec3 axis, double angle, Vec3 normal) {

		double m00 = 1D;
		double m01 = 0D;
		double m02 = 0D;

		double m10 = 0D;
		double m11 = 1D;
		double m12 = 0D;

		double m20 = 0D;
		double m21 = 0D;
		double m22 = 1D;
		double mag = Math.sqrt(axis.x * axis.x + axis.y * axis.y + axis.z * axis.z);

		if (mag >= 1.0E-10) {

			mag = 1D / mag;
			double ax = axis.x * mag;
			double ay = axis.y * mag;
			double az = axis.z * mag;

			double sinTheta = Math.sin(angle);
			double cosTheta = Math.cos(angle);
			double t = 1D - cosTheta;

			double xz = ax * az;
			double xy = ax * ay;
			double yz = ay * az;

			m00 = t * ax * ax + cosTheta;
			m01 = t * xy - sinTheta * az;
			m02 = t * xz + sinTheta * ay;

			m10 = t * xy + sinTheta * az;
			m11 = t * ay * ay + cosTheta;
			m12 = t * yz - sinTheta * ax;

			m20 = t * xz - sinTheta * ay;
			m21 = t * yz + sinTheta * ax;
			m22 = t * az * az + cosTheta;
		}

		return new Vec3(m00 * normal.x + m01 * normal.y + m02 * normal.z, m10 * normal.x + m11 * normal.y + m12 * normal.z, m20 * normal.x + m21 * normal.y + m22 * normal.z);
	}

	/*
	 * =========================================================
	 * 				情報の設定と取得　End
	 * =========================================================
	 */

	// NBTの書き込み
	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);

		if (this.lastState != null) {
			tags.put("inBlockState", NbtUtils.writeBlockState(this.lastState));
		}

		tags.putInt("data", this.getData());
		tags.putInt("tier", this.getTier());
		tags.putInt("wandLevel", this.getWandLevel());
		tags.putShort("lifeTime", (short) this.getLifeTime());
		tags.putShort("maxLifeTime", (short) this.getMaxLifeTime());
		tags.putInt("tickTime", this.tickTime);
		tags.putByte("shake", (byte) this.shakeTime);
		tags.putInt("knockback", this.knockback);
		tags.putInt("addAttack", this.addAttack);
		tags.putInt("maxBreak", this.maxBreak);
		tags.putFloat("baseDamage", this.getBaseDamage());
		tags.putFloat("addDamage", this.getAddDamage());
		tags.putDouble("range", this.getRange());
		tags.putFloat("shotSpeed", this.getShotSpeed());
		tags.putBoolean("inGround", this.inGround);
		tags.putBoolean("isHitDead", this.getHitDead());
		tags.putBoolean("isCritical", this.getCritical());
		tags.putBoolean("isPlayerThrower", this.getPlayerThrower());
		tags.putBoolean("isBlockPenetration", this.getBlockPenetration());
		tags.putBoolean("isNotDamage", this.getNotDamage());
		tags.putBoolean("isSilk", this.getSilk());
		tags.putBoolean("isArrow", this.getArrow());
		tags.putBoolean("isWarriorCard", this.getWarriorCard());
		tags.putBoolean("isQuillpen", this.getQuillpen());
		tags.putBoolean("isHourGlass", this.getHourGlass());

		if (!this.getStack().isEmpty()) {
			tags.put("wandStack", this.getStack().save(new CompoundTag()));
		}
	}

	// NBTの読み込み
	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);

		if (tags.contains("inBlockState", 10)) {
			this.lastState = NbtUtils.readBlockState(tags.getCompound("inBlockState"));
		}

		this.setData(tags.getInt("data"));
		this.setTier(tags.getInt("tier"));
		this.setLifeTime(tags.getShort("lifeTime"));
		this.setMaxLifeTime(tags.getShort("maxLifeTime"));
		this.tickTime = tags.getInt("tickTime");
		this.shakeTime = tags.getByte("shake") & 255;
		this.knockback = tags.getInt("knockback");
		this.addDamage = tags.getInt("addDamage");
		this.maxBreak = tags.getInt("maxBreak");
		this.setBaseDamage(tags.getFloat("baseDamage"));
		this.setAddDamage(tags.getFloat("addDamage"));
		this.setRange(tags.getDouble("range"));
		this.setShotSpeed(tags.getFloat("shotSpeed"));
		this.inGround = tags.getBoolean("inGround");
		this.setHitDead(tags.getBoolean("isHitDead"));
		this.setCritical(tags.getBoolean("isCritical"));
		this.setPlayerThrower(tags.getBoolean("isPlayerThrower"));
		this.setBlockPenetration(tags.getBoolean("isBlockPenetration"));
		this.setNotDamage(tags.getBoolean("isNotDamage"));
		this.setSilk(tags.getBoolean("isSilk"));
		this.setArrow(tags.getBoolean("isArrow"));
		this.setWarriorCard(tags.getBoolean("isWarriorCard"));
		this.setQuillpen(tags.getBoolean("isQuillpen"));
		this.setHourGlass(tags.getBoolean("isHourGlass"));

		if (tags.contains("wandStack", 10)) {
			this.stack = ItemStack.of(tags.getCompound("wandStack"));
			this.wand = IWand.getWand(this.getStack());
			this.tags = this.wand.getNBT(this.getStack());
		}
	}

	/*
	 * =========================================================
	 * 				汎用メソッド　Start
	 * =========================================================
	 */

	// 音を流す
	public void playSound(Entity entity, SoundEvent sound, float vol, float pit) {
		this.level.playSound(null, entity.getOnPos(), sound, SoundSource.AMBIENT, vol, pit);
	}

	// えんちちーリストの取得
	public <T extends Entity> List<T> getEntityList(Class<T> enClass, double range) {
		return this.level.getEntitiesOfClass(enClass, this.getAABB(range));
	}

	// えんちちーリストの取得
	public <T extends Entity> List<T> getEntityList(Class<T> enClass, Predicate<T> filter, AABB aabb) {
		return this.level.getEntitiesOfClass(enClass, aabb).stream().filter(filter).toList();
	}

	// フィルターえんちちーリストの取得
	public <T extends Entity> List<T> getEntityList(Class<T> enClass, Predicate<T> filter, double range) {
		return this.level.getEntitiesOfClass(enClass, this.getAABB(range)).stream().filter(filter).toList();
	}

	// 範囲の取得
	public AABB getAABB(double range) {
		return this.getAABB(range, range / 2, range);
	}

	// 範囲の取得
	public AABB getAABB(double x, double y, double z) {
		return this.getBoundingBox().inflate(x, y, z);
	}

	// 範囲の取得
	public AABB getAABB(BlockPos pos, double range) {
		return new AABB(pos.offset(-range, -range, -range), pos.offset(range, range, range));
	}

	// 射撃者によってえんちちーを取得
	public <T extends LivingEntity> Predicate<T> isTarget() {
		return e -> e.isAlive() && this.canTargetEffect(e, this.getOwner());
	}

	// 範囲内にいる射撃者によってえんちちーを取得
	public <T extends LivingEntity> Predicate<T> isBladTarget(double range) {
		return e -> e.isAlive() && this.canTargetEffect(e, this.getOwner()) && this.checkDistance(e.blockPosition(), range);
	}

	public Iterable<BlockPos> getPosList(BlockPos pos, double range) {
		return WorldHelper.getRangePos(pos, range);
	}

	public Iterable<BlockPos> getPosRangeList(BlockPos pos, double range) {
		return WorldHelper.getRangePos(pos, -range, 0, -range, range, 0, range);
	}

	// プレイヤーの取得
	public Player getPlayer() {
		return (Player) this.getOwner();
	}

	// 乱数取得
	public float getRandFloat(float rate) {
		return (this.rand.nextFloat() - this.rand.nextFloat()) * rate;
	}

	public double getColorValue(double base, double max) {
		return Math.min(1F, (base + this.rand.nextDouble() * max) / 255D);
	}

	public boolean canTargetEffect(LivingEntity target, Entity owner) {
		return (owner instanceof Player || owner instanceof AbstractSummonMob) ? target instanceof Enemy : target instanceof Player;
	}

	// 範囲内にいるかのチェック
	public boolean checkDistance(BlockPos pos, double range) {
		return this.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= range;
	}

	public void vulnerableAttack(LivingEntity target, LivingEntity attacker, float dame) {

		// 炎、爆発脆弱化
		if (this.isVulnerable(target, PotionInit.flame_explosion_vulnerable, SMElement.FLAME, SMElement.BLAST)) {
			this.vulnerableAttack(target, attacker, PotionInit.flame_explosion_vulnerable, dame);
		}

		// 氷、水脆弱化
		if (this.isVulnerable(target, PotionInit.flost_water_vulnerable, SMElement.FROST, SMElement.WATER)) {
			this.vulnerableAttack(target, attacker, PotionInit.flost_water_vulnerable, dame);
		}

		// 雷、風脆弱化
		if (this.isVulnerable(target, PotionInit.lightning_wind_vulnerable, SMElement.LIGHTNING, SMElement.CYCLON)) {
			this.vulnerableAttack(target, attacker, PotionInit.lightning_wind_vulnerable, dame);
		}

		// 地面、毒脆弱化
		if (this.isVulnerable(target, PotionInit.dig_poison_vulnerable, SMElement.EARTH, SMElement.TOXIC)) {
			this.vulnerableAttack(target, attacker, PotionInit.dig_poison_vulnerable, dame);
		}
	}

	public boolean isVulnerable(LivingEntity target, MobEffect potion, SMElement... eleArray) {
		return target.hasEffect(potion) && this.getElement().is(eleArray);
	}

	public void vulnerableAttack(LivingEntity target, LivingEntity attacker, MobEffect potion, float dame) {
		this.addAttackEntity(target, attacker, dame * 2.5F, target.getEffect(potion).getAmplifier() + 1);
	}

	public void vulnerableParticle(Entity target) {
		if (!(this.level instanceof ServerLevel server)) { return; }

		double red = this.getColorValue(10D, 235D);
		double green = this.getColorValue(10D, 235D);
		double blue = this.getColorValue(10D, 235D);
		float addX = this.getRandFloat(1.25F);
		float addY = this.getRandFloat(1.25F);
		float addZ = this.getRandFloat(1.25F);

		for (int i = 0; i < 5; i++) {
			double x = target.getX() + this.getRandFloat(0.5F) + addX;
			double y = target.getY() + 1.5D + this.getRandFloat(0.5F) + addY;
			double z = target.getZ() + this.getRandFloat(0.5F) + addZ;
			server.sendParticles(ParticleInit.ADDATTACK, x, y, z, 0, red, green, blue, 1F);
		}
	}

	/*
	 * =========================================================
	 * 				汎用メソッド　End
	 * =========================================================
	 */
}
