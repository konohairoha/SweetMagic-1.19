package sweetmagic.init.render.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.boss.Arlaune;
import sweetmagic.init.render.entity.layer.ArlauneLayer;
import sweetmagic.init.render.entity.model.ArlauneModel;

public class RenderArlaune<T extends Arlaune> extends MobRenderer<T, ArlauneModel<T>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/arlaune.png");

	public RenderArlaune(EntityRendererProvider.Context con) {
		super(con, new ArlauneModel<>(con.bakeLayer(ArlauneModel.LAYER)), 0.5F);
		this.addLayer(new ArlauneLayer<>(this, con));
	}

	protected void scale(Arlaune entity, PoseStack pose, float par1) {
		pose.scale(1.25F, 1.25F, 1.25F);
	}

	public ResourceLocation getTextureLocation(Arlaune entity) {
		return TEX;
	}
}
