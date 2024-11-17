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

public class TileMFChangerMaster extends TileMFChanger {

	public int maxMagiaFlux = 4000000;				// 最大MF量を設定

	public TileMFChangerMaster(BlockPos pos, BlockState state) {
		this(TileInit.changerMaster, pos, state);
	}

	public TileMFChangerMaster(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new SingleHandlerProvider(this.getHandler(this.inputInv, IN));
	}

	protected final StackHandler inputInv = new StackHandler(this.getInvSize()) {

		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			return SweetMagicAPI.hasMF(stack) ? super.insertItem(slot, stack, simulate) : stack;
		}
	};

	// NBTの読み込み
	@Override
	public void loadNBT(CompoundTag tags) {
		this.inputInv.deserializeNBT(tags.getCompound("inputInv"));
	}

	// NBTの書き込み
	@Override
	public void saveNBT(CompoundTag tags) {
		tags.put("inputInv", this.inputInv.serializeNBT());
	}

	// 最大MFの取得
	@Override
	public int getMaxMF () {
		return this.maxMagiaFlux;
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize () {
		return 10;
	}

	// MFスロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}
}
