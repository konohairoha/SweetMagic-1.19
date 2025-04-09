package sweetmagic.init.block.sm;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseSMBlock;

public class SMBrick extends BaseSMBlock {

	private final Block block;

	public SMBrick(String name) {
		super(name, setState(Material.STONE).sound(SoundType.STONE).strength(1F, 8192F));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
		this.block = null;
	}
	public SMBrick(String name, Block block) {
		super(name, setState(Material.STONE).sound(SoundType.STONE).strength(1F, 8192F));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
		this.block = block;
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		if (this.block == null) { return; }
		toolTip.add(this.getText("originatorblock", this.block.getName().getString()).withStyle(GOLD));
	}
}
