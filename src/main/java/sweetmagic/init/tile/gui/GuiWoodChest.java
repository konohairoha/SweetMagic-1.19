package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.gui.util.SMButton.SMButtonTip;
import sweetmagic.init.tile.menu.WoodChestMenu;
import sweetmagic.init.tile.sm.TileWoodChest;

public class GuiWoodChest extends GuiSMBase<WoodChestMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_dchest.png");
	private static final ResourceLocation DRAWER = SweetMagicCore.getSRC("textures/gui/gui_magia_drawer.png");
	private static final ResourceLocation FREEZER_TEX = SweetMagicCore.getSRC("textures/gui/gui_freezer_chest.png");
	private final TileWoodChest tile;
	private final int data;

	public GuiWoodChest(WoodChestMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.tile = menu.tile;
		this.data = menu.tile.getData();
		this.setGuiWidth(256);
		this.setGuiHeight(231);
		this.getButtonMap().put(0, new SMButton(MISC, 231, -11, 114, 0, 10, 9, new SMButtonTip("sort", -18, 14)));
		this.getButtonMap().put(1, new SMButton(MISC, 219, -11, 137, 0, 11, 9, new SMButtonTip("quick_stack", -18, 14)));
		this.getButtonMap().put(2, new SMButton(MISC, 207, -11, 161, 0, 11, 9, new SMButtonTip("restock", -18, 14)));

		if (this.data == 4) {
			this.getButtonMap().put(3, new SMButton(DRAWER, 219, 167, 112, 237, 15, 15, new SMButtonTip("trash_can_del", 0, 0)));
		}
	}

	protected void renderBGBase (PoseStack pose, float parTick, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		RenderSystem.setShaderTexture(0, this.getTEX());
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		this.blit(pose, this.leftPos, this.topPos, 0, this.data == 3 ? 0 : 15, this.getGuiWidth(), this.getGuiHeight());
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);
		this.renderStock(this.tile.getInvList(), pose, 1, 2, 0, 0);
	}

	protected ResourceLocation getTEX () {
		return this.data == 3 ? FREEZER_TEX : TEX;
	}
}
