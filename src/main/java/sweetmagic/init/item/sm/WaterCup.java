package sweetmagic.init.item.sm;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import sweetmagic.init.fluid.WaterCupWrapper;

public class WaterCup extends SMFood {

	public WaterCup(String name, int healAmount, float saturation, int data, boolean isDrink) {
		super(name, healAmount, saturation, data, isDrink);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag tag) {
		return new WaterCupWrapper(stack);
	}

	// ツールチップの表示
	public void addTip(ItemStack stack, List<Component> toolTip) {
		super.addTip(stack, toolTip);
		FluidStack fluid = new FluidStack(Fluids.WATER, 250);
		toolTip.add(this.getTipArray(fluid.getDisplayName(), ": ", this.getLabel(fluid.getAmount() + "mB", GREEN)));
	}
}
