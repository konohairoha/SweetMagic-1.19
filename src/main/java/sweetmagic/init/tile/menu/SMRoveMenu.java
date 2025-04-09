package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sweetmagic.api.iitem.IPorch;
import sweetmagic.api.iitem.IWand;
import sweetmagic.api.iitem.info.SMToolInfo;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.inventory.SMInventory.SMRobeInventory;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.util.ItemHelper;

public class SMRoveMenu extends BaseItemMenu {

	public SMRoveMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, pInv.player.getItemBySlot(EquipmentSlot.CHEST));
	}

	public SMRoveMenu(int windowId, Inventory pInv, ItemStack stack) {
		super(MenuInit.robeMenu, windowId, pInv, new SMRobeInventory(new SMToolInfo(stack)));

		for (int y = 0; y < 6; y++)
			for (int x = 0; x < 9; x++)
				this.addSlot(new SMSlot(this.inventory, x + y * 9, 8 + x * 18, 18 + y * 18));

		this.setPInv(pInv, 8, 130);
		this.slotSize = 54;
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		switch (id) {
		case 0:
			ItemStack porch = player.getItemBySlot(EquipmentSlot.LEGS);
			((IPorch) porch.getItem()).openGui(player.level, player, porch);
			break;
		case 1:
			ItemStack stack = player.getMainHandItem();
			if ( !(stack.getItem() instanceof IWand wand) ) { break; }

			wand.openGui(player.level, player, stack);
			break;
		case 2:
			ItemHelper.compactInventory(this.inventory.inv);
			Level world = player.level;
			player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.15F, world.random.nextFloat() * 0.1F + 0.9F);
			this.inventory.writeBack();
			break;
		}

		return true;
	}
}
