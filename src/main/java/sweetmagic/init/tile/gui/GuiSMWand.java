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
import sweetmagic.init.tile.menu.SMWandMenu;

public class GuiSMWand extends GuiSMBase<SMWandMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_wand.png");
	private final ItemStack stack;	// 杖のアイテムスタック
	private final IWand wand;		// 杖
	private int slot;				// スロット数

	public GuiSMWand(SMWandMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(196, 211);

		this.stack = this.player.getMainHandItem();
		this.wand = IWand.getWand(this.stack);
		this.slot = this.wand.getSlotCount(this.stack);
		this.hasRobe = this.hasRobe();
		this.hasPorch = this.hasPorch();
		int x = 160;

		if (this.hasRobe) {
			this.addButtonMap(0, new SMButton(MISC, x, -18, 200, 0, 16, 31, new SMButtonTip("open_robe", -18, 14)));
			x -= 18;
		}

		if (this.hasPorch) {
			this.addButtonMap(1, new SMButton(MISC, x, -18, 200, 0, 16, 30, new SMButtonTip("open_porch", -18, 14)));
		}
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);
		int tipX = this.getWidth() + 15;
		int tipY = this.getHeight() + 31;

		if (this.isRender(tipX, tipY, mouseX, mouseY, 10, 76)) {
			int xAxis = (mouseX - this.getWidth());
			int yAxis = (mouseY - this.getHeight());
			int mf = this.wand.getMF(this.stack);
			int max = this.wand.getMaxMF(this.stack);
			String tip = String.format("%,d", mf) + "mf / " + String.format("%,d", max) + "mf";
			this.renderTooltip(pose, Component.translatable(tip), xAxis, yAxis);
		}
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);
		this.renderBGBase(pose, parTick, mouseX, mouseY);
		int x = this.getWidth();
		int y = this.getHeight();
		RenderSystem.setShaderTexture(0, MISC);
		this.blit(pose, x + 15, y + 30, 0, 93, 11, 77);

		//こっちではゲージ量を計算する
		if (this.wand.getMF(this.stack) > 0) {
			int progress = this.wand.getMFProgressScaled(this.stack, 76);
			this.blit(pose, x + 15, y + 106 - progress, 11, 93 + 76 - progress, 11, progress);
		}

		int count = 0;

		for (int k = 0; k < 5; k++) {
			for (int i = 0; i < 6; i++) {

				count++;
				if (count > this.slot) { break; }

				this.blit(pose, x + 38 + 22 * i, y + 13 + 19 * k, 0, 72, 18, 18);
			}

			if (count > this.slot) { break; }
		}

		int pX = x + 160;
		int pY = y - 12;

		if (this.hasRobe) {
			ItemStack robe = this.player.getItemBySlot(EquipmentSlot.CHEST);
			this.itemRenderer.renderAndDecorateFakeItem(robe, pX, pY);
			pX -= 18;
		}

		if (this.hasPorch) {
			ItemStack robe = this.player.getItemBySlot(EquipmentSlot.LEGS);
			this.itemRenderer.renderAndDecorateFakeItem(robe, pX, pY);
		}
	}

	@Override
	protected ResourceLocation getTEX() {
		return TEX;
	}
}
