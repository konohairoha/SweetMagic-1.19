package sweetmagic.plugin.jei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.util.ISMTip;
import sweetmagic.recipe.base.AbstractRecipe;
public abstract class AbstractRecipeCategory implements ISMTip {

	protected static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_smcraft.png");
	protected static final ResourceLocation MISC = SweetMagicCore.getSRC("textures/gui/gui_misc.png");
	protected IDrawable recipeType;

	public Component getTitle(String name) {
		return this.getTip("jei.sweetmagic." + name);
	}

	public void setSMRecipe(IRecipeLayoutBuilder build, AbstractRecipe recipe, IFocusGroup focusGroup) {

		List<Ingredient> ingredList = recipe.getIngredList();
		ItemStack resultStack = recipe.getResultItem();

		List<ItemStack> handList = new ArrayList<>();
		int handCount = recipe.getCountList().get(0);

		for (ItemStack hand : ingredList.get(0).getItems()) {
			ItemStack copy = hand.copy();
			copy.setCount(handCount);
			handList.add(copy);
		}

		build.addSlot(RecipeIngredientRole.INPUT, 49, 22).addItemStacks(handList);

		for (int i = 1; i < ingredList.size(); i++) {

			int x = 8 + (i - 1) * 18;
			int count = recipe.getCountList().get(i);
			List<ItemStack> stackList = new ArrayList<>();
			List<ItemStack> ingStackList = Arrays.asList(ingredList.get(i).getItems());

			for (ItemStack stack : ingStackList) {
				ItemStack copy = stack.copy();
				copy.setCount(count);
				stackList.add(copy);
			}

			build.addSlot(RecipeIngredientRole.INPUT, x, 110).addItemStacks(stackList);
		}

		build.addSlot(RecipeIngredientRole.OUTPUT, 109, 22).addItemStack(resultStack);
	}

	public void drawText(AbstractRecipe recipe, PoseStack pose) {
		Font font = Minecraft.getInstance().font;
		font.drawShadow(pose, this.getText("right_click"), 10, 7, 0xFFFFFF);
		font.drawShadow(pose, this.getText("inv_in"), 8, 96, 0xFFA000);
	}
}
