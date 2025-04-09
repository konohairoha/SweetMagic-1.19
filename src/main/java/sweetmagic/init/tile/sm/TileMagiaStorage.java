package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.sm.MagiaStorage;
import sweetmagic.init.tile.menu.MagiaStorageMenu;

public class TileMagiaStorage extends TileAbstractSM {

	private Block block = null;

	public TileMagiaStorage(BlockPos pos, BlockState state) {
		this(TileInit.magiaStorage, pos, state);
	}

	public TileMagiaStorage(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new SingleHandlerProvider(this.inputInv, IN_OUT);
	}

	public final MagiaHandler inputInv = new MagiaHandler(this.getInvSize());

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("inputInv", this.inputInv.serializeNBT());
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.inputInv.deserializeNBT(tag.getCompound("inputInv"));
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize() {
		return 104;
	}

	public Block getBlock() {
		if (this.block == null) {
			this.block = this.getBlock(this.getBlockPos());
		}

		return this.block;
	}

	public int getMaxStackSize() {
		switch (this.getData()) {
		case 1 : return 1280;
		case 2 : return 5120;
		case 3 : return 25600;
		case 4 : return Integer.MAX_VALUE;
		default: return 256;
		}
	}

	// スロットの取得
	public MagiaHandler getInput() {
		return this.inputInv;
	}

	// スロットのアイテムを取得
	public ItemStack getInputItem(int i) {
		return this.getInput().getStackInSlot(i);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new MagiaStorageMenu(windowId, inv, this);
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

	public List<ItemStack> getInvList() {
		List<ItemStack> stackList = new ArrayList<>();

		for (int i = 0; i < this.getInvSize(); i++) {
			stackList.add(this.getInputItem(i));
		}

		return stackList;
	}

	public int getData() {
		return ((MagiaStorage) this.getBlock(this.getBlockPos())).getData();
	}
}
