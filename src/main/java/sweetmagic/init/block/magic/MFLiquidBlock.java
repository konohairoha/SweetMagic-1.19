package sweetmagic.init.block.magic;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseModelBlock;

public class MFLiquidBlock extends BaseModelBlock {

	public MFLiquidBlock(String name) {
		super(name, setState(Material.PISTON, SoundType.METAL, 1F, 8192F));
		BlockInfo.create(this, null, name);
	}

	@Override
	public float getEnchantPower () {
		return 1F;
	}
}
