package sweetmagic.init.tile.slot;

import java.util.function.Predicate;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.tile.sm.TileAbstractSM;

public class MagiaSlot extends SMSlot {

	private final TileAbstractSM tile;

	public MagiaSlot(TileAbstractSM tile, IItemHandler handler, int index, int xPos, int yPos) {
		super(handler, index, xPos, yPos);
		this.tile = tile;
	}

	public MagiaSlot(TileAbstractSM tile, IItemHandler handler, int index, int xPos, int yPos, Predicate<ItemStack> val) {
		super(handler, index, xPos, yPos, val);
		this.tile = tile;
	}

	public void setChanged() {
		super.setChanged();
		this.tile.sendPKT();
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return this.getMaxStackSize();
	}

	@Override
	public int getMaxStackSize() {
		return this.tile.getMaxStackSize();
	}
}
