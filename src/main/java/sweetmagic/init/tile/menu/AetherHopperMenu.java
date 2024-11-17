package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.slot.SlotInput;
import sweetmagic.init.tile.sm.TileAetherHopper;
import sweetmagic.util.ItemHelper;

public class AetherHopperMenu extends BaseSMMenu {

	public final Slot wandSlot;

    public AetherHopperMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
        this(windowId, pInv, (TileAetherHopper) MenuInit.getTile(pInv, data));
    }

	public AetherHopperMenu(int windowId, Inventory pInv, TileAetherHopper tile) {
		super(MenuInit.aetherHopperMenu, windowId, pInv, tile);

		this.wandSlot = this.addSlot(new SMSlot(tile.getWand(), 0, 26, 70, SlotInput.ISSTUFF));

		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 8; x++)
				this.addSlot(new SMSlot(tile.getInput(), x + y * 8, 26 + x * 18, 11 + y * 18));

		this.setPInv(pInv, 8, 93);
		this.setSlotSize(tile.getInvSize() + 1);
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		ItemHelper.compactInventory(( (TileAetherHopper) this.getTile()).inputInv);
		this.tile.clickButton();
		return true;
	}
}
