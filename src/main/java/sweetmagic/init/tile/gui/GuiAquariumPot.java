package sweetmagic.init.tile.gui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.AquariumPotMenu;
import sweetmagic.init.tile.sm.TileAquariumPot;

public class GuiAquariumPot extends GuiSMBase<AquariumPotMenu> implements ISMTip {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_aquarium.png");
	private final TileAquariumPot tile;
	private final int data;

	public GuiAquariumPot(AquariumPotMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiWidth(176);
		this.setGuiHeight(172);
		this.tile = menu.tile;
		this.data = this.tile.getData(this.tile.getBlockPos());

		if (this.data == 3) {
			this.getButtonMap().put(0, new SMButton(TEX, 112, 22, 0, 194, 20, 12));
			this.getButtonMap().put(1, new SMButton(TEX, 133, 22, 0, 194, 20, 12));
		}

		this.getRenderTexList().add(new SMRenderTex(TEX, 7, 7, 0, 0, 11, 77, new MFRenderGage(menu.tile, 179, 7, 11, 76, 76, true)));
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		// 座標の取得
		int x = this.getWidth();
		int y = this.getHeight();

		// ゲージの値を設定
		if (this.tile.getStackCount() > 0) {
			int progress = this.tile.getProgressScaled(31);
			this.blit(pose, x + 78, y + 75 - progress, 198, 82 - progress, 16, progress);
		}

		if (this.data == 3) {
			this.blit(pose, x + 112, y + 7, 0, 176, 41, 13);
			this.font.drawShadow(pose, this.getLabel("-1"), x + 116, y + 24, 0xEEEEEE);
			this.font.drawShadow(pose, this.getLabel("-10"), x + 135, y + 24, 0xEEEEEE);

			MutableComponent tip = this.getLabel("" + this.player.experienceLevel);
			this.font.drawShadow(pose, tip, x + 114 + this.font.width(tip.getString()), y + 10, 0x70FF6D);
		}

//		PoseStack view = RenderSystem.getModelViewStack();
//		view.pushPose();
//		view.scale(2F, 2F, 2F);
		this.itemRenderer.renderAndDecorateFakeItem(this.tile.getStack(), x + 125, y + 50);
//		view.scale(0.5F, 0.5F, 0.5F);
//		view.popPose();

	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);

		//描画位置を計算
		int tipX = this.getWidth() + 78;
		int tipY = this.getHeight() + 43;

		if (this.isRendeer(tipX, tipY, mouseX, mouseY, 16, 30)) {

			int xAxis = mouseX - this.getWidth();
			int yAxis = mouseY - this.getHeight();
			boolean isMax = this.tile.isMaxStackCount();

			String tip = this.tile.getStackCount() + " / " + TileAquariumPot.MAX_STACKCOUNT;
			List<Component> tipList = new ArrayList<>();
			tipList.add(this.getTipArray(this.getText("stack_count"), ": ", this.getTip(tip).withStyle(isMax ? GOLD : WHITE)).withStyle(GREEN));

			if (!isMax) {
				tipList.add(this.getTipArray(this.getText("stack_count_item"), ": ").withStyle(GOLD));
				tipList.add(this.getTipArray("-", this.tile.getStack().getHoverName()));
			}

            this.renderComponentTooltip(pose, tipList, xAxis, yAxis);
		}

		tipX = this.getWidth() + 112;
		tipY = this.getHeight() + 7;

		if (this.isRendeer(tipX, tipY, mouseX, mouseY, 41, 13)) {
			int xAxis = mouseX - this.getWidth();
			int yAxis = mouseY - this.getHeight();
            this.renderTooltip(pose, this.getText("turkey_button").withStyle(GOLD), xAxis, yAxis);
		}

		tipX = this.getWidth() + 115;
		tipY = this.getHeight() + 40;

		if (this.isRendeer(tipX, tipY, mouseX, mouseY, 36, 36)) {
			int xAxis = mouseX - this.getWidth();
			int yAxis = mouseY - this.getHeight();
			List<Component> tipList = new ArrayList<>();
			tipList.add(this.getTipArray(this.getText("stack_count_item"), ": ").withStyle(GOLD));
			tipList.add(this.getTipArray("-", this.tile.getStack().getHoverName()));
            this.renderComponentTooltip(pose, tipList, xAxis, yAxis);
		}
	}

	protected ResourceLocation getTEX () {
		return TEX;
	}
}
