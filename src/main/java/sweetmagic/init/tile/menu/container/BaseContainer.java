package sweetmagic.init.tile.menu.container;

import org.antlr.v4.runtime.misc.NotNull;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.tile.menu.CleroMenu;
import sweetmagic.init.tile.menu.CompasMenu;
import sweetmagic.init.tile.menu.FurnitureCraftMenu;
import sweetmagic.init.tile.menu.MagicBookMenu;
import sweetmagic.init.tile.menu.PhoneMenu;
import sweetmagic.init.tile.menu.SMBookMenu;
import sweetmagic.init.tile.menu.SMPorchMenu;
import sweetmagic.init.tile.menu.SMRoveMenu;
import sweetmagic.init.tile.menu.SMWandMenu;
import sweetmagic.init.tile.menu.TrunkCaseMenu;
import sweetmagic.init.tile.sm.TileFurnitureTable;

public interface BaseContainer extends MenuProvider {
	ItemStack stack();

	@NotNull
	@Override
	default Component getDisplayName() {
		return this.stack().getHoverName();
	}

	public record ContainerBook(ItemStack stack) implements BaseContainer {

		@NotNull
		@Override
		public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory pInv, @NotNull Player player) {
			return new SMBookMenu(windowId, pInv, ContainerLevelAccess.create(player.getCommandSenderWorld(), player.blockPosition()));
		}
	}

	public record ContainerClero(ItemStack stack) implements BaseContainer {

		@NotNull
		@Override
		public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory pInv, @NotNull Player player) {
			return new CleroMenu(windowId, pInv, pInv.player.getMainHandItem());
		}
	}

	public record ContainerCompas(ItemStack stack) implements BaseContainer {

		@NotNull
		@Override
		public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory pInv, @NotNull Player player) {
			return new CompasMenu(windowId, pInv, pInv.player.getMainHandItem());
		}
	}

	public record ContainerMagicBook(ItemStack stack) implements BaseContainer {

		@NotNull
		@Override
		public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory pInv, @NotNull Player player) {
			return new MagicBookMenu(windowId, pInv, pInv.player.getMainHandItem());
		}
	}

	public record ContainerPhone(ItemStack stack) implements BaseContainer {

		@NotNull
		@Override
		public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory pInv, @NotNull Player player) {
			return new PhoneMenu(windowId, pInv, pInv.player.getMainHandItem());
		}
	}

	public record ContainerPorch(ItemStack stack) implements BaseContainer {

		@NotNull
		@Override
		public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory pInv, @NotNull Player player) {
			return new SMPorchMenu(windowId, pInv, pInv.player.getItemBySlot(EquipmentSlot.LEGS));
		}
	}

	public record ContainerRobe(ItemStack stack) implements BaseContainer {

		@NotNull
		@Override
		public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory pInv, @NotNull Player player) {
			return new SMRoveMenu(windowId, pInv, pInv.player.getItemBySlot(EquipmentSlot.CHEST));
		}
	}

	public record ContainerWand(ItemStack stack) implements BaseContainer {

		@NotNull
		@Override
		public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory pInv, @NotNull Player player) {
			return new SMWandMenu(windowId, pInv, this.stack);
		}
	}
	public record ContainerTrunkCase(ItemStack stack, int data) implements BaseContainer {

		public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory pInv, @NotNull Player player) {
			return new TrunkCaseMenu(windowId, pInv, pInv.player.getMainHandItem(), data);
		}
	}

	public record ContainerWoodChest(TileFurnitureTable tile) implements MenuProvider {

		@NotNull
		@Override
		public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory pInv, @NotNull Player player) {
			return new FurnitureCraftMenu(windowId, pInv, tile);
		}

		@NotNull
		@Override
		public Component getDisplayName() {
			return Component.translatable("");
		}
	}
}
