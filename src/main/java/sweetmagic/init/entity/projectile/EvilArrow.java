package sweetmagic.init.entity.projectile;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
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

	public EvilArrow(Level world, LivingEntity entity, ItemStack stack) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = stack;
	}

	// ブロック着弾
	protected void onHitBlock(BlockHitResult result) {
		this.rangeAttack(null, 2.5D);
		super.onHitBlock(result);
	}

	// えんちちーに当たった時の処理
	protected void entityHit(LivingEntity living) {

		if (living instanceof ISMMob || living.hasEffect(PotionInit.darkness_fog)) {

			if (!this.isBoss(living)) {
				if (living.getHealth() > 1 && !this.isNotSpecial(living)) {
					living.setHealth(1);
				}

				this.attackDamage(living, 999F, false);
			}

			else {
				if (living instanceof AbstractSMBoss boss && boss.isArmorEmpty()) {
					this.attackDamage(boss, 10F, false);
				}
			}
		}

		this.rangeAttack(living, 3.5D);
	}

	public void rangeAttack (LivingEntity living, double range) {

		this.playSound(SoundEvents.GENERIC_EXPLODE, 3F, 1F / (this.random.nextFloat() * 0.2F + 0.9F));
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.expTarget(living), range);

		for (LivingEntity entity : entityList) {

			if (!this.isBoss(entity)) {
				if (entity.getHealth() > 1 && !this.isNotSpecial(entity)) {
					entity.setHealth(1);
				}

				this.attackDamage(entity, 999F, false);
			}

			else {
				if (entity instanceof AbstractSMBoss boss && boss.isArmorEmpty()) {
					this.attackDamage(boss, 10F, false);
				}
			}
		}

		if (this.level instanceof ServerLevel sever) {
			sever.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY() + 0.5D, this.getZ(), 2, 0D, 0D, 0D, 0D);
		}
	}

	public <T extends LivingEntity> Predicate<T> expTarget (LivingEntity living) {
		return e -> (e instanceof ISMMob || e.hasEffect(PotionInit.darkness_fog)) && ( living == null || e.getUUID() != living.getUUID());
	}

	// パーティクルスポーン
	protected void spawnParticle() {

		if (this.tickCount < 3) { return; }

		Vec3 vec = this.getDeltaMovement();
		float addX = (float) (-vec.x / 10F);
		float addY = (float) (-vec.y / 10F);
		float addZ = (float) (-vec.z / 10F);
		this.level.addParticle(ParticleInit.NORMAL.get(), this.getX(), this.getY(), this.getZ(), addX, addY, addZ);
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.BLAST;
	}
}
