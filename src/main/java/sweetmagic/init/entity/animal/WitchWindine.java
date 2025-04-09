package sweetmagic.init.entity.animal;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.entity.projectile.BubbleMagicShot;
import sweetmagic.init.entity.projectile.FrostMagicShot;
import sweetmagic.init.entity.projectile.PoisonMagicShot;

public class WitchWindine extends AbstractWitch {

	private static final ItemStack WAND = new ItemStack(ItemInit.purecrystal_wand_b);

	public WitchWindine(Level world) {
		super(EntityInit.witchWindine, world);
	}

	public WitchWindine(EntityType<? extends AbstractWitch> eType, Level world) {
		super(eType, world);
	}

	@Override
	public AbstractMagicShot getMagicShot(LivingEntity target, boolean isWarden) {

		AbstractMagicShot entity = null;

		switch (this.rand.nextInt(4)) {
		case 0:
			entity = new BubbleMagicShot(this.level, this);
			break;
		case 1:
			entity = new PoisonMagicShot(this.level, this);
			break;
		case 2:
			entity = new FrostMagicShot(this.level, this);
			break;
		case 3:
			entity = new BubbleMagicShot(this.level, this);
			break;
		}
		return entity;
	}

	public ItemStack getStack() {
		return WAND;
	}

	public float getDamageRate() {
		return 0.85F;
	}

	@Override
	public float getHealValue() {
		return this.getMaxHealth() * 0.67F;
	}
}
