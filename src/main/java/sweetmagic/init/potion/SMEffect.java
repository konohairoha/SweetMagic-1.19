package sweetmagic.init.potion;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.util.PlayerHelper;
import sweetmagic.util.SMDamage;

public class SMEffect extends MobEffect {

	private int tickTime = 0;
	private final boolean isActive;
	public static final UUID MODIFIER_UUID = UUID.fromString("CE9DBC2A-EE3F-43F5-9DF7-F7F1EE4915A9");
	public static final UUID SPEED_UUID = UUID.fromString("CE9DBC2A-EE3F-43F5-9DF7-F7F1EE1222A9");

	public SMEffect(String name, int data, MobEffectCategory buff, boolean isActive) {
		super(buff, 0);
		this.isActive = isActive;
		PotionInit.potionMap.put(this, name);
	}

	public SMEffect(String name, int data, MobEffectCategory buff) {
		super(buff, 0);
		this.isActive = data == 7;

		if (data == 3) {
			this.addAttributeModifier(Attributes.MOVEMENT_SPEED, SMEffect.MODIFIER_UUID.toString(), -0.15D, AttributeModifier.Operation.MULTIPLY_TOTAL);
		}

		else if (data == 7) {
			this.addAttributeModifier(Attributes.MOVEMENT_SPEED, SMEffect.SPEED_UUID.toString(), -0.25D, AttributeModifier.Operation.MULTIPLY_TOTAL);
		}

		else if (data == 9) {
			this.addAttributeModifier(Attributes.MOVEMENT_SPEED, SMEffect.SPEED_UUID.toString(), 0.05D, AttributeModifier.Operation.ADDITION);
		}

		PotionInit.potionMap.put(this, name);
	}

	public void applyEffectTick(LivingEntity entity, int level) {

		this.tickTime++;

		if (this == PotionInit.reflash_effect) {
			this.actionReflash(entity);
		}

		else if (this == PotionInit.deadly_poison) {
			if (entity.getHealth() > 1F) {
				this.magicDamage(entity, SMDamage.poisonDamage, 1F);
			}
		}

		else if (this == PotionInit.bleeding) {
			if (entity.getHealth() > 0.5F) {
				entity.setHealth(entity.getHealth() - 0.5F);
				this.magicDamage(entity, SMDamage.magicDamage, 0.0001F);
			}
		}

		else if (this == PotionInit.regeneration) {
			if (entity.getHealth() < entity.getMaxHealth()) {
				entity.heal(1F);

				if (entity.level instanceof ServerLevel sever) {
					RandomSource rand = sever.random;
					double x = entity.xo;
					double y = entity.yo + 1D;
					double z = entity.zo;

					for (int i = 0; i < 8; ++i) {
						double d0 = rand.nextGaussian() * 0.02D;
						double d1 = rand.nextGaussian() * 0.02D;
						double d2 = rand.nextGaussian() * 0.02D;
						sever.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getRandPos(x, rand, 0.5D), this.getRandPos(y, rand, 1D), this.getRandPos(z, rand, 0.5D), 0, d0, d1, d2, 1F);
					}
				}
			}
		}

		else if (this == PotionInit.attack_disable) {
			if (entity.getHealth() < entity.getMaxHealth()) {
				entity.heal(0.25F);
			}
		}

		else if (this == PotionInit.flame) {
			this.magicDamage(entity, SMDamage.flameDamage, 0.75F);

			if (entity.level instanceof ServerLevel sever) {

				RandomSource rand = sever.random;
				double x = entity.xo;
				double y = entity.yo + 1D;
				double z = entity.zo;

				for (int i = 0; i < 8; ++i) {
					double d0 = rand.nextGaussian() * 0.02D;
					double d1 = rand.nextGaussian() * 0.02D;
					double d2 = rand.nextGaussian() * 0.02D;
					sever.sendParticles(ParticleTypes.FLAME, this.getRandPos(x, rand, 0.5D), this.getRandPos(y, rand, 1D), this.getRandPos(z, rand, 0.5D), 0, d0, d1, d2, 1F);
				}
			}
		}

