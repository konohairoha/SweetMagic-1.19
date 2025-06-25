package sweetmagic.init.render.entity.monster;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Slime;
import sweetmagic.SweetMagicCore;

public class RenderElectricCube extends SlimeRenderer {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/electriccube.png");

	public RenderElectricCube(EntityRendererProvider.Context cont) {
		super(cont);
	}

	public ResourceLocation getTextureLocation(Slime entity) {
		return TEX;
	}
}
