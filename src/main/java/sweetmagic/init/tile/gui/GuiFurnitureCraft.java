package sweetmagic.init.tile.gui;

import java.util.Arrays;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.handler.PacketHandler;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.menu.FurnitureCraftMenu;
import sweetmagic.init.tile.sm.TileFurnitureTable;
import sweetmagic.packet.FurnitureCraftPKT;

public class GuiFurnitureCraft extends GuiSMBase<FurnitureCraftMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_furniture_craft.png");
	private final TileFurnitureTable tile;
	private final FurnitureCraftMenu menu;
	private EditBox count;
	private int oldSetCount = 0;
	private boolean isInit = false;

	public GuiFurnitureCraft(FurnitureCraftMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.tile = menu.tile;
		this.menu = menu;
		this.setGuiSize(80, 81);
		this.addButtonMap(0, new SMButton(TEX, 9, 9, 83, 9, 20, 11));
		this.addButtonMap(1, new SMButton(TEX, 9, 43, 83, 9, 20, 11));
		this.addButtonMap(2, new SMButton(TEX, 30, 9, 83, 9, 20, 11));
		this.addButtonMap(3, new SMButton(TEX, 30, 43, 83, 9, 20, 11));
		this.addButtonMap(4, new SMButton(TEX, 51, 9, 83, 9, 20, 11));
		this.addButtonMap(5, new SMButton(TEX, 51, 43, 83, 9, 20, 11));
		this.addButtonMap(6, new SMButton(TEX, 9, 59, 83, 23, 29, 12));
		this.addButtonMap(7, new SMButton(TEX, 42, 59, 83, 23, 29, 12));
	}

	public void render(PoseStack pose, int mouseX, int mouseY, float parTick) {
		super.render(pose, mouseX, mouseY, parTick);
		this.count.render(pose, mouseX, mouseY, parTick);
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		int x = this.getWidth();
		int y = this.getHeight();

		this.font.drawShadow(pose, this.getLabel("+1"), x + 14, y + 11, 0xEEEEEE);
		this.font.drawShadow(pose, this.getLabel("-1"), x + 14, y + 45, 0xEEEEEE);
		this.font.drawShadow(pose, this.getLabel("+10"), x + 31, y + 11, 0xEEEEEE);
		this.font.drawShadow(pose, this.getLabel("-10"), x + 31, y + 45, 0xEEEEEE);
		this.font.drawShadow(pose, this.getLabel("+64"), x + 52, y + 11, 0xEEEEEE);
		this.font.drawShadow(pose, this.getLabel("-64"), x + 52, y + 45, 0xEEEEEE);
		this.font.drawShadow(pose, this.getText("decision"), x + 14, y + 61, 0xEEEEEE);
		this.font.drawShadow(pose, this.getText("cancel"), x + 46, y + 61, 0xEEEEEE);
		this.renderSlotItem(this.menu.resultSlot, this.tile.outStack, pose);
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);

		// クラフト後のアイテムのチップを表示
		if (!this.tile.outStack.isEmpty()) {
			this.renderItemLabel(this.menu.resultSlot, this.tile.outStack, pose, mouseX, mouseY, Arrays.<Component> asList(this.getText("craft_item").withStyle(GREEN), this.tile.outStack.getHoverName()));
		}
	}

	public void containerTick() {
		super.containerTick();
		this.count.tick();

		if (this.oldSetCount != this.tile.setCount) {
			this.count.setValue("" + this.tile.setCount);
			this.oldSetCount = this.tile.setCount;
		}
	}

	protected void init() {
		super.init();
		this.subInit();
	}

	protected void subInit() {

		int x = this.getWidth();
		int y = this.getHeight();

		this.count = new EditBox(this.font, x + 12, y + 27, 116, 12, this.getTip("container.repair1"));
		this.count.setCanLoseFocus(false);
		this.count.setTextColor(-1);
		this.count.setTextColorUneditable(-1);
		this.count.setBordered(false);
		this.count.setMaxLength(40);
		this.count.setResponder(this::onNameChanged);
		this.count.setValue("" + this.tile.setCount);

		this.addWidget(this.count);
		this.setInitialFocus(this.count);
		this.setFocused(this.count);

		this.isInit = true;
	}

	public void resize(Minecraft mc, int x, int y) {
		String countString = this.count.getValue();
		this.init(mc, x, y);
		this.count.setValue(countString);
	}

	public boolean keyPressed(int id, int par1, int par2) {

		if (id == 256) {
			this.minecraft.player.closeContainer();
		}

		return !this.count.keyPressed(id, par1, par2) ? super.keyPressed(id, par1, par2) : true;
	}

	private void onNameChanged(String name) {
		if (name.isEmpty()) { return; }

		String s = name;
		this.minecraft.player.connection.send(new ServerboundRenameItemPacket(s));
		if (!this.isInt(s) || !this.isInit) { return; }

		int value = Integer.valueOf(s);
		if (this.tile.setCount == value) { return; }

		PacketHandler.sendToServer(new FurnitureCraftPKT(this.getValue(s), this.tile.getBlockPos()));
	}

	private boolean isInt(String str) {
		return str != null && str.matches("[0-9]+");
	}

	private Integer getValue(String str) {
		return Math.min(1024, Math.max(1, Integer.valueOf(str)));
	}

	protected ResourceLocation getTEX() {
		return TEX;
	}
}
