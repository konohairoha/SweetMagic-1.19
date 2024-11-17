package sweetmagic.init.block.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import sweetmagic.api.event.AbstractChopTaskEvent;

public class MagicBarrierGlass extends SMGlass {

	public MagicBarrierGlass(String name) {
		super(name, false, false);
	}

	@Override
	public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
		if (world instanceof ServerLevel && !player.isShiftKeyDown()) {
	        MinecraftForge.EVENT_BUS.register(new GlassChopTask(pos, player, 1));
		}
	}

    public class GlassChopTask extends AbstractChopTaskEvent {

        public GlassChopTask(BlockPos start, Player player, int blockTick) {
            super(start, player, blockTick);
        }

	    // ブロックチェック
	    public boolean checkBlock(Level world, BlockPos pos) {
	    	return world.getBlockState(pos).getBlock() instanceof MagicBarrierGlass;
	    }
    }
}
