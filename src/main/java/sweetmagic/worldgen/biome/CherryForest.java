package sweetmagic.worldgen.biome;

import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;
import sweetmagic.init.PlaceInit;

public class CherryForest extends AbstractSMBiome {

	private static final ResourceKey<Biome> KEY = register("cherry_blossoms_forest");
	private static final ResourceKey<Biome> SM = register("cherry_blossoms_forest_sm");

	public CherryForest() {
		super();
	}

	public static ResourceKey<Biome> getKey(int data) {
		switch (data) {
		case 1: return SM;
		default: return KEY;
		}
	}

	public static Biome create() {
		CherryForest bio = new CherryForest();
		return bio.getBiome(bio.getSpawnSetting(), bio.getBiomeSetting());
	}

	public Biome getBiome(MobSpawnSettings.Builder spawn, BiomeGenerationSettings.Builder biome) {
		this.addFeature(biome, PlaceInit.CHERRY_PLACE);
		this.addFeature(biome, PlaceInit.CHERRY_BIG_PLACE);
		this.addFeature(biome, PlaceInit.CHERRY_KING_PLACE);
		this.addFeature(biome, PlaceInit.AZALEA_PLACE);
		BiomeDefaultFeatures.addSurfaceFreezing(biome);
		return this.biome(Biome.Precipitation.RAIN, 0.25F, 0.2F, 9355880, spawn, biome);
	}
}
