package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.api.SweetMagicAPI;
import sweetmagic.init.BlockInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.tile.menu.MFChangerMenu;

public class TileMFChanger extends TileSMMagic {

	public int maxMagiaFlux = 30000;				// 最大MF量を設定
	public boolean isReceive = false;				// 受け取る側かどうか

	public TileMFChanger(BlockPos pos, BlockState state) {
		this(TileInit.changer, pos, state);
	}

	public TileMFChanger(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new SingleHandlerProvider(this.inputInv, IN);
	}

	protected final StackHandler inputInv = new StackHandler(this.getInvSize()) {

		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			return SweetMagicAPI.hasMF(stack) ? super.insertItem(slot, stack, simulate) : stack;
		}
	};

	// サーバー側処理
	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state) {
		super.serverTick(level, pos, state);
		if (this.getTickTime() % 40 != 0 || this.isRSPower()) { return; }

		// MF量が最大に足してなかったら動かす
		if (!this.isMaxMF()) {
			this.changeMF();
		}
	}

	// MFに変換
	protected boolean changeMF() {

		boolean isUpdate = false;

		for (int i = 0; i < this.getInvSize(); i++) {

			// スロットが空なら終了
			ItemStack stack = this.getInputItem(i);
			if (stack.isEmpty() || !SweetMagicAPI.hasMF(stack)) { continue; }

			// 1アイテムのMF量の取得
			int mfValue = this.getItemMF(stack.getItem());

			// 消費個数を計算
			int value = (this.getMaxMF() - this.getMF()) / mfValue + 1;
			value = value > stack.getCount() ? stack.getCount(): value;

			// MF量を計算
			int itemMF = mfValue * value;

			// MFを入れる
			this.setMF(this.getMF() + itemMF);
			isUpdate = true;

			// スロットのアイテムを減らす
			stack.shrink(value);

			// 最大になったら終了
			if (this.isMaxMF()) { break; }
		}

		if (isUpdate) {
			this.sentClient();
		}

		return true;
	}

	public void loadNBT(CompoundTag tags) {
		this.inputInv.deserializeNBT(tags.getCompound("inputInv"));
	}

	public void saveNBT(CompoundTag tags) {
		tags.put("inputInv", this.inputInv.serializeNBT());
	}

	// 最大MFの取得
	@Override
	public int getMaxMF () {
		return this.maxMagiaFlux;
	}

	// 受信側かどうかの取得
	@Override
	public boolean getReceive () {
		return this.isReceive;
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize () {
		return 3;
	}

	// MFスロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// インプットスロットのアイテムを取得
	public  ItemStack getInputItem(int i) {
		return this.getInput().getStackInSlot(i);
	}

	@Override
	public Component getDisplayName() {
		return BlockInit.mfchanger.getName();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new MFChangerMenu(windowId, inv, this);
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
