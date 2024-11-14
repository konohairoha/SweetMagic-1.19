package sweetmagic.api.util;

import java.util.Arrays;
import java.util.List;

import net.minecraft.util.StringRepresentable;

public enum EnumConect implements StringRepresentable {

	TOP("top"),
	CEN("center"),
	BOT("bottom");

	private final String name;

	EnumConect(String name) {
		this.name = name;
	}

	public static List<EnumConect> getLocalList() {
		return Arrays.<EnumConect> asList(BOT, CEN, TOP);
	}

	public String toString() {
		return this.name;
	}

	public String getSerializedName() {
		return this.name;
	}

	public boolean is(EnumConect ver) {
		return this.equals(ver);
	}
}