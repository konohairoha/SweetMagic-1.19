package sweetmagic.api.iblock;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import sweetmagic.init.fluid.FluidTankHandler.TankProperty;

public interface ITileFluid {

	int fluidTanks();

	IFluidHandler createFluidHandler();

	void setContent(FluidStack fluid);

	FluidStack getContent();

	void setAmount(int amount);

	TankProperty getTank();

	int getFluidValue();

	int getMaxFuildValue();

	void sendData();

	default float getFluidProgressScaled(float value) {
		return Math.min(value, (value * (float) (this.getFluidValue()) / (float) (this.getMaxFuildValue())));
	}

	default boolean isMaxFluid() {
		return this.getFluidValue() >= this.getMaxFuildValue();
	}

	default String getFluidPercent() {
		return String.format("%.1f", ((float) this.getFluidValue() / (float) this.getMaxFuildValue()) * 100F) + "%";
	}
}
