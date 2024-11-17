package sweetmagic.init.tile.sm;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.api.SweetMagicAPI;
import sweetmagic.init.TileInit;

public class TileMFTableAdvanced extends TileMFTable {

	public int maxMagiaFlux = 400000;				// 最大MF量を設定

	protected final StackHandler wandInv = new StackHandler(this.getInvSize(), true) {

		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			return SweetMagicAPI.hasMF(stack) ? super.insertItem(slot, stack, simulate) : stack;
		}
	};

	public TileMFTableAdvanced(BlockPos pos, BlockState state) {
		super(TileInit.tableAdavance, pos, state);
	}

	public TileMFTableAdvanced(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public void saveNBT(CompoundTag tags) {
		tags.put("wandInv", this.wandInv.serializeNBT());
		tags.put("fuelInv", this.fuelInv.serializeNBT());
	}

	public void loadNBT(CompoundTag tags) {
		this.wandInv.deserializeNBT(tags.getCompound("wandInv"));
		this.fuelInv.deserializeNBT(tags.getCompound("fuelInv"));
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize () {
		return 4;
	}

	// 受信するMF量の取得
	@Override
	public int getReceiveMF () {
		return 80000;
	}

	// 受信するMF量の取得
	@Override
	public int getShrinkMF () {
		return 20000;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF () {
		return this.maxMagiaFlux;
	}

	// 入力スロットの取得
	public IItemHandler getInput() {
		return this.wandInv;
	}
}
