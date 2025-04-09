package sweetmagic.init.render.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.DwarfZombieMaster;
import sweetmagic.init.render.entity.model.SMZombieModel;

public class RenderDwarfZombieMaster<T extends DwarfZombieMaster> extends MobRenderer<T, SMZombieModel<T>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/dwarfzombie_master.png");

	public RenderDwarfZombieMaster(EntityRendererProvider.Context con) {
		super(con, new SMZombieModel<>(con.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
		this.addLayer(new ItemInHandLayer<>(this, con.getItemInHandRenderer()));
	}

	protected void scale(T entity, PoseStack pose, float par1) {
		pose.scale(1.25F, 1.25F, 1.25F);
	}

	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}
}
