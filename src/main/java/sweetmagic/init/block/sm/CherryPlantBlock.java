package sweetmagic.init.block.sm;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import sweetmagic.api.iblock.ISMCrop;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseModelBlock;

public class CherryPlantBlock extends BaseModelBlock {

	public CherryPlantBlock(String name) {
		super(name, setState(Material.PLANT, SoundType.GRASS, 1F, 8192F));
		this.registerDefaultState(this.stateDefinition.any().setValue(ISMCrop.AGE3, 0));
		BlockInfo.create(this, null, name);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(ISMCrop.AGE3);
	}
}
