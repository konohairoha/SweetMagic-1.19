package sweetmagic.init.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import sweetmagic.init.entity.projectile.AbstractBossMagic;

public class RenderBossMagic<T extends AbstractBossMagic> extends EntityRenderer<T> {

	private final EntityRenderDispatcher render;

	public RenderBossMagic(EntityRendererProvider.Context con) {
		super(con);
		this.render = con.getEntityRenderDispatcher();
	}

	public ResourceLocation getTextureLocation(T entity) {
		return TextureAtlas.LOCATION_BLOCKS;
	}

	public void render(T entity, float parTick, float par2, PoseStack pose, MultiBufferSource buf, int light) {
		LivingEntity summon = entity.getEntity();
		summon.tickCount++;
		pose.scale(1F, 1F, 1F);
		pose.mulPose(Vector3f.YP.rotationDegrees(entity.getRotData()));
		this.render.render(summon, 0D, -0.5D, 0D, 0F, parTick, pose, buf, light);
	}
}
