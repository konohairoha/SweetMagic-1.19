package sweetmagic.init.entity.projectile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.entity.monster.boss.HolyAngel;

public class HolyBusterMagic extends AbstractBossMagic {

	private int chargeTime = 0;						// ホーリーバスターのチャージ時間
	private static final int MAX_CHARGETIME = 80;	// ホーリーバスターの最大チャージ時間
	private Map<Integer, BlockPos> posMap = new LinkedHashMap<>();	// セイクリッドレイン

	public HolyBusterMagic(EntityType<? extends HolyBusterMagic> entityType, Level world) {
		super(entityType, world);
	}

	public HolyBusterMagic(double x, double y, double z, Level world) {
		this(EntityInit.holyBusert, world);
		this.setPos(x, y, z);
	}

	public HolyBusterMagic(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
	}

	// 常時更新処理
	public void onUpdate() {

		// ターゲットがいる場合
		if (this.target != null) {

			this.setRotInit();		// 回転の設定
			this.attackParticle();	// 攻撃時のパーティクル

			// 攻撃時のパーティクル
			if (this.chargeTime % 30 == 0) {
				this.attackParticle();
			}

			// 魔法攻撃
			if (this.chargeTime++ >= MAX_CHARGETIME && this.tickCount % 6 == 0) {
				this.attackMagic();
			}
		}

		// ターゲットの設定
		if ((this.target == null || !this.target.isAlive()) && this.tickCount % 8 == 0) {
			this.setTarget();
		}
	}

	// 攻撃時のパーティクル
	public void attackParticle() {
		if (!(this.level instanceof ServerLevel server)) { return; }

		int count = 18;
		double range = this.getData() == 1 ? 16.75D : 10D;
		Random rand = this.rand;
		ParticleOptions par = ParticleInit.CYCLE_ELECTRIC;

		for (BlockPos pos : this.posMap.values()) {

			if (pos == null) { return; }

			this.spawnParticleCycle(server, pos, range + 1.15D, rand, count, par);
			this.spawnParticleCycle(server, pos, range - 3.85D, rand, count, par);
			this.spawnParticleCycle(server, pos, range - 7.85D, rand, count, par);
		}
	}

	protected void spawnParticleCycle(ServerLevel server, BlockPos pos, double range, Random rand, int count, ParticleOptions par) {
		for (int i = 0; i < count; i++) {
			this.spawnParticleCycle(server, par, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, Direction.UP, range, i * 20F, false);
		}
	}

	// パーティクルスポーンサイクル
	protected void spawnParticleCycle(ServerLevel server, ParticleOptions par, double x, double y, double z, Direction face, double range, double angle, boolean isRevese) {
		int way = isRevese ? -1 : 1;
		server.sendParticles(par, x, y, z, 0, face.get3DDataValue() * way, range, angle + way * 1 * 6F, 1F);
	}

	// ターゲット座標設定
	public void setTargetPos() {
		if (!this.posMap.isEmpty()) { return; }

		final int maxTarget = 8;
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, e -> e.isAlive() && e instanceof Enemy, 48D);
		int maxLoop = Math.min(maxTarget, entityList.size());

		if (this.getData() == 1) {
			maxLoop += 4;
		}

		for (int i = 0; i < maxLoop; i++) {
			this.posMap.put(i, entityList.get(i).blockPosition());
		}

		if (10 > maxLoop) {

			int mapRange = 32;
			Random rand = this.rand;
			BlockPos basePos = this.blockPosition().below();

			for (int i = maxLoop; i < maxTarget; i++) {

				while (true) {
					BlockPos pos = new BlockPos(basePos.getX() + this.getRandRange(rand, mapRange), basePos.getY(), basePos.getZ() + this.getRandRange(rand, mapRange));

					if (!this.posMap.containsValue(pos)) {
						this.posMap.put(i, pos);
						break;
					}
				}
			}
		}
	}

	public int getRandRange(Random rand, int range) {
		return rand.nextInt(range) - rand.nextInt(range);
	}

	// 魔法攻撃
	public void attackMagic() {

		int data = this.getData();
		double range = data == 1 ? 17.5D : 12.5D;
		float damage = (data == 1 ? 50F : 25F) + this.getAddDamage();

		for (Entry<Integer, BlockPos> map : this.posMap.entrySet()) {

			BlockPos pos = map.getValue();
			AABB aabb = new AABB(pos.offset(-range, -range, -range), pos.offset(range, range, range));
			List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, aabb).stream().filter(e -> e.isAlive() && e instanceof Enemy && this.checkDistances(pos, e.blockPosition(), range * range)).toList();

			for (LivingEntity target : entityList) {
				this.attackDamage(target, damage, true);

				if (data == 1) {
					if (target.hasEffect(MobEffects.GLOWING)) {
						target.setHealth(Math.min(1F, target.getHealth() - 10F));
					}

					this.addPotion(target, MobEffects.GLOWING, 0, 600);
				}
			}

			this.posMap.remove(map.getKey());

			if (this.posMap.isEmpty()) {
				this.chargeTime = 0;
				this.posMap.clear();
			}

			if (this.level instanceof ServerLevel sever) {

				// 範囲の座標取得
				Random rand = this.rand;
				Iterable<BlockPos> posList = this.getPosList(pos, range);

				for (BlockPos p : posList) {

					if (!this.checkDistances(pos, p, range * range) || rand.nextFloat() >= 0.25F) { continue; }

					float x = (float) (p.getX() + rand.nextFloat() - 0.5F);
					float y = (float) (p.getY() + rand.nextFloat() - 0.5F);
					float z = (float) (p.getZ() + rand.nextFloat() - 0.5F);
					sever.sendParticles(ParticleTypes.END_ROD, x, y, z, 0, 0F, rand.nextFloat() * 0.75F, 0F, 1F);
				}
			}

			this.level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.GENERIC_EXPLODE, this.getSoundSource(), 2F, 1F);
			return;
		}
	}

	// 範囲内にいるかのチェック
	public boolean checkDistances(BlockPos basePos, BlockPos pos, double range) {
		double d0 = basePos.getX() - pos.getX();
		double d1 = basePos.getY() - pos.getY();
		double d2 = basePos.getZ() - pos.getZ();
		return (d0 * d0 + d1 * d1 + d2 * d2) <= range;
	}

	// ターゲットの設定
	public void setTarget() {

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
	public LivingEntity getEntity() {

		// えんちちーの初期化が出来ていないなら初期化
		if (this.summon == null) {
			HolyAngel queen = new HolyAngel(this.level);
			queen.setORU(4);
			queen.setMagic(true);
			this.summon = queen;
		}
		return this.summon;
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.SHINE;
	}
}
