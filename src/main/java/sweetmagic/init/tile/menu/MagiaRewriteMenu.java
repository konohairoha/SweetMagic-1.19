package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.slot.SlotArmor;
import sweetmagic.init.tile.slot.SlotInput;
import sweetmagic.init.tile.sm.TileMagiaRewrite;
import sweetmagic.util.SMUtil;

public class MagiaRewriteMenu extends BaseSMMenu {

	public final TileMagiaRewrite tile;
	public final Slot bookSlot;
	public final Slot magicBookSlot;
	private Runnable slotUpdateListener = () -> { };

	public MagiaRewriteMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, MenuInit.getTile(TileMagiaRewrite::new, pInv, data));
	}

	public MagiaRewriteMenu(int windowId, Inventory pInv, TileMagiaRewrite tile) {
		super(MenuInit.magiaRewriteMenu, windowId, pInv, tile);
		this.tile = tile;

		int addX = 24;
		int nowLevel = this.tile.nowLevel;

		this.magicBookSlot = this.addSlot(new SMSlot(this.tile.getBook(), 0, 76 + addX, 8, SlotInput.ISREWRITE) {

			public void setChanged() {
				super.setChanged();
				MagiaRewriteMenu.this.slotsChangedSide(this.container);
				MagiaRewriteMenu.this.slotUpdateListener.run();
			};
		});
		this.bookSlot = this.addSlot(new SMSlot(this.tile.getInput(), 0, 44 + addX, 24, SlotInput.ISENCHA));

		this.addSlot(new SMSlot(this.tile.getOut(), 0, 43 + addX, 84, (s) -> false));

		this.setPInv(pInv, 12 + addX, 111, -2);
		this.setSlotSize(4);
		this.tile.nowLevel = nowLevel;
		this.tile.sendPKT();

		// Armor slots
		for (int y = 0; y < 4; y++)
			this.addSlot(new SlotArmor(pInv.player, SMUtil.getEquipmentSlot(y), pInv, 39 - y, -19 + addX, 114 + y * 18));
	}

	// アイテムが変わった時
	public void slotsChangedSide(Container container) {
		this.tile.nowLevel = 1;
		this.tile.sendPKT();
	}

	public void registerUpdateListener(Runnable run) {
		this.slotUpdateListener = run;
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		if (this.tile.isCraft) { return false; }

		if (id <= 1) {
			this.tile.clickLevelButton(id);
		}

		else {
			this.tile.craftStart(id - 2);
		}

		return true;
	}
}
