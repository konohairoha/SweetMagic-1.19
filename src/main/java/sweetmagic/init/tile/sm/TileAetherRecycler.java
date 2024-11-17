package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import sweetmagic.init.SoundInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.tile.menu.AetherRecyclerMenu;
import sweetmagic.recipe.recycler.RecyclerRecipe;

public class TileAetherRecycler extends TileSMMagic {

	public int maxMagiaFlux = 20000;				// 最大MF量を設定
	public boolean isCraft = false;
	private static final int MAX_CRAFT_TIME = 10;
	public int craftTime = 0;
	public int amount = 0;

	public List<Float> chanceList = new ArrayList<>();
	public List<ItemStack> inputStackList = new ArrayList<>();			// 投入アイテム
	public List<List<ItemStack>> outStackListList = new ArrayList<>();	// 出力アイテム
	public List<ItemStack> outStackOverList = new ArrayList<>();		// 投入できなかったアイテム

	protected final StackHandler handInv = new StackHandler(24);
	protected final StackHandler inputInv = new StackHandler(1);
	protected final StackHandler outputInv = new StackHandler(this.getInvSize());

	public TileAetherRecycler(BlockPos pos, BlockState state) {
		this(TileInit.aetherRecycler, pos, state);
	}

	public TileAetherRecycler(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new InOutHandlerProvider(this.handInv, this.outputInv);
	}

	// サーバー側処理
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		if (this.tickTime % 10 != 0 || this.isRSPower()) { return; }

		if (!this.outStackOverList.isEmpty()) {
			this.craftOverInsert();
		}

		else if (!this.isCraft) {
			this.craftStart();
		}

