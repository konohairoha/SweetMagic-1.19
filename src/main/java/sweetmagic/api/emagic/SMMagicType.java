package sweetmagic.api.emagic;

public enum SMMagicType {

	SHOT,		// 射撃
	NORMAL,		// 通常
	SUMMON,		// 召喚
	CHARGE,		// 溜め
	FIELD,		// フィールド
	BOSS;		// ボス

	SMMagicType() { }

	public boolean is(SMMagicType type) {
		return this.equals(type);
	}
}
