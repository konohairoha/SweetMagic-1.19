package sweetmagic.init.entity.projectile;

import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.SoundInit;

public class FrostMagicShot extends AbstractMagicShot {

	private static final BlockState STATE = Blocks.BLUE_ICE.defaultBlockState();

	public FrostMagicShot(EntityType<? extends FrostMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public FrostMagicShot(double x, double y, double z, Level world) {
		this(EntityInit.frostMagic, world);
		this.setPos(x, y, z);
	}

	public FrostMagicShot(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
	}

	public FrostMagicShot(Level world, LivingEntity entity) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = ItemStack.EMPTY;
	}

	// えんちちーに当たった時の処理
	protected void entityHit(LivingEntity living) {

		int time = 60 * (this.getWandLevel() + 1);

		if ( this.canTargetEffect(living, this.getOwner()) ) {
			this.addPotion(living, PotionInit.frost, time, this.getData());
		}

		else {
			living.removeEffect(PotionInit.frost);
		}

		if (this.getRange() > 0) {
			this.rangeAttack(living.blockPosition(), this.getDamage(), this.getRange());
		}

		this.playSound(SoundInit.FROST, 0.0625F, 1F);
	}

	// ブロック着弾
	protected void onHitBlock(BlockHitResult result) {

		if (this.getChangeParticle()) {
			this.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 0.1F);
		}

		if (this.getRange() > 0) {
			this.rangeAttack(result.getBlockPos().above(), this.getDamage(), this.getRange());
		}

		super.onHitBlock(result);
		this.discard();
	}

	public void rangeAttack(BlockPos bPos, float dame, double range) {

		boolean isTier3 = this.getData() >= 2;
		boolean isTier4 = this.getData() >= 3;
		double effectRange = range * range;

		if (isTier3 && this.getLevel() instanceof ServerLevel server) {
			this.rangeParticle(server, bPos, range, effectRange, isTier3, isTier4);
		}

		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, range);
		if (entityList.isEmpty()) { return; }

		Entity owner = this.getOwner();
		int time = 60 * (this.getWandLevel() + 1);
		int level = isTier4 ? 1 : 0;

		for (LivingEntity entity : entityList) {

			if (!this.canTargetEffect(entity, owner) || !this.checkDistance(entity.blockPosition(), effectRange)) { continue; }
			this.attackDamage(entity, dame, false);

			if (isTier3) {
				this.addPotion(entity, PotionInit.flost_water_vulnerable, 1200, level);
				this.addPotion(entity, PotionInit.frost, time, 2 + level);
			}
		}
	}

	public void rangeParticle(ServerLevel server, BlockPos bPos, double range, double effectRange, boolean isTier3, boolean isTier4) {

		// 範囲の座標取得
		Random rand = this.rand;
		boolean isChange = this.getChangeParticle();
		SimpleParticleType par = ParticleInit.FROST;
		Iterable<BlockPos> pList = this.getPosRangeList(bPos, range);

		if (!isChange) {
			for (BlockPos pos : pList) {
				if(!this.checkDistance(pos, effectRange)) { continue; }

				double x = pos.getX() + rand.nextDouble() * 1.5D - 0.75D;
				double y = pos.getY() + rand.nextDouble() * 0.25D + 0.25D;
				double z = pos.getZ() + rand.nextDouble() * 1.5D - 0.75D;
				float xSpeed = this.getRandFloat(0.5F);
				float ySpeed = 0.25F + rand.nextFloat() * 0.25F;
				float zSpeed = this.getRandFloat(0.5F);
				server.sendParticles(par, x, y, z, 0, xSpeed, ySpeed, zSpeed, 0.25F);
			}

			for (int i = 0; i < 16; i ++) {
				server.sendParticles(this.getParticle(STATE), this.getX(), this.getY(), this.getZ(), 0, 0F, 0F, 0F, 1F);
			}
		}

		if (isTier4) {

			int count = 16;
			BlockPos pos = this.blockPosition();
			ParticleOptions par2 = ParticleInit.CYCLE_FROST_TORNADO;

			for (int y = -40; y < 8; y++) {
				for (int i = 0; i < count; i++) {
					this.spawnParticleCycle(server, par2, pos.getX() + 0.5D, pos.getY() - 0.5D + rand.nextDouble() * 1.5D + y * 0.5D, pos.getZ() + 0.5D, Direction.UP, 3, i * 16F + y * 15, false);
				}
			}

			this.playSound(SoundEvents.BLASTFURNACE_FIRE_CRACKLE, 3F, 1F);
		}

		else {
			double ran = isChange ? 0.5D : 1D;

			for (int i = 0; i < 4; i++) {
				this.spawnParticleRingY(server, par, ran, bPos.above(3 + i), range / 15D, -0.3D);
				this.spawnParticleRingY(server, par, ran, bPos.above(2 + i), range / 30D, -0.2D);
				this.spawnParticleRingY(server, par, ran, bPos.above(1 + i), range / 60D, -0.125D);
			}
		}

		this.playSound(SoundInit.FROST, 0.25F, 1F);
	}

	protected void spawnParticleShort(ServerLevel sever, BlockPos pos) {
		float x = (float) (pos.getX() + this.getRandFloat(0.25F));
		float y = (float) (pos.getY() + this.getRandFloat(0.25F));
		float z = (float) (pos.getZ() + this.getRandFloat(0.25F));

		for (int i = 0; i < 3; i++) {
			sever.sendParticles(ParticleInit.FROST, x, y, z, 4, 0F, 0F, 0F, 0.15F);
		}
	}

	public int getMinParticleTick() {
		return 3;
	}

	// パーティクルスポーン
	protected void spawnParticle() {

		Vec3 vec = this.getDeltaMovement();
		float x = (float) (-vec.x / 20F);
		float y = (float) (-vec.y / 20F);
		float z = (float) (-vec.z / 20F);

		if (this.getChangeParticle()) {
			if (this.lifeTime > 1) { return; }
			this.addParticle(ParticleInit.CRYSTAL, this.getX() - 0.5F, this.getY() - 10.5F, this.getZ() - 0.5F, x, vec.y, z);
		}

		else {

			boolean isArrow = this.getArrow() && this.onGround;
			Random rand = this.rand;
			if (isArrow && rand.nextFloat() >= 0.1F) { return; }

			int count = isArrow ? 1 : 4;

			for (int i = 0; i < count; i++) {
				float f1 = (float) (this.getX() - 0.5F + rand.nextFloat() + vec.x * i / 4D);
				float f2 = (float) (this.getY() - 0.25F + rand.nextFloat() * 0.5 + vec.y * i / 4D);
				float f3 = (float) (this.getZ() - 0.5F + rand.nextFloat() + vec.z * i / 4D);
				this.addParticle(ParticleInit.FROST, f1, f2, f3, x + this.getRandFloat(0.075F), y + this.getRandFloat(0.075F), z + this.getRandFloat(0.075F));
			}
		}
	}

	// パーティクルスポーンサイクル
	protected void spawnParticleCycle(ServerLevel server, ParticleOptions par, double x, double y, double z, Direction face, double range, double angle, boolean isRevese) {
		int way = isRevese ? -1 : 1;
		server.sendParticles(par, x, y, z, 0, face.get3DDataValue() * way, range, angle + way * 1 * 6F - this.tickCount * 5, 1F);
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.FROST;
	}
}
