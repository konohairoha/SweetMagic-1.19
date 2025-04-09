package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import sweetmagic.api.iblock.ITileFluid;
import sweetmagic.api.iitem.IFood;
import sweetmagic.init.ItemInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.fluid.FluidTankHandler;
import sweetmagic.init.fluid.FluidTankHandler.TankProperty;
import sweetmagic.init.item.sm.SMBucket;
import sweetmagic.init.tile.menu.FreezerMenu;
import sweetmagic.recipe.feezer.FreezerRecipe;

public class TileFreezer extends TileAbstractSM implements ITileFluid {

	private static final int MAX_CRAFT_TIME = 6;
	public int craftTime = 0;
	public boolean isCraft = false;
	public Player player = null;
	public ItemStack outStack = ItemStack.EMPTY;
	public List<ItemStack> craftList = new ArrayList<>();

	private static final int MAX_WATER_VALUE = 10000;
	private final int useWaterValue = 250;
	protected final TankProperty fluidPro;
	public FluidStack fluid = new FluidStack(Fluids.EMPTY, 0);
	public LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> this.createFluidHandler());
	private final static ItemStack ICE = new ItemStack(Blocks.ICE);

	protected final StackHandler handInv = new StackHandler(1);
	protected final StackHandler inputInv = new StackHandler(this.getInvSize());
	protected final StackHandler outputInv = new StackHandler(this.getOutSize());
	protected final StackHandler bucketInv = new StackHandler(1);
	protected final StackHandler iceInv = new StackHandler(2);

	public TileFreezer(BlockPos pos, BlockState state) {
		this(TileInit.freezer, pos, state);
	}

	public TileFreezer(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.fluidPro = new TankProperty(MAX_WATER_VALUE, true, false, f -> f.isSame(Fluids.WATER));
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
		return !FreezerRecipe.getRecipe(this.level, this.getStackList()).isEmpty();
	}

	// 作成開始
	public void craftStart() {
		List<ItemStack> stackList = this.getStackList();
		FreezerRecipe recipe = FreezerRecipe.getRecipe(this.level, stackList).get();
		ItemStack resultStack = recipe.getResultItem().copy();
		if (!ItemHandlerHelper.insertItemStacked(this.getOutput(), resultStack, true).isEmpty()) { return; }

		// クラフトで使うアイテムを入れておく
		this.craftList = new ArrayList<ItemStack>(recipe.getRequestList());
		List<ItemStack> requestList = recipe.getRequestList();
		this.outStack = this.player != null ? this.setCookQuality(this.player, resultStack) : resultStack;

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
	public void craftFinish() {
		ItemHandlerHelper.insertItemStacked(this.getOutput(), this.outStack, false);
		this.playSound(this.getBlockPos(), SoundInit.FREEZER_CRAFT, 0.1F, 1F);
		this.clearInfo();
	}

	// 水の投入
	public void insertWater() {
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

		else if (bucket.is(ItemInit.alt_bucket_water) && bucket.getItem() instanceof SMBucket bk) {
			FluidStack fluid = bk.getFluidStack(bucket);
			insertWaterValue = Math.min(1000, fluid.getAmount());
		}

		if (insertWaterValue <= 0 || !this.canInsertWater(insertWaterValue)) { return; }

		if (this.getContent().isEmpty()) {
			this.setContent(new FluidStack(Fluids.WATER, insertWaterValue));
		}

		else {
			this.setAmount(this.getFluidValue() + insertWaterValue);
		}

		this.sendPKT();

		if (bucket.is(ItemInit.alt_bucket_water) && bucket.getItem() instanceof SMBucket bk) {
			FluidStack fluid = bk.getFluidStack(bucket);
			fluid.shrink(insertWaterValue);
			bk.saveFluid(bucket, fluid);

			if (fluid.isEmpty() || fluid.getAmount() <= 0) {
				bucket.shrink(1);
				ItemHandlerHelper.insertItemStacked(this.getBucket(), new ItemStack(ItemInit.alt_bucket), false);
			}
		}

		else {
			bucket.shrink(1);
		}

		if (copy.is(Items.WATER_BUCKET)) {
			ItemHandlerHelper.insertItemStacked(this.getBucket(), new ItemStack(Items.BUCKET), false);
		}
	}

	// 氷の作成
	public void craftIce() {
		if(!ItemHandlerHelper.insertItemStacked(this.getIce(), ICE.copy(), true).isEmpty()) { return; }

		ItemHandlerHelper.insertItemStacked(this.getIce(), ICE.copy(), false);
		this.setAmount(this.getFluidValue() - this.useWaterValue);
		this.sendPKT();
	}

	public ItemStack setCookQuality(Player player, ItemStack stack) {
		Item item = stack.getItem();
		if (!(item instanceof IFood food)) { return stack; }

		float chance = this.getBaseChance();
		int level = food.setQuality(this.getBlockPos(), player, stack, chance);
		food.setQualityValue(stack, level);
		return stack;
	}

	public float getBaseChance() {
		float chance = 0F;
		List<ItemStack> craftList = this.craftList;
		if (craftList.isEmpty()) { return chance; }

		for (ItemStack stack : craftList) {
			if (stack.getItem() instanceof IFood food) {
				chance += food.getQualityValue(stack) * 0.05F;
			}
		}

		return chance;
	}

	// 初期化
	public void clearInfo() {
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
	public List<ItemStack> getDropList() {

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

		CompoundTag fluidNBT = new CompoundTag();
		this.getContent().writeToNBT(fluidNBT);
		tag.put("fluid", fluidNBT);
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
		this.setContent(FluidStack.loadFluidStackFromNBT(tag.getCompound("fluid")));

		if (tag.contains("waterValue")) {
			this.setContent(new FluidStack(Fluids.WATER, tag.getInt("waterValue")));
			tag.remove("waterValue");
		}
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize() {
		return 6;
	}

	// 出力スロットのスロット数
	public int getOutSize() {
		return 4;
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
	public ItemStack getOutputItem(int i) {
		return this.getOutput().getStackInSlot(i);
	}

	// バケツスロットの取得
	public IItemHandler getBucket() {
		return this.bucketInv;
	}

	// バケツスロットのアイテムを取得
	public ItemStack getBucketItem() {
		return this.getBucket().getStackInSlot(0);
	}

	// 氷スロットの取得
	public IItemHandler getIce() {
		return this.iceInv;
	}

	// 氷スロットのアイテムを取得
	public ItemStack getIceItem(int i) {
		return this.getIce().getStackInSlot(i);
	}

	// 水が必要容量を超えているかどうか
	public boolean isNeedWater() {
		return this.getFluidValue() >= this.useWaterValue;
	}

	// 水を入れれる量があるか
	public boolean canInsertWater(int insertWaterValue) {
		return this.getFluidValue() + insertWaterValue <= this.getMaxFuildValue();
	}

	// クラフト描画量を計算するためのメソッド
	public int getCraftProgress(int value) {
		return Math.min(value, (int) (value * (float) (this.craftTime) / (float) (MAX_CRAFT_TIME)));
	}

	// 水描画量を計算するためのメソッド
	public int getWaterProgress(int value) {
		return Math.min(value, (int) (value * (float) (this.getFluidValue()) / (float) (this.getMaxFuildValue())));
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new FreezerMenu(windowId, inv, this);
	}

	public int fluidTanks() {
		return 1;
	}

	public IFluidHandler createFluidHandler() {
		return new FluidTankHandler(this);
	}

	public void setContent(FluidStack fluid) {
		this.fluid = fluid;
		this.setChanged();
	}

	public TankProperty getTank() {
		return this.fluidPro;
	}

	public FluidStack getContent() {
		return this.fluid;
	}

	public void setAmount(int amount) {
		this.fluid.setAmount(amount);
	}

	public int getFluidValue() {
		return this.getContent().getAmount();
	}

	@Override
	public int getMaxFuildValue() {
		return MAX_WATER_VALUE;
	}

	public void sendData() {
		this.sendInfo();
	}

	public LazyOptional<IFluidHandler> getFluidHandler() {
		return this.fluidHandler;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction face) {

		if (cap == ForgeCapabilities.FLUID_HANDLER) {
			return (LazyOptional<T>) this.getFluidHandler();
		}

		return super.getCapability(cap, face);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		if (this.getFluidHandler() != null) {
			this.getFluidHandler().invalidate();
		}
	}
}
