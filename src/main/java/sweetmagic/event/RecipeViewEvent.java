package sweetmagic.event;

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
import sweetmagic.api.iblock.ISMCookBlock;
import sweetmagic.api.iblock.ISMCraftBlock;
import sweetmagic.init.capability.icap.ICookingStatus;
import sweetmagic.recipe.RecipeHelper;
import sweetmagic.recipe.RecipeHelper.RecipeUtil;

@Mod.EventBusSubscriber(modid = SweetMagicCore.MODID, value = Dist.CLIENT)
public class RecipeViewEvent extends SMUtilEvent {

	// GUIの取得
	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_usergage.png");
	private static final ResourceLocation TEX2 = SweetMagicCore.getSRC("textures/gui/gui_cook_book.png");
	public static RecipeUtil recipe = null;
	public static boolean canShiftCraft = false;
	public static boolean isCookBlock = false;
	public static Player player = null;
	public static int tickTime = 0;
	public static int renderTick = 0;
	public static int renderCookTick = 0;

	// レンダーイベントの呼び出し
	@SubscribeEvent
	public static void onWandRenderEvent(RenderGuiOverlayEvent.Post event) {
		if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) { return; }

		if (RecipeViewEvent.recipe != null) {

			int height = event.getWindow().getGuiScaledHeight();	// 画面サイズの高さを取得
			int weight = event.getWindow().getGuiScaledWidth();		// 画面サイズの高さを取得
			int addX = (int) (25 - 10 * getProgress(renderTick, 20));
			int addY = -10;

			// レンダーの開始
			renderStart(TEX);
			RenderSystem.setShaderColor(1F, 1F, 1F, getProgress(renderTick, 10));
			PoseStack pose = event.getPoseStack();
			Matrix4f mat = pose.last().pose();

			drawTextured(mat, weight - 200 + addX, height - 186 + addY, 145, 0, 98, 131);
			renderStart(TEX);

			Minecraft mc = Minecraft.getInstance();
			Font font = mc.font;
			ItemRenderer render = mc.getItemRenderer();

			if(RecipeViewEvent.canShiftCraft) {
				drawTextured(mat, weight - 200 + addX, height - 210 + addY, 145, 179, 104, 23);

				for(int i = 0; i < 2; i++) {
					MutableComponent tip3 = getText("shift_craft_" + i);
					pose.pushPose();
					int spSize = font.width(tip3.getString());
					float maxSize = 100F;
					pose.scale(spSize < maxSize ? 1F : maxSize / spSize, 1F, 1F);
					float addX2 = spSize < maxSize ? 1F : spSize / maxSize;
					font.drawShadow(pose, tip3, (weight - 200 + addX) * addX2, height - 218 + i * 9, 0xF9B848);
					pose.popPose();
				}
			}

			// レシピから完成品を取得
			List<ItemStack> resultList = RecipeViewEvent.recipe.getResultList();
			List<ItemStack> inputList = RecipeViewEvent.recipe.getInputList();
			ItemStack inputStack = inputList.get(0);

			for (int i = 0; i < resultList.size(); i++) {
				ItemStack stack = resultList.get(i);
				if (stack.isEmpty()) { continue; }

				render.renderAndDecorateItem(stack, weight - 143 + addX, height - 179 + addY + i * 18);
				render.renderGuiItemDecorations(font, stack, weight - 143 + addX, height - 179 + addY + i * 18);
			}

			for (int i = 1; i < inputList.size(); i++) {
				ItemStack stack = inputList.get(i);
				if (stack.isEmpty()) { continue; }

				boolean second = i > 5;
				int x = second ? 18 : 0;
				int y = (!second ? (i - 1) : (i - 6) ) * 18;

				render.renderAndDecorateItem(stack, weight - 194 + x + addX, height - 149 + y + addY);
				render.renderGuiItemDecorations(font, stack, weight - 194 + x + addX, height - 149 + y + addY);
			}

			render.renderAndDecorateItem(inputStack, weight - 185 + addX, height - 179 + addY);
			render.renderGuiItemDecorations(font, inputStack, weight - 185 + addX, height - 179 + addY);

			renderTick++;
			if (tickTime++ % 5 == 0) {
				tickTime = 0;
				RecipeViewEvent.recipe = null;
			}
		}

