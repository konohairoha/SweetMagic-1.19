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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.recipe.tank.TankRecipe;

public class TankRecipeCategory extends AbstractRecipeCategory implements IRecipeCategory<TankRecipe> {

	protected static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_mftank.png");
	private final IDrawable background;
	private final IDrawable icon;

	public TankRecipeCategory(IGuiHelper helper) {
		this.background = helper.createDrawable(TEX, 4, 20, 169, 77);
		this.recipeType = helper.createDrawable(TEX, 0, 185, 106, 8);
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockInit.mftank));
	}

	@Override
	public RecipeType<TankRecipe> getRecipeType() {
		return SMJeiPlugin.TANK;
	}

	@Override
	public Component getTitle() {
		return this.getTitle("tank");
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder build, TankRecipe recipe, IFocusGroup focusGroup) {

		List<Ingredient> ingredList = recipe.getIngredList();
		ItemStack resultStack = recipe.getResultItem();
		List<ItemStack> handList = new ArrayList<>();
		int handCount = recipe.getCountList().get(0);

		for (ItemStack hand : ingredList.get(0).getItems()) {
			ItemStack copy = hand.copy();
			copy.setCount(handCount);
			handList.add(copy);
		}

		build.addSlot(RecipeIngredientRole.INPUT, 76, 3).addItemStacks(handList);
		build.addSlot(RecipeIngredientRole.OUTPUT, 58, 54).addItemStack(resultStack);
	}

	@Override
	public void draw(TankRecipe recipe, IRecipeSlotsView slotsView, PoseStack pose, double mouseX, double mouseY) {
		this.recipeType.draw(pose, 31, 29);
		int mf = recipe.getMFList().get(0);
		Font font = Minecraft.getInstance().font;
		font.drawShadow(pose, this.getTipArray(this.getText("needmf"), String.format("%,d", mf)), 8, 42, 0xFFA000);
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
