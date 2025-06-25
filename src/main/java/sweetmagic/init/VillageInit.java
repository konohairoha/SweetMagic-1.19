package sweetmagic.init;

import com.google.common.collect.ImmutableSet;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sweetmagic.SweetMagicCore;

public class VillageInit {

	public static final DeferredRegister<PoiType> POI_TYPE = SweetMagicCore.getDef(ForgeRegistries.POI_TYPES);
	public static final DeferredRegister<VillagerProfession> PRO_TYPE = SweetMagicCore.getDef(ForgeRegistries.VILLAGER_PROFESSIONS);

	public static final RegistryObject<PoiType> COOK_POI = POI_TYPE.register("cooker", () -> new PoiType(ImmutableSet.copyOf(BlockInit.oven.getStateDefinition().getPossibleStates()), 1, 1));
	public static final RegistryObject<VillagerProfession> COOK_PRO = regster("cooker", COOK_POI, SoundInit.OVEN_FIN);

	public static final RegistryObject<PoiType> MAGICIAN_POI = POI_TYPE.register("magician", () -> new PoiType(ImmutableSet.copyOf(BlockInit.obmagia_top.getStateDefinition().getPossibleStates()), 1, 1));
	public static final RegistryObject<VillagerProfession> MAGICIAN_PRO = regster("magician", MAGICIAN_POI, SoundInit.WRITE);

	public static RegistryObject<VillagerProfession> regster(String name, RegistryObject<PoiType> poy, SoundEvent sound) {
		return PRO_TYPE.register(name, () -> new VillagerProfession(SweetMagicCore.MODID + name, h -> h.is(poy.getKey()), h -> h.is(poy.getKey()), ImmutableSet.of(), ImmutableSet.of(), sound));
	}
}
