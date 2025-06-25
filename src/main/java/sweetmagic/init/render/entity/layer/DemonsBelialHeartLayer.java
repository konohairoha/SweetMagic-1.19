package sweetmagic.init.render.entity.layer;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sweetmagic.init.entity.monster.boss.DemonsBelial;
import sweetmagic.init.render.entity.model.DemonsBelialModel;

public class DemonsBelialHeartLayer<T extends DemonsBelial, M extends DemonsBelialModel<T>> extends RenderLayer<T, M> {

	private final ResourceLocation tex;
	private final ResourceLocation mainTex;
	private final AlphaFunction<T> alpha;
	private final DrawSelector<T, M> draw;
	private final DrawSelector<T, M> main;

	public DemonsBelialHeartLayer(RenderLayerParent<T, M> render, ResourceLocation tex, ResourceLocation mainTex, AlphaFunction<T> alpha, DrawSelector<T, M> draw, DrawSelector<T, M> main) {
		super(render);
		this.tex = tex;
		this.mainTex = mainTex;
		this.alpha = alpha;
		this.draw = draw;
		this.main = main;
	}

	public void render(PoseStack pose, MultiBufferSource buf, int light, T entity, float swing, float swingAmount, float parTick, float ageTick, float headYaw, float headPitch) {
		this.renderModel(pose, buf, light, entity, parTick, ageTick);
		this.renderHeart(pose, buf, light, entity, parTick, ageTick);
	}

	public void renderModel(PoseStack pose, MultiBufferSource buf, int light, T entity, float parTick, float ageTick) {
		List<ModelPart> list = this.main.getPartsToDraw(this.getParentModel());
		list.forEach(p -> p.visible = true);
		if (entity.isAlive()) { return; }
		pose.pushPose();
		this.getParentModel().root().getAllParts().forEach(part -> part.skipDraw = true);
		list.forEach(p -> p.skipDraw = false);
		VertexConsumer vert = buf.getBuffer(RenderType.entityTranslucentEmissive(this.mainTex));
		float maxDeathTime = (float) entity.getMaxDeathTime();
		this.getParentModel().renderToBuffer(pose, vert, light, LivingEntityRenderer.getOverlayCoords(entity, 0F), 1F, 1F, 1F, (maxDeathTime - (float) entity.deathTime) / maxDeathTime);
		ModelPart part = this.getParentModel().root();
		part.visible = true;
		list.forEach(p -> p.visible = false);
		part.getAllParts().forEach(p -> p.skipDraw = false);
		pose.popPose();
	}

	public void renderHeart(PoseStack pose, MultiBufferSource buf, int light, T entity, float parTick, float ageTick) {
		if (entity.isInvisible()) { return; }

		pose.pushPose();
		pose.scale(0.67F, 0.67F, 0.67F);
		pose.translate(-0.025F, 0F, -0.14F);
		List<ModelPart> list = this.draw.getPartsToDraw(this.getParentModel());
		this.getParentModel().root().getAllParts().forEach(part -> part.skipDraw = true);
		list.forEach(part -> part.skipDraw = false);
		VertexConsumer vert = buf.getBuffer(RenderType.entityTranslucentEmissive(this.tex));
		this.getParentModel().renderToBuffer(pose, vert, light, LivingEntityRenderer.getOverlayCoords(entity, 0F), 1F, 1F, 1F, this.alpha.apply(entity, parTick, ageTick));
		this.getParentModel().root().getAllParts().forEach(part -> part.skipDraw = false);
		pose.popPose();
	}

	@OnlyIn(Dist.CLIENT)
	public interface AlphaFunction<T> {
		float apply(T entity, float par1, float par2);
	}

	@OnlyIn(Dist.CLIENT)
	public interface DrawSelector<T, M> {
		List<ModelPart> getPartsToDraw(M model);
	}
}
