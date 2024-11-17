package sweetmagic.worldgen.biome;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import sweetmagic.SweetMagicCore;

public abstract class AbstractSMBiome {

	private MobSpawnSettings.Builder mb;
	private BiomeGenerationSettings.Builder bb;

	public AbstractSMBiome() {

		this.mb = new MobSpawnSettings.Builder();
		BiomeDefaultFeatures.farmAnimals(this.mb);
		BiomeDefaultFeatures.commonSpawns(this.mb);

		this.bb = new BiomeGenerationSettings.Builder();
		this.globalOverworldGeneration(this.bb);
		BiomeDefaultFeatures.addForestFlowers(this.bb);
		BiomeDefaultFeatures.addFerns(this.bb);
		BiomeDefaultFeatures.addDefaultOres(this.bb);
		BiomeDefaultFeatures.addDefaultMushrooms(this.bb);
		BiomeDefaultFeatures.addDefaultExtraVegetation(this.bb);
	}

	public MobSpawnSettings.Builder getSpawnSetting() {
		return this.mb;
	}

	public BiomeGenerationSettings.Builder getBiomeSetting() {
		return this.bb;
	}

	public void globalOverworldGeneration(BiomeGenerationSettings.Builder build) {
		BiomeDefaultFeatures.addDefaultCarversAndLakes(build);
		BiomeDefaultFeatures.addDefaultCrystalFormations(build);
		BiomeDefaultFeatures.addDefaultMonsterRoom(build);
		BiomeDefaultFeatures.addDefaultUndergroundVariety(build);
		BiomeDefaultFeatures.addDefaultSprings(build);
	}

	public void addFeature(BiomeGenerationSettings.Builder biome, Holder<PlacedFeature> place) {
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, place);
	}

	public Biome biome(Biome.Precipitation precipitation, float temperature, float downfall, int grassColor, MobSpawnSettings.Builder sb, BiomeGenerationSettings.Builder bb) {
		return new Biome.BiomeBuilder().precipitation(precipitation).temperature(temperature).downfall(downfall).specialEffects((new BiomeSpecialEffects.Builder())
			.grassColorOverride(grassColor).waterColor(5738489).waterFogColor(329011).fogColor(12638463).skyColor(7978751)
			.ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(sb.build()).generationSettings(bb.build()).build();
	}

	public abstract Biome getBiome(MobSpawnSettings.Builder spawn, BiomeGenerationSettings.Builder biome);

	public static ResourceKey<Biome> register(String name) {
		return ResourceKey.create(Registry.BIOME_REGISTRY, SweetMagicCore.getSRC(name));
	}
}
