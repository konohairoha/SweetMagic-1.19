package sweetmagic.plugin.jei;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.BlockInit;
import sweetmagic.recipe.oven.OvenRecipe;

public class OvenRecipeCategory extends AbstractRecipeCategory implements IRecipeCategory<OvenRecipe> {

	private final IDrawable background;
	private final IDrawable icon;

	public OvenRecipeCategory(IGuiHelper helper) {
		this.background = helper.createDrawable(TEX, 0, 0, 176, 134);
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockInit.oven));
		this.recipeType = helper.createDrawable(TEX, 127, 143, 22, 22);
	}

	@Override
	public RecipeType<OvenRecipe> getRecipeType() {
		return SMJeiPlugin.OVEN;
	}

	@Override
	public Component getTitle() {
		return this.getTitle("oven");
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, OvenRecipe recipe, IFocusGroup focusGroup) {
		this.setSMRecipe(builder, recipe, focusGroup);
	}

	@Override
	public void draw(OvenRecipe recipe, IRecipeSlotsView slotsView, PoseStack pose, double mouseX, double mouseY) {
		this.recipeType.draw(pose, 78, 48);
		this.drawText(recipe, pose);
	}

	@Override
	public IDrawable getBackground() {
		return this.background;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}
}
