package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iitem.IMFTool;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.MFTableMenu;
import sweetmagic.init.tile.sm.TileMFTable;

public class GuiMFTable extends GuiSMBase<MFTableMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_magictable.png");
	private final TileMFTable tile;

	public GuiMFTable(MFTableMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.tile = menu.tile;
		this.setGuiWidth(234);
		this.setGuiHeight(243);
		this.getRenderTexList().add(new SMRenderTex(TEX, 172, 49, 0, 0, 3, 50, new MFRenderGage(this.tile, 240, 0, 3, 50, 50, true)));
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		// 座標の取得
		int x = this.getWidth();
		int y = this.getHeight();

		if (!this.tile.getInputItem(0).isEmpty()) {
			ItemStack stack = this.tile.getInputItem(0);
			IMFTool wand = (IMFTool) stack.getItem();

			int progress = wand.getMFProgressScaled(stack, 14);
			this.blit(pose, x + 39, y + 66, 240, 50, progress, 3);
		}

		int size = this.tile.getInvSize();
		if (size < 4) { return; }

		this.blit(pose, x + 37, y + 84, 236, 66, 18, 22);
		this.blit(pose, x + 14, y + 22, 236, 66, 18, 22);
		this.blit(pose, x + 61, y + 22, 236, 66, 18, 22);

		if (size >= 6) {
			this.blit(pose, x + 1, y + 59, 236, 66, 18, 22);
			this.blit(pose, x + 73, y + 59, 236, 66, 18, 22);
		}

		for (int i = 1; i < size; i++) {

			ItemStack stack = this.tile.getInputItem(i);
			if (stack.isEmpty()) { continue; }

			IMFTool wand = (IMFTool) stack.getItem();
			int progress = wand.getMFProgressScaled(stack, 14);

			switch (i) {
			case 1:
				this.blit(pose, x + 39, y + 102, 240, 50, progress, 3);
				break;
			case 2:
				this.blit(pose, x + 16, y + 40, 240, 50, progress, 3);
				break;
			case 3:
				this.blit(pose, x + 63, y + 40, 240, 50, progress, 3);
				break;
			case 4:
				this.blit(pose, x + 3, y + 77, 240, 50, progress, 3);
				break;
			case 5:
				this.blit(pose, x + 75, y + 77, 240, 50, progress, 3);
				break;
			}
		}
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);

		int x = this.getWidth();
		int y = this.getHeight();
		int xAxis = (mouseX - x);
		int yAxis = (mouseY - y);

		if (!this.tile.getInputItem(0).isEmpty()) {

			//描画位置を計算
			int tipX = x + 38;
			int tipY = y + 67;

			if (this.isRendeer(tipX, tipY, mouseX, mouseY, 15, 2)) {

				ItemStack stack = this.tile.getInputItem(0);
				IMFTool wand = (IMFTool) stack.getItem();
				int wandMF = wand.getMF(stack);
				int wabdMax = wand.getMaxMF(stack);

				String tip = String.format("%,d", wandMF) + "mf / " + String.format("%,d", wabdMax) + "mf";
	            this.renderTooltip(pose, this.getTip(tip), xAxis, yAxis);
			}
		}

		int size = this.tile.getInvSize();
		if (size < 4) { return; }

		for (int i = 1; i < size; i++) {

			ItemStack stack = this.tile.getInputItem(i);
			if (stack.isEmpty()) { continue; }

			//描画位置を計算
			int tipX = x;
			int tipY = y;

			switch (i) {
			case 1:
				tipX += 39;
				tipY += 104;
				break;
			case 2:
				tipX += 16;
				tipY += 42;
				break;
			case 3:
				tipX += 63;
				tipY += 42;
				break;
			case 4:
				tipX += 3;
				tipY += 79;
				break;
			case 5:
				tipX += 75;
				tipY += 79;
				break;
			}

			if (this.isRendeer(tipX, tipY, mouseX, mouseY, 15, 2)) {

				IMFTool wand = (IMFTool) stack.getItem();
				int wandMF = wand.getMF(stack);
				int wabdMax = wand.getMaxMF(stack);

				String tip = String.format("%,d", wandMF) + "mf / " + String.format("%,d", wabdMax) + "mf";
	            this.renderTooltip(pose, this.getTip(tip), xAxis, yAxis);
			}
		}
	}

	@Override
	protected ResourceLocation getTEX () {
		return TEX;
	}
}
