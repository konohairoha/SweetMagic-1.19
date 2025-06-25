package sweetmagic.init.item.magic;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.antlr.v4.runtime.misc.NotNull;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkHooks;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iitem.IAmorUtil;
import sweetmagic.api.iitem.IRobe;
import sweetmagic.event.KeyPressEvent;
import sweetmagic.init.EnchantInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.tile.menu.container.BaseContainer.ContainerRobe;
import sweetmagic.key.SMKeybind;

public class SMRobe extends ArmorItem implements IRobe, IAmorUtil {

	private final String name;
	public final int data;
	public int maxMF;

	public SMRobe(String name, int data, int maxMF) {
		super(IAmorUtil.getArmorMaterial(data), EquipmentSlot.CHEST, IAmorUtil.getArmorPro());
		this.name = name;
		this.data = data;
		this.setMaxMF(maxMF);
		ItemInit.itemMap.put(this, this.name);
	}

	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
		ImmutableMultimap.Builder<Attribute, AttributeModifier> map = ImmutableMultimap.builder();
		map.putAll(super.getDefaultAttributeModifiers(slot));
		map.put(ForgeMod.ATTACK_RANGE.get(), new AttributeModifier(IAmorUtil.SMATTACKREACH, "SM Reach", 2.5F * this.data, AttributeModifier.Operation.ADDITION));
		return slot == EquipmentSlot.CHEST ? map.build() : super.getDefaultAttributeModifiers(slot);
	}

	//アイテムにダメージを与える処理を無効
	public void setDamage(ItemStack stack, int damage) {
		return;
	}

	// エンチャント表示をしない
	public boolean isFoil(ItemStack stack) {
		return false;
	}

	// SMモブのダメージカット率（1だとダメージカット無し）
	public float getSMMobDamageCut() {
		return this.getTier() == 1 ? 0.67F : 0.5F;
	}

	// 魔法ダメージカット率（1だとダメージカット無し）
	public float getMagicDamageCut() {
		return this.getTier() == 1 ? 0.67F : 0.35F;
	}

	@Override
	public void openGui(Level world, Player player, ItemStack stack) {
		if (!world.isClientSide) {
			NetworkHooks.openScreen((ServerPlayer) player, new ContainerRobe(stack));
			this.playSound(world, player, SoundInit.ROBE, 0.0625F, 1F);
		}
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> con) {
		con.accept(IAmorUtil.ArmorRobeRender.INSTANCE);
	}

	@Override
	@Nullable
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String typeIn) {
		return SweetMagicCore.MODID + ":textures/armor/" + this.name + ".png";
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
	public int getTier() {
		return this.data + 1;
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

	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchant) {
		return ENCHACATELIST.contains(enchant.category) || (enchant != Enchantments.UNBREAKING && enchant.category.canEnchant(stack.getItem()));
	}

	// ツールチップの表示
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> toolTip, TooltipFlag flag) {
		Component keyOpen = KeyPressEvent.getKeyName(SMKeybind.OPEN);
		toolTip.add(this.getTipArray(keyOpen.copy(), this.getText("key"), this.getText("magicians_robe")).withStyle(RED));
	}
}
