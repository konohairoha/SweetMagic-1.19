package sweetmagic.init.tile.gui.util;

import net.minecraft.resources.ResourceLocation;
import sweetmagic.init.tile.gui.util.SMButton.SMButtonTip;
import sweetmagic.init.tile.sm.TileSMMagic;

public class SMRenderTex {

	private final ResourceLocation tex;
	private final int x, y;
	private final int texX, texY;
	private final int sizeX, sizeY;
	private final SMButtonTip buttonTip;
	private final MFRenderGage mfRender;

	public SMRenderTex(ResourceLocation tex, int x, int y, int texX, int texY, int sizeX, int sizeY) {
		this.tex = tex;
		this.x = x;
		this.y = y;
		this.texX = texX;
		this.texY = texY;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.buttonTip = null;
		this.mfRender = null;
	}

	public SMRenderTex(ResourceLocation tex, int x, int y, int texX, int texY, int sizeX, int sizeY, SMButtonTip buttonTip) {
		this.tex = tex;
		this.x = x;
		this.y = y;
		this.texX = texX;
		this.texY = texY;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.buttonTip = buttonTip;
		this.mfRender = null;
	}

	public SMRenderTex(ResourceLocation tex, int x, int y, int texX, int texY, int sizeX, int sizeY, MFRenderGage mfRender) {
		this.tex = tex;
		this.x = x;
		this.y = y;
		this.texX = texX;
		this.texY = texY;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.buttonTip = null;
		this.mfRender = mfRender;
	}

	public ResourceLocation getTex() {
		return this.tex;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getTexX() {
		return this.texX;
	}

	public int getTexY() {
		return this.texY;
	}

	public int getSizeX() {
		return this.sizeX;
	}

	public int getSizeY() {
		return this.sizeY;
	}

	public SMButtonTip getButtonTip() {
		return this.buttonTip;
	}

	public MFRenderGage getMFRender() {
		return this.mfRender;
	}

	public record MFRenderGage(TileSMMagic tile, boolean isVertical) {

		public static MFRenderGage createChanger(TileSMMagic tile) {
			return new MFRenderGage(tile, false);
		}
	}
}