		else {
			renderTick = 0;
		}

		if (RecipeViewEvent.isCookBlock && RecipeViewEvent.player != null) {

			Minecraft mc = Minecraft.getInstance();
			Font font = mc.font;
			PoseStack pose = event.getPoseStack();
			Matrix4f mat = pose.last().pose();
			Player player = RecipeViewEvent.player;
			int level = ICookingStatus.getState(player).getLevel();
			float addX2 = (int) (20 - 10 * getProgress(renderCookTick, 20));
			renderStart(TEX2);
			drawTextured(mat, (int) (addX2 - 3), 57, 0, 0, 137, 74);

			MutableComponent tip = getTipArray(getText("player_cook_level"), ": ", getLabel("" + level).withStyle(WHITE));
			font.drawShadow(pose, tip, addX2, 60, 0xF9B848);

			ICookingStatus status = ICookingStatus.getState(player);
			MutableComponent tip2 = getTipArray(getText("experience"), ": ", getLabel("" + status.needExp(level + 1)).withStyle(WHITE));
			font.drawShadow(pose, tip2, addX2, 71, 0xF9B848);
			float maxSize = 133F;

			if (player.isShiftKeyDown()) {

				for (int i = 1; i <= 4; i++) {
					MutableComponent tip3 = getText("cook_level_tip" + i);
					pose.pushPose();
					int spSize = font.width(tip3.getString());
					pose.scale(spSize < maxSize ? 1F : maxSize / spSize, 1F, 1F);
					float addX = spSize < maxSize ? 1F : spSize / maxSize;
					font.drawShadow(pose, tip3, (addX2 - 1.5F) * addX, 74 + i * 11, 0xEEEEEE);
					pose.popPose();
				}
			}

			else {
				MutableComponent tip3 = getText("shift").withStyle(RED);
				pose.pushPose();
				int spSize = font.width(tip3.getString());
				pose.scale(spSize < maxSize ? 1F : maxSize / spSize, 1F, 1F);
				float addX = spSize < maxSize ? 1F : spSize / maxSize;
				font.drawShadow(pose, tip3, (addX2 - 1.5F) * addX, 85, 0xEEEEEE);
				pose.popPose();
			}


			if (renderCookTick++ % 5 == 0) {
				RecipeViewEvent.isCookBlock = false;
			}
		}

		else {
			renderCookTick = 0;
		}
	}

	@SubscribeEvent
	public static void highlightBlockEvent(RenderHighlightEvent.Block event) {
		Camera camera = event.getCamera();
		if (!(camera.getEntity() instanceof Player player) || (RecipeViewEvent.isCookBlock && RecipeViewEvent.recipe != null)) { return; }

		Level world = player.getLevel();
		Block block = world.getBlockState(event.getTarget().getBlockPos()).getBlock();

		if (!RecipeViewEvent.isCookBlock) {
			RecipeViewEvent.isCookBlock = block instanceof ISMCookBlock cook && cook.isView();
			RecipeViewEvent.player = player;
		}

		if (RecipeViewEvent.recipe != null) { return; }

		RecipeViewEvent.recipe = null;

		if (block instanceof ISMCraftBlock smCraft) {

			List<ItemStack> stackList = RecipeHelper.getPlayerInv(player, player.getMainHandItem());
			if (!smCraft.notNullRecipe(world, stackList)) { return; }

			RecipeViewEvent.recipe = smCraft.getItemList(stackList, smCraft.getRecipe(world, stackList));
			RecipeViewEvent.canShiftCraft = smCraft.canShiftCraft();
		}
	}

	public static float getProgress(int renderTick, int maxTime) {
		return Math.min(1F, (float) renderTick / maxTime);
	}
}
