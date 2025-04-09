package sweetmagic.init.entity.projectile;

import javax.annotation.Nullable;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;

public class LigningBulletShot extends AbstractMagicShot {

	private static final EntityDataAccessor<Integer> TARGET = SynchedEntityData.defineId(LigningBulletShot.class, EntityDataSerializers.INT);

	public LigningBulletShot(EntityType<? extends AbstractMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public LigningBulletShot(double x, double y, double z, Level world) {
		this(EntityInit.ligningBullet, world);
		this.setPos(x, y, z);
	}

	public LigningBulletShot(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
	}

	public LigningBulletShot(Level world, LivingEntity entity) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = ItemStack.EMPTY;
		this.setRange(5D);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(TARGET, -1);
	}

	public void tick() {

		Entity target = this.getTarget();

		if (target != null && !target.isAlive()) {
			target = null;
			this.setTarget(null);
		}

		if (target != null) {

			Vec3 vec = this.getDeltaMovement();
			double mX = vec.x();
			double mY = vec.y();
			double mZ = vec.z();

			Vec3 arrowVec = new Vec3(this.getX(), this.getY(), this.getZ());
			Vec3 lookVec = new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ()).subtract(arrowVec);
			Vec3 arrowMotion = new Vec3(mX, mY, mZ);
			double theta = this.wrap180Radian(this.angleBetween(arrowMotion, lookVec));
			theta = this.clampAbs(theta, Math.PI / 2);

			Vec3 crossVec = arrowMotion.cross(lookVec).normalize();
			Vec3 adjustedVec = this.transform(crossVec, theta, arrowMotion);
			this.shoot(adjustedVec.x, adjustedVec.y, adjustedVec.z, 1F, 0);
			this.setDeltaMovement(this.getDeltaMovement().scale(0.625F));
		}

		super.tick();
	}

	// パーティクルスポーン
	protected void spawnParticle() {
		this.level.addParticle(ParticleInit.MAGICLIGHT, this.getX(), this.getY(), this.getZ(), 0F, 0F, 0F);
	}

	@Nullable
	private Entity getTarget() {
		return this.level.getEntity(this.getEntityData().get(TARGET));
	}

	public void setTarget(Entity entity) {
		this.getEntityData().set(TARGET, entity == null ? -1 : entity.getId());
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.LIGHTNING;
	}
}
