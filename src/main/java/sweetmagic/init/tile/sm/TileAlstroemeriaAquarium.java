package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.magic.PedalCreate;
import sweetmagic.init.capability.ICapabilityResolver;
import sweetmagic.init.capability.SidItemHandler;
import sweetmagic.init.tile.menu.AlstroemeriaAquariumMenu;
import sweetmagic.init.tile.slot.WrappedItemHandler;
import sweetmagic.recipe.RecipeHelper;
import sweetmagic.recipe.RecipeUtil;
import sweetmagic.recipe.alstrameria.AlstroemeriaRecipe;

public class TileAlstroemeriaAquarium extends TileSMMagic {

	public int maxMagiaFlux = 50000;				// 最大MF量を設定
	public boolean isCraft = false;
	public List<ItemStack> outStackList = new ArrayList<>();

	protected final StackHandler handInv = new StackHandler(1);
	protected final StackHandler inputInv = new StackHandler(10);
	protected final StackHandler outputInv = new StackHandler(this.getInvSize());

	public TileAlstroemeriaAquarium(BlockPos pos, BlockState state) {
		this(TileInit.alstroemeriaAquarium, pos, state);
	}

	public TileAlstroemeriaAquarium(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new SMHandlerProvider();
	}

	// サーバー側処理
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		int checkTime = this.isCraft ? 5 : 20;
		if (this.tickTime % checkTime != 0) { return; }

		// 作成中で
		if (this.isCraft) {
			this.craftFinish();
			this.sendPKT();
		}

		// メインスロットに何もなければ終了
		if (this.isCraft || this.getHandItem().isEmpty() || this.getMF() < this.getUseMF()) { return; }

		// レシピが見つかれば作成開始
		if (this.checkRecipe()) {
			this.craftStart();
		}
	}

	// 素材の取得
	public List<ItemStack> getStackList () {

		List<ItemStack> stackList = new ArrayList<>();
		stackList.add(this.getHandItem());

		for (int i = 0; i < 10; i++) {

			ItemStack stack = this.getInputItem(i);
			if (stack.isEmpty()) { continue; }

			stackList.add(stack);
		}

		return stackList;
	}

	// レシピチェック
	public boolean checkRecipe () {
		List<ItemStack> stackList = this.getStackList();
		return !AlstroemeriaRecipe.getRecipe(this.level, stackList).isEmpty();
	}

	// 作成開始
	public void craftStart () {

		// レシピを取得して見つからなければ終了
		List<ItemStack> stackList = this.getStackList();
		AlstroemeriaRecipe recipe = AlstroemeriaRecipe.getRecipe(this.level, stackList).get();
		RecipeUtil recipeUtil = RecipeHelper.recipeSingleCraft(stackList, recipe);

		// レシピから完成品を取得
		List<ItemStack> resultList = new ArrayList<>(recipeUtil.getResultList());
		ItemStack inputStack = recipeUtil.getInputList().get(0);

		if (inputStack.getItem() instanceof BlockItem bItem && bItem.getBlock() instanceof PedalCreate) {
			CompoundTag tags = inputStack.getTag();
			resultList.get(0).setTag(tags);
		}

		for (ItemStack result : resultList) {
			if (!ItemHandlerHelper.insertItemStacked(this.getOutput(), result, true).isEmpty()) { return; }
		}

		// リザルトアイテムを保存
		this.outStackList = resultList;
		this.playSound(this.getBlockPos(), SoundEvents.FIREWORK_ROCKET_BLAST_FAR, 0.5F, 1F / (this.level.random.nextFloat() * 0.4F + 1.2F) + 1F * 0.5F);
		this.isCraft = true;
		this.setMF(this.getMF() - this.getUseMF());
	}

	// クラフトの完成
	public void craftFinish () {

		// 完成品を入れる
		for (ItemStack result : this.outStackList) {
			ItemHandlerHelper.insertItemStacked(this.getOutput(), result.copy(), false);
		}

		// 初期化
		this.clearInfo();
	}

	// 初期化
	public void clearInfo () {
		this.isCraft = false;
		this.outStackList.clear();
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("handInv", this.handInv.serializeNBT());
		tag.put("inputInv", this.inputInv.serializeNBT());
		tag.put("outputInv", this.outputInv.serializeNBT());
		tag.putBoolean("isCraft", this.isCraft);
		this.saveStackList(tag, this.outStackList, "outStackList");
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.handInv.deserializeNBT(tag.getCompound("handInv"));
		this.inputInv.deserializeNBT(tag.getCompound("inputInv"));
		this.outputInv.deserializeNBT(tag.getCompound("outputInv"));
		this.isCraft = tag.getBoolean("isCraft");
		this.outStackList = this.loadAllStack(tag, "outStackList");
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
		return 10;
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize () {
		return 27;
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

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new AlstroemeriaAquariumMenu(windowId, inv, this);
	}

	// RS信号で動作を停止するかどうか
	public boolean isRSStop () {
		return true;
	}

	// インベントリのアイテムを取得
	public List<ItemStack> getInvList () {
		List<ItemStack> stackList = new ArrayList<>();
		this.addStackList(stackList, this.getHandItem());

		for (int i = 0; i < 10; i++) {
			this.addStackList(stackList, this.getInputItem(i));
		}

		for (int i = 0; i < this.getInvSize(); i++) {
			this.addStackList(stackList, this.getOutputItem(i));
		}

		return stackList;
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.getInvList().isEmpty() && this.isMFEmpty();
	}

	private class SMHandlerProvider extends SidItemHandler {

		private final ICapabilityResolver<IItemHandler> inRes;
		private final ICapabilityResolver<IItemHandler> handRes;
		private final ICapabilityResolver<IItemHandler> outRes;

		protected SMHandlerProvider() {
			this.inRes = this.getBasicResolver(this.getHandler(inputInv, WrappedItemHandler.WriteMode.IN));
			this.handRes = this.getBasicResolver(this.getHandler(handInv, WrappedItemHandler.WriteMode.IN));
			this.outRes = this.getBasicResolver(this.getHandler(outputInv, WrappedItemHandler.WriteMode.OUT));
		}

		@Override
		protected ICapabilityResolver<IItemHandler> getResolver(@Nullable Direction face) {

			if (face == null) {
				return this.inRes;
			}

			else if (face == Direction.UP) {
				return this.handRes;
			}

			else if (face == Direction.DOWN) {
				return this.outRes;
			}

			return this.inRes;
		}

		@Override
		public void invalidateAll() {
			this.inRes.invalidateAll();
			this.handRes.invalidateAll();
			this.outRes.invalidateAll();
		}
	}
}
