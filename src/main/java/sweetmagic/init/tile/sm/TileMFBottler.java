package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import sweetmagic.api.SweetMagicAPI;
import sweetmagic.init.ItemInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.tile.menu.MFBottlerMenu;

public class TileMFBottler extends TileSMMagic {

	public int maxMagiaFlux = 1000000;				// 最大MF量を設定
	public boolean isSelect = false;
	public int selectId = -1;
	public int setCount = 1;
	public ItemStack outStack = ItemStack.EMPTY;

	private List<ItemStack> stackList = Arrays.<ItemStack> asList(
		new ItemStack(ItemInit.mf_small_bottle), new ItemStack(ItemInit.mf_bottle), new ItemStack(ItemInit.magia_bottle)
	);

	protected final StackHandler inputInv = new StackHandler(this.getInvSize());

	public TileMFBottler(BlockPos pos, BlockState state) {
		this(TileInit.mfBottler, pos, state);
	}

	public TileMFBottler(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new SingleHandlerProvider(this.inputInv, OUT);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		if (!this.isSelect || this.outStack.isEmpty() || this.isRSPower()) { return; }

		this.craftFinish();
	}

	public void craftFinish() {
		ItemStack outStack = ItemHandlerHelper.insertItemStacked(this.getInput(), this.outStack.copy(), true);
		if (!outStack.isEmpty()) { return; }

		this.setMF(this.getMF() - SweetMagicAPI.getMF(this.outStack.copy()) * this.setCount);
		ItemHandlerHelper.insertItemStacked(this.getInput(), this.outStack.copy(), false);
		this.outStack = ItemStack.EMPTY;
		this.isSelect = false;
		this.setCount = 1;
		this.sendInfo();
	}

	public List<ItemStack> getStackList () {
		return this.stackList;
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize () {
		return 18;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF () {
		return this.maxMagiaFlux;
	}

	// 受信するMF量の取得
	@Override
	public int getReceiveMF () {
		return 50000;
	}

	// 出力スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// 出力スロットのアイテムを取得
	public ItemStack getInputItem(int i) {
		return this.getInput().getStackInSlot(i);
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("outInv", this.inputInv.serializeNBT());
		tag.putBoolean("isSelect", this.isSelect);
		tag.putInt("selectId", this.selectId);
		tag.putInt("stackCount", this.setCount);
		tag.put("outstack", this.outStack.save(new CompoundTag()));
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.inputInv.deserializeNBT(tag.getCompound("outInv"));
		this.isSelect = tag.getBoolean("isSelect");
		this.selectId = tag.getInt("selectId");
		this.setCount = tag.getInt("stackCount");
		this.outStack = ItemStack.of(tag.getCompound("outstack"));
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new MFBottlerMenu(windowId, inv, this);
	}

	// RS信号で動作を停止するかどうか
	public boolean isRSStop () {
		return true;
	}

	// インベントリのアイテムを取得
	public List<ItemStack> getInvList () {
		List<ItemStack> stackList = new ArrayList<>();

		for (int i = 0; i < this.getInvSize(); i++) {
			this.addStackList(stackList, this.getInputItem(i));
		}

		return stackList;
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.getInvList().isEmpty() && this.isMFEmpty();
	}
}
