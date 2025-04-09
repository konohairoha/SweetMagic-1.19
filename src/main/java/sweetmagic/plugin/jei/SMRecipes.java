package sweetmagic.plugin.jei;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import sweetmagic.init.RecipeTypeInit;
import sweetmagic.recipe.alstrameria.AlstroemeriaRecipe;
import sweetmagic.recipe.base.AbstractRecipe;
import sweetmagic.recipe.bottle.BottleRecipe;
import sweetmagic.recipe.feezer.FreezerRecipe;
import sweetmagic.recipe.frypan.FrypanRecipe;
import sweetmagic.recipe.furniture.FurnitureRecipe;
import sweetmagic.recipe.iris.IrisRecipe;
import sweetmagic.recipe.juice_maker.JuiceMakerRecipe;
import sweetmagic.recipe.mill.MillRecipe;
import sweetmagic.recipe.obmagia.ObMagiaRecipe;
import sweetmagic.recipe.oven.OvenRecipe;
import sweetmagic.recipe.pedal.PedalRecipe;
import sweetmagic.recipe.pot.PotRecipe;
import sweetmagic.recipe.recycler.RecyclerRecipe;
import sweetmagic.recipe.tank.TankRecipe;

public class SMRecipes {

	private final RecipeManager recipeManager;

	public SMRecipes() {
		ClientLevel level = Minecraft.getInstance().level;

		if (level != null) {
			this.recipeManager = level.getRecipeManager();
		}

		else {
			throw new NullPointerException("minecraft world must not be null.");
		}
	}

	public List<AlstroemeriaRecipe> getAlstroemeriaRecipe() {
		return this.getRecipe(RecipeTypeInit.ALSTROMERIA);
	}


	public List<ObMagiaRecipe> getObMagiaRecipe() {
		return this.getRecipe(RecipeTypeInit.OBMAGIA);
	}

	public List<IrisRecipe> getIrisRecipe() {
		return this.getRecipe(RecipeTypeInit.IRIS);
	}

	public List<OvenRecipe> getOvenRecipe() {
		return this.getRecipe(RecipeTypeInit.OVEN);
	}

	public List<MillRecipe> getMillRecipe() {
		return this.getRecipe(RecipeTypeInit.MILL);
	}

	public List<BottleRecipe> getBottleRecipe() {
		return this.getRecipe(RecipeTypeInit.BOTTLE);
	}

	public List<FrypanRecipe> getFrypanRecipe() {
		return this.getRecipe(RecipeTypeInit.FRYPAN);
	}

	public List<PotRecipe> getPotRecipe() {
		return this.getRecipe(RecipeTypeInit.POT);
	}

	public List<PedalRecipe> getPedalRecipe() {
		return this.getRecipe(RecipeTypeInit.PEDAL);
	}

	public List<TankRecipe> getTankRecipe() {
		return this.getRecipe(RecipeTypeInit.TANK);
	}

	public List<FreezerRecipe> getFreezerRecipe() {
		return this.getRecipe(RecipeTypeInit.FREEZER);
	}

	public List<JuiceMakerRecipe> getJuiceMakerRecipe() {
		return this.getRecipe(RecipeTypeInit.JUICEMAKER);
	}

	public List<FurnitureRecipe> getFurnitureRecipe() {
		return this.getRecipe(RecipeTypeInit.FURNITURE);
	}

	public List<RecyclerRecipe> getRecyclerRecipe() {
		return this.getRecipe(RecipeTypeInit.RECYCLER);
	}

	public <T extends AbstractRecipe> List<T> getRecipe (RecipeType<T> recipe) {
		return this.recipeManager.getAllRecipesFor(recipe).stream().toList();
	}
}
