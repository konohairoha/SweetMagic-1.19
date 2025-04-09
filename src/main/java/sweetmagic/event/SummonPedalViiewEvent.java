package sweetmagic.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
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
import sweetmagic.init.block.sm.SummonPedal;

@Mod.EventBusSubscriber(modid = SweetMagicCore.MODID, value = Dist.CLIENT)
public class SummonPedalViiewEvent extends SMUtilEvent {

	// GUIの取得
	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_usergage.png");
	private static boolean isView = false;
	public static int tickTime = 0;
	public static int renderTick = 0;

	// レンダーイベントの呼び出し
	@SubscribeEvent
	public static void onWandRenderEvent(RenderGuiOverlayEvent.Post event) {
		if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id()) || !SummonPedalViiewEvent.isView) { return; }

		int height = event.getWindow().getGuiScaledHeight();	// 画面サイズの高さを取得
		int addX = (int) (-10 * getProgress(20));
		int addY = -10;

		// レンダーの開始
		renderStart(TEX);
		RenderSystem.setShaderColor(1F, 1F, 1F, getProgress(10));
		PoseStack pose = event.getPoseStack();
		Matrix4f mat = pose.last().pose();

		drawTextured(mat, 36 + addX, height - 203 + addY, 137, 221, 117, 24);

		Minecraft mc = Minecraft.getInstance();
		Font font = mc.font;
		float limit = 112F;

		MutableComponent text = getText("summon_pedal_view1");

		int nameSize = font.width(text);
		pose.pushPose();
		pose.translate((nameSize - limit) / 10D + 1D, 0D, 0D);
		pose.scale(nameSize < limit ? 1F : limit / nameSize, 1F, 1F);
		font.drawShadow(pose, text, 40 + addX, height - 210, 0xF9B848);
		pose.popPose();
		MutableComponent text2 = getText("summon_pedal_view2");
		nameSize = font.width(text2);
		pose.pushPose();
		pose.translate((nameSize - limit) / 10D + 1D, 0D, 0D);
		pose.scale(nameSize < limit ? 1F : limit / nameSize, 1F, 1F);
		font.drawShadow(pose, text2, 40 + addX, height - 201, 0xF9B848);
		pose.popPose();

		renderTick++;
		if (tickTime++ % 5 == 0) {
			tickTime = 0;
			SummonPedalViiewEvent.isView = false;
		}
	}

	@SubscribeEvent
	public static void highlightBlockEvent(RenderHighlightEvent.Block event) {
		Camera camera = event.getCamera();
		if (!(camera.getEntity() instanceof Player player) || SummonPedalViiewEvent.isView) { return; }

		Level world = player.getLevel();
		BlockPos pos = event.getTarget().getBlockPos();
		Block block = world.getBlockState(pos).getBlock();
		SummonPedalViiewEvent.isView = false;

		if (block instanceof SummonPedal pedal) {
			SummonPedalViiewEvent.isView = true;
		}

		else {
			renderTick = 0;
		}
	}

	public static float getProgress (int maxTime) {
		return Math.min(1F, (float) renderTick / maxTime);
	}
}
