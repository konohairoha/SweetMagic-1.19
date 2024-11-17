package sweetmagic.init.entity.animal;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.entity.projectile.ExplosionMagicShot;
import sweetmagic.init.entity.projectile.FireMagicShot;
import sweetmagic.init.entity.projectile.GravityMagicShot;

public class WitchIfrit extends AbstractWitch {

	private static final ItemStack WAND = new ItemStack(ItemInit.purecrystal_wand_r);

	public WitchIfrit(Level world) {
		super(EntityInit.witchIfrit, world);
	}

	public WitchIfrit(EntityType<? extends AbstractWitch> eType, Level world) {
		super(eType, world);
	}

	@Override
	public AbstractMagicShot getMagicShot(LivingEntity target, boolean isWarden) {

		AbstractMagicShot entity = null;

		switch (this.rand.nextInt(4)) {
		case 0:
			entity = new FireMagicShot(this.level, this, ItemStack.EMPTY);
			break;
		case 1:
			entity = new FireMagicShot(this.level, this, ItemStack.EMPTY);
			break;
		case 2:
			entity = new ExplosionMagicShot(this.level, this, ItemStack.EMPTY);
			break;
		case 3:
			entity = new GravityMagicShot(this.level, this, ItemStack.EMPTY);
			break;
		}
		return entity;
	}

	public ItemStack getStack() {
		return WAND;
	}

	public float getDamageRate () {
		return 1.15F;
	}

	@Override
	public float getHealValue() {
		return this.getMaxHealth() / 3F;
	}
}
