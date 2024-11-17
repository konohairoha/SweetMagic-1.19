package sweetmagic.init.render.entity.monster;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.DwarfZombie;
import sweetmagic.init.render.entity.model.SMZombieModel;

public class RenderDwarfZombie extends MobRenderer<DwarfZombie, SMZombieModel<DwarfZombie>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/dwarfzombie.png");

	public RenderDwarfZombie(EntityRendererProvider.Context con) {
		super(con, new SMZombieModel<DwarfZombie>(con.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
		this.addLayer(new ItemInHandLayer<>(this, con.getItemInHandRenderer()));
	}

	public ResourceLocation getTextureLocation(DwarfZombie entity) {
		return TEX;
	}
}