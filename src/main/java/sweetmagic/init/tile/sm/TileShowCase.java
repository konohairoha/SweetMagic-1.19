package sweetmagic.init.tile.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.TileInit;

public class TileShowCase extends TilePlate {

	public TileShowCase(BlockPos pos, BlockState state) {
		this(TileInit.showcase, pos, state);
	}

	public TileShowCase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new SingleHandlerProvider(this.inputInv, IN_OUT);
	}

	protected final StackHandler inputInv = new StackHandler(this.getInvSize(), true);

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.merge(this.inputInv.serializeNBT());
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.inputInv.deserializeNBT(tag);
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize () {
		return 2;
	}

	// スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// スロットのアイテムを取得
	public  ItemStack getInputItem(int i) {
		return this.getInput().getStackInSlot(i);
	}
}
