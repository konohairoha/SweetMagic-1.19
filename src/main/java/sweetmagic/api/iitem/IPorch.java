package sweetmagic.api.iitem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import sweetmagic.api.emagic.SMAcceType;
import sweetmagic.api.iitem.info.AcceInfo;
import sweetmagic.api.iitem.info.PorchInfo;
import sweetmagic.init.EnchantInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.item.magic.SMAcce;
import sweetmagic.init.tile.inventory.SMInventory.SMPorchInventory;

public interface IPorch extends ISMArmor {

	public static final List<EnchantmentCategory> ENCHACATELIST = Arrays.<EnchantmentCategory> asList(
		EnchantInit.ISALL
	);

	// GUIを開く
	void openGui(Level world, Player player, ItemStack stack);

	// スロット数
	default int getSlotSize() {
		return 8 * this.getTier();
	}

	default void playSound(Level world, Player player, SoundEvent sound, float vol, float pitch) {
		world.playSound(null, player.blockPosition(), sound, SoundSource.PLAYERS, vol, pitch);
	}

	// 常時発動処理
	default void onTick(Level world, Player player, ItemStack porch) {
		if (player.isSpectator()) { return; }

		// インベントリを取得
		List<ItemStack> stackList = this.getStackList(porch);
		if (stackList.isEmpty()) { return; }

		List<ItemStack> acceList = new ArrayList<>();

		for (ItemStack stack : stackList) {

			// アクセサリ以外かクラフトタイムがあるなら次へ
			AcceInfo info = new AcceInfo(stack);
			IAcce acce = info.getAcce();
			if (!acce.canUseEffect(world, player, info)) { continue; }

			// update処理不可のアイテムまたは重複不可で既に発動済みなら次へ
			if (!acce.isUpdateType(world, player, info) || (!acce.isDuplication() && acceList.contains(stack))) { continue; }

			// アクセサリーの常時処理へ
			acce.onUpdate(world, player, info);
			acceList.add(stack);
		}

		Set<AcceInfo> acceSet = new HashSet<>();

		for (ItemStack stack : stackList) {
			AcceInfo info = new AcceInfo(stack);
			IAcce acce = info.getAcce();
			if (!acce.getAcceType().is(SMAcceType.MUL_UPDATE) || !acce.canUseEffect(world, player, info)) { continue; }
			acceSet.add(info);
		}

		if (acceSet.isEmpty()) { return; }
		PorchInfo pInfo = new PorchInfo(porch);
		acceSet.forEach(i -> i.getAcce().onMultiUpdate(world, player, i, pInfo));
	}

	default List<ItemStack> getStackList(ItemStack stack) {
		return new PorchInfo(stack).getInv().getStackList().stream().filter(s -> s.getItem() instanceof IAcce).toList();
	}

	default boolean hasAcce(ItemStack stack, Item acce) {
		return !this.getStackList(stack).stream().filter(s -> s.is(acce)).toList().isEmpty();
	}

	default boolean hasAcceIsActive(ItemStack stack, Item acce) {
		return !this.getStackList(stack).stream().filter(s -> s.is(acce) && !s.getOrCreateTag().getBoolean(SMAcce.NOT_ACTIVE)).toList().isEmpty();
	}

	default boolean getFilter(AcceInfo info) {
		IAcce acce = info.getAcce();
		return acce.isDuplication() && acce.getAcceType().is(SMAcceType.MUL_UPDATE);
	}

	default int acceCount(ItemStack stack, Item acce, int maxValue) {

		int acceCount = 0;
		List<ItemStack> stackList = this.getStackList(stack).stream().filter(s -> s.is(acce)).toList();

		for (ItemStack s : stackList) {
			AcceInfo info = new AcceInfo(s);
			acceCount += info.getAcce().getStackCount(info);
		}

		return Math.min(acceCount, maxValue);
	}

	default void acceInvalidate(Player player, ItemStack stack, Item acce) {

		// インベントリ取得
		SMPorchInventory inv = new PorchInfo(stack).getInv();

		for (int i = 0; i < inv.getSlots(); i++) {

			ItemStack magicStack = inv.getStackInSlot(i);
			if (!magicStack.is(acce)) { continue; }

			CompoundTag tags = magicStack.getOrCreateTag();
			tags.putBoolean(SMAcce.NOT_ACTIVE, !tags.getBoolean(SMAcce.NOT_ACTIVE));

			// リキャスト時間の経過を通知
			inv.writeBack();
			boolean notActive = tags.getBoolean(SMAcce.NOT_ACTIVE);
			this.playSound(player.level, player, SoundInit.STOVE_OFF, 0.1F, !notActive ? 0.75F : 1.25F);
			player.sendSystemMessage(this.getTipArray(((MutableComponent) magicStack.getDisplayName()).withStyle(GOLD), this.getText(notActive ? "acce_invalid" : "acce_active").withStyle(notActive ? RED : GREEN)));
			break;
		}
	}

	public static IPorch getPorch(Player player) {
		ItemStack leg = player.getItemBySlot(EquipmentSlot.LEGS);
		return !leg.isEmpty() && leg.getItem() instanceof IPorch porch ? porch : null;
	}

	public static IPorch getPorch(ItemStack leg) {
		return !leg.isEmpty() && leg.getItem() instanceof IPorch porch ? porch : null;
	}
}
