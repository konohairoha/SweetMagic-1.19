package sweetmagic.plugin.jei;

import java.util.ArrayList;
import java.util.Arrays;
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
import sweetmagic.recipe.recycler.RecyclerRecipe;

public class RecyclerRecipeCategory extends AbstractRecipeCategory implements IRecipeCategory<RecyclerRecipe> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_mill.png");
	private final IDrawable background;
	private final IDrawable icon;

	public RecyclerRecipeCategory(IGuiHelper helper) {
		this.background = helper.createDrawable(TEX, 0, 0, 176, 106);
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockInit.aether_recycler));
		this.recipeType = helper.createDrawable(TEX, 0, 110, 21, 21);
	}

	@Override
	public RecipeType<RecyclerRecipe> getRecipeType() {
		return SMJeiPlugin.RECYCLER;
	}

	@Override
	public Component getTitle() {
		return this.getTitle("recycler");
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder build, RecyclerRecipe recipe, IFocusGroup focusGroup) {

		List<Ingredient> ingredList = recipe.getIngredList();
		List<Ingredient> resultList = recipe.getResultIngList();
		List<Float> chancList = recipe.getChanceList();

		build.addSlot(RecipeIngredientRole.INPUT, 49, 31).addItemStacks(Arrays.asList(ingredList.get(0).getItems()));

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

		List<ItemStack> resultStack0List = Arrays.asList(resultList.get(0).getItems());
		String chance0 = String.format("%.2f", ( chancList.isEmpty() ? 1F : chancList.get(0) ) * 100F);
		build.addSlot(RecipeIngredientRole.OUTPUT, 109, 31).addItemStacks(resultStack0List)
		.addTooltipCallback((slotView, tooltip) -> {
			tooltip.add(1, this.getTipArray(this.getText("chancedrop"), "：" + chance0 + "%").withStyle(GOLD));
		});

		for (int i = 1; i < resultList.size(); i++) {

			int addY = (i -1) * 18;
			String chance = String.format("%.2f", ( chancList.isEmpty() ? 1F : chancList.get(i) ) * 100F);
			List<ItemStack> resultStackList = Arrays.asList(resultList.get(i).getItems());

			build.addSlot(RecipeIngredientRole.OUTPUT, 147, 9 + addY).addItemStacks(resultStackList)
				.addTooltipCallback((slotView, tooltip) -> {
					tooltip.add(1, this.getTipArray(this.getText("chancedrop"), "：" + chance + "%").withStyle(GOLD));
				});
		}
	}

	@Override
	public void draw(RecyclerRecipe recipe, IRecipeSlotsView slotsView, PoseStack pose, double mouseX, double mouseY) {
		this.recipeType.draw(pose, 76, 57);
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
