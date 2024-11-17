package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
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
import sweetmagic.init.tile.menu.MagiaDrawerMenu;

public class TileMagiaDrawer extends TileSMMagic {

	public int maxMagiaFlux = 100000;				// 最大MF量を設定
	public int range = 4;							// 範囲
	private static final int MIN_RANGE = 1;			// 最小範囲
	private static final int MAX_RANGE = 64;		// 最大範囲

	public final StackHandler inputInv = new StackHandler(this.getInvSize());

	public TileMagiaDrawer(BlockPos pos, BlockState state) {
		this(TileInit.magiarDrawer, pos, state);
	}

	public TileMagiaDrawer(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new SingleHandlerProvider(this.inputInv, IN_OUT);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		if (this.tickTime % 30 != 0 || this.isRSPower()) { return; }

		this.tickTime = 0;

		if (this.getMF() >= 4) {
			this.suctionItem(world, pos);
		}
	}

	// アイテム吸い込み
	public void suctionItem (Level world, BlockPos pos) {
		List<ItemEntity> entityList = this.getEntityList(ItemEntity.class, e -> !e.getItem().isEmpty(), this.range);
		if (entityList.isEmpty()) { return; }

		int isInsert = 0;
		int mf = this.getMF();

		for (ItemEntity entity : entityList) {

			ItemStack stack = entity.getItem();
			ItemStack insert = ItemHandlerHelper.insertItemStacked(this.getInput(), stack.copy(), false);

			if (insert.isEmpty() || insert.getCount() <= 0) {
				entity.discard();
				isInsert += stack.getCount();
			}

			if (isInsert >= mf) { break; }
		}

		// アイテム投入を行ったら
		if (isInsert > 0) {
			this.setMF(mf - isInsert);
			this.sendPKT();
		}
	}

	public void addRange (int id) {

		int addValue = 0;

		switch (id) {
		case 0:
			addValue = 1;
			break;
		case 1:
			addValue = 10;
			break;
		case 2:
			addValue = -1;
			break;
		case 3:
			addValue = -10;
			break;
		}

		this.range = Math.min(MAX_RANGE, Math.max(MIN_RANGE, this.range + addValue));
		this.clickButton();
		this.sendPKT();
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize () {
		return 104;
	}

	// 受信するMF量の取得
	@Override
	public int getReceiveMF () {
		return 10000;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF () {
		return this.maxMagiaFlux;
	}

	// 杖スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// 杖スロットのアイテムを取得
	public  ItemStack getInputItem(int i) {
		return this.getInput().getStackInSlot(i);
	}

	public boolean isInvEmpty () {

		for (int i = 0; i < this.getInvSize(); i ++) {
			if (!this.getInputItem(i).isEmpty()) { return false; }
		}

		return this.isMFEmpty();
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("inputInv", this.inputInv.serializeNBT());
		tag.putInt("range", this.range);
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.inputInv.deserializeNBT(tag.getCompound("inputInv"));
		this.range = tag.getInt("range");
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new MagiaDrawerMenu(windowId, inv, this);
	}

	// RS信号で動作を停止するかどうか
	public boolean isRSStop () {
		return true;
	}

	// インベントリのアイテムを取得
	public List<ItemStack> getInputList () {
		List<ItemStack> stackList = new ArrayList<>();

		for (int i = 0; i < this.getInvSize(); i++) {
			stackList.add(this.getInputItem(i));
		}

		return stackList;
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
