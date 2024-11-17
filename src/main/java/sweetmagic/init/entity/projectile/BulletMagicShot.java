package sweetmagic.init.entity.projectile;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;

public class BulletMagicShot extends AbstractMagicShot {

	private static final EntityDataAccessor<Integer> TARGET = SynchedEntityData.defineId(BulletMagicShot.class, EntityDataSerializers.INT);

	public BulletMagicShot(EntityType<? extends BulletMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public BulletMagicShot(double x, double y, double z, Level world) {
		this(EntityInit.bulletMagic, world);
		this.setPos(x, y, z);
	}

	public BulletMagicShot(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(TARGET, -1);
	}

	public void tick () {

		if (!this.level.isClientSide()) {
			this.updateTarget();
		}

		Entity target = this.getTarget();
		if (target != null) {

			Vec3 bulletVec = this.getDeltaMovement();
			double mX = bulletVec.x();
			double mY = bulletVec.y();
			double mZ = bulletVec.z();

			Vec3 arrowLoc = new Vec3(this.getX(), this.getY(), this.getZ());
			Vec3 targetLoc = new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ());

			Vec3 lookVec = targetLoc.subtract(arrowLoc);
			Vec3 arrowMotion = new Vec3(mX, mY, mZ);

			double theta = this.wrap180Radian(this.angleBetween(arrowMotion, lookVec));
			theta = this.clampAbs(theta, Math.PI / 2);

			Vec3 crossProduct = arrowMotion.cross(lookVec).normalize();
			Vec3 adjustedLookVec = this.transform(crossProduct, theta, arrowMotion);

			this.shoot(adjustedLookVec.x, adjustedLookVec.y, adjustedLookVec.z, 1F, 0);
            this.setDeltaMovement(this.getDeltaMovement().scale(1.1F));
		}

		this.spawnParticleSever();
		super.tick();
	}

	private void updateTarget() {

		Entity target = this.getTarget();

		if (target != null && !target.isAlive()) {
			target = null;
			this.setTarget(null);
		}

		if (target != null) { return; }

		Entity owner = this.getOwner();
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, e -> this.canTargetEffect(e, owner) && e.isAlive(), this.getRange());
		if (entityList.isEmpty()) { return; }

		double distance = this.getRange() + 1;

		for (LivingEntity entity : entityList) {

			if (distance >= entity.distanceTo(this)) {
				this.setTarget(entity);
			}
		}
	}

	private double wrap180Radian(double radian) {

		radian %= 2 * Math.PI;

		while (radian >= Math.PI) {
			radian -= 2 * Math.PI;
		}

		while (radian < -Math.PI) {
			radian += 2 * Math.PI;
		}

		return radian;
	}

	private double clampAbs(double param, double maxMagnitude) {
		if (Math.abs(param) > maxMagnitude) {
			param =param < 0 ? -Math.abs(maxMagnitude) : Math.abs(maxMagnitude);
		}
		return param;
	}

	private double angleBetween(Vec3 v1, Vec3 v2) {

		double vDot = v1.dot(v2) / (v1.length() * v2.length());

		if (vDot < -1D) {
			vDot = -1D;
		}

		if (vDot > 1D) {
			vDot = 1D;
		}

		return Math.acos(vDot);
	}

	private Vec3 transform(Vec3 axis, double angle, Vec3 normal) {

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

	// えんちちーに当たった時の処理
	protected void entityHit(LivingEntity living) {

		if (this.knockback > 0D) {

			double rate = 1D + this.wandLevel * 0.05D;

			double d0 = Math.max(0D, 1D - living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)) * rate;
			Vec3 vec3 = this.getDeltaMovement().multiply(1D, 0D, 1D).normalize().scale((double) this.knockback * d0);
			if (vec3.lengthSqr() > 0D) {
				living.push(vec3.x, 0.1D, vec3.z);
			}
		}
	}

	// 追加攻撃
	public void addAttack (LivingEntity entity, float dame, int addAttackCount) {

		if (addAttackCount > 0) {

			for (int i = 0; i < addAttackCount; i++) {
				this.attackDamage(entity, dame * 0.25F, true);
			}
		}
	}

	// 追加攻撃
	public void addAttackEntity (Entity target, LivingEntity attacker, float dame, int addAttackCount) {

		if (addAttackCount > 0) {

			for (int i = 0; i < addAttackCount; i++) {

				if (target.hurt(DamageSource.playerAttack((Player) attacker), dame * 0.25F) ) {
					target.invulnerableTime = 0;
				}
			}
		}
	}

	// パーティクルスポーン
	protected void spawnParticleSever() {

		if (this.tickCount < 3) { return; }

		if (this.level instanceof ServerLevel sever) {
			sever.sendParticles(ParticleInit.ORB.get(), this.getX(), this.getY(), this.getZ(), 0, 114F / 255F, 255F / 255F, 170F / 255F, 1F);
		}
	}

	@Nullable
	private Entity getTarget() {
		return this.level.getEntity(this.getEntityData().get(TARGET));
	}

	private void setTarget(Entity entity) {
		this.getEntityData().set(TARGET, entity == null ? -1 : entity.getId());
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.NON;
	}
}
