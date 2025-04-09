package sweetmagic.worldgen.biome;

import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;
import sweetmagic.init.PlaceInit;

public class MapleForest extends AbstractSMBiome {

	private static final ResourceKey<Biome> KEY = register("maple_forest");

	public MapleForest() {
		super();
	}

	public static ResourceKey<Biome> getKey() {
		return KEY;
	}

	public static Biome create() {
		MapleForest bio = new MapleForest();
		return bio.getBiome(bio.getSpawnSetting(), bio.getBiomeSetting());
	}

	public Biome getBiome(MobSpawnSettings.Builder spawn, BiomeGenerationSettings.Builder biome) {
		this.addFeature(biome, PlaceInit.MAPLE_PLACE);
		this.addFeature(biome, PlaceInit.MAPLE_BIG_PLACE);
		this.addFeature(biome, PlaceInit.MAPLE_LONG_PLACE);
		BiomeDefaultFeatures.addSurfaceFreezing(biome);
		return this.biome(Biome.Precipitation.RAIN, 1F, 0.2F, 16763469, spawn, biome);
	}
}
