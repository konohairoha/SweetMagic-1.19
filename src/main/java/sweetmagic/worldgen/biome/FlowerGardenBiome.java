package sweetmagic.worldgen.biome;

import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;

public class FlowerGardenBiome extends AbstractSMBiome {

	private static final ResourceKey<Biome> KEY = register("flower_garden");

	public FlowerGardenBiome() {
		super();
	}

	public static ResourceKey<Biome> getKey() {
		return KEY;
	}

	public static Biome create() {
		FlowerGardenBiome bio = new FlowerGardenBiome();
		return bio.getBiome(bio.getSpawnSetting(), bio.getBiomeSetting());
	}

	public Biome getBiome(MobSpawnSettings.Builder spawn, BiomeGenerationSettings.Builder biome) {
		this.addFeature(biome, VegetationPlacements.TREES_PLAINS);
		BiomeDefaultFeatures.addSurfaceFreezing(biome);
		return this.biome(Biome.Precipitation.RAIN, 1F, 0F, 9355880, spawn, biome);
	}
}
