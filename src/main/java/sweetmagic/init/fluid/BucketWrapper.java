package sweetmagic.init.fluid;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import sweetmagic.init.ItemInit;
import sweetmagic.init.item.sm.SMBucket;

public class BucketWrapper implements IFluidHandlerItem, ICapabilityProvider {

	private static final int MAX_VALUE = 1000;
	private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);
	private ItemStack stack;
	private final Item item;
	private SMBucket bucket;
	private FluidStack fS;

	public BucketWrapper(ItemStack stack, SMBucket bucket) {
		this.stack = stack;
		this.item = stack.getItem();
		this.bucket = bucket;
		this.fS = this.bucket.getFluidStack(this.stack);
	}

	@NotNull
	@Override
	public ItemStack getContainer() {
		return this.stack;
	}

	public SMBucket getBucket() {
		return this.bucket;
	}

	public boolean canFillFluidType(FluidStack fluid) {
		return true;
	}

	@NotNull
	public FluidStack getFluid() {
		return this.getBucket().getFluidStack(this.getContainer());
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
		if (!this.getContainer().is(ItemInit.alt_bucket)) { return 0; }

		Fluid flu = resource.getFluid();
		if (!flu.isSame(Fluids.WATER) && flu.isSame(Fluids.LAVA)) { return 0; }

		if (action.execute()) {
			this.setFluid(resource);

			this.stack = new ItemStack(flu.isSame(Fluids.WATER) ? ItemInit.alt_bucket_water : ItemInit.alt_bucket_lava);
			FluidStack fluid = new FluidStack(resource.getFluid(), Math.min(1000, resource.getAmount()));
			this.getBucket().saveFluid(this.getContainer(), fluid);
		}

		return FluidType.BUCKET_VOLUME;
	}

	@NotNull
	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {

		FluidStack fluidStack = this.getFluid().copy();
		if (!fluidStack.isEmpty() && fluidStack.isFluidEqual(resource)) {

			fluidStack.setAmount(1000);

			if (action.execute()) {
				this.setFluid(FluidStack.EMPTY);
				this.fS.shrink(1000);
				this.stack = new ItemStack(this.fS.getAmount() > 0 ? this.item : ItemInit.alt_bucket);
				this.getBucket().saveFluid(this.getContainer(), this.fS);
			}

			return fluidStack;
		}

		return FluidStack.EMPTY;
	}

	@NotNull
	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {

		FluidStack fluidStack = this.getFluid();
		if (!fluidStack.isEmpty()) {
			if (action.execute()) {
				this.setFluid(FluidStack.EMPTY);
			}

			return fluidStack;
		}

		return FluidStack.EMPTY;
	}

	@Override
	@NotNull
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction face) {
		return ForgeCapabilities.FLUID_HANDLER_ITEM.orEmpty(cap, this.holder);
	}
}
