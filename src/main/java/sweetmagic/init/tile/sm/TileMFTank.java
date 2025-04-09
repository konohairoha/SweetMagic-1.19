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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import sweetmagic.api.SweetMagicAPI;
import sweetmagic.api.iblock.ITileMF;
import sweetmagic.init.TileInit;
import sweetmagic.init.tile.menu.MFTankMenu;
import sweetmagic.recipe.tank.TankRecipe;

public class TileMFTank extends TileSMMagic {

	public int maxMagiaFlux = 100000;
	public int viewMFInsert = 0;
	public int viewMFExtract = 0;
	public int newMFInsert = 0;
	public int newMFExtract = 0;
	public int oldMFInsert = 0;
	public int oldMFExtract = 0;
	protected final StackHandler inputInv = new StackHandler(this.getInvSize());
	protected final StackHandler outputInv = new StackHandler(this.getSubInvSize());

	public TileMFTank(BlockPos pos, BlockState state) {
		this(TileInit.tank, pos, state);
	}

	public TileMFTank(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new InOutHandlerProvider(this.inputInv, this.outputInv);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state) {

		super.serverTick(level, pos, state);
		if (this.getTickTime() % 10 != 0) { return; }

		this.smeltAction();

		// MFが空でなければタンク下のタンクにMFを入れる
		if (!this.isMFEmpty()) {
			this.underInsertMF(pos);
		}

		if (this.getTickTime() % 20 == 0) {
			this.getMFStatistics();
		}
	}

	public void smeltAction() {

		// 精錬可能かつ必要MF以上なら
		if (this.hasNeedMF() && this.canSmelt(0)) {
			this.smeltItem(0);
		}
	}

	// 精錬可能か銅か
	public boolean canSmelt(int index) {
		ItemStack toSmelt = this.getInputItem(index);
		if (toSmelt.isEmpty()) { return false; }
		return !TankRecipe.getRecipe(this.level, Arrays.<ItemStack> asList(toSmelt)).isEmpty();
	}

	// 精錬後のアイテム
	protected void smeltItem(int index) {

		// レシピを取得して見つからなければ終了
		ItemStack inputStack = this.getInputItem(index);
		TankRecipe recipe = TankRecipe.getRecipe(this.level, Arrays.<ItemStack> asList(inputStack)).get();

		int needMF = recipe.getMFList().get(0);
		ItemStack smeltResult = recipe.getResultItem();

		// smeltResultがnullまたは必要MF未満なら終了
		if (smeltResult.isEmpty() || this.getMF() < needMF) { return; }
		if (!ItemHandlerHelper.insertItemStacked(this.getOut(), smeltResult, true).isEmpty()) { return; }

		this.setMF(this.getMF() - needMF);
		ItemHandlerHelper.insertItemStacked(this.getOut(), smeltResult, false);
		inputStack.shrink(recipe.getRequestList().get(0).getCount());
		this.sentClient();
	}

	public void getMFStatistics() {
		this.viewMFInsert = this.oldMFInsert = this.newMFInsert = this.newMFInsert - this.oldMFInsert;
		this.viewMFExtract = this.oldMFExtract = this.newMFExtract = this.newMFExtract - this.oldMFExtract;
		this.tickTime = 0;
		this.sendPKT();
	}

	// 必要MF
	public int getNeedMF(ItemStack stack) {
		return SweetMagicAPI.getMF(stack);
	}

	// MF最大時のインサート処理
	public void maxMFInsert(ITileMF tran) {
		BlockEntity tile = this.getTile(this.getBlockPos().above());
		if ( !(tile instanceof TileMFTank mfTile)) { return; }

		mfTile.insertMF(mfTile, tran, mfTile.getTickTime());
	}

	// タンク下のタンクにMFを入れる
	public void underInsertMF(BlockPos pos) {
		BlockEntity tile = this.getTile(pos.below());
		if ( !(tile instanceof TileMFTank mfTile)) { return; }

		// MFが最大のときは終了
		if (mfTile.isMaxMF()) { return; }
		this.insertMF(mfTile, this, this.getTickTime());
	}

	// MF受信時のインサート処理
	public void recipedMFInsert() {
		BlockEntity tile = this.getTile(this.getBlockPos().below());
		if ( !(tile instanceof TileMFTank mfTile)) { return; }

		// MFが最大のときは終了
		if (mfTile.isMaxMF()) { return; }
		this.insertMF(mfTile, this, this.getTickTime());
	}

	public void loadNBT(CompoundTag tags) {
		this.inputInv.deserializeNBT(tags.getCompound("inputInv"));
		this.outputInv.deserializeNBT(tags.getCompound("outputInv"));
		this.tickTime = tags.getInt("tickTime");
		this.viewMFInsert = tags.getInt("viewMFInsert");
		this.newMFInsert = tags.getInt("newMFInsert");
		this.oldMFInsert = tags.getInt("oldMFInsert");
		this.viewMFExtract = tags.getInt("viewMFExtract");
		this.newMFExtract = tags.getInt("newMFExtract");
		this.oldMFExtract = tags.getInt("oldMFExtract");
	}

	public void saveNBT(CompoundTag tags) {
		tags.put("inputInv", this.inputInv.serializeNBT());
		tags.put("outputInv", this.outputInv.serializeNBT());
		tags.putInt("tickTime", this.tickTime);
		tags.putInt("viewMFInsert", this.viewMFInsert);
		tags.putInt("newMFInsert", this.newMFInsert);
		tags.putInt("oldMFInsert", this.oldMFInsert);
		tags.putInt("viewMFExtract", this.viewMFExtract);
		tags.putInt("newMFExtract", this.newMFExtract);
		tags.putInt("oldMFExtract", this.oldMFExtract);
	}

	public void setMF(int mf) {
		int oldMF = this.getMF();
		super.setMF(mf);
		int newMF = this.getMF();
		if (this.getLevel() == null || !this.isSever()) { return; }

		if (newMF > oldMF) {
			this.newMFInsert += newMF - oldMF;
		}

		else if (oldMF > newMF) {
			this.newMFExtract += oldMF - newMF;
		}
	}

	// 最大MFの取得
	@Override
	public int getMaxMF() {
		return this.maxMagiaFlux;
	}

	// 受信するMF量の取得
	@Override
	public int getReceiveMF() {
		return 10000;
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize() {
		return 1;
	}

	public int getSubInvSize() {
		return 3;
	}

	// 入力スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// 入力スロットのアイテムを取得
	public ItemStack getInputItem(int i) {
		return this.getInput().getStackInSlot(i);
	}

	// 出力スロットの取得
	public IItemHandler getOut() {
		return this.outputInv;
	}

	// 出力スロットのアイテムを取得
	public ItemStack getOutItem(int i) {
		return this.getOut().getStackInSlot(i);
	}

	// 消費MF量の取得
	public int getShrinkMF() {
		return 1000;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new MFTankMenu(windowId, inv, this);
	}

	// インベントリのアイテムを取得
	public List<ItemStack> getInvList() {
		List<ItemStack> stackList = new ArrayList<>();

		for (int i = 0; i < this.getInvSize(); i++) {
			this.addStackList(stackList, this.getInputItem(i));
		}

		for (int i = 0; i < this.getSubInvSize(); i++) {
			this.addStackList(stackList, this.getOutItem(i));
		}

		return stackList;
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.getInvList().isEmpty() && this.isMFEmpty();
	}
}
