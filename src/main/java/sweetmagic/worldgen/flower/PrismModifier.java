package sweetmagic.worldgen.flower;

import java.util.Arrays;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.RegistryObject;
import sweetmagic.init.TagInit;

public record PrismModifier(GenerationStep.Decoration step, Holder<PlacedFeature> feature) implements BiomeModifier {

	public static final RegistryObject<Codec<? extends BiomeModifier>> SERIALIZER = BaseModifier.getModifier("smflower_prism");

	private static List<TagKey<Biome>> biomeTagList = Arrays.<TagKey<Biome>> asList(
		Tags.Biomes.IS_UNDERGROUND, TagInit.IS_PRISM
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

	public static Codec<PrismModifier> makeCodec() {
		return RecordCodecBuilder.create(builder -> builder.group(Codec.STRING.comapFlatMap(PrismModifier::generationStageFromString,
			GenerationStep.Decoration::toString).fieldOf("generation_stage").forGetter(PrismModifier::step),
			PlacedFeature.CODEC.fieldOf("feature").forGetter(PrismModifier::feature)).apply(builder, PrismModifier::new));
	}

	private static DataResult<GenerationStep.Decoration> generationStageFromString(String name) {
		return BaseModifier.generatResult(name);
	}
}
