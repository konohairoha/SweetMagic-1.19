package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.slot.SlotArmor;
import sweetmagic.init.tile.slot.SlotInput;
import sweetmagic.init.tile.sm.TileMagiaRewrite;
import sweetmagic.util.SMUtil;

public class MagiaRewriteMenu extends BaseSMMenu {

	public final TileMagiaRewrite tile;
	public final Slot bookSlot;
	public final Slot magicBookSlot;

    public MagiaRewriteMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
        this(windowId, pInv, (TileMagiaRewrite) MenuInit.getTile(pInv, data));
    }

	public MagiaRewriteMenu(int windowId, Inventory pInv, TileMagiaRewrite tile) {
		super(MenuInit.magiaRewriteMenu, windowId, pInv, tile);
		this.tile = tile;

		int addX = 24;

		this.magicBookSlot = this.addSlot(new SMSlot(this.tile.getBook(), 0, 76 + addX, 8, SlotInput.ISREWRITE));
		this.bookSlot = this.addSlot(new SMSlot(this.tile.getInput(), 0, 44 + addX, 24, SlotInput.ISENCHA));

		this.addSlot(new SMSlot(this.tile.getOut(), 0, 43 + addX, 84, (s) -> false));

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
