package sweetmagic.plugin.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.menu.SMBookMenu;
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

@JeiPlugin
public class SMJeiPlugin implements IModPlugin {

	private static final ResourceLocation ID = SweetMagicCore.getSRC("sm_jei");

	public static final RecipeType<AlstroemeriaRecipe> ALSTROEMERIA = create("alstroemeria", AlstroemeriaRecipe.class);
	public static final RecipeType<ObMagiaRecipe> OBMAGIA = create("obmagia", ObMagiaRecipe.class);
	public static final RecipeType<IrisRecipe> IRIS = create("iris", IrisRecipe.class);
	public static final RecipeType<MillRecipe> MILL = create("mill", MillRecipe.class);
	public static final RecipeType<OvenRecipe> OVEN = create("oven", OvenRecipe.class);
	public static final RecipeType<BottleRecipe> BOTTLE = create("bottle", BottleRecipe.class);
	public static final RecipeType<FrypanRecipe> FRYPAN = create("frypan", FrypanRecipe.class);
	public static final RecipeType<PotRecipe> POT = create("pot", PotRecipe.class);
	public static final RecipeType<PedalRecipe> PEDAL = create("pedal", PedalRecipe.class);
	public static final RecipeType<TankRecipe> TANK = create("tank", TankRecipe.class);
	public static final RecipeType<FreezerRecipe> FREEZER = create("freezer", FreezerRecipe.class);
	public static final RecipeType<JuiceMakerRecipe> JUICEMAKER = create("juicemaker", JuiceMakerRecipe.class);
	public static final RecipeType<FurnitureRecipe> FURNITURE = create("furniture", FurnitureRecipe.class);
	public static final RecipeType<RecyclerRecipe> RECYCLER = create("recycler", RecyclerRecipe.class);

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {

		IGuiHelper helper = registry.getJeiHelpers().getGuiHelper();

		registry.addRecipeCategories(new AlstroemeriaRecipeCategory(helper));
		registry.addRecipeCategories(new ObMagiaRecipeCategory(helper));
		registry.addRecipeCategories(new IrisRecipeCategory(helper));
		registry.addRecipeCategories(new MillRecipeCategory(helper));
		registry.addRecipeCategories(new OvenRecipeCategory(helper));
		registry.addRecipeCategories(new BottleRecipeCategory(helper));
		registry.addRecipeCategories(new FrypanRecipeCategory(helper));
		registry.addRecipeCategories(new PotRecipeCategory(helper));
		registry.addRecipeCategories(new PedalRecipeCategory(helper));
		registry.addRecipeCategories(new TankRecipeCategory(helper));
		registry.addRecipeCategories(new FreezerRecipeCategory(helper));
		registry.addRecipeCategories(new JuiceMakerRecipeCategory(helper));
		registry.addRecipeCategories(new FurnitureRecipeCategory(helper));
		registry.addRecipeCategories(new RecyclerRecipeCategory(helper));
	}

	// レシピ定義、JEIアイテム関連をいじる
	@Override
	public void registerRecipes(IRecipeRegistration registry) {
		SMRecipes modRecipes = new SMRecipes();
		registry.addRecipes(ALSTROEMERIA, modRecipes.getAlstroemeriaRecipe());
		registry.addRecipes(OBMAGIA, modRecipes.getObMagiaRecipe());
		registry.addRecipes(MILL, modRecipes.getMillRecipe());
		registry.addRecipes(OVEN, modRecipes.getOvenRecipe());
		registry.addRecipes(BOTTLE, modRecipes.getBottleRecipe());
		registry.addRecipes(FRYPAN, modRecipes.getFrypanRecipe());
		registry.addRecipes(POT, modRecipes.getPotRecipe());
		registry.addRecipes(IRIS, modRecipes.getIrisRecipe());
		registry.addRecipes(PEDAL, modRecipes.getPedalRecipe());
		registry.addRecipes(TANK, modRecipes.getTankRecipe());
		registry.addRecipes(FREEZER, modRecipes.getFreezerRecipe());
		registry.addRecipes(JUICEMAKER, modRecipes.getJuiceMakerRecipe());
		registry.addRecipes(FURNITURE, modRecipes.getFurnitureRecipe());
		registry.addRecipes(RECYCLER, modRecipes.getRecyclerRecipe());
//		registry.addIngredientInfo(new ItemStack(ItemInit.aether_wand), VanillaTypes.ITEM_STACK, Component.translatable("tip.sweetmagic.magic_fire"));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
		registry.addRecipeCatalyst(new ItemStack(BlockInit.twilight_alstroemeria), ALSTROEMERIA);
		registry.addRecipeCatalyst(new ItemStack(BlockInit.obmagia), OBMAGIA);
		registry.addRecipeCatalyst(new ItemStack(BlockInit.iris_creation), IRIS);
		registry.addRecipeCatalyst(new ItemStack(BlockInit.oven), OVEN);
		registry.addRecipeCatalyst(new ItemStack(BlockInit.mill), MILL);
		registry.addRecipeCatalyst(new ItemStack(BlockInit.bottle), BOTTLE);
		registry.addRecipeCatalyst(new ItemStack(BlockInit.frypan_r), FRYPAN);
		registry.addRecipeCatalyst(new ItemStack(BlockInit.pot_w), POT);
		registry.addRecipeCatalyst(new ItemStack(BlockInit.pedestal_creat), PEDAL);
		registry.addRecipeCatalyst(new ItemStack(BlockInit.mftank), TANK);
		registry.addRecipeCatalyst(new ItemStack(BlockInit.freezer), FREEZER);
		registry.addRecipeCatalyst(new ItemStack(BlockInit.juice_maker), JUICEMAKER);
		registry.addRecipeCatalyst(new ItemStack(BlockInit.furniture_processing_table), FURNITURE);
		registry.addRecipeCatalyst(new ItemStack(BlockInit.aether_recycler), RECYCLER);
	}

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}

	public static <T> RecipeType<T> create (String name, Class<? extends T> recipeClass) {
		return RecipeType.create(SweetMagicCore.MODID, name, recipeClass);
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration regist) {
		regist.addRecipeTransferHandler(SMBookMenu.class, MenuInit.bookMenu, RecipeTypes.CRAFTING, 1, 9, 10, 36);
//		regist.addRecipeTransferHandler(AetherCraftTableMenu.class, MenuInit.aetherCraftTableMenu, RecipeTypes.CRAFTING, 1, 9, 10, 27);
		regist.addRecipeTransferHandler(new AetherCraftTableInfo());
//		regist.addRecipeTransferHandler(FreezerMenu.class, MenuInit.freezerMenu, RecipeTypeInit.FREEZER.get(), 1, 9, 10, 36);
//		regist.addRecipeTransferHandler(SMBookMenu.class, MenuInit.bookMenu, RecipeTypes.CRAFTING, 1, 9, 10, 36);
	}
}
