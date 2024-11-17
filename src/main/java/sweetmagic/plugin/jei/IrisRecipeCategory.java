package sweetmagic.plugin.jei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
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
import sweetmagic.recipe.iris.IrisRecipe;

public class IrisRecipeCategory extends AbstractRecipeCategory implements IRecipeCategory<IrisRecipe> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_iris_creation.png");
	private final IDrawableAnimated arrow;
	private final IDrawable background;
	private final IDrawable icon;

	public IrisRecipeCategory(IGuiHelper helper) {
		this.background = helper.createDrawable(TEX, 4, 4, 168, 88);
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockInit.iris_creation));
		this.arrow = helper.drawableBuilder(TEX, 55, 176, 60, 32).buildAnimated(50, IDrawableAnimated.StartDirection.LEFT, false);
	}

	@Override
	public RecipeType<IrisRecipe> getRecipeType() {
		return SMJeiPlugin.IRIS;
	}

	@Override
	public Component getTitle() {
		return this.getTitle("iris");
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder build, IrisRecipe recipe, IFocusGroup focusGroup) {

		List<Ingredient> ingredList = recipe.getIngredList();
		ItemStack resultStack = recipe.getResultItem();

		build.addSlot(RecipeIngredientRole.INPUT, 22, 12).addItemStacks(Arrays.asList(ingredList.get(0).getItems()));

		for (int i = 1; i < ingredList.size(); i++) {

			boolean isSecond = i > 5;

			int x = 49 + (i - 1 - (isSecond ? 4 : 0)) * 18;
			int y = 46 + (isSecond ? 1 : 0);
			int count = recipe.getCountList().get(i);
			List<ItemStack> stackList = new ArrayList<>();
			List<ItemStack> ingStackList = Arrays.asList(ingredList.get(i).getItems());

			for (ItemStack stack : ingStackList) {
				ItemStack copy = stack.copy();
				copy.setCount(count);
				stackList.add(copy);
			}

			build.addSlot(RecipeIngredientRole.INPUT, x, y).addItemStacks(stackList);
		}

		build.addSlot(RecipeIngredientRole.OUTPUT, 129, 12).addItemStack(resultStack);
	}

	@Override
	public void draw(IrisRecipe recipe, IRecipeSlotsView slotsView, PoseStack pose, double mouseX, double mouseY) {
		this.arrow.draw(pose, 51, 8);
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
