package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.MenuInit;

public class CompasMenu extends BaseItemMenu {

	public CompasMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, pInv.player.getMainHandItem());
	}

	public CompasMenu(int windowId, Inventory pInv, ItemStack stack) {
		super(MenuInit.compasMenu, windowId, pInv);
	}

	@Override
	public void clicked(int slotId, int dragType, ClickType clickType, Player player) {

		if (slotId >= 36 || slotId <= -1) {
			super.clicked(slotId, dragType, clickType, player);
			return;
		}

		ItemStack stack = this.slots.get(slotId).getItem();
		if (stack.isEmpty() || !stack.is(player.getMainHandItem().getItem())) {
			super.clicked(slotId, dragType, clickType, player);
		}
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		return true;
	}
}
