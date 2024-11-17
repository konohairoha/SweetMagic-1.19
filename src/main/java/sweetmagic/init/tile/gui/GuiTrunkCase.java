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
import sweetmagic.init.tile.menu.TrunkCaseMenu;

public class GuiTrunkCase extends GuiSMBase<TrunkCaseMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_dchest.png");
	private final TrunkCaseMenu menu;

	public GuiTrunkCase(TrunkCaseMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiWidth(255);
		this.setGuiHeight(230);
		this.menu = menu;
		this.getButtonMap().put(0, new SMButton(MISC, 231, -11, 114, 0, 10, 9, new SMButtonTip("sort", -18, 14)));
		this.getButtonMap().put(1, new SMButton(MISC, 219, -11, 137, 0, 11, 9, new SMButtonTip("quick_stack", -18, 14)));
		this.getButtonMap().put(2, new SMButton(MISC, 207, -11, 161, 0, 11, 9, new SMButtonTip("restock", -18, 14)));
	}

	protected void renderBGBase (PoseStack pose, float parTick, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		RenderSystem.setShaderTexture(0, this.getTEX());
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		this.blit(pose, this.leftPos, this.topPos, 0, 15, this.getGuiWidth(), this.getGuiHeight());
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);
		this.renderStock(this.menu.inventory.getStackAllList(), pose, 1, 2, 0, 0);
	}

	protected ResourceLocation getTEX () {
		return TEX;
	}
}
