package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iitem.IWand;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.gui.util.SMButton.SMButtonTip;
import sweetmagic.init.tile.menu.SMPorchMenu;

public class GuiPorch extends GuiSMBase<SMPorchMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_magic_book.png");
	private final int slotSize;

	public GuiPorch(SMPorchMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(176, 142);
		this.hasRobe = this.hasRobe();
		this.hasWand = this.hasWand();
		int x = 130;

		if (this.hasRobe) {
			this.addButtonMap(0, new SMButton(MISC, x, -27, 200, 0, 16, 27, new SMButtonTip("open_robe", -18, 14)));
			x -= 18;
		}

		if (this.hasWand) {
			this.addButtonMap(1, new SMButton(MISC, x, -27, 200, 0, 16, 27, new SMButtonTip("open_wand", -18, 14)));
		}

		this.slotSize = menu.slotSize;
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);
		RenderSystem.setShaderTexture(0, this.getTEX());

		// 座標の取得
		int x = this.getWidth();
		int y = this.getHeight();
		int count = 0;

		for (int tY = 0; tY < 2; tY++) {
			for (int tX = 0; tX < 8; tX++) {
				if (++count > this.slotSize) { break; }
				this.blit(pose, x + 14 + tX * 18, y + 12 + tY * 18, 238, 22, 18, 18);
			}
		}

		int pX = x + 130;
		int pY = y - 21;

		if (this.hasRobe) {
			ItemStack robe = this.player.getItemBySlot(EquipmentSlot.CHEST);
			this.itemRenderer.renderAndDecorateFakeItem(robe, pX, pY);
			pX -= 18;
		}

		if (this.hasWand) {
			ItemStack wand = this.player.getMainHandItem();
			if ( !(wand.getItem() instanceof IWand) ) { return; }

			this.itemRenderer.renderAndDecorateFakeItem(wand, pX, pY);
		}
	}

	@Override
	protected ResourceLocation getTEX() {
		return TEX;
	}
}
