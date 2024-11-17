package sweetmagic.init;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import sweetmagic.SweetMagicCore;

public class SoundInit {

    public static Map<SoundEvent, String> soundMap = new HashMap<>();

	public static SoundEvent PAGE = register("page");
	public static SoundEvent HEAL = register("heal");
	public static SoundEvent NEXT = register("next");
	public static SoundEvent LEVELUP = register("levelup");
	public static SoundEvent CHANGETIME = register("changetime");
	public static SoundEvent QUICK = register("quick");
	public static SoundEvent ROBE = register("robe");
	public static SoundEvent WRITE = register("write");
	public static SoundEvent POT = register("pot");
	public static SoundEvent MACHIN = register("machin");
	public static SoundEvent DROP = register("drop");
	public static SoundEvent OVEN_ON = register("oven_on");
	public static SoundEvent OVEN_FIN = register("oven_fin");
	public static SoundEvent FRYPAN = register("frypan");
	public static SoundEvent STOVE_OFF = register("stove_off");
	public static SoundEvent MAGIC_CRAFT = register("magic_craft");
	public static SoundEvent FROST = register("frost");
	public static SoundEvent FREEZER_OPEN = register("freezer_open");
	public static SoundEvent FREEZER_CLOSE = register("freezer_close");
	public static SoundEvent FREEZER_CRAFT = register("freezer_craft");
	public static SoundEvent GROW = register("grow");
	public static SoundEvent BUBBLE = register("bubble");
	public static SoundEvent ROBE_SMALL = register("robe_small");
	public static SoundEvent SWING = register("swing");
	public static SoundEvent TURN_PAGE = register("turn_page");
	public static SoundEvent JM_ON = register("jm_on");
	public static SoundEvent JM_FIN = register("jm_fin");
	public static SoundEvent LASER = register("laser");
	public static SoundEvent QUEEN_VOICE = register("queen_voice");
	public static SoundEvent QUEEN_DAME = register("queen_dame");
	public static SoundEvent HORAMAGIC = register("horamagic");
	public static SoundEvent IRON = register("iron");
	public static SoundEvent CRITICAL = register("critical");
	public static SoundEvent RECAST = register("recast");
	public static SoundEvent FLASH = register("flash");
	public static SoundEvent RECYCLER_ON = register("recycler_on");
	public static SoundEvent RECYCLER_FIN = register("recycler_fin");
	public static SoundEvent RECAST_CLEAR = register("recast_clear");
	public static SoundEvent KNIFE_SHOT = register("knife_shot");
	public static SoundEvent RIFLE_SHOT = register("rifle_shot");

	public static SoundEvent register (String name) {
		SoundEvent sound = new SoundEvent(SweetMagicCore.getSRC(name));
		soundMap.put(sound, name);
		return sound;
	}

	@SubscribeEvent
	public static void registerSound(RegisterEvent event) {
		event.register(ForgeRegistries.Keys.SOUND_EVENTS, h -> soundMap.forEach((key, val) -> h.register(SweetMagicCore.getSRC(val), key)));
	}
}
