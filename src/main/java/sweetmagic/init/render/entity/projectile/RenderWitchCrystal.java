package sweetmagic.init.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.ItemInit;
import sweetmagic.init.entity.monster.WitchCrystal;

public class RenderWitchCrystal<T extends WitchCrystal> extends EntityRenderer<T> {

	private static final ResourceLocation CRYSTAL = SweetMagicCore.getSRC("textures/entity/witch_crystal.png");
	private static final ItemStack STACK = new ItemStack(ItemInit.witch_tears);
	private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(CRYSTAL);
	private static final float SIN_45 = (float) Math.sin((Math.PI / 4D));
	private final ModelPart glass;
	private final ItemRenderer render;

	public RenderWitchCrystal(EntityRendererProvider.Context con) {
		super(con);
		this.render = con.getItemRenderer();
		ModelPart model = con.bakeLayer(ModelLayers.END_CRYSTAL);
		this.glass = model.getChild("glass");
	}

	public ResourceLocation getTextureLocation(T entity) {
		return TextureAtlas.LOCATION_BLOCKS;
	}

	public void render(T entity, float parTick, float par2, PoseStack pose, MultiBufferSource buf, int light) {

		int overray = OverlayTexture.NO_OVERLAY;
		int tickCount = entity.tickCount;

		pose.pushPose();
		float size = 0.65F;
		pose.translate(0D, 1.15D, 0D);
		pose.translate(0D, Math.sin(tickCount / 20F) * 0.4D, 0D);
		float angle = tickCount / 20F * (180F / (float) Math.PI);
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		pose.scale(size, size, size);
		this.render.renderStatic(STACK, ItemTransforms.TransformType.FIXED, light, overray, pose, buf, 0);
		pose.popPose();

		pose.pushPose();
		size = 1.5F;
		pose.scale(size, size, size);
		pose.translate(0D, 0.75D, 0D);
		float f1 = tickCount * 3F;
		VertexConsumer vert = buf.getBuffer(RENDER_TYPE).color(0F, 0F, 0F, 1F);
		pose.mulPose(Vector3f.YP.rotationDegrees(f1));
		pose.translate(0D, Math.sin(tickCount / 20F) * 0.2D, 0D);
		pose.mulPose(new Quaternion(new Vector3f(SIN_45, 0F, SIN_45), 60F, true));
		this.glass.render(pose, vert, light, overray);

		pose.scale(0.875F, 0.875F, 0.875F);
		pose.mulPose(new Quaternion(new Vector3f(SIN_45, 0F, SIN_45), 60F, true));
		pose.mulPose(Vector3f.YP.rotationDegrees(f1));

		this.glass.render(pose, vert, light, overray);
		pose.scale(0.875F, 0.875F, 0.875F);
		pose.mulPose(new Quaternion(new Vector3f(SIN_45, 0F, SIN_45), 60F, true));
		pose.mulPose(Vector3f.YP.rotationDegrees(f1));
		pose.popPose();
	}
}
