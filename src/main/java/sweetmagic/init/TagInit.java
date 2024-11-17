package sweetmagic.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import sweetmagic.SweetMagicCore;

public class TagInit {

	public static final TagKey<Biome> IS_SMFOREST = smBiomeTag("is_smforest");
	public static final TagKey<Biome> IS_PRISM = smBiomeTag("is_prism");
	public static final TagKey<Biome> IS_FRUIT = smBiomeTag("is_fruit");
	public static final TagKey<Biome> IS_CHERRY_BLOSSOM = smBiomeTag("is_cherry_blossom");
	public static final TagKey<Biome> IS_MAGIA = smBiomeTag("is_magia");
	public static final TagKey<Biome> IS_FLOWERGARDEN = smBiomeTag("is_flowergarden");
	public static final TagKey<Biome> IS_SWEETMAGIC = smBiomeTag("is_sweetmagic");

	public static final TagKey<Block> ALL_TOOLS = smBlockTag("mineable/all_tools");
	public static final TagKey<Block> AC_BLOCK = smBlockTag("aethercrystal_block");
	public static final TagKey<Block> DC_BLOCK = smBlockTag("divinecrystal_block");
	public static final TagKey<Block> PC_BLOCK = smBlockTag("purecrystal_block");
	public static final TagKey<Block> OVEN = smBlockTag("oven");
	public static final TagKey<Block> CHEST_READER = smBlockTag("chest_reader");

	public static final TagKey<Item> SM_PAGE = smItemTag("page");
	public static final TagKey<Item> SM_BASE = smItemTag("base");
	public static final TagKey<Item> MAGIC_ACCESSORY = smItemTag("magic_accessory");
	public static final TagKey<Item> WISH_CRYSTAL = smItemTag("wish_crystal");
	public static final TagKey<Item> MAGIC_PAGE = smItemTag("magic_page");
	public static final TagKey<Item> COSMIC_ORE = smItemTag("cosmic_ore");

	public static final TagKey<Block> STONE = forgeBlockTag("stone");

	public static final TagKey<Item> RECIPE_BOOK = forgeItemTag("recipe_book");
	public static final TagKey<Item> MAGIC_BOOK = forgeItemTag("magic_book");
	public static final TagKey<Item> MAGIC_BOOK_COSMIC = forgeItemTag("magic_book_cosmic");

	public static final TagKey<Item> BREAD = forgeItemTag("bread");
	public static final TagKey<Item> EGGS = forgeItemTag("eggs");
	public static final TagKey<Item> MILK = forgeItemTag("milk");
	public static final TagKey<Item> SEEDS = forgeItemTag("seeds");
	public static final TagKey<Item> FLOWER = minecraftItemTag("flowers");
	public static final TagKey<Item> MEAT = forgeItemTag("foods/meat/raw");

	public static final TagKey<EntityType<?>> BOSS = forgeEntityTag("bosses");
	public static final TagKey<EntityType<?>> NOT_SPECIAL = smEntityTag("not_special_treatment");

	private static TagKey<Item> minecraftItemTag(String name) {
		return ItemTags.create(new ResourceLocation("minecraft", name));
	}

	private static TagKey<Block> forgeBlockTag(String name) {
		return BlockTags.create(new ResourceLocation("forge", name));
	}

	private static TagKey<Item> forgeItemTag(String name) {
		return ItemTags.create(new ResourceLocation("forge", name));
	}

	private static TagKey<EntityType<?>> forgeEntityTag(String name) {
		return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation("forge", name));
	}

	private static TagKey<EntityType<?>> smEntityTag(String name) {
		return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, SweetMagicCore.getSRC(name));
	}

	private static TagKey<Biome> smBiomeTag(String name) {
		return TagKey.create(Registry.BIOME_REGISTRY, SweetMagicCore.getSRC(name));
	}

	private static TagKey<Block> smBlockTag(String name) {
		return BlockTags.create(SweetMagicCore.getSRC(name));
	}

	private static TagKey<Item> smItemTag(String name) {
		return ItemTags.create(SweetMagicCore.getSRC(name));
	}
}
