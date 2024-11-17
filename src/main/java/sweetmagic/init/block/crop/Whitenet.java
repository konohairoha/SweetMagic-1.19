package sweetmagic.init.block.crop;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.ItemInit;
import sweetmagic.init.TagInit;

public class Whitenet extends SweetCrops_STAGE3 {

	public Whitenet(String name) {
		super(name, 0, 2, false);
	}

	// 作物の取得
	@Override
	public ItemLike getCrop() {
		return ItemInit.whitenet;
	}

	@Override
	public ItemLike getSeed () {
		return ItemInit.whitenet_seed;
	}

	@Override
	public boolean checkPlace(BlockState state, BlockGetter get, BlockPos pos) {
		return state.is(BlockTags.LOGS) || state.is(TagInit.STONE) || state.is(BlockTags.MUSHROOM_GROW_BLOCK);
	}
}
