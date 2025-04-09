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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import sweetmagic.api.iblock.ITileFluid;
import sweetmagic.init.ItemInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.fluid.FluidTankHandler;
import sweetmagic.init.fluid.FluidTankHandler.TankProperty;
import sweetmagic.init.item.sm.SMBucket;
import sweetmagic.init.tile.menu.MFGeneraterMenu;
import sweetmagic.util.ItemHelper;

public class TileMFGenerater extends TileSMMagic implements ITileFluid {

	public int craftTime = 0;
	public int maxMagiaFlux = 1000000;
	public static final int MAX_CRAFT_TIME = 5;
	public boolean isCraft = false;
	private static final Block AIR = Blocks.AIR;
	private static final Block MAGMA = Blocks.MAGMA_BLOCK;
	private static final BlockState LAVA = Blocks.LAVA.defaultBlockState();
	private final static Direction[] ALLFACE = new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

	private static final int MAX_FLUID_VALUE = 64_000;
	public FluidStack fluid = new FluidStack(Fluids.EMPTY, 0);
	protected final TankProperty fluidPro;
	public LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> this.createFluidHandler());
	protected final StackHandler inputInv = new StackHandler(this.getInvSize());
	protected final StackHandler bucketInv = new StackHandler(1);

	public TileMFGenerater(BlockPos pos, BlockState state) {
		this(TileInit.mfGenerater, pos, state);
	}

	public TileMFGenerater(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new SingleHandlerProvider(this.inputInv, IN);
		this.fluidPro = new TankProperty(MAX_FLUID_VALUE, false, true, f -> f.isSame(Fluids.LAVA));
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		if (this.tickTime % 20 != 0 || this.isRSPower()) { return; }

		if (!this.isCraft) {
			this.craftStart();
		}

		else if (this.craftTime++ >= MAX_CRAFT_TIME) {
			this.craftFinish();
		}

		if (this.isCraft) {
			this.sendInfo();
		}

		this.fillBucket();
		this.fillTank(pos);
		this.setLava(world, pos.below());
	}

	public void craftStart() {
		ItemHelper.compactSimpleInventory(this.inputInv);
		ItemStack stack = this.getInputItem(0);
		if (stack.isEmpty() || !stack.is(Tags.Items.COBBLESTONE) || stack.getCount() < 8 || this.isMaxFluid() || this.getMF() < this.shrinkMF()) { return; }

		stack.shrink(8);
		this.isCraft = true;
		this.craftTime = 0;
		this.setMF(this.getMF() - this.shrinkMF());
		this.sendInfo();
	}

	public void craftFinish() {

		this.isCraft = false;
		FluidStack fluid = this.getContent();

		if (fluid.isEmpty()) {
			this.setContent(new FluidStack(Fluids.LAVA, 1000));
		}

		else {
			fluid.setAmount(Math.min(MAX_FLUID_VALUE, this.getFluidValue() + 1000));
		}

		this.playSound(this.getBlockPos(), SoundEvents.BUCKET_FILL_LAVA, 0.5F, 1F);
		this.sendInfo();
	}

	public void fillBucket() {
		if (this.getFluidValue() <= 0) { return; }

		ItemStack stack = this.getBucketItem().copy();
		if (stack.isEmpty() || !(stack.getItem() instanceof SMBucket bucket)) { return; }

		FluidStack fluid = new FluidStack(Fluids.LAVA, Math.min(1000, this.getFluidValue()));

		if (stack.is(ItemInit.alt_bucket)) {
			stack = new ItemStack(ItemInit.alt_bucket_lava);
		}

		else {
			fluid = bucket.getFluidStack(stack);
			int amount = Math.min(1000, this.getFluidValue());

			if (fluid.isEmpty()) {
				fluid = new FluidStack(Fluids.LAVA, amount);
			}

			else {
				fluid.grow(amount);
			}
		}

		bucket.saveFluid(stack, fluid);
		this.getContent().setAmount(Math.max(0, this.getFluidValue() - 1000));
		this.getBucketItem().shrink(1);
		ItemHandlerHelper.insertItemStacked(this.getBucket(), stack, false);

		this.sendInfo();
	}

	public void fillTank(BlockPos pos) {
		if (this.getFluidValue() <= 0) { return; }

		BlockEntity tile = this.getTile(pos.relative(Direction.UP));
		if (tile == null) { return; }

		IFluidHandler handler = this.getFluidHandler(tile, Direction.UP);
		if (handler == null) { return; }

		FluidStack flu = this.getContent().copy();
		flu.setAmount(Math.min(1000, this.getFluidValue()));

		int amount = handler.fill(flu, FluidAction.EXECUTE);
		this.getContent().shrink(amount);
	}

	public void setLava(Level world, BlockPos pos) {
		if (this.getFluidValue() < 1000 || this.getBlock(pos) != MAGMA) { return; }
		int count = 0;
		int maxCount = this.getFluidValue() / 1000;

		for (Direction face : ALLFACE) {
			if (this.getBlock(pos.relative(face)) != AIR || count >= maxCount) { continue; }

			world.setBlock(pos.relative(face), LAVA, 3);
			count++;
		}

		if (count > 0) {
			this.getContent().shrink(count * 1000);
			this.sendInfo();
		}
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize() {
		return 12;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF() {
		return this.maxMagiaFlux;
	}

	// 受信するMF量の取得
	@Override
	public int getReceiveMF() {
		return 50000;
	}

	public int shrinkMF() {
		return 1000;
	}

	// 出力スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// 出力スロットのアイテムを取得
	public ItemStack getInputItem(int i) {
		return this.getInput().getStackInSlot(i);
	}

	// バケツスロットの取得
	public IItemHandler getBucket() {
		return this.bucketInv;
	}

	// バケツスロットのアイテムを取得
	public ItemStack getBucketItem() {
		return this.getBucket().getStackInSlot(0);
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

	// 最大水量を設定
	public int getMaxFuildValue() {
		return MAX_FLUID_VALUE;
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("outInv", this.inputInv.serializeNBT());
		tag.put("bucketInv", this.bucketInv.serializeNBT());
		CompoundTag fluidNBT = new CompoundTag();
		this.getContent().writeToNBT(fluidNBT);
		tag.put("fluid", fluidNBT);
		tag.putBoolean("isCraft", this.isCraft);
		tag.putInt("craftTime", this.craftTime);
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.inputInv.deserializeNBT(tag.getCompound("outInv"));
		this.bucketInv.deserializeNBT(tag.getCompound("bucketInv"));
		this.setContent(FluidStack.loadFluidStackFromNBT(tag.getCompound("fluid")));
		this.isCraft = tag.getBoolean("isCraft");
		this.craftTime = tag.getInt("craftTime");
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new MFGeneraterMenu(windowId, inv, this);
	}

	// RS信号で動作を停止するかどうか
	public boolean isRSStop() {
		return true;
	}

	// インベントリのアイテムを取得
	public List<ItemStack> getInvList() {
		List<ItemStack> stackList = new ArrayList<>();

		for (int i = 0; i < this.getInvSize(); i++) {
			this.addStackList(stackList, this.getInputItem(i));
		}

		return stackList;
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.getInvList().isEmpty() && this.isMFEmpty() && this.getContent().isEmpty();
	}
}
