package sweetmagic.init.tile.gui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iitem.IMagicItem;
import sweetmagic.api.iitem.info.MagicInfo;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.gui.util.SMButton.SMButtonTip;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.MagiaTableMenu;
import sweetmagic.init.tile.sm.TileMagiaTable;

public class GuiMagiaTable extends GuiSMBase<MagiaTableMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_magia_table.png");
	private final TileMagiaTable tile;
	private boolean enchaView[] = new boolean[4];

	public GuiMagiaTable(MagiaTableMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(176, 191);
		this.tile = menu.tile;
		this.addRenderTexList(new SMRenderTex(TEX, 7, 6, 0, 0, 11, 77, new MFRenderGage(menu.tile, true)));

		SMButtonTip buttonTip = new SMButtonTip("", 30, 0, this.tile) {

			public boolean isFlagText(TileMagiaTable tile) {
				return tile.canCraft;
			}

			public String getTip() {
				return this.isFlagText(tile) ? "caraft_start" : "no_recipe";
			}
		};

		SMButton button = new SMButton(MISC, 22, 80, 114, 15, 32, 12, buttonTip) {
			public boolean isButtonRender() {
				return tile.canCraft;
			}
		};

		this.addButtonMap(0, button);
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		// 座標の取得
		int x = this.getWidth();
		int y = this.getHeight();
		RenderSystem.setShaderTexture(0, MISC);

		if (!this.tile.canCraft) {
			this.blit(pose, x + 22, y + 80, 20, 0, 32, 14);
		}

		RenderSystem.setShaderTexture(0, this.getTEX());

		// こっちではゲージ量を計算する
		if (this.tile.craftTime > 0) {
			int progress =this.tile.getCraftProgress(19);
			this.blit(pose, x + 32, y + 27, 193, 27, 39, progress);
		}

		ItemStack stack = this.tile.getInputItem();
		if(!stack.isEmpty() && stack.getItem() instanceof IMagicItem magic && !magic.isUniqueMagic()) {
			List<ItemStack> stackList = this.tile.getRequestList(new MagicInfo(stack));

			for (int i = 0; i < stackList.size(); i++) {
				this.blit(pose, x + 98, y + 31 + i * 18, 97, 191 + (this.enchaView[i] ? 18 : 0), 53, 18);
			}

			for (int i = 0; i < stackList.size(); i++) {
				pose.pushPose();
				ItemStack s = stackList.get(i);
				MutableComponent tip2 = this.getLabel("×" + s.getCount());
				this.itemRenderer.renderAndDecorateFakeItem(s, x + 100, y + 32 + i * 18);
				pose.translate(0D, 0D, 200D);
				this.font.drawShadow(pose, tip2, x + 118, y + 39 + i * 18, 0xEEEEEE);
				pose.popPose();
			}
		}
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);

		int tipX = this.getWidth() + 98;
		int tipY = this.getHeight() + 31;

		ItemStack stack = this.tile.getInputItem();
		if(!stack.isEmpty() && stack.getItem() instanceof IMagicItem magic && !magic.isUniqueMagic()) {

			int xAxis = mouseX - this.getWidth();
			int yAxis = mouseY - this.getHeight();
			List<ItemStack> stackList = this.tile.getRequestList(new MagicInfo(stack));

			for (int i = 0; i < stackList.size(); i++) {

				this.enchaView[i] = false;
				if (this.isRender(tipX, tipY, mouseX, mouseY, 53, 17)) {
					List<Component> tipList = new ArrayList<>();
					tipList.add(this.getText("request_item").withStyle(GREEN));
					tipList.add(stackList.get(i).getHoverName());
					this.renderComponentTooltip(pose, tipList, xAxis, yAxis);
					this.enchaView[i] = true;
				}

				tipY += 18;
			}
		}
	}

	protected ResourceLocation getTEX() {
		return TEX;
	}
}
