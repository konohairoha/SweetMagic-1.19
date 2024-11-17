package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import sweetmagic.init.TileInit;
import sweetmagic.init.tile.menu.FurnitureTableMenu;

public class TileFurnitureTable extends TileAbstractSM {

	public boolean isCraft = false;
	public ItemStack inputStack = ItemStack.EMPTY;
	public ItemStack outStack = ItemStack.EMPTY;
	public int selectId = 0;
	public boolean isSelect = false;
	public int setCount = 1;
	public int oldSetCount = 1;
	public boolean isSetCount = false;

	public TileFurnitureTable(BlockPos pos, BlockState state) {
		super(TileInit.furnitureTable, pos, state);
	}

	public TileFurnitureTable(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new InOutHandlerProvider(this.inputSideInv, this.outInv);
	}

	public final StackHandler inputInv = new StackHandler(1);
	public final StackHandler inputSideInv = new StackHandler(9);
	public final StackHandler resultInv = new StackHandler(1);
	public final StackHandler outInv = new StackHandler(this.getInvSize(), true);

	// サーバー側処理
	public void serverTick(Level level, BlockPos pos, BlockState state) {
		super.serverTick(level, pos, state);
		if (!this.isCraft || this.tickTime % 2 != 0) { return; }

		if (this.getInputItem().isEmpty()) {
			this.tickTime = 0;
			this.isCraft = false;
			this.outStack = ItemStack.EMPTY;
			this.sendPKT();
		}

		else {
			this.craftFinish();

			if (this.tickTime % 4 == 0) {
				this.playSound(pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, 0.5F, 1F);
			}
		}
	}

	public void insertInput () {
		for (int i = 0; i < 9; i++) {
			ItemStack stack = this.getInputSideItem(i);
			if (stack.isEmpty()) { continue; }

			ItemStack out = ItemHandlerHelper.insertItemStacked(this.getInput(), stack.copy(), false);
			stack.shrink(stack.getCount() - out.getCount());
		}
	}

	public void chekcSlot () {
		if (this.getInputItem().isEmpty() && this.setCount > 0) {
			this.setCount = 1;
			this.sendPKT();
		}
	}

	// 作成開始
	public void craftStart () {
		this.tickTime = 0;
		this.isCraft = true;
		this.sendPKT();
	}

	public void craftFinish () {

		ItemStack input = this.getInputItem();
		ItemStack out = this.outStack.copy();
		CompoundTag tags = input.getTag();

		if (out.isEmpty() || !input.is(this.inputStack.getItem())) {
			this.isCraft = false;
			this.sendPKT();
		}

		if (tags != null) {
			out.setTag(tags);
		}

		int outCount = out.getCount();
		int count = Math.min(64, Math.min(input.getCount() * outCount, this.setCount));
		out.setCount(count);
		int shrinkCount = out.getCount();

		if (!ItemHandlerHelper.insertItemStacked(this.getOut(), out, true).isEmpty()) {
			this.isCraft = false;
			return;
		}

		ItemHandlerHelper.insertItemStacked(this.getOut(), out, false);
		input.shrink(count / outCount);
		this.setCount -= shrinkCount;
		this.insertInput();
		this.sendPKT();

		if (this.getInputItem().isEmpty() || this.setCount <= 0) {
			this.tickTime = 0;
			this.isCraft = false;
			this.selectId = 0;
			this.setCount = 1;
			this.isSelect = false;
			this.outStack = ItemStack.EMPTY;
			this.sendPKT();
		}
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("inputInv", this.inputInv.serializeNBT());
		tag.put("inputSideInv", this.inputSideInv.serializeNBT());
		tag.put("resultInv", this.resultInv.serializeNBT());
		tag.put("outInv", this.outInv.serializeNBT());
		tag.putBoolean("isCraft", this.isCraft);
		tag.putBoolean("isSelect", this.isSelect);
		tag.putBoolean("isSetCount", this.isSetCount);
		tag.putInt("selectId", this.selectId);
		tag.putInt("setCount", this.setCount);
		tag.putInt("oldSetCount", this.oldSetCount);
		tag.put("outPutStack", this.outStack.save(new CompoundTag()));
		tag.put("inputStack", this.inputStack.save(new CompoundTag()));
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.inputInv.deserializeNBT(tag.getCompound("inputInv"));

		if (tag.contains("inputSideInv")) {
			this.inputSideInv.deserializeNBT(tag.getCompound("inputSideInv"));
		}

		this.resultInv.deserializeNBT(tag.getCompound("resultInv"));
		this.outInv.deserializeNBT(tag.getCompound("outInv"));
		this.isCraft = tag.getBoolean("isCraft");
		this.isSelect = tag.getBoolean("isSelect");
		this.isSetCount = tag.getBoolean("isSetCount");
		this.selectId = tag.getInt("selectId");
		this.setCount = tag.getInt("setCount");
		this.oldSetCount = tag.getInt("oldSetCount");
		this.outStack = ItemStack.of(tag.getCompound("outPutStack"));
		this.inputStack = ItemStack.of(tag.getCompound("inputStack"));
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize () {
		return 27;
	}

	// スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// スロットのアイテムを取得
	public  ItemStack getInputItem() {
		return this.getInput().getStackInSlot(0);
	}

	// スロットの取得
	public IItemHandler getInputSide() {
		return this.inputSideInv;
	}

	// スロットのアイテムを取得
	public  ItemStack getInputSideItem(int i) {
		return this.getInputSide().getStackInSlot(i);
	}

	// スロットの取得
	public IItemHandler getResult() {
		return this.resultInv;
	}

	// スロットのアイテムを取得
	public  ItemStack getResultItem() {
		return this.getResult().getStackInSlot(0);
	}

	// スロットの取得
	public IItemHandler getOut() {
		return this.outInv;
	}

	// スロットのアイテムを取得
	public  ItemStack getOutItem(int i) {
		return this.getOut().getStackInSlot(i);
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		List<ItemStack> stackList = new ArrayList<>();
		this.addStackList(stackList, this.getInputItem());
		for(int i = 0; i < 9; i++)
			this.addStackList(stackList, this.getInputSideItem(i));
		this.addStackList(stackList, this.getResultItem());
		for(int i = 0; i < this.getInvSize(); i++)
			this.addStackList(stackList, this.getOutItem(i));
		return stackList.isEmpty();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new FurnitureTableMenu(windowId, inv, this);
	}
}
