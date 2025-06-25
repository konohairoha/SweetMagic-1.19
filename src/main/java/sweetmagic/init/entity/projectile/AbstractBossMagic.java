package sweetmagic.init.entity.projectile;

import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.TagInit;
import sweetmagic.init.entity.monster.boss.QueenFrost;
import sweetmagic.util.WorldHelper;

public abstract class AbstractBossMagic extends AbstractMagicShot {

	protected int tickTime = 0;
	protected LivingEntity summon = null;
	protected LivingEntity target = null;
	private static final EntityDataAccessor<Float> ROT = setEntityData(EntityDataSerializers.FLOAT);

	public AbstractBossMagic(EntityType<? extends AbstractBossMagic> entityType, Level world) {
		super(entityType, world);
	}

	public AbstractBossMagic(EntityType<? extends AbstractBossMagic> entityType, double x, double y, double z, Level world) {
		this(entityType, world);
		this.setPos(x, y, z);
	}

	public AbstractBossMagic(EntityType<? extends AbstractBossMagic> entityType, LivingEntity entity, Level world) {
		this(entityType, entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.define(ROT, 0F);
	}

	// 回転率の取得
	public float getRotData() {
		return this.get(ROT);
	}

	// 回転率の設定
	public void setRotData(float rot) {
		this.set(ROT, rot);
	}

	public void tick() {
		super.tick();

		// 移動の無効化
		this.setDeltaMovement(new Vec3(0, 0, 0));

		// 常時スポーンパーティクル
		this.tickSpawnParticle();

		// 召喚時パーティクル
		this.summonSpawnParticle();

		// 常時更新処理
		this.onUpdate();

		// プレイヤーへテレポート
		this.teleportToPlayer();
	}

	public void teleportToPlayer() {
		if(this.tickCount % 100 != 0 || this.target != null || this.getOwner() == null || this.getOwner().distanceTo(this) < 256F) { return; }
		this.teleportTo(this.getOwner().getX(), this.getOwner().getY(), this.getOwner().getZ() + 1);
	}

	// 常時更新処理
	abstract void onUpdate();

	// 常時スポーンパーティクル
	public void tickSpawnParticle() { }

	// 召喚時スポーンパーティクル
	public void summonSpawnParticle() {
		if (this.tickCount != 1 || !(this.getLevel() instanceof ServerLevel server)) { return; }

		BlockPos pos = this.blockPosition();
		double range = 1D;
		double ySpeed = 0.35D;

		for (int i= 0; i < 4; i++) {
			this.spawnParticleRing(server, ParticleInit.NORMAL, range, pos.below(2), i / 3D, ySpeed, 0D);
		}
	}

	// 回転の設定
	public void setRotInit() {
		if (this.target == null || !this.target.isAlive()) { return; }

		double d1 = this.target.getX() - this.getX();
		double d2 = this.target.getZ() - this.getZ();
		float rot = -((float) Math.atan2(d1, d2)) * (180F / (float) Math.PI);
		this.setYRot(rot);
		this.setRotData(-rot);
	}

	public <T extends Entity> List<T> getEntityList(Class<T> enClass, AABB aabb) {
		return WorldHelper.getEntityList(this, enClass, aabb);
	}

	// 範囲の取得
	public AABB getAABB(double x, double y, double z, boolean flag) {
		double pX = this.getX() + x;
		double pY = this.getY() + y;
		double pZ = this.getZ() + z;
		return new AABB(pX - 1.5D, pY - 1.5D, pZ - 1.5D, pX + 1.5D, pY + 1.5D, pZ + 1.5D);
	}

	// 召喚えんちちーに取得
	public LivingEntity getEntity() {

		// えんちちーの初期化が出来ていないなら初期化
		if (this.summon == null) {

			switch (this.getData()) {
			case 0:
				QueenFrost queen = new QueenFrost(EntityInit.queenFrost, this.getLevel());
				queen.setArmor(3);
				queen.setMagic(true);
				this.summon = queen;
				break;
			}
		}
		return this.summon;
	}

	// 時間経過
	protected void tickDespawn() {

		// 最大生存時間を超えたらエンティティを削除
		if (this.getLifeTime() >= this.getMaxLifeTime() && this.getLevel() instanceof ServerLevel server) {
			this.discordParticle(server);
		}

		super.tickDespawn();
	}

	// 消滅時のパーティクル
	public void discordParticle(ServerLevel server) {

		this.playSound(SoundEvents.BUBBLE_COLUMN_UPWARDS_INSIDE, 1F, 2F);
		Random rand = this.rand;
		BlockPos pos = this.blockPosition().above();
		ParticleOptions par = ParticleInit.NORMAL;

		for (int i = 0; i < 64; ++i) {
			double d0 = this.getRand(rand) * 0.3D;
			double d1 = rand.nextFloat() * 0.4D;
			double d2 = this.getRand(rand) * 0.3D;
			server.sendParticles(par, this.getRandomX(pos, rand), this.getRandomY(pos, rand), this.getRandomZ(pos, rand), 0, d0, d1 + 0.1D, d2, 1F);
		}
	}

	public double getRand(Random rand) {
		return rand.nextDouble() - rand.nextDouble();
	}

	public double getRandomX(BlockPos pos, Random rand) {
		return pos.getX() + (this.getRand(rand) * 0.25D) + 0.5D;
	}

	public double getRandomY(BlockPos pos, Random rand) {
		return pos.getY() + this.getRand(rand) * 0.5D - 1D;
	}

	public double getRandomZ(BlockPos pos, Random rand) {
		return pos.getZ() + (this.getRand(rand) * 0.25D) + 0.5D;
	}

	// えんちちーソート
	public int sortEntity(Entity mob, Entity entity1, Entity entity2) {
		if (entity1 == null || entity2 == null) { return 0; }

		boolean isBoss1 = entity1.getType().is(TagInit.BOSS);
		boolean isBoss2 = entity2.getType().is(TagInit.BOSS);
		if (isBoss1 && isBoss2) { return 0; }
		if (isBoss1) { return -1; }
		if (isBoss2) { return 1; }

		double distance1 = mob.distanceToSqr(entity1);
		double distance2 = mob.distanceToSqr(entity2);

		if (distance1 > distance2) { return 1; }
		else if (distance1 < distance2) { return -1; }

		return 0;
	}

	// パーティクルスポーン
	public void spawnParticleRing(ServerLevel server, ParticleOptions par, double range, BlockPos pos, double addY, double ySpeed, double moveValue) {

		double x = pos.getX() + 0.5D;
		double y = pos.getY() + 1D + addY;
		double z = pos.getZ() + 0.5D;

		for (double degree = -range * Math.PI; degree < range * Math.PI; degree += 0.25D) {
			double rate = range * 0.75D;
			server.sendParticles(par, x + Math.cos(degree) * rate, y, z + Math.sin(degree) * rate, 0, Math.cos(degree) * 0.1D, ySpeed, Math.sin(degree) * 0.1D, 1D);
		}
	}

	protected void spawnParticleCycle(BlockPos pos, double range, Random rand) {
		if (!(this.getLevel() instanceof ServerLevel server) || pos == null) { return; }

		int count = 18;
		ParticleOptions par = ParticleInit.CYCLE_ELECTRIC;

		for (int i = 0; i < count; i++) {
			this.spawnParticleCycle(server, par, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, Direction.UP, range, i * 20F, false);
		}
	}

	// パーティクルスポーンサイクル
	protected void spawnParticleCycle(ServerLevel server, ParticleOptions par, double x, double y, double z, Direction face, double range, double angle, boolean isRevese) {
		int way = isRevese ? -1 : 1;
		server.sendParticles(par, x, y, z, 0, face.get3DDataValue() * way, range, angle + way * 1 * 6F, 1F);
	}

	public void teleportParticle(ParticleOptions par, Level world, BlockPos beforePos, BlockPos afterPos) {
		if (!(this.getLevel() instanceof ServerLevel sever)) { return; }

		float pX = afterPos.getX() - beforePos.getX();
		float pY = afterPos.getY() - beforePos.getY();
		float pZ = afterPos.getZ() - beforePos.getZ();
		int count = Math.abs((int) (pX + pZ)) * 2;

		for (int i = 0; i < count; i++) {
			for (int k = 0; k < 16; k++) {

				float randX = this.getRandFloat(1.5F);
				float randY = this.getRandFloat(1.5F);
				float randZ = this.getRandFloat(1.5F);
				float ax = beforePos.getX() + 0.5F + randX+ pX * (i / (float) count);
				float ay = beforePos.getY() + 1.25F + randY + pY * (i / (float) count);
				float az = beforePos.getZ() + 0.5F + randZ + pZ * (i / (float) count);

				sever.sendParticles(par, ax, ay, az, 0, 0F, 0F, 0F, 1F);
			}
		}
	}
}
