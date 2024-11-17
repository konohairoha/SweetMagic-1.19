package sweetmagic.init.entity.projectile;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.animal.AbstractSummonMob;

public class TripleTornadoShot extends AbstractMagicShot {

	public boolean isPlayer = false;

	public TripleTornadoShot(EntityType<TripleTornadoShot> entityType, Level world) {
		super(entityType, world);
		this.setMaxLifeTime(80);
	}

	public TripleTornadoShot(double x, double y, double z, Level world) {
		this(EntityInit.tripleTornado, world);
		this.setPos(x, y, z);
	}

	public TripleTornadoShot(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
	}

	public TripleTornadoShot(Level world, LivingEntity entity, ItemStack stack) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = stack;
	}

	public void tick() {
		super.tick();

		// 移動の無効化
		this.setDeltaMovement(new Vec3(0, 0, 0));

		// 常時発動効果
		this.tickEffect();
	}

	// 常時発動効果
	public void tickEffect () {

		BlockPos pos = this.blockPosition();
		double range = this.getRange() / 2D;

		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer, pos, range), range);
		entityList.forEach(e -> this.addPotion(e, MobEffects.MOVEMENT_SLOWDOWN, 1000, 10));

		if (this.tickCount % 5 == 0 && this.level instanceof ServerLevel sever) {

			double scaleRate = Math.min(1D, this.tickCount * 0.05D);
			double scale = range * scaleRate;

			for (double i = scale; i > 1; i -= 2) {
				this.spawnParticleCycle(pos, i);
			}

			if (this.tickCount % 10 == 0) {
				this.playSound(SoundEvents.BLAZE_SHOOT, 0.67F, 1.15F);
			}
		}

		// チャージ完了
		if (this.tickCount >= this.getMaxLifeTime()) {

			// 対象のえんちちーに攻撃
			float damage = this.getDamage();
			List<LivingEntity> attackList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer, pos, range), range);
			attackList.forEach(e -> this.attackDamage(e, e instanceof Enemy ? damage * 2F : damage, false));

			if (this.level instanceof ServerLevel sever) {

				// 範囲の座標取得
				Iterable<BlockPos> posList = BlockPos.betweenClosed(pos.offset(-range, -range, -range), pos.offset(range, range, range));
				Random rand = this.rand;

				for (BlockPos p : posList) {

					if (rand.nextFloat() >= 0.2F || !this.checkDistances(pos, p, range * range)) { continue; }

					float x = (float) (p.getX() + rand.nextFloat() - 0.5F);
					float y = (float) (p.getY() + rand.nextFloat() - 0.5F);
					float z = (float) (p.getZ() + rand.nextFloat() - 0.5F);
					sever.sendParticles(ParticleTypes.END_ROD, x, y, z, 0, this.getRandFloat(0.5F), rand.nextFloat() * 0.5F, this.getRandFloat(0.5F), 1F);
				}
			}

			this.playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 2F, 1F);
			this.discard();
		}

		else if (this.tickCount > 20 && this.tickCount % 10 == 0) {

			float damage = 1F;
			List<LivingEntity> attackList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer, this.blockPosition(), range), range);

			// 対象のえんちちーに攻撃
			for (LivingEntity entity : attackList) {
				this.addPotion(entity, PotionInit.resistance_blow, 11, 5);
				this.attackDamage(entity, damage, false);
			}
		}
	}

	public Predicate<LivingEntity> getFilter (boolean isPlayer, BlockPos pos, double range) {
		return e -> !e.isSpectator() && e.isAlive() && (isPlayer ? (e instanceof Player || e instanceof AbstractSummonMob) : !(e instanceof Player) ) && !(e instanceof ISMMob) && this.checkDistances(pos, e.blockPosition(), range * range);
	}

	// 範囲内にいるかのチェック
	public boolean checkDistances (BlockPos basePos, BlockPos pos, double range) {
		double d0 = basePos.getX() - pos.getX();
		double d1 = basePos.getY() - pos.getY();
		double d2 = basePos.getZ() - pos.getZ();
		return (d0 * d0 + d1 * d1 + d2 * d2) <= range;
	}

	protected void spawnParticleCycle (BlockPos pos, double range) {

		if ( !(this.level instanceof ServerLevel server)) { return; }

		int count = 16;
		Random rand = new Random();

		for (int i = 0; i < count; i++) {
			this.spawnParticleCycle(server, ParticleInit.CYCLE_TORNADO.get(), pos.getX() + 0.5D, pos.getY() - 0.5D + rand.nextDouble() * 1.5D, pos.getZ() + 0.5D, Direction.UP, range, i * 16F, false);
		}
	}

	// パーティクルスポーンサイクル
	protected void spawnParticleCycle (ServerLevel server, ParticleOptions particle, double x, double y, double z, Direction face, double range, double angle, boolean isRevese) {
		int way = isRevese ? -1 : 1;
		server.sendParticles(particle, x, y, z, 0, face.get3DDataValue() * way, range, angle + way * 1 * 6F - this.tickCount * 5, 1F);
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.CYCLON;
	}
}
