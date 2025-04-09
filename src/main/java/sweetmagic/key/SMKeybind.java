package sweetmagic.key;

public enum SMKeybind {

	OPEN("key.sweetmagic.open"),
	NEXT("key.sweetmagic.next"),
	BACK("key.sweetmagic.back"),
	POUCH("key.sweetmagic.pouch"),
	SPECIAL("key.sweetmagic.special");

	private final String keyName;

	SMKeybind(String keyName) {
		this.keyName = keyName;
	}

	public String getName() {
		return this.keyName;
	}
}
