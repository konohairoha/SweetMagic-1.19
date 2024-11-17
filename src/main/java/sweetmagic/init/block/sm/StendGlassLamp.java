package sweetmagic.init.block.sm;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseSMBlock;

public class StendGlassLamp extends BaseSMBlock {

	public static final BooleanProperty LIT = BooleanProperty.create("light_on");

	public StendGlassLamp(String name) {
		super(name, setState(Material.METAL, SoundType.GLASS, 1F, 8192F).lightLevel((b) -> b.getValue(LIT) ? 15 : 0));
		this.registerDefaultState(this.defaultBlockState().setValue(LIT, false));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext con) {
		return this.defaultBlockState().setValue(LIT, con.getLevel().hasNeighborSignal(con.getClickedPos()));
	}

	public void neighborChanged(BlockState state, Level world, BlockPos pos1, Block block, BlockPos block2, boolean par1) {
		if (world.isClientSide) { return; }

		boolean flag = state.getValue(LIT);
		if (flag == world.hasNeighborSignal(pos1)) { return; }

		if (flag) {
			world.scheduleTick(pos1, this, 4);
		}

		else {
			world.setBlock(pos1, state.cycle(LIT), 2);
		}
	}

	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		if (state.getValue(LIT) && !world.hasNeighborSignal(pos)) {
			world.setBlock(pos, state.cycle(LIT), 2);
		}
	}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
        build.add(LIT);
    }
}
