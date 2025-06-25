package sweetmagic.init.render.entity.monster;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.boss.DemonsBelial;
import sweetmagic.init.render.entity.layer.DemonsBelialHeartLayer;
import sweetmagic.init.render.entity.layer.DemonsBelialLayer;
import sweetmagic.init.render.entity.model.DemonsBelialModel;

public class RenderDemonsBelial<T extends DemonsBelial> extends MobRenderer<T, DemonsBelialModel<T>> {
	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/demons_belial.png");
	private static final ResourceLocation HEART = SweetMagicCore.getSRC("textures/entity/demons_belial_heart.png");

	public RenderDemonsBelial(EntityRendererProvider.Context con) {
		super(con, new DemonsBelialModel<>(con.bakeLayer(DemonsBelialModel.LAYER)), 0.9F);
		this.addLayer(new DemonsBelialLayer<>(this, con));
		this.addLayer(new DemonsBelialHeartLayer<>(this, HEART, TEX, (entity, patTick, par1) -> {
			return entity.getHeartAnimation(patTick);
		}, DemonsBelialModel::getHeartPartList, DemonsBelialModel::getRootPartList));
	}

	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}
}
