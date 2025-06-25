package sweetmagic.init.item.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iitem.IMagicItem;
import sweetmagic.api.iitem.IWand;
import sweetmagic.api.iitem.info.MagicInfo;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EnchantInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.TagInit;

public class SMWand extends DiggerItem implements IWand {

	// 変数
	public int tier;
	public int maxMF;
	public int slot;
	public int level;
	public IMagicItem slotItem;
	public float chargeTick = 0;
	public float elementBonusPower = 0F;

	public SMWand(String name, int tier, int maxMF, int slot) {
		super((-2.9F + tier * 0.5F), 6F, Tiers.DIAMOND, TagInit.ALL_TOOLS, SMItem.setItem(9999, SweetMagicCore.smMagicTab).setNoRepair().fireResistant());
		this.setWandTier(tier);
		this.setMaxMF(maxMF);
		this.setSlot(slot);
		ItemInit.itemMap.put(this, name);
	}

	/*
	 * =========================================================
	 * 				アクション登録　Start
	 * =========================================================
	 */

	// 右クリック
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		return this.onItemRightClick(world, player, hand);
	}

	// 右クリックチャージ終了時
	@Override
	public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int chargeTime) {
		this.onPlayerStoppedUsing(stack, world, entity, chargeTime);
	}

	/*
	 * =========================================================
	 * 				アクション登録　End
	 * =========================================================
	 */


	/*
	 * =========================================================
	 * 				以下インターフェース必要メソッド
	 * =========================================================
	 */

	// tierの取得
	@Override
	public int getWandTier() {
		return this.tier;
	}

	// tierの設定
	@Override
	public void setWandTier(int tier) {
		this.tier = tier;
	}

	// レベルを取得
	@Override
	public void setLevel(ItemStack stack, int level) {
		this.level = level;
	}

	// 最大MFを取得
	@Override
	public int getMaxMF(ItemStack stack) {
		int addMaxMF = (this.getEnchantLevel(EnchantInit.maxMFUP, stack) * 10) * (this.maxMF / 100);
		return this.maxMF + addMaxMF;
	}

	// 最大MFを設定
	public void setMaxMF(int maxMF) {
		this.maxMF = maxMF;
	}

	// スロット数の取得
	@Override
	public int getSlot() {
		return this.slot;
	}

	// スロット数の設定
	public void setSlot(int slot) {
		this.slot = slot;
	}

	// 溜める時間の取得
	@Override
	public float getChargeTick() {
		return this.chargeTick;
	}

	// 溜める時間の設定
	@Override
	public void setChargeTick(float chargeTick) {
		this.chargeTick = chargeTick;
	}

	// 魔法の取得
	@Override
	public IMagicItem getMagicItem() {
		return this.slotItem;
	}

	// 魔法の設定
	@Override
	public void setMagicItem(IMagicItem magicItem) {
		this.slotItem = magicItem;
	}

	// 属性一致時の効果値取得
	@Override
	public float getElementBonus() {
		return this.elementBonusPower;
	}

	// 属性一致時の効果値設定
	@Override
	public void setElementBonus(WandInfo wInfo, MagicInfo mInfo) { }

	public int getUseDuration(ItemStack stack) {
		return this.getMaxItemUseDuration(stack);
	}

	public UseAnim getUseAnimation(ItemStack stack) {
		return this.getItemUseAction(stack);
	}

	@Override
	public BlockPos getWandPos() {
		return null;
	}

	//アイテムにダメージを与える処理を無効
	public void setDamage(ItemStack stack, int damage) {
		return;
	}

	// エンチャント表示をしない
	public boolean isFoil(ItemStack stack) {
		return false;
	}

	//壊すブロックの採掘速度を変更
	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return 1F + (this.tier - 1) * 0.5F;
	}

	//全てのブロック（マテリアル）を破壊可能に
	@Override
	public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entity) {
		return true;
	}

	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		return false;
	}

	// インベントリ常時更新
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean main) {
		this.onUpdate(stack, world, entity, slot, main);
	}

	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchant) {
		return ENCHACATELIST.contains(enchant.category) && enchant.category.canEnchant(stack.getItem());
	}
}
