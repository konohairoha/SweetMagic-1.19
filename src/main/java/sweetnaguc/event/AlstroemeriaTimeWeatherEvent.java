package sweetmagic.event;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
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
import sweetmagic.init.ItemInit;
import sweetmagic.init.block.crop.Alstroemeria;
import sweetmagic.init.block.crop.Alstroemeria.TimeWeatherType;

@Mod.EventBusSubscriber(modid = SweetMagicCore.MODID, value = Dist.CLIENT)
public class AlstroemeriaTimeWeatherEvent extends SMUtilEvent {

	// GUIの取得
	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_usergage.png");
	private static TimeWeatherType timeWeather = null;
	public static int tickTime = 0;
	public static int renderTick = 0;

	// レンダーイベントの呼び出し
	@SubscribeEvent
	public static void onWandRenderEvent(RenderGuiOverlayEvent.Post event) {
		if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) { return; }

		if (timeWeather != null) {

			int height = event.getWindow().getGuiScaledHeight();	// 画面サイズの高さを取得
			int weight = event.getWindow().getGuiScaledWidth();		// 画面サイズの高さを取得
			int addX = (int) (25 - 10 * getProgress(20));
			int addY = -10;

			// レンダーの開始
			renderStart(TEX);
			RenderSystem.setShaderColor(1F, 1F, 1F, getProgress(10));
			PoseStack pose = event.getPoseStack();
			Matrix4f mat = pose.last().pose();

			drawTextured(mat, weight - 200 + addX, height - 186 + addY, 145, 144, 93, 29);

			Minecraft mc = Minecraft.getInstance();
			Font font = mc.font;
			ItemRenderer render = mc.getItemRenderer();
			MutableComponent tip = getText("als_" + AlstroemeriaTimeWeatherEvent.timeWeather.name().toLowerCase());
			font.drawShadow(pose, tip, weight - 135 + addX, height - 186, 0xffffff);

			MutableComponent text = getText("sneak_timeweather");
			font.drawShadow(pose, text, weight - 195 + addX, height - 175, 0xffffff);

			// レシピから完成品を取得
			List<ItemStack> stackList = new ArrayList<>();

			switch (AlstroemeriaTimeWeatherEvent.timeWeather) {
			case SUN:
				stackList.add(new ItemStack(ItemInit.fire_nasturtium_petal));
				break;
			case RAIN:
				stackList.add(new ItemStack(ItemInit.dm_flower));
				break;
			case DAYTIME:
				stackList.add(new ItemStack(ItemInit.sannyflower_petal));
				break;
			case TWILIGHT:
				stackList.add(new ItemStack(ItemInit.sannyflower_petal));
				stackList.add(new ItemStack(ItemInit.moonblossom_petal));
				break;
			case NIGHT:
				stackList.add(new ItemStack(ItemInit.moonblossom_petal));
				break;
			}

			for (int i = 0; i < stackList.size(); i++) {

				ItemStack stack = stackList.get(i);
				if (stack.isEmpty()) { continue; }

				render.renderAndDecorateItem(stack, weight - 194 + addX + i * 18, height - 180 + addY);
			}

			tickTime++;
			renderTick++;
			if (tickTime % 5 == 0) {
				tickTime = 0;
				AlstroemeriaTimeWeatherEvent.timeWeather = null;
			}
		}

		else {
			renderTick = 0;
		}
	}

	@SubscribeEvent
	public static void highlightBlockEvent(RenderHighlightEvent.Block event) {

		Camera camera = event.getCamera();
		if (!(camera.getEntity() instanceof Player player) || !player.getMainHandItem().isEmpty()) { return; }

		if (AlstroemeriaTimeWeatherEvent.timeWeather != null) { return; }

		Level world = player.getLevel();
		Block block = world.getBlockState(event.getTarget().getBlockPos()).getBlock();
		AlstroemeriaTimeWeatherEvent.timeWeather = null;

		if (block instanceof Alstroemeria als) {
			AlstroemeriaTimeWeatherEvent.timeWeather = als.getTimeWeather(world, player.getInventory().items, player);
		}
	}

	public static float getProgress (int maxTime) {
		return Math.min(1F, (float) renderTick / maxTime);
	}
}
