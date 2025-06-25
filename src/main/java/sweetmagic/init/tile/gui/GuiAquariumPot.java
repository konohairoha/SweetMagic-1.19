package sweetmagic.init.tile.gui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.AquariumPotMenu;
import sweetmagic.init.tile.sm.TileAquariumPot;

public class GuiAquariumPot extends GuiSMBase<AquariumPotMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_aquarium.png");
	private final TileAquariumPot tile;
	private final int data;

	public GuiAquariumPot(AquariumPotMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(176, 172);
		this.tile = menu.tile;
		this.data = this.tile.getData(this.tile.getBlockPos());

		if (this.data == 3) {
			this.addButtonMap(0, new SMButton(TEX, 112, 22, 0, 194, 20, 12));
			this.addButtonMap(1, new SMButton(TEX, 133, 22, 0, 194, 20, 12));
		}

		this.addRenderTexList(new SMRenderTex(TEX, 7, 7, 0, 0, 11, 77, new MFRenderGage(menu.tile, true)));
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		// 座標の取得
		int x = this.getWidth();
		int y = this.getHeight();

		// ゲージの値を設定
		if (this.tile.getStackCount() > 0) {
			int progress = this.tile.getProgress(31);
			this.blit(pose, x + 78, y + 75 - progress, 198, 82 - progress, 16, progress);
		}

		if (this.data == 3) {
			this.blit(pose, x + 112, y + 7, 0, 176, 41, 13);
			this.font.drawShadow(pose, this.getLabel("-1"), x + 116, y + 24, 0xEEEEEE);
			this.font.drawShadow(pose, this.getLabel("-10"), x + 135, y + 24, 0xEEEEEE);

			MutableComponent tip = this.getLabel(this.player.experienceLevel);
			this.font.drawShadow(pose, tip, x + 114 + this.font.width(tip.getString()), y + 10, 0x70FF6D);
		}

		else if (this.data == 10) {
			this.blit(pose, x + 157, y + 7, 218, 7, 11, 77);

			int progress = (int) this.tile.getFluidProgressScaled(75);

			if (progress > 0) {

				int height = 75;
				int width = 10;

				FluidStack fluid = this.tile.getContent();
				IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(fluid.getFluid().getFluidType());
				ResourceLocation src = ext.getStillTexture(fluid);
				TextureAtlasSprite tex = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(src);
				int y1 = y + (height - progress);

				RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
				Matrix4f mat = pose.last().pose();

				int hc = progress / 16;
				int hr = progress - (hc * 16);
				if (hc > 0) {
					for (int h = 0; h < hc; h++) {
						int y2 = y1 + hr + (h * 16);
						this.drawFluid(mat, x + 157, y2 + 8, width, 16, tex, 0);
					}
				}

				if (hr > 0) {
					this.drawFluid(mat, x + 157, y1 + 8, width, hr, tex, 16 - hr);
				}

				RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
				RenderSystem.setShaderTexture(0, this.getTEX());
				this.blit(pose, x + 157, y + 7, 232, 7, 11, 77);
			}
		}

		this.itemRenderer.renderAndDecorateFakeItem(this.tile.getStack(), x + 125, y + 50);
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);

		//描画位置を計算
		int tipX = this.getWidth() + 78;
		int tipY = this.getHeight() + 43;

		if (this.isRender(tipX, tipY, mouseX, mouseY, 16, 30)) {

			int xAxis = mouseX - this.getWidth();
			int yAxis = mouseY - this.getHeight();
			boolean isMax = this.tile.isMaxStackCount();

			String tip = this.tile.getStackCount() + " / " + TileAquariumPot.MAX_STACKCOUNT;
			List<Component> tipList = new ArrayList<>();
			tipList.add(this.getTipArray(this.getText("stack_count"), ": ", this.getLabel(tip, isMax ? GOLD : WHITE)).withStyle(GREEN));

			if (!isMax) {
				tipList.add(this.getTipArray(this.getText("stack_count_item"), ": ").withStyle(GOLD));
				tipList.add(this.getTipArray("-", this.tile.getStack().getHoverName()));
			}

			this.renderComponentTooltip(pose, tipList, xAxis, yAxis);
		}

		tipX = this.getWidth() + 112;
		tipY = this.getHeight() + 7;

		if (this.isRender(tipX, tipY, mouseX, mouseY, 41, 13)) {
			int xAxis = mouseX - this.getWidth();
			int yAxis = mouseY - this.getHeight();
			this.renderTooltip(pose, this.getText("turkey_button").withStyle(GOLD), xAxis, yAxis);
		}

		tipX = this.getWidth() + 115;
		tipY = this.getHeight() + 40;

		if (this.isRender(tipX, tipY, mouseX, mouseY, 36, 36)) {
			int xAxis = mouseX - this.getWidth();
			int yAxis = mouseY - this.getHeight();
			List<Component> tipList = new ArrayList<>();
			tipList.add(this.getTipArray(this.getText("stack_count_item"), ": ").withStyle(GOLD));
			tipList.add(this.getTipArray("-", this.tile.getStack().getHoverName()));
			this.renderComponentTooltip(pose, tipList, xAxis, yAxis);
		}

		tipX = this.getWidth() + 157;
		tipY = this.getHeight() + 7;

		if (this.data == 10 && this.isRender(tipX, tipY, mouseX, mouseY, 11, 76)) {
			float fluid = this.tile.getFluidValue() * 0.001F;
			float maxFluid = this.tile.getMaxFuildValue() * 0.001F;
			String par = " (" + this.tile.getFluidPercent() + ")";
			int xAxis = mouseX - this.getWidth();
			int yAxis = mouseY - this.getHeight();

			String tip = String.format("%,.1f", fluid) + "B / " + String.format("%,.1f", maxFluid) + "B" + par;
			this.renderTooltip(pose, this.getLabel(tip), xAxis, yAxis);
		}
	}

	protected ResourceLocation getTEX() {
		return TEX;
	}
}
