package sweetmagic.init.tile.sm;

import java.util.Arrays;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.BlockInit;
import sweetmagic.init.TagInit;
import sweetmagic.init.TileInit;

public class TileAltarCreatStar extends TilePedalCreate {

	public int maxMagiaFlux = 2000000;				// 最大MF量を設定
	public boolean isRangeBlock = false;

	public TileAltarCreatStar(BlockPos pos, BlockState state) {
		super(TileInit.altarCreatStar, pos, state);
	}

	public TileAltarCreatStar(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// クライアント側処理
	public void clientTick(Level world, BlockPos pos, BlockState state) {
		super.clientTick(world, pos, state);
		if (this.tickTime % 20 != 0) { return; }

		this.isHaveBlock = this.getState(pos.below()).is(TagInit.PC_BLOCK);
		this.isRangeBlock = this.checkRangeBlock(false);
		if (!this.isCraft || this.nowTick > ((this.maxCrafttime - 1) * 20)) { return; }

		float addY = this.nowTick * 0.00575F * (this.quickCraft ? 2F : 1F);
		this.spawnParticleRing(world, pos.getX() + 0.5F, pos.getY() + 1.4F + addY, pos.getZ() + 0.5F, 0D, 0D, 0D, 0.75D);
	}

	// クラフト可能か
	public MutableComponent checkCanCraft(List<ItemStack> stackList) {

		// 必要なブロックがない場合
		if (!this.checkRangeBlock(false)) {
			return this.getTipArray(this.getText("pedastal_norangeblock"), ":", this.getRangeBlock().getName().withStyle(RED), this.getText("or"), this.getOverRangeBlock().getName().withStyle(RED));
		}

		return super.checkCanCraft(stackList);
	}

	public boolean checkRangeBlock(boolean isClient) {

		BlockPos pos = this.getBlockPos().below();

		List<BlockPos> dcPosList = Arrays.<BlockPos> asList(
			pos.north(), pos.south(), pos.east(), pos.west()
		);

		List<BlockPos> acPosList = Arrays.<BlockPos> asList(
			pos.north(2), pos.south(2), pos.east(2), pos.west(2),
			pos.offset(-1, 0, -1), pos.offset(1, 0, -1), pos.offset(-1, 0, 1), pos.offset(1, 0, 1)
		);

		for (BlockPos p : dcPosList) {
			if (!this.getState(p).is(TagInit.DC_BLOCK)) { return false; }
		}

		for (BlockPos p : acPosList) {
			if (!this.getState(p).is(TagInit.AC_BLOCK)) { return false; }
		}

		return true;
	}

	public Block getNeedBlock() {
		return BlockInit.purecrystal_block;
	}

	public Block getRangeBlock() {
		return BlockInit.divinecrystal_block;
	}

	public Block getOverRangeBlock() {
		return BlockInit.aethercrystal_block;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF() {
		return this.maxMagiaFlux;
	}

	// 受信するMF量の取得
	@Override
	public int getReceiveMF() {
		return 80000;
	}
}
