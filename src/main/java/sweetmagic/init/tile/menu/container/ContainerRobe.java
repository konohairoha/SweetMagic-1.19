package sweetmagic.init.tile.menu.container;

import org.antlr.v4.runtime.misc.NotNull;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.tile.menu.SMRoveMenu;

public record ContainerRobe(ItemStack stack) implements MenuProvider {

	@NotNull
	@Override
	public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory pInv, @NotNull Player player) {
		return new SMRoveMenu(windowId, pInv, pInv.player.getItemBySlot(EquipmentSlot.CHEST));
	}

	@NotNull
	@Override
	public Component getDisplayName() {
		return stack.getHoverName();
	}
}
