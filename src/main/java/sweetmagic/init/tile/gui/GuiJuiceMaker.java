package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.menu.JuiceMakerMenu;
import sweetmagic.init.tile.sm.TileJuiceMaker;

public class GuiJuiceMaker extends GuiSMBase<JuiceMakerMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_juicemaker.png");
	private final TileJuiceMaker tile;

	public GuiJuiceMaker(JuiceMakerMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiWidth(175);
		this.setGuiHeight(184);
		this.tile = menu.tile;
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		// 座標の取得
		int x = this.getWidth();
		int y = this.getHeight();

		// クラフトの矢印表示
		if (this.tile.craftTime > 0) {
			int progress =this.tile.getCraftProgressScaled(22);
			this.blit(pose, x + 99, y + 36, 211, 15, progress, 15);
		}

		// 水の量表示
		if (!this.tile.isWaterEmpty()) {
			int progress = this.tile.getWaterProgressScaled(75);
			this.blit(pose, x + 29, y + 94 - progress, 191, 76 - progress, 14, progress);
		}
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);

		//描画位置を計算
		int tipX = this.getWidth() + 28;
		int tipY = this.getHeight() + 18;
		if (!this.isRendeer(tipX, tipY, mouseX, mouseY, 15, 76)) { return; }

		//ツールチップでMF量を表示する
		int mf = this.tile.getWaterValue();
		int max = this.tile.getMaxWaterValue();
		int xAxis = mouseX - this.getWidth();
		int yAxis = mouseY - this.getHeight();
		String tip = String.format("%,d", mf) + "mb / " + String.format("%,d", max) + "mb";
        this.renderTooltip(pose, this.getLabel(tip), xAxis, yAxis);
	}

	protected ResourceLocation getTEX () {
		return TEX;
	}
}
