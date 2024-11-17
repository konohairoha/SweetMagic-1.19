package sweetmagic.init;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sweetmagic.SweetMagicCore;
import sweetmagic.worldgen.tree.featuer.AbstractTreeFeatuer;
import sweetmagic.worldgen.tree.featuer.AzaleaFeatuer;
import sweetmagic.worldgen.tree.featuer.CherryFeatuer;
import sweetmagic.worldgen.tree.featuer.EstorFeatuer;
import sweetmagic.worldgen.tree.featuer.MagiaFeature;
import sweetmagic.worldgen.tree.featuer.PrismFeature;
import sweetmagic.worldgen.tree.featuer.SMTreeFeatuer;

public class FeatuerInit {

    public static final DeferredRegister<Feature<?>> REGISTER = SweetMagicCore.getDef(ForgeRegistries.FEATURES);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> PRISM = register("prism_fst", new PrismFeature(0, false));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> PRISM_SMALL = register("prism_small", new PrismFeature(0, true));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> CHESTNUT = register("chestnut", new SMTreeFeatuer(0));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> LEMON = register("lemon", new SMTreeFeatuer(1));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ORANGE = register("orange", new SMTreeFeatuer(2));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ESTOR = register("estor", new EstorFeatuer());
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> PEARCH = register("peatch", new SMTreeFeatuer(4));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> CHERRY = register("cherry", new SMTreeFeatuer(5));
    public static final RegistryObject<Feature<TreeConfiguration>> CHERRY_BIG = register("cherry_big", new CherryFeatuer());
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> AZALEA = register("azalea", new AzaleaFeatuer());
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> MAGIA = register("magia_fst", new MagiaFeature());
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> MAGIA_BIG = register("magia_big", new PrismFeature(1, false));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> LEMON_BIG = register("lemon_big", new PrismFeature(2, false));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ORANGE_BIG = register("orange_big", new PrismFeature(3, false));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ESTOR_BIG = register("estor_big", new PrismFeature(4, false));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> PEARCH_BIG = register("peatch_big", new PrismFeature(5, false));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> CHESTNUT_BIG = register("chestnut_big", new PrismFeature(6, false));

    private static RegistryObject<Feature<NoneFeatureConfiguration>> register (String name, AbstractTreeFeatuer fea) {
    	return REGISTER.register(name, () -> fea);
    }

    private static RegistryObject<Feature<TreeConfiguration>> register (String name, Feature<TreeConfiguration> fea) {
    	return REGISTER.register(name, () -> fea);
    }
}
