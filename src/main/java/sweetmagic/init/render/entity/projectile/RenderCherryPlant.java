package sweetmagic.init.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.api.iblock.ISMCrop;
import sweetmagic.init.BlockInit;
import sweetmagic.init.entity.monster.CherryPlant;
import sweetmagic.util.RenderUtil;

public class RenderCherryPlant<T extends CherryPlant> extends EntityRenderer<T> {

	private static final Block PLANT = BlockInit.cherry_plant;
	private final BlockRenderDispatcher render;

	public RenderCherryPlant(EntityRendererProvider.Context con) {
		super(con);
		this.render = con.getBlockRenderDispatcher();
	}

	public ResourceLocation getTextureLocation(T entity) {
		return TextureAtlas.LOCATION_BLOCKS;
	}

	public void render(T entity, float parTick, float par2, PoseStack pose, MultiBufferSource buf, int light) {
		pose.pushPose();
		pose.translate(-0.5D, 0D, -0.5D);

		BlockState state = PLANT.defaultBlockState();
		state = state.setValue(ISMCrop.AGE3, entity.getStage());

		ModelBlockRenderer.enableCaching();
		RenderUtil.renderBlock(entity.level, entity.blockPosition(), state, this.render, pose, buf, OverlayTexture.NO_OVERLAY);
		ModelBlockRenderer.clearCache();
		pose.popPose();
	}
}
