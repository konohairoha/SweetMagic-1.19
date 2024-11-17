package sweetmagic.init.entity.projectile;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;

public class RainMagicShot extends AbstractMagicShot {

	public RainMagicShot(EntityType<? extends RainMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public RainMagicShot(double x, double y, double z, Level world) {
		this(EntityInit.rainMagic, world);
		this.setPos(x, y, z);
	}

	public RainMagicShot(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
	}

	// ブロック着弾
	protected void onHitBlock(BlockHitResult result) {

		if (!this.level.isClientSide) {
			this.setLifeTime(0);
			this.setMaxLifeTime(120);
		}

		super.onHitBlock(result);
	}

	public void inGround() {

		if (this.level.isClientSide) { return; }

		if (this.lifeTime % 2 == 0) {
			float dame = this.getPower(this.getWandLevel());
			this.rangeAttack(this.blockPosition(), dame / 4F, this.getRange());
		}

		if (this.lifeTime % 30 == 1) {
			this.spawnParticleCycle(this.rand);
		}
	}

	public void rangeAttack (BlockPos pos, float dame, double range) {

		if (this.stack.isEmpty() || this.level.isClientSide || this.getOwner() == null) { return; }

		Level world = this.level;
		Random rand = this.rand;
		WandInfo wandInfo = new WandInfo(this.stack);
		LivingEntity owner = (LivingEntity) this.getOwner();

		boolean isThunder = this.getData() == 2;
		int rate = isThunder ? 3 : 1;
		int addY = isThunder ? -26 : 0;
		int addSpeed = isThunder ? -10 : 0;

		int rang = (int) range + 1;
		int maxCount = (int) (rang * 1.25) / rate;
		double entityRange = isThunder ? 5D : 2D;

		for (int i = 0; i < maxCount; i++) {
			BlockPos targetPos = pos.offset(rand.nextInt(rang) - rand.nextInt(rang), 10, rand.nextInt(rang) - rand.nextInt(rang));
			AbstractMagicShot entity = this.getMagicShot(world, owner, wandInfo);
			entity.shoot(0D, -0.65D + addSpeed, 0D, 1.35F, 0F);
			entity.setPos(targetPos.getX() + 0.5D, targetPos.getY() + 20.5D + addY, targetPos.getZ() + 0.5D);
			entity.setBaseDamage( entity.getDamage() + dame );
			entity.setChangeParticle(true);
			entity.setData(0);
			entity.setRange(entityRange);
			world.addFreshEntity(entity);
		}
	}

	public AbstractMagicShot getMagicShot (Level world, LivingEntity entity, WandInfo wandInfo) {

		AbstractMagicShot magic = null;

		switch (this.getData()) {
		case 0:
			magic = new MeteorMagicShot(world, entity, wandInfo);
			break;
		case 1:
			magic = new FrostMagicShot(world, entity, wandInfo);
			break;
		case 2:
			magic = new ElectricMagicShot(world, entity, wandInfo);
			break;
		}

		return magic;
	}

	public ParticleOptions getParticle () {
		ParticleOptions particle = null;

		switch (this.getData()) {
		case 0:
			particle = ParticleInit.CYCLE_FIRE.get();
			break;
		case 1:
			particle = ParticleInit.CYCLE_FROST.get();
			break;
		case 2:
			particle = ParticleInit.CYCLE_ELECTRIC.get();
			break;
		}

		return particle;
	}

	protected void spawnParticleCycle (Random rand) {

		if (!(this.level instanceof ServerLevel server)) { return; }

		BlockPos pos = this.blockPosition().above();
		ParticleOptions particle = this.getParticle();

		for (double range = 2.5D; range < 10D; range += 3D) {

			int count = (int) (range / 3D) + 1;
			boolean isReverse = count % 2 == 0;

			for (int i = 0; i < 6 * count; i++) {
				this.spawnParticleCycle(server, particle, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, Direction.UP, range, (i * 60 / count) + rand.nextFloat() * 10D, isReverse);
			}
		}
	}

	// パーティクルスポーンサイクル
	protected void spawnParticleCycle (ServerLevel server, ParticleOptions particle, double x, double y, double z, Direction face, double range, double angle, boolean isRevese) {
		int way = isRevese ? -1 : 1;
		server.sendParticles(particle, x, y, z, 0, face.get3DDataValue() * way, range, angle + way * 1 * 6F, 1F);
	}

	// 火力取得
	public float getPower (float level) {
		return (level * 0.5F) + (level + 1) / (level / 2) + 1;
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.NON;
	}
}
