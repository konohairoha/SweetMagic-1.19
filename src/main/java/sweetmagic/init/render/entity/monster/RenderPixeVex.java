package sweetmagic.init.render.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.PixeVex;
import sweetmagic.init.render.entity.model.AncientFairyModel;

public class RenderPixeVex extends HumanoidMobRenderer<PixeVex, AncientFairyModel<PixeVex>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/pixevex.png");

	public RenderPixeVex(EntityRendererProvider.Context con) {
		super(con, new AncientFairyModel<PixeVex>(con.bakeLayer(AncientFairyModel.LAYER)), 0.3F);
	}

	public ResourceLocation getTextureLocation(PixeVex entity) {
		return TEX;
	}

	protected void scale(PixeVex entity, PoseStack pose, float par1) {
		pose.scale(0.65F, 0.65F, 0.65F);
	}
}
