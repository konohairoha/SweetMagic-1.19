package sweetmagic.init.block.sm;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.block.base.BaseModelBlock;

public class Planting extends BaseModelBlock {

	public Planting(String name) {
		super(name, setState(Material.PLANT, SoundType.GRASS, 0F, 8192F), SweetMagicCore.smTab, false);
	}
}
