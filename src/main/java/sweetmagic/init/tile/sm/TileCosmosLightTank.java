package sweetmagic.init.tile.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import sweetmagic.init.TileInit;
import sweetmagic.init.fluid.FluidTankHandler.TankProperty;

public class TileCosmosLightTank extends TileAlternativeTank {

	private static final int MAX_FLUID_VALUE = 25600_000;
	protected final TankProperty fluidPro;
	public LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> this.createFluidHandler());

	public TileCosmosLightTank(BlockPos pos, BlockState state) {
		this(TileInit.cosmosLightTank, pos, state);
	}

	public TileCosmosLightTank(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
//		this.resolver = new SingleHandlerProvider(this.inputInv, IN_OUT);
		this.fluidPro = new TankProperty(MAX_FLUID_VALUE, true, true);
	}

	public TankProperty getTank() {
		return this.fluidPro;
	}

	// 最大水量を設定
	public int getMaxFuildValue () {
		return MAX_FLUID_VALUE;
	}

	public LazyOptional<IFluidHandler> getFluidHandler () {
		return this.fluidHandler;
	}
}
