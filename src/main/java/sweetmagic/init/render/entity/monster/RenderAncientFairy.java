package sweetmagic.init.render.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.boss.AncientFairy;
import sweetmagic.init.render.entity.model.AncientFairyModel;

public class RenderAncientFairy<T extends AncientFairy> extends HumanoidMobRenderer<T, AncientFairyModel<T>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/ancientfairy.png");

	public RenderAncientFairy(EntityRendererProvider.Context con) {
		super(con, new AncientFairyModel<>(con.bakeLayer(AncientFairyModel.LAYER)), 0.3F);
	}

	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}

	protected void scale(T entity, PoseStack pose, float par1) {
		pose.scale(1.75F, 1.75F, 1.75F);
	}
}
