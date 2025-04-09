package sweetmagic.init.tile.gui;

import java.util.Arrays;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.ItemInit;
import sweetmagic.init.block.magic.AetherHopper;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.gui.util.SMButton.SMButtonTip;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.AetherHopperMenu;
import sweetmagic.init.tile.sm.TileAetherHopper;

public class GuiAetherHopper extends GuiSMBase<AetherHopperMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_aether_hopper.png");
	private final static ItemStack WAND = new ItemStack(ItemInit.mf_stuff);
	private final TileAetherHopper tile;
	protected final AetherHopperMenu menu;

	public GuiAetherHopper(AetherHopperMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(176, 175);
		this.tile = (TileAetherHopper) menu.tile;
		this.menu = menu;
		this.addButtonMap(0, new SMButton(MISC, 150, -11, 114, 0, 10, 9, new SMButtonTip("sort", -18, 14)));
		this.addRenderTexList(new SMRenderTex(TEX, 7, 10, 0, 0, 11, 77, new MFRenderGage(this.tile, true)));
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		// アイテム描画
		this.renderSlotItem(this.menu.wandSlot, WAND, pose);

		// NBTを持っていないなら終了
		ItemStack stack = this.tile.getWandItem();
		CompoundTag tag = stack.getTag();
		if (tag == null || !tag.contains("X")) { return; }

		// 座標の取得
		int x = tag.getInt("X");
		int y = tag.getInt("Y");
		int z = tag.getInt("Z");
		String pos = ": " + x + ", " + y + ", " + z;

		boolean isHopper = this.tile.getBlock(new BlockPos(x, y, z)) instanceof AetherHopper;
		ChatFormatting color = isHopper ? GREEN : RED;
		int tipX = this.getWidth() + 50;
		int tipY = this.getHeight() + 74;
		this.font.drawShadow(pose, this.getTipArray(this.getText("regi_pos"), pos).withStyle(color), tipX, tipY, 0x2BC444);
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);
		this.renderItemLabel(this.menu.wandSlot, WAND, pose, mouseX, mouseY, Arrays.<Component> asList(this.getText("send_stuff_0").withStyle(GOLD), this.getText("send_stuff_1").withStyle(GOLD), this.getTip(""), this.getText("can_stuff").withStyle(GREEN), WAND.getDisplayName()));
	}

	protected ResourceLocation getTEX() {
		return TEX;
	}
}
