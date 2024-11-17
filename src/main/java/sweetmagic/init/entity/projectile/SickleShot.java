package sweetmagic.init.entity.projectile;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.entity.animal.AbstractSummonMob;

public class SickleShot extends AbstractMagicShot {

	public SickleShot(EntityType<? extends AbstractMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public SickleShot(double x, double y, double z, Level world) {
		this(EntityInit.sickleShot, world);
		this.setPos(x, y, z);
	}

	public SickleShot(Level world, LivingEntity entity, ItemStack stack) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = stack;
	}

	public void tick() {
		super.tick();
		this.setDeltaMovement(this.getDeltaMovement().scale(0.8D));

		if (this.tickCount % 10 == 0) {
			this.rangeAttack(this.blockPosition(), this.getDamage(), this.getRange());
		}
	}

	public void rangeAttack (BlockPos pos, float dame, double range) {

		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.getFilter(pos, range), range);
		this.playSound(SoundInit.SWING, 0.2F, 1F);

		for (LivingEntity entity : entityList) {
			this.attackDamage(entity, this.getDamage() * 0.25F, false);
		}
	}

	public void spawnParticle (ServerLevel server) {
		if (this.tickCount % 30 == 0) {
			this.spawnParticleCycle(server, this.xo, this.yo + 1.15D, this.zo, 0.85D, this.rand, 10);
		}
	}

	protected void spawnParticleCycle (ServerLevel server, double x, double y, double z, double range, Random rand, int count) {
		for (int i = 0; i < count; i++) {
			this.spawnParticleCycle(server, ParticleInit.CYCLE_TOXIC.get(), x, y, z, Direction.UP, range, i * 36F, false);
			this.spawnParticleCycle(server, ParticleInit.CYCLE_TOXIC.get(), x, y, z, Direction.NORTH, range, i * 36F, false);
			this.spawnParticleCycle(server, ParticleInit.CYCLE_TOXIC.get(), x, y, z, Direction.EAST, range, i * 36F, false);
		}
	}

	// パーティクルスポーンサイクル
	protected void spawnParticleCycle (ServerLevel server, ParticleOptions particle, double x, double y, double z, Direction face, double range, double angle, boolean isRevese) {
		int way = isRevese ? -1 : 1;
		server.sendParticles(particle, x, y, z, 0, face.get3DDataValue() * way, range, angle + way * 1 * 6F, 1F);
	}

	public Predicate<LivingEntity> getFilter (BlockPos pos, double range) {
		return e -> !e.isSpectator() && e.isAlive() && this.checkDistances(pos, e.blockPosition(), range * range);
	}

	// 範囲内にいるかのチェック
	public boolean checkDistances (BlockPos basePos, BlockPos pos, double range) {
		double d0 = basePos.getX() - pos.getX();
		double d1 = basePos.getY() - pos.getY();
		double d2 = basePos.getZ() - pos.getZ();
		return (d0 * d0 + d1 * d1 + d2 * d2) <= range;
	}

	public boolean isPlayer (Entity entity) {
		return entity instanceof Player || entity instanceof AbstractSummonMob;
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.TOXIC;
	}
}
