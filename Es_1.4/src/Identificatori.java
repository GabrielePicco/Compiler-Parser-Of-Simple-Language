/**
 *  Esercizio 1.4. Progettare e implementare un DFA che riconosca 
 * il linguaggio degli identificatori in un linguaggio in stile Java: un identificatore e'
 * una sequenza non vuota di lettere, numeri, ed il simbolo di sottolineatura _ che non 
 * comincia con un numero e che non puo essere composto solo da un _.
 */

import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;

public class Identificatori {
	public static boolean scan(String s) {
		int state = 0;
		int i = 0;
		while (state >= 0 && i < s.length()) {
			final char ch = s.charAt(i++);
			switch (state) {
			case 0:
				if (ch == '_')
					state = 1;
				else if (Character.isLetter(ch))
					state = 2;
				else
					state = -1;
				break;
			case 1:
				if (ch == '_')
					state = 1;
				else if (Character.isLetter(ch) || Character.isDigit(ch))
					state = 2;
				else
					state = -1;
				break;
			case 2:
				if (ch == '_' || Character.isLetter(ch) || Character.isDigit(ch))
					state = 2;
				else
					state = -1;
				break;
			}
		}
		return state == 2;
	}

	public static void main(String[] args) {
		String[] test = {"_a","_","45f","abc6","__cd"};
		for(int i=0;i< test.length;i++){
			System.out.println(test[i] + " e' un identificatore: " + (scan(test[i]) ? "SI" : "NO"));
		}
	}
}