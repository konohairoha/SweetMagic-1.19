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
import sweetmagic.init.ParticleInit;

public class ExplosionMagicShot extends AbstractMagicShot {

	public ExplosionMagicShot(EntityType<? extends ExplosionMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public ExplosionMagicShot(double x, double y, double z, Level world) {
		this(EntityInit.explosionMagic, world);
		this.setPos(x, y, z);
	}

	public ExplosionMagicShot(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
	}

	public ExplosionMagicShot(Level world, LivingEntity entity, ItemStack stack) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = stack;
		this.setRange(5D);
	}

	// えんちちーに当たった時の処理
	protected void entityHit(LivingEntity living) {
		double range = this.getRange() * (1D + 0.5D * this.getData());
		float dame = this.getDamage() * this.getDamageRate();
		this.rangeAttack(living.blockPosition(), dame, range);
	}

	// ブロック着弾
	protected void onHitBlock(BlockHitResult result) {
		double range = this.getRange();
		float dame = this.getDamage() * this.getDamageRate() * 0.67F;
		this.rangeAttack(result.getBlockPos().above(), dame, range);
		this.discard();
	}

	public void rangeAttack (BlockPos pos, float dame, double range) {
		this.playSound(SoundEvents.GENERIC_EXPLODE, 3F, 1F / (this.random.nextFloat() * 0.2F + 0.9F));

		if (this.level instanceof ServerLevel sever) {
			sever.sendParticles(ParticleTypes.EXPLOSION_EMITTER, pos.getX(), pos.getY() + 0.5D, pos.getZ(), 2, 0D, 0D, 0D, 0D);
		}

		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.isTarget(), range);
		entityList.forEach(e -> this.attackDamage(e, dame, false));
	}

	// ダメージレートの取得
	public float getDamageRate () {
		switch (this.getData()) {
		case 1: return 0.875F;
		case 2: return 1.35F;
		default: return 0.5F;
		}
	}

	// パーティクルスポーン
	protected void spawnParticle() {
		this.level.addParticle(ParticleInit.ORB.get(), this.getX(), this.getY(), this.getZ(), 255F / 255F, 248F / 255F, 44F / 255F);
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.BLAST;
	}
}
