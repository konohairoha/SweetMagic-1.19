package sweetmagic.worldgen.biome;

import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;
import sweetmagic.init.PlaceInit;

public class FruitForest extends AbstractSMBiome {

	private static final ResourceKey<Biome> KEY = register("fruit_forest");
	private static final ResourceKey<Biome> SM = register("fruit_forest_sm");

	public FruitForest() {
		super();
	}

	public static ResourceKey<Biome> getKey(int data) {
		switch (data) {
		case 1: return SM;
		default: return KEY;
		}
	}

	public static Biome create() {
		FruitForest bio = new FruitForest();
		return bio.getBiome(bio.getSpawnSetting(), bio.getBiomeSetting());
	}

	public Biome getBiome(MobSpawnSettings.Builder spawn, BiomeGenerationSettings.Builder biome) {
		this.addFeature(biome, PlaceInit.CHEST_PLACE);
		this.addFeature(biome, PlaceInit.LEMON_PLACE);
		this.addFeature(biome, PlaceInit.ORANGE_PLACE);
		this.addFeature(biome, PlaceInit.ESTOR_PLACE);
		this.addFeature(biome, PlaceInit.PEARCH_PLACE);
		BiomeDefaultFeatures.addSurfaceFreezing(biome);
		return this.biome(Biome.Precipitation.RAIN, 0.75F, 0.2F, 3192141, spawn, biome);
	}
}
