package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.magic.MFFurnace;
import sweetmagic.init.tile.menu.MFFurnaceMenu;
import sweetmagic.util.ItemHelper;

public class TileMFFurnace extends TileSMMagic {

	public float exp = 0F;
	public int craftTime = 0;
	public int maxCraftTime = 0;
	protected final int costMF = 5;
	protected final int maxMagiaFlux = 20000;
	public boolean isCraft = false;
	protected ItemStack stack = ItemStack.EMPTY;
	protected final RecipeWrapper dummyFurnace = new RecipeWrapper(new ItemStackHandler());
	protected final StackHandler inputInv = new StackHandler(this.getInvSize() + 1);
	protected final StackHandler outInv = new StackHandler(this.getInvSize());

	public TileMFFurnace(BlockPos pos, BlockState state) {
		this(TileInit.mffurnace, pos, state);
	}

	public TileMFFurnace(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new InOutHandlerProvider(this.inputInv, this.outInv);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		if ((!this.isCraft && this.tickTime % 10 != 0) || this.isRSPower()) { return; }

		if (this.isCraft) {
			this.craftTime++;
			this.sendPKT();

			if (this.craftTime >= this.maxCraftTime) {
				this.craftFinish(world, pos);
			}
		}

		else {

			// 精錬できるか
			if (this.canSmelt(world, pos)) {
				this.craftStart(world, pos);
			}
		}
	}

	// 精錬できるか
	public boolean canSmelt(Level world, BlockPos pos) {
		ItemHelper.compactSimpleInventory(this.inputInv);	// インベントリの整理
		this.suctionItem(world, pos);	// 上のチェストからアイテム吸い込み
		return this.canSmelt(world, this.inputInv.getStackInSlot(0));	// 精錬するアイテムが無かったら終了
	}

	// 精錬できるか
	public boolean canSmelt(Level world, ItemStack stack) {
		if (stack.isEmpty()) { return false; }

		// 精錬結果がないなら終了
		ItemStack result = this.checkSmeltResult(world, stack);
		if (result.isEmpty() || this.isMFEmpty() || this.getCostMF() > this.getMF()) { return false; }

		// 精錬後アイテムをスロットに入れられないなら終了
		return ItemHelper.insertStack(this.getOut(), result.copy(), true).isEmpty();
	}

	// 精錬後のアイテムを取得
	public ItemStack checkSmeltResult(Level world, ItemStack stack) {
		this.dummyFurnace.setItem(0, stack);
		Optional<SmeltingRecipe> recipe = world.getRecipeManager().getRecipeFor(RecipeType.SMELTING, this.dummyFurnace, world);
		this.dummyFurnace.setItem(0, ItemStack.EMPTY);
		return recipe.map(Recipe::getResultItem).orElse(ItemStack.EMPTY);
	}

	public void craftStart(Level world, BlockPos pos) {

		ItemStack stack = this.getInputItem(0);
		this.stack = stack.copy();
		stack.shrink(1);

		// 精錬時間の取得
		this.dummyFurnace.setItem(0, this.stack);
		SmeltingRecipe recipe = world.getRecipeManager().getRecipeFor(RecipeType.SMELTING, this.dummyFurnace, world).get();
		this.maxCraftTime = (int) (recipe.getCookingTime() / this.getCraftRate());
		this.exp += recipe.getExperience();

		// 情報の初期化
		this.craftTime = 1;
		this.tickTime = -1;
		this.isCraft = true;
		this.sendPKT();

		// 精錬状態に見た目を変える
		world.setBlock(pos, this.getState(pos).setValue(MFFurnace.ISCRAFT, true), 3);
	}

