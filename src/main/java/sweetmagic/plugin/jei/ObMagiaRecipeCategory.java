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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.recipe.obmagia.ObMagiaRecipe;

public class ObMagiaRecipeCategory extends AbstractRecipeCategory implements IRecipeCategory<ObMagiaRecipe> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_obmagia.png");
	private final IDrawableAnimated arrow;
	private final IDrawable background;
	private final IDrawable icon;

	public ObMagiaRecipeCategory(IGuiHelper helper) {
		this.background = helper.createDrawable(TEX, 0, 0, 178, 109);
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockInit.obmagia));
		this.arrow = helper.drawableBuilder(TEX, 193, 1, 33, 13).buildAnimated(60, IDrawableAnimated.StartDirection.LEFT, false);
	}

	@Override
	public RecipeType<ObMagiaRecipe> getRecipeType() {
		return SMJeiPlugin.OBMAGIA;
	}

	@Override
	public Component getTitle() {
		return this.getTitle("obmagia");
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, ObMagiaRecipe recipe, IFocusGroup focusGroup) {

		Ingredient pageList = recipe.getPageList();
		Ingredient baseList = recipe.getBaseList();
		List<Ingredient> ingredList = recipe.getIngredList();
		ItemStack resultStack = recipe.getResultItem();

		this.addSlot(builder, 126, 5, pageList);
		this.addSlot(builder, 106, 5, baseList);

		this.addSlotList(builder, 43, 49, recipe, ingredList, 0);
		this.addSlotList(builder, 43, 11, recipe, ingredList, 1);
		this.addSlotList(builder, 71, 21, recipe, ingredList, 2);
		this.addSlotList(builder, 80, 49, recipe, ingredList, 3);
		this.addSlotList(builder, 71, 76, recipe, ingredList, 4);
		this.addSlotList(builder, 43, 86, recipe, ingredList, 5);
		this.addSlotList(builder, 16, 76, recipe, ingredList, 6);
		this.addSlotList(builder,  6, 49, recipe, ingredList, 7);
		this.addSlotList(builder, 16, 21, recipe, ingredList, 8);

		builder.addSlot(RecipeIngredientRole.OUTPUT, 148, 47).addItemStack(resultStack);
	}

	public void addSlotList (IRecipeLayoutBuilder builder, int x, int y, ObMagiaRecipe recipe, List<Ingredient> ingredList, int count) {

		if(count >= ingredList.size()) { return; }

		Ingredient ing = ingredList.get(count);
		List<ItemStack> stackList = new ArrayList<>();
		int handCount = recipe.getCountList().get(count);

		for (ItemStack stack : ing.getItems()) {
			ItemStack copy = stack.copy();
			copy.setCount(handCount);
			stackList.add(copy);
		}

		this.addSlot(builder, x, y, stackList);
	}

	public void addSlot (IRecipeLayoutBuilder build, int x, int y, Ingredient ing) {

		if (ing.isEmpty()) { return; }

		build.addSlot(RecipeIngredientRole.INPUT, x, y).addItemStacks(Arrays.asList(ing.getItems()));
	}

	public void addSlot (IRecipeLayoutBuilder builder, int x, int y, List<ItemStack> stackList) {

		if (stackList.isEmpty()) { return; }

		builder.addSlot(RecipeIngredientRole.INPUT, x, y).addItemStacks(stackList);
	}

	@Override
	public void draw(ObMagiaRecipe recipe, IRecipeSlotsView slotsView, PoseStack pose, double mouseX, double mouseY) {
		this.arrow.draw(pose, 105, 49);
		Font font = Minecraft.getInstance().font;
		font.drawShadow(pose, this.getTipArray(this.getText("craft_time"), ":" + ((float) (recipe.getCraftTime()) / 2F) + "s"), 107, 76, 0xFFFFFF);
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
