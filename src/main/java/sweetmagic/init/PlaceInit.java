package sweetmagic.init;

import java.util.OptionalInt;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import sweetmagic.SweetMagicCore;

public class PlaceInit {

	public static final ResourceLocation PRISM_SRC = SweetMagicCore.getSRC("prismwood");
	private static final ResourceLocation PRISM_SMALL_SRC = SweetMagicCore.getSRC("prismwood_small");
	private static final ResourceLocation CHESTNU_SRC = SweetMagicCore.getSRC("chestnut");
	private static final ResourceLocation LEMON_SRC = SweetMagicCore.getSRC("lemon");
	private static final ResourceLocation ORANGE_SRC = SweetMagicCore.getSRC("orange");
	private static final ResourceLocation ESTOR_SRC = SweetMagicCore.getSRC("estor");
	private static final ResourceLocation PEARCH_SRC = SweetMagicCore.getSRC("peach");
	private static final ResourceLocation CHERRY_SRC = SweetMagicCore.getSRC("cherry");
	private static final ResourceLocation CHERRY_BIG_SRC = SweetMagicCore.getSRC("cherry_big");
	private static final ResourceLocation CHERRY_KING_SRC = SweetMagicCore.getSRC("cherry_king");
	private static final ResourceLocation AZALEA_SRC = SweetMagicCore.getSRC("azalea");
	private static final ResourceLocation MAGIA_SRC = SweetMagicCore.getSRC("magiawood");
	private static final ResourceLocation MAGIA_BIG_SRC = SweetMagicCore.getSRC("magiawood_big");
	private static final ResourceLocation LEMON_BIG_SRC = SweetMagicCore.getSRC("lemon_big");
	private static final ResourceLocation ORANGE_BIG_SRC = SweetMagicCore.getSRC("orange_big");
	private static final ResourceLocation ESTOR_BIG_SRC = SweetMagicCore.getSRC("estor_big");
	private static final ResourceLocation PEACH_BIG_SRC = SweetMagicCore.getSRC("peach_big");
	private static final ResourceLocation CHESTNU_BIG_SRC = SweetMagicCore.getSRC("chestnu_big");

	public static Holder<PlacedFeature> PRISM_PLACE = getPF(PRISM_SRC, 3, FeatuerInit.PRISM.get());
	public static Holder<PlacedFeature> PRISM_SMALL_PLACE = getPF(PRISM_SMALL_SRC, 6, FeatuerInit.PRISM_SMALL.get());
	public static Holder<PlacedFeature> CHEST_PLACE = getPF(CHESTNU_SRC, 2, FeatuerInit.CHESTNUT.get());
	public static Holder<PlacedFeature> LEMON_PLACE = getPF(LEMON_SRC, 2, FeatuerInit.LEMON.get());
	public static Holder<PlacedFeature> ORANGE_PLACE = getPF(ORANGE_SRC, 2, FeatuerInit.ORANGE.get());
	public static Holder<PlacedFeature> ESTOR_PLACE = getPF(ESTOR_SRC, 2, FeatuerInit.ESTOR.get());
	public static Holder<PlacedFeature> PEARCH_PLACE = getPF(PEARCH_SRC, 2, FeatuerInit.PEARCH.get());
	public static Holder<PlacedFeature> CHERRY_PLACE = getPF(CHERRY_SRC, 1, FeatuerInit.CHERRY.get());
	public static Holder<PlacedFeature> CHERRY_BIG_PLACE = getPFSakura(CHERRY_BIG_SRC, 24, FeatuerInit.CHERRY_BIG.get());
	public static Holder<PlacedFeature> CHERRY_KING_PLACE = getPFSakuraBig(CHERRY_KING_SRC, 96, FeatuerInit.CHERRY_BIG.get());
	public static Holder<PlacedFeature> AZALEA_PLACE = getPF(AZALEA_SRC, 6, FeatuerInit.AZALEA.get());
	public static Holder<PlacedFeature> MAGIA_PLACE = getPF(MAGIA_SRC, 1, FeatuerInit.MAGIA.get());
	public static Holder<PlacedFeature> MAGIA_BIG_PLACE = getPF(MAGIA_BIG_SRC, 5, FeatuerInit.MAGIA_BIG.get());
	public static Holder<PlacedFeature> LEMON_BIG_PLACE = getPF(LEMON_BIG_SRC, 6, FeatuerInit.LEMON_BIG.get());
	public static Holder<PlacedFeature> ORANGE_BIG_PLACE = getPF(ORANGE_BIG_SRC, 6, FeatuerInit.ORANGE_BIG.get());
	public static Holder<PlacedFeature> ESTOR_BIG_PLACE = getPF(ESTOR_BIG_SRC, 6, FeatuerInit.ESTOR_BIG.get());
	public static Holder<PlacedFeature> PEARCH_BIG_PLACE = getPF(PEACH_BIG_SRC, 6, FeatuerInit.PEARCH_BIG.get());
	public static Holder<PlacedFeature> CHESTNUT_BIG_PLACE = getPF(CHESTNU_BIG_SRC, 6, FeatuerInit.CHESTNUT_BIG.get());

    public static Holder<PlacedFeature> getPF (ResourceLocation src, int rate, Feature<NoneFeatureConfiguration> ft) {
    	Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> cf = FeatureUtils.register(src.getPath(), ft);
    	return PlacementUtils.register(src.getPath(), cf, VegetationPlacements.treePlacement(RarityFilter.onAverageOnceEvery(rate)));
    }

    public static Holder<PlacedFeature> getPFSakura (ResourceLocation src, int rate, Feature<TreeConfiguration> tc) {
    	TreeConfiguration ft = (new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(BlockInit.cherry_blossoms_log), new FancyTrunkPlacer(5, 14, 0), BlockStateProvider.simple(BlockInit.cherry_blossoms_leaves), new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4), new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4)))).ignoreVines().build();
    	Holder<ConfiguredFeature<TreeConfiguration, ?>> cf = FeatureUtils.register(src.getPath(), tc, ft);
    	return PlacementUtils.register(src.getPath(), cf, VegetationPlacements.treePlacement(RarityFilter.onAverageOnceEvery(rate)));
    }

    public static Holder<PlacedFeature> getPFSakuraBig (ResourceLocation src, int rate, Feature<TreeConfiguration> tc) {
    	TreeConfiguration ft = (new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(BlockInit.cherry_blossoms_log), new FancyTrunkPlacer(12, 24, 0), BlockStateProvider.simple(BlockInit.cherry_blossoms_leaves.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true)), new FancyFoliagePlacer(ConstantInt.of(3), ConstantInt.of(6), 6), new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(8)))).ignoreVines().build();
    	Holder<ConfiguredFeature<TreeConfiguration, ?>> cf = FeatureUtils.register(src.getPath(), tc, ft);
    	return PlacementUtils.register(src.getPath(), cf, VegetationPlacements.treePlacement(RarityFilter.onAverageOnceEvery(rate)));
    }
}
