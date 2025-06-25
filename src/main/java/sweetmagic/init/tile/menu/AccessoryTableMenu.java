package sweetmagic.init.tile.menu;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.MagiaSlot;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.slot.SlotInput;
import sweetmagic.init.tile.sm.TileAccessoryTable;

public class AccessoryTableMenu extends BaseSMMenu {

	public final TileAccessoryTable tile;
	public final NonNullList<Slot> slotList = NonNullList.create();
	public final Slot starSlot;

	public AccessoryTableMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, MenuInit.getTile(TileAccessoryTable::new, pInv, data));
	}

	public AccessoryTableMenu(int windowId, Inventory pInv, TileAccessoryTable tile) {
		super(MenuInit.accessoryProcessingMenu, windowId, pInv, tile);
		this.tile = tile;

		this.addSlots(new SMSlot(this.tile.getInput(), 0, 44, 16, SlotInput.CANACCE));
		this.addSlots(new MagiaSlot(this.tile, this.tile.getAcce(), 0, 70, 42, SlotInput.ISDUPACCE));
		this.starSlot = this.addSlots(new MagiaSlot(this.tile, this.tile.getStar(), 0, 70, 64, SlotInput.ISSTAR));
		this.addSlot(new SMSlot(this.tile.getOutput(), 0, 134, 17, s -> false));

		this.setPInv(pInv, 8, 90);
		this.setSlotSize(this.tile.getInvSize() * 4);
	}

	protected Slot addSlots(Slot slot) {
		this.slotList.add(slot);
		return this.addSlot(slot);
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		if (this.tile.isCraft || !this.tile.canCraft(this.starSlot.getItem().getCount())) { return false; }
		this.tile.craftStart();
		return true;
	}

	protected boolean moveItemStackTo(ItemStack stack, int slotStart, int slotEnd, boolean par1) {
		return this.moveSlot(stack, slotStart, slotEnd, par1);
	}

	public boolean checkStack(ItemStack stack) {
		return true;
	}
}
