package sweetmagic.init.entity.projectile;

import java.util.ArrayList;
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

	private int count = 0;
	private List<LivingEntity> taregetList = new ArrayList<>();
	private static final EntityDataAccessor<Integer> TARGET = SynchedEntityData.defineId(BulletMagicShot.class, EntityDataSerializers.INT);

	public BulletMagicShot(EntityType<? extends AbstractMagicShot> entityType, Level world) {
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

	public void tick() {

		if (!this.level.isClientSide()) {
			this.updateTarget();
		}

		Entity target = this.getTarget();
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
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, e -> this.canTargetEffect(e, owner) && e.isAlive() && !this.taregetList.contains(e), this.getRange());
		if (entityList.isEmpty()) { return; }

		double distance = this.getRange() + 1;

		for (LivingEntity entity : entityList) {

			if (distance >= entity.distanceTo(this)) {
				this.setTarget(entity);
			}
		}
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

		if (this.getData() >= 3) {
			this.taregetList.add(living);
			this.setTarget(null);

			if (this.count++ > 3) {
				this.discard();
			}
		}
	}

	// 追加攻撃
	public void addAttack(LivingEntity entity, float dame, int addAttackCount) {
		if (addAttackCount <= 0) { return; }
		for (int i = 0; i < addAttackCount; i++) {
			this.attackDamage(entity, dame * 0.25F, true);
		}
	}

	// 追加攻撃
	public void addAttackEntity(Entity target, LivingEntity attacker, float dame, int addAttackCount) {
		if (addAttackCount <= 0) { return; }

		for (int i = 0; i < addAttackCount; i++) {
			if (target.hurt(DamageSource.playerAttack((Player) attacker), dame * 0.25F)) {
				target.invulnerableTime = 0;
			}
		}
	}

	// パーティクルスポーン
	protected void spawnParticleSever() {
		if (this.tickCount < 3 || !(this.level instanceof ServerLevel sever)) { return; }
		sever.sendParticles(ParticleInit.ORB, this.getX(), this.getY(), this.getZ(), 0, 114F / 255F, 255F / 255F, 170F / 255F, 1F);
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
