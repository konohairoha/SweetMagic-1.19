package sweetmagic.init.tile.gui;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.MFMinerAdvancedMenu;

public class GuiMFMinerAdvanced extends GuiSMBase<MFMinerAdvancedMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_mf_miner_advanced.png");
	private final MFMinerAdvancedMenu menu;
	private int counter = 0;
	private int tickTime = 0;
	private final static List<ItemStack> STONE_LIST = ForgeRegistries.ITEMS.tags().getTag(Tags.Items.COBBLESTONE).stream().map(Item::getDefaultInstance).collect(Collectors.toList());

	public GuiMFMinerAdvanced(MFMinerAdvancedMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiWidth(176);
		this.setGuiHeight(253);
		this.menu = menu;
		this.getRenderTexList().add(new SMRenderTex(TEX, 7, 7, 0, 0, 11, 77, new MFRenderGage(menu.tile, 179, 7, 11, 76, 76, true)));
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		if (this.menu.tile.craftTime > 0) {
			int x = this.getWidth();
			int y = this.getHeight();
			int progress = this.menu.tile.getCraftProgressScaled(22);
			this.blit(pose, x + 81, y + 62, 194, 62, 15, progress);
		}

		// 魔術書を順番に表示
		if (this.tickTime++ > 120) {
			this.tickTime = 0;

			if (this.counter++ >= STONE_LIST.size() - 1) {
				this.counter = 0;
			}
		}

		this.renderSlotItem(this.menu.stoneSlotList.get(0), STONE_LIST.get(this.counter), pose);
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);

		// 要求アイテムの描画
		this.renderItemLabel(this.menu.stoneSlotList.get(0), STONE_LIST.get(this.counter), pose, mouseX, mouseY);
	}

	public void setTip (List<Component> tipList, ItemStack stack) {
		tipList.addAll(Arrays.<Component> asList(this.getText("slot_need").withStyle(GREEN), stack.getDisplayName()));
	}

	protected ResourceLocation getTEX () {
		return TEX;
	}
}
