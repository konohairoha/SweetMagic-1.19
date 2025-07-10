package sweetmagic.init.entity.projectile;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;

public class WindStormShot extends AbstractMagicShot {

	public boolean isPlayer = false;
	private List<LivingEntity> targetList = new ArrayList<>();

	public WindStormShot(EntityType<? extends AbstractMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public WindStormShot(double x, double y, double z, Level world) {
		this(EntityInit.windBlast, world);
		this.setPos(x, y, z);
	}

	public WindStormShot(Level world, LivingEntity entity, AbstractBossMagic magic, WandInfo wandInfo) {
		this(magic.getX(), magic.getEyeY() - (double) 0.1F, magic.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
		this.setRange(6D);
	}

	public WindStormShot(Level world, LivingEntity entity) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = ItemStack.EMPTY;
	}

	public void tick() {
		super.tick();
		this.rangeAttack(this.blockPosition(), this.getDamage(), this.getRange());
	}

	public void rangeAttack(BlockPos pos, float dame, double range) {

		int data = this.getData();

		if (data == 1) {
			dame *= 1.25F;
			range *= 1.25D;
		}

		int addLevel = 1;
		int time = 600;
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.getFilter(this.isPlayer, pos, range), range);

		for (LivingEntity target : entityList) {
			this.attackDamage(target, dame, false);

			if (data == 1) {

				int level = target.hasEffect(PotionInit.bleeding) ? target.getEffect(PotionInit.bleeding).getAmplifier() + addLevel : addLevel - 1;

				if (level > 0) {
					target.removeEffect(PotionInit.bleeding);
				}

				this.addPotion(target, PotionInit.bleeding, time, Math.max(0, level));
			}
		}
	}

	// パーティクルスポーン
	protected void spawnParticle() {

		Vec3 vec = this.getDeltaMovement();
		ParticleOptions par = ParticleTypes.CLOUD;

		for (int i = 0; i < 8; i++) {
			for (int k = -4; k < 8; k++) {
				float x = (float) (vec.x / 10F) * this.getRandFloat(1.5F);
				float y = (float) (vec.y / 10F) + 0.5F * this.getRandFloat(0.5F);
				float z = (float) (vec.z / 10F) * this.getRandFloat(1.5F);
				this.addParticle(par, this.getX() + this.getRandFloat(1F), this.getY() + 0.5F + k + this.getRandFloat(0.5F), this.getZ() + this.getRandFloat(1F), x, y, z);
			}
		}
	}

	public Predicate<LivingEntity> getFilter(boolean isPlayer, BlockPos pos, double range) {
		return e -> !e.isSpectator() && e.isAlive() && (isPlayer ? e instanceof Player : !(e instanceof Player)) && !(e instanceof ISMMob) && this.checkDistances(pos, e.blockPosition(), range * range) && !this.targetList.contains(e);
	}

	// 範囲内にいるかのチェック
	public boolean checkDistances(BlockPos basePos, BlockPos pos, double range) {
		double d0 = basePos.getX() - pos.getX();
		double d2 = basePos.getZ() - pos.getZ();
		return (d0 * d0 + d2 * d2) <= range;
	}

	// 範囲の取得
	public AABB getAABB(double range) {
		return this.getAABB(range, range * 8D, range);
	}

	protected void spawnParticleCycle(BlockPos pos, double range) {
		if (!(this.getLevel() instanceof ServerLevel server)) { return; }

		ParticleOptions par = ParticleInit.CYCLE_TORNADO;

		for (int i = 0; i < 16; i++) {
			this.spawnParticleCycle(server, par, pos.getX() + 0.5D, pos.getY() - 0.5D + this.rand.nextDouble() * 1.5D, pos.getZ() + 0.5D, Direction.UP, range, i * 16F, false);
		}
	}

	// パーティクルスポーンサイクル
	protected void spawnParticleCycle(ServerLevel server, ParticleOptions par, double x, double y, double z, Direction face, double range, double angle, boolean isRevese) {
		int way = isRevese ? -1 : 1;
		server.sendParticles(par, x, y, z, 0, face.get3DDataValue() * way, range, angle + way * 1 * 6F - this.tickCount * 5, 1F);
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.CYCLON;
	}
}
