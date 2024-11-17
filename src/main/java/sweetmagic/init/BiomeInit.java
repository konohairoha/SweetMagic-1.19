package sweetmagic.init;

import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.IForgeRegistry;
import sweetmagic.worldgen.biome.CherryForest;
import sweetmagic.worldgen.biome.FlowerGardenBiome;
import sweetmagic.worldgen.biome.FruitForest;
import sweetmagic.worldgen.biome.FruitTowerForestBiome;
import sweetmagic.worldgen.biome.MagiaWoodForestBiome;
import sweetmagic.worldgen.biome.PrismForest;

public class BiomeInit {

    public static void registerBiome(IForgeRegistry<Biome> registry) {
        registry.register(PrismForest.getKey(0).location(), PrismForest.create());
        registry.register(FruitForest.getKey(0).location(), FruitForest.create());
        registry.register(CherryForest.getKey(0).location(), CherryForest.create());
        registry.register(PrismForest.getKey(1).location(), PrismForest.create());
        registry.register(FruitForest.getKey(1).location(), FruitForest.create());
        registry.register(CherryForest.getKey(1).location(), CherryForest.create());
        registry.register(FlowerGardenBiome.getKey().location(), FlowerGardenBiome.create());
        registry.register(MagiaWoodForestBiome.getKey().location(), MagiaWoodForestBiome.create());
        registry.register(FruitTowerForestBiome.getKey().location(), FruitTowerForestBiome.create());
    }
}
