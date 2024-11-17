package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import sweetmagic.init.ItemInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.tile.menu.FreezerMenu;
import sweetmagic.recipe.feezer.FreezerRecipe;

public class TileFreezer extends TileAbstractSM {

	private static final int MAX_CRAFT_TIME = 6;
	public int craftTime = 0;
	public boolean isCraft = false;
	public ItemStack outStack = ItemStack.EMPTY;
	public List<ItemStack> craftList = new ArrayList<>();

	private static final int MAX_WATER_VALUE = 10000;
	private int waterValue = 0;
	private final int useWaterValue = 250;

	private final static ItemStack ICE = new ItemStack(Blocks.ICE);

	protected final StackHandler handInv = new StackHandler(1);
	protected final StackHandler inputInv = new StackHandler(this.getInvSize());
	protected final StackHandler outputInv = new StackHandler(this.getOutSize());
	protected final StackHandler bucketInv = new StackHandler(1);
	protected final StackHandler iceInv = new StackHandler(2);

	public TileFreezer(BlockPos pos, BlockState state) {
		super(TileInit.freezer, pos, state);
	}

	public TileFreezer(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// サーバー側処理
	public void serverTick(Level level, BlockPos pos, BlockState state) {
		super.serverTick(level, pos, state);
		if (this.tickTime % 10 != 0) { return; }

		// 水の投入
		this.insertWater();

		if (this.tickTime >= 20) {
			this.tickTime = 0;

			// 水が必要以上にあるなら
			if (this.isNeedWater()) {
				this.craftIce();
			}
		}

		// 作成中で
		if (this.isCraft) {

			// 一定時間が経てばクラフトの完成
			if (this.craftTime++ >= MAX_CRAFT_TIME) {
				this.craftFinish();
			}

			this.sendPKT();
		}

		// メインスロットに何もなければ終了
		if (this.isCraft || this.getHandItem().isEmpty()) { return; }

		// レシピが見つかれば作成開始
		if (this.checkRecipe()) {
			this.craftStart();
		}
	}

	// 素材の取得
	public List<ItemStack> getStackList () {

		List<ItemStack> stackList = new ArrayList<>();
		stackList.add(this.getHandItem());

		for (int i = 0; i < this.getInvSize(); i++) {
			ItemStack stack = this.getInputItem(i);
			if (stack.isEmpty()) { continue; }

			stackList.add(stack);
		}

		return stackList;
	}

	// レシピチェック
	public boolean checkRecipe () {
		return !FreezerRecipe.getRecipe(this.level, this.getStackList()).isEmpty();
	}

	// 作成開始
	public void craftStart () {

		// レシピを取得して見つからなければ終了
		List<ItemStack> stackList = this.getStackList();
		FreezerRecipe recipe = FreezerRecipe.getRecipe(this.level, stackList).get();

		// レシピから完成品を取得
		ItemStack resultStack = recipe.getResultItem().copy();
		if (!ItemHandlerHelper.insertItemStacked(this.getOutput(), resultStack, true).isEmpty()) { return; }

		// クラフトで使うアイテムを入れておく
		this.craftList = new ArrayList<ItemStack>(recipe.getRequestList());
		this.outStack = resultStack;
		List<ItemStack> requestList = recipe.getRequestList();

		// 要求アイテム分回す
		for (int count = 0; count < requestList.size(); count++) {

			// 要求アイテムの取得
			ItemStack request = requestList.get(count);

			// 初回はメインアイテムを消費
			if (count == 0) {
				stackList.get(0).shrink(request.getCount());
			}

			// 二回目以降
			else {
				for (int i = 1; i < stackList.size(); i++) {

					ItemStack stack = stackList.get(i);

					if (request.is(stack.getItem())) {
						stack.shrink(request.getCount());
						break;
					}
				}
			}
		}

		this.isCraft = true;
	}

	// クラフトの完成
	public void craftFinish () {
		ItemHandlerHelper.insertItemStacked(this.getOutput(), this.outStack, false);
		this.playSound(this.getBlockPos(), SoundInit.FREEZER_CRAFT, 0.1F, 1F);
		this.clearInfo();
	}

	// 水の投入
	public void insertWater () {
		ItemStack bucket = this.getBucketItem();
		if (bucket.isEmpty()) { return; }

		int insertWaterValue = 0;
		ItemStack copy = bucket.copy();

		if (bucket.is(Items.WATER_BUCKET)) {
			insertWaterValue = 1000;
		}

		else if (bucket.is(ItemInit.watercup)) {
			insertWaterValue = 250;
		}

		if (insertWaterValue <= 0 || !this.canInsertWater(insertWaterValue)) { return; }

		this.setWaterValue(this.getWaterValue() + insertWaterValue);
		this.sendPKT();
		bucket.shrink(1);

		if (copy.is(Items.WATER_BUCKET)) {
			ItemHandlerHelper.insertItemStacked(this.getBucket(), new ItemStack(Items.BUCKET), false);
		}
	}

	// 氷の作成
	public void craftIce () {
		if(!ItemHandlerHelper.insertItemStacked(this.getIce(), ICE.copy(), true).isEmpty()) { return; }

		ItemHandlerHelper.insertItemStacked(this.getIce(), ICE.copy(), false);
		this.setWaterValue(this.getWaterValue() - this.useWaterValue);
		this.sendPKT();
	}

	// 初期化
	public void clearInfo () {
		this.craftTime = 0;
		this.isCraft = false;
		this.outStack = ItemStack.EMPTY;
		this.craftList.clear();
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.getDropList().isEmpty();
	}

	// ドロップリストを取得
	public List<ItemStack> getDropList () {

		List<ItemStack> stackList = new ArrayList<>();
		this.addStackList(stackList, this.getBucketItem());
		this.addStackList(stackList, this.getHandItem());

		for (int i = 0; i < 2; i++) {
			this.addStackList(stackList, this.getIceItem(i));
		}

		for (int i = 0; i < this.getInvSize(); i++) {
			this.addStackList(stackList, this.getInputItem(i));
		}

		for (int i = 0; i < this.getOutSize(); i++) {
			this.addStackList(stackList, this.getOutputItem(i));
		}

		stackList.addAll(this.craftList);

		return stackList;
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("bucketInv", this.bucketInv.serializeNBT());
		tag.put("iceInv", this.iceInv.serializeNBT());
		tag.put("handInv", this.handInv.serializeNBT());
		tag.put("inputInv", this.inputInv.serializeNBT());
		tag.put("outputInv", this.outputInv.serializeNBT());
		tag.putBoolean("isCraft", this.isCraft);
		tag.putInt("craftTime", this.craftTime);
		this.saveStackList(tag, this.craftList, "craftList");
		tag.put("outPutStack", this.outStack.save(new CompoundTag()));
		tag.putInt("waterValue", this.getWaterValue());
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.bucketInv.deserializeNBT(tag.getCompound("bucketInv"));
		this.iceInv.deserializeNBT(tag.getCompound("iceInv"));
		this.handInv.deserializeNBT(tag.getCompound("handInv"));
		this.inputInv.deserializeNBT(tag.getCompound("inputInv"));
		this.outputInv.deserializeNBT(tag.getCompound("outputInv"));
		this.isCraft = tag.getBoolean("isCraft");
		this.craftTime = tag.getInt("craftTime");
		this.craftList = this.loadAllStack(tag, "craftList");
		this.outStack = ItemStack.of(tag.getCompound("outPutStack"));
		this.setWaterValue(tag.getInt("waterValue"));
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize () {
		return 6;
	}

	// 出力スロットのスロット数
	public int getOutSize () {
		return 4;
	}

	// メインスロットの取得
	public IItemHandler getHand() {
		return this.handInv;
	}

	// メインスロットのアイテムを取得
	public  ItemStack getHandItem() {
		return this.getHand().getStackInSlot(0);
	}

	// 素材スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// 素材スロットのアイテムを取得
	public  ItemStack getInputItem(int i) {
		return this.getInput().getStackInSlot(i);
	}

	// 出力スロットの取得
	public IItemHandler getOutput() {
		return this.outputInv;
	}

	// 出力のアイテムを取得
	public  ItemStack getOutputItem(int i) {
		return this.getOutput().getStackInSlot(i);
	}

	// バケツスロットの取得
	public IItemHandler getBucket() {
		return this.bucketInv;
	}

	// バケツスロットのアイテムを取得
	public  ItemStack getBucketItem() {
		return this.getBucket().getStackInSlot(0);
	}

	// 氷スロットの取得
	public IItemHandler getIce() {
		return this.iceInv;
	}

	// 氷スロットのアイテムを取得
	public  ItemStack getIceItem(int i) {
		return this.getIce().getStackInSlot(i);
	}

	// 最大水量を設定
	public int getMaxWaterValue () {
		return MAX_WATER_VALUE;
	}

	// 水量を取得
	public int getWaterValue () {
		return this.waterValue;
	}

	// 水量を設定
	public void setWaterValue (int value) {
		this.waterValue = value;
	}

	// 水が最大かどうか
	public boolean isMaxWater() {
		return this.getWaterValue() >= this.getMaxWaterValue();
	}

	// 水が必要容量を超えているかどうか
	public boolean isNeedWater () {
		return this.getWaterValue() >= this.useWaterValue;
	}

	// 水を入れれる量があるか
	public boolean canInsertWater (int insertWaterValue) {
		return this.getWaterValue() + insertWaterValue <= this.getMaxWaterValue();
	}

	// MFゲージの描画量を計算するためのメソッド
	public int getCraftProgressScaled(int value) {
		return Math.min(value, (int) (value * (float) (this.craftTime) / (float) (MAX_CRAFT_TIME)));
    }

	// MFゲージの描画量を計算するためのメソッド
	public int getWaterProgressScaled(int value) {
		return Math.min(value, (int) (value * (float) (this.getWaterValue()) / (float) (this.getMaxWaterValue())));
    }

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new FreezerMenu(windowId, inv, this);
	}
}
