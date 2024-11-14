package sweetmagic.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.emagic.SMMagicType;
import sweetmagic.api.event.SMUtilEvent;
import sweetmagic.api.iitem.IMagicItem;
import sweetmagic.api.iitem.IWand;
import sweetmagic.api.iitem.info.MagicInfo;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.item.magic.MFTeleport;

@OnlyIn(Dist.CLIENT)
public class WandRenderEvent extends SMUtilEvent {

	// GUIの取得
	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_usergage.png");
	private static final ResourceLocation SCOPE = SweetMagicCore.getSRC("textures/gui/spyglass_scope.png");
	private static int tickTime = 0;

	// レンダーイベントの呼び出し
	@SubscribeEvent
	public static void onWandRenderEvent(RenderGuiOverlayEvent.Post event) {
		if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) { return; }

		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (player.isSpectator()) { return; }

		// ItemStackを取得
		ItemStack stack = player.getMainHandItem();
		if (stack.isEmpty() || !(stack.getItem() instanceof IWand wand)) { return; }

		Level world = mc.level;
		PoseStack pose = event.getPoseStack();
		Matrix4f mat = pose.last().pose();
		int height = event.getWindow().getGuiScaledHeight();	// 画面サイズの高さを取得
		int weight = event.getWindow().getGuiScaledWidth();		// 画面サイズの高さを取得

		// 選択している魔法の取得
		WandInfo wandInfo = new WandInfo(stack);
		ItemStack slotStack = wand.getSlotItem(player, wandInfo);

		// レンダーの開始
		renderStart(TEX);
		boolean isSneak = player.isShiftKeyDown();
		boolean isLeftSide = true;

		// 前後のスロットアイテムを初期化
		ItemStack nextStack = wand.getNextStack(world, player, stack);
		ItemStack backStack = wand.getBackStack(world, player, stack);

		// アニメーションの時間を取得
		tickTime = isSneak ? Math.min(20, ++tickTime) : Math.max(0, --tickTime);

		// GUIを描画
		renderGUI(mat, height, weight, isLeftSide, isSneak);

		// GUIのゲージを描画
		int progress = wand.getMFProgressScaled(stack, 76);
		renderMFProgress(mat, height, weight, isLeftSide, progress);

		// GUIのゲージを描画
		int expProgress = wand.isCreativeWand() ? 76 : wand.getExpProgressScaled(stack, 76);
		renderEXPProgress(mat, height, weight, isLeftSide, expProgress);

		// 選択してるスロットが空なら終了
		if (!slotStack.isEmpty()) {

			int addY = (int) (( isSneak ? -40 : Math.min(-40, tickTime) ) * getProgress(10));

			// 選択中のスロットのアイテムを描画
			renderSlotItem(mat, player, height, weight, new MagicInfo(slotStack), isLeftSide, addY);

			// GUIのゲージを描画
			if (slotStack.getItem() instanceof IMagicItem magic && magic.getMagicType() == SMMagicType.CHARGE) {
				renderChargeProgress(mat, height, weight, isLeftSide, player);
			}
		}

		if ( ( isSneak || !isSneak ) && tickTime >= 10 ) {

			RenderSystem.setShaderColor(1F, 1F, 1F, getProgress(20));
			if (!nextStack.isEmpty()) {

				RenderSystem.setShaderTexture(0, TEX);
				drawTextured(mat, 11, height - 131, 41, 69, 41, 40);
				drawTextured(mat, 26, height - 95, 20, 121, 11, 12);

				// 選択中のスロットのアイテムを描画
				renderSlotItem(mat, player, height, weight, new MagicInfo(nextStack), isLeftSide, -84);
			}

			if (!backStack.isEmpty()) {

				RenderSystem.setShaderTexture(0, TEX);
				drawTextured(mat, 11, height - 43, 41, 69, 41, 40);
				drawTextured(mat, 26, height - 51, 5, 121, 11, 12);

				// 選択中のスロットのアイテムを描画
				renderSlotItem(mat, player, height, weight, new MagicInfo(backStack), isLeftSide, 4);
			}
		}

		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

		// 杖のMFをテキスト描画
		renderMFText(mc, pose, height, weight, wandInfo, isLeftSide);

		// 選択してるスロットが空なら終了
		if (!slotStack.isEmpty() && slotStack.getItem() instanceof MFTeleport) {
			renderMagicText(mc, pose, height, weight, slotStack, isLeftSide);
		}

		RenderSystem.disableBlend();

