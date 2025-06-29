package sweetmagic.init.tile.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.api.iblock.ISMCrop;
import sweetmagic.init.BlockInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.crop.Alstroemeria;

public class TileAlstroemeria extends TileAbstractSM {

	public static boolean flagTwilight = false;

	public TileAlstroemeria(BlockPos pos, BlockState state) {
		super(TileInit.alst, pos, state);
	}

	public TileAlstroemeria(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		if (this.tickTime % 160 != 0) { return; }

		// 夕方だったら
		long worldTime = world.dayTime() % 24000;
		flagTwilight = worldTime >= 10400 && worldTime < 14000 || this.getBlock(pos.above()) == BlockInit.twilightlight || level.dimension().location().getPath().equals("twilight_forest");

		ISMCrop crop = (ISMCrop) state.getBlock();
		boolean isMaxAge = crop.isMaxAge(state);

		if ((flagTwilight && !isMaxAge) || (!flagTwilight && isMaxAge)) {
			world.setBlock(pos, state.setValue(crop.getSMMaxAge(), flagTwilight ? 1 : 0), 2);

			if (flagTwilight) {
				((Alstroemeria) this.getBlock(pos)).bloomAlstoemeria(world, pos.below());
			}
		}
	}
}
