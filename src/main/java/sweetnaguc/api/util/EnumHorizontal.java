package sweetmagic.api.util;

import java.util.Arrays;
import java.util.List;

import net.minecraft.util.StringRepresentable;

public enum EnumHorizontal implements StringRepresentable {

	LEFT("left"),
	CEN("center"),
	RIGHT("right"),
	NOR("normal");

	private final String name;

	EnumHorizontal(String name) {
		this.name = name;
	}

	public static List<EnumHorizontal> getLocalList() {
		return Arrays.<EnumHorizontal> asList(RIGHT, CEN, LEFT, NOR);
	}

	public String toString() {
		return this.name;
	}

	public String getSerializedName() {
		return this.name;
	}

	public static EnumHorizontal getHorizontal(boolean left, boolean right) {

		EnumHorizontal local = EnumHorizontal.NOR;

		if (left) {
			local = right ? EnumHorizontal.CEN : EnumHorizontal.RIGHT;
		}

		else {
			if (right) {
				local = EnumHorizontal.LEFT;
			}
		}

		return local;
	}
}
