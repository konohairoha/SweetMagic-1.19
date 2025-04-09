package sweetmagic.init;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.minecraft.client.renderer.RenderType;
import sweetmagic.SweetMagicCore;

public class RenderTypeInit extends RenderType {

	public RenderTypeInit(String name, VertexFormat format, Mode mode, int bufferSize, boolean affectsCrumbling, boolean sort, Runnable setupState, Runnable clearState) {
		super(name, format, mode, bufferSize, affectsCrumbling, sort, setupState, clearState);
	}

	public static final RenderType SMELTERY_FLUID = RenderType.create(
			String.format("%s:%s", SweetMagicCore.MODID, "smeltery_fluid"), DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
			VertexFormat.Mode.QUADS, 256, false, true, CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(POSITION_COLOR_TEX_LIGHTMAP_SHADER).setTextureState(BLOCK_SHEET_MIPPED).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).createCompositeState(false));
}
