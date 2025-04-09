package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.network.NetworkHooks;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.sm.TileFurnitureTable;

public class FurnitureCraftMenu extends BaseSMMenu {

	public final TileFurnitureTable tile;
	public final Slot resultSlot;

	public FurnitureCraftMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, (TileFurnitureTable) MenuInit.getTile(pInv, data));
	}

	public FurnitureCraftMenu(int windowId, Inventory pInv, TileFurnitureTable tile) {
		super(MenuInit.furnitureCraftMenu, windowId, pInv, tile);
		this.tile = tile;
		this.resultSlot = this.addSlot(new SMSlot(this.tile.getResult(), 0, 54, 24, s -> false));
		this.tile.oldSetCount = this.tile.setCount;
		this.tile.sendPKT();
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {

		int addCount = 0;

		switch (id) {
		case 0:
			addCount += 1;
			break;
		case 1:
			addCount -= 1;
			break;
		case 2:
			addCount += 10;
			break;
		case 3:
			addCount -= 10;
			break;
		case 4:
			addCount += 64;
			break;
		case 5:
			addCount -= 64;
			break;
		case 6:
			if (player instanceof ServerPlayer sePlayer) {
				NetworkHooks.openScreen(sePlayer, this.tile, this.tile.getBlockPos());

				int count = this.tile.outStack.getCount();
				int value = this.tile.setCount / count;
				this.tile.setCount = Math.max(value * count, count);
				this.tile.clickButton();
				this.tile.sendPKT();
			}
		return true;
		case 7:
			if (player instanceof ServerPlayer sePlayer) {
				NetworkHooks.openScreen(sePlayer, this.tile, this.tile.getBlockPos());
				this.tile.setCount = this.tile.oldSetCount;
				this.tile.clickButton();
				this.tile.sendPKT();
			}
		return true;
		}

		this.tile.setCount = Math.min(1024, Math.max(1, this.tile.setCount + addCount));
		this.tile.clickButton();
		this.tile.sendPKT();
		return true;
	}
}
