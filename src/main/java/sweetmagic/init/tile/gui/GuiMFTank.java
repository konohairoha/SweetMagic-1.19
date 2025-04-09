package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.gui.util.SMButton.SMButtonTip;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.MFTankMenu;
import sweetmagic.init.tile.sm.TileMFTank;

public class GuiMFTank extends GuiSMBase<MFTankMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_mftank.png");
	private final TileMFTank tile;

	public GuiMFTank(MFTankMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.imageWidth = 176;
		this.imageHeight = 181;
		this.tile = menu.tile;
		this.addRenderTexList(new SMRenderTex(TEX, 7, 7, 0, 200, 67, 13, new SMButtonTip("mf_insert", 70, -2)));
		this.addRenderTexList(new SMRenderTex(TEX, 101, 7, 0, 200, 67, 13, new SMButtonTip("mf_extract", 70, -2)));
		this.addRenderTexList(new SMRenderTex(TEX, 49, 47, 0, 0, 77, 11, new MFRenderGage(this.tile, false)));
	}

	protected void renderBGBase(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBGBase(pose, parTick, mouseX, mouseY);

		int x = this.getWidth();
		int y = this.getHeight();

		switch (this.tile.getInvSize()) {
		case 3:
			for (int i = 0; i < 3; i++)
				this.blit(pose, x + 61 + i * 18, y + 22, 79, 22, 18, 18);

			for (int i = 0; i < 5; i++)
				this.blit(pose, x + 43 + i * 18, y + 73, 79, 22, 18, 18);
			break;
		case 5:
			for (int i = 0; i < 5; i++)
				this.blit(pose, x + 43 + i * 18, y + 22, 79, 22, 18, 18);

			for (int i = 0; i < 7; i++)
				this.blit(pose, x + 25 + i * 18, y + 73, 79, 22, 18, 18);
			break;
		}
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);
		int tipX = this.getWidth();
		int tipY = this.getHeight();
		this.font.drawShadow(pose, this.getTip(String.format("%,d", this.tile.viewMFInsert) + "mf"), tipX + 10, tipY + 9.5F, 0x2BC444);
		this.font.drawShadow(pose, this.getTip(String.format("%,d", this.tile.viewMFExtract) + "mf"), tipX + 104, tipY + 9.5F, 0xFF5349);
	}

	@Override
	protected ResourceLocation getTEX() {
		return TEX;
	}
}
