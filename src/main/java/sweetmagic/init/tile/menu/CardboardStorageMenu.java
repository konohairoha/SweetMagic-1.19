package sweetmagic.init.tile.menu;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.MagiaSlot;
import sweetmagic.init.tile.sm.TileAbstractSM.MagiaHandler;
import sweetmagic.init.tile.sm.TileCardboardStorage;

public class CardboardStorageMenu extends BaseSMMenu {

	public final TileCardboardStorage tile;
	public final NonNullList<Slot> slotList = NonNullList.create();
	private final MagiaHandler inv;

	public CardboardStorageMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, MenuInit.getTile(TileCardboardStorage::new, pInv, data));
	}

	public CardboardStorageMenu(int windowId, Inventory pInv, TileCardboardStorage tile) {
		super(MenuInit.cardboardStorageMenu, windowId, pInv, tile);
		this.tile = tile;
		this.inv = this.tile.getInput();

		this.addSlots(new MagiaSlot(tile, this.inv, 0, 79, 14), tile.getLevel());

		this.setPInv(pInv, 7, 50, 0);
		this.setSlotSize(this.tile.getInvSize());
	}

	protected Slot addSlots(Slot slot, Level world) {
		this.slotList.add(slot);
		return this.addSlot(slot);
	}

	protected boolean moveItemStackTo(ItemStack stack, int slotStart, int slotEnd, boolean par1) {
		return this.moveSlot(stack, slotStart, slotEnd, par1);
	}
}
