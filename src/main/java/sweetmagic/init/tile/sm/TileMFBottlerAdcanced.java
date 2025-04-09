package sweetmagic.init.tile.sm;

import java.util.Arrays;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.BlockInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.TileInit;

public class TileMFBottlerAdcanced extends TileMFBottler {

	public int maxMagiaFlux = 7000000;				// 最大MF量を設定

	private List<ItemStack> stackList = Arrays.<ItemStack> asList(
		new ItemStack(ItemInit.mf_small_bottle), new ItemStack(ItemInit.mf_bottle), new ItemStack(ItemInit.magia_bottle), new ItemStack(BlockInit.magiaflux_block),
		new ItemStack(ItemInit.aether_crystal), new ItemStack(ItemInit.divine_crystal), new ItemStack(ItemInit.pure_crystal)
	);

	public TileMFBottlerAdcanced(BlockPos pos, BlockState state) {
		this(TileInit.mfBottlerAdvance, pos, state);
	}

	public TileMFBottlerAdcanced(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public List<ItemStack> getStackList () {
		return this.stackList;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF () {
		return this.maxMagiaFlux;
	}

	// 受信するMF量の取得
	@Override
	public int getReceiveMF () {
		return 250000;
	}
}
