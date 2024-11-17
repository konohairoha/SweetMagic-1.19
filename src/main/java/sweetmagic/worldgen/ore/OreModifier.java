package sweetmagic.worldgen.ore;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.RegistryObject;
import sweetmagic.worldgen.flower.BaseModifier;

public record OreModifier(GenerationStep.Decoration step, Holder<PlacedFeature> feature) implements BiomeModifier {

	public static final RegistryObject<Codec<? extends BiomeModifier>> OREMOD = BaseModifier.getModifier("smores");

	@Override
	public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder build) {
		BaseModifier.addFeature(phase, build, step, feature);
	}

	@Override
	public Codec<? extends BiomeModifier> codec() {
		return OREMOD.get();
	}

	public static Codec<OreModifier> makeCodec() {
		return RecordCodecBuilder.create(build -> build.group(Codec.STRING.comapFlatMap(OreModifier::generationStageFromString,
			GenerationStep.Decoration::toString).fieldOf("generation_stage").forGetter(OreModifier::step),
			PlacedFeature.CODEC.fieldOf("feature").forGetter(OreModifier::feature)).apply(build, OreModifier::new));
	}

	private static DataResult<GenerationStep.Decoration> generationStageFromString(String name) {
		return BaseModifier.generatResult(name);
	}
}
