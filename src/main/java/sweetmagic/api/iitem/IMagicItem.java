package sweetmagic.api.iitem;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.emagic.SMMagicType;
import sweetmagic.api.iitem.info.BookInfo;
import sweetmagic.api.iitem.info.MagicInfo;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.ItemInit;
import sweetmagic.util.SMDamage;

public interface IMagicItem extends ISMUtil {

	public static final String RECASTTIME = "recastTime";	// 残りリキャスト時間
	public static final String ISIMMED = "isimmed";			// 即発動したか

	// 属性の取得
	SMElement getElement();

	// 魔法タイプの取得
	SMMagicType getMagicType();

	// tierの取得
	int getTier();

	// 最大リキャストタイムの取得
	int getMaxRecastTime();

	// 消費MFの取得
	int getUseMF();

	// アイテムを消費するかどうか
	boolean isShirink();

	// 魔法発動条件が整っているか
	default boolean canItemMagic(Level world, Player player, WandInfo info) {
		return this.getMagicType() != SMMagicType.CHARGE ? true : info.getWand().getChargeTick() >= 1;
	}

	// アイテムのアクション
	boolean onItemAction(Level world, Player player, WandInfo wandInfo, MagicInfo magicInfo);

	// テクスチャのリソースを取得
	ResourceLocation getResource();

	// ツールチップ
	List<MutableComponent> magicToolTip(List<MutableComponent> toolTip);

	// 現在のリキャストタイム取得
	default int getRecastTime(ItemStack stack) {
		return this.getNBT(stack).getInt(RECASTTIME);
	}

	// 現在のリキャストタイム設定
	default void setRecastTime(ItemStack stack, int recastTime) {
		this.getNBT(stack).putInt(RECASTTIME, recastTime);
	}

	// 即発動フラグの取得
	default boolean isImmedFlag(ItemStack stack) {
		return this.getNBT(stack).getBoolean(ISIMMED);
	}

	// 即発動フラグの設定
	default void setImmedFlag(ItemStack stack, boolean isImmed) {
		this.getNBT(stack).putBoolean(ISIMMED, isImmed);
	}

	// リキャストタイムがないかどうか
	default boolean isNoRecast(ItemStack stack) {
		return this.getRecastTime(stack) <= 0;
	}

	// リキャストタイムを1tickごとに経過させる
	default void recastTimeElapse(ItemStack stack) {
		this.setRecastTime(stack, this.getRecastTime(stack) - 1);
	}

	// 火力取得
	default float getPower(WandInfo wandInfo) {
		return wandInfo.getWand().getPower(wandInfo.getLevel());
	}

	// ポーション効果時間(最大( 1200 × ( 1 - (レベル - 1) × 0.05 ), 0) + 400 × (レベル - 1) × 最大( 2 - (レベル - 1) × 0.15, 0.5 ))
	default int effectTime(WandInfo wandInfo) {
		return wandInfo.getWand().effectTime(wandInfo.getLevel());
	}

	// 魔法アイテムの取得
	public static IMagicItem getSMItem(Item item) {
		return (IMagicItem) item;
	}

	// ユニーク魔法かどうか
	default boolean isUniqueMagic() {
		return false;
	}

	default boolean isEqualMagic(ItemStack stack, MagicInfo info) {
		return stack.is(info.getItem());

	}

	// nbt初期化用
	default CompoundTag getNBT(ItemStack stack) {
		CompoundTag tags = stack.getTag();
		if (tags != null) { return tags; }

		tags = new CompoundTag();
		tags.putInt(RECASTTIME, 0);		// リキャスト時間の初期化
		tags.putBoolean(ISIMMED, false);	// 即発動フラグ
		stack.setTag(tags);
		return tags;
	}

	// アップデート
	default void onUpdate(Level world, Player player, ItemStack stack) {

		// リキャストタイムがある場合時間経過させる
		if (stack.getTag() != null && !this.isNoRecast(stack)) {
			this.recastTimeElapse(stack);
		}
	}

	// サブ属性の取得
	default SMElement getSubElement() {
		return null;
	}

	// サブ属性の設定
	default void setSubElement(SMElement ele) { }

	// 追加経験値
	default int addExp() {
		return 0;
	}

	// パーティクルスポーン
	default void spawnParticleRing(ServerLevel server, ParticleOptions particle, double range, BlockPos pos, double addY, double ySpeed, double moveValue) {

		double x = pos.getX() + 0.5D;
		double y = pos.getY() + 1D + addY;
		double z = pos.getZ() + 0.5D;

		for (double degree = -range * Math.PI; degree < range * Math.PI; degree += 0.05D) {
			double rate = range;
			server.sendParticles(particle, x + Math.cos(degree) * rate, y, z + Math.sin(degree) * rate, 0, -Math.cos(degree) * 0.25D, ySpeed, -Math.sin(degree) * 0.25D, moveValue);
		}
	}

