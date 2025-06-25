package sweetmagic.plugin.jei;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import sweetmagic.recipe.base.AbstractRecipe;

public class SMRecipes {

	private final RecipeManager manager;

	public SMRecipes() {
		ClientLevel world = Minecraft.getInstance().level;

		if (world != null) {
			this.manager = world.getRecipeManager();
		}

		else {
			throw new NullPointerException("minecraft world must not be null.");
		}
	}

	public <T extends AbstractRecipe> List<T> getRecipe(RecipeType<T> recipe) {
		return this.manager.getAllRecipesFor(recipe).stream().toList();
	}
}
