package sweetmagic.init.entity.projectile;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;

public class MeteorMagicShot extends FireMagicShot {

	public MeteorMagicShot(EntityType<? extends MeteorMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public MeteorMagicShot(double x, double y, double z, Level world) {
		this(EntityInit.meteorMagic, world);
		this.setPos(x, y, z);
	}

	public MeteorMagicShot(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
	}

	public void rangeAttack (BlockPos bPos, float dame, double range) {

		this.playSound(SoundEvents.LIGHTNING_BOLT_IMPACT, 1F, 1F);

		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, range);
		if (entityList.isEmpty()) { return; }

		Entity owner = this.getOwner();
		double effectRange = range * range;

		for (LivingEntity entity : entityList) {
			if (!this.canTargetEffect(entity, owner) || !this.checkDistance(entity.blockPosition(), effectRange)) { continue; }
			this.attackDamage(entity, dame, false);
		}
	}

	// パーティクルスポーン
	protected void spawnParticle() {

		if (this.lifeTime % 2 != 1) { return; }

		Vec3 vec = this.getDeltaMovement();
		float x = (float) (-vec.x / 80F );
		float y = (float) (vec.y / 2F);
		float z = (float) (-vec.z / 80F);
		float f1 = (float) (this.getX() - 0.0F);
		float f2 = (float) (this.getY() + 0.5F);
		float f3 = (float) (this.getZ() - 0.0F);

		this.level.addParticle(ParticleTypes.FLAME, f1, f2, f3, x, y, z);
	}
}
