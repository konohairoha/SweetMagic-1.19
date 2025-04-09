package sweetmagic.init;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import sweetmagic.SweetMagicCore;
import sweetmagic.recipe.alstrameria.AlstroemeriaRecipe;
import sweetmagic.recipe.alstrameria.AlstroemeriaRecipeSeria;
import sweetmagic.recipe.bottle.BottleRecipe;
import sweetmagic.recipe.bottle.BottleRecipeSeria;
import sweetmagic.recipe.feezer.FreezerRecipe;
import sweetmagic.recipe.feezer.FreezerRecipeSeria;
import sweetmagic.recipe.frypan.FrypanRecipe;
import sweetmagic.recipe.frypan.FrypanRecipeSeria;
import sweetmagic.recipe.furniture.FurnitureRecipe;
import sweetmagic.recipe.furniture.FurnitureRecipeSeria;
import sweetmagic.recipe.iris.IrisRecipe;
import sweetmagic.recipe.iris.IrisRecipeSeria;
import sweetmagic.recipe.juice_maker.JuiceMakerRecipe;
import sweetmagic.recipe.juice_maker.JuiceMakerRecipeSeria;
import sweetmagic.recipe.mill.MillRecipe;
import sweetmagic.recipe.mill.MillRecipeSeria;
import sweetmagic.recipe.obmagia.ObMagiaRecipe;
import sweetmagic.recipe.obmagia.ObMagiaRecipeSeria;
import sweetmagic.recipe.oven.OvenRecipe;
import sweetmagic.recipe.oven.OvenRecipeSeria;
import sweetmagic.recipe.pedal.PedalRecipe;
import sweetmagic.recipe.pedal.PedalRecipeSeria;
import sweetmagic.recipe.pot.PotRecipe;
import sweetmagic.recipe.pot.PotRecipeSeria;
import sweetmagic.recipe.recycler.RecyclerRecipe;
import sweetmagic.recipe.recycler.RecyclerRecipeSeria;
import sweetmagic.recipe.tank.TankRecipe;
import sweetmagic.recipe.tank.TankRecipeSeria;

public class RecipeSerializerInit {

	public static final DeferredRegister<RecipeSerializer<?>> REGISTRY = SweetMagicCore.getDef(ForgeRegistries.RECIPE_SERIALIZERS);

	public static final RecipeSerializer<AlstroemeriaRecipe> ALSTROMERIA = register("alstromeria_craft", new AlstroemeriaRecipeSeria());
	public static final RecipeSerializer<ObMagiaRecipe> OBMAGIA = register("obmagia_craft", new ObMagiaRecipeSeria());
	public static final RecipeSerializer<IrisRecipe> IRIS = register("iris_craft", new IrisRecipeSeria());
	public static final RecipeSerializer<MillRecipe> MILL = register("mill_craft", new MillRecipeSeria());
	public static final RecipeSerializer<OvenRecipe> OVEN = register("oven_craft", new OvenRecipeSeria());
	public static final RecipeSerializer<BottleRecipe> BOTTLE = register("bottle_craft", new BottleRecipeSeria());
	public static final RecipeSerializer<FrypanRecipe> FRYPAN = register("frypan_craft", new FrypanRecipeSeria());
	public static final RecipeSerializer<PotRecipe> POT = register("pot_craft", new PotRecipeSeria());
	public static final RecipeSerializer<PedalRecipe> PEDAL = register("pedal_craft", new PedalRecipeSeria());
	public static final RecipeSerializer<TankRecipe> TANK = register("tank_craft", new TankRecipeSeria());
	public static final RecipeSerializer<FreezerRecipe> FREEZER = register("freezer_craft", new FreezerRecipeSeria());
	public static final RecipeSerializer<JuiceMakerRecipe> JUICEMAKER = register("juicemaker_craft", new JuiceMakerRecipeSeria());
	public static final RecipeSerializer<FurnitureRecipe> FURNITURE = register("furniture_craft", new FurnitureRecipeSeria());
	public static final RecipeSerializer<RecyclerRecipe> RECYCLER = register("recycler_craft", new RecyclerRecipeSeria());

	public static <T extends RecipeSerializer<?>> T register(String name, T seria) {
		REGISTRY.register(name, () -> seria);
		return seria;
	}
}
