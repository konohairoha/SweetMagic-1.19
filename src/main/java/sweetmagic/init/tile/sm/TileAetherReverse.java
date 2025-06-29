package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.TagInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.tile.menu.AetherReverseMenu;
import sweetmagic.util.ItemHelper;

public class TileAetherReverse extends TileSMMagic {

	private int maxCraftTime = 9;
	public boolean isCraft = false;
	public boolean canCraft = false;
	public int craftTime = 0;
	public int shrinkValue = 0;
	public int maxMagiaFlux = 100000;
	public ItemStack stack = ItemStack.EMPTY;
	public List<ItemStack> craftList = new ArrayList<>();
	protected final StackHandler inputInv = new StackHandler(1, true);
	protected final StackHandler outputInv = new StackHandler(9, true);
	protected final StackHandler chestInv = new StackHandler(27);

	public TileAetherReverse(BlockPos pos, BlockState state) {
		this(TileInit.aetherReverse, pos, state);
	}

	public TileAetherReverse(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new InOutHandlerProvider(this.inputInv, this.chestInv);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(level, pos, state);
		if (this.tickTime % 10 != 0 ) { return; }

		if (this.isCraft) {

			// 一定時間が経てばクラフトの完成
			if (this.craftTime++ >= this.maxCraftTime) {
				this.craftFinish();
			}
		}

		else {

			// リバースクラフトで取得できるアイテムの取得
			List<ItemStack> stackList = this.getReverseStack(world);

			if (stackList.isEmpty()) {
				this.craftList.clear();
				this.canCraft = false;
			}

			else {
				this.craftList = new ArrayList<ItemStack>(stackList);
				this.canCraft = true;

				if (this.isRSPower()) {
					this.craftStart();
				}
			}
		}

		this.sendPKT();
	}

	// クライアント側処理
	public void clientTick(Level world, BlockPos pos, BlockState state) {
		super.clientTick(world, pos, state);
		if (this.tickTime % 30 != 0) { return; }

		this.tickTime = 0;
		if (this.getInputItem().isEmpty()) { return; }

		for (int i = 0; i < 8; i++) {
			this.spawnParticleCycle(world, ParticleInit.CYCLE_ORB, pos.getX() + 0.5D, pos.getY() + 1.35D, pos.getZ() + 0.5D, Direction.UP, 0.25D, (i * 45), false);
			this.spawnParticleCycle(world, ParticleInit.CYCLE_ORB, pos.getX() + 0.5D, pos.getY() + 1.1D, pos.getZ() + 0.5D, Direction.UP, 0.4D, (i * 45), true);
		}
	}

	// 作成開始
	public void craftStart() {
		int needMF = this.getReverseCost();
		if (needMF > this.getMF()) { return; }

		for (ItemStack stack : this.craftList) {
			ItemStack input = ItemHelper.insertStack(this.getChest(), stack, true);
			if (!input.isEmpty()) { return; }
		}

		this.stack = this.getInputItem().copy();
		this.maxCraftTime = this.craftList.size();
		this.isCraft = true;
		this.canCraft = false;
		this.getInputItem().shrink(this.shrinkValue);
		this.setMF(this.getMF() - needMF);

		this.sendPKT();
		this.clickButton();
	}

	// クラフトの完成
	public void craftFinish() {
		this.craftList.forEach(s -> ItemHelper.insertStack(this.getChest(), s.copy(), false));
		this.playSound(this.getBlockPos(), SoundEvents.ANVIL_USE, 0.1F, 1.125F);
		this.clearInfo();
	}

	// 初期化
	public void clearInfo() {
		this.craftTime = 0;
		this.maxCraftTime = 9;
		this.shrinkValue = 0;
		this.isCraft = false;
		this.craftList.clear();
		this.stack = ItemStack.EMPTY;
		this.sendPKT();
	}

	public List<ItemStack> getReverseStack(Level world) {

		List<ItemStack> stackList = new ArrayList<>();
		ItemStack stack = this.getInputItem();
		if (stack.isEmpty()) { return stackList; }

		// 全レシピの取得
		Recipe<?> recipe = null;
		List<Recipe<?>> allRecipeList = world.getRecipeManager().getRecipes().stream().filter(r -> this.checkRecipe(r)).toList();

		for (Recipe<?> allRecipe : allRecipeList) {

			// リザルトが一致しないなら次へ
			ItemStack result = allRecipe.getResultItem();
			if ( !result.is(stack.getItem()) || stack.getCount() < result.getCount()) { continue; }

			recipe = allRecipe;
			this.shrinkValue = result.getCount();
			break;
		}

		if (recipe == null) { return stackList; }

		// 要求アイテムリストの取得
		List<ItemStack> ingredList = this.getIngredientList(recipe);

		if (recipe instanceof IShapedRecipe<?> rec) {

			int recipeWidth = rec.getRecipeWidth();
			int recipeHeight = rec.getRecipeHeight();

			for (int invY = 0; invY < recipeHeight; invY++) {
				for (int invX = 0; invX < recipeWidth; invX++) {

					int index = invX + invY * recipeWidth;
					if (index >= ingredList.size()) continue;

					ItemStack ingred = ingredList.get(index).copy();
					if (ingred.is(TagInit.RECIPE_BOOK) || ingred.getDisplayName().toString().contains("bucket")) { continue; }

					stackList.add(this.getSingleStack(ingred));
				}
			}
		}

		else {
			for (int i = 0; i < 9; i++) {
				if (i < ingredList.size()) {

					ItemStack ingred = ingredList.get(i).copy();
					if (ingred.isEmpty() || ingred.is(TagInit.RECIPE_BOOK) || ingred.getDisplayName().toString().contains("bucket")) { continue; }

					stackList.add(this.getSingleStack(ingred));
				}
			}
		}

		return stackList;
	}

