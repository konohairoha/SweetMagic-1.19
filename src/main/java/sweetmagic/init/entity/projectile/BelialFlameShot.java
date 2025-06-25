package sweetmagic.init.entity.projectile;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;

public class BelialFlameShot extends AbstractMagicShot {

	public BelialFlameShot(EntityType<? extends AbstractMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public BelialFlameShot(double x, double y, double z, Level world) {
		this(EntityInit.belialFlameShot, world);
		this.setPos(x, y, z);
	}

	public BelialFlameShot(Level world, LivingEntity entity) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = ItemStack.EMPTY;
	}
	// ブロック着弾
	protected void onHitBlock(BlockHitResult result) {
		this.rangeAttack(null, this.getRange());
		super.onHitBlock(result);
		this.discard();
	}

	public void rangeAttack(LivingEntity living, double range) {

		this.playSound(SoundEvents.LIGHTNING_BOLT_IMPACT, 1F, 1F);
		float damage = this.getDamage();
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.isTarget(), range);
		entityList.forEach(e -> this.attackDamage(e, damage, false));

		for(LivingEntity entity : entityList) {
			this.attackDamage(entity, damage, false);
			this.addPotion(entity, PotionInit.belial_flame, 240, 0);
		}

		if (!(this.getLevel() instanceof ServerLevel sever)) { return; }

		double effectRange = range * range;
		Iterable<BlockPos> pList = this.getPosRangeList(this.blockPosition(), range);

		for (BlockPos pos : pList) {
			if(!this.checkDistance(pos, effectRange)) { continue; }

			double x = pos.getX() + this.rand.nextDouble() * 1.5D - 0.75D;
			double y = pos.getY() + this.rand.nextDouble() * 0.25D + 0.25D;
			double z = pos.getZ() + this.rand.nextDouble() * 1.5D - 0.75D;
			float xSpeed = this.getRandFloat(0.5F);
			float ySpeed = 0.25F + rand.nextFloat() * 0.25F;
			float zSpeed = this.getRandFloat(0.5F);
			sever.sendParticles(ParticleInit.BELIAL_FLAME, x, y, z, 0, xSpeed, ySpeed, zSpeed, 0.25F);
		}
	}

	// パーティクルスポーン
	protected void spawnParticle() {

		Vec3 vec = this.getDeltaMovement();

		if(this.tickCount % 3 == 0) {
			float x = (float) (-vec.x / 30F);
			float y = (float) (-vec.y / 30F);
			float z = (float) (-vec.z / 30F);
			this.addParticle(ParticleInit.CYCLONE, this.getX(), this.getY() + 0.5F, this.getZ(), x, y, z);
		}

		float x = (float) (-vec.x / 20F);
		float y = (float) (-vec.y / 20F);
		float z = (float) (-vec.z / 20F);

		for (int i = 0; i < 6; i++) {
			float f1 = (float) (this.getX() - 0.5F + this.rand.nextFloat() + vec.x * i / 4D);
			float f2 = (float) (this.getY() - 0.25F + this.rand.nextFloat() * 0.5 + vec.y * i / 4D);
			float f3 = (float) (this.getZ() - 0.5F + this.rand.nextFloat() + vec.z * i / 4D);
			this.addParticle(ParticleInit.BELIAL_FLAME, f1, f2, f3, x + this.getRandFloat(0.075F), y + this.getRandFloat(0.075F), z + this.getRandFloat(0.075F));
		}
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.FLAME;
	}
}
