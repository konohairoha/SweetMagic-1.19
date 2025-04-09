package sweetmagic.init.entity.projectile;

import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.monster.PhantomWolf;

public class PoisonMagicShot extends AbstractMagicShot {

	private LivingEntity entity = null;
	private static final EntityDataAccessor<Boolean> IS_WOLF = setEntityData(EntityDataSerializers.BOOLEAN);

	public PoisonMagicShot(EntityType<? extends AbstractMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public PoisonMagicShot(double x, double y, double z, Level world) {
		this(EntityInit.poisonMagic, world);
		this.setPos(x, y, z);
	}

	public PoisonMagicShot(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
	}

	public PoisonMagicShot(Level world, LivingEntity entity) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = ItemStack.EMPTY;
		this.setRange(4D);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(IS_WOLF, false);
	}

	public void setWolf(boolean isWolf) {
		this.entityData.set(IS_WOLF, isWolf);
	}

	public boolean getWolf() {
		return this.entityData.get(IS_WOLF);
	}

	// えんちちーに当たった時の処理
	protected void entityHit(LivingEntity living) {

		int time = 60 * (this.getWandLevel() + 1);

		if (living instanceof Player && this.getOwner() instanceof Player) {
			living.removeEffect(PotionInit.deadly_poison);
		}

		else {
			this.addPotion(living, PotionInit.deadly_poison, time, this.getData());
		}

		if (this.getData() >= 1) {
			this.rangeAttack(living.blockPosition(), (float) this.getDamage() * this.getDamageRate(), this.getRange());
		}

		else {
			this.hitToSpawnParticle();
		}

		if (this.getData() >= 3) {

		}
	}

	// ブロック着弾
	protected void onHitBlock(BlockHitResult result) {

		if (this.getData() >= 1) {
			float damageRate = this.getDamageRate() * 0.67F;
			this.rangeAttack(result.getBlockPos().above(), this.getDamage() * damageRate, this.getRange() * 0.67F);
		}

		else {
			this.hitToSpawnParticle();
		}

		this.discard();
	}

	public void rangeAttack(BlockPos bPos, float dame, double range) {

		if (this.level instanceof ServerLevel server) {

			// 範囲の座標取得
			Random rand = this.rand;
			double effectRange = range * range;
			Iterable<BlockPos> pList = this.getPosRangeList(bPos, range);
			ParticleOptions par = ParticleInit.SMOKY;

			for (BlockPos pos : pList) {
				if(!this.checkDistance(pos, effectRange)) { continue; }

				double x = pos.getX() + rand.nextDouble() * 1.5D - 0.75D;
				double y = pos.getY() + rand.nextDouble() * 1.5D - 0.75D;
				double z = pos.getZ() + rand.nextDouble() * 1.5D - 0.75D;
				server.sendParticles(par, x, y, z, 0, 67F / 255F, 173F / 255F, 103F / 255F, 1F);
			}
		}

		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.isTarget(), range);
		if (entityList.isEmpty()) { return; }

		double effectRange = range * range;
		int time = 60 * (this.getWandLevel() + 1);
		boolean isTier4 = this.getData() >= 3;

		for (LivingEntity entity : entityList) {
			if (!this.checkDistance(entity.blockPosition(), effectRange)) { continue; }

			this.attackDamage(entity, dame, false);
			this.addPotion(entity, PotionInit.deadly_poison, time, this.getData());

			if (isTier4) {
				this.addPotion(entity, PotionInit.magic_damage_receive, time, 1);

				if (entity.hasEffect(PotionInit.reflash_effect)) {
					float rate = this.isBoss(entity) ? 0.025F : 0.1F;
					entity.setHealth(Math.max(1, entity.getHealth() - entity.getMaxHealth() * rate));
				}
			}
		}
	}

	protected void spawnParticleShort(ServerLevel sever, BlockPos pos) {
		float x = (float) (pos.getX() + this.getRandFloat(0.25F));
		float y = (float) (pos.getY() + this.getRandFloat(0.25F));
		float z = (float) (pos.getZ() + this.getRandFloat(0.25F));

		for (int i = 0; i < 3; i++) {
			sever.sendParticles(ParticleInit.POISON, x, y, z, 4, 0F, 0F, 0F, 0.15F);
		}
	}

	public int getMinParticleTick() {
		return 3;
	}

	// パーティクルスポーン
	protected void spawnParticle() {

		Vec3 vec = this.getDeltaMovement();
		float addX = (float) (-vec.x / 20F);
		float addY = (float) (-vec.y / 20F);
		float addZ = (float) (-vec.z / 20F);
		Random rand = this.rand;

		for (int i = 0; i < 6; i++) {
			float x = addX + this.getRandFloat(0.075F);
			float y = addY + this.getRandFloat(0.075F);
			float z = addZ + this.getRandFloat(0.075F);
			float f1 = (float) (this.getX() - 0.5F + rand.nextFloat() + vec.x * i / 4.0F);
			float f2 = (float) (this.getY() - 0.25F + rand.nextFloat() * 0.5 + vec.y * i / 4.0D);
			float f3 = (float) (this.getZ() - 0.5F + rand.nextFloat() + vec.z * i / 4.0D);
			this.level.addParticle(ParticleInit.POISON, f1, f2, f3, x, y, z);
		}
	}

	public void hitToSpawnParticle() {
		if (!(this.level instanceof ServerLevel server)) { return; }

		Random rand = this.rand;
		BlockPos pos = this.blockPosition();

		for (int i = 0; i < 4; i++) {
			double x = pos.getX() + rand.nextDouble() * 3D - 1.5D;
			double y = pos.getY() + rand.nextDouble() * 1.5D - 0.75D;
			double z = pos.getZ() + rand.nextDouble() * 3D - 1.5D;
			server.sendParticles(ParticleInit.SMOKY, x, y, z, 0, 67F / 255F, 173F / 255F, 103F / 255F, 1F);
		}
	}

	// NBTの書き込み
	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putBoolean("isWolf", this.getWolf());
	}

	// NBTの読み込み
	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setWolf(tags.getBoolean("isWolf"));
	}

	// レンダー用のえんちちー取得
	public LivingEntity getRenderEntity() {

		if (this.entity == null) {
			this.entity = entity = new PhantomWolf(this.level);
		}

		return this.entity;
	}

	// ダメージレートの取得
	public float getDamageRate() {
		switch (this.getData()) {
		case 1: return 0.67F;
		case 2: return 1F;
		case 3: return 1.375F;
		default: return 0.5F;
		}
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.TOXIC;
	}
}
