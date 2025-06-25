package sweetmagic.init.tile.gui;

import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.TagInit;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.MFWoodCutterMenu;
import sweetmagic.init.tile.sm.TileMFWoodCutter;

public class GuiMFWoodCutter extends GuiSMBase<MFWoodCutterMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_mf_miner_advanced.png");
	private final TileMFWoodCutter tile;
	private int counter = 0;
	private int tickTime = 0;
	private final static List<ItemStack> SAPLING_LIST = GuiSMBase.getTagStack(TagInit.SMSAPLING);

	public GuiMFWoodCutter(MFWoodCutterMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(176, 228);
		this.tile = menu.tile;
		this.addRenderTexList(new SMRenderTex(TEX, 7, 7, 0, 0, 11, 77, new MFRenderGage(menu.tile, true)));
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);
		int x = this.getWidth();
		int y = this.getHeight();

		if (this.menu.tile.craftTime > 0) {
			int progress = this.menu.tile.getCraftProgress(22);
			this.blit(pose, x + 81, y + 62, 194, 62, 15, progress);
		}

		// 魔術書を順番に表示
		if (this.tickTime++ > 120) {
			this.tickTime = 0;

			if (this.counter++ >= SAPLING_LIST.size() - 1) {
				this.counter = 0;
			}
		}

		this.blit(pose, x + 79, y + 40, 180, 0, 18, 18);

		this.renderSlotItem(this.menu.stoneSlotList.get(0), SAPLING_LIST.get(this.counter), pose);
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);

		// 要求アイテムの描画
		this.renderItemLabel(this.menu.stoneSlotList.get(0), SAPLING_LIST.get(this.counter), pose, mouseX, mouseY);

		int tipX = this.getWidth() + 80;
		int tipY = this.getHeight() + 61;

		if (this.isRender(tipX, tipY, mouseX, mouseY, 15, 22)) {
			int xAxis = mouseX - this.getWidth();
			int yAxis = mouseY - this.getHeight();
			this.renderTooltip(pose, this.getText("progress", this.tile.getCraftProgress(100)).withStyle(GOLD), xAxis, yAxis);
		}
	}

	public void setTip(List<Component> tipList, ItemStack stack) {
		tipList.addAll(Arrays.<Component> asList(this.getText("slot_need").withStyle(GREEN), stack.getDisplayName()));
	}

	protected ResourceLocation getTEX() {
		return TEX;
	}
}
