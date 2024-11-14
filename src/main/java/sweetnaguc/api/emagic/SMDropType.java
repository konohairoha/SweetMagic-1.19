package sweetmagic.api.emagic;

public enum SMDropType {

	CRAFT("acc_howget_craft"),
	CHEST("acc_howget_chest"),
	BAG("acc_howget_bag"),
	CHEST_BAGS("acc_howget_chestbag"),
	MOBDROP("acc_howget_mobdrop");

	SMDropType(String name) {
		this.name = name;
	}

	private final String name;

	public String getName () {
		return this.name;
	}

	public boolean is(SMDropType type) {
		return this == type;
	}
}
