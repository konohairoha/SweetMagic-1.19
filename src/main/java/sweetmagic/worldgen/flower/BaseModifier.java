package sweetmagic.worldgen.flower;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.BiomeModifier.Phase;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sweetmagic.SweetMagicCore;

public class BaseModifier {

	public static RegistryObject<Codec<? extends BiomeModifier>> getModifier (String name) {
		return  RegistryObject.create(SweetMagicCore.getSRC(name), ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, SweetMagicCore.MODID);
	}

	public static void addFeature (Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder build, GenerationStep.Decoration step, Holder<PlacedFeature> feature) {
		if (phase == Phase.ADD) {
			build.getGenerationSettings().addFeature(step, feature);
		}
	}

	public static boolean is (Holder<Biome> biome, List<TagKey<Biome>> biomeList) {
		for (TagKey<Biome> biomeTag : biomeList) {
			if (biome.is(biomeTag)) { return true; }
		}
		return false;
	}

	public static DataResult<GenerationStep.Decoration> generatResult(String name) {
		try {
			return DataResult.success(GenerationStep.Decoration.valueOf(name));
		}

		catch (Exception e) {
			return DataResult.error("Not a decoration stage: " + name);
		}
	}
}
