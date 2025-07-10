package sweetmagic.init.entity.projectile;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.DimentionInit;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.monster.boss.AbstractSMBoss;

public class EvilArrow extends AbstractMagicShot {

	public EvilArrow(EntityType<? extends EvilArrow> entityType, Level world) {
		super(entityType, world);
	}

	public EvilArrow(double x, double y, double z, Level world) {
		this(EntityInit.evilArrow, world);
		this.setPos(x, y, z);
	}

	public EvilArrow(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = wandInfo.getStack();
	}

	public EvilArrow(Level world, LivingEntity entity) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = ItemStack.EMPTY;
	}

	// ブロック着弾
	protected void onHitBlock(BlockHitResult result) {
		this.rangeAttack(null, 2.5D);
		super.onHitBlock(result);
	}

	// えんちちーに当たった時の処理
	protected void entityHit(LivingEntity living) {

		if (!(this.getOwner() instanceof Enemy) && (living instanceof ISMMob || living.hasEffect(PotionInit.darkness_fog))) {

			if (!this.isBoss(living)) {
				if (living.getHealth() > 1 && !this.isNotSpecial(living)) {
					living.setHealth(1);
				}

				this.attackDamage(living, 999F, false);
			}

			else {

				boolean isSMDim = this.getLevel().dimension() == DimentionInit.SweetMagicWorld;

				if (isSMDim && living instanceof AbstractSMBoss boss) {
					this.attackDamage(boss, 15F, false);
				}

				else if (living instanceof AbstractSMBoss boss && boss.isArmorEmpty()) {
					this.attackDamage(boss, 10F, false);
				}
			}
		}

		this.rangeAttack(living, 3.5D);
	}

	public void rangeAttack(LivingEntity living, double range) {

		Level world = this.getLevel();
		boolean isSMDim = world.dimension() == DimentionInit.SweetMagicWorld;
		this.playSound(SoundEvents.GENERIC_EXPLODE, 3F, 1F / (this.rand.nextFloat() * 0.2F + 0.9F));
		boolean isEnemy = this.getOwner() instanceof Enemy;
		Predicate<LivingEntity> filter= isEnemy ? this.isTarget() : this.expTarget(living);
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class,filter , range);

		for (LivingEntity entity : entityList) {

			if(this.canHitTarget(entity)) {
				this.attackDamage(entity, 6F, false);
			}

			else if (!this.isBoss(entity)) {
				if (entity.getHealth() > 1 && !this.isNotSpecial(entity)) {
					entity.setHealth(1);
				}

				this.attackDamage(entity, 999F, false);
			}

			else {
				if (isSMDim && entity instanceof AbstractSMBoss boss) {
					this.attackDamage(boss, 15F, false);
				}

				else if (entity instanceof AbstractSMBoss boss && boss.isArmorEmpty()) {
					this.attackDamage(boss, 10F, false);
				}
			}
		}

		if (world instanceof ServerLevel sever) {
			sever.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY() + 0.5D, this.getZ(), 2, 0D, 0D, 0D, 0D);
		}

		if(isEnemy) {
			world.addFreshEntity(new ItemEntity(world, this.getX(), this.getY(), this.getZ(), new ItemStack(ItemInit.evil_arrow)));
		}
	}

	public <T extends LivingEntity> Predicate<T> expTarget(LivingEntity living) {
		return e -> e instanceof ISMMob || e.hasEffect(PotionInit.darkness_fog);
	}

	// パーティクルスポーン
	protected void spawnParticle() {
		if (this.tickCount < 3) { return; }

		Vec3 vec = this.getDeltaMovement();
		float addX = (float) (-vec.x / 10F);
		float addY = (float) (-vec.y / 10F);
		float addZ = (float) (-vec.z / 10F);
		this.addParticle(ParticleInit.NORMAL, this.getX(), this.getY(), this.getZ(), addX, addY, addZ);
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.BLAST;
	}
}
