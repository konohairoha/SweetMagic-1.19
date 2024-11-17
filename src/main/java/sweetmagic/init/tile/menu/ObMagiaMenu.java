package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.slot.SlotInput;
import sweetmagic.init.tile.sm.TileObMagia;

public class ObMagiaMenu extends BaseSMMenu {

	public final TileObMagia tile;
	public final Slot pageSlot;
	public final Slot baseSlot;
	public final Slot outSlot;

    public ObMagiaMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
        this(windowId, pInv, (TileObMagia) MenuInit.getTile(pInv, data));
    }

	public ObMagiaMenu(int windowId, Inventory pInv, TileObMagia tile) {
		super(MenuInit.obMagiaMenu, windowId, pInv, tile);
		this.tile = tile;

		int addX = 27;

		IItemHandler input = this.tile.getInput();
		IItemHandler side = this.tile.getSide();
		this.baseSlot = this.addSlot(new SMSlot(this.tile.getBase(), 0, 106 + addX, 5, SlotInput.ISSM_BASE));
		this.pageSlot = this.addSlot(new SMSlot(this.tile.getpage(), 0, 126 + addX, 5, SlotInput.ISSM_PAGE));

		this.addSlot(new SMSlot(this.tile.getHand(), 0, 43 + addX, 49));

		this.addSlot(new SMSlot(input, 0, 43 + addX, 11));
		this.addSlot(new SMSlot(input, 1, 71 + addX, 21));
		this.addSlot(new SMSlot(input, 2, 80 + addX, 49));
		this.addSlot(new SMSlot(input, 3, 71 + addX, 76));
		this.addSlot(new SMSlot(input, 4, 43 + addX, 86));
		this.addSlot(new SMSlot(input, 5, 16 + addX, 76));
		this.addSlot(new SMSlot(input, 6,  6 + addX, 49));
		this.addSlot(new SMSlot(input, 7, 16 + addX, 21));

		for (int y = 0; y < 9; y++)
			this.addSlot(new SMSlot(side, y, -19 + addX, 16 + y * 18));

		this.outSlot = this.addSlot(new SMSlot(this.tile.getOutput(), 0, 147 + addX, 48, s -> false));

		this.setPInv(pInv, 12 + addX, 111, -2);
		this.setSlotSize(this.tile.getInvSize() + 4 + 9);
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		if (this.tile.isCraft || !this.tile.canCraft) { return false; }

		// レシピが見つかれば作成開始
		this.tile.craftStart();
		return true;
	}
}
