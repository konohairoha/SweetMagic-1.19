package sweetmagic.worldgen.ore;

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
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.RegistryObject;
import sweetmagic.init.TagInit;
import sweetmagic.worldgen.flower.BaseModifier;

public record CSOreModifier(GenerationStep.Decoration step, Holder<PlacedFeature> feature) implements BiomeModifier {

	public static final RegistryObject<Codec<? extends BiomeModifier>> OREMOD = BaseModifier.getModifier("cssmores");

	private static List<TagKey<Biome>> biomeTagList = Arrays.<TagKey<Biome>> asList(
		TagInit.IS_SWEETMAGIC
	);

	@Override
	public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder build) {
		if (BaseModifier.is(biome, biomeTagList)) {
			BaseModifier.addFeature(phase, build, step, feature);
		}
	}

	@Override
	public Codec<? extends BiomeModifier> codec() {
		return OREMOD.get();
	}

	public static Codec<CSOreModifier> makeCodec() {
		return RecordCodecBuilder.create(build -> build.group(Codec.STRING.comapFlatMap(CSOreModifier::generationStageFromString,
			GenerationStep.Decoration::toString).fieldOf("generation_stage").forGetter(CSOreModifier::step),
			PlacedFeature.CODEC.fieldOf("feature").forGetter(CSOreModifier::feature)).apply(build, CSOreModifier::new));
	}

	private static DataResult<GenerationStep.Decoration> generationStageFromString(String name) {
		return BaseModifier.generatResult(name);
	}
}
