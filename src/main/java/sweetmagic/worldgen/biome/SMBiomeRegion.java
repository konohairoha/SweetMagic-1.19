package sweetmagic.worldgen.biome;

import java.util.function.Consumer;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import sweetmagic.SweetMagicCore;
import sweetmagic.config.SMConfig;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.Regions;

public class SMBiomeRegion extends Region {

	private final int data;

	public SMBiomeRegion(int weight, int data) {
		super(SweetMagicCore.getSRC("overworld" + data), RegionType.OVERWORLD, weight);
		this.data = data;
	}

	@Override
	public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
		this.addModifiedVanillaOverworldBiomes(mapper, (b -> b.replaceBiome(Biomes.FOREST, this.getKey())));
	}

	public ResourceKey<Biome> getKey() {
		switch (this.data) {
		case 1: return FruitForest.getKey(0);
		case 3: return CherryForest.getKey(0);
		default: return PrismForest.getKey(0);
		}
	}

	public static void register() {
		Regions.register(new SMBiomeRegion(SMConfig.prismChance.get(), 0));
		Regions.register(new SMBiomeRegion(SMConfig.fruitChance.get(), 1));
		Regions.register(new SMBiomeRegion(SMConfig.cherreyChance.get(), 3));
	}
}
