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
import sweetmagic.recipe.woodcutter.WoodCutterRecipe;

public class WoddCutterRecipeCategory extends AbstractRecipeCategory implements IRecipeCategory<WoodCutterRecipe> {

	protected static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_mftank.png");
	private final IDrawable background;
	private final IDrawable icon;
	private final IDrawable misc;

	public WoddCutterRecipeCategory(IGuiHelper helper) {
		this.background = helper.createDrawable(TEX, 4, 20, 169, 77);
		this.recipeType = helper.createDrawable(TEX, 0, 185, 106, 8);
		this.misc = helper.createDrawable(MISC, 22, 159, 77, 11);
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockInit.mf_woodcutter));
	}

	@Override
	public RecipeType<WoodCutterRecipe> getRecipeType() {
		return SMJeiPlugin.WOODCUTTER;
	}

	@Override
	public Component getTitle() {
		return this.getTitle("woodcutter");
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder build, WoodCutterRecipe recipe, IFocusGroup focusGroup) {

		List<Ingredient> ingredList = recipe.getIngredList();
		List<ItemStack> resultList = recipe.getRequestList();
		List<ItemStack> handList = new ArrayList<>();
		int handCount = recipe.getCountList().get(0);

		for (ItemStack hand : ingredList.get(0).getItems()) {
			ItemStack copy = hand.copy();
			copy.setCount(handCount);
			handList.add(copy);
		}

		build.addSlot(RecipeIngredientRole.INPUT, 76, 3).addItemStacks(handList);

		for(int i = 0; i < resultList.size(); i++)
			build.addSlot(RecipeIngredientRole.OUTPUT, 58 + i * 18, 54).addItemStack(resultList.get(i));
	}

	@Override
	public void draw(WoodCutterRecipe recipe, IRecipeSlotsView slotsView, PoseStack pose, double mouseX, double mouseY) {
		this.recipeType.draw(pose, 31, 29);
		this.misc.draw(pose, 45, 27);
		int mf = recipe.getMF();
		int min = recipe.getMinList().get(0);
		int max = recipe.getMaxList().get(0);
		Font font = Minecraft.getInstance().font;
		font.drawShadow(pose, this.getTipArray(this.getText("needmf"), this.format(mf)), 95, 2, 0xFFA000);
		font.drawShadow(pose, this.getTipArray(this.getText("generated_count"), min + " - " + max), 95, 12, 0xFFA000);
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
