package sweetmagic.init.render.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.PhantomWolf;
import sweetmagic.init.render.entity.model.WitchWolfModel;

public class RenderPhantomWolf extends MobRenderer<PhantomWolf, WitchWolfModel<PhantomWolf>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/phantom_wolf.png");

	public RenderPhantomWolf(EntityRendererProvider.Context con) {
		super(con, new WitchWolfModel<>(con.bakeLayer(WitchWolfModel.LAYER)), 0.5F);
	}
	protected void scale(PhantomWolf entity, PoseStack pose, float par1) {
		pose.scale(2F, 2F, 2F);
	}

	public ResourceLocation getTextureLocation(PhantomWolf entity) {
		return TEX;
	}
}
