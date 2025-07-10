package sweetmagic.api.iitem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.emagic.SMMagicType;
import sweetmagic.api.iitem.info.MagicInfo;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.config.SMConfig;
import sweetmagic.init.AdvancedInit;
import sweetmagic.init.EnchantInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.item.magic.RankUpMagic;
import sweetmagic.init.tile.inventory.SMInventory.SMWandInventory;
import sweetmagic.init.tile.menu.container.BaseContainer.ContainerWand;

public interface IWand extends IMFTool {

	// NBT用の変数
	public static final String SLOT = "slot";			// 選択中のスロット
	public static final String SLOTCOUNT = "slotCount";	// スロットの数
	public static final String EXP = "exp";				// 経験値
	public static final String LEVEL = "level";			// レベル
	public static final String ELEMENT = "element";		// 属性
	public static final String IMMEDTIME = "immedtime";	// 即発動受付時間
	public static final String IMMEDSLOT = "immedslot";	// 前回発動時の魔法スロットID
	public static final String SMWAND = "SMWand";
	public static final String WANDCHECK = "WandCheck";

	public static final List<EnchantmentCategory> ENCHACATELIST = Arrays.<EnchantmentCategory> asList(
		EnchantInit.ISMFTOOL, EnchantInit.ISWAND_HARNESS, EnchantInit.ISWAND, EnchantInit.ISWAND5, EnchantInit.ISALL
	);

	/*
	 * =========================================================
	 * 				アクション登録　Start
	 * =========================================================
	 */

