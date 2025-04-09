package sweetmagic.init.block.sm;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import sweetmagic.api.iblock.ISMCrop;

public class MapleLeave extends SMLeave {

	   public static final IntegerProperty AGE_5 = ISMCrop.AGE5;

	public MapleLeave(String name, int data) {
		super(name, data);
		this.registerDefaultState(this.stateDefinition.any().setValue(AGE_5, 0).setValue(PERSISTENT, false).setValue(WATERLOGGED, false));
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		super.createBlockStateDefinition(build);
		build.add(AGE_5);
	}
}
