package sweetmagic.worldgen.biome;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;
import sweetmagic.init.PlaceInit;

public class MagiaWoodForest extends AbstractSMBiome {

	private static final ResourceKey<Biome> KEY = register("magiawood_forest");

	public MagiaWoodForest() {
		super();
	}

	public static ResourceKey<Biome> getKey() {
		return KEY;
	}

	public static Biome create() {
		MagiaWoodForest bio = new MagiaWoodForest();
		return bio.getBiome(bio.getSpawnSetting(), bio.getBiomeSetting());
	}

	public Biome getBiome(MobSpawnSettings.Builder spawn, BiomeGenerationSettings.Builder biome) {
		this.addFeature(biome, PlaceInit.MAGIA_PLACE);
		this.addFeature(biome, PlaceInit.MAGIA_BIG_PLACE);
		return this.biome(Biome.Precipitation.RAIN, 1F, 0F, 5410402, spawn, biome);
	}
}
