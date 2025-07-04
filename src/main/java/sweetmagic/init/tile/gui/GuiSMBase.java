package sweetmagic.init.tile.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.registries.ForgeRegistries;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iitem.IPorch;
import sweetmagic.api.iitem.IRobe;
import sweetmagic.api.iitem.IWand;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.slot.MagiaSlot;
import sweetmagic.init.tile.sm.TileMFChanger;
import sweetmagic.init.tile.sm.TileSMMagic;

public abstract class GuiSMBase<M extends AbstractContainerMenu> extends AbstractContainerScreen<M> implements ISMTip {

	protected final Player player;
	protected final M menu;
	protected boolean hasRobe = false;
	protected boolean hasPorch = false;
	protected boolean hasWand = false;
	protected boolean hasBook = false;
	private final Map<Integer, SMButton> buttonMap = new HashMap<>();
	private final List<SMRenderTex> renderList = new ArrayList<>();
	protected Map<Slot, Integer> stackCountMap = new HashMap<>();
	protected static final String[] ENCODED_SUFFIXES = {"K", "M", "G"};
	protected static final ResourceLocation MISC = SweetMagicCore.getSRC("textures/gui/gui_misc.png");

	public GuiSMBase(M menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.menu = menu;
		this.player = pInv.player;
	}

	public void render(PoseStack pose, int mouseX, int mouseY, float parTick) {
		this.renderBackground(pose);
		super.render(pose, mouseX, mouseY, parTick);
		this.renderTooltip(pose, mouseX, mouseY);
	}

	protected void renderBGBase(PoseStack pose, float parTick, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		RenderSystem.setShaderTexture(0, this.getTEX());
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		this.blit(pose, this.leftPos, this.topPos, 0, 0, this.getGuiWidth(), this.getGuiHeight());
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		this.renderBGBase(pose, parTick, mouseX, mouseY);

		if (!this.getButtonMap().isEmpty()) {
			this.buttonMapRender(pose);
		}

		if (!this.getRenderTexList().isEmpty()) {
			this.renderTexRender(pose);
		}
	}

	public void buttonMapRender(PoseStack pose) {

		Map<Integer, SMButton> buttonMap = this.getButtonMap();
		for (Entry<Integer, SMButton> map : buttonMap.entrySet()) {
			SMButton button = map.getValue();
			if (!button.isButtonRender()) { continue; }

			int x = button.getX();
			int y = button.getY();
			int texX = button.getTexX();
			int texY = button.getTexY();
			int sizeX = button.getSizeX();
			int sizeY = button.getSizeY();
			RenderSystem.setShaderTexture(0, button.getTex());
			this.blit(pose, this.leftPos + x, this.topPos + y, texX + (button.isView() ? sizeX : 0), texY, sizeX, sizeY);
		}
	}

