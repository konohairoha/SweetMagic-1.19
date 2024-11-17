package sweetmagic.worldgen.flower;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.DeferredRegister;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iblock.ISMCrop;
import sweetmagic.config.SMConfig;
import sweetmagic.init.BlockInit;
import sweetmagic.init.block.sm.SMFlowerDiff;
public class SMFlowerGen {

    private static final Map<String, ResourceKey<PlacedFeature>> keyMap = new HashMap<>();

	public static final SimpleBlockConfiguration SUGAR = new SimpleBlockConfiguration(
		new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
			.add(BlockInit.sugarbell_plant.defaultBlockState().setValue(ISMCrop.AGE3, 3), 10)
			.add(BlockInit.glowflower_plant.defaultBlockState().setValue(ISMCrop.AGE3, 3), 5)
			.add(BlockInit.sannyflower_plant.defaultBlockState().setValue(ISMCrop.AGE3, 2), 2)
			.add(BlockInit.raspberry_plant.defaultBlockState().setValue(ISMCrop.AGE5, 5), 1)
			.add(BlockInit.mint_plant.defaultBlockState().setValue(ISMCrop.AGE3, 3), 1)
			.build()
		)
	);

	public static final SimpleBlockConfiguration MOON = new SimpleBlockConfiguration(
		new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
			.add(BlockInit.moonblossom_plant.defaultBlockState().setValue(ISMCrop.AGE3, 2), 5)
			.add(BlockInit.drizzly_mysotis_plant.defaultBlockState().setValue(ISMCrop.AGE3, 2), 5)
			.build()
		)
	);

	public static final SimpleBlockConfiguration PRISM = new SimpleBlockConfiguration(
		new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
			.add(BlockInit.snowdrop.defaultBlockState(), 2)
			.add(BlockInit.glowflower_plant.defaultBlockState().setValue(ISMCrop.AGE3, 3), 3)
			.add(BlockInit.sannyflower_plant.defaultBlockState().setValue(ISMCrop.AGE3, 2), 3)
			.add(BlockInit.fire_nasturtium_plant.defaultBlockState().setValue(ISMCrop.AGE3, 3), 3)
			.add(BlockInit.clerodendrum_plant.defaultBlockState().setValue(ISMCrop.AGE3, 3), 3)
			.add(BlockInit.mint_plant.defaultBlockState().setValue(ISMCrop.AGE3, 3), 3)
			.add(BlockInit.cotton_plant.defaultBlockState().setValue(ISMCrop.AGE3, 3), 3)
			.build()
		)
	);

	public static final SimpleBlockConfiguration FRUIT = new SimpleBlockConfiguration(
		new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
			.add(BlockInit.blueberry_plant.defaultBlockState().setValue(ISMCrop.AGE4, 4), 3)
			.add(BlockInit.olive_plant.defaultBlockState().setValue(ISMCrop.AGE4, 4), 3)
			.build()
		)
	);

	public static final SimpleBlockConfiguration WHITE = new SimpleBlockConfiguration(
		new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
			.add(BlockInit.whitenet_plant.defaultBlockState().setValue(ISMCrop.AGE3, 3), 5)
			.build()
		)
	);

	public static final SimpleBlockConfiguration FLOWER = new SimpleBlockConfiguration(
		new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
			.add(BlockInit.cornflower.defaultBlockState().setValue(SMFlowerDiff.SIZE, 0), 5)
			.add(BlockInit.cornflower.defaultBlockState().setValue(SMFlowerDiff.SIZE, 1), 5)
			.add(BlockInit.lily_valley.defaultBlockState().setValue(SMFlowerDiff.SIZE, 0), 5)
			.add(BlockInit.lily_valley.defaultBlockState().setValue(SMFlowerDiff.SIZE, 1), 5)
			.add(BlockInit.cosmos.defaultBlockState(), 10)
			.add(BlockInit.blackrose.defaultBlockState(), 10)
			.add(BlockInit.white_clover.defaultBlockState().setValue(SMFlowerDiff.SIZE, 0), 4)
			.add(BlockInit.white_clover.defaultBlockState().setValue(SMFlowerDiff.SIZE, 1), 3)
			.add(BlockInit.white_clover.defaultBlockState().setValue(SMFlowerDiff.SIZE, 2), 3)
			.add(BlockInit.foxtail_grass.defaultBlockState(), 10)
			.add(BlockInit.snowdrop.defaultBlockState(), 10)
			.add(BlockInit.turkey_balloonflower.defaultBlockState(), 10)
			.add(BlockInit.iberis_umbellata.defaultBlockState(), 10)
			.add(BlockInit.ultramarine_rose.defaultBlockState(), 10)
			.add(BlockInit.solid_star.defaultBlockState(), 10)
			.add(BlockInit.zinnia.defaultBlockState(), 10)
			.add(BlockInit.campanula.defaultBlockState(), 10)
			.add(BlockInit.primula_polyansa.defaultBlockState(), 10)
			.add(BlockInit.hydrangea.defaultBlockState().setValue(SMFlowerDiff.SIZE, 0), 4)
			.add(BlockInit.hydrangea.defaultBlockState().setValue(SMFlowerDiff.SIZE, 1), 3)
			.add(BlockInit.hydrangea.defaultBlockState().setValue(SMFlowerDiff.SIZE, 2), 3)
			.add(BlockInit.carnation_crayola.defaultBlockState(), 10)
			.add(BlockInit.christmas_rose.defaultBlockState(), 10)
			.add(BlockInit.portulaca.defaultBlockState(), 10)
			.add(BlockInit.surfinia.defaultBlockState(), 10)
			.add(BlockInit.pansy_blue.defaultBlockState().setValue(SMFlowerDiff.SIZE, 0), 4)
			.add(BlockInit.pansy_blue.defaultBlockState().setValue(SMFlowerDiff.SIZE, 1), 3)
			.add(BlockInit.pansy_blue.defaultBlockState().setValue(SMFlowerDiff.SIZE, 2), 3)
			.add(BlockInit.pansy_yellowmazenta.defaultBlockState(), 10)
			.add(BlockInit.marigold.defaultBlockState(), 10)
			.add(BlockInit.christmarose_ericsmithii.defaultBlockState(), 10)
			.build()
		)
	);

	public static void register(DeferredRegister<BiomeModifier> register) {
		Decoration dec = GenerationStep.Decoration.VEGETAL_DECORATION;
		register.register("plain_flower", () -> new SugarModifier(dec, registerPlace("plain_flower", SUGAR, SMConfig.plainFlowerChance.get())));
		register.register("forest_flower", () -> new MoonModifier(dec, registerPlace("forest_flower", MOON, SMConfig.forestFlowerChance.get())));
		register.register("flower_flower", () -> new FlowerModifier(dec, registerPlace("flower_flower", FLOWER, SMConfig.flowerFlowerChance.get())));
		register.register("prism_flower", () -> new PrismModifier(dec, registerPlace("prism_flower", PRISM, 6)));
		register.register("fruit_flower", () -> new FruitModifier(dec, registerPlace("fruit_flower", FRUIT, 5)));
		register.register("garden_flower", () -> new FlowerGurdenModifier(dec, registerPlace("garden_flower", FLOWER, 100)));
	}

	public static final Holder<PlacedFeature> registerPlace(String name, SimpleBlockConfiguration config, int chance) {
		return register(SweetMagicCore.getSRC(name), register(SweetMagicCore.getSRC(name), Feature.RANDOM_PATCH,
				FeatureUtils.simpleRandomPatchConfiguration(6, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, config))),
				CountPlacement.of(chance), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
	}

    public static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<FC, ?>> register(ResourceLocation id, F feature, FC config) {
        return badRegister(BuiltinRegistries.CONFIGURED_FEATURE, id, new ConfiguredFeature<>(feature, config));
    }

    public static <V extends T, T> Holder<V> badRegister(Registry<T> registry, ResourceLocation id, V value) {
        return BuiltinRegistries.register((Registry<V>) registry, id, value);
    }

    public static Holder<PlacedFeature> register(ResourceLocation id, Holder<? extends ConfiguredFeature<?, ?>> feature, List<PlacementModifier> mod) {
        keyMap.put(id.getPath(), ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, id));
        return BuiltinRegistries.register(BuiltinRegistries.PLACED_FEATURE, id, new PlacedFeature(Holder.hackyErase(feature), List.copyOf(mod)));
    }

    public static Holder<PlacedFeature> register(ResourceLocation id, Holder<? extends ConfiguredFeature<?, ?>> feature, PlacementModifier... mod) {
        return register(id, feature, List.of(mod));
    }

    public static ResourceKey<PlacedFeature> getFeatureKey(String key) {
        return keyMap.get(key);
    }
}
