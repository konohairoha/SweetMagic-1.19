package sweetmagic.init;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.potion.SMEffect;

public class PotionInit {

	private static final MobEffectCategory BUFF = MobEffectCategory.BENEFICIAL;
	private static final MobEffectCategory DEBUFF = MobEffectCategory.HARMFUL;
	private static final MobEffectCategory NONE = MobEffectCategory.NEUTRAL;

	public static Map<MobEffect, String> potionMap = new HashMap<>();

	public static final MobEffect aether_armor = new SMEffect("aether_armor", 0, BUFF, false);
	public static final MobEffect aether_barrier = new SMEffect("aether_barrier", 1, BUFF, false);
	public static final MobEffect reflash_effect = new SMEffect("reflash_effect", 2, BUFF, true);
	public static final MobEffect frost = new SMEffect("frost", 3, DEBUFF);
	public static final MobEffect flame = new SMEffect("flame", 4, DEBUFF, true);
	public static final MobEffect deadly_poison = new SMEffect("deadly_poison", 5, DEBUFF, true);
	public static final MobEffect gravity = new SMEffect("gravity", 6, DEBUFF, true);
	public static final MobEffect bubble = new SMEffect("bubble", 7, DEBUFF);
	public static final MobEffect resistance_blow = new SMEffect("resistance_blow", 8, BUFF);
	public static final MobEffect prompt_feather = new SMEffect("prompt_feather", 9, BUFF);
	public static final MobEffect mfcostdown = new SMEffect("mfcostdown", 10, BUFF);
	public static final MobEffect drop_increase = new SMEffect("drop_increase", 11, BUFF);
	public static final MobEffect leader_flag = new SMEffect("leader_flag", 12, NONE);
	public static final MobEffect darkness_fog = new SMEffect("darkness_fog", 13, NONE, true);
	public static final MobEffect bleeding = new SMEffect("bleeding", 14, DEBUFF, true);
	public static final MobEffect regeneration = new SMEffect("regeneration", 15, BUFF, true);
	public static final MobEffect resurrection = new SMEffect("resurrection", 16, BUFF, true);
	public static final MobEffect blood_curse = new SMEffect("blood_curse", 17, BUFF, true);
	public static final MobEffect increased_experience = new SMEffect("increased_experience", 18, BUFF);
	public static final MobEffect increased_recovery = new SMEffect("increased_recovery", 19, BUFF);
	public static final MobEffect aether_shield = new SMEffect("aether_shield", 20, BUFF, false);
	public static final MobEffect debuff_extension = new SMEffect("debuff_extension", 21, DEBUFF, false);
	public static final MobEffect damage_cut = new SMEffect("damage_cut", 22, BUFF, false);
	public static final MobEffect attack_disable = new SMEffect("attack_disable", 23, BUFF, true);
	public static final MobEffect magic_damage_cause = new SMEffect("magic_damage_cause", 24, BUFF, false);
	public static final MobEffect magic_damage_receive = new SMEffect("magic_damage_receive", 25, DEBUFF, false);
	public static final MobEffect future_vision = new SMEffect("future_vision", 26, BUFF, false);
	public static final MobEffect non_destructive = new SMEffect("non_destructive", 27, NONE, false);
	public static final MobEffect flame_explosion_vulnerable = new SMEffect("flame_explosion_vulnerable", 28, NONE, false);
	public static final MobEffect flost_water_vulnerable = new SMEffect("flost_water_vulnerable", 29, NONE, false);
	public static final MobEffect lightning_wind_vulnerable = new SMEffect("lightning_wind_vulnerable", 30, NONE, false);
	public static final MobEffect dig_poison_vulnerable = new SMEffect("dig_poison_vulnerable", 31, NONE, false);
	public static final MobEffect recast_reduction = new SMEffect("recast_reduction", 32, NONE, false);

	@SubscribeEvent
	public static void registerPotion(RegisterEvent event) {
		event.register(ForgeRegistries.Keys.MOB_EFFECTS, h -> potionMap.forEach((key, val) -> h.register(SweetMagicCore.getSRC(val), key)));
	}
}
