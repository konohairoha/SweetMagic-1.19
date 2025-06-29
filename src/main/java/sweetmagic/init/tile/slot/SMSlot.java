package sweetmagic.init.tile.slot;

import java.util.function.Predicate;

import org.antlr.v4.runtime.misc.NotNull;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SMSlot extends SlotItemHandler {

	private final Predicate<ItemStack> val;

	public SMSlot(IItemHandler handler, int index, int xPos, int yPos) {
		super(handler, index, xPos, yPos);
		this.val = (s) -> true;
	}

	public SMSlot(IItemHandler handler, int index, int xPos, int yPos, Predicate<ItemStack> val) {
		super(handler, index, xPos, yPos);
		this.val = val;
	}

	@Override
	public boolean mayPlace(@NotNull ItemStack stack) {
		return super.mayPlace(stack) && this.val.test(stack);
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return Math.min(this.getMaxStackSize(), stack.getMaxStackSize());
	}
}
