package sweetmagic.init.tile.gui.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import sweetmagic.init.tile.sm.TileAbstractSM;

public class SMButton {

	private final ResourceLocation tex;
	private final int x, y;
	private final int texX, texY;
	private final int sizeX, sizeY;
	private final SMButtonTip buttonTip;
	private List<Boolean> isView = new ArrayList<>();

	public SMButton (ResourceLocation tex, int x, int y, int texX, int texY, int sizeX, int sizeY) {
		this.tex = tex;
		this.x = x;
		this.y = y;
		this.texX = texX;
		this.texY = texY;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.buttonTip = null;
		this.setIsView(false);
	}

	public SMButton (ResourceLocation tex, int x, int y, int texX, int texY, int sizeX, int sizeY, SMButtonTip buttonTip) {
		this.tex = tex;
		this.x = x;
		this.y = y;
		this.texX = texX;
		this.texY = texY;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.buttonTip = buttonTip;
		this.setIsView(false);
	}

	public ResourceLocation getTex () {
		return this.tex;
	}

	public int getX () {
		return this.x;
	}

	public int getY () {
		return this.y;
	}

	public int getTexX () {
		return this.texX;
	}

	public int getTexY () {
		return this.texY;
	}

	public int getSizeX () {
		return this.sizeX;
	}

	public int getSizeY () {
		return this.sizeY;
	}

	public void setIsView (boolean isView) {
		this.isView.clear();
		this.isView.add(isView);
	}

	public boolean isView () {
		return this.isView.get(0);
	}

	public SMButtonTip getButtonTip () {
		return this.buttonTip;
	}

	public boolean isButtonRender () {
		return true;
	}

	public static class SMButtonTip {

		private final String tip;
		private final int tipX, tipY;
		private final TileAbstractSM tile;

		public SMButtonTip (String tip, int tipX, int tipY) {
			this.tip = tip;
			this.tipX = tipX;
			this.tipY = tipY;
			this.tile = null;
		}

		public SMButtonTip (String tip, int tipX, int tipY, TileAbstractSM tile) {
			this.tip = tip;
			this.tipX = tipX;
			this.tipY = tipY;
			this.tile = tile;
		}

		public String getTip () {
			return this.tip;
		}

		public int getTipX () {
			return this.tipX;
		}

		public int getTipY () {
			return this.tipY;
		}

		public TileAbstractSM getTile() {
			return this.tile;
		}

		public boolean isFlagText (TileAbstractSM tile) {
			return false;
		}
	}
}
