package sweetmagic.event;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.event.SMUtilEvent;
import sweetmagic.api.iblock.ISMNeedItem;
import sweetmagic.init.tile.sm.TileAbstractMagicianLectern;
import sweetmagic.init.tile.sm.TileAbstractMagicianLectern.SummonType;

@Mod.EventBusSubscriber(modid = SweetMagicCore.MODID, value = Dist.CLIENT)
public class MagicianLecternViewEvent extends SMUtilEvent {

	// GUIの取得
	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_usergage.png");
	private static List<ItemStack> stackList = new ArrayList<>();
	private static boolean isHard = false;
	public static int tickTime = 0;
	public static int renderTick = 0;

	// レンダーイベントの呼び出し
	@SubscribeEvent
	public static void onWandRenderEvent(RenderGuiOverlayEvent.Post event) {
		if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) { return; }

		if (!stackList.isEmpty()) {

			int height = event.getWindow().getGuiScaledHeight();	// 画面サイズの高さを取得
			int weight = event.getWindow().getGuiScaledWidth();		// 画面サイズの高さを取得
			int addX = (int) (25 - 10 * getProgress(20));
			int addY = -10;

			// レンダーの開始
			renderStart(TEX);
			RenderSystem.setShaderColor(1F, 1F, 1F, getProgress(10));
			PoseStack pose = event.getPoseStack();
			Matrix4f mat = pose.last().pose();

			drawTextured(mat, weight - 200 + addX, height - 186 + addY, 90, 144, 46, 97);
			drawTextured(mat, weight - 200 + addX, height - 202 + addY, 145, 179, 104, 24);

			Minecraft mc = Minecraft.getInstance();
			Font font = mc.font;
			ItemRenderer render = mc.getItemRenderer();

			MutableComponent text = getText("needitem_0");
			font.drawShadow(pose, text, weight - 198 + addX, height - 210, 0xF9B848);

			MutableComponent text2 = getText("needitem_1");

			if (MagicianLecternViewEvent.isHard) {
				text2.append(getText("ishard").withStyle(ChatFormatting.RED));
			}

			font.drawShadow(pose, text2, weight - 198 + addX, height - 200, 0xF9B848);

			// レシピから完成品を取得
			List<ItemStack> stackList = MagicianLecternViewEvent.stackList;

			for (int i = 0; i < stackList.size(); i++) {

				ItemStack stack = stackList.get(i);
				if (stack.isEmpty()) { continue; }

				render.renderAndDecorateItem(stack, weight - 195 + addX, height - 166 + addY + i * 18);
				render.renderGuiItemDecorations(font, stack, weight - 195 + addX, height - 166 + addY + i * 18);
			}

			tickTime++;
			renderTick++;
			if (tickTime % 5 == 0) {
				tickTime = 0;
				MagicianLecternViewEvent.stackList.clear();
			}
		}

		else {
			renderTick = 0;
		}
	}

	@SubscribeEvent
	public static void highlightBlockEvent(RenderHighlightEvent.Block event) {
		Camera camera = event.getCamera();
		if (!(camera.getEntity() instanceof Player player)) { return; }

		Level world = player.getLevel();
		BlockPos pos = event.getTarget().getBlockPos();
		Block block = world.getBlockState(pos).getBlock();
		MagicianLecternViewEvent.stackList.clear();
		MagicianLecternViewEvent.isHard = false;

		if (block instanceof ISMNeedItem needBlock) {

			TileAbstractMagicianLectern tile = (TileAbstractMagicianLectern) world.getBlockEntity(pos);

			if (tile.summonType.is(SummonType.START) || tile.summonType.is(SummonType.END)) {
				MagicianLecternViewEvent.isHard = needBlock.isHard(player);
				MagicianLecternViewEvent.stackList = isHard ? needBlock.getNeedHardItemList() : needBlock.getNeedItemList();
			}
		}
	}

	public static float getProgress (int maxTime) {
		return Math.min(1F, (float) renderTick / maxTime);
	}
}
