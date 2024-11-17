package sweetmagic.init.item.magic;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraftforge.network.NetworkHooks;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iitem.IAmorUtil;
import sweetmagic.api.iitem.IPorch;
import sweetmagic.event.KeyPressEvent;
import sweetmagic.init.ItemInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.tile.menu.container.ContainerPorch;
import sweetmagic.key.SMKeybind;

public class SMPorch extends ArmorItem implements IPorch {

	private final String name;
	private final int data;

	public SMPorch(String name, int data) {
		super(IAmorUtil.getArmorMaterial(data), EquipmentSlot.LEGS, IAmorUtil.getArmorPro());
		this.name = name;
		this.data = data;
		ItemInit.itemMap.put(this, this.name);
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
	public void openGui(Level world, Player player, ItemStack stack) {
		if (!world.isClientSide) {
			NetworkHooks.openScreen((ServerPlayer) player, new ContainerPorch(stack));
			this.playSound(world, player, SoundInit.ROBE, 0.0625F, 1F);
		}
	}

	@Override
	public void onArmorTick(ItemStack stack, Level world, Player player) {
		this.onTick(world, player, stack);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> con) {
		con.accept(IAmorUtil.ArmorPorchRender.INSTANCE);
	}

	@Override
	@Nullable
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		return SweetMagicCore.MODID + ":textures/armor/" + this.name + ".png";
	}

	@Override
	public int getTier () {
		return this.data + 1;
	}

	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment encha) {
		return ENCHACATELIST.contains(encha.category) || encha != Enchantments.UNBREAKING && encha.category.canEnchant(stack.getItem());
	}

	// ツールチップの表示
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> toolTip, TooltipFlag flag) {
		Component keyOpen = KeyPressEvent.getKeyName(SMKeybind.POUCH);
		toolTip.add(this.getTipArray(keyOpen.copy(), this.getText("key"), this.getText("open_pouch")).withStyle(RED));
		toolTip.add(this.getText("magicians_pouch").withStyle(GOLD));
	}
}
