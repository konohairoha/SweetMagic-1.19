package sweetmagic.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.event.SMUtilEvent;
import sweetmagic.handler.PacketHandler;
import sweetmagic.init.BlockInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.item.magic.StartLightWand;
import sweetmagic.packet.StartLightWandPKT;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;

@Mod.EventBusSubscriber(modid = SweetMagicCore.MODID, value = Dist.CLIENT)
public class StarLightWandEvent extends SMUtilEvent {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_misc.png");

	@SubscribeEvent
	public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
		Player player = event.getEntity();
		ItemStack stack = player.getMainHandItem();
		if (!(stack.getItem() instanceof StartLightWand wand)) { return; }

		event.setCanceled(true);

		if (player.isShiftKeyDown()) {
			PacketHandler.sendToServer(new StartLightWandPKT(0, event.getPos()));
		}

		else {
			PacketHandler.sendToServer(new StartLightWandPKT(1, event.getPos()));
			player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1F, 1F);
		}
	}

	@SubscribeEvent
	public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
		Player player = event.getEntity();
		if (!player.isShiftKeyDown()) { return; }

		ItemStack stack = player.getMainHandItem();
		if (!(stack.getItem() instanceof StartLightWand wand)) { return; }

		PacketHandler.sendToServer(new StartLightWandPKT(0, event.getPos()));
		event.setCanceled(true);
	}

	@SubscribeEvent
	public static void renderLevelLastEvent(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) { return; }

		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (player == null) { return; }

		ItemStack stack = player.getMainHandItem();
		if (stack.isEmpty() || !stack.is(ItemInit.startlight_wand)) { return; }

		CompoundTag tags = stack.getTag();
		if (tags == null || (!tags.contains("startX") && !tags.contains("endX")) || !tags.contains("blockId")) { return; }

		Level world = player.level;
		BlockState state = NbtUtils.readBlockState(tags.getCompound("state"));
		BlockPos startPos = tags.contains("startX") ? new BlockPos(tags.getInt("startX"), tags.getInt("startY"), tags.getInt("startZ")) : new BlockPos(tags.getInt("endX"), tags.getInt("endY"), tags.getInt("endZ"));
		BlockPos endPos = tags.contains("endX") ? new BlockPos(tags.getInt("endX"), tags.getInt("endY"), tags.getInt("endZ")) : new BlockPos(tags.getInt("startX"), tags.getInt("startY"), tags.getInt("startZ"));
		Iterable<BlockPos> posList = BlockPos.betweenClosed(startPos, endPos);

		PoseStack pose = event.getPoseStack();
		MultiBufferSource.BufferSource buf = mc.renderBuffers().bufferSource();
		Vec3 base = mc.gameRenderer.getMainCamera().getPosition();
		int count = 0;
		boolean isExchange = tags.getBoolean("isExchange");
		boolean isFull = tags.getBoolean("isFull");

		for (BlockPos pos : posList) {

			if (!isExchange && !world.getBlockState(pos).isAir()) { continue; }

			Vec3 renderPos = base.subtract(pos.getX(), pos.getY(), pos.getZ()).add(.005F, .005F, .005F);
			pose.pushPose();
			pose.translate(-renderPos.x(), -renderPos.y(), -renderPos.z());
			pose.scale(1.01F, 1.01F, 1.01F);

			if (isFull) {
				RenderUtil.renderTransBlock(pose, buf, RenderColor.create(1000), state, 0.65F);
			}

			else {
				RenderUtil.renderTransBlock(pose, buf, RenderColor.create(1000), state);
			}

			pose.popPose();
			if (count++ > 10000) { break; }
		}

		RenderSystem.disableDepthTest();
		buf.endBatch();
	}

	// レンダーイベントの呼び出し
	@SubscribeEvent
	public static void onWandRenderEvent(RenderGuiOverlayEvent.Post event) {
		if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) { return; }

		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (player.isSpectator()) { return; }

		// ItemStackを取得
		ItemStack stack = player.getMainHandItem();
		if (stack.isEmpty() || !(stack.getItem() instanceof StartLightWand wand)) { return; }

		PoseStack pose = event.getPoseStack();
		Matrix4f mat = pose.last().pose();
		int height = event.getWindow().getGuiScaledHeight();	// 画面サイズの高さを取得

		// レンダーの開始
		renderStart(TEX);
		drawTextured(mat, 8, height - 133, 114, 47, 32, 32);

		CompoundTag tags = stack.getOrCreateTag();
		Block block = null;

		if (tags.contains("blockId")) {
			block = wand.getBlock(tags);
			ItemRenderer render = mc.getItemRenderer();
			render.renderAndDecorateFakeItem(new ItemStack(block), 9, height - 132);
		}

		Font font = mc.font;
		String blockName = tags.contains("blockId") ? block.getName().getString() : getText("unregistered").getString();
		font.drawShadow(pose, getTipArray(getText("set_block").withStyle(GREEN), getTip(blockName).withStyle(WHITE)), 9, height - 110, 0xffffff);

		String start = tags.contains("startX") ? tags.getInt("startX") + ", " + tags.getInt("startY") + ", " + tags.getInt("startZ") : getText("unregistered").getString();
		font.drawShadow(pose, getTipArray(getText("start_pos").withStyle(GREEN), getTip(start).withStyle(WHITE)), 9, height - 99, 0xffffff);

		String end = tags.contains("endX") ? tags.getInt("endX") + ", " + tags.getInt("endY") + ", " + tags.getInt("endZ") : getText("unregistered").getString();
		font.drawShadow(pose, getTipArray(getText("end_pos").withStyle(GREEN), getTip(end).withStyle(WHITE)), 9, height - 88, 0xffffff);

		font.drawShadow(pose, getTipArray(getText("star_mode").withStyle(GREEN), getText(tags.getBoolean("isExchange") ? "exchange_mode" : "set_mode").withStyle(WHITE)), 9, height - 77, 0xffffff);

		int type = 3;

		if (!tags.contains("startX")) {
			type = 0;
		}

		else if (!tags.contains("endX")) {
			type = 1;
		}

		else if (!tags.contains("blockId")) {
			type = 2;
		}

		font.drawShadow(pose, getText("startlight_wand_operation." + type).withStyle(GOLD), 9, height - 66, 0xffffff);
	}

	@SubscribeEvent
	public static void renderTargetBlock(RenderHighlightEvent.Block event) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		ItemStack stack = player.getMainHandItem();
		if (stack.isEmpty() || !(stack.getItem() instanceof StartLightWand wand)) { return; }

		PoseStack pose = event.getPoseStack();
		MultiBufferSource.BufferSource buf = mc.renderBuffers().bufferSource();
		Vec3 base = mc.gameRenderer.getMainCamera().getPosition();

		BlockHitResult result = event.getTarget();
		BlockPos pos = wand.getPos(event.getTarget().getBlockPos(), result.getLocation(), result.getDirection());
		Vec3 renderPos = base.subtract(pos.getX(), pos.getY(), pos.getZ()).add(.005F, .005F, .005F);
		pose.pushPose();
		pose.translate(-renderPos.x(), -renderPos.y(), -renderPos.z());
		pose.scale(1.01F, 1.01F, 1.01F);
		RenderUtil.renderTransBlock(pose, buf, RenderColor.create(1000), BlockInit.select_block.defaultBlockState(), 0.8F);
		pose.popPose();
	}
}
