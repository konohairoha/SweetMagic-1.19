package sweetmagic.init.entity.projectile;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.monster.boss.WitchSandryon;
import sweetmagic.util.PlayerHelper;

public class InfinitWandMagic extends AbstractBossMagic {

	private int infiniteTime = 0;
	private static final int MAX_INFINITE_TIME = 175;

	public InfinitWandMagic(EntityType<? extends AbstractBossMagic> entityType, Level world) {
		super(entityType, world);
	}

	public InfinitWandMagic(double x, double y, double z, Level world) {
		this(EntityInit.infinitWand, world);
		this.setPos(x, y, z);
	}

	public InfinitWandMagic(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
	}

	// 常時更新処理
	public void onUpdate() {

		// ターゲットがいる場合
		if (this.target != null && this.target.isAlive()) {

			// 回転の設定
			this.setRotInit();
			this.attackMagic(this.target);
		}

		// ターゲットの設定
		if ( this.infiniteTime <= 0 && ( this.target == null || !this.target.isAlive() ) && this.tickCount % 8 == 0) {
			this.setTarget();
		}
	}

	// 魔法攻撃
	public void attackMagic(LivingEntity target) {

		if (this.infiniteTime == MAX_INFINITE_TIME - 100 && this.getEntity() != null) {
			WitchSandryon sand = this.getsandryon();
			sand.setInfiniteWand(true);
		}

		if (this.infiniteTime++ < MAX_INFINITE_TIME) { return; }

		double range = 24D;
		float damage = 50F + this.getAddDamage();
		int addAttack = 10 + this.getAddAttack();

		if (this.getLevel() instanceof ServerLevel sever) {

			ParticleOptions par = ParticleTypes.FIREWORK;
			List<LivingEntity> targetList = this.getEntityList(LivingEntity.class, e -> e instanceof Enemy && e.isAlive(), this.getAABB(this.getPos(target), range));

			// 対象のえんちちーに攻撃
			for (LivingEntity entity : targetList) {

				List<MobEffectInstance> effecList = PlayerHelper.getEffectList(entity, PotionInit.BUFF);
				effecList.forEach(p -> entity.removeEffect(p.getEffect()));
				this.attackDamage(entity, damage, true);
				this.addAttack(entity, damage * 0.33F, addAttack);
				entity.playSound(SoundEvents.GLASS_BREAK, 0.25F, 1.15F);

				float x = (float) (entity.xo + this.rand.nextFloat() * 0.5F);
				float y = (float) (entity.getY() + this.rand.nextFloat() * 0.5F + 1.5F);
				float z = (float) (entity.zo + this.rand.nextFloat() * 0.5F);

				for (int i = 0; i < 8; i++) {
					sever.sendParticles(par, x, y, z, 2, 0F, 0F, 0F, 0.1F);
				}
			}
		}

		this.infiniteTime = 0;

		if(this.getEntity() != null) {
			((WitchSandryon) this.getEntity()).setInfiniteWand(false);
		}
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

	public BlockPos getPos(LivingEntity target) {

		if (this.target != null && this.target.isAlive()) {
			return target.blockPosition();
		}

		return this.blockPosition();
	}

	// 召喚えんちちーに取得
	public LivingEntity getEntity() {

		// えんちちーの初期化が出来ていないなら初期化
		if (this.summon == null) {
			WitchSandryon sand = new WitchSandryon(this.getLevel());
			sand.setMagic(true);
			this.summon = sand;
		}
		return this.summon;
	}

	public WitchSandryon getsandryon() {
		return (WitchSandryon) this.getEntity();
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.LIGHTNING;
	}
}
