package sweetmagic.init.entity.projectile;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;

public class CycloneMagicShot extends AbstractMagicShot {

	public CycloneMagicShot(EntityType<? extends CycloneMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public CycloneMagicShot(double x, double y, double z, Level world) {
		this(EntityInit.cycloneMagic, world);
		this.setPos(x, y, z);
	}

	public CycloneMagicShot(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
		this.setRange(6D);
	}

	public CycloneMagicShot(Level world, LivingEntity entity) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = ItemStack.EMPTY;
		this.setRange(5D);
	}

	// えんちちーに当たった時の処理
	protected void entityHit(LivingEntity living) {

		// 被弾時ノックバック
		if (this.canTargetEffect(living, this.getOwner())) {

			int level = this.getWandLevel();
			LivingKnockBackEvent event = ForgeHooks.onLivingKnockBack(living, this.knockback * (1F + (level * 0.1F)), 1F, 1F);

			if (!event.isCanceled()) {
				Vec3 vec3 = this.getDeltaMovement().multiply(event.getRatioX(), 0D, event.getRatioZ()).normalize().scale(event.getStrength());
				if (vec3.lengthSqr() > 0D) {
					living.push(vec3.x, 0.1D, vec3.z);
				}
			}
		}

		if (this.level instanceof ServerLevel server) {
			this.spawnParticleShort(server, living.blockPosition());
		}

		double range = this.getRange() / 0.67D;
		float dame = 0.5F + 0.67F * this.getWandLevel() * this.getDamageRate();
		this.rangeAttack(living.blockPosition(), dame, range);
	}

	// ブロック着弾
	protected void onHitBlock(BlockHitResult result) {

		double range = this.getRange();
		float dame = 0.5F + 0.5F * this.getWandLevel() * this.getDamageRate();
		this.rangeAttack(result.getBlockPos().above(), dame, range);

		if (this.level instanceof ServerLevel server) {
			this.spawnParticleShort(server, result.getBlockPos().above());
		}

		this.discard();
	}

	public void rangeAttack(BlockPos bPos, float dame, double range) {
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.isTarget(), range);
		if(entityList.isEmpty()) { return; }

		boolean isTier3 = this.getData() >= 2;
		boolean isTier4 = this.getData() >= 3;
		int time = 60 * (this.getWandLevel() + 1);
		int addLevel = isTier4 ? 2 : 1;

		for (LivingEntity entity : entityList) {

			LivingKnockBackEvent event = ForgeHooks.onLivingKnockBack(entity, this.knockback * 1F, 1F, 1F);

			if (!event.isCanceled()) {
				Vec3 vec3 = this.getDeltaMovement().multiply(event.getRatioX(), 0D, event.getRatioZ()).normalize().scale(event.getStrength());
				if (vec3.lengthSqr() > 0D) {
					entity.push(vec3.x, 0.1D, vec3.z);
				}
			}

			this.attackDamage(entity, dame, false);

			if (isTier3) {
				int level = entity.hasEffect(PotionInit.bleeding) ? entity.getEffect(PotionInit.bleeding).getAmplifier() + addLevel : addLevel - 1;

				if (level > 0) {
					entity.removeEffect(PotionInit.bleeding);
				}

				this.addPotion(entity, PotionInit.bleeding, time, Math.max(0, level));
				if (!entity.hasEffect(PotionInit.bleeding)) { continue; }

				MobEffectInstance effect = entity.getEffect(PotionInit.bleeding);
				int pLevel = effect.getAmplifier();
				int pTime = effect.getDuration();
				if (isTier4 && pLevel >= 4) {
					entity.removeEffect(PotionInit.bleeding);
					this.addPotion(entity, PotionInit.bleeding, time, 0);
					float rate = 1F + pTime / 300F;
					this.addAttack(entity, dame * rate, pLevel * 3);
				}
			}
		}
	}

	// ダメージレートの取得
	public float getDamageRate() {
		switch (this.getData()) {
		case 0: return 1F;
		case 1: return 1.5F;
		case 2: return 1.75F;
		default: return 2F;
		}
	}

	protected void spawnParticleShort(ServerLevel sever, BlockPos pos) {
		float x = (float) (pos.getX() + this.getRandFloat(0.5F));
		float y = (float) (pos.getY() + this.getRandFloat(0.5F));
		float z = (float) (pos.getZ() + this.getRandFloat(0.5F));

		int count = 16;
		float rate = 0.15F;

		if (this.getData() >= 3) {
			count = 64;
			rate = 0.25F;
		}

		for (int i = 0; i < count; i++) {
			sever.sendParticles(ParticleTypes.CLOUD, x, y, z, 4, 0F, 0F, 0F, rate);
		}
	}

	public int getMinParticleTick() {
		return 3;
	}

	// パーティクルスポーン
	protected void spawnParticle() {
		Vec3 vec = this.getDeltaMovement();
		float x = (float) (-vec.x / 30F);
		float y = (float) (-vec.y / 30F);
		float z = (float) (-vec.z / 30F);
		this.level.addParticle(ParticleInit.CYCLONE, this.getX(), this.getY() + 0.5F, this.getZ(), x, y, z);
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.CYCLON;
	}
}
