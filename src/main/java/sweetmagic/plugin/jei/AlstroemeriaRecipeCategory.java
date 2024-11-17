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
import sweetmagic.recipe.alstrameria.AlstroemeriaRecipe;

public class AlstroemeriaRecipeCategory extends AbstractRecipeCategory implements IRecipeCategory<AlstroemeriaRecipe> {

	private final IDrawable background;
	private final IDrawable icon;

	public AlstroemeriaRecipeCategory(IGuiHelper helper) {
		this.background = helper.createDrawable(TEX, 0, 0, 176, 134);
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockInit.twilight_alstroemeria));
		this.recipeType = helper.createDrawable(TEX, 25, 143, 35, 41);
	}

	@Override
	public RecipeType<AlstroemeriaRecipe> getRecipeType() {
		return SMJeiPlugin.ALSTROEMERIA;
	}

	@Override
	public Component getTitle() {
		return this.getTitle("alstroemeria");
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder build, AlstroemeriaRecipe recipe, IFocusGroup focusGroup) {
		this.setSMRecipe(build, recipe, focusGroup);
	}

	@Override
	public void draw(AlstroemeriaRecipe recipe, IRecipeSlotsView slotsView, PoseStack pose, double mouseX, double mouseY) {
		this.recipeType.draw(pose, 70, 39);
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
