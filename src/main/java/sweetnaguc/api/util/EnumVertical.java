package sweetmagic.api.util;

import java.util.Arrays;
import java.util.List;

import net.minecraft.util.StringRepresentable;

public enum EnumVertical implements StringRepresentable {

	TOP("top"),
	CEN("center"),
	BOT("bottom"),
	NOR("normal");

	private final String name;

	EnumVertical(String name) {
		this.name = name;
	}

	public static List<EnumVertical> getLocalList() {
		return Arrays.<EnumVertical> asList(BOT, CEN, TOP, NOR);
	}

	public String toString() {
		return this.name;
	}

	public String getSerializedName() {
		return this.name;
	}

	public boolean is(EnumVertical ver) {
		return this.equals(ver);
	}

	public static EnumVertical getVertical(boolean bot, boolean top) {

		EnumVertical local = EnumVertical.NOR;

		if (bot) {
			local = top ? EnumVertical.CEN : EnumVertical.TOP;
		}

		else {
			if (top) {
				local = EnumVertical.BOT;
			}
		}

		return local;
	}
}
