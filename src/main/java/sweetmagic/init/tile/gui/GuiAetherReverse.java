package sweetmagic.init.tile.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.AetherReverseMenu;
import sweetmagic.init.tile.sm.TileAetherReverse;

public class GuiAetherReverse extends GuiSMBase<AetherReverseMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_aether_reverse.png");
	private static final ResourceLocation MISC = SweetMagicCore.getSRC("textures/gui/gui_misc.png");

	public final TileAetherReverse tile;
	private boolean caraftView = false;
	private final List<SMRenderTex> renderList = new ArrayList<>();

	public GuiAetherReverse(AetherReverseMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.tile = menu.tile;
		this.setGuiWidth(176);
		this.setGuiHeight(230);
		this.renderList.add(new SMRenderTex(TEX, 7, 7, 0, 0, 11, 77, new MFRenderGage(this.tile, 179, 7, 11, 76, 76, true)));
	}

	protected void renderBGBase (PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBGBase(pose, parTick, mouseX, mouseY);
		RenderSystem.setShaderTexture(0, MISC);
		this.blit(pose, this.getWidth() + 29, this.getHeight() + 51, 20, 0, 32, 14);
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		int x = this.getWidth();
		int y = this.getHeight();

		// MFの表示
		if (this.tile.isCraft) {
			int progress = this.tile.getCraftProgressScaled(22);
			this.blit(pose, x + 116, y + 64, 198, 62, 15, progress);
		}

		RenderSystem.setShaderTexture(0, MISC);

		if (this.tile.canCraft) {
			this.blit(pose, x + 29, y + 51, 20, 16 + (this.caraftView ? 16 : 0), 32, 14);
		}

		List<ItemStack> stackList = this.tile.craftList;
		if (stackList.isEmpty()) { return; }

		for (int i = 0; i < stackList.size(); i++) {
			this.renderSlotItem(((AetherReverseMenu) this.menu).craftSlot[i], stackList.get(i), pose);
		}
	}

	public boolean mouseClicked(double guiX, double guiY, int mouseButton) {

		int x = this.getWidth();
		int y = this.getHeight();
		double dX = guiX - (double) (x + 29);
		double dY = guiY - (double) (y + 51);

		// 各エンチャントの当たり判定チェック
		if (dX >= 0D && dX <= 31 && dY >= 0D && dY < 11) {
			this.menu.clickMenuButton(this.player, 0);
			this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, 0);
		}

		return super.mouseClicked(guiX, guiY, mouseButton);
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);

		int tipX = this.getWidth() + 29;
		int tipY = this.getHeight() + 51;
		this.caraftView = false;

		if (this.isRendeer(tipX, tipY, mouseX, mouseY, 31, 11)) {

			this.caraftView = true;
			int xAxis = (mouseX - this.getWidth());
			int yAxis = (mouseY - this.getHeight());

			if (!this.tile.craftList.isEmpty()) {
				List<Component> tipList = Arrays.<Component> asList(this.getText("reverse_start").withStyle(GREEN), this.getTipArray(this.getText("needmf"), String.format("%,d", this.tile.getReverseCost())).withStyle(GOLD));
	            this.renderComponentTooltip(pose, tipList, xAxis, yAxis);
			}

			else {
	            this.renderTooltip(pose, this.getText("no_reverse").withStyle(GOLD), xAxis, yAxis);
			}
		}

		List<ItemStack> stackList = this.tile.craftList;
		if (stackList.isEmpty()) { return; }

		for (int i = 0; i < stackList.size(); i++) {
			this.renderItemLabel(((AetherReverseMenu) this.menu).craftSlot[i], stackList.get(i), pose, mouseX, mouseY, Arrays.<Component> asList(this.getText("reverse_craft").withStyle(GREEN), stackList.get(i).getDisplayName()));
		}
	}

	@Override
	protected ResourceLocation getTEX () {
		return TEX;
	}

	@Override
	protected List<SMRenderTex> getRenderTexList () {
		return this.renderList;
	}
}
