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

public class TileMFTableMaster extends TileMFTable {

	public int maxMagiaFlux = 8000000;

	protected final StackHandler wandInv = new StackHandler(this.getInvSize(), true) {

		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			return SweetMagicAPI.hasMF(stack) ? super.insertItem(slot, stack, simulate) : stack;
		}
	};

	public TileMFTableMaster(BlockPos pos, BlockState state) {
		super(TileInit.tableMaster, pos, state);
	}

	public TileMFTableMaster(BlockEntityType<?> type, BlockPos pos, BlockState state) {
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
	public int getInvSize() {
		return 6;
	}

	// 受信するMF量の取得
	@Override
	public int getReceiveMF() {
		return 200000;
	}

	// 受信するMF量の取得
	@Override
	public int getShrinkMF() {
		return 40000;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF() {
		return this.maxMagiaFlux;
	}

	// 入力スロットの取得
	public IItemHandler getInput() {
		return this.wandInv;
	}
}
