package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.api.iitem.IFood;
import sweetmagic.init.MenuInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.tile.slot.CookedItemSlot;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.slot.SlotInput;
import sweetmagic.init.tile.sm.TileFreezer;

public class FreezerMenu extends BaseSMMenu {

	public final TileFreezer tile;

	public FreezerMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, MenuInit.getTile(TileFreezer::new, pInv, data));
	}

	public FreezerMenu(int windowId, Inventory pInv, TileFreezer tile) {
		super(MenuInit.freezerMenu, windowId, pInv, tile);
		this.tile = tile;

		IItemHandler input = this.tile.getInput();

		for (int y = 0; y < 2; y++)
			this.addSlot(new SMSlot(this.tile.getIce(), y, 8, 6 + y * 18, (s) -> false));

		this.addSlot(new SMSlot(this.tile.getHand(), 0, 62, 11));

		for (int x = 0; x < 2; x++)
			for (int y = 0; y < 3; y++)
				this.addSlot(new SMSlot(input, y + x * 3, 53 + x * 18, 44 + y * 18));

		this.addSlot(new SMSlot(this.tile.getBucket(), 0, 8, 78, SlotInput.ISBUCKET));

		for (int y = 0; y < 4; y++)
			this.addSlot(new CookedItemSlot(pInv.player, this.tile.getOutput(), y, 134, 8 + y * 18, (s) -> false));

		this.setPInv(pInv, 8, 103);
		this.setSlotSize(1 + this.tile.getInvSize() + this.tile.getOutSize() + 1 + 2);
	}

	public void removed(Player player) {
		super.removed(player);
		this.tile.playSound(this.tile.getBlockPos(), SoundInit.FREEZER_CLOSE, 0.2F, this.rand.nextFloat() * 0.1F + 0.9F);
	}

	public void quickMoveStack(Player player, Slot slot, ItemStack oldStack, ItemStack newStack) {
		if (!(slot instanceof CookedItemSlot cook) || !(oldStack.getItem() instanceof IFood)) { return; }

		ItemStack stack = oldStack.copy();
		stack.setCount(oldStack.getCount() - newStack.getCount());
		cook.onQuickCraft(stack, stack.getCount());
	}
}
