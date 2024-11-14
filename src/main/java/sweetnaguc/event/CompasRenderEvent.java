package sweetmagic.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.event.SMUtilEvent;
import sweetmagic.init.item.sm.DungeonCompas;

@OnlyIn(Dist.CLIENT)
public class CompasRenderEvent extends SMUtilEvent {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_misc.png");

	// レンダーイベントの呼び出し
	@SubscribeEvent
	public static void onWandRenderEvent(RenderGuiOverlayEvent.Post event) {
		if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) { return; }

		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (player.isSpectator()) { return; }

		ItemStack stack = player.getMainHandItem();
		if ( !(stack.getItem() instanceof DungeonCompas compas) ) { return; }

		CompoundTag tags = stack.getOrCreateTag();
		PoseStack pose = event.getPoseStack();
		Matrix4f mat = pose.last().pose();
		int height = event.getWindow().getGuiScaledHeight() + 60;	// 画面サイズの高さを取得
		int weight = event.getWindow().getGuiScaledWidth() + 8;		// 画面サイズの高さを取得

		// レンダーの開始
		renderStart(TEX);
		RenderSystem.setShaderColor(1F, 1F, 1F, 100F / 255F);
		drawTextured(mat, weight - 80, height - 190, 192, 192, 64, 64);
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		drawTextured(mat, weight - 80, height - 190, 0, 192, 64, 64);

		Font font = mc.font;
		int posX = weight - 80;
		int posZ = height - 120;

		if (tags.getBoolean("isSearch")) {
			renderFont(pose, font, getText("is_serach"), posX + 60, posZ);
			return;
		}

		if (tags.getBoolean("notFound")) {
			renderFont(pose, font, getTip("structure.sweetmagic." + compas.getDungeonName(stack)), posX + 60, posZ + 12);
			renderFont(pose, font, getText("not_found"), posX + 60, posZ);
			return;
		}

		int pX = tags.getInt("X");
		int pY = player.blockPosition().getY();
		int pZ = tags.getInt("Z");
		BlockPos pos = new BlockPos(pX, pY, pZ);

		if (tags.getBoolean("foundStructure")) {
			float angle = compas.getRotCompas(player, pos);
			float addX = (float) Math.sin(angle * 6.15F) * 21F;
			float addY = -(float) Math.cos(angle * 6.15F) * 21F;
			drawTextured(mat, weight - 52 + (int) addX, height - 162 + (int) addY, 64, 200, 9, 9);
		}

		double dis = Math.abs(player.xo - pos.getX()) + Math.abs(player.zo - pos.getZ()) - 0.5D;
		boolean foundStructure = tags.getBoolean("foundStructure");
		posZ = !foundStructure ? posZ - 12 : posZ;
		renderFont(pose, font, getTip("structure.sweetmagic." + compas.getDungeonName(stack)), posX + 60, posZ + 12);

		if (foundStructure) {
			renderFont(pose, font, getLabel(pX + ", " + pZ), posX + 60, posZ);
			renderFont(pose, font, getLabel(String.format("%,.1f", dis) + " Block"), posX + 60, posZ + 24);
		}

		else {
			renderFont(pose, font, getText("compas_click").withStyle(RED), posX + 60, posZ + 24);
		}
	}

	public static void renderFont (PoseStack pose, Font font, MutableComponent tip, int x, int z) {
		int nameSize = font.width(tip.getString());
		font.drawShadow(pose, tip, x - nameSize, z, 0xffffff);
	}
}
