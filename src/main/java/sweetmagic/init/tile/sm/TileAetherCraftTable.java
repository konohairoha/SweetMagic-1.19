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
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.TagInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.tile.menu.AetherCraftTableMenu;

public class TileAetherCraftTable extends TileAbstractSM {

	private int sortType = 0;
	private boolean isAscending = true;

	public TileAetherCraftTable(BlockPos pos, BlockState state) {
		super(TileInit.aetherCraftTable, pos, state);
	}

	public TileAetherCraftTable(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public final StackHandler inputInv = new StackHandler(this.getInvSize());
	public final StackHandler outInv = new StackHandler(1);

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("inputInv", this.inputInv.serializeNBT());
		tag.put("outInv", this.outInv.serializeNBT());
		tag.putInt("sortType", this.sortType);
		tag.putBoolean("isAscending", this.isAscending);
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.inputInv.deserializeNBT(tag.getCompound("inputInv"));
		this.outInv.deserializeNBT(tag.getCompound("outInv"));
		this.sortType = tag.getInt("sortType");
		this.isAscending = tag.getBoolean("isAscending");
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize () {
		return 9;
	}

	// スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// スロットのアイテムを取得
	public  ItemStack getInputItem(int i) {
		return this.getInput().getStackInSlot(i);
	}

	// スロットの取得
	public IItemHandler getOut() {
		return this.outInv;
	}

	// スロットのアイテムを取得
	public  ItemStack getOutItem() {
		return this.getOut().getStackInSlot(0);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new AetherCraftTableMenu(windowId, inv, this);
	}

	public void setSortType (int sortType) {
		this.sortType = sortType;
	}

	public int getSortType () {
		return this.sortType;
	}

	public void setAscending (boolean isAscending) {
		this.isAscending = isAscending;
	}

	public boolean getAscending () {
		return this.isAscending;
	}

	public int getMaxViewChest () {
		return 4;
	}

	public boolean isReader (Block block) {
		return block.defaultBlockState().is(TagInit.CHEST_READER);
	}

	public boolean isInfoEmpty() {
		List<ItemStack> stackList = new ArrayList<>();
		for(int i = 0; i < 9; i++)
			this.addStackList(stackList, this.getInputItem(i));
		return stackList.isEmpty();
	}
}
