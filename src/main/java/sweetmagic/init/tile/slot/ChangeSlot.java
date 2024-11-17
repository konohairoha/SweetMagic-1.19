package sweetmagic.init.tile.slot;

import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.tile.menu.AetherCraftTableMenu;

public class ChangeSlot extends SMSlot {

	private final AetherCraftTableMenu menu;
	private boolean isActice = true;

	public ChangeSlot(IItemHandler handler, int index, int xPos, int yPos, Predicate<ItemStack> val, AetherCraftTableMenu menu) {
		super(handler, index, xPos, yPos, val);
		this.menu = menu;
	}

	public void set(@NotNull ItemStack stack) {
		super.set(stack);
		this.menu.slotsChangInv(this.container);
	}

	public boolean isActive () {
		return this.isActice;
	}

	public void setActive(boolean actice) {
		this.isActice = actice;
	}
}
