package sweetmagic.plugin.jei;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.recipe.furniture.FurnitureRecipe;

public class FurnitureRecipeCategory extends AbstractRecipeCategory implements IRecipeCategory<FurnitureRecipe> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_furniture.png");
	private final IDrawable background;
	private final IDrawable icon;

	public FurnitureRecipeCategory(IGuiHelper helper) {
		this.background = helper.createDrawable(TEX, 0, 0, 94, 34);
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockInit.furniture_processing_table));
	}

	@Override
	public RecipeType<FurnitureRecipe> getRecipeType() {
		return SMJeiPlugin.FURNITURE;
	}

	@Override
	public Component getTitle() {
		return this.getTitle("furniture");
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder build, FurnitureRecipe recipe, IFocusGroup focusGroup) {

		List<Ingredient> ingredList = recipe.getIngredList();
		ItemStack resultStack = recipe.getResultItem();

		List<ItemStack> handList = new ArrayList<>();

		for (ItemStack hand : ingredList.get(0).getItems()) {
			ItemStack copy = hand.copy();
			handList.add(copy);
		}

		build.addSlot(RecipeIngredientRole.INPUT, 9, 9).addItemStacks(handList);
		build.addSlot(RecipeIngredientRole.OUTPUT, 69, 9).addItemStack(resultStack);
	}

	@Override
	public void draw(FurnitureRecipe recipe, IRecipeSlotsView slotsView, PoseStack pose, double mouseX, double mouseY) { }

	@Override
	public IDrawable getBackground() {
		return this.background;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}
}
