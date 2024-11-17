package sweetmagic.init.entity.projectile;

import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.entity.monster.boss.Arlaune;

public class CherryRainMagic extends AbstractBossMagic {

	private int rainTime = 0;
	private final static int MAX_RAINTIME = 150;

	private BlockPos pos = null;

	public CherryRainMagic(EntityType<? extends AbstractBossMagic> entityType, Level world) {
		super(entityType, world);
	}

	public CherryRainMagic(double x, double y, double z, Level world) {
		this(EntityInit.cherryRain, world);
		this.setPos(x, y, z);
	}

	public CherryRainMagic(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
	}

	// 常時更新処理
	public void onUpdate () {

		// ターゲットがいる場合
		if ( ( this.target != null && this.target.isAlive() ) || this.pos != null) {

			// 回転の設定
			this.setRotInit();

			if (this.target != null && this.target.isAlive()) {
				this.pos = this.target.blockPosition();
			}

			this.attackMagic(this.target);
		}

		// ターゲットの設定
		if ( this.rainTime <= 0 && ( this.target == null || !this.target.isAlive() ) && this.tickCount % 8 == 0) {
			this.setTarget();
		}
	}

	// 魔法攻撃
	public void attackMagic (LivingEntity target) {

		if (this.level.isClientSide) { return; }

		if (this.rainTime >= 30D) {

			double range = 12.5D;

			if (this.rainTime % 10 == 0) {

				// 対象のえんちちーに攻撃
				float damage = 3F + this.getAddDamage();
				List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, e -> e instanceof Enemy && e.isAlive(), this.getAABB(this.getPos(target), range));
				targetList.forEach(e -> this.attackDamage(e, damage, true));
			}

			if (this.level instanceof ServerLevel server) {

				Random rand = this.rand;
				BlockPos pos = this.getPos(target);
				Iterable<BlockPos> posList = this.getPosRangeList(pos, range);

				for (BlockPos p : posList) {

					if (rand.nextFloat() >= 0.04F) { continue; }

					double x = p.getX() + rand.nextDouble() * 1.5D - 0.75D;
					double y = p.getY() + rand.nextDouble() * 1.5D - 0.75D;
					double z = p.getZ() + rand.nextDouble() * 1.5D - 0.75D;
					float f1 = this.getRandFloat(0.1F);
					float f2 = 0.2F + this.getRandFloat(0.1F);
					float f3 = this.getRandFloat(0.1F);
					server.sendParticles(ParticleInit.CHERRY_BLOSSOMS_LARGE.get(), x, y, z, 0, f1, f2, f3, 1F);
				}
			}
		}

		if (this.rainTime++ >= MAX_RAINTIME) {

			double range = 13D;
			List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, e -> e instanceof Enemy && e.isAlive(), this.getAABB(this.getPos(target), range));

			// 対象のえんちちーに攻撃
			for (LivingEntity entity : targetList) {
				this.attackDamage(entity, 40F, true);
				entity.playSound(SoundEvents.LIGHTNING_BOLT_IMPACT, 1.5F, 1F);

				if (this.level instanceof ServerLevel sever) {

					float x = (float) (entity.xo + this.rand.nextFloat() * 0.5F);
					float y = (float) (entity.getY() + this.rand.nextFloat() * 0.5F + 1.5F);
					float z = (float) (entity.zo + this.rand.nextFloat() * 0.5F);

					for (int i = 0; i < 8; i++) {
						sever.sendParticles(ParticleTypes.FIREWORK, x, y, z, 2, 0F, 0F, 0F, 0.1F);
					}
				}
			}

			this.rainTime = -50;
			this.pos = null;
		}
	}

	// ターゲットの設定
	public void setTarget () {

		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, e -> e instanceof Enemy && e.isAlive(), 48D);
		double dis = 0D;
		this.target = null;

		for (LivingEntity entity : entityList) {

			if (this.target == null) {
				this.target = entity;
				dis = this.distanceToSqr(this.target);
				continue;
			}

			if (dis < this.distanceToSqr(entity)) {
				this.target = entity;
				dis = this.distanceToSqr(this.target);
			}
		}
	}

	public BlockPos getPos (LivingEntity target) {

		if (this.target != null && this.target.isAlive()) {
			return target.blockPosition();
		}

		else if (this.pos != null) {
			return this.pos;
		}

		return this.blockPosition();
	}

	// 召喚えんちちーに取得
	public LivingEntity getEntity () {

		// えんちちーの初期化が出来ていないなら初期化
		if (this.summon == null) {
			Arlaune queen = new Arlaune(this.level);
			queen.setMagic(true);
			this.summon = queen;
		}
		return this.summon;
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.EARTH;
	}
}
