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
import sweetmagic.recipe.feezer.FreezerRecipe;

public class FreezerRecipeCategory extends AbstractRecipeCategory implements IRecipeCategory<FreezerRecipe> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_freezer.png");
	private final IDrawableAnimated arrow;
	private final IDrawable background;
	private final IDrawable icon;

	public FreezerRecipeCategory(IGuiHelper helper) {
		this.background = helper.createDrawable(TEX, 0, 0, 176, 106);
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockInit.freezer));
		this.recipeType = helper.createDrawable(TEX, 0, 181, 176, 4);
		this.arrow = helper.drawableBuilder(TEX, 211, 14, 22, 15).buildAnimated(60, IDrawableAnimated.StartDirection.LEFT, false);
	}

	@Override
	public RecipeType<FreezerRecipe> getRecipeType() {
		return SMJeiPlugin.FREEZER;
	}

	@Override
	public Component getTitle() {
		return this.getTitle("freezer");
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder build, FreezerRecipe recipe, IFocusGroup focusGroup) {

		List<Ingredient> ingredList = recipe.getIngredList();
		List<ItemStack> resultList = recipe.getResultList();

		int handCount = recipe.getCountList().get(0);
		List<ItemStack> handList = new ArrayList<>();
		List<ItemStack> ingHandList = Arrays.asList(ingredList.get(0).getItems());

		for (ItemStack stack : ingHandList) {
			ItemStack copy = stack.copy();
			copy.setCount(handCount);
			handList.add(copy);
		}

		build.addSlot(RecipeIngredientRole.INPUT, 62, 11).addItemStacks(handList);

		for (int i = 1; i < ingredList.size(); i++) {

			int value = i -1;
			boolean isSecond = value >= 3;

			int x = 53 + (isSecond ? 1 : 0) * 18;
			int y = 44 + (isSecond ? value - 3 : value) * 18;
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

		for (int i = 0; i < resultList.size(); i++) {
			ItemStack stack = resultList.get(i);
			build.addSlot(RecipeIngredientRole.OUTPUT, 134, 8 + i * 18).addItemStack(stack);
		}
	}

	@Override
	public void draw(FreezerRecipe recipe, IRecipeSlotsView slotsView, PoseStack pose, double mouseX, double mouseY) {
		this.recipeType.draw(pose, 0, 102);
		this.arrow.draw(pose, 99, 36);
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
