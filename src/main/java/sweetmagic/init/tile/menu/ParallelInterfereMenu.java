package sweetmagic.init.tile.menu;

import java.lang.reflect.Field;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.sm.TileParallelInterfere;
import sweetmagic.util.ItemHelper;

public class ParallelInterfereMenu extends BaseSMMenu {

	public final TileParallelInterfere tile;

	public ParallelInterfereMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, (TileParallelInterfere) MenuInit.getTile(pInv, data));
	}

	public ParallelInterfereMenu(int windowId, Inventory pInv, TileParallelInterfere tile) {
		super(MenuInit.parallelInterfereMenu, windowId, pInv, tile);
		this.tile = tile;

		IItemHandler fuel = this.tile.getInput();
		int maxY = tile.getInvSize() / 9;

		for (int y = 0; y < maxY; y++) {
			for (int x = 0; x < 9; x++) {

				int pY = (x + y * 9) >= 54 ? Integer.MAX_VALUE : 8 + y * 18;
				this.addSlot(new SMSlot(fuel, x + y * 9, 8 + x * 18, pY));
			}
		}

		this.setPInv(pInv, 8, 120);
		this.setSlotSize(this.tile.getInvSize());
	}

	public void updateSlotPositions(int offsetY) {

		int maxY = tile.getInvSize() / 9;

		for (int y = 0; y < maxY; y++) {
			for (int x = 0; x < 9; x++) {

				int tX = x + (y - offsetY) * 9;
				int pY = (tX >= 54 || tX < 0) ? -1000 : 8 + (y - offsetY) * 18;
				Slot slot = this.slots.get(x + y * 9);
				this.setSlotPosY(slot, pY);
			}
		}
	}

	public void setSlotPos(Slot slot, String fieldName, int newValue) {
		try {
			Field field = ObfuscationReflectionHelper.findField(Slot.class, fieldName);
			field.setAccessible(true);
			field.set(slot, newValue);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void setSlotPosY(Slot slot, int newValue) {
		setSlotPos(slot, "f_40221_", newValue);
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {

		if (id == 0) {
			ItemHelper.compactInventory(this.tile.getInputInv());
		}

		else if (id == 1) {
			return true;
		}

		this.tile.clickButton();
		return true;
	}
}