	default boolean canTargetEffect(LivingEntity target, LivingEntity owner) {
		return owner instanceof Player ? target instanceof Enemy : target instanceof Player;
	}

	// 拡張の指輪の個数取得
	default int getExtensionRingCount(Player player) {

		ItemStack leg = player.getItemBySlot(EquipmentSlot.LEGS);

		if (!leg.isEmpty() && leg.getItem() instanceof IPorch porch) {
			return porch.acceCount(leg, ItemInit.extension_ring, 8);
		}

		return 0;
	}

	// 血吸の指輪の個数取得
	default int getBloodSuckingRing(Player player) {

		ItemStack leg = player.getItemBySlot(EquipmentSlot.LEGS);

		if (!leg.isEmpty() && leg.getItem() instanceof IPorch porch) {
			return porch.acceCount(leg, ItemInit.blood_sucking_ring, 5);
		}

		return 0;
	}

	// 血吸の指輪の個数取得
	default boolean hasBloodSuckingRing(Player player) {
		return this.getBloodSuckingRing(player) > 0;
	}

	// ホーリーチャームの個数取得
	default boolean gethollyCharm(Player player) {

		ItemStack leg = player.getItemBySlot(EquipmentSlot.LEGS);

		if (!leg.isEmpty() && leg.getItem() instanceof IPorch porch) {
			return porch.hasAcce(leg, ItemInit.holly_charm);
		}

		return false;
	}

	default void acceEffect(Player player, Object obj) {
		SMElement ele = this.getElement();
		SMElement subEle = this.getSubElement();
		if (!this.gethollyCharm(player) || !this.isElement(ele, subEle, SMElement.SHINE)) { return; }

		Level world = player.level;
		SimpleParticleType par = ParticleTypes.END_ROD;
		RandomSource rand = world.random;
		float dame = ( (float) obj ) * 0.67F;
		List<Monster> entityList = this.getEntityList(Monster.class, player, 7.5D);

		for (Monster entity : entityList) {

			int count = entity.hasEffect(MobEffects.GLOWING) ? 3 : 1;

			for (int i = 0; i < count; i++) {
				entity.hurt(SMDamage.getMagicDamage(player, player), dame);
				entity.invulnerableTime = 0;
			}

			if ( !(world instanceof ServerLevel sever) ) { continue; }

			BlockPos pos = entity.blockPosition().above();

			for (int i = 0; i < 16; ++i) {
				double d0 = this.getRand(rand, 0.1F);
				double d1 = rand.nextFloat() * 0.25D;
				double d2 = this.getRand(rand, 0.1F);
				sever.sendParticles(par, pos.getX() + this.getRand(rand, 0.5D), pos.getY() - 0.25F + this.getRand(rand, 0.25D), pos.getZ() + this.getRand(rand, 0.5D), 0, d0, d1, d2, 1F);
			}
		}
	}

	default boolean isElement (SMElement ele, SMElement subEle, SMElement checkEle) {
		return ele.is(checkEle) || ( subEle != null && subEle.is(checkEle) );
	}

	// アップデート
	default void onMagicUpdate(Level world, Player player, WandInfo wandInfo, ItemStack stack) { }

	// 回復強化ができるか
	default boolean getHealup(Player player) {
		List<ItemStack> stackBookList = IMagicBook.getBookList(player);
		if (stackBookList.isEmpty()) { return false; }

		BookInfo info = new BookInfo(stackBookList.get(0));
		IMagicBook book = info.getBook();
		return book.checkChance(book.getHealPage(info), player.level);
	}

	// MF無しができるか
	default boolean getNoMF(Player player) {
		List<ItemStack> stackBookList = IMagicBook.getBookList(player);
		if (stackBookList.isEmpty()) { return false; }

		BookInfo info = new BookInfo(stackBookList.get(0));
		IMagicBook book = info.getBook();
		return book.checkChance(book.getMFPage(info), player.level);
	}

	// リキャスト無しができるか
	default boolean getNoRecast(Player player) {
		List<ItemStack> stackBookList = IMagicBook.getBookList(player);
		if (stackBookList.isEmpty()) { return false; }

		BookInfo info = new BookInfo(stackBookList.get(0));
		IMagicBook book = info.getBook();
		return book.checkChance(book.getRecastPage(info), player.level);
	}

	default float getHealValue(Player player, float healValue) {
		boolean isHealUp = this.getHealup(player);
		return !isHealUp ? healValue : healValue * 1.5F;
	}

	default int getSummonTime() {
		return 0;
	}
}
