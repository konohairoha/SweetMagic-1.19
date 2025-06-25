package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.sm.TileMagiaDrawer;
import sweetmagic.util.ItemHelper;

public class MagiaDrawerMenu extends BaseSMMenu {

	public final TileMagiaDrawer tile;

	public MagiaDrawerMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, MenuInit.getTile(TileMagiaDrawer::new, pInv, data));
	}

	public MagiaDrawerMenu(int windowId, Inventory pInv, TileMagiaDrawer tile) {
		super(MenuInit.magiaDrawerMenu, windowId, pInv, tile);
		this.tile = tile;

		for (int y = 0; y < 8; y++)
			for (int x = 0; x < 13; x++)
				this.addSlot(new SMSlot(this.tile.getInput(), x + y * 13, 5 + x * 18, 13 + y * 18));

		this.setPInv(pInv, 41, 159, 1);
		this.setSlotSize(this.tile.getInvSize());
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {

		switch (id) {
		case 4:
			ItemHelper.compactInventory(this.tile.inputInv);
			this.tile.clickButton();
			break;
		case 5:
			ItemHelper.inventoryInput(player, this.tile.inputInv);
			this.tile.clickButton();
			break;
		case 6:
			ItemHelper.inventoryOutput(player, this.tile.inputInv);
			this.tile.clickButton();
			break;
		}

		if (id < 4) {
			this.tile.addRange(id);
		}

		return true;
	}

	public void removed(Player player) {
		super.removed(player);
		this.tile.playSound(this.tile.getBlockPos(), SoundEvents.PISTON_CONTRACT, 0.5F, this.rand.nextFloat() * 0.1F + 0.9F);
	}
}
