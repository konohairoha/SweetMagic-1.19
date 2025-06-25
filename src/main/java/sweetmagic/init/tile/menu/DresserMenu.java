package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.MagiaSlot;
import sweetmagic.init.tile.slot.SlotInput;
import sweetmagic.init.tile.sm.TileDresser;
import sweetmagic.util.ItemHelper;

public class DresserMenu extends BaseSMMenu {

	public final TileDresser tile;

	public DresserMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, MenuInit.getTile(TileDresser::new, pInv, data));
	}

	public DresserMenu(int windowId, Inventory pInv, TileDresser tile) {
		super(MenuInit.dresserMenu, windowId, pInv, tile);
		this.tile = tile;
		IItemHandler fuel = this.tile.getInput();

		for (int y = 0; y < 6; y++)
			for (int x = 0; x < 9; x++)
				this.addSlot(new MagiaSlot(tile, fuel, x + y * 9, 8 + x * 18, 8 + y * 18, SlotInput.ISSM_ACC));

		this.setPInv(pInv, 8, 120);
		this.setSlotSize(this.tile.getInvSize());
	}

	public void removed(Player player) {
		super.removed(player);
		this.tile.playSound(this.tile.getBlockPos(), SoundEvents.BARREL_CLOSE, 0.5F, this.rand.nextFloat() * 0.1F + 0.9F);
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		switch (id) {
		case 0:
			ItemHelper.compactInventory(this.tile.inputInv);
			this.tile.clickButton();
			break;
		case 1:
			ItemHelper.inventoryInput(player, this.tile.inputInv);
			this.tile.clickButton();
			break;
		case 2:
			ItemHelper.inventoryOutput(player, this.tile.inputInv);
			this.tile.clickButton();
			break;
		}
		return true;
	}

	protected boolean moveItemStackTo(ItemStack stack, int slotStart, int slotEnd, boolean par1) {
		return this.moveSlot(stack, slotStart, slotEnd, par1);
	}

	public boolean checkStack(ItemStack stack) {
		return true;
	}
}
