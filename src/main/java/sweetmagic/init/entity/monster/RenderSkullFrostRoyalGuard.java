package sweetmagic.init.render.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.PotionInit;
import sweetmagic.init.render.entity.layer.RoyalGuardLayer;

public class RenderSkullFrostRoyalGuard extends SkeletonRenderer {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/skullflost_royalguard.png");

	public RenderSkullFrostRoyalGuard(EntityRendererProvider.Context con) {
		super(con);
		this.addLayer(new RoyalGuardLayer<>(this, con));
	}

	protected void scale(AbstractSkeleton entity, PoseStack pose, float par1) {
		float size = this.getSize(entity, 1.25F);
		pose.scale(size, size, size);
	}

	public float getSize (Mob mob, float size) {
		return mob.hasEffect(PotionInit.leader_flag) ? size + 0.35F : size;
	}

	public ResourceLocation getTextureLocation(AbstractSkeleton entity) {
		return TEX;
	}
}
