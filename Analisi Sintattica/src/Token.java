/**
 * Esercizio 2.1
 * Classe per rappresentare i token:
 * - I token che corrispondono a numeri prenderanno la forma <NUM, valore>;
 * - I token che corrispondono agli identificatori prenderanno la forma <ID, "lessema">;
 * - I token che corrispondono agli elementi della sintassi che consistono di un solo 
 *   carattere (ad esempio, ’(’, ’+’ e ’:’) prenderanno la forma hnomei, dove il nome 
 *   e il codice ASCII del carattere;
 * - I token che corrispondono agli elementi della sintassi che consistono di più
 *   caratteri (ad esempio, &&, <>, print, integer e :=) prenderanno la forma <nome, "lessema">. 
 */

public class Token {
	public final int tag;

	public Token(int t) {
		tag = t;
	}

	public String toString() {
		return "<" + tag + ">";
	}

	public static final 
		Token comma = new Token(','), 
		colon = new Token(':'),
		semicolon = new Token(';'),
		lpar = new Token('('),
		rpar = new Token(')'),
		plus = new Token('+'),
		minus = new Token('-'),
		mult = new Token('*'),
		div = new Token('/'),
		lt = new Token('<'),
		gt = new Token('>');
}