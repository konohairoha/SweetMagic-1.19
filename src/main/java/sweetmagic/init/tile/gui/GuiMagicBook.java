package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iitem.IMagicBook;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.gui.util.SMButton.SMButtonTip;
import sweetmagic.init.tile.menu.MagicBookMenu;

public class GuiMagicBook extends GuiSMBase<MagicBookMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_magic_book.png");

	private ItemStack stack;	// 本のアイテムスタック
	private IMagicBook book;	// 本
	private int slot;			// スロット数

	public GuiMagicBook(MagicBookMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiWidth(175);
		this.setGuiHeight(141);

		this.stack = this.player.getMainHandItem();
		this.book = IMagicBook.getBook(this.stack);
		this.slot = this.book.getSlotCount(this.stack);

		this.hasRobe = this.hasRobe();
		this.hasPorch = this.hasPorch();
		int x = 150;

		if (this.hasRobe) {
			this.getButtonMap().put(0, new SMButton(MISC, x, -25, 200, 0, 16, 31, new SMButtonTip("open_robe", -18, 14)));
			x -= 18;
		}

		if (this.hasPorch) {
			this.getButtonMap().put(1, new SMButton(MISC, x, -25, 200, 0, 16, 30, new SMButtonTip("open_porch", -18, 14)));
			x -= 18;
		}

		this.getButtonMap().put(2, new SMButton(MISC, x, -25, 200, 0, 16, 30, new SMButtonTip("open_craftbook", -18, 14)));
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {

		super.renderBg(pose, parTick, mouseX, mouseY);
		this.renderBGBase(pose, parTick, mouseX, mouseY);

		// 座標の取得
		int x = this.getWidth();
		int y = this.getHeight();
		int count = 0;

		for (int k = 0; k < 2; k++) {
			for (int i = 0; i < 5; i++) {

				count++;
				if (count > this.slot) { break; }

				this.blit(pose, x + 36 + 22 * i, y + 11 + 19 * k, 238, 0, 18, 18);
			}

			if (count > this.slot) { break; }
		}

		int pX = x + 150;
		int pY = y - 17;

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
