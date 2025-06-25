package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.api.iitem.IFood;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.CookedItemSlot;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.slot.SlotInput;
import sweetmagic.init.tile.sm.TileJuiceMaker;

public class JuiceMakerMenu extends BaseSMMenu {

	public final TileJuiceMaker tile;

	public JuiceMakerMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, MenuInit.getTile(TileJuiceMaker::new, pInv, data));
	}

	public JuiceMakerMenu(int windowId, Inventory pInv, TileJuiceMaker tile) {
		super(MenuInit.juiceMakerMenu, windowId, pInv, tile);
		this.tile = tile;

		IItemHandler input = this.tile.getInput();
		this.addSlot(new SMSlot(this.tile.getBucket(), 0, 8, 78, SlotInput.ISBUCKET));
		this.addSlot(new SMSlot(this.tile.getHand(), 0, 71, 8));

		for (int y = 0; y < 3; y++)
			this.addSlot(new SMSlot(input, y, 71, 44 + y * 18));

		for (int y = 0; y < 4; y++)
			this.addSlot(new CookedItemSlot(pInv.player, this.tile.getOutput(), y, 134, 8 + y * 18, (s) -> false));

		this.setPInv(pInv, 8, 103);
		this.setSlotSize(1 + 1 + this.tile.getInvSize() + this.tile.getOutSize());
	}

	public void quickMoveStack(Player player, Slot slot, ItemStack oldStack, ItemStack newStack) {
		if (!(slot instanceof CookedItemSlot cook) || !(oldStack.getItem() instanceof IFood)) { return; }

		ItemStack stack = oldStack.copy();
		stack.setCount(oldStack.getCount() - newStack.getCount());
		cook.onQuickCraft(stack, stack.getCount());
	}
}
