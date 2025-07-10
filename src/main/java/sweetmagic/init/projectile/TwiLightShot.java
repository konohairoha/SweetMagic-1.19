package sweetmagic.init.entity.projectile;

import java.util.List;
import java.util.Random;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.util.PlayerHelper;

public class TwiLightShot extends AbstractMagicShot {

	public TwiLightShot(EntityType<? extends AbstractMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public TwiLightShot(double x, double y, double z, Level world) {
		this(EntityInit.twiLightShot, world);
		this.setPos(x, y, z);
	}

	public TwiLightShot(Level world, LivingEntity entity) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = ItemStack.EMPTY;
	}

	// えんちちーに当たった時の処理
	protected void entityHit(LivingEntity target) {

		List<MobEffectInstance> effecList = PlayerHelper.getEffectList(target, PotionInit.BUFF);

		for (MobEffectInstance ins : effecList) {
			MobEffect effect = ins.getEffect();
			int time = ins.getAmplifier();
			int level = ins.getDuration();
			target.removeEffect(effect);
			this.addPotion(target, effect, time / 2, level);
		}
	}

	// パーティクルスポーン
	protected void spawnParticle() {

		Random rand = this.rand;
		Vec3 vec = this.getDeltaMovement();
		float addX = (float) (-vec.x / 20F);
		float addY = (float) (-vec.y / 20F);
		float addZ = (float) (-vec.z / 20F);

		for (int i = 0; i < 4; i++) {
			float x = addX + this.getRandFloat(0.075F);
			float y = addY + this.getRandFloat(0.075F);
			float z = addZ + this.getRandFloat(0.075F);
			float f1 = (float) (this.getX() - 0.5F + rand.nextFloat() + vec.x * i / 4F);
			float f2 = (float) (this.getY() - 0.5F + rand.nextFloat() + vec.y * i / 4F);
			float f3 = (float) (this.getZ() - 0.5F + rand.nextFloat() + vec.z * i / 4F);
			this.addParticle(ParticleInit.TWILIGHTLIGHT, f1, f2, f3, x, y, z);
		}
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.TIME;
	}
}
