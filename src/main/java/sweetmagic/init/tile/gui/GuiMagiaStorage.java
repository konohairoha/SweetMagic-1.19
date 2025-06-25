package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.gui.util.SMButton.SMButtonTip;
import sweetmagic.init.tile.menu.MagiaStorageMenu;

public class GuiMagiaStorage extends GuiSMBase<MagiaStorageMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_magia_drawer.png");

	public GuiMagiaStorage(MagiaStorageMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(242, 237);
		this.addButtonMap(0, new SMButton(MISC, 231, -9, 114, 0, 10, 9, new SMButtonTip("sort", -18, 14)));
		this.addButtonMap(1, new SMButton(MISC, 219, -9, 137, 0, 11, 9, new SMButtonTip("quick_stack", -18, 14)));
		this.addButtonMap(2, new SMButton(MISC, 207, -9, 161, 0, 11, 9, new SMButtonTip("restock", -18, 14)));
	}

	protected void renderBGBase(PoseStack pose, float parTick, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		RenderSystem.setShaderTexture(0, this.getTEX());
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		this.blit(pose, this.leftPos, this.topPos - 8, 0, 0, this.getGuiWidth(), this.getGuiHeight());
	}

	public void render(PoseStack pose, int mouseX, int mouseY, float parTick) {
		this.renderMagiaStack(pose, mouseX, mouseY, parTick, this.menu.slotList, this.menu.slots);
	}

	protected void renderTooltip(PoseStack pose, ItemStack stack, int x, int y) {
		this.renderMagiaStackTooltip(pose, stack, x, y, this.menu.slotList);
	}

	protected ResourceLocation getTEX() {
		return TEX;
	}
}
