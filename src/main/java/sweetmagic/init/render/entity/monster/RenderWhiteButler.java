package sweetmagic.init.render.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.boss.WhiteButler;
import sweetmagic.init.render.entity.layer.WhiteButlerLayer;
import sweetmagic.init.render.entity.model.WhiteButlerModel;

public class RenderWhiteButler<T extends WhiteButler> extends MobRenderer<T, WhiteButlerModel<T>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/white_butler.png");

	public RenderWhiteButler(EntityRendererProvider.Context con) {
		super(con, new WhiteButlerModel<>(con.bakeLayer(WhiteButlerModel.LAYER)), 0.5F);
		this.addLayer(new WhiteButlerLayer<>(this, con));
	}

	protected void scale(T entity, PoseStack pose, float par1) {
		pose.scale(1.25F, 1.25F, 1.25F);
	}

	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}
}
