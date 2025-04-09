package sweetmagic.init.tile.slot;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;
import sweetmagic.init.tile.menu.AetherCraftTableMenu;
import sweetmagic.init.tile.menu.AetherCraftTableMenu.TileHandler;

public class ChangeSlot extends SMSlot {

	private final AetherCraftTableMenu menu;
	private final TileHandler handler;
	private boolean isActice = true;
	private final int masStackSize;

	public ChangeSlot(TileHandler handler, int index, int xPos, int yPos, AetherCraftTableMenu menu, int masStackSize) {
		super(handler.handler(), index, xPos, yPos, s -> true);
		this.menu = menu;
		this.handler = handler;
		this.masStackSize = masStackSize;
	}

	public void set(@NotNull ItemStack stack) {
		super.set(stack);
		this.menu.slotsChangInv(this.container);
	}

	public void setChanged() {
		super.setChanged();
		if (this.getMaxStackSize() > 64) {
			this.handler.sentPKT();
		}
	}

	public boolean isActive() {
		return this.isActice;
	}

	public void setActive(boolean actice) {
		this.isActice = actice;
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return this.masStackSize;
	}

	@Override
	public int getMaxStackSize() {
		return this.masStackSize;
	}
}
