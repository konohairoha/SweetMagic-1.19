package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.sm.TileAquariumPot;
import sweetmagic.util.PlayerHelper;

public class AquariumPotMenu extends BaseSMMenu {

	public final TileAquariumPot tile;

    public AquariumPotMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
        this(windowId, pInv, (TileAquariumPot) MenuInit.getTile(pInv, data));
    }

	public AquariumPotMenu(int windowId, Inventory pInv, TileAquariumPot tile) {
		super(MenuInit.aquariumPotMenu, windowId, pInv, tile);
		this.tile = tile;

		this.addSlot(new SMSlot(this.tile.getInput(), 0, 44, 28));
		this.setPInv(pInv, 8, 90);
		this.setSlotSize(1);
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		int level = player.experienceLevel;
		if(level <= 0) { return true; }

		int befEXP = PlayerHelper.getExpValue(player);
		player.experienceLevel -= id == 1 ? Math.min(level, 10) : 1;
		player.totalExperience = PlayerHelper.getExpValue(player);
		int aftEXP = PlayerHelper.getExpValue(player);
		this.tile.setMF(this.tile.getMF() + (befEXP + aftEXP) * (12 + this.tile.getStackCount()) );

		this.tile.clickButton();
		this.tile.playSound(this.tile.getBlockPos(), SoundEvents.ITEM_PICKUP, 0.5F, 1F);

		return true;
	}
}
