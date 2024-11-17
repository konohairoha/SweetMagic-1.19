package sweetmagic.init;

import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sweetmagic.SweetMagicCore;
import sweetmagic.recipe.alstrameria.AlstroemeriaRecipe;
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

public class RecipeTypeInit {

	public static final DeferredRegister<RecipeType<?>> REGISTRY = SweetMagicCore.getDef(ForgeRegistries.RECIPE_TYPES);

	public static final RegistryObject<RecipeType<AlstroemeriaRecipe>> ALSTROMERIA = register("alstromeria_craft", new RecipeType<AlstroemeriaRecipe>() {});
	public static final RegistryObject<RecipeType<ObMagiaRecipe>> OBMAGIA = register("obmagia_craft", new RecipeType<ObMagiaRecipe>() {});
	public static final RegistryObject<RecipeType<IrisRecipe>> IRIS = register("iris_craft", new RecipeType<IrisRecipe>() {});
	public static final RegistryObject<RecipeType<MillRecipe>> MILL = register("mill_craft", new RecipeType<MillRecipe>() {});
	public static final RegistryObject<RecipeType<OvenRecipe>> OVEN = register("oven_craft", new RecipeType<OvenRecipe>() {});
	public static final RegistryObject<RecipeType<BottleRecipe>> BOTTLE = register("bottle_craft", new RecipeType<BottleRecipe>() {});
	public static final RegistryObject<RecipeType<FrypanRecipe>> FRYPAN = register("frypan_craft", new RecipeType<FrypanRecipe>() {});
	public static final RegistryObject<RecipeType<PotRecipe>> POT = register("pot_craft", new RecipeType<PotRecipe>() {});
	public static final RegistryObject<RecipeType<PedalRecipe>> PEDAL = register("pedal_craft", new RecipeType<PedalRecipe>() {});
	public static final RegistryObject<RecipeType<TankRecipe>> TANK = register("tank_craft", new RecipeType<TankRecipe>() {});
	public static final RegistryObject<RecipeType<FreezerRecipe>> FREEZER = register("freezer_craft", new RecipeType<FreezerRecipe>() {});
	public static final RegistryObject<RecipeType<JuiceMakerRecipe>> JUICEMAKER = register("juicemaker_craft", new RecipeType<JuiceMakerRecipe>() {});
	public static final RegistryObject<RecipeType<FurnitureRecipe>> FURNITURE = register("furniture_craft", new RecipeType<FurnitureRecipe>() {});
	public static final RegistryObject<RecipeType<RecyclerRecipe>> RECYCLER = register("recycler_craft", new RecipeType<RecyclerRecipe>() {});

	public static <T extends RecipeType<?>> RegistryObject<T> register (String name, T recipeType) {
		return REGISTRY.register(name, () -> recipeType);
	}
}
