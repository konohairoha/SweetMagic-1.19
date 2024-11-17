package sweetmagic.init.render.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.monster.BlazeTempestTornado;
import sweetmagic.init.render.entity.layer.TempestTornadoLayer;
import sweetmagic.init.render.entity.model.TempestModel;

public class RenderBlazeTempestTornado extends MobRenderer<BlazeTempestTornado, TempestModel<BlazeTempestTornado>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/blazetempest_tornado.png");

	public RenderBlazeTempestTornado(EntityRendererProvider.Context con) {
		super(con, new TempestModel<>(con.bakeLayer(TempestModel.LAYER)), 0.5F);
		this.addLayer(new TempestTornadoLayer<BlazeTempestTornado, TempestModel<BlazeTempestTornado>>(this, con));
	}

	protected int getBlockLightLevel(BlazeTempestTornado entity, BlockPos pos) {
		return 15;
	}

	protected void scale(BlazeTempestTornado entity, PoseStack pose, float par1) {
		float size = this.getSize(entity, 1.5F);
		pose.scale(size, size, size);
	}

	public float getSize (Mob mob, float size) {
		return mob.hasEffect(PotionInit.leader_flag) ? size + 0.35F : size;
	}

	public ResourceLocation getTextureLocation(BlazeTempestTornado entity) {
		return TEX;
	}
}
