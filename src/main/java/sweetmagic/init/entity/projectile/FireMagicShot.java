package sweetmagic.init.entity.projectile;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.PotionInit;

public class FireMagicShot extends AbstractMagicShot {

	private static final BlockState STATE = Blocks.MAGMA_BLOCK.defaultBlockState();

	public FireMagicShot(EntityType<? extends FireMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public FireMagicShot(double x, double y, double z, Level world) {
		this(EntityInit.fireMagic, world);
		this.setPos(x, y, z);
	}

	public FireMagicShot(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
	}

	public FireMagicShot(Level world, LivingEntity entity, ItemStack stack) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = stack;
	}

	// えんちちーに当たった時の処理
	protected void entityHit(LivingEntity living) {

		int time = 60 * (this.getWandLevel() + 1);

		if (living instanceof Player && this.getOwner() instanceof Player) {
			this.addPotion(living, MobEffects.FIRE_RESISTANCE, time, 0);
		}

		else {
			this.addPotion(living, PotionInit.flame, time, this.getData());
		}

		if (this.getData() >= 1) {
			this.rangeAttack(living.blockPosition(), (float) this.getDamage() * 0.25F, this.getRange());
		}

		else {
			this.hitToSpawnParticle();
		}

		if (this.getRange() > 0 && this.getData() == 0) {
			this.rangeAttack(living.blockPosition(), this.getDamage(), this.getRange());
		}
	}

	// ブロック着弾
	protected void onHitBlock(BlockHitResult result) {

		if (this.getData() >= 1 && !this.getBlockPenetration()) {
			this.rangeAttack(result.getBlockPos().above(), this.getDamage() * 0.1F, this.getRange() * 0.67F);
		}

		else {
			this.hitToSpawnParticle();
		}

		if (this.getRange() > 0 && this.getData() == 0) {
			this.rangeAttack(result.getBlockPos().above(), this.getDamage(), this.getRange());
		}

		this.discard();
	}

	public void rangeAttack (BlockPos bPos, float dame, double range) {

		double effectRange = range * range;
		boolean isTier3 = this.getData() >= 2;

		if (this.level instanceof ServerLevel server) {

			// 範囲の座標取得
			RandomSource rand = this.random;
			Iterable<BlockPos> pList = BlockPos.betweenClosed(bPos.offset(-range, 0, -range), bPos.offset(range, 0, range));

			for (BlockPos pos : pList) {

				if(!this.checkDistance(pos, effectRange)) { continue; }

				double x = pos.getX() + rand.nextDouble() * 1.5D - 0.75D;
				double y = pos.getY() + rand.nextDouble() * 0.25D + 0.25D;
				double z = pos.getZ() + rand.nextDouble() * 1.5D - 0.75D;
				float xSpeed = this.getRandFloat(0.5F);
				float ySpeed = 0.25F + rand.nextFloat() * 0.25F;
				float zSpeed = this.getRandFloat(0.5F);
				server.sendParticles(ParticleTypes.FLAME, x, y, z, 0, xSpeed, ySpeed, zSpeed, 0.25F);
			}

			if (isTier3) {
				for (int i = 0; i < 16; i ++) {
					server.sendParticles(this.getParticle(STATE), this.getX(), this.getY() + 0.0D, this.getZ(), 0, 0F, 0F, 0F, 1F);
				}

				for (int i = 0; i < 4; i++) {
					this.spawnParticleRingY(server, ParticleTypes.FLAME, 1D, bPos.above(3 + i), range / 15D, -0.3D);
					this.spawnParticleRingY(server, ParticleTypes.FLAME, 1D, bPos.above(2 + i), range / 30D, -0.2D);
					this.spawnParticleRingY(server, ParticleTypes.FLAME, 1D, bPos.above(1 + i), range / 60D, -0.125D);
				}

				this.playSound(SoundEvents.LIGHTNING_BOLT_IMPACT, 3F, 1F);
			}
		}

		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.isBladTarget(effectRange), range);
		if (entityList.isEmpty()) { return; }

		int time = 60 * (this.getWandLevel() + 1);

		for (LivingEntity entity : entityList) {
			this.attackDamage(entity, dame, false);
			this.addPotion(entity, PotionInit.flame, time, 0);

			if (isTier3) {
				this.addPotion(entity, PotionInit.flame_explosion_vulnerable, 1200, 0);
			}
		}
	}

	// デスポーン時効果
	public void despawnAction() {
		if (!this.getArrow()) { return; }
		this.rangeAttack(this.blockPosition().above(), this.getDamage(), this.getRange());
	}

	protected void spawnParticleShort(ServerLevel sever, BlockPos pos) {
		float x = (float) (pos.getX() + this.getRandFloat(0.25F));
		float y = (float) (pos.getY() + this.getRandFloat(0.25F));
		float z = (float) (pos.getZ() + this.getRandFloat(0.25F));

		for (int i = 0; i < 3; i++) {
			sever.sendParticles(ParticleTypes.FLAME, x, y, z, 4, 0F, 0F, 0F, 0.15F);
		}
	}

	public int getMinParticleTick () {
		return 3;
	}

	// パーティクルスポーン
	protected void spawnParticle() {

		Vec3 vec = this.getDeltaMovement();
		float x = (float) (-vec.x / 20F);
		float y = (float) (-vec.y / 20F);
		float z = (float) (-vec.z / 20F);
		RandomSource rand = this.random;

		for (int i = 0; i < 6; i++) {
			float f1 = (float) (this.getX() - 0.5F + rand.nextFloat() + vec.x * i / 4.0F);
			float f2 = (float) (this.getY() - 0.25F + rand.nextFloat() * 0.5 + vec.y * i / 4.0D);
			float f3 = (float) (this.getZ() - 0.5F + rand.nextFloat() + vec.z * i / 4.0D);
			this.level.addParticle(ParticleTypes.FLAME, f1, f2, f3, x + this.getRandFloat(0.075F), y + this.getRandFloat(0.075F), z + this.getRandFloat(0.075F));
		}
	}

	public void hitToSpawnParticle () {

		if (this.level instanceof ServerLevel server) {

			RandomSource rand = this.random;
			BlockPos pos = this.blockPosition();

			for (int i = 0; i < 4; i++) {
				double x = pos.getX() + rand.nextDouble() * 3D - 1.5D;
				double y = pos.getY() + rand.nextDouble() * 1.5D - 0.75D;
				double z = pos.getZ() + rand.nextDouble() * 3D - 1.5D;
				server.sendParticles(ParticleTypes.FLAME, x, y, z, 0, 0F, 0F, 0F, 0.1F);
			}
		}
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.FLAME;
	}
}
