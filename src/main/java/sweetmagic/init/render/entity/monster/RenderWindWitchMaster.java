package sweetmagic.init.render.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.boss.WindWitchMaster;
import sweetmagic.init.render.entity.layer.WitchMasterWandLayer;
import sweetmagic.init.render.entity.model.WindWitchModel;

public class RenderWindWitchMaster extends MobRenderer<WindWitchMaster, WindWitchModel<WindWitchMaster>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/windwitch_master.png");

	public RenderWindWitchMaster(EntityRendererProvider.Context con) {
		super(con, new WindWitchModel<WindWitchMaster>(con.bakeLayer(WindWitchModel.LAYER)), 0.5F);
		this.addLayer(new WitchMasterWandLayer<WindWitchMaster, WindWitchModel<WindWitchMaster>>(this, con));
	}

	protected void scale(WindWitchMaster entity, PoseStack pose, float par1) {
		pose.scale(1.15F, 1.15F, 1.15F);
	}

	public ResourceLocation getTextureLocation(WindWitchMaster entity) {
		return TEX;
	}
}
