package sweetmagic.worldgen.ore;

import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.DeferredRegister;
import sweetmagic.init.BlockInit;

public class SMOreGen {

	public static final List<OreConfiguration.TargetBlockState> ORE_AETHER = getOreList(BlockInit.aether_crystal_ore, BlockInit.deep_aether_crystal_ore);
	public static final List<OreConfiguration.TargetBlockState> ORE_COSMIC = getOreList(BlockInit.cosmic_crystal_ore, BlockInit.deep_cosmic_crystal_ore);
	public static final List<OreConfiguration.TargetBlockState> ORE_FLUORITE = getOreList(BlockInit.fluorite_ore, BlockInit.deep_fluorite_ore);
	public static final List<OreConfiguration.TargetBlockState> ORE_REDBERYL = getOreList(BlockInit.redberyl_ore, BlockInit.deep_redberyl_ore);

	public static final Holder<PlacedFeature> ORE_AE_SMALL = getPlace("ore_ae_sm", ORE_AETHER, 4, 0.3F, getOreMod(6, absolute(10), absolute(30)));
	public static final Holder<PlacedFeature> ORE_AE_LARGE = getPlace("ore_ae_la", ORE_AETHER, 12, 0.45F, getOreMod(8, absolute(-20), absolute(60)));
	public static final Holder<PlacedFeature> ORE_AE_BURIED = getPlace("ore_ae_bu", ORE_AETHER, 8, 0.75F, getOreMod(6, aboveBottom(-80), aboveBottom(40)));

	public static final Holder<PlacedFeature> ORE_CS_SMALL = getPlace("ore_cs_sm", ORE_COSMIC, 2, 0.3F, getOreMod(3, absolute(10), absolute(30)));
	public static final Holder<PlacedFeature> ORE_CS_LARGE = getPlace("ore_cs_la", ORE_COSMIC, 6, 0.45F, getOreMod(4, absolute(-20), absolute(60)));
	public static final Holder<PlacedFeature> ORE_CS_BURIED = getPlace("ore_cs_bu", ORE_COSMIC, 4, 0.75F, getOreMod(2, aboveBottom(-80), aboveBottom(40)));

	public static final Holder<PlacedFeature> ORE_FR_SMALL = getPlace("ore_fr_sm", ORE_FLUORITE, 3, 0.4F, getOreMod(5, absolute(20), absolute(40)));
	public static final Holder<PlacedFeature> ORE_FR_LARGE = getPlace("ore_fr_la", ORE_FLUORITE, 9, 0.6F, getOreMod(7, absolute(-30), absolute(50)));
	public static final Holder<PlacedFeature> ORE_FR_BURIED = getPlace("ore_fr_bu", ORE_FLUORITE, 6, 0.85F, getOreMod(4, aboveBottom(-90), aboveBottom(30)));

	public static final Holder<PlacedFeature> ORE_RB_SMALL = getPlace("ore_fb_sm", ORE_REDBERYL, 2, 0.35F, getOreMod(4, absolute(0), absolute(30)));
	public static final Holder<PlacedFeature> ORE_RB_LARGE = getPlace("ore_fb_la", ORE_REDBERYL, 6, 0.5F, getOreMod(5, absolute(-40), absolute(40)));
	public static final Holder<PlacedFeature> ORE_RB_BURIED = getPlace("ore_fb_bu", ORE_REDBERYL, 4, 0.8F, getOreMod(3, aboveBottom(-100), aboveBottom(20)));

	public static void register(DeferredRegister<BiomeModifier> biome) {
		register("ore_ae_small", biome, SMOreGen.ORE_AE_SMALL, 0);
		register("ore_ae_large", biome, SMOreGen.ORE_AE_LARGE, 0);
		register("ore_ae_buried", biome, SMOreGen.ORE_AE_BURIED, 0);
		register("ore_cs_small", biome, SMOreGen.ORE_CS_SMALL, 1);
		register("ore_cs_large", biome, SMOreGen.ORE_CS_LARGE, 1);
		register("ore_cs_buried", biome, SMOreGen.ORE_CS_BURIED, 1);
		register("ore_fr_small", biome, SMOreGen.ORE_FR_SMALL, 1);
		register("ore_fr_large", biome, SMOreGen.ORE_FR_LARGE, 1);
		register("ore_fr_buried", biome, SMOreGen.ORE_FR_BURIED, 1);
		register("ore_rb_small", biome, SMOreGen.ORE_RB_SMALL, 1);
		register("ore_rb_large", biome, SMOreGen.ORE_RB_LARGE, 1);
		register("ore_rb_buried", biome, SMOreGen.ORE_RB_BURIED, 1);
	}

	public static void register(String name, DeferredRegister<BiomeModifier> biome, Holder<PlacedFeature> place, int data) {

		BiomeModifier mod;

		switch(data) {
		case 1:
			mod = new CSOreModifier(GenerationStep.Decoration.UNDERGROUND_ORES, place);
			break;
		default:
			mod = new OreModifier(GenerationStep.Decoration.UNDERGROUND_ORES, place);
			break;
		}

		register(name, biome, mod);
	}

	public static void register(String name, DeferredRegister<BiomeModifier> biome, BiomeModifier mod) {
		biome.register(name, () -> mod);
	}

	public static List<OreConfiguration.TargetBlockState> getOreList (Block ore, Block deepOre) {
		return List.of(
				OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, ore.defaultBlockState()),
				OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, deepOre.defaultBlockState())
			);
	}

	public static Holder<PlacedFeature> getPlace (String name, List<OreConfiguration.TargetBlockState> oreList, int size, float chance, List<PlacementModifier> placeMod) {
		Holder<? extends ConfiguredFeature<?, ?>> featuer = FeatureUtils.register(name + "_fe", Feature.ORE, new OreConfiguration(oreList, size, chance));
		return PlacementUtils.register(name, featuer, placeMod);
	}

	public static List<PlacementModifier> orePlacement(PlacementModifier count, PlacementModifier place) {
		return List.of(count, InSquarePlacement.spread(), place, BiomeFilter.biome());
	}

	public static List<PlacementModifier> getOreMod(int count, VerticalAnchor ver1, VerticalAnchor ver2) {
		return orePlacement(CountPlacement.of(count), HeightRangePlacement.triangle(ver1, ver2));
	}

	public static VerticalAnchor absolute (int y) {
		return VerticalAnchor.absolute(y);
	}

	public static VerticalAnchor aboveBottom (int y) {
		return VerticalAnchor.aboveBottom(y);
	}
}
