package sweetmagic.init.fluid;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class WaterCupWrapper implements IFluidHandlerItem, ICapabilityProvider {

	private static final int MAX_VALUE = 250;
	private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);
	protected ItemStack stack;

	public WaterCupWrapper(ItemStack stack) {
		this.stack = stack;
	}

	@NotNull
	@Override
	public ItemStack getContainer() {
		return this.stack;
	}

	public boolean canFillFluidType(FluidStack fluid) {
		if (fluid.getFluid() == Fluids.WATER || fluid.getFluid() == Fluids.LAVA) {
			return true;
		}
		return !fluid.getFluid().getFluidType().getBucket(fluid).isEmpty();
	}

	@NotNull
	public FluidStack getFluid() {
		return new FluidStack(Fluids.WATER, MAX_VALUE);
	}

	protected void setFluid(@NotNull FluidStack fluidStack) {
		this.stack = FluidUtil.getFilledBucket(fluidStack);
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@NotNull
	@Override
	public FluidStack getFluidInTank(int tank) {
		return this.getFluid();
	}

	@Override
	public int getTankCapacity(int tank) {
		return MAX_VALUE;
	}

	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		return true;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return 0;
	}

	@NotNull
	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		if (this.stack.getCount() != 1 || resource.getAmount() < MAX_VALUE) {
			return FluidStack.EMPTY;
		}

		FluidStack fluid = this.getFluid();
		if (!fluid.isEmpty() && fluid.isFluidEqual(resource)) {
			if (action.execute()) {
				this.setFluid(FluidStack.EMPTY);
			}

			return fluid;
		}

		return FluidStack.EMPTY;
	}

	@NotNull
	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		if (this.stack.getCount() != 1 || maxDrain < MAX_VALUE) {
			return FluidStack.EMPTY;
		}

		FluidStack fluid = this.getFluid();
		if (!fluid.isEmpty()) {
			if (action.execute()) {
				this.setFluid(FluidStack.EMPTY);
			}

			return fluid;
		}

		return FluidStack.EMPTY;
	}

	@Override
	@NotNull
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction face) {
		return ForgeCapabilities.FLUID_HANDLER_ITEM.orEmpty(cap, this.holder);
	}
}
