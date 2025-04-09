package sweetmagic.worldgen.biome;

import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;
import sweetmagic.init.PlaceInit;

public class FrozenForest extends AbstractSMBiome {

	private static final ResourceKey<Biome> KEY = register("frozen_forest");

	public FrozenForest () {
		super();
	}

	public static ResourceKey<Biome> getKey() {
		return KEY;
	}

	public static Biome create() {
		FrozenForest bio = new FrozenForest();
		return bio.getBiome(bio.getSpawnSetting(), bio.getBiomeSetting());
	}

	public Biome getBiome(MobSpawnSettings.Builder spawn, BiomeGenerationSettings.Builder biome) {
		this.addFeature(biome, PlaceInit.MAGIA_LONG_PLACE);
		BiomeDefaultFeatures.addSurfaceFreezing(biome);
		return this.biome(Biome.Precipitation.SNOW, -0.25F, 0.2F, 9355880, spawn, biome);
	}
}
