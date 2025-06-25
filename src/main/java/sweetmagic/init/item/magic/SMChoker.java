package sweetmagic.init.item.magic;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.antlr.v4.runtime.misc.NotNull;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iitem.IAmorUtil;
import sweetmagic.api.iitem.IChoker;
import sweetmagic.api.iitem.IMFTool;
import sweetmagic.init.EnchantInit;
import sweetmagic.init.ItemInit;

public class SMChoker extends ArmorItem implements IChoker {

	public final int data;
	private int tickTime = 0;
	private final String name;
	public int maxMF;
	private static final EquipmentSlot[] SLOT = new EquipmentSlot[] { EquipmentSlot.CHEST, EquipmentSlot.FEET, EquipmentSlot.MAINHAND };

	public SMChoker(String name, int data, int maxMF) {
		super(IAmorUtil.getArmorMaterial(data), EquipmentSlot.HEAD, IAmorUtil.getArmorPro());
		this.name = name;
		this.data = data;
		this.setMaxMF(maxMF);
		ItemInit.itemMap.put(this, this.name);
	}

	@Override
	public void onArmorTick(ItemStack stack, Level world, Player player) {
		if (this.isMFEmpty(stack) || ++this.tickTime % 200 != 0) { return; }

		this.tickTime = 0;

		for (EquipmentSlot slot : SLOT) {
			ItemStack armor = player.getItemBySlot(slot);
			if (armor.isEmpty() || !(armor.getItem() instanceof IMFTool mfTool) || mfTool.isMaxMF(armor)) { continue; }

			// 消費MFを取得
			int mf = this.getMF(stack);
			int useMF = Math.min(this.getHealValue(), mf);
			int needMF = mfTool.insetMF(armor, useMF);
			this.setMF(stack, mf - needMF);
			if (this.isMFEmpty(stack)) { return; }
		}
	}

	public int getHealValue() {
		switch (this.data) {
		case 1: return 5000;
		case 2: return 25000;
		default: return 1000;
		}
	}

	@Override
	public int getMaxMF(ItemStack stack) {
		int addMaxMF = (this.getEnchantLevel(EnchantInit.maxMFUP, stack) * 10) * (this.maxMF / 100);
		return this.maxMF + addMaxMF;
	}

	@Override
	public void setMaxMF(int maxMF) {
		this.maxMF = maxMF;
	}

	@Override
	public int getBarColor(@NotNull ItemStack stack) {
		return this.getMF(stack) >= this.getMaxMF(stack) ? 0X30FF89 : 0X00C3FF;
	}

	@Override
	public boolean isBarVisible(@NotNull ItemStack stack) {
		return this.getMF(stack) != 0;
	}

	@Override
	public int getBarWidth(@NotNull ItemStack stack) {
		return Math.min(13, Math.round(13F * (float) this.getMF(stack) / (float) this.getMaxMF(stack)));
	}

	//アイテムにダメージを与える処理を無効
	public void setDamage(ItemStack stack, int damage) {
		return;
	}

	// エンチャント表示をしない
	public boolean isFoil(ItemStack stack) {
		return false;
	}

	@Override
	public int getTier() {
		return this.data + 1;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> con) {
		con.accept(IAmorUtil.ArmorRobeRender.INSTANCE);
	}

	@Override
	@Nullable
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		return SweetMagicCore.MODID + ":textures/block/empty.png";
	}

	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment encha) {
		return ENCHACATELIST.contains(encha.category) || ( encha != Enchantments.UNBREAKING && encha.category.canEnchant(stack.getItem()));
	}

	// ツールチップの表示
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> toolTip, TooltipFlag flag) {
		toolTip.add(this.getText("aether_choker").withStyle(GOLD));
	}
}
