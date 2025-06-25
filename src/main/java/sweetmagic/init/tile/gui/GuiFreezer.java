package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.menu.FreezerMenu;
import sweetmagic.init.tile.sm.TileFreezer;

public class GuiFreezer extends GuiSMBase<FreezerMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_freezer.png");
	private final TileFreezer tile;

	public GuiFreezer(FreezerMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(176, 185);
		this.tile = menu.tile;
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		// 座標の取得
		int x = this.getWidth();
		int y = this.getHeight();

		// クラフトゲージの値を設定
		if (this.tile.craftTime > 0) {
			int progress =this.tile.getCraftProgress(22);
			this.blit(pose, x + 99, y + 36, 211, 14, progress, 15);
		}

		// 水のゲージの値を設定
		if (this.tile.getFluidValue() > 0) {
			int progress = this.tile.getWaterProgress(75);
			this.blit(pose, x + 29, y + 94 - progress, 191, 76 - progress, 9, progress);
		}
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);

		int tipX = this.getWidth() + 28;
		int tipY = this.getHeight() + 18;
		if (!this.isRender(tipX, tipY, mouseX, mouseY, 10, 76)) { return; }

		int mf = this.tile.getFluidValue();
		int max = this.tile.getMaxFuildValue();
		int xAxis = mouseX - this.getWidth();
		int yAxis = mouseY - this.getHeight();
		String tip = this.format(mf) + "mb / " + this.format(max) + "mb";
		this.renderTooltip(pose, this.getLabel(tip), xAxis, yAxis);
	}

	protected ResourceLocation getTEX() {
		return TEX;
	}
}
