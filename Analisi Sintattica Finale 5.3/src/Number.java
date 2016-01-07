public class Number extends Token {
	private int val = 0;

	public Number(int tag, int v) {
		super(tag);
		val = v;
	}

	public String toString() {
		return "<" + tag + ", " + val + ">";
	}
	
	public int lexVal(){
		return val;
	}
}