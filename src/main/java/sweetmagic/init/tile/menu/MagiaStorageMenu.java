package sweetmagic.init.tile.menu;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.MagiaSlot;
import sweetmagic.init.tile.sm.TileAbstractSM.MagiaHandler;
import sweetmagic.init.tile.sm.TileMagiaStorage;
import sweetmagic.util.ItemHelper;

public class MagiaStorageMenu extends BaseSMMenu {

	public final TileMagiaStorage tile;
	public final NonNullList<Slot> slotList = NonNullList.create();
	private final MagiaHandler inv;

	public MagiaStorageMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, MenuInit.getTile(TileMagiaStorage::new, pInv, data));
	}

	public MagiaStorageMenu(int windowId, Inventory pInv, TileMagiaStorage tile) {
		super(MenuInit.magiaStorageMenu, windowId, pInv, tile);
		this.tile = tile;
		this.inv = (MagiaHandler) this.tile.getInput();

		for (int y = 0; y < 8; y++)
			for (int x = 0; x < 13; x++)
			this.addSlots(new MagiaSlot(tile, this.inv, x + y * 13, 5 + x * 18, 5 + y * 18), tile.getLevel());

		this.setPInv(pInv, 41, 151, 1);
		this.setSlotSize(this.tile.getInvSize());
	}

	protected Slot addSlots(Slot slot, Level world) {
		this.slotList.add(slot);
		return this.addSlot(slot);
	}

	protected boolean moveItemStackTo(ItemStack stack, int slotStart, int slotEnd, boolean par1) {
		return this.moveSlot(stack, slotStart, slotEnd, par1);
	}

	public void removed(Player player) {
		super.removed(player);
//		this.tile.sendPKT();
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		switch (id) {
		case 0:
			ItemHelper.compactInventory(this.tile.inputInv);
			this.tile.clickButton();
			break;
		case 1:
			ItemHelper.inventoryInput(player, this.tile.inputInv);
			this.tile.clickButton();
			break;
		case 2:
			ItemHelper.inventoryOutput(player, this.tile.inputInv);
			this.tile.clickButton();
			break;
		}
		this.tile.sendPKT();
		return true;
	}
}
