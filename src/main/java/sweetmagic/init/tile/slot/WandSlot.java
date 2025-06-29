package sweetmagic.init.tile.slot;

import java.util.function.Predicate;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.api.iitem.IMagicItem;

public class WandSlot extends SMSlot {

	public WandSlot(IItemHandler handler, int index, int xPos, int yPos, Predicate<ItemStack> val) {
		super(handler, index, xPos, yPos, val);
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return stack.getItem() instanceof IMagicItem magic && !magic.isShirink() ? 1 : 64;
	}

	public int getMaxStackSize() {
		return 64;
	}
}
