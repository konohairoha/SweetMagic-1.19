package sweetmagic.init.render.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.boss.BraveSkeleton;
import sweetmagic.init.render.entity.layer.BraveSkeletonLayer;
import sweetmagic.init.render.entity.model.BraveSkeletonModel;

public class RenderBraveSkeleton<T extends BraveSkeleton> extends MobRenderer<T, BraveSkeletonModel<T>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/brave_skeleton.png");

	public RenderBraveSkeleton(EntityRendererProvider.Context con) {
		super(con, new BraveSkeletonModel<>(con.bakeLayer(BraveSkeletonModel.LAYER)), 1.1F);
		this.addLayer(new BraveSkeletonLayer<>(this, con));
	}

	protected void scale(T entity, PoseStack pose, float par1) {
		pose.scale(1.5F, 1.5F, 1.5F);
	}

	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}
}
