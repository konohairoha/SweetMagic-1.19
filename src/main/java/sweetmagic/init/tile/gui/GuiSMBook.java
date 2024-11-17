package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.gui.util.SMButton.SMButtonTip;
import sweetmagic.init.tile.menu.SMBookMenu;

public class GuiSMBook extends GuiSMBase<SMBookMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_smbook.png");

	private ItemStack stack;	// 本のアイテムスタック

	public GuiSMBook(SMBookMenu menu, Inventory inv, Component com) {
		super(menu, inv, com);
		this.setGuiWidth(194);
		this.setGuiHeight(222);

		this.stack = this.player.getMainHandItem();

		this.hasRobe = this.hasRobe();
		this.hasPorch = this.hasPorch();
		int x = 150;
		int y = -18;

		if (this.hasRobe) {
			this.getButtonMap().put(0, new SMButton(MISC, x, y, 200, 0, 16, 31, new SMButtonTip("open_robe", -18, 14)));
			x -= 18;
		}

		if (this.hasPorch) {
			this.getButtonMap().put(1, new SMButton(MISC, x, y, 200, 0, 16, 30, new SMButtonTip("open_porch", -18, 14)));
			x -= 18;
		}

		this.getButtonMap().put(2, new SMButton(MISC, x, y, 200, 0, 16, 30, new SMButtonTip("open_book", -18, 14)));
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {

		super.renderBg(pose, parTick, mouseX, mouseY);
		this.renderBGBase(pose, parTick, mouseX, mouseY);

		// 座標の取得
		int x = this.getWidth();
		int y = this.getHeight();
		int pX = x + 150;
		int pY = y - 14;

		if (this.hasRobe) {
			ItemStack robe = this.player.getItemBySlot(EquipmentSlot.CHEST);
			this.itemRenderer.renderAndDecorateFakeItem(robe, pX, pY);
			pX -= 18;
		}

		if (this.hasPorch) {
			ItemStack porch = this.player.getItemBySlot(EquipmentSlot.LEGS);
			this.itemRenderer.renderAndDecorateFakeItem(porch, pX, pY);
			pX -= 18;
		}

		this.itemRenderer.renderAndDecorateFakeItem(this.stack, pX, pY);
	}

	@Override
	protected ResourceLocation getTEX () {
		return TEX;
	}
}