	public void renderTexRender(PoseStack pose) {

		for (SMRenderTex render : this.getRenderTexList()) {

			int x = render.getX();
			int y = render.getY();
			int texX = render.getTexX();
			int texY = render.getTexY();
			int sizeX = render.getSizeX();
			int sizeY = render.getSizeY();
			RenderSystem.setShaderTexture(0, render.getTex());

			if (render.getMFRender() != null) {

				MFRenderGage mfRender = render.getMFRender();
				TileSMMagic tile = mfRender.tile();
				RenderSystem.setShaderTexture(0, MISC);

				if (mfRender.isVertical()) {
					this.blit(pose, this.leftPos + x, this.topPos + y, 0, 93, 11, 77);
				}

				else if(!(tile instanceof TileMFChanger)) {
					this.blit(pose, this.leftPos + x, this.topPos + y, 22, 148, 77, 11);
				}

				// こっちではゲージ量を計算する
				if (!tile.isMFEmpty()) {

					int progress = tile.getMFProgressScaled(76);

					if (mfRender.isVertical()) {
						this.blit(pose, this.leftPos + x, this.topPos + y + 76 - progress, 11, 93 + 76 - progress, 11, progress);
					}

					else if(!(tile instanceof TileMFChanger)) {
						this.blit(pose, this.leftPos + x, this.topPos + y, 22, 159, progress, 11);
					}

					else {
						RenderSystem.setShaderTexture(0, this.getTEX());
						progress = tile.getMFProgressScaled(106);
						this.blit(pose, this.leftPos + x, this.topPos + y, 0, 166, progress, 10);
					}
				}

				RenderSystem.setShaderTexture(0, this.getTEX());
			}

			else {
				this.blit(pose, this.leftPos + x, this.topPos + y, texX, texY, sizeX, sizeY);
			}
		}
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {

		int x = this.getWidth();
		int y = this.getHeight();

		if (!this.getButtonMap().isEmpty()) {
			this.buttonMapLabel(pose, mouseX, mouseY, x, y);
		}

		if (!this.getRenderTexList().isEmpty()) {
			this.renderTexLabel(pose, mouseX, mouseY, x, y);
		}
	}

	public void buttonMapLabel(PoseStack pose, int mouseX, int mouseY, int texSizeX, int texSizeY) {

		Map<Integer, SMButton> buttonMap = this.getButtonMap();
		for (Entry<Integer, SMButton> map : buttonMap.entrySet()) {

			SMButton button = map.getValue();
			int x = button.getX();
			int y = button.getY();
			int sizeX = button.getSizeX();
			int sizeY = button.getSizeY();
			int tipX = texSizeX + x;
			int tipY = texSizeY + y;
			button.setIsView(false);
			if (!this.isRender(tipX, tipY, mouseX, mouseY, sizeX, sizeY)) { continue; }

			button.setIsView(true);
			SMButton.SMButtonTip buttonTip = button.getButtonTip();
			if (buttonTip == null) { continue; }

			MutableComponent tip = this.getText(buttonTip.getTip());
			int lenght = this.font.width(tip.getString());

			// GUIの左上からの位置
			int xAxis = mouseX - texSizeX - lenght + buttonTip.getTipX();
			int yAxis = mouseY - texSizeY + buttonTip.getTipY();
			this.renderTooltip(pose, tip, xAxis, yAxis);
		}
	}

	public void renderTexLabel(PoseStack pose, int mouseX, int mouseY, int texSizeX, int texSizeY) {

		for (SMRenderTex render : this.getRenderTexList()) {

			int x = render.getX();
			int y = render.getY();
			int sizeX = render.getSizeX();
			int sizeY = render.getSizeY();
			int tipX = texSizeX + x;
			int tipY = texSizeY + y;
			if (!this.isRender(tipX, tipY, mouseX, mouseY, sizeX, sizeY)) { continue; }

			SMButton.SMButtonTip buttonTip = render.getButtonTip();

			if (buttonTip != null) {
				MutableComponent tip = this.getText(buttonTip.getTip());
				int lenght = this.font.width(tip.getString());

				// GUIの左上からの位置
				int xAxis = mouseX - texSizeX - lenght + buttonTip.getTipX();
				int yAxis = mouseY - texSizeY + buttonTip.getTipY();
				this.renderTooltip(pose, tip, xAxis, yAxis);
			}

			else if (render.getMFRender() != null) {

				MFRenderGage mfRender = render.getMFRender();
				TileSMMagic tile = mfRender.tile();

				int mf = tile.getMF();
				int max = tile.getMaxMF();
				String par = " (" + tile.getMFPercent() + ")";
				int xAxis = mouseX - this.getWidth();
				int yAxis = mouseY - this.getHeight();

				String tip = this.format(mf) + "mf / " + this.format(max) + "mf" + par;
				this.renderTooltip(pose, this.getLabel(tip), xAxis, yAxis);
			}
		}
	}

	public boolean mouseClicked(double guiX, double guiY, int mouseButton) {

		Map<Integer, SMButton> buttonMap = this.getButtonMap();
		for (Entry<Integer, SMButton> map : buttonMap.entrySet()) {

			SMButton button = map.getValue();
			int aX = this.leftPos + button.getX();
			int aY = this.topPos + button.getY();
			int width = button.getSizeX();
			int height = button.getSizeY();

			// スクロールバーの当たり判定チェック
			if (guiX >= aX && guiX < aX + width && guiY >= aY && guiY < aY + height) {
				this.clickSMButton(map.getKey());
			}
		}

		return super.mouseClicked(guiX, guiY, mouseButton);
	}

	public void clickSMButton(int id) {
		this.clickButton(id);
	}

	protected abstract ResourceLocation getTEX();

	protected int getWidth() {
		return (this.width - this.getGuiWidth()) / 2;
	}

	protected int getHeight() {
		return (this.height - this.getGuiHeight()) / 2;
	}

	public void setGuiSize(int imageWidth, int imageHeight) {
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}

	public int getGuiWidth() {
		return this.imageWidth;
	}

	public int getGuiHeight() {
		return this.imageHeight;
	}

	protected void clickButton(int id) {
		this.menu.clickMenuButton(this.player, id);
		this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
	}

	// ボタンリスト
	private Map<Integer, SMButton> getButtonMap() {
		return this.buttonMap;
	}

	// レンダーリスト
	private List<SMRenderTex> getRenderTexList() {
		return this.renderList;
	}

	// ボタンリスト
	protected void addButtonMap(Integer key, SMButton val) {
		this.buttonMap.put(key, val);
	}

	// レンダーリスト
	protected void addRenderTexList(SMRenderTex val) {
		this.renderList.add(val);
	}

	protected void drawFont(PoseStack pose, Component tip, float maxSize, float x, float y, int color, boolean isShadow) {
		pose.pushPose();
		int spSize2 = this.font.width(tip.getString());
		pose.scale(spSize2 < maxSize ? 1F : maxSize / spSize2, 1F, 1F);
		float addX2 = spSize2 < maxSize ? 1F : spSize2 / maxSize;

		if(isShadow) {
			this.font.drawShadow(pose, tip, (x) * addX2, y, color);
		}

		else {
			this.font.draw(pose, tip, (x) * addX2, y, color);
		}

		pose.popPose();
	}

	// アイテム描画
	public void renderSlotItem(Slot slot, ItemStack stack, PoseStack pose) {
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

	// アイテム描画
	public void renderSlotItem(Slot slot, ItemStack stack, int count, PoseStack pose) {
		ItemStack copy = stack.copy();
		copy.setCount(count);
		this.renderSlotItem(slot, copy, pose);
	}

	public void renderMagiaStack(PoseStack pose, int mouseX, int mouseY, float parTick, NonNullList<Slot> slotList, NonNullList<Slot> slots) {

		this.stackCountMap.clear();

		for (int i = 0; i < slotList.size(); i++) {
			Slot slot = slots.get(i);
			if(!slot.isActive()) { continue; }

			ItemStack stack = slots.get(i).getItem().copy();
			this.stackCountMap.put(slot, stack.getCount());

			if (stack.isEmpty() || this.checkStackCount(stack.getCount())) { continue; }
			stack.setCount(1);
			slot.set(stack);
		}

		this.renderBackground(pose);
		super.render(pose, mouseX, mouseY, parTick);
		this.renderTooltip(pose, mouseX, mouseY);

		for (int i = 0; i < slotList.size(); i++) {
			Slot slot = slots.get(i);
			if (!slot.isActive() || this.stackCountMap.get(slot) == null) { continue; }

			int count = this.stackCountMap.get(slot);
			if (this.checkStackCount(count)) { continue; }

			ItemStack stack = slot.getItem();
			if (stack.isEmpty()) { continue; }

			if (count > 1) {
				this.renderSlotCount(slot, stack, count, pose);
			}
			stack.setCount(count);
			slot.set(stack);
		}
	}

	public boolean checkStackCount(int count) {
		return count <= 1;
	}

	protected void renderMagiaStackTooltip(PoseStack pose, ItemStack stack, int x, int y, NonNullList<Slot> slotList) {
		List<Component> tipList = this.getTooltipFromItem(stack);

		if (this.hoveredSlot instanceof MagiaSlot slot) {
			int vCount = this.stackCountMap.get(slot);
			if(vCount <= 0) { return; }

//			int vCount = this.stackCountMap.get(i);
			int vMax = this.hoveredSlot.getMaxStackSize();
			String count = this.format(vCount) + "/";
			String max = this.format(vMax);
			String par = " (" + this.format(((float) vCount / (float) vMax) * 100F) + "%" + ")";
			tipList.add(this.getLabel(count + max + par, GOLD));
//			for(int i = 0; i < slotList.size(); i++) {
//				if(slotList.get(i) != slot) { continue; }
//
//				int index = this.hoveredSlot.getSlotIndex();
//				if (i >= this.stackCountMap.size()) { return; }
//
//			}
		}
		this.renderTooltip(pose, tipList, stack.getTooltipImage(), x, y);
	}

	// アイテム描画
	public void renderSlotCount(Slot slot, ItemStack stack, int count, PoseStack pose) {
		int x = this.leftPos + slot.x;
		int y = this.topPos + slot.y + 0;
		PoseStack view = RenderSystem.getModelViewStack();

		view.pushPose();
		view.mulPoseMatrix(pose.last().pose());
		Font font = IClientItemExtensions.of(stack).getFont(stack, IClientItemExtensions.FontContext.TOOLTIP);
		font = font == null ? this.font : font;

		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(516);
		GuiComponent.fill(view, x, y, x + 16, y + 16, 0);
		RenderSystem.depthFunc(515);
		RenderSystem.disableDepthTest();
		String stackSize = String.valueOf(count);

		if (stackSize.length() > 3) {
			int stackLength = (stackSize.length() - 1) / 3;
			String encord = ENCODED_SUFFIXES[stackLength - 1];
			stackSize = stackSize.substring(0, stackSize.length() - stackLength * 3 + 1) + encord;
			stackSize = stackSize.substring(0, stackSize.length() - 2) + "." + stackSize.substring(stackSize.length() - 2, stackSize.length());
		}

		MultiBufferSource.BufferSource buf = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		PoseStack poses = new PoseStack();
		poses.scale(0.5F, 0.5F, 0.5F);
		poses.translate(0D, 0D, 600D);
		font.drawInBatch(stackSize, (x + 6 + 9) * 2 - font.width(stackSize), (y + 12) * 2, 16777215, true, poses.last().pose(), buf, false, 0, 15728880);
		buf.endBatch();
		view.popPose();
	}

	// カーソルがあった時に描画するか
	public boolean isRender(int tipX, int tipY, int mouseX, int mouseY, int maxX, int maxY) {
		return tipX <= mouseX && mouseX <= tipX + maxX && tipY <= mouseY && mouseY <= tipY + maxY;
	}

	// スロットの説明
	public void renderItemLabel(Slot slot, ItemStack stack, PoseStack pose, int mouseX, int mouseY, List<Component> tipList) {
		if (!slot.getItem().isEmpty()) { return; }

		int tipX = this.getWidth() + slot.x;
		int tipY = this.getHeight() + slot.y;
		if (!this.isRender(tipX, tipY, mouseX, mouseY, 16, 16)) { return; }

		int xAxis = (mouseX - this.getWidth());
		int yAxis = (mouseY - this.getHeight());
		this.renderComponentTooltip(pose, tipList, xAxis, yAxis);
	}

	// スロットの説明
	public void renderItemLabel(Slot slot, ItemStack stack, PoseStack pose, int mouseX, int mouseY) {
		if (!slot.getItem().isEmpty()) { return; }

		int tipX = this.getWidth() + slot.x;
		int tipY = this.getHeight() + slot.y;
		if (!this.isRender(tipX, tipY, mouseX, mouseY, 16, 16)) { return; }

		int xAxis = mouseX - this.getWidth();
		int yAxis = mouseY - this.getHeight();
		List<Component> tipList = new ArrayList<>();
		this.setTip(tipList, stack);
		this.renderComponentTooltip(pose, tipList, xAxis, yAxis);
	}

	public void renderStock(List<ItemStack> tileStackList, PoseStack pose, int stockId, int restockId, int addX, int addY) {
		SMButton quick = this.getButtonMap().get(stockId);
		SMButton restock = this.getButtonMap().get(restockId);
		if (!quick.isView() && !restock.isView()) { return; }

		List<ItemStack> pInveList = this.player.getInventory().items;
		RenderSystem.setShaderTexture(0, MISC);

		if (quick.isView()) {

			List<Item> itemList = new ArrayList<>();

			for (ItemStack stack : tileStackList) {
				if (stack.isEmpty()) { continue; }
				itemList.add(stack.getItem());
			}

			this.renderInvStock(itemList, pInveList, pose, addX, addY);
		}

		if (!restock.isView()) { return; }

		List<Item> itemList = new ArrayList<>();

		for (ItemStack stack : pInveList) {
			if (stack.isEmpty()) { continue; }
			itemList.add(stack.getItem());
		}

		this.renderChestStock(itemList, tileStackList, pose, addX, addY);
	}

	public void renderInvStock(List<Item> itemList, List<ItemStack> pInveList, PoseStack pose, int addX, int addY) {

		for (int x = 0; x < 9; x++) {
			ItemStack stack = pInveList.get(x);
			if (stack.isEmpty() || !itemList.contains(stack.getItem())) { continue; }

			this.blit(pose, this.leftPos + 47 + x * 18 + addX, this.topPos + 209 + addY, 114, 47, 18, 18);
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				ItemStack stack = pInveList.get(9 + x + y * 9);
				if (stack.isEmpty() || !itemList.contains(stack.getItem())) { continue; }

				this.blit(pose, this.leftPos + 47 + x * 18 + addX, this.topPos + 150 + y * 18 + addY, 114, 47, 18, 18);
			}
		}
	}

	public void renderChestStock(List<Item> itemList, List<ItemStack> tileStackList, PoseStack pose, int addX, int addY) {
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 13; x++) {
				if (itemList.contains(tileStackList.get(x + y * 13).getItem())) {
					this.blit(pose, this.leftPos + 11 + x * 18 + addX, this.topPos + 4 + y * 18 + addY, 114, 47, 18, 18);
				}
			}
		}
	}

	public void setTip(List<Component> tipList, ItemStack stack) {
		if (stack.isEmpty()) {
			tipList.add(this.getText("enchated").withStyle(GREEN));
		}

		else {
			tipList.addAll(Arrays.<Component> asList(this.getText("slot_need").withStyle(GREEN), stack.getDisplayName()));
		}
	}

	public static List<ItemStack> getTagStack(TagKey<Item> tags) {
		return ForgeRegistries.ITEMS.tags().getTag(tags).stream().map(Item::getDefaultInstance).collect(Collectors.toList());
	}

	public boolean hasWand() {
		ItemStack stack = IWand.getWand(this.player);
		return !stack.isEmpty() && stack.getItem() instanceof IWand;
	}

	public boolean hasPorch() {
		return this.player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof IPorch;
	}

	public boolean hasRobe() {
		return this.player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof IRobe;
	}

	public void drawFluid(Matrix4f mat, float x, float y, int width, int height, TextureAtlasSprite sprite, int top) {
		float uMin = sprite.getU0();
		float uMax = sprite.getU1();
		float vMin = sprite.getV0();
		float vMax = sprite.getV1();
		vMax = vMax - (top / 16F * (vMax - vMin));

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		Tesselator tes = Tesselator.getInstance();
		BufferBuilder buf = tes.getBuilder();
		buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		buf.vertex(mat, x, y + height, 100F).uv(uMin, vMax).endVertex();
		buf.vertex(mat, x + width, y + height, 100F).uv(uMax, vMax).endVertex();
		buf.vertex(mat, x + width, y, 100F).uv(uMax, vMin).endVertex();
		buf.vertex(mat, x, y, 100F).uv(uMin, vMin).endVertex();
		tes.end();
	}
}
