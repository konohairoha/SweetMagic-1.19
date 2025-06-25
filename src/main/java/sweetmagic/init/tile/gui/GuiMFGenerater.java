package sweetmagic.init.tile.gui;

import java.util.Arrays;
import java.util.List;

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
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.TagInit;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.MFGeneraterMenu;
import sweetmagic.init.tile.sm.TileMFGenerater;

public class GuiMFGenerater extends GuiSMBase<MFGeneraterMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_mf_generator.png");
	private final TileMFGenerater tile;
	private int counter = 0;
	private int counter_lava = 0;
	private int tickTime = 0;
	private final static List<ItemStack> LAVA_LIST = GuiSMBase.getTagStack(TagInit.LAVA);
	private final static List<ItemStack> STONE_LIST = GuiSMBase.getTagStack(Tags.Items.COBBLESTONE);

	public GuiMFGenerater(MFGeneraterMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(176, 172);
		this.tile = menu.tile;
		this.addRenderTexList(new SMRenderTex(TEX, 7, 7, 0, 0, 11, 77, new MFRenderGage(menu.tile, true)));
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		int x = this.getWidth() + 159;
		int y = this.getHeight() + 11;
		int progress = (int) this.tile.getFluidProgressScaled(75);

		if (progress > 0) {

			int height = 72;
			int width = 9;

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
					this.drawFluid(mat, x, y2, width, 16, tex, 0);
				}
			}

			if (hr > 0) {
				this.drawFluid(mat, x, y1, width, hr, tex, 16 - hr);
			}

			RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
			RenderSystem.setShaderTexture(0, this.getTEX());
		}

		this.blit(pose, x - 1, y - 2, 194, 9, 11, 74);

		// 魔術書を順番に表示
		if (this.tickTime++ > 120) {
			this.tickTime = 0;

			if (this.counter++ >= STONE_LIST.size() - 1) {
				this.counter = 0;
			}

			if (this.counter_lava++ >= LAVA_LIST.size() - 1) {
				this.counter_lava = 0;
			}
		}

		this.renderSlotItem(this.menu.bucketSlot, LAVA_LIST.get(this.counter_lava), pose);
		this.renderSlotItem(this.menu.stoneSlotList.get(0), STONE_LIST.get(this.counter), pose);
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);

		int tipX = this.getWidth() + 158;
		int tipY = this.getHeight() + 7;

		if (this.isRender(tipX, tipY, mouseX, mouseY, 11, 76)) {
			float fluid = this.tile.getFluidValue() * 0.001F;
			float maxFluid = this.tile.getMaxFuildValue() * 0.001F;
			String par = " (" + this.tile.getFluidPercent() + ")";
			int xAxis = mouseX - this.getWidth();
			int yAxis = mouseY - this.getHeight();

			String tip = String.format("%,.1f", fluid) + "B / " + String.format("%,.1f", maxFluid) + "B" + par;
			this.renderTooltip(pose, this.getLabel(tip), xAxis, yAxis);
		}

		// 要求アイテムの描画
		this.renderItemLabel(this.menu.bucketSlot, LAVA_LIST.get(this.counter_lava), pose, mouseX, mouseY);
		this.renderItemLabel(this.menu.stoneSlotList.get(0), STONE_LIST.get(this.counter), pose, mouseX, mouseY);
	}

	public void setTip(List<Component> tipList, ItemStack stack) {

		if (LAVA_LIST.contains(stack)) {
			tipList.addAll(Arrays.<Component> asList(this.getText("fill_bucket").withStyle(GREEN), stack.getDisplayName()));
		}

		else {
			tipList.addAll(Arrays.<Component> asList(this.getText("slot_need").withStyle(GREEN), stack.getDisplayName()));
		}
	}

	protected ResourceLocation getTEX() {
		return TEX;
	}
}
