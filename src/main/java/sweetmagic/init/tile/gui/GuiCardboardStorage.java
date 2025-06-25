package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.menu.CardboardStorageMenu;

public class GuiCardboardStorage extends GuiSMBase<CardboardStorageMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_moden_rack.png");

	public GuiCardboardStorage(CardboardStorageMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(173, 132);
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);
		this.blit(pose, this.getWidth() + 78, this.getHeight() + 13, 173, 0, 18, 18);
	}

	public void render(PoseStack pose, int mouseX, int mouseY, float parTick) {
		this.renderMagiaStack(pose, mouseX, mouseY, parTick, this.menu.slotList, this.menu.slots);
	}

	@Override
	protected void renderTooltip(PoseStack pose, ItemStack stack, int x, int y) {
		this.renderMagiaStackTooltip(pose, stack, x, y, this.menu.slotList);
	}

	protected ResourceLocation getTEX() {
		return TEX;
	}
}
