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
import sweetmagic.init.tile.menu.AetherLanternMenu;
import sweetmagic.init.tile.sm.TileAetherLanp;

public class GuiAetherLantern extends GuiSMBase<AetherLanternMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_aether_lantern.png");
	private final TileAetherLanp tile;

	public GuiAetherLantern(AetherLanternMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.tile = menu.tile;
		this.setGuiSize(173, 132);
		this.addButtonMap(0, new SMButton(TEX, 87, 24, 0, 133, 11, 9));
		this.addButtonMap(1, new SMButton(TEX, 99, 24, 0, 142, 15, 9));
		this.addButtonMap(2, new SMButton(TEX, 87, 36, 30, 133, 11, 9));
		this.addButtonMap(3, new SMButton(TEX, 99, 36, 30, 142, 15, 9));
		this.addRenderTexList(new SMRenderTex(TEX, 18, 28, 0, 153, 48, 13, new SMButtonTip("mf_average", 80, -4)));
		this.addRenderTexList(new SMRenderTex(TEX, 118, 28, 0, 153, 27, 13, new SMButtonTip("mf_range", 0, -4)));
		this.addRenderTexList(new SMRenderTex(TEX, 48, 9, 0, 0, 77, 10, new MFRenderGage(this.tile, false)));
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		// 座標の取得
		int tip_x = this.getWidth();
		int tip_y = this.getHeight();
		this.font.drawShadow(pose, this.getTip("" + this.tile.range), tip_x + 125, tip_y + 30, 0x2BC444);
		this.font.drawShadow(pose, this.getTip(String.format("%,d", this.tile.mfInsert / 2) + "mf"), tip_x + 20, tip_y + 30, 0x2BC444);
	}

	@Override
	protected ResourceLocation getTEX() {
		return TEX;
	}
}
