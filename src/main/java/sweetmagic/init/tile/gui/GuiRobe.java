package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iitem.IWand;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.gui.util.SMButton.SMButtonTip;
import sweetmagic.init.tile.menu.SMRoveMenu;

public class GuiRobe extends GuiSMBase<SMRoveMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/generic_54.png");

	public GuiRobe(SMRoveMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiWidth(176);
		this.setGuiHeight(202);

		this.hasPorch = this.hasPorch();
		this.hasWand = this.hasWand();
		int x = 140;

		if (this.hasPorch) {
			this.getButtonMap().put(0, new SMButton(MISC, x, -16, 200, 0, 16, 27, new SMButtonTip("open_porch", -18, 14)));
			x -= 18;
		}

		if (this.hasWand) {
			this.getButtonMap().put(1, new SMButton(MISC, x, -16, 200, 0, 16, 27, new SMButtonTip("open_wand", -18, 14)));
		}

		this.getButtonMap().put(2, new SMButton(MISC, 160, -2, 114, 0, 10, 9, new SMButtonTip("sort", -18, 14)));
	}

	protected void renderBGBase (PoseStack pose, float parTick, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		RenderSystem.setShaderTexture(0, this.getTEX());
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		this.blit(pose, this.leftPos, this.topPos + 10, 0, 0, this.getGuiWidth(), this.getGuiHeight());
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		// 座標の取得
		int x = this.getWidth();
		int y = this.getHeight();
		int pX = x + 140;
		int pY = y - 8;

		if (this.hasPorch) {
			ItemStack robe = this.player.getItemBySlot(EquipmentSlot.LEGS);
			this.itemRenderer.renderAndDecorateFakeItem(robe, pX, pY);
			pX -= 18;
		}

		if (this.hasWand) {

			ItemStack wand = this.player.getMainHandItem();
			if ( !(wand.getItem() instanceof IWand) ) { return; }

			this.itemRenderer.renderAndDecorateFakeItem(wand, pX, pY);
		}
	}

	@Override
	protected ResourceLocation getTEX () {
		return TEX;
	}
}
