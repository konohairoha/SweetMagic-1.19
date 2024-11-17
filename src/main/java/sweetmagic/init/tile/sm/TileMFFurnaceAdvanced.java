package sweetmagic.init.tile.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.TileInit;

public class TileMFFurnaceAdvanced extends TileMFFurnace {

	protected final int maxMagiaFlux = 200000;				// 最大MF量を設定
	protected final int costMF = 10;

	public TileMFFurnaceAdvanced(BlockPos pos, BlockState state) {
		super(TileInit.mffurnaceAdavance, pos, state);
	}

	public TileMFFurnaceAdvanced(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// 最大MFの取得
	@Override
	public int getMaxMF () {
		return this.maxMagiaFlux;
	}

	// 消費MFの取得
	public int getCostMF () {
		return this.costMF;
	}

	// 受信するMF量の取得
	@Override
	public int getReceiveMF () {
		return 20000;
	}

	public float getCraftRate () {
		return 10F;
	}
}
