package sweetmagic.worldgen.entity;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import sweetmagic.handler.RegisterHandler;
import sweetmagic.init.EntityInit;
import sweetmagic.init.TagInit;

public class EntityModifier implements BiomeModifier {

    public static final EntityModifier INSTANCE = new EntityModifier();

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder build) {
    	if (phase == Phase.ADD && ( biome.is(BiomeTags.IS_OVERWORLD) || biome.is(TagInit.IS_SWEETMAGIC))) {
    		EntityInit.registerSpawnSetting(build.getMobSpawnSettings().getSpawner(MobCategory.MONSTER));
    	}
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return RegisterHandler.ENHTITY_REGISTER.get();
    }
}
