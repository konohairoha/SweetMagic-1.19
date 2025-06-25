package sweetmagic.init.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;

public class NormalMagicShot extends AbstractMagicShot {

	public NormalMagicShot(EntityType<? extends AbstractMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public NormalMagicShot(double x, double y, double z, Level world) {
		this(EntityInit.normalMagic, world);
		this.setPos(x, y, z);
	}

	public NormalMagicShot(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = wandInfo.getStack();
	}

	// ノックバック
	public void onKnockBack(LivingEntity living) {
		if (this.knockback <= 0D) { return; }

		double rate = 1D + this.wandLevel * 0.05D;
		double d0 = Math.max(0D, 1D - living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)) * rate;
		Vec3 vec3 = this.getDeltaMovement().multiply(1D, 0D, 1D).normalize().scale((double) this.knockback * d0);
		if (vec3.lengthSqr() > 0D) {
			living.push(vec3.x, 0.1D, vec3.z);
		}
	}

	public int getMinParticleTick() {
		return 3;
	}

	// パーティクルスポーン
	protected void spawnParticle() {
		this.addParticle(ParticleInit.ORB, this.getX(), this.getY(), this.getZ(), 114F / 255F, 255F / 255F, 170F / 255F);
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.NON;
	}
}
