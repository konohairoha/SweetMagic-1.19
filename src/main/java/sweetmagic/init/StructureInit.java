package sweetmagic.init;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import sweetmagic.SweetMagicCore;
import sweetmagic.worldgen.structure.SMStructure;

public class StructureInit {

    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPE = DeferredRegister.create(Registry.STRUCTURE_TYPE_REGISTRY, SweetMagicCore.MODID);
    public static RegistryObject<StructureType<SMStructure>> SM_JIGSAW_STRUCTURE = STRUCTURE_TYPE.register("sm_jigsaw", () -> () -> SMStructure.CODEC);

    public static int count = 0;
    public static Map<Integer, StructureInfo> strucMap = new LinkedHashMap<>();

    public static void init () {
    	register(1, false, "tomb");
    	register(2, false, "witch_house");
    	register(2, false, "well");
    	register(3, false, "witch_tower");
    	register(3, false, "labyrinth");
    	register(3, false, "desert_mine");
    	register(2, false, "ruins_site");
    	register(2, false, "ruins_site_light");
    	register(3, false, "ruins_site_fire");
    	register(3, false, "ruins_site_wind");
    	register(3, true, "arena");
    	register(3, true, "witch_large_house");
    	register(3, true, "well_old");
    	register(4, true, "white_silver_house");
    }

    public static void register(int level, boolean isSMDim, String name) {
    	strucMap.put(count , new StructureInfo(level, isSMDim, name));
    	count++;
    }

    public record StructureInfo(int level, boolean isSMDim, String name) {

    	public int getLevel () {
    		return this.level;
    	}

    	public String getDim () {
    		return this.isSMDim ? "sweetmagic" : "overworld";
    	}

    	public String getName () {
    		return this.name;
    	}
    }
}
