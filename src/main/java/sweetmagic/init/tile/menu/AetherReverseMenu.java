package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.sm.TileAetherReverse;

public class AetherReverseMenu extends BaseSMMenu {

	public final TileAetherReverse tile;
	public Slot[] craftSlot = new Slot[9];

    public AetherReverseMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
        this(windowId, pInv, (TileAetherReverse) MenuInit.getTile(pInv, data));
    }

	public AetherReverseMenu(int windowId, Inventory pInv, TileAetherReverse tile) {
		super(MenuInit.aetherReverseMenu, windowId, pInv, tile);
		this.tile = tile;

		this.addSlot(new SMSlot(this.tile.getInput(), 0, 37, 26));

		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 3; x++)
				craftSlot[x + y * 3] = this.addSlot(new SMSlot(this.tile.getOut(), x + y * 3, 98 + x * 18, 8 + y * 18, s -> false));

		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 9; x++)
				this.addSlot(new SMSlot(this.tile.getChest(), x + y * 9, 8 + x * 18, 90 + y * 18, s -> false));

		this.setPInv(pInv, 8, 148);
		this.setSlotSize(1 + 9 + 27);
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		if (this.tile.isCraft || !this.tile.canCraft) { return false; }

		// レシピが見つかれば作成開始
		this.tile.craftStart();
		return true;
	}
}
