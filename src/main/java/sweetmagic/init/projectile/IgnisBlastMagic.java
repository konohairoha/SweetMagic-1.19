package sweetmagic.init.entity.projectile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.monster.boss.IgnisKnight;

public class IgnisBlastMagic extends AbstractBossMagic {

	private boolean isGround = false;				// 着地したかどうか
	private int groundTime = 0;						// 着地時間
	private int chargeTime = 0;						// イグニスブラストのチャージ時間
	private static final int MAX_CHARGETIME = 50;	// イグニスブラストの最大チャージ時間
	private List<LivingEntity> entityList = new ArrayList<>();

	public IgnisBlastMagic(EntityType<? extends IgnisBlastMagic> entityType, Level world) {
		super(entityType, world);
	}

	public IgnisBlastMagic(double x, double y, double z, Level world) {
		this(EntityInit.ignisBlast, world);
		this.setPos(x, y, z);
	}

	public IgnisBlastMagic(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
	}

	// 常時更新処理
	public void onUpdate() {

		// ターゲットがいる場合
		if (this.target != null) {

			// 回転の設定
			this.setRotInit();

			// チャージ時間が満たしていないなら終了
			if (this.chargeTime++ < MAX_CHARGETIME) { return; }

			// えんちちーリストが設定されていないなら設定
			if (this.entityList.isEmpty()) {
				this.entityList = this.setTarget(false);

				// えんちちーソート
				if (this.entityList.size() > 1) {
					this.entityList = this.entityList.stream().sorted((s1, s2) -> this.sortEntity(this, s1, s2)).toList();
					this.target = this.entityList.get(0);
				}
			}

			// 魔法攻撃
			this.attackMagic(this.target);
		}

		// ターゲットの設定
		if ((this.target == null || !this.target.isAlive()) && this.tickCount % 8 == 0) {
			this.setTarget(true);
		}
	}

	// 魔法攻撃
	public void attackMagic(LivingEntity target) {

		this.tickTime++;
		IgnisKnight ignis = this.getIgnis();
		ignis.setBlast(true);
		ignis.setAttackType(2);

		// ターゲットの真上にテレポート
		if (this.tickTime == 10) {
			BlockPos beforePos = this.blockPosition();
			this.targetTeleport(ignis, target);
			this.teleportParticle(ParticleTypes.SOUL_FIRE_FLAME, this.getLevel(), beforePos, this.blockPosition());
		}

		// 空中浮遊
		else if (this.tickTime > 10 && this.tickTime <= 50) {
			this.ignisJump(ignis, target);
		}

		// 一定時間が経ったら
		else if (this.tickTime > 50) {

			// 地面についていないなら落下
			if (!this.inGround) {
				this.notOnGround(ignis, target);
			}

			// 地面に付いたら
			else {

				int data = this.getData();

				// 地面着地時の爆発攻撃
				if (!this.isGround) {
					this.groundBlastAttack(ignis, target, data);
				}

				// 地面着地時の追加攻撃
				else {
					this.groundRingAttack(ignis, target, data);
				}
			}
		}
	}

	// ターゲットの真上にテレポート
	public void targetTeleport(IgnisKnight ignis, LivingEntity target) {

		// ターゲットの真上の座標取得
		BlockPos targetPos = new BlockPos(target.getX(), target.getY() + 5D, target.getZ());

		// 空気ブロックでないなら位置を下に変更
		while(true) {
			if (this.getLevel().isEmptyBlock(targetPos)) { break; }
			targetPos = targetPos.below();
		}

		// テレポート
		this.teleportTo(targetPos.getX(), targetPos.getY(), targetPos.getZ());
	}