		else if (this == PotionInit.frost && level > 0) {
			this.magicDamage(entity, SMDamage.flostDamage, 1F);
		}

		else if (this == PotionInit.gravity) {

			Vec3 vec = entity.getDeltaMovement();
			double x = vec.x;
			double y = vec.y;
			double z = vec.z;

			if (!entity.isOnGround()) {
				y -= ( (level + 2) * 0.0275D);
			}

			if (level >= 1) {
				x *= Math.max(1D - level * 0.15D, 0);
				z *= Math.max(1D - level * 0.15D, 0);
			}

			entity.setDeltaMovement(new Vec3(x, y, z));
		}

		else if (this == PotionInit.bubble) {

			// 移動速度を取得
			Vec3 vec = entity.getDeltaMovement();
			double vX = vec.x * 0.67D;
			double vY = 0.033D;
			double vZ = vec.z * 0.67D;

			// 落下速度を設定
			entity.setDeltaMovement(new Vec3(vX, vY, vZ));

			if (level >= 1 && this.tickTime % 20 == 0) {
				this.magicDamage(entity, SMDamage.magicDamage, 1F);
			}

			if (entity.level instanceof ServerLevel sever) {

				Random rand = new Random();
				float x = (float) (entity.getX() - 0.25F + rand.nextFloat() * 0.5F);
				float y = (float) (entity.getY() - 0.25F + rand.nextFloat() * 0.5F) + 0.5F;
				float z = (float) (entity.getZ() - 0.15F + rand.nextFloat() * 0.5F);

				for (int i = 0; i < 1; i++) {
					sever.sendParticles(ParticleInit.BUBBLE, x, y, z, 1, 0F, 0F, 0F, 0.03F);
				}
			}
		}

		else if (this == PotionInit.darkness_fog) {

			if (entity.level instanceof ServerLevel sever) {

				RandomSource rand = sever.random;
				double x = entity.xo;
				double y = entity.yo + 1D;
				double z = entity.zo;

				for (int i = 0; i < 1; ++i) {
					double d0 = rand.nextDouble() * 0.075D;
					double d1 = 0.05D + rand.nextDouble() * 0.1D;
					double d2 = rand.nextDouble() * 0.075D;
					sever.sendParticles(ParticleInit.DARK, this.getRandPos(x, rand, 0.75D), this.getRandPos(y, rand, 1D) + 0.5D, this.getRandPos(z, rand, 0.75D), 0, d0, d1, d2, 1F);
				}
			}
		}

		if (this.tickTime > 20) {
			this.tickTime = 0;
		}
	}

	// デバフ解除
	public void actionReflash(LivingEntity entity) {
		List<MobEffectInstance> effecList = PlayerHelper.getEffectList(entity, PotionInit.DEBUFF);
		effecList.forEach(p -> entity.removeEffect(p.getEffect()));
	}

	// 毒発動が出来るか
	public boolean isDurationEffectTick(int level, int time) {

		if (this == PotionInit.deadly_poison || this == PotionInit.flame || this == PotionInit.bleeding || this == PotionInit.regeneration || this == PotionInit.attack_disable) {
			int j = 25 >> time;
			return j > 0 ? level % j == 0 : true;
		}

		else if (this == PotionInit.frost && level >= 1) {
			int j = 40 >> time;
			return j > 0 ? level % j == 0 : true;
		}

		return this.isActive;
	}

	public double getRandPos(double pos, RandomSource rand, double scale) {
		return pos + ((rand.nextDouble() - rand.nextDouble()) * scale);
	}

	public void magicDamage(LivingEntity entity, SMDamage src, float dame) {
		src.setDebuffFlag(true);
		entity.hurt(src, dame);
		entity.invulnerableTime = 0;
	}
}
