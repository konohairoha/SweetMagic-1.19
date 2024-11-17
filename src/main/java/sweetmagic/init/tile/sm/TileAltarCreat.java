package sweetmagic.init.tile.sm;

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

public class TileAltarCreat extends TilePedalCreate {

	public int maxMagiaFlux = 200000;				// 最大MF量を設定
	public boolean isRangeBlock = false;

	public TileAltarCreat(BlockPos pos, BlockState state) {
		super(TileInit.altarCreat, pos, state);
	}

	public TileAltarCreat(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// クライアント側処理
	public void clientTick(Level world, BlockPos pos, BlockState state) {
		super.clientTick(world, pos, state);
		if (this.tickTime % 20 != 0) { return; }

		this.isHaveBlock = this.getState(pos.below()).is(TagInit.DC_BLOCK);
		this.isRangeBlock = this.checkRangeBlock(false);
		if (!this.isCraft || this.nowTick > ( (this.maxCrafttime - 1) * 20) ) { return; }

		float addY = this.nowTick * 0.00575F * (this.quickCraft ? 2F : 1F);
		this.spawnParticleRing(world, pos.getX() + 0.5F, pos.getY() + 1.4F + addY, pos.getZ() + 0.5F, 0D, 0D, 0D, 0.75D);
	}

	// クラフト可能か
	public MutableComponent checkCanCraft (List<ItemStack> stackList) {

		// 必要なブロックがない場合
		if (!this.checkRangeBlock(false)) {
			return this.getTipArray(this.getText("pedastal_norangeblock"), ":", this.getRangeBlock(false).getName().withStyle(RED));
		}

		return super.checkCanCraft(stackList);
	}

	public boolean checkRangeBlock (boolean isClient) {

		BlockPos pos = this.getBlockPos().below();

		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {

				if (x == 0 && z == 0) { continue; }

				if (!this.getState(pos.offset(x, 0, z)).is(TagInit.AC_BLOCK)) { return false; }
			}
		}

		return true;
	}

	public Block getNeedBlock (boolean isClient) {
		return isClient ? BlockInit.divinecrystal_block_alpha : BlockInit.divinecrystal_block;
	}

	public Block getRangeBlock  (boolean isClient) {
		return isClient ? BlockInit.aethercrystal_block_alpha : BlockInit.aethercrystal_block;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF () {
		return this.maxMagiaFlux;
	}

	// 受信するMF量の取得
	@Override
	public int getReceiveMF () {
		return 40000;
	}
}
