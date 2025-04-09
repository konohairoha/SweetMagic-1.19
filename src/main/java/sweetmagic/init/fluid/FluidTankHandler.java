package sweetmagic.init.fluid;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import sweetmagic.api.iblock.ITileFluid;

public record FluidTankHandler(ITileFluid tile) implements IFluidHandler, IFluidTank {

	@Override
	public int fill(FluidStack fStack, FluidAction action) {

		FluidStack fluid = this.getFluid();
		TankProperty tank = this.getTank();

		if (!fStack.isEmpty() && this.isFluidValid(0, fStack) && (fluid.isEmpty() || fluid.isFluidEqual(fStack))) {

			int change = Math.min(tank.cap - fluid.getAmount(), fStack.getAmount());
			if (action == FluidAction.EXECUTE && change >= 0) {
				int prevAmount = fluid.getAmount();
				fluid = fStack.copy();

				if (this.getFluid().isEmpty()) {
					this.tile.setContent(fStack);
				}

				else {
					this.getFluid().setAmount(prevAmount + change);
				}

				this.blockChanged();
			}

			this.tile.sendData();
			return change;
		}

		this.tile.sendData();
		return 0;
	}

	@Nonnull
	@Override
	public FluidStack drain(FluidStack fStack, FluidAction action) {
		if (fStack.isEmpty()) { return FluidStack.EMPTY; }

		FluidStack fluid = this.getFluid();
		TankProperty tank = this.getTank();

		if (tank.canDrain && fStack.isFluidEqual(fluid)) {
			int change = Math.min(fluid.getAmount(), fStack.getAmount());

			if (action == FluidAction.EXECUTE) {
				fluid.shrink(change);
				this.blockChanged();
			}

			FluidStack out = fStack.copy();
			out.setAmount(change);
			this.tile.sendData();
			return out;
		}

		this.tile.sendData();
		return FluidStack.EMPTY;
	}

	@Nonnull
	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		if (maxDrain <= 0) { return FluidStack.EMPTY; }

		FluidStack fluid = this.getFluid();
		TankProperty tank = this.getTank();

		if (tank.canDrain && !fluid.isEmpty()) {
			int change = Math.min(fluid.getAmount(), maxDrain);
			FluidStack content = fluid.copy();
			content.setAmount(change);

			if (action == FluidAction.EXECUTE) {
				fluid.shrink(change);
				this.blockChanged();
			}

			this.tile.sendData();
			return content;
		}

		this.tile.sendData();
		return FluidStack.EMPTY;
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Nonnull
	@Override
	public FluidStack getFluidInTank(int tank) {
		return this.getFluid();
	}

	@Override
	public int getTankCapacity(int tank) {
		return this.getTank().cap;
	}

	@Override
	public boolean isFluidValid(int tank, @Nonnull FluidStack fStack) {
		TankProperty tankPro = this.getTank();
		return tankPro.canFill && tankPro.canAccept.test(fStack.getFluid());
	}

	public void blockChanged() { }

	@Nonnull
	@Override
	public FluidStack getFluid() {
		return this.tile.getContent();
	}

	public TankProperty getTank() {
		return this.tile.getTank();
	}

	@Override
	public int getFluidAmount() {
		return this.getFluid().getAmount();
	}

	@Override
	public int getCapacity() {
		return this.getTank().cap;
	}

	@Override
	public boolean isFluidValid(FluidStack fStack) {
		TankProperty tankPro = this.getTank();
		return tankPro.canFill && tankPro.canAccept.test(fStack.getFluid());
	}

	public static class TankProperty {

		public final int cap;
		public final boolean canFill;
		public final boolean canDrain;
		public final Predicate<Fluid> canAccept;

		public TankProperty(int cap, boolean canFill, boolean canDrain) {
			this(cap, canFill, canDrain, f -> canFill);
		}

		public TankProperty(int cap, boolean canFill, boolean canDrain, @Nonnull Predicate<Fluid> canAccept) {
			this.cap = cap;
			this.canFill = canFill;
			this.canDrain = canDrain;
			this.canAccept = canAccept;
		}
	}
}
