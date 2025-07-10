package sweetmagic.init.entity.projectile;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.entity.ai.ExplosionAttackGoal;

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

	public ExplosionMagicShot(Level world, LivingEntity entity) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = ItemStack.EMPTY;
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

	public void rangeAttack(BlockPos pos, float dame, double range) {
		this.playSound(SoundEvents.GENERIC_EXPLODE, 3F, 1F / (this.rand.nextFloat() * 0.2F + 0.9F));

		if (this.getLevel() instanceof ServerLevel sever) {
			sever.sendParticles(ParticleTypes.EXPLOSION_EMITTER, pos.getX(), pos.getY() + 0.5D, pos.getZ(), 2, 0D, 0D, 0D, 0D);
		}

		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.isTarget(), range);
		entityList.forEach(e -> this.attackDamage(e, dame, false));

		if (this.getData() >= 3) {
			List<Mob> mobList = this.getEntityList(Mob.class, this.isTarget(), range);

			for (Mob mob : mobList) {

				List<WrappedGoal> goalList = mob.goalSelector.getAvailableGoals().stream().filter(e -> e.getGoal() instanceof ExplosionAttackGoal).toList();

				if(goalList.isEmpty()) {
					mob.goalSelector.addGoal(0, new ExplosionAttackGoal(mob, this.getOwner(), this, this.getDamage(), 31 + this.rand.nextInt(4)));
				}

				else {
					((ExplosionAttackGoal) goalList.get(0).getGoal()).clearInfo(31 + this.rand.nextInt(4));
				}
			}
		}
	}

	// ダメージレートの取得
	public float getDamageRate() {
		switch (this.getData()) {
		case 1: return 0.875F;
		case 2: return 1.35F;
		case 3: return 1.75F;
		default: return 0.5F;
		}
	}

	// パーティクルスポーン
	protected void spawnParticle() {
		this.addParticle(ParticleInit.ORB, this.getX(), this.getY(), this.getZ(), 1F, 248F / 255F, 44F / 255F);
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.BLAST;
	}
}