	// 右クリック
	InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand);

	void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int charge);

	/*
	 * =========================================================
	 * 				アクション登録　End
	 * =========================================================
	 */


	/*
	 * =========================================================
	 * 				アクション処理　Start
	 * =========================================================
	 */


	default InteractionResultHolder<ItemStack> onItemRightClick(Level world, Player player, InteractionHand hand) {

		// アイテムスタックを取得
		ItemStack stack = player.getItemInHand(hand);
		if (stack.isEmpty()) { return InteractionResultHolder.fail(stack); }

		WandInfo wandInfo = new WandInfo(stack);					// 杖情報を取得
		ItemStack slotItem = this.getSlotItem(player, wandInfo);	// 選択中のアイテムを取得
		if (slotItem.isEmpty() || !(slotItem.getItem() instanceof IMagicItem)) { return InteractionResultHolder.fail(stack); }

		// 魔法の情報を取得
		MagicInfo magicInfo = new MagicInfo(slotItem);
		IMagicItem item = magicInfo.getMagicItem();
		this.setMagicItem(item);

		// 射撃タイプで分別
		switch (item.getMagicType()) {

		// 射撃タイプ
		case SHOT:
			if (this.isScope()) {
				player.startUsingItem(hand);
			}

			else {
				this.shotterActived(world, player, wandInfo, magicInfo);
			}
			break;

		// 空中タイプ
		case NORMAL:
		case FIELD:
		case SUMMON:
		case BOSS:
			this.airActived(world, player, wandInfo, magicInfo);
			break;
		case CHARGE:
			player.startUsingItem(hand);
			break;
		default:
			return InteractionResultHolder.fail(stack);
		}

		return InteractionResultHolder.consume(stack);
	}

	//右クリックチャージをやめたときに矢を消費せずに矢を射る
	default void onPlayerStoppedUsing(ItemStack stack, Level world, LivingEntity living, int timeLeft) {
		if (!(living instanceof Player player) || stack.isEmpty()) { return;}

		// 選択中のアイテムを取得
		WandInfo wandInfo = new WandInfo(stack);
		ItemStack slotItem = this.getSlotItem(player, wandInfo);
		player.stopUsingItem();
		if (slotItem.isEmpty() || !(slotItem.getItem() instanceof IMagicItem)) { return; }

		// 魔法の情報を取得
		MagicInfo magicInfo = new MagicInfo(slotItem);
		IMagicItem item = magicInfo.getMagicItem();
		this.setMagicItem(item);

		float rate = 20F;
		ItemStack leg = player.getItemBySlot(EquipmentSlot.LEGS);

		if (!leg.isEmpty() && leg.getItem() instanceof IPorch portch && portch.hasAcce(leg, ItemInit.magician_quillpen)) {
			rate /= 4F;
		}

		int i = this.getMaxItemUseDuration(stack) - timeLeft;
		this.setChargeTick(this.getArrowVelocity(i, 1F, rate));

		// 射撃タイプで分別
		switch (item.getMagicType()) {

		// 地面タイプ
		case CHARGE:
			this.chargeActived(world, player, wandInfo, magicInfo);
			break;
		// 射撃タイプ
		case SHOT:
			if (!this.isScope()) { return; }
			this.shotterActived(world, player, wandInfo, magicInfo);
			break;
		default:
			return;
		}
	}

	// 射撃処理
	default void shotterActived(Level world, Player player, WandInfo wandInfo, MagicInfo magicInfo) {

		// クリエワンド以外で魔法が使えるかチェック
		if(!this.magicActionBeforeCheck(player, wandInfo, magicInfo)) { return; }

		// アイテムの処理を実行
		boolean actionFlag = this.onAction(world, player, wandInfo, magicInfo);

		// 魔法アクション後の処理
		this.magicActionAfter(world, player, wandInfo, magicInfo, actionFlag);
	}

	// 空中処理
	default void airActived(Level world, Player player, WandInfo wandInfo, MagicInfo magicInfo) {

		// クリエワンド以外で魔法が使えるかチェック
		if(!this.magicActionBeforeCheck(player, wandInfo, magicInfo)) { return; }

		// アイテムの処理を実行
		boolean actionFlag = this.onAction(world, player, wandInfo, magicInfo);

		// 魔法アクション後の処理
		this.magicActionAfter(world, player, wandInfo, magicInfo, actionFlag);
	}

	// 溜め行動
	default void chargeActived(Level world, Player player, WandInfo wandInfo, MagicInfo magicInfo) {

		// クリエワンド以外で魔法が使えるかチェック
		if(!this.magicActionBeforeCheck(player, wandInfo, magicInfo)) { return; }

		// アイテムの処理を実行
		boolean actionFlag = this.onAction(world, player, wandInfo, magicInfo);

		// 魔法アクション後の処理
		this.magicActionAfter(world, player, wandInfo, magicInfo, actionFlag);
	}

	/*
	 * =========================================================
	 * 				アクション処理　End
	 * =========================================================
	 */


	/*
	 * =========================================================
	 * 				キー処理　Start
	 * =========================================================
	 */


	// 次のスロットへ
	default void nextSlot(Level world, Player player, ItemStack stack) {

		// nbtを取得
		CompoundTag tags = this.getNBT(stack);
		ItemStack slotItem = ItemStack.EMPTY;
		int slotCount = tags.getInt(SLOTCOUNT);
		int maxCount = 0;
		int slot = this.getSelectSlot(stack);

		while (slotItem.isEmpty() && slotCount >= maxCount) {
			slot = slot >= slotCount - 1 ? 0 : slot + 1;
			this.setSelectSlot(stack, slot);
			slotItem = this.getSlotItem(player, new WandInfo(stack));
			maxCount++;
		}
		this.playSound(world, player, SoundInit.NEXT, 0.0625F, 1F);
	}

	// 前のスロットへ
	default void backSlot(Level world, Player player, ItemStack stack) {

		// nbtを取得
		CompoundTag tags = this.getNBT(stack);
		ItemStack slotItem = ItemStack.EMPTY;
		int slotCount = tags.getInt(SLOTCOUNT);
		int maxCount = 0;
		int slot = this.getSelectSlot(stack);

		while (slotItem.isEmpty() && slotCount >= maxCount) {
			slot = slot <= 0 ? slotCount - 1 : slot - 1;
			this.setSelectSlot(stack, slot);
			slotItem = this.getSlotItem(player, new WandInfo(stack));
			maxCount++;
		}

		this.playSound(world, player, SoundInit.NEXT, 0.0625F, 1F);
	}

	// GUIを開く
	default void openGui(Level world, Player player, ItemStack stack) {

		// nbtを取得
		this.getNBT(stack);

		if (!world.isClientSide()) {
			NetworkHooks.openScreen((ServerPlayer) player, new ContainerWand(stack));
			this.playSound(world, player, SoundInit.PAGE, 0.125F, 1F);
		}
	}

	/*
	 * =========================================================
	 * 				キー処理　End
	 * =========================================================
	 */


	/*
	 * =========================================================
	 * 				魔法発動前処理　Start
	 * =========================================================
	 */

	// クリエワンド以外で魔法が使えるかチェック
	default boolean magicActionBeforeCheck(Player player, WandInfo wandInfo, MagicInfo magicInfo) {

		ItemStack stack = wandInfo.getStack();
		IMagicItem smItem = magicInfo.getMagicItem();

		// MFが消費量を超えてかつtierを満たしてるかどうか
		if (this.isCreativeWand()) { return true; }

		// MFが足りてるか
		if (!this.canUseMagic(stack, this.getCostMF(player, stack, magicInfo))) { return false; }

		// tierが杖のほうが大きいか
		if (this.checkOverTier(smItem)) { return false; }

		// クールタイムがあるなら
		if (!smItem.isNoRecast(magicInfo.getStack())) { return false; }

		// 魔法の発動条件を満たしていないなら
		if (!smItem.canItemMagic(player.getLevel(), player, wandInfo)) { return false; }
		return true;
	}

	// 魔法が必要MFを超えているかどうか
	default boolean canUseMagic(ItemStack stack, int mf) {
		return this.getNBT(stack).getInt(MF) >= mf;
	}

	// tierが足りているか
	default boolean checkOverTier(IMagicItem item) {
		return item.getTier() > this.getWandTier();
	}

	/*
	 * =========================================================
	 * 				魔法発動前処理　End
	 * =========================================================
	 */


	/*
	 * =========================================================
	 * 				魔法発動中処理　Start
	 * =========================================================
	 */

	// 魔法アクション中の処理
	default boolean onAction(Level world, Player player, WandInfo wandInfo, MagicInfo magicInfo) {
		// 魔法が発動出来たかのフラグを取得
		return magicInfo.getMagicItem().onItemAction(world, player, wandInfo, magicInfo);
	}

	/*
	 * =========================================================
	 * 				魔法発動中処理　End
	 * =========================================================
	 */


	/*
	 * =========================================================
	 * 				魔法発動後処理　Start
	 * =========================================================
	 */


	// 魔法アクション後の処理
	default void magicActionAfter(Level world, Player player, WandInfo wandInfo, MagicInfo magicInfo, boolean actionFlag) {
		if (this.isCreativeWand()) { return; }

		ItemStack stack = wandInfo.getStack();
		ItemStack magicStack = magicInfo.getStack();
		IMagicItem smItem = magicInfo.getMagicItem();

		// 属性一致ボーナス効果設定
		this.setElementBonus(wandInfo, magicInfo);

		// 即発動フラグをfalseに
		smItem.setImmedFlag(magicStack, false);

		// リキャストタイムの設定
		smItem.setRecastTime(magicStack, this.getRecastTime(player, stack, magicInfo));

		// 即発動受付時間の設定
		this.setImmedTime(wandInfo, 13);

		// 使用した魔法分だけ消費
		this.setMF(stack, this.setMF(player, stack, magicInfo));

		// アイテムを消費するかどうか
		if (smItem.isShirink() && !player.isCreative()) {
			this.shrinkItem(player, wandInfo, magicInfo);
		}

		// actionFlagがtrueならレベルアップチェック
		if (actionFlag && !world.isClientSide()) {
			this.levelUpCheck(world, player, stack, this.getAddExp(player, magicInfo));
		}

		// 魔法使用時に空腹ゲージ減少する設定なら
		if (SMConfig.hungerSetting.get()) {
			player.causeFoodExhaustion(magicInfo.getMagicItem().getUseMF() * 0.001F);
		}
	}

	/*
	 * =========================================================
	 * 				魔法発動後処理　End
	 * =========================================================
	 */

	//クリエワンドかどうか
	default boolean isCreativeWand() {
		return this.getWandTier() >= 6;
	}

	// MFを設定
	default int setMF(Player player, ItemStack stack, MagicInfo magicInfo) {
		return this.getMF(stack) - this.getCostMF(player, stack, magicInfo);
	}

	// 消費MF量取得
	default int getCostMF(Player player, ItemStack stack, MagicInfo magicInfo) {

		float costDown = 0;
		IMagicItem magicItem = magicInfo.getMagicItem();

		if (magicItem.getNoMF(player)) {
			return (int) costDown;
		}

		int useMF = magicInfo.getMagicItem().getUseMF();

		// エンチャ分のMF消費の減少
		costDown += this.getEnchantLevel(EnchantInit.mfCostDown, stack) * 7;

		// バフによるMF消費量の減少
		costDown += this.getPotionLevel(player, PotionInit.mfcostdown) * 10;

		// 属性一致ボーナスが0以上ならMF消費減少
		if (this.getElementBonus() > 0F) {
			costDown += this.getElementBonus() * 15F;
		}

		// アクセサリー分のMF消費量の増減
		costDown += this.acceCostRate(player);

		// MF消費量が99%より大きくならないように
		costDown = Math.min(99, costDown);

		if (costDown != 0) {
			useMF *= (100 - costDown) / 100F;
		}

		// 属性一致によるボーナス
		return useMF *= this.getElementMatchDown();
	}

	// リキャストタイムの取得
	default int getRecastTime(Player player, ItemStack stack, MagicInfo info) {

		int recastDown = 0;
		Level world = player.getLevel();
		IMagicItem magicItem = info.getMagicItem();

		if (magicItem.getNoRecast(player)) {
			this.playSound(world, player, SoundInit.RECAST_CLEAR, 0.25F, 1F);
			return magicItem.isUniqueMagic() ? (int) (magicItem.getMaxRecastTime() * 0.05F) : recastDown;
		}

		int recastTime = magicItem.getMaxRecastTime();
		int enchantLevel = this.getEnchantLevel(EnchantInit.recastTimeDown, stack);

		// エンチャ分のリキャストタイムを減らす
		recastDown += Math.min(enchantLevel, 5) * 7;

		if (enchantLevel > 5) {
			recastDown += (enchantLevel - 5) * 3;
		}

		// 即発動受付時間内ならリキャストタイム減少
		if (!this.isEmptyImmedTime(stack) && this.diffSlot(stack)) {
			recastDown += 10;
			magicItem.setImmedFlag(info.getStack(), true);

			if (!world.isClientSide()) {
				this.playSound(world, player, SoundInit.QUICK, 0.0625F, 1F);
			}
		}

		// 属性一致ボーナスが0以上ならリキャスト減少
		if (this.getElementBonus() > 0F) {
			recastDown += this.getElementBonus() * 10F;
		}

		// 選択中のスロットを前回使用使用したスロットIDに設定
		this.setImmedSlot(stack, this.getSelectSlot(stack));

		// 装備品分
		recastDown += this.acceCoolTime(world, player.getItemBySlot(EquipmentSlot.LEGS), info);

		if (this.isScope() && magicItem.getMagicType().is(SMMagicType.SHOT)) {
			recastDown += 10;
		}

		// リキャストタイムが99%より大きくならないように
		int maxRecast = info.getMagicItem().isUniqueMagic() ? 90 : 99;
		recastDown = Math.min(recastTime, recastDown);

		// リキャストタイム減少が0より大きいなら
		if (recastDown > 0) {
			recastTime *= (100 - recastDown) / 100F;
		}

		// 属性一致によるボーナス
		recastTime *= this.getElementMatchDown();

		// ユニーク魔法の場合、同じ魔法にすべてリキャスト設定
		if (magicItem.isUniqueMagic()) {
			List<ItemStack> magicList = IWand.getMagicList(IWand.getWandList(player), e -> magicItem.isEqualMagic(e, info));

			// リキャストタイムの設定
			for (ItemStack magic : magicList) {
				new MagicInfo(magic).getMagicItem().setRecastTime(magic, recastTime);
			}
		}

		return recastTime;
	}

	// エンチャレベル取得
	default int getEnchantLevel(Enchantment enchant, ItemStack stack) {
		return Math.min(EnchantmentHelper.getItemEnchantmentLevel(enchant, stack), 10);
	}

	// ポーションレベル取得
	default int getPotionLevel(Player player, MobEffect potion) {
		if (!player.hasEffect(potion)) { return 0; }
		return Math.min(player.getEffect(potion).getAmplifier() + 1, 10);
	}

	// 射撃魔法以外ならエンチャレベルだけを返す
	default int addWandLevel(Level world, Player player, ItemStack stack, IMagicItem smItem, Enchantment enchant) {
		int enchaLevel = this.getEnchantLevel(enchant, stack);
		if (!smItem.getMagicType().is(SMMagicType.SHOT)) { return enchaLevel; }
		return enchaLevel;
	}

	// 属性一致ボーナスの取得
	default float getElementMatchDown() {

		// 属性一致ボーナス取得
		float downTime = this.getBounusValue();

		// クールタイム減少が0以外なら
		if (downTime != 0) {
			return (100F - downTime) * 0.01F;
		}

		return 1F;
	}

	// リキャストタイム減少時間の値
	default float getBounusValue() {
		return 0;
	}

	// 装備品のクールタイム取得
	default int acceCoolTime(Level world, ItemStack stack, MagicInfo magicInfo) {
		if (!(stack.getItem() instanceof IPorch porch)) { return 0; }

		int coolTime = 0;

		if (porch.hasAcce(stack, ItemInit.witch_scroll)) {
			coolTime += 10;
		}

		if (porch.hasAcce(stack, ItemInit.twilight_hourglass) && magicInfo.getMagicItem().getMagicType().is(SMMagicType.SUMMON)) {

			long worldTime = world.dayTime() % 24000;
			if (worldTime >= 10400 && worldTime < 14000) {
				coolTime += 5;
			}
		}

		return coolTime;
	}

	// 装備品のMF消費取得
	default float acceCostRate(Player player) {
		int costValue = 0;
		ItemStack leg = player.getItemBySlot(EquipmentSlot.LEGS);
		if (!(leg.getItem() instanceof IPorch porch)) { return costValue; }

		return -6.25F * porch.acceCount(leg, ItemInit.magicians_grobe, 8);
	}

	// 装備品の経験値追加
	default int getAddExp(Player player, MagicInfo magicInfo) {

		IMagicItem magic = magicInfo.getMagicItem();
		int exp = Math.max((int) magic.getUseMF() / 10, 0) + magic.addExp() * (magic.isAllShrink() ? magicInfo.getStack().getCount() : 1);
		float addPower = 1F + this.getPotionLevel(player, PotionInit.increased_experience);

		ItemStack leg = player.getItemBySlot(EquipmentSlot.LEGS);
		if (!(magic instanceof RankUpMagic) && leg.getItem() instanceof IPorch porch) {
			addPower += 0.125F * porch.acceCount(leg, ItemInit.magicians_grobe, 8);
		}

		return exp *= addPower;
	}

	// レベルアップできるかどうか
	default void levelUpCheck(Level world, Player player, ItemStack stack, int addExp) {
		if(this.isCreativeWand()) { return; }

		CompoundTag tags = this.getNBT(stack);
		int level = tags.getInt(LEVEL);			// レベル
		int exp = tags.getInt(EXP);				// 経験値
		int maxLevel = this.getMaxLevel();		// 最大レベル
		int nextLevel = 1 + level;				// 次のレベル

		// 最大レベルに達してたら終了
		if (level >= maxLevel) {
			this.checkAdavanced(player, stack);
			return;
		}

		// レベルアップ後に持ち越し用の経験値と必要経験値の取得
		int keepExp = 0;
		int nowNeedExp = this.needExp(maxLevel, nextLevel, stack);

		// 要求経験値を超えた場合に次へ持ち越し
		if (addExp > nowNeedExp) {
			keepExp = addExp - nowNeedExp;
			tags.putInt(EXP, exp + nowNeedExp);
		}

		else {
			tags.putInt(EXP, exp += addExp);
		}

		// レベルアップに必要な経験値を満たしていないなら終了
		int needExp = this.needExp(maxLevel, nextLevel, stack);
		if (needExp > 0) { return; }

		int upLevel = ++level;
		tags.putInt(LEVEL, upLevel);
		tags.putInt(EXP, needExp);

		if (!player.getLevel().isClientSide()) {
			this.playSound(world, player, SoundInit.LEVELUP, 0.0625F, 1F);
		}

		// 進捗確認
		this.checkAdavanced(player, stack);

		// 余った分を再度レベルアップに回す
		if (keepExp > 0) {
			this.levelUpCheck(world, player, stack, keepExp);
		}
	}

	// 進捗チェック
	default void checkAdavanced(Player player, ItemStack stack) {

		// 杖レベル取得
		int level = this.getNBT(stack).getInt(LEVEL);

		if(player instanceof ServerPlayer sPlayer) {
			AdvancedInit.biginerMagician.trigger(sPlayer, 10, level);
			AdvancedInit.intermediateMagician.trigger(sPlayer, 20, level);
			AdvancedInit.advancedMagician.trigger(sPlayer, 30, level);
		}
	}

	// 最大レベルの取得
	default int getMaxLevel() {
		return 50;
	}

	// 必要経験値を取得
	default int needExp(int maxLevel, int nextLevel, ItemStack stack) {
		int needExp = this.getNeedExp(nextLevel) - this.getExpValue(stack);
		return (nextLevel - 1) >= maxLevel ? 0 : needExp;
	}

	// 必要経験値を取得
	default int getNeedExp(int nextLevel) {

		int level = nextLevel - 1;							// 今のレベルを取得
		int tierLevel = level % 8 == 0 ? level - 1 : level;	// レベル8用に別の変数に
		int tier = (int) (tierLevel / 8);					// レベル8ごとに振り分け用
		int value = level - (tier * 8);						// 0～7に振り分け

		value = value != 0 ? value : 8;			// 0なら8に上げる
		tier = level == 8 ? tier - 1 : tier;	// レベル8ならtierを一つ落とす
		int exp = value * 120;					// 経験値の取得

		// レベル9以降なら一桁増やす
		if (tier > 0) {
			for (int i = 0; i < tier; i++) { exp *= 10; }
		}

		return Math.min(exp, 600000);
	}

	// 合計杖レベルの取得
	default int getWandLevel(ItemStack stack) {
		int baseLevel = this.isCreativeWand() ? this.getCreativePower() : this.getLevel(stack);
		int addLevel = Math.min(10, this.getEnchaLevel(stack, EnchantInit.wandAddPower));
		return baseLevel + addLevel;
	}

	// 即発動受付時間外かどうか
	default boolean isEmptyImmedTime(ItemStack stack) {
		return this.getImmedTime(stack) <= 0;
	}

	// 即発動受付時間の減少
	default void immedTimeElapse(ItemStack stack) {
		this.setImmedTime(stack, this.getImmedTime(stack) - 1);
	}

	// 選択しているスロットと前回使用したスロットIDが違っているなら
	default boolean diffSlot(ItemStack stack) {
		return this.getSelectSlot(stack) != this.getImmedSlot(stack);
	}

	/*
	 * =========================================================
	 * 				魔法発動後処理　Start
	 * =========================================================
	 */


	/*
	 * =========================================================
	 * 				NBT用メソッド　Start
	 * =========================================================
	 */

	// nbt初期化用
	default CompoundTag getNBT(ItemStack stack) {

		CompoundTag tags = stack.getTag();

		// NBTがnullなら初期化
		if (tags == null) {

			tags = new CompoundTag();
			tags.putInt(SLOT, 0);						// 選択スロットの初期化
			tags.putInt(SLOTCOUNT, this.getSlot());		// 最大スロット数の初期化
			tags.putInt(EXP, 0);						// 経験値の初期化
			tags.putInt(LEVEL, 1);						// レベルの初期化
			tags.putInt(MF, 0);							// MFの初期化
			tags.putString(ELEMENT, "empty");			// 属性の初期化
			tags.putInt(IMMEDTIME, 0);					// 即発動受付時間
			tags.putInt(IMMEDSLOT, 0);					// 前回発動時のスロットID

			stack.setTag(tags);
		}

		if (!tags.contains(SLOT)) {
			tags.putInt(SLOT, 0);
		}

		if (!tags.contains(SLOTCOUNT)) {
			tags.putInt(SLOTCOUNT, this.getSlot());
		}

		if (!tags.contains(EXP)) {
			tags.putInt(EXP, 0);
		}

		if (!tags.contains(LEVEL)) {
			tags.putInt(LEVEL, 1);
		}

		if (!tags.contains(MF)) {
			tags.putInt(MF, 0);
		}

		if (!tags.contains(ELEMENT)) {
			tags.putString(ELEMENT, "empty");
		}

		if (!tags.contains(IMMEDTIME)) {
			tags.putInt(IMMEDTIME, 0);
		}

		if (!tags.contains(IMMEDSLOT)) {
			tags.putInt(IMMEDSLOT, 0);
		}

		return tags;
	}

	// レベルを設定
	default int getLevel(ItemStack stack) {
		return this.getNBT(stack).getInt(LEVEL);
	}

	// 経験値を取得
	default int getExpValue(ItemStack stack) {
		return this.getNBT(stack).getInt(EXP);
	}

	// 経験値を設定
	default void setExpValue(ItemStack stack, int expValue) {
		this.getNBT(stack).putInt(EXP, expValue);
	}

	// 選択中スロットの取得
	default int getSelectSlot(ItemStack stack) {
		return this.getNBT(stack).getInt(SLOT);
	}

	// 選択中スロットの設定
	default void setSelectSlot(ItemStack stack, int slotId) {
		this.getNBT(stack).putInt(SLOT, slotId);
	}

	// スロットの取得
	default int getSlotCount(ItemStack stack) {
		return this.getNBT(stack).getInt(SLOTCOUNT);
	}

	// 属性の取得
	default String getElement(ItemStack stack) {
		return this.getNBT(stack).getString(ELEMENT);
	}

	// 属性の設定
	default void setElement(ItemStack stack, String ele) {
		this.getNBT(stack).putString(ELEMENT, ele);
	}

	// 即発動受付時間の取得
	default int getImmedTime(ItemStack stack) {
		return this.getNBT(stack).getInt(IMMEDTIME);
	}

	// 即発動受付時間の設定
	default void setImmedTime(WandInfo info, int time) {

		// 属性一致ボーナスが0以上ならリキャスト減少
		if (this.getElementBonus() > 0F) {
			time += this.getElementBonus() * 10F;
		}

		this.setImmedTime(info.getStack(), time);
	}

	// 即発動受付時間の設定
	default void setImmedTime(ItemStack stack, int time) {
		this.getNBT(stack).putInt(IMMEDTIME, time);
	}

	// 前回発動時のスロットIDの取得
	default int getImmedSlot(ItemStack stack) {
		return this.getNBT(stack).getInt(IMMEDSLOT);
	}

	// 前回発動時のスロットIDの設定
	default void setImmedSlot(ItemStack stack, int slot) {
		this.getNBT(stack).putInt(IMMEDSLOT, slot);
	}

	/*
	 * =========================================================
	 * 				NBT用メソッド　End
	 * =========================================================
	 */

	// 選択しているアイテムを取得
	default ItemStack getSlotItem(Player player, ItemStack stack, CompoundTag tags, int slot) {
		SMWandInventory inv = new WandInfo(stack).getInv();
		return inv.inv.getStackInSlot(slot);
	}

	// 選択しているアイテムを取得
	default ItemStack getSlotItem(Player player, WandInfo wandInfo) {

		// 選択しているスロットを取得
		ItemStack stack = wandInfo.getStack();
		int slot = this.getSelectSlot(stack);

		// インベントリ取得
		SMWandInventory inv = new WandInfo(stack).getInv();
		return inv.inv.getStackInSlot(slot);
	}

	// スロット内のアイテムを減らす処理
	default void shrinkItem(Player player, WandInfo wandInfo, MagicInfo magicInfo) {
		SMWandInventory inv = wandInfo.getInv();
		ItemStack selectStack = inv.getStackInSlot(this.getSelectSlot(wandInfo.getStack()));
		selectStack.shrink(magicInfo.getMagicItem().isAllShrink() ? selectStack.getCount() : 1);
		inv.writeBack();
	}

	// クリエパワーを取得
	default int getCreativePower() {
		return 60;
	}

	default boolean isScope() {
		return false;
	}

	default void shotSound(Player player) {
		this.playSound(player.getLevel(), player, SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);
	}

	default List<Component> addTip() {
		return new ArrayList<>();
	}

	//右クリックでチャージした量で射程を伸ばす
	default float getArrowVelocity(int charge, float maxTick, float rate) {
		float f = (float) charge / rate;
		f = (f * f + f * 2F) / 3F;
		return Math.min(f, maxTick);
	}

	// 最大チャージ時間
	default int getMaxItemUseDuration(ItemStack stack) {

		int chargeTime = 0;
		if (this.getMagicItem() != null) {

			SMMagicType magicType = this.getMagicItem().getMagicType();
			if (magicType != null && (magicType.is(SMMagicType.CHARGE) || magicType.is(SMMagicType.SHOT) && this.isScope())) {
				chargeTime = 72000;
			}
		}

		return chargeTime;
	}

	// アクション時の挙動
	default UseAnim getItemUseAction(ItemStack stack) {
		if (this.getMagicItem() == null) { return UseAnim.NONE; }

		SMMagicType magicType = this.getMagicItem().getMagicType();
		if (magicType != null && (magicType.is(SMMagicType.CHARGE) || magicType.is(SMMagicType.SHOT) && this.isScope())) {
			return UseAnim.BOW;
		}
		return UseAnim.NONE;
	}

	// デフォルトの座標設定
	default BlockPos getWandPos() {
		return new BlockPos(0, 0, 0);
	}

	// 属性が空以外かつtier5以上なら
	default boolean isNotElement() {
		return this.getWandElement() != null && this.getWandTier() >= 5;
	}

	// 魔法と杖の属性一致確認
	default boolean isElementEqual(IMagicItem smItem) {
		SMElement elemet = this.getWandElement();
		return smItem.getElement() == elemet || (smItem.getSubElement() != null && smItem.getSubElement() == elemet);
	}

	// 魔法と杖の属性一致確認
	default boolean isElementEqual(IMagicItem smItem, SMElement ele) {
		return smItem.getElement().is(ele) || (smItem.getSubElement() != null && smItem.getSubElement().is(ele));
	}

	// インベントリ常時更新
	default void onUpdate(ItemStack stack, Level level, Entity entity, int slot, boolean main) {
		if (!(entity instanceof Player player) || stack.isEmpty() || !(stack.getItem() instanceof IWand wand)) { return; }

		// 杖のインベントリ内の魔法の常時更新
		this.slotUpdate(level, player, stack);

		// 即発動受付時間を減少
		if (!this.isEmptyImmedTime(stack)) {
			this.immedTimeElapse(stack);
		}
	}

	// 杖のインベントリ内の魔法の常時更新
	default void slotUpdate(Level level, Player player, ItemStack stack) {

		// インベントリ取得
		WandInfo info = new WandInfo(stack);
		SMWandInventory inv = info.getInv();

		for (int i = 0; i < inv.getSlots(); i++) {

			ItemStack magicStack = inv.getStackInSlot(i);
			if (magicStack.isEmpty()) { continue; }

			// 魔法の取得
			MagicInfo magicInfo = new MagicInfo(magicStack);
			IMagicItem magicItem = magicInfo.getMagicItem();
			magicItem.onMagicUpdate(level, player, info, magicStack);

			boolean hasTag = magicStack.getTag() != null;
			magicItem.getNBT(magicStack);

			// NBT保持していなかったら通知
			if (!hasTag) {
				inv.writeBack();
			}

			if (magicItem.isNoRecast(magicStack)) { continue; }

			// リキャスト時間の経過
			magicItem.recastTimeElapse(magicStack);

			// リキャスト時間の経過を通知
			inv.writeBack();
		}
	}

	// 魔法のリストを取得
	default List<ItemStack> getMagicList(ItemStack stack) {

		// インベントリ取得
		SMWandInventory inv = new WandInfo(stack).getInv();
		List<ItemStack> stackList = new ArrayList<>();
		int slotCount = inv.getSlots();

		// 選択中のアイテムを取得
		for (int i = 0; i < slotCount; i++) {
			stackList.add(inv.inv.getStackInSlot(i));
		}

		return stackList;
	}

	// 次のスロットへ
	default ItemStack getNextStack(Level world, Player player, ItemStack stack) {

		// nbtを取得
		CompoundTag tags = this.getNBT(stack);
		ItemStack slotItem = ItemStack.EMPTY;
		int slotCount = tags.getInt(SLOTCOUNT);
		int maxCount = 0;
		int slot = this.getSelectSlot(stack);

		while (slotItem.isEmpty() && slotCount >= maxCount) {
			slot = slot >= slotCount - 1 ? 0 : slot + 1;
			slotItem = this.getSlotItem(player, stack, tags, slot);
			maxCount++;
		}

		return slotItem;
	}

	// 前のスロットへ
	default ItemStack getBackStack(Level world, Player player, ItemStack stack) {

		// nbtを取得
		CompoundTag tags = this.getNBT(stack);
		ItemStack slotItem = ItemStack.EMPTY;
		int slotCount = tags.getInt(SLOTCOUNT);
		int maxCount = 0;
		int slot = this.getSelectSlot(stack);

		while (slotItem.isEmpty() && slotCount >= maxCount) {
			slot = slot <= 0 ? slotCount - 1 : slot - 1;
			slotItem = this.getSlotItem(player, stack, tags, slot);
			maxCount++;
		}

		return slotItem;
	}

	default int getExpProgressScaled(ItemStack stack, int value) {
		int maxEXP = this.getNeedExp(this.getLevel(stack) + 1);
		int exp = this.getExpValue(stack);
		return Math.min(value, (int) (value * ((float) exp / (float) maxEXP)));
	}

	// 火力取得( レベル × 0.2 ) + 最小((レベル - 1) × 0.175, 5) + 最小( 最大(5 × (1 - (レベル - 1) × 0.02), 0), 4)
	default float getPower(float level) {
		return (level * 0.2F) + Math.min((level - 1) * 0.255F, 5) + Math.min(Math.max(6 * (1 - (level - 1) * 0.0185F), 0), 5.8F);
	}

	// ポーション効果時間(最大( 1200 × ( 1 - (レベル - 1) × 0.05 ), 0) + 450 × (レベル - 1) × 最大( 2 - (レベル - 1) × 0.1, 0.65 ))
	default int effectTime(int level) {
		return (int) (Math.max(1200 * (1 - (level - 1) * 0.05F), 0) + 450 * (level - 1) * Math.max(2 - (level - 1) * 0.1F, 0.65F));
	}

	/*
	 * =========================================================
	 * 				定義用メソッド　Start
	 * =========================================================
	 */

	// tierを取得
	int getWandTier();

	// tierを設定
	void setWandTier(int tier);

	// 最大MFを取得
	int getMaxMF(ItemStack stack);

	// 最大MFを設定
	void setMaxMF(int maxMF);

	// レベルを設定
	void setLevel(ItemStack stack, int level);

	// スロット数の取得
	int getSlot();

	// スロット数の設定
	void setSlot(int slot);

	// 溜め時間の受け取り
	float getChargeTick();

	// 溜め時間の設定
	void setChargeTick(float chargeTick);

	// 魔法の取得
	IMagicItem getMagicItem();

	// 魔法の設定
	void setMagicItem(IMagicItem magicItem);

	// 属性一致時の効果値取得
	float getElementBonus();

	// 属性一致時の効果値設定
	void setElementBonus(WandInfo wInfo, MagicInfo mInfo);

	// 杖の属性
	default SMElement getWandElement() {
		return null;
	}

	/*
	 * =========================================================
	 * 				定義用メソッド　End
	 * =========================================================
	 */


	/*
	 * =========================================================
	 * 				汎用メソッド　Start
	 * =========================================================
	 */

	// 杖の取得
	public static IWand getWand(ItemStack stack) {
		return (IWand) stack.getItem();
	}

	// レベルの取得
	public static int getLevel(IWand wand, ItemStack stack) {
		return (int) (wand.isCreativeWand() ? wand.getCreativePower() : wand.getLevel(stack));
	}

	// インベントリ内の杖リストの取得
	public static List<ItemStack> getWandList(Player player) {
		return player.getInventory().items.stream().filter(s -> s.getItem() instanceof IWand).toList();
	}

	// 杖内の魔法の取得
	public static List<ItemStack> getMagicList(List<ItemStack> wandList, Predicate<ItemStack> flag) {
		List<ItemStack> magicList = new ArrayList<>();

		for (ItemStack stack : wandList) {
			SMWandInventory inv = new WandInfo(stack).getInv();
			magicList.addAll(inv.getStackList().stream().filter(flag).toList());
		}

		return magicList;
	}

	public static ItemStack getWand(Player player) {
		ItemStack stack = player.getMainHandItem();

		if(stack.isEmpty() || !(stack.getItem() instanceof IWand)) {
			stack = player.getOffhandItem();
		}

		if(stack.isEmpty() || !(stack.getItem() instanceof IWand)) {
			stack = ItemStack.EMPTY;
		}

		return stack;
	}

	/*
	 * =========================================================
	 * 				汎用メソッド　End
	 * =========================================================
	 */

	/*
	 * =========================================================
	 * 				レンダーメソッド　Start
	 * =========================================================
	 */

	// 杖のレンダー時の設定
	default void renderWand(PoseStack pose, MultiBufferSource buffer, Player player, float parTick) {

		// スニーク時
		if (player.isShiftKeyDown() && !player.getAbilities().flying) {
			pose.translate(0.05D, 0.725D, 0.06D);
			pose.mulPose(Vector3f.ZP.rotationDegrees(225F));
		}

		// 通常
		else {
			pose.translate(0.05D, 0.55D, 0.15D);
			pose.mulPose(Vector3f.ZP.rotationDegrees(225F));
		}
	}

	/*
	 * =========================================================
	 * 				レンダーメソッド　Start
	 * =========================================================
	 */
}
