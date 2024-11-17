package sweetmagic.util;

public record RGBColor (int red, int green, int blue) {

	public int getRed () {
		return this.red;
	}

	public int getGreen () {
		return this.green;
	}

	public int getBlue () {
		return this.blue;
	}
}
