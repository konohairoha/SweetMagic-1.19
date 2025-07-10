package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.api.SweetMagicAPI;
import sweetmagic.api.iitem.IMFTool;
import sweetmagic.init.BlockInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.tile.menu.MFTableMenu;

public class TileMFTable extends TileSMMagic {

	public int maxMagiaFlux = 20000;				// 最大MF量を設定
	public boolean isReceive = true;				// 受け取る側かどうか

	public TileMFTable(BlockPos pos, BlockState state) {
		super(TileInit.table, pos, state);
	}

	public TileMFTable(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	protected final StackHandler wandInv = new StackHandler(this.getInvSize(), true) {

		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			return SweetMagicAPI.hasMF(stack) ? super.insertItem(slot, stack, simulate) : stack;
		}
	};

	protected final StackHandler fuelInv = new StackHandler(this.getInvSize()) {

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
		if (this.getTickTime() % 10 != 0) { return; }

		// MF量が最大に足してなかったら動かす
		if (!this.isMaxMF()) {
			this.smeltItem();
		}

		if (this.getTickTime() % 20 != 0) { return; }

		// MFが空ではないなら杖にMFを入れる
		if (!this.isMFEmpty()) {
			this.wandChargeMF();
		}
	}

	// インプットスロットのMFを取得
	public void smeltItem() {
		ItemStack stack = this.getFuelItem();
		if (stack.isEmpty()) { return; }

		ItemStack copy = stack.copy();
		copy.setCount(1);

		// 燃焼アイテムのMFを取得してMFに加算する
		this.setMF(this.getMF() + this.getItemMF(copy));
		stack.shrink(1);
		this.sentClient();
	}

	// 杖スロットにある杖にMFを入れる
	public void wandChargeMF() {

		for (int i = 0; i < this.getInvSize(); i++) {
			ItemStack stack = this.getInputItem(i);
			if(stack.isEmpty() || this.wandMaxMF(stack) || this.isMFEmpty()){ continue; }

			// 杖にMFを補給
			IMFTool wand = (IMFTool) stack.getItem();
			wand.insertMF(stack, this);

			// MFが最大になったときに通知
			if (this.wandMaxMF(stack)) {
				this.playSound(this.getTilePos(), SoundEvents.PLAYER_LEVELUP, 1F, 1F);
				this.getLevel().levelEvent(2003, this.getBlockPos().above(2), 0);
			}
		}
	}

	// 杖の杖が最大値を超えているか
	public boolean wandMaxMF(ItemStack stack) {
		IMFTool wand = (IMFTool) stack.getItem();
		return wand.getMF(stack) >= wand.getMaxMF(stack);
	}

	public void loadNBT(CompoundTag tags) {
		this.wandInv.deserializeNBT(tags.getCompound("wandInv"));
		this.fuelInv.deserializeNBT(tags.getCompound("fuelInv"));
	}

	public void saveNBT(CompoundTag tags) {
		tags.put("wandInv", this.wandInv.serializeNBT());
		tags.put("fuelInv", this.fuelInv.serializeNBT());
	}

	// 最大MFの取得
	@Override
	public int getMaxMF() {
		return this.maxMagiaFlux;
	}

	// 受信側かどうかの取得
	@Override
	public boolean getReceive() {
		return this.isReceive;
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize() {
		return 1;
	}

	// 杖スロットの取得
	public IItemHandler getInput() {
		return this.wandInv;
	}

	// 杖スロットのアイテムを取得
	public ItemStack getInputItem(int i) {
		return this.getInput().getStackInSlot(i);
	}

	// 燃焼スロットの取得
	public IItemHandler getFuel() {
		return this.fuelInv;
	}

	// 燃焼スロットのアイテムを取得
	public ItemStack getFuelItem() {
		return this.getFuel().getStackInSlot(0);
	}

	// 消費MF量の取得
	public int getShrinkMF() {
		return 5000;
	}

	@Override
	public Component getDisplayName() {
		return BlockInit.mftable.getName();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new MFTableMenu(windowId, inv, this);
	}

	// インベントリのアイテムを取得
	public List<ItemStack> getInvList() {
		List<ItemStack> stackList = new ArrayList<>();

		for (int i = 0; i < this.getInvSize(); i++) {
			this.addStackList(stackList, this.getInputItem(i));
		}

		this.addStackList(stackList, this.getFuelItem());
		return stackList;
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.getInvList().isEmpty() && this.isMFEmpty();
	}
}
