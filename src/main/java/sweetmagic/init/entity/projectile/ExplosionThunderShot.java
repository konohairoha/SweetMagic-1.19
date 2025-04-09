package sweetmagic.init.entity.projectile;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;

public class ExplosionThunderShot extends AbstractMagicShot {

	public ExplosionThunderShot(EntityType<? extends AbstractMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public ExplosionThunderShot(double x, double y, double z, Level world) {
		this(EntityInit.explosionThunder, world);
		this.setPos(x, y, z);
	}

	public ExplosionThunderShot(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
	}

	public ExplosionThunderShot(Level world, LivingEntity entity) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = ItemStack.EMPTY;
		this.setRange(5D);
	}

	// えんちちーに当たった時の処理
	protected void entityHit(LivingEntity living) {
		this.rangeAttack(living.blockPosition(), this.getDamage(), this.getRange());
	}

	// ブロック着弾
	protected void onHitBlock(BlockHitResult result) {
		this.rangeAttack(result.getBlockPos().above(), this.getDamage(), this.getRange());
		this.discard();
	}

	public void rangeAttack(BlockPos pos, float dame, double range) {
		this.playSound(SoundEvents.GENERIC_EXPLODE, 3F, 1F / (this.rand.nextFloat() * 0.2F + 0.9F));

		if (this.level instanceof ServerLevel sever) {
			sever.sendParticles(ParticleTypes.EXPLOSION_EMITTER, pos.getX(), pos.getY() + 0.5D, pos.getZ(), 2, 0D, 0D, 0D, 0D);
		}

		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.isTarget(), range);
		entityList.forEach(e -> this.attackDamage(e, dame, false));
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.BLAST;
	}
}
