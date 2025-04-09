package sweetmagic.init.tile.gui;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.registries.ForgeRegistries;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.TagInit;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.gui.util.SMButton.SMButtonTip;
import sweetmagic.init.tile.menu.ObMagiaMenu;
import sweetmagic.init.tile.sm.TileObMagia;

public class GuiObMagia extends GuiSMBase<ObMagiaMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_obmagia.png");
	private final TileObMagia tile;
	private final ObMagiaMenu menu;
	private int tickTime = 0;
	private int pageCounter = 0;
	private int baseCounter = 0;
	private final static List<ItemStack> PAGE = ForgeRegistries.ITEMS.tags().getTag(TagInit.SM_PAGE).stream().map(Item::getDefaultInstance).collect(Collectors.toList());
	private final static List<ItemStack> BASE = ForgeRegistries.ITEMS.tags().getTag(TagInit.SM_BASE).stream().map(Item::getDefaultInstance).collect(Collectors.toList());

	public GuiObMagia(ObMagiaMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(219, 189);
		this.tile = menu.tile;
		this.menu = menu;

		SMButtonTip buttonTip = new SMButtonTip("", 30, 0, this.tile) {

			public boolean isFlagText(TileObMagia tile) {
				return tile.canCraft;
			}

			public String getTip() {
				return this.isFlagText(tile) ? "caraft_start" : "no_recipe";
			}
		};

		SMButton button = new SMButton(MISC, 137, 81, 114, 15, 32, 12, buttonTip) {

			public boolean isButtonRender() {
				return tile.canCraft;
			}
		};
		this.addButtonMap(0, button);
	}

	protected void renderBGBase(PoseStack pose, float parTick, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		RenderSystem.setShaderTexture(0, this.getTEX());
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		this.blit(pose, this.leftPos + 27, this.topPos, 0, 0, this.imageWidth - 27, this.imageHeight);
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		// 座標の取得
		int x = this.getWidth();
		int y = this.getHeight();

		if (!this.tile.canCraft) {
			RenderSystem.setShaderTexture(0, MISC);
			this.blit(pose, x + 137, y + 81, 20, 0, 32, 14);
		}

		RenderSystem.setShaderTexture(0, this.getTEX());
		this.blit(pose, x, y + 8, 228, 17, 27, 182);

		// 魔術書を順番に表示
		if (this.tickTime++ > 120) {
			this.tickTime = 0;

			if (this.pageCounter++ >= PAGE.size() - 1) {
				this.pageCounter = 0;
			}

			if (this.baseCounter++ >= BASE.size() - 1) {
				this.baseCounter = 0;
			}
		}

		// こっちではゲージ量を計算する
		if (this.tile.craftTime > 0) {
			int progress =this.tile.getProgress(33);
			this.blit(pose, x + 132, y + 49, 193, 1, progress, 14);
		}

		this.renderSlotItem(this.menu.baseSlot, BASE.get(this.baseCounter), pose);
		this.renderSlotItem(this.menu.pageSlot, PAGE.get(this.pageCounter), pose);

		if (!this.tile.isCraft && this.tile.canCraft) {
			this.renderSlotItem(this.menu.outSlot, this.tile.viewStack, pose, true);
		}
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);

		this.renderItemLabel(this.menu.baseSlot, BASE.get(this.baseCounter), pose, mouseX, mouseY);
		this.renderItemLabel(this.menu.pageSlot, PAGE.get(this.pageCounter), pose, mouseX, mouseY);

		if (!this.tile.isCraft && this.tile.canCraft) {
			this.renderItemLabel(this.menu.outSlot, this.tile.viewStack, pose, mouseX, mouseY, true);
		}
	}

	// アイテム描画
	public void renderSlotItem(Slot slot, ItemStack stack, PoseStack pose) {
		this.renderSlotItem(slot, stack, pose, false);
	}

	// アイテム描画
	public void renderSlotItem(Slot slot, ItemStack stack, PoseStack pose, boolean flag) {
		if (!slot.getItem().isEmpty()) { return; }

		int x = this.leftPos + slot.x;
		int y = this.topPos + slot.y;
		PoseStack view = RenderSystem.getModelViewStack();
		view.pushPose();
		view.mulPoseMatrix(pose.last().pose());

		Font font = IClientItemExtensions.of(stack).getFont(stack, IClientItemExtensions.FontContext.TOOLTIP);
		font = font == null ? this.font : font;
		this.itemRenderer.renderAndDecorateFakeItem(stack, x, y);

		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(516);
		GuiComponent.fill(pose, x, y, x + 16, y + 16, 822083583);
		RenderSystem.depthFunc(515);
		RenderSystem.disableDepthTest();
		this.itemRenderer.renderGuiItemDecorations(font, stack, x, y, null);
		view.popPose();
	}

	// スロットの説明
	public void renderItemLabel(Slot slot, ItemStack stack, PoseStack pose, int mouseX, int mouseY) {
		this.renderItemLabel(slot, stack, pose, mouseX, mouseY, false);
	}

	// スロットの説明
	public void renderItemLabel(Slot slot, ItemStack stack, PoseStack pose, int mouseX, int mouseY, boolean flag) {
		if (!slot.getItem().isEmpty()) { return; }

		int tipX = this.getWidth() + slot.x;
		int tipY = this.getHeight() + slot.y;

		if (this.isRender(tipX, tipY, mouseX, mouseY, 16, 16)) {

			int xAxis = (mouseX - this.getWidth());
			int yAxis = (mouseY - this.getHeight());
			String name = flag ? "craft_out" : "slot_need";
			List<Component> tipList = Arrays.<Component> asList(this.getText(name).withStyle(GREEN), stack.getDisplayName());
			this.renderComponentTooltip(pose, tipList, xAxis, yAxis);
		}
	}

	protected ResourceLocation getTEX() {
		return TEX;
	}
}