	// 空中浮遊
	public void ignisJump(IgnisKnight ignis, LivingEntity target) {

		// 移動を止める
		Vec3 vec = this.getDeltaMovement();
		if (vec.y < 0D) {
			this.setDeltaMovement(new Vec3(0D, 0D, 0D));
		}

		// ハンマーを振り下ろす
		if (this.tickTime == 50) {
			ignis.setSwing(true);
		}

		// 着地前のパーティクル表示
		if (this.tickTime == 11 || this.tickTime == 41) {

			BlockPos pos = target.blockPosition();
			double range = 10D + this.entityList.size() * 0.5D;

			for (int i = 1; i <= 4; i++) {
				this.spawnParticleCycle(pos, range * 0.25D * i, this.rand);
			}
		}

		// 以降パーティクル表示
		if (!(this.getLevel() instanceof ServerLevel sever)) { return; }

		for (int i = 0; i < 4; i++) {
			float x = (float) this.getX() + this.getRandFloat(0.5F);
			float y = (float) this.getY() + this.getRandFloat(0.5F);
			float z = (float) this.getZ() + this.getRandFloat(0.5F);
			float aX = this.getRandFloat(0.75F);
			float aY = 0.1F + this.rand.nextFloat() * 0.2F;
			float aZ = this.getRandFloat(0.75F);
			sever.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0, aX, aY, aZ, 1F);
		}
	}

	// 地面についていないなら落下
	public void notOnGround(IgnisKnight ignis, LivingEntity target) {
		ignis.setSwing(true);
		this.setDeltaMovement(new Vec3(0D, -1.5D, 0D));
	}

	// 地面着地時の爆発攻撃
	public void groundBlastAttack(IgnisKnight ignis, LivingEntity target, int data) {

		if (!this.getLevel().isEmptyBlock(this.blockPosition())) {
			this.teleportTo(this.getX(), this.getY() + 1.75D, this.getZ());
		}

		// 周囲の敵モブ取得
		ignis.setSwing(true);
		float amount = (data == 1 ? 35F : 25F) + this.getAddDamage();
		double range = (data == 1 ? 15F : 10D) + this.entityList.size() * 0.5D;

		// 対象のえんちちーに攻撃
		List<LivingEntity> attackList = this.getEntityList(LivingEntity.class, e -> e instanceof Enemy && e.isAlive(), range);

		for (LivingEntity entity : attackList) {
			this.attackDamage(entity, amount, true);

			if (data == 1) {
				this.addPotion(entity, PotionInit.flame, 0, 600);
			}
		}

		this.isGround = true;
		this.playSound(SoundEvents.GENERIC_EXPLODE, 3F, 1F / (this.rand.nextFloat() * 0.2F + 0.9F));
		this.sortEntityList(ignis);
		if (!(this.getLevel() instanceof ServerLevel sever)) { return; }

		sever.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY() + 1F, this.getZ(), 1, 0F, 0F, 0F, 1F);
		float x = (float) this.getX() + this.getRandFloat(0.5F);
		float y = (float) this.getY() + this.getRandFloat(0.5F);
		float z = (float) this.getZ() + this.getRandFloat(0.5F);

		for (int i = 0; i < 16; i++) {
			sever.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 4, 0F, 0F, 0F, 0.15F);
		}
	}

	// 地面着地時の追加攻撃
	public void groundRingAttack(IgnisKnight ignis, LivingEntity target, int data) {
		if (this.groundTime++ < 19 || this.groundTime % 20 != 0) { return; }

		// 周囲の敵モブ取得
		float amount = (data == 1 ? 15F : 10F) + this.getAddDamage() * 0.5F;
		double ran = (data == 1 ? 20D : 14D) + this.entityList.size() * 2D;
		this.playSound(SoundEvents.BLAZE_SHOOT, 1F, 1F / (this.rand.nextFloat() * 0.2F + 0.9F));

		// 対象のえんちちーに攻撃
		List<LivingEntity> attackList = this.getEntityList(LivingEntity.class, e -> e instanceof Enemy && e.isAlive(), ran);

		for (LivingEntity entity : attackList) {
			this.attackDamage(entity, amount, true);

			if (data == 1 && entity.hasEffect(PotionInit.flame)) {
				this.addAttack(entity, amount, 3);
			}
		}

		// えんちちーリストのソート
		this.sortEntityList(ignis);

		// 一定時間後終了
		if (this.groundTime >= 60) {
			this.clearInfo(ignis);
		}

		// 以降パーティクル表示
		if (!(this.getLevel() instanceof ServerLevel sever)) { return; }

		BlockPos pos = this.blockPosition();

		for (int range = 0; range < 3; range++) {
			for (int i = 0; i < 3; i++) {
				this.spawnParticleRing(sever, ParticleTypes.SOUL_FIRE_FLAME, 2 + range * 4, pos, -0.25D + i * 0.5D);
			}
		}
	}

	// えんちちーリストのソート
	public void sortEntityList(IgnisKnight ignis) {

		// 生存しているえんちちーだけに絞る
		this.entityList = this.entityList.stream().filter(e -> e.isAlive()).toList();

		// えんちちーがいなくなったら終了
		if (this.entityList.isEmpty()) {
			this.clearInfo(ignis);
		}

		// えんちちーがいるなら次のターゲットへ
		else {
			this.target = this.entityList.get(0);
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
	public List<LivingEntity> setTarget(boolean isSetTarget) {
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, e -> e instanceof Enemy && e.isAlive(), 48D);
		if (!isSetTarget) { return entityList; }

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

		return entityList;
	}

	public void clearInfo(IgnisKnight ignis) {
		this.chargeTime = 0;
		this.tickTime = 0;
		this.groundTime = 0;
		this.entityList = new ArrayList<>();
		this.isGround = false;
		this.inGround = false;
		ignis.setAttackType(0);
		ignis.setSwing(false);
		ignis.setRush(false);
		ignis.setAttack(false);
		ignis.setBlast(false);
	}

	// パーティクルスポーン
	public void spawnParticleRing(ServerLevel server, ParticleOptions par, double range, BlockPos pos, double addY) {

		double x = pos.getX() + 0.5D;
		double y = pos.getY() + 1D + addY;
		double z = pos.getZ() + 0.5D;

		for (double degree = -range * Math.PI; degree < range * Math.PI; degree += 0.25D) {
			double rate = range * 0.75D;
			server.sendParticles(par, x + Math.cos(degree) * rate, y, z + Math.sin(degree) * rate, 0, Math.cos(degree) * 0.65D, 0, Math.sin(degree) * 0.65D, 1D);
		}
	}

	// 召喚えんちちーに取得
	public LivingEntity getEntity() {

		// えんちちーの初期化が出来ていないなら初期化
		if (this.summon == null) {
			IgnisKnight queen = new IgnisKnight(this.getLevel());
			queen.setMagic(true);
			this.summon = queen;
		}
		return this.summon;
	}

	public IgnisKnight getIgnis() {
		return (IgnisKnight) this.getEntity();
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.FLAME;
	}
}
