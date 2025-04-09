package sweetmagic.init.item.magic;

import java.util.List;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.emagic.SMMagicType;
import sweetmagic.api.iitem.IMagicItem;
import sweetmagic.api.iitem.info.MagicInfo;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.item.sm.SMItem;

public abstract class BaseMagicItem extends SMItem implements IMagicItem, ISMTip {

	private SMElement ele;
	private SMMagicType magicType;
	private int tier;
	private int maxRecastTime;
	private int mf;
	private boolean isShirink;
	private ResourceLocation icon;

	public BaseMagicItem (String name, SMMagicType type, SMElement ele, int tier, int coolTime, int useMF, boolean isShirink) {
		super(name, setItem(SweetMagicCore.smMagicTab).setNoRepair());
		this.setMagicType(type);
		this.setElement(ele);
		this.setTier(tier);
		this.setMaxRecastTime(coolTime);
		this.setUseMF(useMF);
		this.isShirink = isShirink;
		this.icon = SweetMagicCore.getSRC("textures/item/" + name + ".png");
	}

	public BaseMagicItem(String name, SMMagicType type, SMElement ele, int tier, int coolTime, int useMF, boolean isShirink, String iconName) {
		super(name, setItem(SweetMagicCore.smMagicTab).setNoRepair());
		this.setMagicType(type);
		this.setElement(ele);
		this.setTier(tier);
		this.setMaxRecastTime(coolTime);
		this.setUseMF(useMF);
		this.isShirink = isShirink;
		this.icon = SweetMagicCore.getSRC("textures/item/" + iconName + ".png");
	}

	// 属性の取得
	@Override
	public SMElement getElement() {
		return this.ele;
	}

	// 属性の設定
	public void setElement(SMElement ele) {
		this.ele = ele;
	}

	// 魔法タイプの取得
	@Override
	public SMMagicType getMagicType() {
		return this.magicType;
	}

	// 魔法タイプの設定
	public void setMagicType(SMMagicType magicType) {
		this.magicType = magicType;
	}

	// tierの取得
	@Override
	public int getTier() {
		return this.tier;
	}

	// tierの設定
	public void setTier(int tier) {
		this.tier = tier;
	}

	// 最大リキャストタイムの取得
	@Override
	public int getMaxRecastTime() {
		return this.maxRecastTime;
	}

	// 最大リキャストタイムの設定
	public void setMaxRecastTime(int maxRecastTime) {
		this.maxRecastTime = maxRecastTime;
	}

	// 消費MFの取得
	@Override
	public int getUseMF() {
		return this.mf;
	}

	// 消費MFの設定
	public void setUseMF(int mf) {
		this.mf = mf;
	}

	// アイテム消費の設定
	@Override
	public boolean isShirink() {
		return this.isShirink;
	}

	// テクスチャのリソースを取得
	public ResourceLocation getResource() {
		return this.icon;
	}

	// 魔法発動時の動作
	@Override
	public abstract boolean onItemAction(Level world, Player player, WandInfo wandInfo, MagicInfo magicInfo);

	// ツールチップ
	public List<MutableComponent> magicToolTip(List<MutableComponent> toolTip) {
		return toolTip;
	}

	public ResourceLocation getIcon() {
		return this.icon;
	}

	// インベントリ常時更新
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean main) {
		if (!(entity instanceof Player player)) { return; }
		this.onUpdate(level, player, stack);
	}
}
