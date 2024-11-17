package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import sweetmagic.init.SoundInit;
import sweetmagic.init.TileInit;
import sweetmagic.recipe.mill.MillRecipe;

public class TileMill extends TileAbstractSMCook {

	private static final int MAX_CRAFT_TIME = 4;
	public int craftTime = 0;
	public int amount = 0;
	public boolean isCraft = false;
	public List<Float> chanceList = new ArrayList<>();
	public List<ItemStack> craftList = new ArrayList<>();
	public List<ItemStack> resultList = new ArrayList<>();

	protected final StackHandler handInv = new StackHandler(1);
	protected final StackHandler outputInv = new StackHandler(this.getInvSize());

	public TileMill(BlockPos pos, BlockState state) {
		this(TileInit.mill, pos, state);
	}

	public TileMill(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new InOutHandlerProvider(this.handInv, this.outputInv);
	}

	// サーバー側処理
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		if (this.tickTime % 20 != 0) { return; }

		this.tickTime = 0;

		// 作成中で
		if (this.isCook()) {

			// 料理時間の経過
			this.addCookTime();

			// 一定時間が経てばクラフトの完成
			if (this.isFinishCook()) {
				this.craftFinish();
			}

			else {
				this.playSound(pos, SoundInit.MACHIN, 0.0625F, 1F);
			}

			this.sendPKT();
		}

		else if (this.isOutputEmpty()) {

			// 完成なら初期化
			if (this.getCookData() == 2) {
				this.setState(0);
				this.outPutClear();
			}
		}

		// メインスロットに何もなければ終了
		if (this.isCook() || this.getHandItem().isEmpty() || !this.isOutputEmpty()) { return; }

		// レシピが見つかれば作成開始
		if (this.checkRecipe()) {
			this.craftStart();
		}

		// レシピが見つからないがメインスロットにアイテムがあるなら吐き出す
		else {
			ItemStack hand = this.getHandItem();
			ItemHandlerHelper.insertItemStacked(this.getOutput(), hand.copy(), false);
			hand.shrink(hand.getCount());
			this.setState(2);
		}
	}

	// 素材の取得
	public List<ItemStack> getStackList () {
		return Arrays.<ItemStack> asList(this.getHandItem());
	}

	// ドロップリストを取得
	public List<ItemStack> getDropList () {

		List<ItemStack> stackList = new ArrayList<>();
		this.addStackList(stackList, this.getHandItem());

		for (int i = 0; i < this.getInvSize(); i++) {
			this.addStackList(stackList, this.getOutputItem(i));
		}

		if (!this.craftList.isEmpty()) {
			ItemStack stack = this.craftList.get(0);
			stack.setCount(amount);
			this.addStackList(stackList, stack);
		}

		return stackList;
	}

	// レシピチェック
	public boolean checkRecipe () {
		return !MillRecipe.getRecipe(this.level, this.getStackList()).isEmpty();
	}

	// 作成開始
	public void craftStart () {

		// レシピを取得
		List<ItemStack> stackList = this.getStackList();
		MillRecipe recipe = MillRecipe.getRecipe(this.level, stackList).get();

		// 要求アイテムリスト
		List<ItemStack> requestList = recipe.getRequestList();

		// メインアイテムを取得
		ItemStack hasndStack = stackList.get(0);
		int amount = hasndStack.getCount() / requestList.get(0).getCount();

		// クラフトで使うアイテムを入れておく
		this.craftList = new ArrayList<ItemStack>(requestList);

		// レシピから完成品を取得
		List<ItemStack> resultList = recipe.getResultList();
		resultList = new ArrayList<ItemStack>(resultList);

		// 要求アイテムの消費
		for (ItemStack request: recipe.getRequestList()) {
			if (request.is(hasndStack.getItem())) {
				hasndStack.shrink(request.getCount() * amount);
				break;
			}
		}

		this.amount = amount;
		this.isCraft = true;
		this.chanceList = new ArrayList<Float>(recipe.getChanceList());
		this.resultList = recipe.getResultList();
		this.tickTime = 0;

		this.setState(1);
		this.playSound(this.getBlockPos(), SoundInit.MACHIN, 0.0625F, 1F);
		this.sendPKT();
	}

	// クラフトの完成
	public void craftFinish () {

		// リザルト分回す
		for (int i = 0; i < this.resultList.size(); i++) {

			float chance = this.chanceList.isEmpty() ? 0F : this.chanceList.get(i);

			// クラフト個数分回す
			for (int k = 0; k < this.amount; k++) {

				// 最初の1つかチャンスより大きいなら完成品を入れる
				if (i == 0 || chance >= rand.nextFloat()) {
					ItemHandlerHelper.insertItemStacked(this.getOutput(), this.resultList.get(i).copy(), false);
				}
			}
		}

		// 初期化
		this.clearInfo();
		this.setState(2);
		this.playSound(this.getBlockPos(), SoundInit.DROP, 0.0625F, 1F);
		this.sendPKT();
	}

	// 初期化
	public void clearInfo () {
		this.amount = 0;
		this.craftTime = 0;
		this.isCraft = false;
		this.chanceList.clear();
		this.craftList.clear();
		this.resultList.clear();
		this.sendPKT();
	}

	// 出力スロットが空か
	public boolean isOutputEmpty () {
		for (int i = 0; i < this.getInvSize(); i++) {
			if (!this.getOutputItem(i).isEmpty()) { return false; }
		}
		return true;
	}

	// 出力アイテムリスト
	public List<ItemStack> getOutPutList () {
		List<ItemStack> stackList = new ArrayList<>();

		for (int i = 0; i < this.getInvSize(); i++) {

			ItemStack stack = this.getOutputItem(i);
			if (stack.isEmpty()) { continue; }

			stackList.add(stack);
		}

		return stackList;
	}

	// 出力アイテムの消費
	public void outPutClear () {
		List<ItemStack> stackList = this.getOutPutList();
		for (ItemStack stack : stackList) {
			stack.shrink(stack.getCount());
		}
	}

	// 最大料理時間の取得
	public int getMaxCookTime() {
		return MAX_CRAFT_TIME;
	}

	// 料理時間の取得
	public int getCookTime() {
		return this.craftTime;
	}

	// 料理時間の設定
	public void setCookTime(int cookTime) {
		this.craftTime = cookTime;
	}

	// 料理中か
	public boolean isCook() {
		return this.isCraft;
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("handInv", this.handInv.serializeNBT());
		tag.put("outputInv", this.outputInv.serializeNBT());
		tag.putBoolean("isCraft", this.isCraft);
		tag.putInt("craftTime", this.craftTime);
		tag.putInt("amount", this.amount);
		this.saveFloatList(tag, this.chanceList, "chanceList");
		this.saveStackList(tag, this.craftList, "craftList");
		this.saveStackList(tag, this.resultList, "resultList");
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.handInv.deserializeNBT(tag.getCompound("handInv"));
		this.outputInv.deserializeNBT(tag.getCompound("outputInv"));
		this.isCraft = tag.getBoolean("isCraft");
		this.craftTime = tag.getInt("craftTime");
		this.amount = tag.getInt("amount");
		this.chanceList = this.loadAllFloat(tag, "chanceList");
		this.craftList = this.loadAllStack(tag, "craftList");
		this.resultList = this.loadAllStack(tag, "resultList");
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize () {
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

	// 出力スロットの取得
	public IItemHandler getOutput() {
		return this.outputInv;
	}

	// 出力のアイテムを取得
	public  ItemStack getOutputItem(int i) {
		return this.getOutput().getStackInSlot(i);
	}
}
