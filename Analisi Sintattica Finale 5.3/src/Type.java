public enum Type {
	BOOLEAN(0), INTEGER(1), NONE(-1);

	private final int value;

	private Type(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
};
