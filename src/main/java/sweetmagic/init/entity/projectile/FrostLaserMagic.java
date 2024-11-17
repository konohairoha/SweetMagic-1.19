package sweetmagic.init.entity.projectile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.entity.monster.boss.QueenFrost;

public class FrostLaserMagic extends AbstractBossMagic {

	public FrostLaserMagic(EntityType<? extends FrostLaserMagic> entityType, Level world) {
		super(entityType, world);
	}

	public FrostLaserMagic(double x, double y, double z, Level world) {
		this(EntityInit.frostLaser, world);
		this.setPos(x, y, z);
	}

	public FrostLaserMagic(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
	}

	// 常時更新処理
	public void onUpdate () {

		// ターゲットがいる場合
		if (this.target != null) {

			this.setRotInit();		// 回転の設定
			this.attackParticle();	// 攻撃時のパーティクル

			// 魔法攻撃
			if (this.tickCount % 8 == 0) {
				this.attackMagic();
			}
		}

		// ターゲットの設定
		if ( ( this.target == null || !this.target.isAlive() ) && this.tickCount % 8 == 0) {
			this.setTarget();
		}
	}

	// 攻撃時のパーティクル
	public void attackParticle () {

		// パーティクルを出す
		if (this.level instanceof ServerLevel server) {

			Random rand = this.rand;
			BlockPos p = this.target.blockPosition();

			for (int i = 0; i < 10; i++) {

				if (rand.nextFloat() >= 0.65F) { continue; }

				float f1 = (float) this.getX() + rand.nextFloat() - 0.5F;
				float f2 = (float) this.getY() + 0.5F;
				float f3 = (float) this.getZ() + rand.nextFloat() - 0.5F;
				float x = (float) ( ( p.getX() - this.getX() + rand.nextFloat() * 1.5F ) * 0.125F);
				float y = (float) ( ( p.getY() - this.getY() + rand.nextFloat() * 1.5F ) * 0.125F);
				float z = (float) ( ( p.getZ() - this.getZ() + rand.nextFloat() * 1.5F ) * 0.125F);
				server.sendParticles(ParticleInit.FROST_LASER.get(), f1, f2, f3, 0, x, y, z, 1F);
			}
		}
	}

	// 魔法攻撃
	public void attackMagic () {

		// 攻撃した人をリストに入れる。
		List<LivingEntity> entityAllList = new ArrayList<>();
		double disX = this.target.xo - this.xo;
		double disY = this.target.yo - this.yo;
		double disZ = this.target.zo - this.zo;
		float damage = 2.5F + this.getAddDamage() * 0.1F;

		// 向き先に座標を設定
		for (int i = 0; i < 36; i++) {

			// 攻撃した人をリストに含まれないプレイヤーリストを取得
			double dis = (double) (i + 1) / 36D;
			List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.getAABB(disX * dis, disY * dis, disZ * dis, false)).stream().filter(e -> e.isAlive() && e instanceof Enemy && !entityAllList.contains(e)).toList();entityAllList.addAll(entityList);
			entityList.forEach(e -> this.attackDamage(e, damage, true));
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

	// 召喚えんちちーに取得
	public LivingEntity getEntity () {

		// えんちちーの初期化が出来ていないなら初期化
		if (this.summon == null) {
			QueenFrost queen = new QueenFrost(this.level);
			queen.setArmor(3);
			queen.setMagic(true);
			this.summon = queen;
		}
		return this.summon;
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.FROST;
	}
}
