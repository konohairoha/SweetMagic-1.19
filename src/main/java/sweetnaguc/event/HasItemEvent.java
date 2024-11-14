package sweetmagic.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iblock.ITileMF;
import sweetmagic.api.iitem.IRangeTool;
import sweetmagic.api.iitem.IWand;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.BlockInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.ParticleInit;

@Mod.EventBusSubscriber(modid = SweetMagicCore.MODID, value = Dist.CLIENT)
public class HasItemEvent {

	private static int tickTime = 0;
	private static int renderTime = 0;
	public static boolean hasThisItem;
	private static List<Item> itemList = new ArrayList<>();

	@SubscribeEvent
	public static void renderfov(ComputeFovModifierEvent event) {

		renderTime++;
		if (renderTime % 10 == 0) {
			renderTime = 0;
			onMFMFlow(event);
		}

		tickTime++;
		if(tickTime % 16 != 0) { return; }

		tickTime = 0;
		Player player = event.getPlayer();
		ItemStack stack = player.getMainHandItem();

		if (stack.isEmpty()) {
			hasThisItem = false;
			return;
		}

		Item item = stack.getItem();

		// 杖の呼び出して選択中のアイテムを取得
		if (item instanceof IWand wand) {
			item = wand.getSlotItem(player, new WandInfo(stack)).getItem();
		}

		if (itemList.isEmpty()) {
			itemList = Arrays.<Item> asList(
				ItemInit.magic_light, Item.byBlock(BlockInit.magiclight)
			);
		}

		Level world = player.level;
		hasThisItem = itemList.contains(item);

		// パーティクル描画
		if (hasThisItem && world.isClientSide) {
			renderEffect(world, player);
		}
	}

	// 魔法の流れの描画
	public static void onMFMFlow(ComputeFovModifierEvent event) {

		// 魔法流の杖以外なら終了
		Player player = event.getPlayer();
		ItemStack stack = player.getMainHandItem();
		if (stack.isEmpty() || stack.getItem() != ItemInit.mf_stuff || !player.level.isClientSide) { return; }

		int area = 16;
		Level world = player.level;
		BlockPos pPos = player.getOnPos();
		ParticleOptions par = ParticleInit.MF.get();

		// 範囲の座標取得
		Iterable<BlockPos> pList = BlockPos.betweenClosed(pPos.offset(-area, -area, -area), pPos.offset(area, area, area));

		for (BlockPos pos : pList) {

			// 魔法の光以外なら次へ
			BlockEntity bEntity = world.getBlockEntity(pos);
			if (bEntity == null || !( bEntity instanceof ITileMF tile) ) { continue; }

			// 座標リストがないなら次へ
			Set<BlockPos> posList = tile.getPosList();
			if (posList.isEmpty()) { continue; }

			// 魔法の流れを描画
			renderMFFlow(world, pos, posList, par);
		}
	}

	// 魔法の光描画
	public static void renderEffect (Level world, Player player) {

		int area = 12;
		BlockPos pPos = player.getOnPos();
		ParticleOptions par = ParticleInit.MAGICLIGHT.get();

		// 範囲の座標取得
		Iterable<BlockPos> posList = BlockPos.betweenClosed(pPos.offset(-area, -area, -area), pPos.offset(area, area, area));

		for (BlockPos pos : posList) {

			// 魔法の光以外ならツ次へ
			if (world.getBlockState(pos).getBlock() != BlockInit.magiclight) { continue; }

			double d0 = pos.getX() + 0.5D;
			double d1 = pos.getY() + 0.6D;
			double d2 = pos.getZ() + 0.5D;
			world.addParticle(par, d0, d1, d2, 0, 0, 0);
		}
	}

	// 魔法の流れを描画
	public static void renderMFFlow (Level world, BlockPos basePos, Set<BlockPos> posList, ParticleOptions particle) {

		// スピードの宣言
		float speed = 0.0265F;

		for (BlockPos pos : posList) {

			if (!(world.getBlockEntity(pos) instanceof ITileMF)) { continue; }

			float x = pos.getX() + 0.5F;
			float y = pos.getY() + 0.5F;
			float z = pos.getZ() + 0.5F;
			float xSpeed = (basePos.getX() - pos.getX()) * speed;
			float ySpeed = (basePos.getY() - pos.getY()) * speed;
			float zSpeed = (basePos.getZ() - pos.getZ()) * speed;

			world.addParticle(particle, x, y, z, xSpeed, ySpeed, zSpeed);
		}
	}

	@SubscribeEvent
	public static void renderBreakeRange(RenderHighlightEvent.Block event) {

		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		ItemStack stack = player.getMainHandItem();
		if (player.isShiftKeyDown() || stack.isEmpty() || !(stack.getItem() instanceof IRangeTool tool) || tool.getRange() == 0) { return; }

		ClientLevel level = mc.level;
        Camera camera = mc.gameRenderer.getMainCamera();
        double posX = camera.getPosition().x;
        double posY = camera.getPosition().y;
        double posZ = camera.getPosition().z;
        Vec3 vec = event.getTarget().getLocation();
        BlockPos pos = new BlockPos(vec.x, vec.y, vec.z);

        BufferSource buffer = mc.renderBuffers().bufferSource();
        VertexConsumer con = buffer.getBuffer(RenderType.lineStrip());

		int area = 1;
		int rangeX, rangeY, rangeZ;	//向きに合わせて座標を変えるための変数
		rangeX = rangeY = rangeZ = area;

		switch (event.getTarget().getDirection()) {
        case UP:
        	rangeY = 0;
        	pos = pos.below();
        	break;
        case DOWN:
        	rangeY = 0;
        	break;
        case NORTH:
        	rangeZ = 0;
        	break;
        case SOUTH:
        	rangeZ = 0;
        	pos = pos.north();
        	break;
        case EAST:
        	rangeX = 0;
        	pos = pos.west();
        	break;
        case WEST:
        	rangeX = 0;
        	break;
		}

		// 範囲の座標取得
		Iterable<BlockPos> pList = BlockPos.betweenClosed(pos.offset(-rangeX, -rangeY, -rangeZ), pos.offset(rangeX, rangeY, rangeZ));
		for (BlockPos p : pList) {

			if (!tool.isAllBlock() && !stack.isCorrectToolForDrops(level.getBlockState(p)) || level.getBlockEntity(p) != null) { continue; }
	        VoxelShape voxel = level.getBlockState(p).getCollisionShape(level, p);
	        drawShape(event.getPoseStack(), con, voxel, -posX + p.getX(), -posY + p.getY(), -posZ + p.getZ());
		}
	}

    private static void drawShape(PoseStack pose, VertexConsumer con, VoxelShape voxel, double x, double y, double z) {
        Matrix4f mat4 = pose.last().pose();
		Matrix3f mat3 = pose.last().normal();
		voxel.forAllEdges((x1, y1, z1, x2, y2, z2) -> {
            con.vertex(mat4, (float) (x1 + x), (float) (y1 + y), (float) (z1 + z)).color(0.13F, 0.13F, 0.13F, 1F).normal(mat3, 3F, 3F, 3F).endVertex();
            con.vertex(mat4, (float) (x2 + x), (float) (y2 + y), (float) (z2 + z)).color(0.13F, 0.13F, 0.13F, 1F).normal(mat3, 3F, 3F, 3F).endVertex();
        });
    }
}
