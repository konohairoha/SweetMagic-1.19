package sweetmagic.api.magiaflux;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import sweetmagic.api.SweetMagicAPI;
import sweetmagic.init.BlockInit;
import sweetmagic.init.ItemInit;

public class MFItemList implements IMagiaFluxItemListPlugin {

	public static MFItemList INSTANCE = new MFItemList();

	public void addPluginList() {
		SweetMagicAPI.getMFPluginList().add(this);
	}

	// アイテムにMFを定義する
	@Override
	public void setMF(MagiaFluxInfo info) {

		this.setMF(info, Items.SUGAR, 20);
		this.setMF(info, Items.STRING, 10);
		this.setMF(info, Items.REDSTONE, 5);
		this.setMF(info, Items.LAPIS_LAZULI, 5);
		this.setMF(info, Items.GLOWSTONE_DUST, 50);
		this.setMF(info, Items.HONEYCOMB, 400);
		this.setMF(info, Items.HONEYCOMB_BLOCK, 1600);
		this.setMF(info, Items.HONEY_BOTTLE, 1200);
		this.setMF(info, Items.HONEY_BLOCK, 4800);
		this.setMF(info, Blocks.REDSTONE_BLOCK, 45);
		this.setMF(info, Blocks.LAPIS_BLOCK, 45);
		this.setMF(info, Blocks.GLOWSTONE, 200);
		this.setMF(info, Items.ENDER_PEARL, 180);
		this.setMF(info, Items.QUARTZ, 25);
		this.setMF(info, Items.SCULK_CATALYST, 50);
		this.setMF(info, Blocks.QUARTZ_BLOCK, 100);
		this.setMF(info, Items.AMETHYST_SHARD, 250);
		this.setMF(info, Blocks.AMETHYST_BLOCK, 1000);
		this.setMF(info, Items.EXPERIENCE_BOTTLE, 500);
		this.setMF(info, Items.INK_SAC, 50);
		this.setMF(info, Items.GLOW_INK_SAC, 750);
		this.setMF(info, Items.ECHO_SHARD, 2000);
		this.setMF(info, Items.SLIME_BALL, 10);
		this.setMF(info, Items.NETHER_WART, 10);
		this.setMF(info, Items.CHORUS_FRUIT, 10);
		this.setMF(info, Items.GLOW_BERRIES, 10);
		this.setMF(info, Items.COCOA_BEANS, 10);
		this.setMF(info, Blocks.SUGAR_CANE, 20);
		this.setMF(info, Items.GHAST_TEAR, 100);
		this.setMF(info, Items.NAUTILUS_SHELL, 500);
		this.setMF(info, Items.PHANTOM_MEMBRANE, 300);
		this.setMF(info, Items.MAGMA_CREAM, 60);
		this.setMF(info, Items.BLAZE_ROD, 100);
		this.setMF(info, Items.BLAZE_POWDER, 50);

		this.setMF(info, ItemInit.sugarbell, 50);
		this.setMF(info, ItemInit.sannyflower_petal, 50);
		this.setMF(info, ItemInit.moonblossom_petal, 50);
		this.setMF(info, ItemInit.fire_nasturtium_petal, 50);
		this.setMF(info, ItemInit.dm_flower, 50);
		this.setMF(info, ItemInit.clero_petal, 50);
		this.setMF(info, ItemInit.cotton, 50);
		this.setMF(info, ItemInit.sticky_stuff_petal, 50);
		this.setMF(info, ItemInit.paper_mint, 50);
		this.setMF(info, ItemInit.maple_syrup, 50);
		this.setMF(info, ItemInit.prizmium, 500);
		this.setMF(info, ItemInit.aether_crystal_shard, 100);
		this.setMF(info, ItemInit.cosmic_crystal_shard, 1000);
		this.setMF(info, ItemInit.fluorite, 1000);
		this.setMF(info, ItemInit.redberyl, 2000);
		this.setMF(info, ItemInit.aether_crystal, 800);
		this.setMF(info, ItemInit.divine_crystal, 9000);
		this.setMF(info, ItemInit.pure_crystal, 100000);
		this.setMF(info, ItemInit.deus_crystal, 600000);
		this.setMF(info, ItemInit.cosmic_crystal, 1000000);
		this.setMF(info, BlockInit.aethercrystal_block, 7200);
		this.setMF(info, BlockInit.divinecrystal_block, 81000);
		this.setMF(info, BlockInit.purecrystal_block, 900000);
		this.setMF(info, BlockInit.fluorite_block, 9000);
		this.setMF(info, BlockInit.redberyl_block, 18000);
		this.setMF(info, ItemInit.mf_small_bottle, 1000);
		this.setMF(info, ItemInit.mf_bottle, 10000);
		this.setMF(info, ItemInit.magia_bottle, 100000);
		this.setMF(info, BlockInit.magiaflux_block, 1000000);
		this.setMF(info, ItemInit.ender_shard, 20);
		this.setMF(info, BlockInit.magiclight, 10);

		// 定義例
		this.setMF(info, ItemInit.stray_soul, 200);
		this.setMF(info, ItemInit.electronic_orb, 200);
		this.setMF(info, ItemInit.poison_bottle, 200);
		this.setMF(info, ItemInit.unmeltable_ice, 200);
		this.setMF(info, ItemInit.grav_powder, 200);
		this.setMF(info, ItemInit.tiny_feather, 200);
		this.setMF(info, ItemInit.witch_tears, 500);
	}
}
