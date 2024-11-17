package sweetmagic.init.block.sm;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseSMBlock;

public class SMPlanks extends BaseSMBlock {

	public SMPlanks(String name) {
		super(name, setState(Material.WOOD).sound(SoundType.WOOD).strength(1F, 8192F));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}
}
