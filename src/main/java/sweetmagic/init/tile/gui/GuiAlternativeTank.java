package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.menu.AlternativeTankMenu;
import sweetmagic.init.tile.sm.TileAlternativeTank;

public class GuiAlternativeTank extends GuiSMBase<AlternativeTankMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_alternative_tank.png");
	private static final ResourceLocation SRC = SweetMagicCore.getSRC("block/water_still");
	private final TileAlternativeTank tile;

	public GuiAlternativeTank(AlternativeTankMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.tile = menu.tile;
		this.setGuiSize(176, 178);
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		int x = this.getWidth() + 13;
		int y = this.getHeight() + 14;
		int progress = (int) this.tile.getFluidProgressScaled(75);

		if (progress > 0) {

			int height = 72;
			int width = 9;

			FluidStack fluid = this.tile.getContent();
			IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(fluid.getFluid().getFluidType());
			ResourceLocation src = ext.getStillTexture(fluid);
			TextureAtlasSprite tex = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluid.getFluid().isSame(Fluids.WATER) ? SRC : src);
			int y1 = y + (height - progress);

			RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
			Matrix4f mat = pose.last().pose();

			int hc = progress / 16;
			int hr = progress - (hc * 16);
			if (hc > 0) {
				for (int h = 0; h < hc; h++) {
					int y2 = y1 + hr + (h * 16);
					this.drawFluid(mat, x, y2, width, 16, tex, 0);
				}
			}

			if (hr > 0) {
				this.drawFluid(mat, x, y1, width, hr, tex, 16 - hr);
			}

			RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
			RenderSystem.setShaderTexture(0, this.getTEX());
			this.blit(pose, x - 1, y - 4, 179, 0, 11, 77);
		}
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);

		int tipX = this.getWidth() + 13;
		int tipY = this.getHeight() + 10;

		if (this.isRender(tipX, tipY, mouseX, mouseY, 11, 76)) {
			float fluid = this.tile.getFluidValue() * 0.001F;
			float maxFluid = this.tile.getMaxFuildValue() * 0.001F;
			String par = " (" + this.tile.getFluidPercent() + ")";
			int xAxis = mouseX - this.getWidth();
			int yAxis = mouseY - this.getHeight();

			String tip = String.format("%,.1f", fluid) + "B / " + String.format("%,.1f", maxFluid) + "B" + par;
			this.renderTooltip(pose, this.getTip(tip), xAxis, yAxis);
		}
	}

	protected ResourceLocation getTEX() {
		return TEX;
	}
}
