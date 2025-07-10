package sweetmagic.api.iitem;

import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import sweetmagic.api.iitem.info.BookInfo;
import sweetmagic.init.ItemInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.tile.menu.container.BaseContainer.ContainerBook;
import sweetmagic.init.tile.menu.container.BaseContainer.ContainerMagicBook;

public interface IMagicBook {

	public static final String SLOTCOUNT = "slotCount";	// スロットの数

	// 確率で発動できるか確認
	default boolean checkChance(float chance, Level world) {
		return chance * 0.01F >= world.getRandom().nextFloat();
	}

	// 確率で発動できるか確認
	default float getChance(float chance) {
		return chance * 0.01F;
	}

	// インベントリのアイテム取得
	default List<ItemStack> getInvList(BookInfo info) {
		return info.getInv().getStackList();
	}

	// 攻撃ページの数でクリティカル確率上昇
	default float getAttackPage(BookInfo info) {
		return this.getPagePower(info, ItemInit.magicpage_attack, 2.5F);
	}

	// 防御ページの数でクリティカル確率上昇
	default float getDefencePage(BookInfo info) {
		return this.getPagePower(info, ItemInit.magicpage_defence, 3F);
	}

	// 回復ページの数で回復バフ確率上昇
	default float getHealPage(BookInfo info) {
		return this.getPagePower(info, ItemInit.magicpage_heal, 5F);
	}

	// MFページの数でMF消費なし確率上昇
	default float getMFPage(BookInfo info) {
		return this.getPagePower(info, ItemInit.magicpage_mf, 3F);
	}

	// リキャストページの数でリキャスト消費なし確率上昇
	default float getRecastPage(BookInfo info) {
		return this.getPagePower(info, ItemInit.magicpage_recast, 1F);
	}

	// ページの枚数の火力を取得
	default float getPagePower(BookInfo info, Item item, float rate) {
		List<ItemStack> stackList = this.getInvList(info).stream().filter(s -> s.is(item)).toList();
		int size = stackList.size();
		float power = size * rate;

		if (size == 10) {
			power += rate * 3F;
		}

		else if (size >= 5) {
			power += rate * 1.5F;
		}

		else if (size >= 3) {
			power += rate;
		}

		return power;
	}

	// GUIを開く
	default void openGui(Level world, Player player, ItemStack stack) {

		// nbtを取得
		this.getNBT(stack);

		if (!world.isClientSide()) {
			NetworkHooks.openScreen((ServerPlayer) player, new ContainerMagicBook(stack));
			this.playSound(world, player, SoundInit.PAGE, 0.125F, 1F);
		}
	}

	default void openCraftGui(Level world, Player player, ItemStack stack) {
		if (!world.isClientSide()) {
			NetworkHooks.openScreen((ServerPlayer) player, new ContainerBook(stack));
			this.playSound(world, player, SoundInit.PAGE, 0.125F, 1F);
		}
	}

	// nbt初期化用
	default CompoundTag getNBT(ItemStack stack) {

		CompoundTag tags = stack.getTag();

		// NBTがnullなら初期化
		if (tags == null) {
			tags = new CompoundTag();
			tags.putInt(SLOTCOUNT, this.getSlotSize());		// スロット数の初期化
			stack.setTag(tags);
		}

		if (!tags.contains(SLOTCOUNT)) {
			tags.putInt(SLOTCOUNT, this.getSlotSize());
		}

		return tags;
	}

	// スロットの取得
	default int getSlotCount(ItemStack stack) {
		return this.getNBT(stack).getInt(SLOTCOUNT);
	}

	default void playSound(Level world, Player player, SoundEvent sound, float vol, float pitch) {
		player.getCommandSenderWorld().playSound(null, player.blockPosition(), sound, SoundSource.PLAYERS, vol, pitch);
	}

	// スロット数
	default int getSlotSize() {
		return 3;
	}

	// プレイヤーインベントリの本を取得
	public static List<ItemStack> getBookList(Player player) {
		return player.getInventory().items.stream().filter(s -> s.getItem() instanceof IMagicBook).toList();
	}

	// 本の取得
	public static IMagicBook getBook(ItemStack stack) {
		return (IMagicBook) stack.getItem();
	}
}
