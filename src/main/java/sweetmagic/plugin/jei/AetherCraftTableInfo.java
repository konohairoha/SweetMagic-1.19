package sweetmagic.plugin.jei;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;

import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.CraftingRecipe;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.menu.AetherCraftTableMenu;

public class AetherCraftTableInfo implements IRecipeTransferInfo<AetherCraftTableMenu, CraftingRecipe> {

	@Nonnull
	@Override
	public Class<AetherCraftTableMenu> getContainerClass() {
		return AetherCraftTableMenu.class;
	}

	@Override
	public List<Slot> getInventorySlots(AetherCraftTableMenu menu, CraftingRecipe recipe) {
		int size = menu.slots.size();
		List<Slot> slotList = IntStream.range(size - 35, size).mapToObj(menu::getSlot).collect(Collectors.toList());
		slotList.addAll(menu.sortSlotId().stream().filter(s -> !s.getItem().isEmpty()).toList());
		return slotList;
	}

	@Override
	public Optional<MenuType<AetherCraftTableMenu>> getMenuType() {
		return Optional.of(MenuInit.aetherCraftTableMenu);
	}

	@Override
	public RecipeType<CraftingRecipe> getRecipeType() {
		return RecipeTypes.CRAFTING;
	}

	@Override
	public List<Slot> getRecipeSlots(AetherCraftTableMenu container, CraftingRecipe recipe) {
		int size = container.slots.size();
		return IntStream.range(size - 45, size - 36).mapToObj(container::getSlot).collect(Collectors.toList());
	}

	@Override
	public boolean canHandle(AetherCraftTableMenu container, CraftingRecipe recipe) {
		return true;
	}
}
