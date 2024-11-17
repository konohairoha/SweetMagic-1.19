package sweetmagic.init.render.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.WindWitch;
import sweetmagic.init.render.entity.layer.WitchWandLayer;
import sweetmagic.init.render.entity.model.SMWitchModel;

public class RenderWindWitch extends MobRenderer<WindWitch, SMWitchModel<WindWitch>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/windwitch.png");

	public RenderWindWitch(EntityRendererProvider.Context con) {
		super(con, new SMWitchModel<WindWitch>(con.bakeLayer(SMWitchModel.LAYER)), 0.5F);
		this.addLayer(new WitchWandLayer<WindWitch, SMWitchModel<WindWitch>>(this, con));
	}

	protected void scale(WindWitch entity, PoseStack pose, float par1) {
		pose.scale(0.75F, 0.75F, 0.75F);
	}

	public ResourceLocation getTextureLocation(WindWitch entity) {
		return TEX;
	}
}
