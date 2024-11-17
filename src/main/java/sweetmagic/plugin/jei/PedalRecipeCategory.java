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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.BlockInit;
import sweetmagic.recipe.pedal.PedalRecipe;

public class PedalRecipeCategory extends AbstractRecipeCategory implements IRecipeCategory<PedalRecipe> {

	private final IDrawable background;
	private final IDrawable icon;

	public PedalRecipeCategory(IGuiHelper helper) {
		this.background = helper.createDrawable(TEX, 0, 0, 176, 134);
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockInit.pedestal_creat));
		this.recipeType = helper.createDrawable(TEX, 181, 143, 19, 26);
	}

	@Override
	public RecipeType<PedalRecipe> getRecipeType() {
		return SMJeiPlugin.PEDAL;
	}

	@Override
	public Component getTitle() {
		return this.getTitle("pedal");
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder build, PedalRecipe recipe, IFocusGroup focusGroup) {
		this.setSMRecipe(build, recipe, focusGroup);
	}

	@Override
	public void draw(PedalRecipe recipe, IRecipeSlotsView slotsView, PoseStack pose, double mouseX, double mouseY) {
		this.recipeType.draw(pose, 78, 48);
		this.drawText(recipe, pose);
		int mf = recipe.getMFList().get(0);
		Font font = Minecraft.getInstance().font;
		font.drawShadow(pose, this.getTipArray(this.getText("needmf"), String.format("%,d", mf)), 8, 86, 0xFFA000);
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
