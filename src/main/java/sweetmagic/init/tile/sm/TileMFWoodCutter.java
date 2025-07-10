package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.TileInit;
import sweetmagic.init.tile.menu.MFWoodCutterMenu;
import sweetmagic.recipe.woodcutter.WoodCutterRecipe;
import sweetmagic.util.ItemHelper;

public class TileMFWoodCutter extends TileSMMagic {

	public int craftTime = 0;
	public int maxMagiaFlux = 10000;
	public static final int MAX_CRAFT_TIME = 20;
	public boolean isCraft = false;
	public List<ItemStack> outStackList = new ArrayList<>();
	public List<ItemStack> outStackOverList = new ArrayList<>();
	protected final StackHandler inputInv = new StackHandler(1);
	protected final StackHandler outInv = new StackHandler(this.getInvSize());

	public TileMFWoodCutter(BlockPos pos, BlockState state) {
		this(TileInit.woodCutter, pos, state);
	}

	public TileMFWoodCutter(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new InOutHandlerProvider(this.inputInv, this.outInv);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		if (this.tickTime % 10 != 0 || this.isRSPower()) { return; }

		if (!this.isCraft) {
			this.craftStart();
		}

		else if (this.craftTime++ >= MAX_CRAFT_TIME) {
			this.craftEnd();
		}

		this.sendInfo();
	}

	public void craftStart() {

		if(!this.outStackOverList.isEmpty()) {
			this.craftOverInsert();
			return;
		}

		ItemHelper.compactSimpleInventory(this.inputInv);
		ItemStack stack = this.getInputItem();
		if (stack.isEmpty() || !this.canSmelt(stack)) { return; }

		WoodCutterRecipe recipe = WoodCutterRecipe.getRecipe(this.getLevel(), Arrays.<ItemStack> asList(stack)).get();
		this.outStackList = this.getLog(recipe, stack);
		if (this.outStackList.isEmpty()) { return; }

		this.isCraft = true;
		this.setMF(this.getMF() - recipe.getMF());
		this.sendInfo();
	}

	public void craftEnd() {

		for(ItemStack outStack : this.outStackList) {
			ItemStack stack = ItemHelper.insertStack(this.getOut(), outStack.copy(), false);

			if (!stack.isEmpty()) {
				this.outStackOverList.add(stack);
			}
		}

		this.outStackList.clear();
		ItemStack stack = this.getInputItem();
		this.isCraft = !stack.isEmpty() && stack.is(ItemTags.SAPLINGS);
		this.craftTime = this.isCraft ? MAX_CRAFT_TIME - 10 : 0;
		this.isCraft = false;
		this.sendInfo();

		for(int i = 0; i < 4; i++)
			this.playSound(this.getBlockPos(), SoundEvents.WOOD_BREAK, 0.5F, 0.9F + this.getRandFloat(0.15F));
	}

	// 精錬可能か銅か
	public boolean canSmelt(ItemStack stack) {
		return !WoodCutterRecipe.getRecipe(this.getLevel(), Arrays.<ItemStack> asList(stack)).isEmpty();
	}

	public List<ItemStack> getLog(WoodCutterRecipe recipe, ItemStack stack) {
		if(this.getMF() < recipe.getMF()) { return new ArrayList<>(); }
		List<ItemStack> stackList = new ArrayList<>();
		List<ItemStack> outList = recipe.getResultList();

		for(int i = 0; i < outList.size(); i++) {
			stackList.add(new ItemStack(outList.get(i).getItem(), recipe.getCount(this.rand, i)));
		}

		return stackList;
	}

	public void craftOverInsert() {

		List<ItemStack> stackList = new ArrayList<>();
		this.outStackOverList.forEach(s -> this.addStackList(stackList, ItemHelper.insertStack(this.getOut(), s.copy(), false)));
		this.outStackOverList.clear();

		if (!stackList.isEmpty()) {
			this.outStackOverList = stackList;
		}
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize() {
		return 27;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF() {
		return this.maxMagiaFlux;
	}

	// 受信するMF量の取得
	@Override
	public int getReceiveMF() {
		return 5000;
	}

	// 必要MF
	public int getNeedMF() {
		return 50;
	}

	// 入力スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// 入力スロットのアイテムを取得
	public ItemStack getInputItem() {
		return this.getInput().getStackInSlot(0);
	}

	// 出力スロットの取得
	public IItemHandler getOut() {
		return this.outInv;
	}

	// 出力スロットのアイテムを取得
	public ItemStack getOutItem(int i) {
		return this.getOut().getStackInSlot(i);
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("inputInv", this.inputInv.serializeNBT());
		tag.put("outInv", this.outInv.serializeNBT());
		tag.putInt("craftTime", this.craftTime);
		tag.putBoolean("isCraft", this.isCraft);
		this.saveStackList(tag, this.outStackOverList, "outStackOverList");
		this.saveStackList(tag, this.outStackList, "outStackList");
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.inputInv.deserializeNBT(tag.getCompound("inputInv"));
		this.outInv.deserializeNBT(tag.getCompound("outInv"));
		this.craftTime = tag.getInt("craftTime");
		this.isCraft = tag.getBoolean("isCraft");
		this.outStackOverList = this.loadAllStack(tag, "outStackOverList");
		this.outStackList = this.loadAllStack(tag, "outStackList");
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new MFWoodCutterMenu(windowId, inv, this);
	}

	// RS信号で動作を停止するかどうか
	public boolean isRSStop() {
		return true;
	}

	// クラフト描画量を計算するためのメソッド
	public int getCraftProgress(int value) {
		return this.getProgress(value, this.craftTime, MAX_CRAFT_TIME);
	}

	// インベントリのアイテムを取得
	public List<ItemStack> getInvList() {
		List<ItemStack> stackList = new ArrayList<>();

		for (int i = 0; i < this.getInvSize(); i++) {
			this.addStackList(stackList, this.getOutItem(i));
		}

		this.addStackList(stackList, this.getInputItem());
		return stackList;
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.getInvList().isEmpty() && this.isMFEmpty();
	}
}
