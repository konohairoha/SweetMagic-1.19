package sweetmagic.init.render.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.boss.StellaWizardMaster;
import sweetmagic.init.render.entity.layer.StellaMasterLayer;
import sweetmagic.init.render.entity.model.StellaWizardModel;

public class RenderStellaWizardMaster<T extends StellaWizardMaster> extends MobRenderer<T, StellaWizardModel<T>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/stella_wizard_master.png");

	public RenderStellaWizardMaster(EntityRendererProvider.Context con) {
		super(con, new StellaWizardModel<T>(con.bakeLayer(StellaWizardModel.LAYER)), 0.5F);
		this.addLayer(new StellaMasterLayer<T, StellaWizardModel<T>>(this, con));
	}

	protected void scale(T entity, PoseStack pose, float par1) {
		pose.scale(1.25F, 1.25F, 1.25F);
	}

	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}
}
