package sweetmagic.init.tile.sm;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import sweetmagic.api.iblock.ITileFluid;
import sweetmagic.init.TileInit;
import sweetmagic.init.fluid.FluidTankHandler;
import sweetmagic.init.fluid.FluidTankHandler.TankProperty;
import sweetmagic.init.item.sm.SMBucket;
import sweetmagic.init.tile.menu.AlternativeTankMenu;

public class TileAlternativeTank extends TileAbstractSM implements ITileFluid {

	private static final int MAX_FLUID_VALUE = 256_000;
	public FluidStack fluid = new FluidStack(Fluids.EMPTY, 0);
	protected final TankProperty fluidPro;
	public LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> this.createFluidHandler());
	protected final StackHandler inputInv = new StackHandler(this.getInvSize());

	public TileAlternativeTank(BlockPos pos, BlockState state) {
		this(TileInit.alternativeTank, pos, state);
	}

	public TileAlternativeTank(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new SingleHandlerProvider(this.inputInv, IN_OUT);
		this.fluidPro = new TankProperty(MAX_FLUID_VALUE, true, true);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state) {
		super.serverTick(level, pos, state);
		if (this.tickTime % 10 != 0 || this.isRSPower()) { return; }

		if (!this.isMaxFluid()) {
			this.drainBucket();
		}
	}

	public void drainBucket() {
		ItemStack stack = this.getInputItem().copy();
		if (stack.isEmpty() || !(stack.getItem() instanceof SMBucket bucket)) { return; }

		FluidStack fStack = bucket.getFluidStack(stack);
		if (fStack.isEmpty()) { return; }

		Fluid fluid = fStack.getFluid();
		FluidStack tileStack = this.getContent();
		Fluid tileFluid = tileStack.getFluid();
		if (!fluid.isSame(Fluids.EMPTY) && (!tileFluid.isSame(Fluids.EMPTY) && !fluid.isSame(tileStack.getFluid() ) )) { return; }

		int value = Math.min(1000, fStack.getAmount());

		if (tileFluid.isSame(Fluids.EMPTY)) {
			this.setContent(new FluidStack(fluid, value));
		}

		else {
			tileStack.grow(value);
		}

		stack = bucket.shrinkWater(stack, value);
		this.getInputItem().shrink(1);
		ItemHandlerHelper.insertItemStacked(this.getInput(), stack, false);
		this.sendPKT();
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

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);

		CompoundTag fluidNBT = new CompoundTag();
		this.getContent().writeToNBT(fluidNBT);
		tag.put("fluid", fluidNBT);
		tag.put("inputInv", this.inputInv.serializeNBT());
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.setContent(FluidStack.loadFluidStackFromNBT(tag.getCompound("fluid")));
		this.inputInv.deserializeNBT(tag.getCompound("inputInv"));
	}

	// MFスロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// インプットスロットのアイテムを取得
	public ItemStack getInputItem() {
		return this.getInput().getStackInSlot(0);
	}

	public int getInvSize() {
		return 1;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new AlternativeTankMenu(windowId, inv, this);
	}

	// 最大水量を設定
	public int getMaxFuildValue() {
		return MAX_FLUID_VALUE;
	}

	public boolean isInfoEmpty() {
		return this.getContent().isEmpty();
	}
}
