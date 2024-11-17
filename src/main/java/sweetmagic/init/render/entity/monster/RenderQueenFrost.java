package sweetmagic.init.render.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.boss.QueenFrost;
import sweetmagic.init.render.entity.layer.QueenFrostLayer;
import sweetmagic.init.render.entity.model.QuenModel;

public class RenderQueenFrost extends MobRenderer<QueenFrost, QuenModel<QueenFrost>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/queenfrost.png");

	public RenderQueenFrost(EntityRendererProvider.Context con) {
		super(con, new QuenModel<QueenFrost>(con.bakeLayer(QuenModel.LAYER)), 0.5F);
		this.addLayer(new QueenFrostLayer<QueenFrost, QuenModel<QueenFrost>>(this, con));
	}

	protected void scale(QueenFrost entity, PoseStack pose, float par1) {
		pose.scale(1.25F, 1.25F, 1.25F);
		pose.translate(0F, -0.75F + Math.sin(entity.tickCount / 10F) * 0.175D, 0F);
	}

	public ResourceLocation getTextureLocation(QueenFrost entity) {
		return TEX;
	}
}
