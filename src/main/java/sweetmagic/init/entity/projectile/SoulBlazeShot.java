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
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;

public class SoulBlazeShot extends AbstractMagicShot {

	public SoulBlazeShot(EntityType<? extends SoulBlazeShot> entityType, Level world) {
		super(entityType, world);
	}

	public SoulBlazeShot(double x, double y, double z, Level world) {
		this(EntityInit.soulBlazeShot, world);
		this.setPos(x, y, z);
	}

	public SoulBlazeShot(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
	}

	public SoulBlazeShot(Level world, LivingEntity entity) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = ItemStack.EMPTY;
	}

	// えんちちーに当たった時の処理
	protected void entityHit(LivingEntity living) {
		this.rangeAttack(living.blockPosition(), this.getDamage(), this.getRange());
	}

	// ブロック着弾
	protected void onHitBlock(BlockHitResult result) {
		this.rangeAttack(result.getBlockPos().above(), this.getDamage() * 0.67F, this.getRange() * 0.67F);
		this.discard();
	}

	public void rangeAttack(BlockPos bPos, float dame, double range) {

		double effectRange = range * range;

		if (this.level instanceof ServerLevel server) {

			BlockPos pos = this.blockPosition();

			for (int ran = 0; ran < 3; ran++) {
				for (int i = 0; i < 3; i++) {
					this.spawnParticleRing(server, ParticleTypes.SOUL_FIRE_FLAME, 2 + ran * 4, pos, -0.25D + i * 0.5D);
				}
			}
		}

		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.isBladTarget(effectRange), range);
		if (entityList.isEmpty()) { return; }

		for (LivingEntity entity : entityList) {
			this.attackDamage(entity, dame, false);
			this.addAttack(entity, dame, this.getAddAttack());
		}
	}

	// デスポーン時効果
	public void despawnAction() {
		if (!this.getArrow()) { return; }
		this.rangeAttack(this.blockPosition().above(), this.getDamage(), this.getRange());
	}

	// パーティクルスポーン
	protected void spawnParticle() {

		Vec3 vec = this.getDeltaMovement();
		float x = (float) (-vec.x / 20F);
		float y = (float) (-vec.y / 20F);
		float z = (float) (-vec.z / 20F);

		for (int i = 0; i < 6; i++) {
			float f1 = (float) (this.getX() - 0.5F + this.rand.nextFloat() + vec.x * i / 4.0F);
			float f2 = (float) (this.getY() - 0.25F + this.rand.nextFloat() * 0.5 + vec.y * i / 4.0D);
			float f3 = (float) (this.getZ() - 0.5F + this.rand.nextFloat() + vec.z * i / 4.0D);
			this.level.addParticle(ParticleTypes.FLAME, f1, f2, f3, x + this.getRandFloat(0.075F), y + this.getRandFloat(0.075F), z + this.getRandFloat(0.075F));
		}
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.FLAME;
	}
}
