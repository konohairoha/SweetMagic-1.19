package sweetmagic.worldgen.flower;

import java.util.Arrays;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.RegistryObject;
import sweetmagic.init.TagInit;

public record MoonModifier(GenerationStep.Decoration step, Holder<PlacedFeature> feature) implements BiomeModifier {

	public static final RegistryObject<Codec<? extends BiomeModifier>> SERIALIZER = BaseModifier.getModifier("smflowers_moon");

	private static List<TagKey<Biome>> biomeTagList = Arrays.<TagKey<Biome>> asList(
		BiomeTags.IS_FOREST, BiomeTags.IS_TAIGA, BiomeTags.IS_JUNGLE, TagInit.IS_SWEETMAGIC
	);

	@Override
	public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder build) {
		if (BaseModifier.is(biome, biomeTagList)) {
			BaseModifier.addFeature(phase, build, step, feature);
		}
	}

	@Override
	public Codec<? extends BiomeModifier> codec() {
		return SERIALIZER.get();
	}

	public static Codec<MoonModifier> makeCodec() {
		return RecordCodecBuilder.create(build -> build.group(Codec.STRING.comapFlatMap(MoonModifier::generationStageFromString,
			GenerationStep.Decoration::toString).fieldOf("generation_stage").forGetter(MoonModifier::step),
			PlacedFeature.CODEC.fieldOf("feature").forGetter(MoonModifier::feature)).apply(build, MoonModifier::new));
	}

	private static DataResult<GenerationStep.Decoration> generationStageFromString(String name) {
		return BaseModifier.generatResult(name);
	}
}
