package sweetmagic.init.entity.animal;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.entity.projectile.CycloneMagicShot;
import sweetmagic.init.entity.projectile.DigMagicShot;
import sweetmagic.init.entity.projectile.FireMagicShot;
import sweetmagic.init.entity.projectile.FrostMagicShot;
import sweetmagic.init.entity.projectile.GravityMagicShot;

public class WitchMaster extends AbstractWitch {

	private static final ItemStack WAND = new ItemStack(ItemInit.purecrystal_wand_y);

	public WitchMaster(Level world) {
		super(EntityInit.witchMaster, world);
	}

	public WitchMaster(EntityType<? extends AbstractWitch> eType, Level world) {
		super(eType, world);
	}

	@Override
	public AbstractMagicShot getMagicShot(LivingEntity target, boolean isWarden) {

		AbstractMagicShot entity = null;

		switch (this.rand.nextInt(5)) {
		case 0:
			entity = new FireMagicShot(this.getLevel(), this);
			break;
		case 1:
			entity = new FrostMagicShot(this.getLevel(), this);
			break;
		case 2:
			entity = new CycloneMagicShot(this.getLevel(), this);
			break;
		case 3:
			entity = new GravityMagicShot(this.getLevel(), this);
			break;
		case 4:
			entity = new DigMagicShot(this.getLevel(), this);
			break;
		}
		return entity;
	}

	public ItemStack getStack() {
		return WAND;
	}

	public int getRecastTime () {
		return 135;
	}

	public int getWandLevel() {
		return 15;
	}

	@Override
	public float getHealValue() {
		return this.getMaxHealth() * 0.67F;
	}
}
