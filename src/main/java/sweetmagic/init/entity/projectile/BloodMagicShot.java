package sweetmagic.init.entity.projectile;

import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
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
import sweetmagic.init.ParticleInit;

public class BloodMagicShot extends AbstractMagicShot {

	public BloodMagicShot(EntityType<? extends BloodMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public BloodMagicShot(double x, double y, double z, Level world) {
		this(EntityInit.bloodMagic, world);
		this.setPos(x, y, z);
	}

	public BloodMagicShot(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
	}

	public BloodMagicShot(Level world, LivingEntity entity) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = ItemStack.EMPTY;
		this.setRange(5D);
	}

	// えんちちーに当たった時の処理
	protected void entityHit(LivingEntity living) {

		living.invulnerableTime = 0;

		if (this.getData() >= 1) {
			float rate = this.getData() == 1 ? 0.67F : 1F;
			this.rangeAttack(living.blockPosition(), (float) this.getDamage() * rate, this.getRange());
		}
	}

	// ブロック着弾
	protected void onHitBlock(BlockHitResult result) {

		if (this.getData() >= 1) {
			float rate = this.getData() == 1 ? 0.33F : 0.67F;
			this.rangeAttack(result.getBlockPos().above(), this.getDamage() * rate, this.getRange() * 0.67F);
		}

		this.discard();
	}

	public void rangeAttack(BlockPos bPos, float dame, double range) {

		int data = this.getData();
		double effectRange = range * range;
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.isBladTarget(effectRange), range);

		if (data <= 2) {
			entityList.forEach(e -> this.attackDamage(e, dame, false));
		}

		else {
			float baseAddAttack = (data - 3) * 25F;
			entityList.forEach(e -> {
				this.attackDamage(e, dame, false);
				this.addAttack(e, dame, (int) (e.getMaxHealth() / (100F - baseAddAttack)));
			});
		}

		if (!(this.getLevel() instanceof ServerLevel server)) { return; }

		if (data <= 1) {

			double moveSpeed = range * 0.175D;
			double ySpeed = 0.45D + (range >= 4 ? range * -0.04D : 0D);

			for (int i= -1; i < 3; i++) {
				this.spawnParticleRing(server, ParticleInit.BLOOD, range, bPos.above(i), ySpeed, moveSpeed);
			}
		}

		else if (data <= 2) {

			BlockPos pos = this.blockPosition();
			ParticleOptions particle = ParticleInit.CYCLE_BLOOD;

			for (double dis = 0.5D; dis < range; dis += 0.5D) {
				for (int i = 0; i < 8; i++) {
					this.spawnParticleCycle(server, particle, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, Direction.UP, dis, (i * 45) + dis * 12D, true);
				}
			}
		}

		else {

			BlockPos pos = this.blockPosition();
			ParticleOptions par = ParticleInit.CYCLE_BLOOD_TORNADO;

			for (int y = -40; y < 8; y++) {
				for (int i = 0; i < 16; i++) {
					this.spawnParticleCycle(server, par, pos.getX() + 0.5D, pos.getY() - 0.5D + this.rand.nextDouble() * 1.5D + y * 0.5D, pos.getZ() + 0.5D, Direction.UP, 2, i * 16F + y * 15, false);
				}
			}

			double x = pos.getX() + 0.5D;
			double z = pos.getZ() + 0.5D;
			range = Math.sqrt(range);

			for (int i = 0; i < 4; i++) {
				double y = pos.getY() + 1D + i;
				for (double degree = -range * Math.PI; degree < range * Math.PI; degree += 0.25D) {
					double rate = range * 0.75D;
					server.sendParticles(ParticleInit.BLOOD, x + Math.cos(degree) * rate, y, z + Math.sin(degree) * rate, 0, Math.cos(degree) * 0.85D, this.rand.nextFloat() * 0.15F, Math.sin(degree) * 0.85D, 1D);
				}
			}
		}
	}

	protected void spawnParticleShort(ServerLevel sever, BlockPos pos) {
		float x = (float) pos.getX() + this.getRandFloat(0.5F);
		float y = (float) pos.getY() + this.getRandFloat(0.5F);
		float z = (float) pos.getZ() + this.getRandFloat(0.5F);

		for (int i = 0; i < 4; i++) {
			sever.sendParticles(ParticleInit.BLOOD, x, y, z, 4, 0F, 0F, 0F, 0.15F);
		}
	}

	public int getMinParticleTick () {
		return 3;
	}

	// パーティクルスポーン
	protected void spawnParticle() {

		Vec3 vec = this.getDeltaMovement();
		float addX = (float) -vec.x / 20F;
		float addY = (float) -vec.y / 20F;
		float addZ = (float) -vec.z / 20F;
		Random rand = this.rand;

		for (int i = 0; i < 6; i++) {

			float x = addX + this.getRandFloat(0.075F);
			float y = addY + this.getRandFloat(0.075F);
			float z = addZ + this.getRandFloat(0.075F);
			float f1 = (float) (this.getX() - 0.5F + rand.nextFloat() + vec.x * i / 4F);
			float f2 = (float) (this.getY() - 0.25F + rand.nextFloat() * 0.5F + vec.y * i / 4F);
			float f3 = (float) (this.getZ() - 0.5F + rand.nextFloat() + vec.z * i / 4F);

			this.addParticle(ParticleInit.BLOOD, f1, f2, f3, x, y, z);
		}
	}

	// パーティクルスポーンサイクル
	protected void spawnParticleCycle(ServerLevel server, ParticleOptions particle, double x, double y, double z, Direction face, double range, double angle, boolean isRevese) {
		int way = isRevese ? -1 : 1;
		server.sendParticles(particle, x, y, z, 0, face.get3DDataValue() * way, range, angle + way * 1 * 6F, 1F);
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.DARK;
	}
}
