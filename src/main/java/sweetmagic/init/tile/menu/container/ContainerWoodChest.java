package sweetmagic.init.tile.menu.container;

import org.antlr.v4.runtime.misc.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import sweetmagic.init.tile.menu.WoodChestLootMenu;
import sweetmagic.init.tile.sm.TileWoodChest;

public record ContainerWoodChest(BlockPos pos) implements MenuProvider {

	@NotNull
	@Override
	public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory pInv, @NotNull Player player) {
		return new WoodChestLootMenu(windowId, pInv, (TileWoodChest) player.level.getBlockEntity(pos));
	}

	@NotNull
	@Override
	public Component getDisplayName() {
		return Component.translatable("");
	}
}
