package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.gui.util.SMButton.SMButtonTip;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.MagiaDrawerMenu;
import sweetmagic.init.tile.sm.TileMagiaDrawer;

public class GuiMagiaDrawer extends GuiSMBase<MagiaDrawerMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_magia_drawer.png");
	private final TileMagiaDrawer tile;

	public GuiMagiaDrawer(MagiaDrawerMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiWidth(242);
		this.setGuiHeight(237);
		this.tile = menu.tile;

		this.getButtonMap().put(0, new SMButton(TEX, 7, 159, 6, 237, 11, 9));
		this.getButtonMap().put(1, new SMButton(TEX, 19, 159, 6, 246, 15, 9));
		this.getButtonMap().put(2, new SMButton(TEX, 7, 170, 36, 237, 11, 9));
		this.getButtonMap().put(3, new SMButton(TEX, 19, 170, 36, 246, 15, 9));
		this.getButtonMap().put(4, new SMButton(MISC, 222, -4, 114, 0, 10, 9, new SMButtonTip("sort", -18, 14)));
		this.getButtonMap().put(5, new SMButton(MISC, 210, -4, 137, 0, 11, 9, new SMButtonTip("quick_stack", -18, 14)));
		this.getButtonMap().put(6, new SMButton(MISC, 198, -4, 161, 0, 11, 9, new SMButtonTip("restock", -18, 14)));

		this.getRenderTexList().add(new SMRenderTex(TEX, 7, 182, 74, 237, 27, 13, new SMButtonTip("item_range", 80, -4)));
		this.getRenderTexList().add(new SMRenderTex(TEX, 214, 158, 243, 93, 11, 77, new MFRenderGage(this.tile, 243, 171, 11, 17, 76, true)));
	}

	protected void renderBGBase (PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBGBase(pose, parTick, mouseX, mouseY);
		this.blit(pose, this.getWidth() + 214, this.getHeight() + 158, 243, 93, 11, 77);
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);
		this.renderStock(this.tile.getInputList(), pose, 5, 6, -7, 8);
		this.font.drawShadow(pose, this.getTip("" + this.tile.range), this.getWidth() + 10, this.getHeight() + 185, 0x2BC444);
	}

	@Override
	protected ResourceLocation getTEX () {
		return TEX;
	}
}
