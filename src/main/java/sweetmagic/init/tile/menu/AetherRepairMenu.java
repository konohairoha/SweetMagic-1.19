package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.slot.SlotArmor;
import sweetmagic.init.tile.slot.SlotInput;
import sweetmagic.init.tile.sm.TileAetherRepair;
import sweetmagic.util.SMUtil;

public class AetherRepairMenu extends BaseSMMenu {

	public final TileAetherRepair tile;

	public AetherRepairMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, MenuInit.getTile(TileAetherRepair::new, pInv, data));
	}

	public AetherRepairMenu(int windowId, Inventory pInv, TileAetherRepair tile) {
		super(MenuInit.aetherRepairMenu, windowId, pInv, tile);
		this.tile = tile;

		IItemHandler fuel = this.tile.getInput();
		int addX = 24;

		for (int x = 0; x < 4; x++)
			this.addSlot(new SMSlot(fuel, x, 57 + x * 18 + addX, 31, SlotInput.ISREPAIR));

		this.setPInv(pInv, 32, 96);

		// Armor slots
		for (int y = 0; y < 4; y++)
			this.addSlot(new SlotArmor(pInv.player, SMUtil.getEquipmentSlot(y), pInv, 39 - y, -19 + addX, 96 + y * 18));

		this.setSlotSize(4);
	}
}
