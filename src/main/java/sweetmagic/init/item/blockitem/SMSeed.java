package sweetmagic.init.item.blockitem;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.ItemInit;
import sweetmagic.init.item.sm.SMItem;

public class SMSeed extends ItemNameBlockItem implements ISMTip {

	public final String name;
	private final Block block;
	private final boolean isBag;

	public SMSeed(String name, Block block) {
		super(block, SMItem.setItem(SweetMagicCore.smFoodTab));
		this.name = name;
		this.block = block;
		this.isBag = false;
		ItemInit.itemMap.put(this, this.name);
		ItemInit.seedList.add(this);
	}

	public SMSeed(String name, Block block, boolean flag) {
		super(block, SMItem.setItem(SweetMagicCore.smFoodTab));
		this.name = name;
		this.block = block;
		this.isBag = false;
		ItemInit.itemMap.put(this, this.name);
	}

	public SMSeed(String name, Block block, boolean flag, boolean isBag) {
		super(block, SMItem.setItem(SweetMagicCore.smFoodTab));
		this.name = name;
		this.block = block;
		this.isBag = isBag;
		ItemInit.itemMap.put(this, this.name);
	}

	public SMSeed(String name, Block block, int healAmount, float saturation) {
		super(block, new Item.Properties().tab(SweetMagicCore.smFoodTab).food(foodBuild(healAmount, saturation)));
		this.name = name;
		this.block = block;
		this.isBag = false;
		ItemInit.itemMap.put(this, this.name);
		ItemInit.seedList.add(this);
	}

	public SMSeed(String name, Block block, int healAmount, float saturation, boolean flag) {
		super(block, new Item.Properties().tab(SweetMagicCore.smFoodTab).food(foodBuild(healAmount, saturation)));
		this.name = name;
		this.block = block;
		this.isBag = false;
		ItemInit.itemMap.put(this, this.name);
	}

	public String getRegistryName () {
		return this.name;
	}

	public Block getPlantBlock() {
		return this.block;
	}

	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

		ItemStack stack = player.getItemInHand(hand);
		if (stack.isEdible()) {
			player.startUsingItem(hand);
			return InteractionResultHolder.consume(stack);
		}

		else {
			return InteractionResultHolder.pass(stack);
		}
	}

	public static FoodProperties foodBuild(int healAmount, float saturation) {
		return (new FoodProperties.Builder()).nutrition(healAmount).saturationMod(saturation).build();
	}

	// ツールチップの表示
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> toolTip, TooltipFlag flag) {

		if (this.isBag) {
			toolTip.add(this.getText("quartz_seed").withStyle(GREEN));
		}

		if (this.name != "sweetpotato_seed" && this.name != "whitenet_seed") { return; }
		toolTip.add(this.getText(this.name).withStyle(GREEN));
	}
}
