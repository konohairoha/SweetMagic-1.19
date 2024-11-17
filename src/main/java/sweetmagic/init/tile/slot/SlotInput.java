package sweetmagic.init.tile.slot;

import java.util.function.Predicate;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import sweetmagic.api.SweetMagicAPI;
import sweetmagic.api.iitem.IAcce;
import sweetmagic.api.iitem.IMFTool;
import sweetmagic.api.iitem.IMagicItem;
import sweetmagic.api.iitem.info.AcceInfo;
import sweetmagic.init.ItemInit;
import sweetmagic.init.TagInit;
import sweetmagic.init.item.magic.MFStuff;
import sweetmagic.init.item.magic.MFTeleport;
import sweetmagic.init.item.magic.SMAcce;

public class SlotInput {

	public static final Predicate<ItemStack> ISMFTOOL = s -> !s.isEmpty() && s.getItem() instanceof IMFTool;	// 杖なら
	public static final Predicate<ItemStack> ISMAGICITEM = s -> !s.isEmpty() && s.getItem() instanceof IMagicItem;	// 魔法アイテムか
	public static final Predicate<ItemStack> HASMF = s -> !s.isEmpty() && SweetMagicAPI.hasMF(s);	// 魔法アイテムか
	public static final Predicate<ItemStack> ISBUCKET = s -> !s.isEmpty() && SlotInput.isBucket(s);
	public static final Predicate<ItemStack> ISSM_ACC = s -> !s.isEmpty() && s.getItem() instanceof SMAcce;
	public static final Predicate<ItemStack> ISSM_PAGE = s -> !s.isEmpty() && s.is(TagInit.SM_PAGE);
	public static final Predicate<ItemStack> ISSM_BASE = s -> !s.isEmpty() && s.is(TagInit.SM_BASE);
	public static final Predicate<ItemStack> ISMAGICBOOK = s -> !s.isEmpty() && s.is(TagInit.MAGIC_BOOK);
	public static final Predicate<ItemStack> ISREWRITE = s -> !s.isEmpty() && s.is(TagInit.WISH_CRYSTAL);
	public static final Predicate<ItemStack> ISMAGICPAGE = s -> !s.isEmpty() && s.is(TagInit.MAGIC_PAGE);
	public static final Predicate<ItemStack> ISPAGE = s -> !s.isEmpty() && s.is(ItemInit.mysterious_page);
	public static final Predicate<ItemStack> ISBOOK = s -> !s.isEmpty() && !(s.getItem() instanceof BlockItem) && !s.is(ItemInit.mysterious_page);
	public static final Predicate<ItemStack> ISENCHA = s -> !s.isEmpty() && s.isEnchanted();
	public static final Predicate<ItemStack> ISREPAIR = s -> !s.isEmpty() && s.getDamageValue() > 0;
	public static final Predicate<ItemStack> ISSTUFF = s -> !s.isEmpty() && s.getItem() instanceof MFStuff && s.getTag() != null;
	public static final Predicate<ItemStack> ISCLERO = s -> !s.isEmpty() && s.getItem() instanceof MFTeleport && s.getTag() != null && s.getTag().contains("pX");
	public static final Predicate<ItemStack> CANACCE = s -> !s.isEmpty() && SlotInput.checkAcce(s);
	public static final Predicate<ItemStack> ISDUPACCE = s -> !s.isEmpty() && SlotInput.isDupAcce(s);
	public static final Predicate<ItemStack> ISSTAR = s -> !s.isEmpty() && s.is(ItemInit.starlight);

	public static boolean isBucket (ItemStack stack) {
		return stack.is(Items.WATER_BUCKET) || stack.is(ItemInit.watercup);
	}

	// 魔法アイテムか
	public static final Predicate<ItemStack> isMagicItem (int tier) {
		return s -> !s.isEmpty() && s.getItem() instanceof IMagicItem magic && tier >= magic.getTier();
	}

	public static boolean checkAcce (ItemStack stack) {
		if ( !(stack.getItem() instanceof IAcce ) ) { return false; }

		AcceInfo info = new AcceInfo(stack);
		IAcce acce = info.getAcce();
		return acce.isDuplication() && acce.canAddStackCount(info);
	}

	public static boolean isDupAcce (ItemStack stack) {
		if ( !(stack.getItem() instanceof IAcce ) ) { return false; }

		AcceInfo info = new AcceInfo(stack);
		IAcce acce = info.getAcce();
		return acce.isDuplication() && acce.getStackCount(info) <= 1;
	}
}
