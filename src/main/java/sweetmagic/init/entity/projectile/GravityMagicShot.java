package sweetmagic.init.entity.projectile;

import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
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

public class GravityMagicShot extends AbstractMagicShot {

	public GravityMagicShot(EntityType<? extends GravityMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public GravityMagicShot(double x, double y, double z, Level world) {
		this(EntityInit.gravityMagic, world);
		this.setPos(x, y, z);
	}

	public GravityMagicShot(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
	}

	public GravityMagicShot(Level world, LivingEntity entity) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = ItemStack.EMPTY;
		this.setRange(5D);
	}

	// えんちちーに当たった時の処理
	protected void entityHit(LivingEntity living) {

		if (this.level instanceof ServerLevel sever) {

			Random rand = this.rand;

			for (int i = 0; i < 16; i++) {
				float x = (float) (living.getX() + rand.nextFloat() * 1.5F - 0.5F);
				float y = (float) (living.getY() + rand.nextFloat() * 2F - 0.5F);
				float z = (float) (living.getZ() + rand.nextFloat() * 1.5F - 0.75F);
				sever.sendParticles(ParticleTypes.DRAGON_BREATH, x, y, z, 4, 0F, 0F, 0F, 0.1F);
			}
		}

		int time = 60 * (this.getWandLevel() + 1);

		if (living instanceof Player && this.getOwner() instanceof Player) {
			living.removeEffect(PotionInit.gravity);
		}

		else {
			this.addPotion(living, PotionInit.gravity, time, this.getData());
		}

		int data = this.getData();
		if (data < 1) { return; }

		if (data >= 2) {
			this.setLifeTime(0);
			this.setMaxLifeTime(data >= 3 ? 100 : 60);
		}

		float rate = this.getDamageRate();
		this.rangeAttack(living.blockPosition(), (float) this.getDamage() * rate, this.getRange());
	}

	// ブロック着弾
	protected void onHitBlock(BlockHitResult result) {

		int data = this.getData();

		if (data >= 1) {

			if (data >= 2) {
				this.setLifeTime(0);
				this.setMaxLifeTime(data >= 3 ? 100 : 60);
			}

			float rate = data >= 2 ? 1F : 0.67F;
			float damageRate = this.getDamageRate() * 0.67F;
			this.rangeAttack(result.getBlockPos().above(), this.getDamage() * damageRate, this.getRange() * rate);
		}

		if (data < 2) {
			this.discard();
		}
	}

	public void rangeAttack(BlockPos bPos, float dame, double range) {

		int tick = this.getLifeTime();
		boolean isTier3 = !this.getHitDead();

		if (this.level instanceof ServerLevel server && (!isTier3 || (tick % 10 == 0))) {

			boolean isZero = this.getMaxLifeTime() == 100;
			double ySpeed = isZero ? 0D : -0.25D;
			double yRate = isZero ? 0D : -0.6D;
			double inRate = isZero ? 1.5D : 0.25D;

			for (int i = 0; i < 4; i++) {
				this.spawnParticleRing(server, ParticleInit.GRAVITY, range * (1 - 0.14D * i), bPos.above(i + 1), ySpeed + i * yRate, inRate);
			}
		}

		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.isTarget(), range);
		if (entityList.isEmpty()) { return; }

		Entity owner = this.getOwner();
		int time = 60 * (this.getWandLevel() + 1);

		for (LivingEntity entity : entityList) {

			if (!this.canTargetEffect(entity, owner)) { continue; }

			if (!isTier3 || (tick % 10 == 0)) {
				this.attackDamage(entity, dame, false);
			}

			this.addPotion(entity, PotionInit.gravity, time, this.getData());
			Vec3 vec3 = (new Vec3(entity.getX() - this.getX(), (entity.getY() - this.getY()) * 1D, entity.getZ() - this.getZ())).scale(-0.25D);
			entity.setDeltaMovement(entity.getDeltaMovement().add(vec3));
			entity.fallDistance += 0.5D;
		}
	}

	public void inGround() {
		if (this.getData() >= 2) {
			this.rangeAttack(this.blockPosition(), this.getData() >= 3 ? this.getDamage() * 0.1F : 1F, this.getRange());
		}
	}

	protected void spawnParticleShort(ServerLevel sever, BlockPos pos) {
		float x = (float) (pos.getX() + this.getRandFloat(0.25F));
		float y = (float) (pos.getY() + this.getRandFloat(0.25F));
		float z = (float) (pos.getZ() + this.getRandFloat(0.25F));

		for (int i = 0; i < 3; i++) {
			sever.sendParticles(ParticleInit.GRAVITY, x, y, z, 4, 0F, 0F, 0F, 0.15F);
		}
	}

	public int getMinParticleTick() {
		return 4;
	}

	// パーティクルスポーン
	protected void spawnParticle() {

		Vec3 vec = this.getDeltaMovement();
		float x = (float) (-vec.x / 10F);
		float y = (float) (-vec.y / 10F);
		float z = (float) (-vec.z / 10F);
		Random rand = this.rand;

		for (int i = 0; i < 6; i++) {
			float f1 = (float) (this.getX() - 0.5F + rand.nextFloat() + vec.x * i / 4F);
			float f2 = (float) (this.getY() - 0.25F + rand.nextFloat() * 0.5F + vec.y * i / 4F);
			float f3 = (float) (this.getZ() - 0.5F + rand.nextFloat() + vec.z * i / 4F);
			this.level.addParticle(ParticleInit.GRAVITY, f1, f2, f3, x + this.getRandFloat(0.075F), y + this.getRandFloat(0.075F), z + this.getRandFloat(0.075F));
		}
	}

	// ダメージレートの取得
	public float getDamageRate() {
		switch (this.getData()) {
		case 2: return 1F;
		case 3: return 1.375F;
		default: return 0.67F;
		}
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.GRAVITY;
	}
}
