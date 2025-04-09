package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.magic.AetherHopper;
import sweetmagic.init.tile.menu.AetherHopperMenu;

public class TileAetherHopper extends TileSMMagic {

	public int maxMagiaFlux = 20000;
	public final StackHandler inputInv = new StackHandler(this.getInvSize());
	protected final StackHandler wandInv = new StackHandler(this.getInvSize());

	public TileAetherHopper(BlockPos pos, BlockState state) {
		this(TileInit.aetherHopper, pos, state);
	}

	public TileAetherHopper(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new SingleHandlerProvider(this.inputInv, IN_OUT);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		if (this.tickTime % 40 != 0 || this.isRSPower()) { return; }

		this.tickTime = 0;

		// アイテム吸い込み
		this.suctionItem(pos);

		// ホッパーからアイテムをチェストに入れる
		this.extractItem(pos);

		// MFが空でなければ実行
		if (!this.isMFEmpty()) {
			this.setHopperItem(world, pos);
		}
	}

	// ホッパーからアイテムをチェストに入れる
	public void extractItem (BlockPos pos) {
		BlockEntity tile = this.getTile(pos.relative(this.getFace()));
		if (tile == null) { return; }

		IItemHandler handler = this.getItemHandler(tile, this.getFaceReverse());
		if (handler == null) { return; }

		for (int h = 0; h < this.getInvSize(); h++) {

			ItemStack input = this.getInputItem(h);
			if (input.isEmpty()) { continue; }

			for (int i = 0; i < handler.getSlots(); i++) {

				ItemStack inStack = handler.insertItem(i, input.copy(), false);
				input.setCount(inStack.getCount());
				if (inStack.isEmpty()) { break; }
			}
		}
	}

	// アイテム吸い込み
	public void suctionItem(BlockPos pos) {
		Direction face = this.getFace() == Direction.UP ? Direction.DOWN : Direction.UP;
		BlockEntity tile = this.getTile(pos.relative(face));
		if (tile == null) { return; }

		IItemHandler handler = this.getItemHandler(tile, this.getFace() == Direction.UP ? Direction.UP : Direction.DOWN);
		if (handler == null) { return; }

		for (int i = 0; i < handler.getSlots(); i++) {

			ItemStack output = handler.getStackInSlot(i);
			if (output.isEmpty()) { continue; }

			ItemStack stack = ItemHandlerHelper.insertItemStacked(this.getInput(), output.copy(), false);
			output.setCount(stack.getCount());
		}
	}

	// 別のホッパーへ送り付ける
	public void setHopperItem(Level world, BlockPos pos) {
		ItemStack stack = this.getWandItem();
		if (stack.isEmpty()) { return; }

		// NBTを持っていないなら終了
		CompoundTag tag = stack.getTag();
		if (tag == null || !tag.contains("X")) { return; }

		// 座標の取得
		int x = tag.getInt("X");
		int y = tag.getInt("Y");
		int z = tag.getInt("Z");
		BlockPos targetPos = new BlockPos(x, y, z);

		// ホッパー以外なら終了
		if ( !(this.getBlock(targetPos) instanceof AetherHopper) ) { return; }

		IItemHandler handler = this.getItemHandler(this.getTile(targetPos), Direction.UP);
		if (handler == null) { return; }

		int mf = this.getMF();
		int sumMF = 0;

		for (int h = 0; h < this.getInvSize(); h++) {

			ItemStack input = this.getInputItem(h);
			if (input.isEmpty()) { continue; }

			// MFが足りなくなったら終了
			if ( (mf - sumMF) < input.getCount()) { return; }

			for (int i = 0; i < handler.getSlots(); i++) {

				ItemStack inStack = handler.insertItem(i, input.copy(), false);
				input.setCount(inStack.getCount());
				if (inStack.isEmpty()) {
					sumMF += input.getCount();
					break;
				}
			}
		}

		if (sumMF > 0) {
			this.setMF(mf - sumMF);
			this.sendPKT();
		}
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize() {
		return 24;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF() {
		return this.maxMagiaFlux;
	}

	// 杖スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// 杖スロットのアイテムを取得
	public ItemStack getInputItem(int i) {
		return this.getInput().getStackInSlot(i);
	}

	// 杖スロットの取得
	public IItemHandler getWand() {
		return this.wandInv;
	}

	// 杖スロットのアイテムを取得
	public ItemStack getWandItem() {
		return this.getWand().getStackInSlot(0);
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("inputInv", this.inputInv.serializeNBT());
		tag.put("wandInv", this.wandInv.serializeNBT());
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.inputInv.deserializeNBT(tag.getCompound("inputInv"));
		this.wandInv.deserializeNBT(tag.getCompound("wandInv"));
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new AetherHopperMenu(windowId, inv, this);
	}

	// 向きの取得
	public Direction getFace () {
		return this.getState(this.getBlockPos()).getValue(AetherHopper.FACING);
	}

	// 向きの取得
	public Direction getFaceReverse () {
		switch (this.getState(this.getBlockPos()).getValue(AetherHopper.FACING)) {
		case NORTH: return Direction.SOUTH;
		case SOUTH: return Direction.NORTH;
		case WEST: return Direction.EAST;
		case EAST: return Direction.WEST;
		case UP: return Direction.DOWN;
		case DOWN: return Direction.UP;
		default: return Direction.NORTH;
		}
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

		this.addStackList(stackList, this.getWandItem());
		return stackList;
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.getInvList().isEmpty() && this.isMFEmpty();
	}
}
