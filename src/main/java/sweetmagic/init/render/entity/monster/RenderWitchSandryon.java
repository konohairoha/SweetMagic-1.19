package sweetmagic.init.render.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.boss.WitchSandryon;
import sweetmagic.init.render.entity.layer.WitchSandryonLayer;
import sweetmagic.init.render.entity.model.WindWitchModel;

public class RenderWitchSandryon<T extends WitchSandryon> extends MobRenderer<T, WindWitchModel<T>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/witchsandryon.png");

	public RenderWitchSandryon(EntityRendererProvider.Context con) {
		super(con, new WindWitchModel<T>(con.bakeLayer(WindWitchModel.LAYER)), 0.5F);
		this.addLayer(new WitchSandryonLayer<T, WindWitchModel<T>>(this, con));
	}

	protected void scale(T entity, PoseStack pose, float par1) {
		pose.scale(1.15F, 1.15F, 1.15F);

		if (entity.getWandCharge() || entity.getInfiniteWand()) {
			pose.translate(0F, -0.05F + Math.sin(entity.tickCount / 10F) * 0.05F, 0F);
		}
	}

	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}
}
