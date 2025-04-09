package sweetmagic.api.emagic;

public enum SMAcceType {

	UPDATE,		// 常に
	MUL_UPDATE,// 常に
	TERMS;		// 条件

	public boolean is(SMAcceType type) {
		return this == type;
	}
}
