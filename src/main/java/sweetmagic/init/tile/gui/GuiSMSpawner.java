package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.menu.SMSpawnerMenu;
import sweetmagic.init.tile.sm.TileSMSpawner;

public class GuiSMSpawner extends GuiSMBase<SMSpawnerMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_smspawner.png");
	private final TileSMSpawner tile;

	public GuiSMSpawner(SMSpawnerMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.tile = menu.tile;
		this.setGuiSize(173, 133);
		this.addButtonMap(0, new SMButton(TEX, 145, 7, 176, 7, 11, 9));
		this.addButtonMap(1, new SMButton(TEX, 157, 7, 176, 16, 11, 9));
		this.addButtonMap(2, new SMButton(TEX, 145, 22, 176, 7, 11, 9));
		this.addButtonMap(3, new SMButton(TEX, 157, 22, 176, 16, 11, 9));
		this.addButtonMap(4, new SMButton(TEX, 145, 37, 176, 7, 11, 9));
		this.addButtonMap(5, new SMButton(TEX, 157, 37, 176, 16, 11, 9));
	}

	protected void renderBGBase(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBGBase(pose, parTick, mouseX, mouseY);
		this.blit(pose, this.getWidth() + 18, this.getHeight() + 25, 175, 35, 49, 16);
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		// 座標の取得
		int tipX = this.getWidth();
		int tipY = this.getHeight();
		this.font.drawShadow(pose, this.getLabel("range: " + this.tile.getRange()), tipX + 8, tipY + 7, 0x2BC444);
		this.font.drawShadow(pose, this.getLabel("level: " + this.tile.getMobLevel()), tipX + 8, tipY + 22, 0x2BC444);
		this.font.drawShadow(pose, this.getLabel("type(" + this.tile.getMobType() + "): " + this.tile.getEntityName()), tipX + 8, tipY + 37, 0x2BC444);
	}

	@Override
	protected ResourceLocation getTEX() {
		return TEX;
	}
}
