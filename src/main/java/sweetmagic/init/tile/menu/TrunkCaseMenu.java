package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.inventory.SMTrunkCaseInventory;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.util.ItemHelper;

public class TrunkCaseMenu extends BaseItemMenu {

    public TrunkCaseMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
        this(windowId, pInv, pInv.player.getMainHandItem());
    }

	public TrunkCaseMenu(int windowId, Inventory pInv, ItemStack stack) {
		super(MenuInit.trankCaseMenu, windowId, pInv, new SMTrunkCaseInventory(stack));

		for (int y = 0; y < 8; y++)
			for (int x = 0; x < 13; x++)
			this.addSlot(new SMSlot(this.inventory, x + y * 13, 12 + x * 18, 5 + y * 18));

		this.setPInv(pInv, 48, 151, 1);
	}

	public void removed(Player player) {
		super.removed(player);
		RandomSource rand = player.level.random;
		player.playSound(SoundEvents.WOODEN_TRAPDOOR_CLOSE, 0.5F, rand.nextFloat() * 0.1F + 0.9F);
	}

	@Override
	public void clicked(int slotId, int dragType, ClickType clickType, Player player) {

		if (slotId == -999 || this.slots.size() >= slotId + 36 || slotId < 0) {
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
		switch (id) {
		case 0:
			ItemHelper.compactInventory(this.inventory);
			break;
		case 1:
			ItemHelper.inventoryInput(player, this.inventory);
			break;
		case 2:
			ItemHelper.inventoryOutput(player, this.inventory);
			break;
		}

		RandomSource rand = player.level.random;
		player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.15F, rand.nextFloat() * 0.1F + 0.9F);
		return true;
	}
}
