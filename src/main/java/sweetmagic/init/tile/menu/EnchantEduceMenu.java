package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.slot.SlotArmor;
import sweetmagic.init.tile.slot.SlotInput;
import sweetmagic.init.tile.sm.TileEnchantEduce;
import sweetmagic.util.SMUtil;

public class EnchantEduceMenu extends BaseSMMenu {

	public final TileEnchantEduce tile;
	public final Slot bookSlot;
	public final Slot pageSlot;
	public final Slot magicBookSlot;

	public EnchantEduceMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, (TileEnchantEduce) MenuInit.getTile(pInv, data));
	}

	public EnchantEduceMenu(int windowId, Inventory pInv, TileEnchantEduce tile) {
		super(MenuInit.enchantEduceMenu, windowId, pInv, tile);
		this.tile = tile;
		int addX = 24;

		this.magicBookSlot = this.addSlot(new SMSlot(this.tile.getBook(), 0, 84 + addX, 8, SlotInput.ISMAGICBOOK));
		this.pageSlot = this.addSlot(new SMSlot(this.tile.getPage(), 0, 54 + addX, 24, SlotInput.ISPAGE));
		this.bookSlot = this.addSlot(new SMSlot(this.tile.getInput(), 0, 32 + addX, 24, SlotInput.ISBOOK));

		this.addSlot(new SMSlot(this.tile.getOutput(), 0, 43 + addX, 84, (s) -> false));

		this.setPInv(pInv, 12 + addX, 111, -2);
		this.setSlotSize(4);

		// Armor slots
		for (int y = 0; y < 4; y++)
			this.addSlot(new SlotArmor(pInv.player, SMUtil.getEquipmentSlot(y), pInv, 39 - y, -19 + addX, 114 + y * 18));
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		if (this.tile.isCraft) { return false; }

		if (id <= 1) {
			this.tile.clickLevelButton(id);
		}

		else {
			this.tile.craftStart(id - 2);
		}

		return true;
	}
}
