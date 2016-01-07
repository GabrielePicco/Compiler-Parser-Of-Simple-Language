/**
 *  Esercizio 2.1
 * Word rappresenta i token che corrispondono agli identificatori, alle parole chiave
 * agli elementi del sintassi che consistono di piu caratteri
 */

public class Word extends Token {
	
	public String lexeme = "";

	public Word(int tag, String s) {
		super(tag);
		lexeme = s;
	}

	public String toString() {
		return "<" + tag + ", " + lexeme + ">";
	}
	
public static final Word
	and = new Word(Tag.AND, "&&"),
	or = new Word(Tag.OR, "||"),
	eq = new Word(Tag.EQ, "=="),
	le = new Word(Tag.LE, "<="),
	ne = new Word(Tag.NE, "<>"),
	ge = new Word(Tag.GE, ">="),
	assign = new Word(Tag.ASSIGN, ":=");
}