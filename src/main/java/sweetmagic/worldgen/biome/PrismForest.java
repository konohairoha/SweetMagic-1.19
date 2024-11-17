package sweetmagic.worldgen.biome;

import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;
import sweetmagic.init.PlaceInit;

public class PrismForest extends AbstractSMBiome {

	private static final ResourceKey<Biome> KEY = register("prism_forest");
	private static final ResourceKey<Biome> SM = register("prism_forest_sm");

	public PrismForest () {
		super();
	}

	public static ResourceKey<Biome> getKey(int data) {
		switch (data) {
		case 1: return SM;
		default: return KEY;
		}
	}

	public static Biome create() {
		PrismForest bio = new PrismForest();
		return bio.getBiome(bio.getSpawnSetting(), bio.getBiomeSetting());
	}

	public Biome getBiome(MobSpawnSettings.Builder spawn, BiomeGenerationSettings.Builder biome) {
		this.addFeature(biome, PlaceInit.PRISM_PLACE);
		this.addFeature(biome, PlaceInit.PRISM_SMALL_PLACE);
		BiomeDefaultFeatures.addSurfaceFreezing(biome);
		return this.biome(Biome.Precipitation.SNOW, -0.25F, 0.2F, 11524343, spawn, biome);
	}
}
