package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.MFChangerMenu;
import sweetmagic.init.tile.sm.TileSMMagic;

public class GuiMFChanger extends GuiSMBase<MFChangerMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_mfchanger.png");
	private final TileSMMagic tile;

	public GuiMFChanger(MFChangerMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(176, 152);
		this.tile = menu.tile;
		this.addRenderTexList(new SMRenderTex(TEX, 35, 53, 0, 0, 106, 10, new MFRenderGage(this.tile, false)));
	}

	@Override
	protected void renderBGBase(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBGBase(pose, parTick, mouseX, mouseY);

		switch (this.tile.getInvSize()) {
		case 3:
			this.blit(pose, this.getWidth() + 61, this.getHeight() + 26, 18, 178, 54, 18);
			break;
		case 5:
			this.blit(pose, this.getWidth() + 43, this.getHeight() + 26, 0, 178, 90, 18);
			break;
		case 10:
			this.blit(pose, this.getWidth() + 43, this.getHeight() + 8, 0, 178, 90, 18);
			this.blit(pose, this.getWidth() + 43, this.getHeight() + 26, 0, 178, 90, 18);
			break;
		}
	}

	@Override
	protected ResourceLocation getTEX() {
		return TEX;
	}
}
