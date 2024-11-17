package sweetmagic.init.entity.projectile;

import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;

public class CherryMagicShot extends AbstractMagicShot {

	public CherryMagicShot(EntityType<? extends AbstractMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public CherryMagicShot(double x, double y, double z, Level world) {
		this(EntityInit.cherryMagic, world);
		this.setPos(x, y, z);
	}

	public CherryMagicShot(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
	}

	public CherryMagicShot(Level world, LivingEntity entity, ItemStack stack) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = stack;
		this.setRange(4D);
	}

	// えんちちーに当たった時の処理
	protected void entityHit(LivingEntity living) {
		if (this.getRange() > 0D) {
			this.rangeAttack(living.blockPosition(), (float) this.getDamage() * 0.85F, this.getRange());
		}
	}

	// ブロック着弾
	protected void onHitBlock(BlockHitResult result) {
		if (this.getRange() > 0D) {
			this.rangeAttack(result.getBlockPos().above(), this.getDamage() * 0.67F, this.getRange() * 0.75F);
		}
		this.discard();
	}

	public void rangeAttack (BlockPos bPos, float dame, double range) {

		double effectRange = range * range;

		if (this.level instanceof ServerLevel server) {

			for (double eRange = 0D; eRange < range; eRange++) {
				for (int i = 0; i < Math.max(1, this.getData()); i++) {
					this.spawnParticleRing2(server, ParticleInit.CHERRY_BLOSSOMS_LARGE.get(), 1 + eRange - i, bPos.above(i), -0.05D, -0.35D);
				}
			}
		}

		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.isTarget(), range);
		if (entityList.isEmpty()) { return; }

		boolean isPlayer = this.getOwner() instanceof Player;

		for (LivingEntity entity : entityList) {

			if (!this.checkDistance(entity.blockPosition(), effectRange)) { continue; }
			this.attackDamage(entity, dame, false);

			for (int i = 0; i < 4; i++)
				this.attackDamage(entity, dame * 0.25F, false);

			if (isPlayer) {
				this.addPotion(entity, PotionInit.dig_poison_vulnerable, 1200, Math.max(1, this.getData() - 1));
			}
		}
	}

	// パーティクルスポーン
	protected void spawnParticle() {

		Vec3 vec = this.getDeltaMovement();
		float addX = (float) (-vec.x / 50F);
		float addY = (float) (-vec.y / 50F);
		float addZ = (float) (-vec.z / 50F);
		Random rand = this.rand;

		for (int i = 0; i < 4; i++) {

			float x = addX + this.getRandFloat(0.05F);
			float y = addY + this.getRandFloat(0.05F);
			float z = addZ + this.getRandFloat(0.05F);
			float f1 = (float) (this.getX() - 0.5F + rand.nextFloat() + vec.x * i / 5F);
			float f2 = (float) (this.getY() - 0.25F + rand.nextFloat() * 0.5F + vec.y * i / 5F);
			float f3 = (float) (this.getZ() - 0.5F + rand.nextFloat() + vec.z * i / 5F);

			this.level.addParticle(ParticleInit.CHERRY_BLOSSOMS_LARGE.get(), f1, f2, f3, x, y, z);
		}
	}

	@Override
	public SMElement getElement() {
		return SMElement.EARTH;
	}

	public void spawnParticleRing2(ServerLevel server, ParticleOptions par, double range, BlockPos pos, double addY, double speed) {

		double x = pos.getX() + 0.5D;
		double y = pos.getY() + 1D + addY;
		double z = pos.getZ() + 0.5D;
		Random rand = this.rand;

		for (double degree = -range * Math.PI; degree < range * Math.PI; degree += 0.25D) {

			if (rand.nextFloat() >= 0.1F) { continue; }
			double rate = range * 0.75D;
			server.sendParticles(par, x + Math.cos(degree) * rate, y, z + Math.sin(degree) * rate, 0, Math.cos(degree) * 0.65D, 0, Math.sin(degree) * 0.65D, speed);
		}
	}
}
