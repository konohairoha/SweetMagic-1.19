package sweetmagic.worldgen.biome;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;
import sweetmagic.init.PlaceInit;

public class FruitTowerForest extends AbstractSMBiome {

	private static final ResourceKey<Biome> KEY = register("fruittower_forest");

	public FruitTowerForest() {
		super();
	}

	public static ResourceKey<Biome> getKey() {
		return KEY;
	}

	public static Biome create() {
		FruitTowerForest bio = new FruitTowerForest();
		return bio.getBiome(bio.getSpawnSetting(), bio.getBiomeSetting());
	}

	public Biome getBiome(MobSpawnSettings.Builder spawn, BiomeGenerationSettings.Builder biome) {
		this.addFeature(biome, PlaceInit.LEMON_BIG_PLACE);
		this.addFeature(biome, PlaceInit.ORANGE_BIG_PLACE);
		this.addFeature(biome, PlaceInit.ESTOR_BIG_PLACE);
		this.addFeature(biome, PlaceInit.PEARCH_BIG_PLACE);
		this.addFeature(biome, PlaceInit.CHESTNUT_BIG_PLACE);
		return this.biome(Biome.Precipitation.RAIN, 1F, 0F, 9355880, spawn, biome);
	}
}
