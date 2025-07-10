package sweetmagic.init.entity.projectile;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.init.EntityInit;

public class BraveShot extends AbstractMagicShot {

	public BraveShot(EntityType<? extends AbstractMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public BraveShot(double x, double y, double z, Level world) {
		this(EntityInit.braveShot, world);
		this.setPos(x, y, z);
	}

	public BraveShot(Level world, LivingEntity entity) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = ItemStack.EMPTY;
	}

	// えんちちーに当たった時の処理
	protected void entityHit(LivingEntity target) {
		double range = this.getRange() / 0.67D;
		float dame = 0.5F + 0.67F * this.getWandLevel();
		this.rangeAttack(target.blockPosition(), dame, range);
	}

	// ブロック着弾
	protected void onHitBlock(BlockHitResult result) {

		double range = this.getRange();
		float dame = 0.5F + 0.5F * this.getWandLevel();
		this.rangeAttack(result.getBlockPos().above(), dame, range);

		if (this.getLevel() instanceof ServerLevel server) {
			this.spawnParticleShort(server, result.getBlockPos().above());
		}

		this.discard();
	}

	public void rangeAttack(BlockPos bPos, float dame, double range) {
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.isTarget(), range);
		entityList.forEach(e -> this.attackDamage(e, dame, false));
	}

	// パーティクルスポーン
	protected void spawnParticle() {
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		this.addParticle(ParticleTypes.SWEEP_ATTACK, x, y, z, 0, 0, 0);
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.FLAME;
	}
}
