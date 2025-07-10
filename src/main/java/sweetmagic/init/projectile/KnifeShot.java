package sweetmagic.init.entity.projectile;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.init.EntityInit;
import sweetmagic.init.PotionInit;

public class KnifeShot extends AbstractMagicShot {

	public KnifeShot(EntityType<? extends AbstractMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public KnifeShot(double x, double y, double z, Level world) {
		this(EntityInit.knifeShot, world);
		this.setPos(x, y, z);
	}

	public KnifeShot(Level world, LivingEntity entity) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = ItemStack.EMPTY;
	}

	// えんちちーに当たった時の処理
	protected void entityHit(LivingEntity target) {
		if (target.hasEffect(PotionInit.reflash_effect)) {
			MobEffectInstance effect = target.getEffect(PotionInit.reflash_effect);
			int time = effect.getDuration();
			int level = effect.getAmplifier() + 1;
			target.addEffect(new MobEffectInstance(PotionInit.reflash_effect, (int) (time * 0.9F), level, true, false));
		}

		else {
			this.addPotion(target, PotionInit.deadly_poison, 120, 2);
		}
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.TOXIC;
	}
}
