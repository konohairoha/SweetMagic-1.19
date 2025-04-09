package sweetmagic.init.render.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.boss.TwilightHora;
import sweetmagic.init.render.entity.model.HoraModel;

public class RenderHora<T extends TwilightHora> extends MobRenderer<T, HoraModel<T>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/hora.png");

	public RenderHora(EntityRendererProvider.Context con) {
		super(con, new HoraModel<>(con.bakeLayer(HoraModel.LAYER)), 0.5F);
	}

	protected void scale(T entity, PoseStack pose, float par1) {
		pose.scale(1.25F, 1.25F, 1.25F);
	}

	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}
}
