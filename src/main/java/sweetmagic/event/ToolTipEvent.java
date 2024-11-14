package sweetmagic.event;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.SweetMagicAPI;
import sweetmagic.api.emagic.SMMagicType;
import sweetmagic.api.event.SMUtilEvent;
import sweetmagic.api.iitem.IAcce;
import sweetmagic.api.iitem.IChoker;
import sweetmagic.api.iitem.IFood;
import sweetmagic.api.iitem.IFood.PotionInfo;
import sweetmagic.api.iitem.IHarness;
import sweetmagic.api.iitem.IMagicBook;
import sweetmagic.api.iitem.IMagicItem;
import sweetmagic.api.iitem.IPorch;
import sweetmagic.api.iitem.IRobe;
import sweetmagic.api.iitem.IWand;
import sweetmagic.api.iitem.info.AcceInfo;
import sweetmagic.api.iitem.info.BookInfo;
import sweetmagic.init.item.magic.SMAcce;
import sweetmagic.key.SMKeybind;

@Mod.EventBusSubscriber(modid = SweetMagicCore.MODID, value = Dist.CLIENT)
public class ToolTipEvent extends SMUtilEvent {

	@SubscribeEvent
	public static void toolTipEvent(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		if (stack.isEmpty()) { return; }

		List<Component> toolTip = event.getToolTip();
		Item item = stack.getItem();

		// 杖に入れる魔法アイテムなら
		if (item instanceof IMagicItem smItem) {

			toolTip.add(getTipArray(getText("tier").withStyle(GREEN), "： ", getLabel( "" + smItem.getTier()).withStyle(WHITE)));

			// シフトを押したとき
			if (Screen.hasShiftDown()) {

				MutableComponent tipEle = enumString(smItem.getElement().name());
				MutableComponent tipType = enumString(smItem.getMagicType().name());

				if (smItem.getSubElement() != null) {
					tipEle = getTipArray(tipEle, "/", enumString(smItem.getSubElement().name()));
				}

				toolTip.add(getTipArray(getText("smtype").withStyle(GREEN), "： ", tipType.withStyle(WHITE)));
				toolTip.add(getTipArray(getText("smelement").withStyle(GREEN), "： ", tipEle.withStyle(WHITE)));
				toolTip.add(getTipArray(getText("requiremf").withStyle(GREEN), "： ", getLabel(String.format("%,d", smItem.getUseMF())).withStyle(WHITE) ));

				float maxRecast = (float) smItem.getMaxRecastTime() / 20F;
				toolTip.add(getTipArray(getText("recasttime").withStyle(GREEN), "： ", getLabel("" + maxRecast).withStyle(WHITE) ));

				if (smItem.isShirink()) {
					toolTip.add(getTipArray(getText("shrink_item").withStyle(GREEN), "： ", getLabel("true").withStyle(WHITE) ));
				}

				if (smItem.getMagicType() == SMMagicType.SUMMON) {
					toolTip.add(getTipArray(getText("summon_time").withStyle(GREEN), "： ", getLabel( String.format("%,d", smItem.getSummonTime())).withStyle(WHITE) ) );
				}

				toolTip.add(empty());

				// ユニーク魔法の場合
				if (smItem.isUniqueMagic()) {
					toolTip.add(getText("unique_magic").withStyle(RED));
				}

				// Stringのリストを作成
				List<MutableComponent> list = new ArrayList<>();
				List<MutableComponent> magicTipList = smItem.magicToolTip(list);
				if (magicTipList.isEmpty()) { return; }

				if (magicTipList.size() == 1) {
					toolTip.add(getTipArray(getText("effect").withStyle(GREEN), "： ", magicTipList.get(0).withStyle(GOLD)));
				}

				else {

					int i = 0;

					for (MutableComponent tip : magicTipList) {
						i++;
						toolTip.add(getTipArray(getText("effect").withStyle(GREEN), i + "： ", tip.withStyle(GOLD)));
					}
				}
			}

			// シフトを押してないとき
			else {
				getShiftTip(toolTip);
			}
		}

		// 杖のとき
		else if (item instanceof IWand wand) {

			int level = wand.getLevel(stack);

			// tier
			toolTip.add(getTipArray(getText("tier").withStyle(GREEN), ": ", getLabel("" + wand.getWandTier()).withStyle(WHITE)));

			// シフトを押したとき
			if (Screen.hasShiftDown()) {

				toolTip.add(empty());

				if (level <= 5) {

					// キー操作
					MutableComponent keyOpen = KeyPressEvent.getKeyName(SMKeybind.OPEN);
					MutableComponent keyNext = KeyPressEvent.getKeyName(SMKeybind.NEXT);
					MutableComponent keyBack = KeyPressEvent.getKeyName(SMKeybind.BACK);

					toolTip.add(getTipArray(keyOpen.copy(), getText("key"), getText("open").withStyle(WHITE)).withStyle(RED));
					toolTip.add(getTipArray(keyNext.copy(), getText("key"), getText("next").withStyle(WHITE)).withStyle(RED));
					toolTip.add(getTipArray(keyBack.copy(), getText("key"), getText("back").withStyle(WHITE)).withStyle(RED));
				}

				toolTip.add(getText("mousescroll").withStyle(GOLD));
				toolTip.add(getText("shiftleft").withStyle(GOLD));

				if (wand.isScope()) {
					wand.addTip().forEach(t -> toolTip.add(t));
				}

				toolTip.add(empty());

				// クリエワンドなら終了
				if (wand.isCreativeWand()) { return; }

				// 次のレベル
				int lv = wand.getLevel(stack);
				int nextLevel = ++lv;

				// レベルアップに必要な経験値
				int needExp = wand.needExp(wand.getMaxLevel(), nextLevel, stack);

				toolTip.add(getTipArray(getText("level"), ": ", getLabel("" + level).withStyle(WHITE)).withStyle(GREEN));
				toolTip.add(getTipArray(getText("mf"), ": ", getLabel(String.format("%,d", wand.getMF(stack))).withStyle(WHITE)).withStyle(GREEN));
				toolTip.add(getTipArray(getText("experience"), ": ", getLabel(String.format("%,d", needExp)).withStyle(WHITE)).withStyle(GREEN));
				toolTip.add(getTipArray(getText("magic_damage"), ": ", getLabel(String.format("%.1f", wand.getPower(level))).withStyle(WHITE)).withStyle(GREEN));

				// 属性杖なら属性表示
				if (wand.isNotElement()) {
					toolTip.add(getTipArray(getText("smelement"), ": ", enumString(wand.getWandElement().name()).withStyle(WHITE)).withStyle(GREEN));
				}
			}

			else {
				getShiftTip(toolTip);
			}
		}

		else if (item instanceof IMagicBook book) {

			toolTip.add(empty());
			Component keyOpen = KeyPressEvent.getKeyName(SMKeybind.BACK);
			toolTip.add(getTipArray(keyOpen.copy(), getText("key"), getText("open").withStyle(WHITE)).withStyle(RED));
			toolTip.add(getText("magic_book_page").withStyle(GREEN));

			BookInfo info = new BookInfo(stack);
			if (book.getInvList(info).isEmpty()) { return; }

			// シフトを押したとき
			if (Screen.hasShiftDown()) {

				float attack = book.getAttackPage(info);
				float defence = book.getDefencePage(info);
				float heal = book.getHealPage(info);
				float mf = book.getMFPage(info);
				float recast = book.getRecastPage(info);

				if (attack > 0F) {
					toolTip.add(getTipArray(getText("magic_book_page_attack"), ": ", getLabel(String.format("%.1f%%", attack)).withStyle(WHITE)).withStyle(GOLD));
				}

				if (defence > 0F) {
					toolTip.add(getTipArray(getText("magic_book_page_defence"), ": ", getLabel(String.format("%.1f%%", defence)).withStyle(WHITE)).withStyle(GOLD));
				}

				if (heal > 0F) {
					toolTip.add(getTipArray(getText("magic_book_page_heal"), ": ", getLabel(String.format("%.1f", heal)).withStyle(WHITE)).withStyle(GOLD));
				}

				if (mf > 0F) {
					toolTip.add(getTipArray(getText("magic_book_page_mf"), ": ", getLabel(String.format("%.1f", mf)).withStyle(WHITE)).withStyle(GOLD));
				}

				if (recast > 0F) {
					toolTip.add(getTipArray(getText("magic_book_page_recast"), ": ", getLabel(String.format("%.1f", recast)).withStyle(WHITE)).withStyle(GOLD));
				}
			}

			else {
				getShiftTip(toolTip);
			}
		}

		else if (item instanceof IRobe robe) {

			toolTip.add(getTipArray(getText("tier"), ": ", getLabel("" + robe.getTier()).withStyle(WHITE)).withStyle(GREEN));

			// シフトを押したとき
			if (Screen.hasShiftDown()) {
				toolTip.add(empty());
				Component keyOpen = KeyPressEvent.getKeyName(SMKeybind.OPEN);
				toolTip.add(getTipArray(keyOpen.copy(), getText("key"), getText("open").withStyle(WHITE)).withStyle(RED));
				toolTip.add(getText("smrobe_chest").withStyle(GOLD));
				toolTip.add(getText("smrobe_damecut").withStyle(GOLD));
				toolTip.add(empty());
				toolTip.add(getTipArray(getText("mf"), ": ", getLabel(String.format("%,d", robe.getMF(stack))).withStyle(WHITE) ).withStyle(GREEN));
				toolTip.add(getTipArray(getText("magic_cut"), ": ", getLabel((1F - robe.getMagicDamageCut()) * 100F + "%").withStyle(WHITE) ).withStyle(GREEN));
				toolTip.add(getTipArray(getText("smmob_cut"), ": ", getLabel((1F - robe.getSMMobDamageCut()) * 100F + "%").withStyle(WHITE) ).withStyle(GREEN));
			}

			else {
				getShiftTip(toolTip);
			}
		}

		else if (item instanceof IHarness harness) {

			toolTip.add(getTipArray(getText("tier"), ": ", getLabel("" + harness.getTier()).withStyle(WHITE)).withStyle(GREEN));

			// シフトを押したとき
			if (Screen.hasShiftDown()) {
				toolTip.add(getTipArray(getText("mf"), ": ", getLabel(String.format("%,d", harness.getMF(stack))).withStyle(WHITE)).withStyle(GREEN));
			}

			else {
				getShiftTip(toolTip);
			}
		}

		else if (item instanceof IChoker choker) {

			toolTip.add(getTipArray(getText("tier"), ": ", getLabel("" + choker.getTier()).withStyle(WHITE)).withStyle(GREEN));

			// シフトを押したとき
			if (Screen.hasShiftDown()) {
				toolTip.add(getTipArray(getText("mf"), ": ", getLabel(String.format("%,d", choker.getMF(stack))).withStyle(WHITE)).withStyle(GREEN));
			}

			else {
				getShiftTip(toolTip);
			}
		}

		else if (item instanceof IPorch porch) {
			toolTip.add(getTipArray(getText("tier"), ": ", getLabel("" + porch.getTier()).withStyle(WHITE)).withStyle(GREEN));
		}

		else if (item instanceof IAcce acce) {

			toolTip.add(getText("smacc").withStyle(RED));
			toolTip.add(getTip("====================================").withStyle(BLUE));

			if (acce.isSwitch()) {
				toolTip.add(getTipArray(getText("isactive"), ": ", getLabel( "" + stack.getOrCreateTag().getBoolean(SMAcce.NOT_ACTIVE)).withStyle(WHITE)).withStyle(GREEN));
			}

			boolean isDup = acce.isDuplication();

			toolTip.add(getTipArray(getText("smacce"), ": ", enumString(acce.getAcceType().name()).withStyle(WHITE)).withStyle(GREEN));
			toolTip.add(getTipArray(getText("isduplication"), ": ", getLabel("" + isDup).withStyle(WHITE)).withStyle(GREEN));
			toolTip.add(getTipArray(getText("acce_howget"), ": ", acce.getHowGetTip().withStyle(WHITE)).withStyle(GREEN));
			toolTip.add(getTipArray(getText("tier"), ": ", getLabel("" + acce.getTier()).withStyle(WHITE)).withStyle(GREEN));

			if (isDup) {
				toolTip.add(getTipArray(getText("stack_count"), ": ", getLabel(acce.getStackCount(new AcceInfo(stack)) + "/" + acce.getMaxStackCount()).withStyle(WHITE)).withStyle(GREEN));
			}

			toolTip.add(getTip("====================================").withStyle(BLUE));

			List<MutableComponent> debufList = new ArrayList<>();
			acce.debuffRemovalTip(debufList);

			if (!debufList.isEmpty()) {
				toolTip.add(getTipArray(getText("debufeffect"), ": ", debufList.get(0).withStyle(WHITE)).withStyle(GOLD));
				toolTip.add(empty());
			}

			List<MutableComponent> toolTipList = new ArrayList<>();
			acce.magicToolTip(toolTipList, stack);

			if (toolTipList.size() == 1) {
				toolTip.add(getTipArray(getText("effect"), "： ", toolTipList.get(0).withStyle(WHITE)).withStyle(GREEN));
			}

			else {

				int i = 0;

				for (MutableComponent tip : toolTipList) {
					i++;
					toolTip.add(getTipArray(getText("effect"), i + "： ", tip.withStyle(WHITE)).withStyle(GREEN));
				}
			}

			List<MutableComponent> dropTipList = new ArrayList<>();
			acce.dropMobTip(dropTipList);

			if (!dropTipList.isEmpty()) {

				for (MutableComponent tip : dropTipList) {
					toolTip.add(empty());
					toolTip.add(getTipArray(getText("dropmob"), "： ", tip.withStyle(WHITE)).withStyle(GOLD));
				}
			}
		}

		else if (item instanceof IFood food) {
			if (!food.isQuality()) { return; }

			PotionInfo info = food.getPotionInfo();
			int value = food.getQualityValue(stack);
			boolean isTier1 = value >= 1;
			boolean isTier2 = value >= 2;
			boolean isTier3 = value >= 3;

			MutableComponent quality_level = getText("quality_level");
			String potionEffect = info.getPotion().getDisplayName().getString() + getEnchaText(info.getLevel() + 1).getString() + "(" + info.getTime() + "sec)";

			toolTip.add(getTipArray(quality_level.copy(), ": ", getLabel("" + value).withStyle(WHITE)).withStyle(GREEN));
			toolTip.add(getText("quality_effect").withStyle(GREEN));
			toolTip.add(getTipArray("-", quality_level, "1: ", getText("quality1").withStyle(isTier1 ? WHITE : GRAY)).withStyle(isTier1 ? GREEN : GRAY));
			toolTip.add(getTipArray("-", quality_level, "2: ", getText("quality2", potionEffect).withStyle(isTier2 ? WHITE : GRAY)).withStyle(isTier2 ? GREEN : GRAY));
			toolTip.add(getTipArray("-", quality_level, "3: ", getText("quality3").withStyle(isTier3 ? WHITE : GRAY)).withStyle(isTier3 ? GREEN : GRAY));
		}

		// MFを持ったアイテムなら
		if (SweetMagicAPI.hasMF(stack)) {

			int mf = SweetMagicAPI.getMF(stack);

			if (mf > 0) {

				if (item instanceof IMagicItem) {
					toolTip.add(empty());
				}

				String stackMF = String.format("%,d", mf * stack.getCount());
				toolTip.add(getTipArray(String.format("%,d", mf), getLabel("MF").withStyle(GREEN)).withStyle(WHITE));
				toolTip.add(getTipArray("Stack：", getTip(stackMF).withStyle(WHITE), "MF").withStyle(GREEN));
			}
		}
	}
}
