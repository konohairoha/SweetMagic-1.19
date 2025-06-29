package sweetmagic.init.tile.sm;

import java.util.ArrayList;
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
import sweetmagic.init.SoundInit;
import sweetmagic.init.TagInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.tile.menu.ObMagiaMenu;
import sweetmagic.recipe.obmagia.ObMagiaRecipe;
import sweetmagic.util.ItemHelper;

public class TileObMagia extends TileAbstractSM {

	public int maxCraftTime = 10;
	public int craftTime = 0;
	public boolean isCraft = false;
	public boolean canCraft = false;
	public ItemStack outStack = ItemStack.EMPTY;
	public ItemStack viewStack = ItemStack.EMPTY;
	public List<ItemStack> craftList = new ArrayList<>();
	protected final StackHandler baseInv = new StackHandler(1);
	protected final StackHandler pageInv = new StackHandler(1);
	protected final StackHandler handInv = new StackHandler(1);
	protected final StackHandler inputInv = new StackHandler(this.getInvSize());
	protected final StackHandler outputInv = new StackHandler(1);
	protected final StackHandler sideInv = new StackHandler(9);

	public TileObMagia(BlockPos pos, BlockState state) {
		this(TileInit.obmagia, pos, state);
	}

	public TileObMagia(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new InOutHandlerProvider(this.inputInv, this.outputInv);
	}

	// サーバー側処理
	public void serverTick(Level level, BlockPos pos, BlockState state) {
		super.serverTick(level, pos, state);
		if (this.tickTime % 10 != 0) { return; }

		if (this.tickTime >= 30) {
			this.tickTime = 0;

			if (this.isCraft && (this.maxCraftTime - 2) * 30 >= this.craftTime) {
				this.playSound(pos, SoundInit.TURN_PAGE, 0.1F, 1F);
			}
		}

		// 作成中で
		if (this.isCraft) {

			// 一定時間が経てばクラフトの完成
			if (this.craftTime++ >= this.maxCraftTime) {
				this.craftFinish();
			}

			this.sendPKT();
			return;
		}

		// メインスロットに何もなければ終了
		if (this.isCraft || this.getHandItem().isEmpty()) {
			this.canCraft = false;
			this.sendPKT();
			return;
		}

		this.canCraft = this.checkRecipe();
		this.sendPKT();
	}

