package sweetmagic.init.entity.projectile;

import java.util.ArrayList;
import java.util.List;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.entity.monster.boss.WindWitchMaster;

public class WindStormMagic extends AbstractBossMagic {

	private int chargeTime = 0;						// ウィンドストームのチャージ時間
	private static final int MAX_CHARGETIME = 60;	// ウィンドストームの最大チャージ時間

	private List<LivingEntity> entityList = new ArrayList<>();

	public WindStormMagic(EntityType<? extends WindStormMagic> entityType, Level world) {
		super(entityType, world);
	}

	public WindStormMagic(double x, double y, double z, Level world) {
		this(EntityInit.windStorm, world);
		this.setPos(x, y, z);
	}

	public WindStormMagic(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
	}

	// 常時更新処理
	public void onUpdate () {

		// ターゲットがいる場合
		if (this.target != null && this.target.isAlive()) {

			// 回転の設定
			this.setRotInit();

			// チャージ時間が満たしていないなら終了
			if (this.chargeTime++ < MAX_CHARGETIME) { return; }

			// えんちちーリストが設定されていないなら設定
			if (this.entityList.isEmpty()) {
				this.entityList = this.setTarget(false);

				// えんちちーソート
				if (this.entityList.size() > 1) {
					this.entityList = this.entityList.stream().sorted( (s1, s2) -> this.sortEntity(this, s1, s2) ).toList();
					this.target = this.entityList.get(0);
				}
			}

			// 魔法攻撃
			this.attackMagic(this.target);
		}

		// ターゲットの設定
		if ( ( this.target == null || !this.target.isAlive() ) && this.tickCount % 8 == 0) {
			this.setTarget(true);
		}
	}

	// 魔法攻撃
	public void attackMagic (LivingEntity target) {

		if (this.level.isClientSide) { return; }

		boolean isWarden = target instanceof Warden;
		float damage = (isWarden ? 60F : 30F) + this.getAddDamage();
		float shake = isWarden ? 5F : 25F;
		LivingEntity player = (LivingEntity) this.getOwner();
		WandInfo info = new WandInfo(this.getStack());

		for (int i = 0; i < 2; i++) {

			WindStormShot entity = new WindStormShot(this.level, player, this, info);

			double d0 = target.getX() - this.getX();
			double d1 = target.getZ() - this.getZ();
			double d3 = target.getY(0.3333333333333333D) - this.getY();
			Vector3f vec = this.getShotVector(this, new Vec3(d0, d3, d1), i == 0 ? shake : -shake);
			entity.shoot(vec.x(), vec.y(), vec.z(), 1F, 0);
			entity.setAddDamage(entity.getAddDamage() + damage);
			entity.setMaxLifeTime(120);
			entity.setRange(1.5D);
			entity.setBlockPenetration(true);
			this.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);
			this.level.addFreshEntity(entity);
		}

		WindStormShot entity = new WindStormShot(this.level, player, this, info);
		double x = target.getX() - this.getX();
		double y = target.getY(0.3333333333333333D) - this.getY();
		double z = target.getZ() - this.getZ();
		double xz = Math.sqrt(x * x + z * z);

		entity.shoot(x, y - xz * 0.035D, z, 1F, 0F);
		entity.setAddDamage(entity.getAddDamage() + damage);
		entity.setMaxLifeTime(120);
		entity.setRange(1.5D);
		entity.setBlockPenetration(true);
		this.level.addFreshEntity(entity);

		this.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);
		this.chargeTime = 0;
		this.setTarget(true);
	}

	public Vector3f getShotVector(AbstractBossMagic entity, Vec3 vec, float shake) {

		Vec3 vec1 = vec.normalize();
		Vec3 vec2 = vec1.cross(new Vec3(0.0D, 1.0D, 0.0D));
		if (vec2.lengthSqr() <= 1.0E-7D) {
			vec2 = vec1.cross(entity.getUpVector(1.0F));
		}

		Quaternion qua1 = new Quaternion(new Vector3f(vec2), 90.0F, true);
		Vector3f vecf1 = new Vector3f(vec1);
		vecf1.transform(qua1);
		Quaternion qua2 = new Quaternion(vecf1, shake, true);
		Vector3f vecf2 = new Vector3f(vec1);
		vecf2.transform(qua2);
		return vecf2;
	}

	// ターゲットの設定
	public List<LivingEntity> setTarget (boolean isSetTarget) {

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

	// 召喚えんちちーに取得
	public LivingEntity getEntity () {

		// えんちちーの初期化が出来ていないなら初期化
		if (this.summon == null) {
			WindWitchMaster queen = new WindWitchMaster(this.level);
			queen.setMagic(true);
			this.summon = queen;
		}
		return this.summon;
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.CYCLON;
	}
}
