package sweetmagic.init.item.sm;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class SMBurnItem extends SMItem {

	private final int burnTime;

	public SMBurnItem (String name, int burnTime, CreativeModeTab tab) {
		super(name, setItem(tab));
		this.burnTime = burnTime;
	}

	@Override
	public int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
		return this.burnTime;
	}
}