		if (wand.isScope() && mc.options.getCameraType().isFirstPerson() && player.isShiftKeyDown()) {

			RenderSystem.disableDepthTest();
			RenderSystem.depthMask(false);
			RenderSystem.defaultBlendFunc();
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, SCOPE);
			Tesselator tes = Tesselator.getInstance();
			BufferBuilder buf = tes.getBuilder();
			float f = (float) Math.min(weight, height);
			float f1 = Math.min((float) weight / f, (float) height / f) * 0.8F;
			float f2 = f * f1;
			float f3 = f * f1;
			float f4 = ((float) weight - f2) / 2F;
			float f5 = ((float) height - f3) / 2F;
			float f6 = f4 + f2;
			float f7 = f5 + f3;
			buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
			buf.vertex((double) f4, (double) f7, -90D).uv(0F, 1F).endVertex();
			buf.vertex((double) f6, (double) f7, -90D).uv(1F, 1F).endVertex();
			buf.vertex((double) f6, (double) f5, -90D).uv(1F, 0F).endVertex();
			buf.vertex((double) f4, (double) f5, -90D).uv(0F, 0F).endVertex();
			tes.end();
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			RenderSystem.disableTexture();
			RenderSystem.enableTexture();
			RenderSystem.depthMask(true);
			RenderSystem.enableDepthTest();
			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		}
	}

	// GUIを描画
	public static void renderGUI (Matrix4f mat, int height, int weight, boolean isLeftSide, boolean isSneak) {
		int addY = (int) ((isSneak ? 40 : -Math.min(-40, tickTime)) * getProgress(10));
		int posX = isLeftSide ? 10 : weight - 247;
		drawTextured(mat, posX, height - 47 - addY, 0, 0, 41, 40);
		drawTextured(mat, posX + 44, height - 38, 44, 13, 77, 36);
	}

	// GUIのゲージを描画
	public static void renderMFProgress (Matrix4f mat, int height, int weight, boolean isLeftSide, int progress) {
		int posX = isLeftSide ? 54 : weight - 202;
		drawTextured(mat, posX, height - 25, 0, 50, progress, 14);
	}

	// GUIのゲージを描画
	public static void renderEXPProgress (Matrix4f mat, int height, int weight, boolean isLeftSide, int progress) {
		int posX = isLeftSide ? 54 : weight - 202;
		drawTextured(mat, posX, height - 11, 0, 64, progress, 4);
	}

	// GUIのゲージを描画
	public static void renderChargeProgress (Matrix4f mat, int height, int weight, boolean isLeftSide, Player player) {

		RenderSystem.setShaderTexture(0, TEX);

		int posX = isLeftSide ? 130 : weight - 202;
		drawTextured(mat, posX, height - 22, 120, 29, 5, 15);
		int remainingTick = player.getUseItemRemainingTicks();

		if (remainingTick != 0) {
			int progress = Math.min(15, (int) (15 * (20F - ( remainingTick - 71980F )  ) / 20F ) );
			drawTextured(mat, posX, height - 22 + 15 - progress, 76 + (progress == 15 ? 8 : 0), 53 + (progress == 15 ? 0 : 15 - progress), 5, progress);
		}
	}

	// 選択中のスロットのアイテムを描画
	public static void renderSlotItem(Matrix4f mat, Player player, int height, int weight, MagicInfo magicInfo, boolean isLeftSide, int addY) {

		// テクスチャのサイズ
		int texSize = 32;
		ItemStack magicStack = magicInfo.getStack();
		IMagicItem smItem = magicInfo.getMagicItem();
		RenderSystem.setShaderTexture(0, smItem.getResource());
		int posX = isLeftSide ? 15 : weight - 242;
		drawTextured(mat, posX, height - 43 + addY, 0, 0, texSize, texSize, texSize);
		RenderSystem.setShaderTexture(0, TEX);

		// 消費アイテム以外ならフレーム表示
		if (!smItem.isShirink()) {

			// tierの取得
			int tier = smItem.getTier();
			String name = "";

			switch (tier) {
			case 1:
				name = "frame_1";
				break;
			case 2:
				name = "frame_2";
				break;
			case 3:
				name = "frame_3";
				break;
			case 4:
				name = "frame_4";
				break;
			case 5:
				name = "frame_5";
				break;
			}

			// テクスチャの指定
			if (!name.equals("")) {
				RenderSystem.setShaderTexture(0, SweetMagicCore.getSRC("textures/item/" + name + ".png"));
				drawTextured(mat, posX, height - 43 + addY, 0, 0, texSize, texSize, 32F);
			}
		}

		// クールタイムを持っていたら
		if (!smItem.isNoRecast(magicStack)) {

			RenderSystem.setShaderTexture(0, TEX);
			posX = isLeftSide ? 15 : weight - 242;
			int x = smItem.isImmedFlag(magicStack) ? 88 : 5;

			// ゲージの計算
			int progress = (int) (32 * ( (float) smItem.getRecastTime(magicStack) / (float) smItem.getMaxRecastTime()));
			drawTextured(mat, posX, height - 11 - progress + addY, x, 73, 32, progress);
		}
	}

	// 杖のMFをテキスト描画
	public static void renderMFText (Minecraft mc, PoseStack pose, int height, int weight, WandInfo wandInfo, boolean isLeftSide) {
		Font font = mc.font;
		String text = String.format("%,d", wandInfo.getWand().getMF(wandInfo.getStack())) + "MF";
		int posX = isLeftSide ? 72 : weight - 192;
		font.drawShadow(pose, text, posX, height - 33, 0xffffff);
	}

	// 杖の魔法名をテキスト描画
	public static void renderMagicText (Minecraft mc, PoseStack pose, int height, int weight, ItemStack slotItem, boolean isLeftSide) {
		Font font = mc.font;
		int posX = isLeftSide ? 65 : weight - 192;
		font.drawShadow(pose, slotItem.getHoverName(), posX, height - 44, 0xffffff);
	}

	public static float getProgress (int maxTime) {
		return Math.min(1F, (float) tickTime / maxTime);
	}

	@SubscribeEvent
	public static void onFOVEvent (ComputeFovModifierEvent event) {
		Player player = event.getPlayer();
		Minecraft mc = Minecraft.getInstance();
		if (!mc.options.getCameraType().isFirstPerson() || !player.isShiftKeyDown()) { return; }

		ItemStack stack = player.getMainHandItem();
		if (stack.isEmpty() || !(stack.getItem() instanceof IWand wand) || !wand.isScope()) { return; }

		event.setNewFovModifier(event.getFovModifier() / 4F);
	}
}