	// レシピの検索条件取得
	public boolean checkRecipe(Recipe<?> recipe) {
		return (recipe instanceof CraftingRecipe || recipe instanceof ShapedRecipe) && !recipe.isIncomplete() && recipe.canCraftInDimensions(3, 3) && !recipe.getIngredients().isEmpty();
	}

	// レシピから要求アイテムリストの取得
	private List<ItemStack> getIngredientList(Recipe<?> recipe) {

		List<ItemStack> stackList = new ArrayList<>();

		for (int i = 0; i < recipe.getIngredients().size(); i++) {
			List<ItemStack> recipeStackList = Arrays.stream(recipe.getIngredients().get(i).getItems()).toList();
			stackList.add(recipeStackList.size() > 0 ? recipeStackList.get(Math.floorMod(0, recipeStackList.size())) : ItemStack.EMPTY);
		}

		return stackList;
	}

	// アイテムのスタックリストを設定
	private ItemStack getSingleStack(ItemStack stack) {
		if (stack.getCount() > 1) {
			stack.setCount(1);
		}
		return stack;
	}

	// リバースクラフトのコスト取得
	public int getReverseCost() {

		int mf = 500;
		float rate = 1F;
		ItemStack stack = this.getInputItem();

		if (stack.getMaxDamage() > 0) {
			rate += stack.getMaxDamage() * 0.01F;
		}

		int needMF = (int) (this.craftList.size() * rate * mf);
		return needMF;
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("inputInv", this.inputInv.serializeNBT());
		tag.put("outputInv", this.outputInv.serializeNBT());
		tag.put("chestInv", this.chestInv.serializeNBT());
		tag.putBoolean("isCraft", this.isCraft);
		tag.putBoolean("canCraft", this.canCraft);
		tag.putInt("craftTime", this.craftTime);
		tag.putInt("maxCraftTime", this.maxCraftTime);
		tag.putInt("shrinkValue", this.shrinkValue);
		tag.put("stack", this.stack.save(new CompoundTag()));
		this.saveStackList(tag, this.craftList, "craftList");
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.inputInv.deserializeNBT(tag.getCompound("inputInv"));
		this.outputInv.deserializeNBT(tag.getCompound("outputInv"));
		this.chestInv.deserializeNBT(tag.getCompound("chestInv"));
		this.isCraft = tag.getBoolean("isCraft");
		this.canCraft = tag.getBoolean("canCraft");
		this.craftTime = tag.getInt("craftTime");
		this.maxCraftTime = tag.getInt("maxCraftTime");
		this.shrinkValue = tag.getInt("shrinkValue");
		this.stack = ItemStack.of(tag.getCompound("stack"));
		this.craftList = this.loadAllStack(tag, "craftList");
	}

	// 受信するMF量の取得
	@Override
	public int getReceiveMF() {
		return 10000;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF() {
		return this.maxMagiaFlux;
	}

	// 素材スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// 素材スロットのアイテムを取得
	public ItemStack getInputItem() {
		return this.getInput().getStackInSlot(0);
	}

	// 素材スロットの取得
	public IItemHandler getOut() {
		return this.outputInv;
	}

	// 素材スロットのアイテムを取得
	public ItemStack getOutItem(int i) {
		return this.getOut().getStackInSlot(i);
	}

	// 素材スロットの取得
	public IItemHandler getChest() {
		return this.chestInv;
	}

	// 素材スロットのアイテムを取得
	public ItemStack getChestItem(int i) {
		return this.getChest().getStackInSlot(i);
	}

	// クラフト描画量を計算するためのメソッド
	public int getCraftProgress(int value) {
		return Math.min(value, (int) (value * (float) (this.craftTime) / (float) (this.maxCraftTime)));
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new AetherReverseMenu(windowId, inv, this);
	}

	// インベントリのアイテムを取得
	public List<ItemStack> getInvList() {
		List<ItemStack> stackList = new ArrayList<>();
		this.addStackList(stackList, this.getInputItem());

		for (int i = 0; i < 9; i++) {
			this.addStackList(stackList, this.getOutItem(i));
		}

		for (int i = 0; i < 27; i++) {
			this.addStackList(stackList, this.getChestItem(i));
		}

		return stackList;
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.getInvList().isEmpty() && this.isMFEmpty();
	}
}