	// 素材の取得
	public List<ItemStack> getStackList() {

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
	public boolean checkRecipe() {

		// 素材の取得
		List<ItemStack> stackList = this.getStackList();
		ItemStack page = this.getPageItem();
		ItemStack base = this.getBaseItem();

		// レシピを取得して見つからなければ終了
		boolean isCraft = !ObMagiaRecipe.getRecipe(this.getLevel(), stackList, page, base).isEmpty();
		if (isCraft) {
			ObMagiaRecipe recipe = ObMagiaRecipe.getRecipe(this.getLevel(), stackList, page, base).get();
			this.viewStack = recipe.getResultItem().copy();
		}

		return isCraft;
	}

	// 作成開始
	public void craftStart() {

		// 素材の取得
		List<ItemStack> stackList = this.getStackList();
		ItemStack page = this.getPageItem();
		ItemStack base = this.getBaseItem();

		// レシピを取得して見つからなければ終了
		ObMagiaRecipe recipe = ObMagiaRecipe.getRecipe(this.getLevel(), stackList, page, base).get();

		// レシピから完成品を取得
		ItemStack resultStack = recipe.getResultItem().copy();
		if (!ItemHelper.insertStack(this.getOutput(), resultStack, true).isEmpty()) { return; }

		// クラフトで使うアイテムを入れておく
		this.craftList = new ArrayList<ItemStack>(recipe.getRequestList());
		this.outStack = resultStack;

		// 要求アイテムの消費
		for (ItemStack request: recipe.getRequestList()) {
			for (ItemStack stack : stackList) {
				if (request.is(stack.getItem())) {
					stack.shrink(request.getCount());
					break;
				}
			}
		}

		page.shrink(recipe.getPage().getCount());

		if (!recipe.getBase().is(TagInit.MAGIC_BOOK)) {
			base.shrink(recipe.getBase().getCount());
		}

		this.maxCraftTime = recipe.getCraftTime();
		this.isCraft = true;
		this.canCraft = false;
		this.sendPKT();
		this.clickButton();
	}

	// クラフトの完成
	public void craftFinish() {
		ItemHelper.insertStack(this.getOutput(), this.outStack, false);
		this.playSound(this.getBlockPos(), SoundInit.WRITE, 0.1F, 1F);
		this.clearInfo();
	}

	// 初期化
	public void clearInfo() {
		this.craftTime = 0;
		this.maxCraftTime = 10;
		this.isCraft = false;
		this.outStack = ItemStack.EMPTY;
		this.viewStack = ItemStack.EMPTY;
		this.craftList.clear();
		this.sendPKT();
	}

	// ドロップリストを取得
	public List<ItemStack> getDropList() {

		List<ItemStack> stackList = new ArrayList<>();
		this.addStackList(stackList, this.getHandItem());

		for (int i = 0; i < this.getInvSize(); i++) {
			this.addStackList(stackList, this.getInputItem(i));
		}

		this.addStackList(stackList, this.getOutputItem());
		stackList.addAll(this.craftList);

		return stackList;
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("baseInv", this.baseInv.serializeNBT());
		tag.put("pageInv", this.pageInv.serializeNBT());
		tag.put("handInv", this.handInv.serializeNBT());
		tag.put("inputInv", this.inputInv.serializeNBT());
		tag.put("outputInv", this.outputInv.serializeNBT());
		tag.put("sideInv", this.sideInv.serializeNBT());
		tag.putBoolean("isCraft", this.isCraft);
		tag.putBoolean("canCraft", this.canCraft);
		tag.putInt("craftTime", this.craftTime);
		tag.putInt("maxCraftTime", this.maxCraftTime);
		this.saveStackList(tag, this.craftList, "craftList");
		tag.put("outPutStack", this.outStack.save(new CompoundTag()));
		tag.put("viewStack", this.viewStack.save(new CompoundTag()));
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.setInv(this.baseInv, tag, "baseInv");
		this.setInv(this.pageInv, tag, "pageInv");
		this.setInv(this.handInv, tag, "handInv");
		this.setInv(this.inputInv, tag, "inputInv");
		this.setInv(this.outputInv, tag, "outputInv");
		this.setInv(this.sideInv, tag, "sideInv");
		this.isCraft = tag.getBoolean("isCraft");
		this.canCraft = tag.getBoolean("canCraft");
		this.craftTime = tag.getInt("craftTime");
		this.maxCraftTime = tag.getInt("maxCraftTime");
		this.craftList = this.loadAllStack(tag, "craftList");
		this.outStack = ItemStack.of(tag.getCompound("outPutStack"));
		this.viewStack = ItemStack.of(tag.getCompound("viewStack"));
	}

	public void setInv(StackHandler inv, CompoundTag tags, String name) {
		CompoundTag tag = tags.getCompound(name);
		if (tag == null) { return; }
		inv.deserializeNBT(tag);
	}

	public boolean isInfoEmpty() {

		List<ItemStack> stackList = new ArrayList<>();
		this.addStackList(stackList, this.outStack);
		this.addStackList(stackList, this.getOutputItem());
		this.addStackList(stackList, this.getPageItem());
		this.addStackList(stackList, this.getBaseItem());
		this.addStackList(stackList, this.getHandItem());

		for (int i = 0; i < this.getInvSize(); i++) {
			this.addStackList(stackList, this.getInputItem(i));
		}

		return stackList.isEmpty();
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize() {
		return 8;
	}

	// ベーススロットの取得
	public IItemHandler getBase() {
		return this.baseInv;
	}

	// ベーススロットのアイテムを取得
	public ItemStack getBaseItem() {
		return this.getBase().getStackInSlot(0);
	}

	// ページスロットの取得
	public IItemHandler getpage() {
		return this.pageInv;
	}

	// ページスロットのアイテムを取得
	public ItemStack getPageItem() {
		return this.getpage().getStackInSlot(0);
	}

	// メインスロットの取得
	public IItemHandler getHand() {
		return this.handInv;
	}

	// メインスロットのアイテムを取得
	public ItemStack getHandItem() {
		return this.getHand().getStackInSlot(0);
	}

	// 素材スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// 素材スロットのアイテムを取得
	public ItemStack getInputItem(int i) {
		return this.getInput().getStackInSlot(i);
	}

	// 出力スロットの取得
	public IItemHandler getOutput() {
		return this.outputInv;
	}

	// 出力のアイテムを取得
	public ItemStack getOutputItem() {
		return this.getOutput().getStackInSlot(0);
	}

	// サイドスロットの取得
	public IItemHandler getSide() {
		return this.sideInv;
	}

	// 描画量を計算するためのメソッド
	public int getProgress(int value) {
		return Math.min(value, (int) (value * (float) (this.craftTime) / (float) (this.maxCraftTime)));
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new ObMagiaMenu(windowId, inv, this);
	}
}
