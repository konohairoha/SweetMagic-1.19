package sweetmagic.init.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.util.RenderUtil;

public abstract class RenderMagicBase<T extends AbstractMagicShot> extends EntityRenderer<T> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/block/empty.png");
	protected final EntityRenderDispatcher eRender;
	protected final BlockRenderDispatcher bRender;
	protected final ItemRenderer iRender;

	public RenderMagicBase(EntityRendererProvider.Context con) {
		super(con);
		this.eRender = con.getEntityRenderDispatcher();
		this.bRender = con.getBlockRenderDispatcher();
		this.iRender = con.getItemRenderer();
	}

	public void renderItem(PoseStack pose, MultiBufferSource buf, int light, ItemStack stack) {
		this.iRender.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buf, 0);
	}

	public void renderBlock(T entity, PoseStack pose, MultiBufferSource buf, BlockState block) {
		RenderUtil.renderBlock(entity.getLevel(), entity.blockPosition(), block, this.bRender, pose, buf, OverlayTexture.NO_OVERLAY);
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}
}
