package sweetmagic.init.block.sm;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseSMBlock;

public class SMLight extends BaseSMBlock {

	public SMLight(String name) {
		super(name, setState(Material.GLASS, SoundType.GLASS, 1F, 8192F, 15));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}
}
