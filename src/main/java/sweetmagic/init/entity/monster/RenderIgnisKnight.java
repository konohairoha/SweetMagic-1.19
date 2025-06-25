package sweetmagic.init.render.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.boss.IgnisKnight;
import sweetmagic.init.render.entity.layer.IgnisKnightLayer;
import sweetmagic.init.render.entity.model.IgnisModel;

public class RenderIgnisKnight<T extends IgnisKnight> extends MobRenderer<T, IgnisModel<T>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/ignis_knight.png");

	public RenderIgnisKnight(EntityRendererProvider.Context con) {
		super(con, new IgnisModel<>(con.bakeLayer(IgnisModel.LAYER)), 0.5F);
		this.addLayer(new IgnisKnightLayer<>(this, con));
	}

	protected void scale(T entity, PoseStack pose, float par1) {
		pose.scale(1.25F, 1.25F, 1.25F);
	}

	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}
}
