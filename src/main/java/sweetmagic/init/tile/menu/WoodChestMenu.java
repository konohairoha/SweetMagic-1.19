package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.sm.TileWoodChest;
import sweetmagic.util.ItemHelper;

public class WoodChestMenu extends BaseSMMenu {

	public final TileWoodChest tile;
	public final int data;

	public WoodChestMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, MenuInit.getTile(TileWoodChest::new, pInv, data));
	}

	public WoodChestMenu(int windowId, Inventory pInv, TileWoodChest tile) {
		super(MenuInit.woodChestMenu, windowId, pInv, tile);
		this.tile = tile;
		this.data = this.tile.getData();

		int addY = data == 3 ? 1 : 0;
		IItemHandler fuel = this.tile.getInput();

		for (int y = 0; y < 8; y++)
			for (int x = 0; x < 13; x++)
			this.addSlot(new SMSlot(fuel, x + y * 13, 12 + x * 18, 5 + y * 18));

		this.setPInv(pInv, 48, 151 + addY, 1);
		this.setSlotSize(this.tile.getInvSize());
	}

	public void removed(Player player) {
		super.removed(player);

		SoundEvent sound = null;
		float pitch = 0.9F;

		switch (this.data) {
		case 0:
		case 5:
		case 7:
			sound = SoundEvents.PISTON_CONTRACT;
			break;
		case 1:
			sound = SoundEvents.BARREL_CLOSE;
			break;
		case 2:
			sound = SoundEvents.IRON_DOOR_CLOSE;
			break;
		case 3:
			pitch = 1.4F;
			sound = SoundEvents.BARREL_CLOSE;
			break;
		case 6:
		case 9:
		case 10:
		case 11:
			sound = SoundEvents.WOODEN_TRAPDOOR_CLOSE;
			break;
		case 8:
			sound = SoundEvents.LEVER_CLICK;
			break;
		}

		if (sound != null) {
			this.tile.playSound(this.tile.getBlockPos(), sound, 0.5F, this.rand.nextFloat() * 0.1F + pitch);
		}
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
		case 3:
			this.tile.invTrash(false);
			break;
		}
		return true;
	}
}
