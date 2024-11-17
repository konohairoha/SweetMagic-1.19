package sweetmagic.init.render.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.monster.ArchSpider;
import sweetmagic.init.render.entity.layer.ArkPowerLayer;

public class RenderArchSpider extends SpiderRenderer<ArchSpider> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/archspider.png");

	public RenderArchSpider(EntityRendererProvider.Context con) {
		super(con);
		this.shadowRadius *= 1.3F;
		this.addLayer(new ArkPowerLayer<ArchSpider, SpiderModel<ArchSpider>>(this, con));
	}

	protected void scale(ArchSpider entity, PoseStack pose, float par1) {
		float size = this.getSize(entity, 1.3F);
		pose.scale(size, size, size);
	}

	public float getSize (Mob mob, float size) {
		return mob.hasEffect(PotionInit.leader_flag) ? size + 0.35F : size;
	}

	public ResourceLocation getTextureLocation(ArchSpider entity) {
		return TEX;
	}
}
