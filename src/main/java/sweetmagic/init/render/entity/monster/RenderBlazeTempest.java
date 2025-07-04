package sweetmagic.init.render.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.BlazeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.monster.BlazeTempest;
import sweetmagic.init.render.entity.layer.TempestLayer;

public class RenderBlazeTempest<T extends BlazeTempest> extends MobRenderer<T, BlazeModel<T>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/blazetempest.png");

	public RenderBlazeTempest(EntityRendererProvider.Context con) {
		super(con, new BlazeModel<>(con.bakeLayer(ModelLayers.BLAZE)), 0.5F);
		this.addLayer(new TempestLayer<>(this, con));
	}

	protected int getBlockLightLevel(T entity, BlockPos pos) {
		return 15;
	}

	protected void scale(T entity, PoseStack pose, float par1) {
		float size = this.getSize(entity, 1F);
		pose.scale(size, size, size);
	}

	public float getSize(Mob mob, float size) {
		return mob.hasEffect(PotionInit.leader_flag) ? size + 0.35F : size;
	}

	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}
}
