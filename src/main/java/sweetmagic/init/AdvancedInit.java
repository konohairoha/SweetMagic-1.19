package sweetmagic.init;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.advanced.CharangeTrigger;
import sweetmagic.init.advanced.CookTrigger;
import sweetmagic.init.advanced.GrantTrigger;
import sweetmagic.init.advanced.WandTrigger;

public class AdvancedInit {

	public static final WandTrigger biginerMagician = WandTrigger.create("biginer_magician");
	public static final WandTrigger intermediateMagician = WandTrigger.create("intermediate_magician");
	public static final WandTrigger advancedMagician = WandTrigger.create("advanced_magician");
	public static final CookTrigger biginerCook = CookTrigger.create("biginer_cook");
	public static final CookTrigger intermediateCook = CookTrigger.create("intermediate_cook");
	public static final CookTrigger advancedCook = CookTrigger.create("advanced_cook");
	public static final GrantTrigger all_magics = GrantTrigger.create("all/all_magics");
	public static final GrantTrigger all_boss = GrantTrigger.create("all/all_boss");
	public static final GrantTrigger advanced_magician = GrantTrigger.create("all/advanced_magician");
	public static final GrantTrigger all_foods = GrantTrigger.create("all/all_foods");
	public static final GrantTrigger all_crpos = GrantTrigger.create("all/all_crpos");
	public static final GrantTrigger advanced_cook = GrantTrigger.create("all/advanced_cook");
	public static final GrantTrigger all_biome = GrantTrigger.create("all/all_biome");
	public static final GrantTrigger overworld_dungeon_master = GrantTrigger.create("all/overworld_dungeon_master");
	public static final GrantTrigger sweetmagic_dungeon_master = GrantTrigger.create("all/sweetmagic_dungeon_master");
	public static final CharangeTrigger all_chalange = CharangeTrigger.create("all_chalange");

	public static void init() { }

	public static Map<ResourceLocation, GrantTrigger> advancedMap = new HashMap<>() {{
		this.put(SweetMagicCore.getSRC("magic/all_magics"), AdvancedInit.all_magics);
		this.put(SweetMagicCore.getSRC("magic/all_boss"), AdvancedInit.all_boss);
		this.put(SweetMagicCore.getSRC("magic/advanced_magician"), AdvancedInit.advanced_magician);
		this.put(SweetMagicCore.getSRC("cook/all_foods"), AdvancedInit.all_foods);
		this.put(SweetMagicCore.getSRC("cook/all_crpos"), AdvancedInit.all_crpos);
		this.put(SweetMagicCore.getSRC("cook/advanced_cook"), AdvancedInit.advanced_cook);
		this.put(SweetMagicCore.getSRC("adventure/sweetmagic_dungeon_master"), AdvancedInit.sweetmagic_dungeon_master);
		this.put(SweetMagicCore.getSRC("adventure/overworld_dungeon_master"), AdvancedInit.overworld_dungeon_master);
		this.put(SweetMagicCore.getSRC("adventure/all_biome"), AdvancedInit.all_biome);
	}};
}