		else {

			if (this.tickTime % 20 == 0 && this.craftTime <= 8) {
				this.playSound(this.getBlockPos(), SoundInit.RECYCLER_ON, 0.375F, 1F);
			}

			if (this.craftTime++ >= MAX_CRAFT_TIME) {
				this.craftFinish();
			}

			this.sendPKT();
		}
	}

	// クライアント側処理
	public void clientTick(Level world, BlockPos pos, BlockState state) {
		super.clientTick(world, pos, state);
		if (!this.isCraft || this.inputStackList.isEmpty() || this.isRSPower()) { return; }

		if (this.craftTime <= 8) {
			ParticleOptions par = new ItemParticleOption(ParticleTypes.ITEM, this.inputStackList.get(0));
			this.spawnParticleRing(world, par, 1.01D, pos, -0.5D, 0D, 0.01F);
		}

		else if (this.tickTime % 20 == 10) {
			this.spawnParticleRing(world, ParticleTypes.CLOUD, 0.75D, pos, -0.5D, 0D, 0.33F);
		}
	}

	// 作成開始
	public void craftStart () {

		if (this.tickTime % 20 == 0) {
			this.insertInput();
			return;
		}

		// アイテムが空または
		if (this.getInputItem().isEmpty() || !this.checkRecipe()) { return; }

		// レシピを取得
		List<ItemStack> stackList = this.getStackList();
		RecyclerRecipe recipe = RecyclerRecipe.getRecipe(this.level, stackList).get();

		// 要求アイテムリスト
		List<ItemStack> requestList = recipe.getRequestList();

		// メインアイテムを取得
		ItemStack hasndStack = stackList.get(0);
		int amount = hasndStack.getCount() / requestList.get(0).getCount();
		amount = Math.min(amount, this.getMF() / ( Math.max(1, (amount - 1) / 16 + 1) * this.getUseMF() ) );
		if (amount <= 0) { return; }

		// クラフトで使うアイテムを入れておく
		this.inputStackList = new ArrayList<ItemStack>(requestList);

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
		this.tickTime = 0;
		this.setMF(this.getMF() - Math.max(1, (amount - 1) / 16 + 1) * this.getUseMF());

		List<Ingredient> ingList = recipe.getResultIngList();
		this.outStackListList = new ArrayList<>();

		for (Ingredient ing : ingList) {
			ItemStack[] stackArray = ing.getItems();
			List<ItemStack> outStackList = new ArrayList<>();

			for (int i = 0; i < amount; i++) {
				outStackList.add(stackArray[stackArray.length > 1 ? this.rand.nextInt(stackArray.length) : 0]);
			}

			this.outStackListList.add(outStackList);
		}

		this.playSound(this.getBlockPos(), SoundInit.RECYCLER_ON, 0.375F, 1F);
		this.sendPKT();
	}

	// クラフトの完成
	public void craftFinish () {

		// リザルト分回す
		for (int i = 0; i < this.outStackListList.size(); i++) {

			float chance = this.chanceList.isEmpty() ? 0F : this.chanceList.get(i);
			List<ItemStack> ouStackList = this.outStackListList.get(i);

			// クラフト個数分回す
			for (int k = 0; k < this.amount; k++) {

				// 最初の1つかチャンスより大きいなら完成品を入れる
				if (chance < this.rand.nextFloat()) { continue; }

				ItemStack stack = ItemHandlerHelper.insertItemStacked(this.getOutput(), ouStackList.get(k).copy(), false);
				if (!stack.isEmpty()) {
					this.outStackOverList.add(stack);
				}
			}
		}

		// 初期化
		this.clearInfo();
		this.playSound(this.getBlockPos(), SoundInit.RECYCLER_FIN, 0.15F, 1F);
		this.sendPKT();
	}

	public void craftOverInsert () {

		List<ItemStack> stackList = new ArrayList<>();

		for (ItemStack stack : this.outStackOverList) {

			ItemStack result = ItemHandlerHelper.insertItemStacked(this.getOutput(), stack.copy(), false);
			if (!result.isEmpty()) {
				stackList.add(result);
			}
		}

		this.outStackOverList.clear();

		if (!stackList.isEmpty()) {
			this.outStackOverList = stackList;
		}
	}

	public void insertInput () {
		for (int i = 0; i < 24; i++) {
			ItemStack stack = this.getHandItem(i);
			if (stack.isEmpty()) { continue; }

			ItemStack out = ItemHandlerHelper.insertItemStacked(this.getInput(), stack.copy(), false);
			stack.shrink(stack.getCount() - out.getCount());
		}
	}

	// 初期化
	public void clearInfo () {
		this.amount = 0;
		this.craftTime = 0;
		this.isCraft = false;
		this.chanceList.clear();
		this.inputStackList.clear();
		this.outStackListList.clear();
		this.sendPKT();
	}

	// レシピチェック
	public boolean checkRecipe () {
		return !RecyclerRecipe.getRecipe(this.level, this.getStackList()).isEmpty();
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("handInv", this.handInv.serializeNBT());
		tag.put("inputInv", this.inputInv.serializeNBT());
		tag.put("outputInv", this.outputInv.serializeNBT());
		tag.putBoolean("isCraft", this.isCraft);
		tag.putInt("tickTime", this.tickTime);
		tag.putInt("craftTime", this.craftTime);
		tag.putInt("amount", this.amount);
		this.saveFloatList(tag, this.chanceList, "chanceList");
		this.saveStackList(tag, this.inputStackList, "inputStackList");
		this.saveStackListList(tag, this.outStackListList, "outStackList");
		this.saveStackList(tag, this.outStackOverList, "outStackOverList");
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.handInv.deserializeNBT(tag.getCompound("handInv"));
		this.inputInv.deserializeNBT(tag.getCompound("inputInv"));
		this.outputInv.deserializeNBT(tag.getCompound("outputInv"));
		this.isCraft = tag.getBoolean("isCraft");
		this.tickTime = tag.getInt("tickTime");
		this.craftTime = tag.getInt("craftTime");
		this.amount = tag.getInt("amount");
		this.chanceList = this.loadAllFloat(tag, "chanceList");
		this.inputStackList = this.loadAllStack(tag, "inputStackList");
		this.outStackListList = this.loadAllStackList(tag, "outStackList");
		this.outStackOverList = this.loadAllStack(tag, "outStackOverList");
	}

	// クラフトゲージの描画量を計算するためのメソッド
	public int getCraftProgressScaled(int value) {
		return Math.min(value, (int) (value * (float) (this.craftTime) / (float) (MAX_CRAFT_TIME)));
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

	// 消費MF
	public int getUseMF () {
		return 50;
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize () {
		return 27;
	}

	// 素材の取得
	public List<ItemStack> getStackList () {
		return Arrays.<ItemStack> asList(this.getInputItem());
	}

	// メインスロットの取得
	public IItemHandler getHand() {
		return this.handInv;
	}

	// メインスロットのアイテムを取得
	public  ItemStack getHandItem(int i) {
		return this.getHand().getStackInSlot(i);
	}

	// 素材スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// 素材スロットのアイテムを取得
	public  ItemStack getInputItem() {
		return this.getInput().getStackInSlot(0);
	}

	// 出力スロットの取得
	public IItemHandler getOutput() {
		return this.outputInv;
	}

	// 出力のアイテムを取得
	public  ItemStack getOutputItem(int i) {
		return this.getOutput().getStackInSlot(i);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new AetherRecyclerMenu(windowId, inv, this);
	}

	// RS信号で動作を停止するかどうか
	public boolean isRSStop () {
		return true;
	}

	// インベントリのアイテムを取得
	public List<ItemStack> getInvList () {
		List<ItemStack> stackList = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			this.addStackList(stackList, this.getHandItem(i));
		}

		this.addStackList(stackList, this.getInputItem());

		for (int i = 0; i < this.getInvSize(); i++) {
			this.addStackList(stackList, this.getOutputItem(i));
		}

		return stackList;
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.getInvList().isEmpty() && this.isMFEmpty();
	}
}
