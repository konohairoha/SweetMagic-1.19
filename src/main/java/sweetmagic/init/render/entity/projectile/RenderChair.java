package sweetmagic.init.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class RenderChair<T extends Entity> extends EntityRenderer<T> {

	public RenderChair(EntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return null;
	}

	@Override
	protected void renderNameTag(T entity, Component com, PoseStack pose, MultiBufferSource buf, int light) { }
}
