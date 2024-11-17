package sweetmagic.init.render.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.boss.HolyAngel;
import sweetmagic.init.render.entity.layer.HolyAngelLayer;
import sweetmagic.init.render.entity.model.SMHolyModel;

public class RenderHolyAngel extends MobRenderer<HolyAngel, SMHolyModel<HolyAngel>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/holyshining.png");

	public RenderHolyAngel(EntityRendererProvider.Context con) {
		super(con, new SMHolyModel<HolyAngel>(con.bakeLayer(SMHolyModel.LAYER)), 0.5F);
		this.addLayer(new HolyAngelLayer<HolyAngel, SMHolyModel<HolyAngel>>(this, con));
	}

	protected void scale(HolyAngel entity, PoseStack pose, float par1) {
		pose.scale(1.25F, 1.25F, 1.25F);
		pose.translate(0F, -0.75F + Math.sin(entity.tickCount / 10F) * 0.175D, 0F);
	}

	public ResourceLocation getTextureLocation(HolyAngel entity) {
		return TEX;
	}
}