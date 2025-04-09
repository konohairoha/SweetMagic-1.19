package sweetmagic.init.render.entity.monster;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.boss.BullFight;
import sweetmagic.init.render.entity.model.BullfightModel;

public class RenderBullfight extends MobRenderer<BullFight, BullfightModel> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/bullfight.png");

	public RenderBullfight(EntityRendererProvider.Context con) {
		super(con, new BullfightModel(con.bakeLayer(BullfightModel.LAYER)), 1.1F);
	}

	public ResourceLocation getTextureLocation(BullFight entity) {
		return TEX;
	}
}
