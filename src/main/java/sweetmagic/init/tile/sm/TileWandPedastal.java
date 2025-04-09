package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.sm.WandPedastal;

public class TileWandPedastal extends TileAbstractSM {

	protected final StackHandler inputInv = new StackHandler(this.getInvSize());

	public TileWandPedastal(BlockPos pos, BlockState state) {
		this(TileInit.wandPedastal, pos, state);
	}

	public TileWandPedastal(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new SingleHandlerProvider(this.inputInv, IN_OUT);
	}

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
	public int getInvSize() {
		return 1;
	}

	// スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// スロットのアイテムを取得
	public ItemStack getInputItem(int i) {
		return this.getInput().getStackInSlot(i);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return null;
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.getInputList().isEmpty();
	}

	public List<ItemStack> getInputList() {
		List<ItemStack> stackList = new ArrayList<>();

		for (int i = 0; i < this.getInvSize(); i++) {
			this.addStackList(stackList, this.getInputItem(i));
		}

		return stackList;
	}

	public int getData() {
		return ((WandPedastal) this.getBlock(this.getBlockPos())).data;
	}
}
