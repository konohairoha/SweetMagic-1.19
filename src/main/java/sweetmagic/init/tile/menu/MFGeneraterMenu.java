package sweetmagic.init.tile.menu;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.common.Tags;
import sweetmagic.init.MenuInit;
import sweetmagic.init.TagInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.sm.TileMFGenerater;

public class MFGeneraterMenu extends BaseSMMenu {

	public final TileMFGenerater tile;
	public final List<Slot> stoneSlotList = new ArrayList<>();
	public final Slot bucketSlot;

	public MFGeneraterMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, (TileMFGenerater) MenuInit.getTile(pInv, data));
	}

	public MFGeneraterMenu(int windowId, Inventory pInv, TileMFGenerater tile) {
		super(MenuInit.mfGeneraterMenu, windowId, pInv, tile);
		this.tile = tile;

		for (int y = 0; y < 2; y++) {
			for (int x = 0; x < 6; x++) {
				SMSlot slot= new SMSlot(this.tile.getInput(), x + y * 6, 35 + x * 18, 9 + y * 18, s -> s.is(Tags.Items.COBBLESTONE));
				this.stoneSlotList.add(slot);
				this.addSlot(slot);
			}
		}

		this.bucketSlot = new SMSlot(this.tile.getBucket(), 0, 106, 59, s -> s.is(TagInit.LAVA));
		this.addSlot(this.bucketSlot);

		this.setPInv(pInv, 8, 90);
		this.setSlotSize(this.tile.getInvSize() + 13);
	}
}
