package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.slot.SlotInput;
import sweetmagic.init.tile.sm.TileMFChanger;
import sweetmagic.init.tile.sm.TileSMMagic;

public class MFChangerMenu extends BaseSMMenu {

	public final TileSMMagic tile;

	public MFChangerMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, MenuInit.getTile(TileMFChanger::new, pInv, data));
	}

	public MFChangerMenu(int windowId, Inventory pInv, TileMFChanger tile) {
		super(MenuInit.changerMenu, windowId, pInv, tile);
		this.tile = tile;

		this.setSlot();

		this.setPInv(pInv, 8, 70);
		this.setSlotSize(this.tile.getInvSize());
	}

	public void setSlot () {

		IItemHandler input = this.tile.getInput();

		switch (this.tile.getInvSize()) {
		case 3:
			for (int i = 0; i < 3; i++)
				this.addSlot(new SMSlot(input, i, 62 + i * 18, 27, SlotInput.HASMF));
			break;
		case 5:
			for (int i = 0; i < 5; i++)
				this.addSlot(new SMSlot(input, i, 44 + i * 18, 27, SlotInput.HASMF));
			break;
		case 10:
			for (int i = 0; i < 5; i++)
				for (int y = 0; y < 2; y++)
				this.addSlot(new SMSlot(input, i + y * 5, 44 + i * 18, 9 + y * 18, SlotInput.HASMF));
			break;
		}
	}
}
