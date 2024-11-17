package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.CookedItemSlot;
import sweetmagic.init.tile.slot.SlotInput;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.sm.TileJuiceMaker;

public class JuiceMakerMenu extends BaseSMMenu {

	public final TileJuiceMaker tile;

    public JuiceMakerMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
        this(windowId, pInv, (TileJuiceMaker) MenuInit.getTile(pInv, data));
    }

	public JuiceMakerMenu(int windowId, Inventory pInv, TileJuiceMaker tile) {
		super(MenuInit.juiceMakerMenu, windowId, pInv, tile);
		this.tile = tile;

		IItemHandler input = this.tile.getInput();
		this.addSlot(new SMSlot(this.tile.getBucket(), 0, 8, 78, SlotInput.ISBUCKET));
		this.addSlot(new SMSlot(this.tile.getHand(), 0, 71, 8, (s) -> true));

		for (int y = 0; y < 3; y++)
			this.addSlot(new SMSlot(input, y, 71, 44 + y * 18, (s) -> true));

		for (int y = 0; y < 4; y++)
			this.addSlot(new CookedItemSlot(pInv.player, this.tile.getOutput(), y, 134, 8 + y * 18, (s) -> false));

		this.setPInv(pInv, 8, 103);
		this.setSlotSize(1 + 1 + this.tile.getInvSize() + this.tile.getOutSize());
	}
}
