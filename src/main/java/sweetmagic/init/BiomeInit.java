package sweetmagic.init;

import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.IForgeRegistry;
import sweetmagic.worldgen.biome.CherryForest;
import sweetmagic.worldgen.biome.FlowerGarden;
import sweetmagic.worldgen.biome.FrozenForest;
import sweetmagic.worldgen.biome.FruitForest;
import sweetmagic.worldgen.biome.FruitTowerForest;
import sweetmagic.worldgen.biome.MagiaWoodForest;
import sweetmagic.worldgen.biome.MapleForest;
import sweetmagic.worldgen.biome.PrismForest;

public class BiomeInit {

	public static void registerBiome(IForgeRegistry<Biome> registry) {
		registry.register(PrismForest.getKey(0).location(), PrismForest.create());
		registry.register(FruitForest.getKey(0).location(), FruitForest.create());
		registry.register(CherryForest.getKey(0).location(), CherryForest.create());
		registry.register(PrismForest.getKey(1).location(), PrismForest.create());
		registry.register(FruitForest.getKey(1).location(), FruitForest.create());
		registry.register(CherryForest.getKey(1).location(), CherryForest.create());
		registry.register(FlowerGarden.getKey().location(), FlowerGarden.create());
		registry.register(MagiaWoodForest.getKey().location(), MagiaWoodForest.create());
		registry.register(FruitTowerForest.getKey().location(), FruitTowerForest.create());
		registry.register(FrozenForest.getKey().location(), FrozenForest.create());
		registry.register(MapleForest.getKey().location(), MapleForest.create());
	}
}