	public void craftFinish(Level world, BlockPos pos) {

		// 精錬後のアイテムを出力スロットに入れる
		ItemStack result = this.checkSmeltResult(world, this.stack).copy();
		ItemHelper.insertStack(this.getOut(), result, false);
		ItemHelper.compactSimpleInventory(this.outInv);

		// 下のチェストへアイテムを送る
		this.extractItem(pos);

		// 情報の初期化
		this.craftTime = 0;
		this.tickTime = -1;
		this.isCraft = false;
		this.stack = ItemStack.EMPTY;
		this.setMF(this.getMF() - this.getCostMF());
		this.sendPKT();

		// 次に精錬できるアイテムがないなら未精錬状態へ見た目を変える
		if (!this.canSmelt(world, pos)) {
			world.setBlock(pos, this.getState(pos).setValue(MFFurnace.ISCRAFT, false), 3);
		}
	}

	// アイテム吸い込み
	public void suctionItem(Level world, BlockPos pos) {
		BlockEntity tile = this.getTile(pos.above());
		if (tile == null) { return; }

		IItemHandler handler = this.getItemHandler(tile, Direction.DOWN);
		if (handler == null) { return; }

		for (int i = 0; i < handler.getSlots(); i++) {
			ItemStack output = handler.getStackInSlot(i);
			if (output.isEmpty() || !this.canSmelt(world, output)) { continue; }

			ItemStack stack = ItemHelper.insertStack(this.getInput(), output.copy(), false);
			output.setCount(stack.getCount());
		}
	}

	// ホッパーからアイテムをチェストに入れる
	public void extractItem(BlockPos pos) {
		BlockEntity tile = this.getTile(pos.below());
		if (tile == null) { return; }

		IItemHandler handler = this.getItemHandler(tile, Direction.UP);
		if (handler == null) { return; }

		for (int h = 0; h < this.getInvSize(); h++) {
			ItemStack input = this.getOutItem(h);
			if (input.isEmpty()) { continue; }

			for (int i = 0; i < handler.getSlots(); i++) {
				ItemStack inStack = handler.insertItem(i, input.copy(), false);
				input.setCount(inStack.getCount());
				if (inStack.isEmpty()) { break; }
			}
		}
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize() {
		return 18;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF() {
		return this.maxMagiaFlux;
	}

	// 消費MFの取得
	public int getCostMF() {
		return this.costMF;
	}

	// 受信するMF量の取得
	@Override
	public int getReceiveMF() {
		return 5000;
	}

	public float getCraftRate() {
		return 5F;
	}

	// メインスロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// メインスロットのアイテムを取得
	public ItemStack getInputItem(int i) {
		return this.getInput().getStackInSlot(i);
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
		tag.putInt("tickTime", this.tickTime);
		tag.putInt("craftTime", this.craftTime);
		tag.putInt("maxCraftTime", this.maxCraftTime);
		tag.putFloat("exp", this.exp);
		tag.putBoolean("isCraft", this.isCraft);
		tag.put("outPutStack", this.stack.save(new CompoundTag()));
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.inputInv.deserializeNBT(tag.getCompound("inputInv"));
		this.outInv.deserializeNBT(tag.getCompound("outInv"));
		this.tickTime = tag.getInt("tickTime");
		this.craftTime = tag.getInt("craftTime");
		this.maxCraftTime = tag.getInt("maxCraftTime");
		this.exp = tag.getFloat("exp");
		this.isCraft = tag.getBoolean("isCraft");
		this.stack = ItemStack.of(tag.getCompound("outPutStack"));
	}

	// クラフト描画量を計算するためのメソッド
	public int getCraftProgress(int value) {
		return this.getProgress(value, this.craftTime, this.maxCraftTime);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new MFFurnaceMenu(windowId, inv, this);
	}

	// RS信号で動作を停止するかどうか
	public boolean isRSStop() {
		return true;
	}

	// インベントリのアイテムを取得
	public List<ItemStack> getInvList() {
		List<ItemStack> stackList = new ArrayList<>();

		for (int i = 0; i < this.getInvSize() + 1; i++) {
			this.addStackList(stackList, this.getInputItem(i));
		}

		for (int i = 0; i < this.getInvSize(); i++) {
			this.addStackList(stackList, this.getOutItem(i));
		}

		return stackList;
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.getInvList().isEmpty() && this.isMFEmpty();
	}
}
