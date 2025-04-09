package sweetmagic.init;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iitem.IHarness;
import sweetmagic.api.iitem.IMFTool;
import sweetmagic.api.iitem.IRobe;
import sweetmagic.api.iitem.ISMArmor;
import sweetmagic.api.iitem.IWand;
import sweetmagic.init.enchant.SMEnchant1;
import sweetmagic.init.enchant.SMEnchant3;
import sweetmagic.init.enchant.SMEnchant5;
import sweetmagic.init.item.magic.StartLightWand;

public class EnchantInit {

	public static Map<Enchantment, String> enchaMap = new LinkedHashMap<>();
	private static final Enchantment.Rarity RARE = Enchantment.Rarity.RARE;
	private static final Enchantment.Rarity VERY_RARE = Enchantment.Rarity.VERY_RARE;
	public static final EnchantmentCategory ALL = create("is_all", s -> true);
	public static final EnchantmentCategory ISWAND = create("is_wand", i -> i instanceof IWand);
	public static final EnchantmentCategory ISWAND5 = create("is_wand", i -> i instanceof IWand wand && wand.getWandTier() >= 5);
	public static final EnchantmentCategory ISWAND_HARNESS = create("is_wand_harness", i -> i instanceof IWand || i instanceof IHarness || i instanceof IRobe || i instanceof StartLightWand);
	public static final EnchantmentCategory ISMFTOOL = create("is_mftool", i -> i instanceof IMFTool);
	public static final EnchantmentCategory ISALL = create("is_all", i -> i instanceof IMFTool || i instanceof ISMArmor);

	public static final Enchantment mfCostDown = new SMEnchant5("mfcostdown", ISWAND_HARNESS, RARE);
	public static final Enchantment recastTimeDown = new SMEnchant5("recasttimedown", ISWAND, VERY_RARE);
	public static final Enchantment wandAddPower = new SMEnchant5("wandaddpower", ISWAND, RARE);
	public static final Enchantment maxMFUP = new SMEnchant5("maxmfup", ISMFTOOL, RARE);
	public static final Enchantment elementBonus = new SMEnchant5("elementbonus", ISWAND5, VERY_RARE);
	public static final Enchantment aetherheal = new SMEnchant3("aetherheal", ISMFTOOL, VERY_RARE);
	public static final Enchantment aethercharm = new SMEnchant1("aethercharm", ALL, VERY_RARE);

	public static EnchantmentCategory create (String name, Predicate<Item> filter) {
		return EnchantmentCategory.create(name, filter);
	}

	@SubscribeEvent
	public static void registerEnchant(RegisterEvent event) {
		event.register(ForgeRegistries.Keys.ENCHANTMENTS, h -> enchaMap.forEach((key, val) -> h.register(SweetMagicCore.getSRC(val), key)));
	}
}
