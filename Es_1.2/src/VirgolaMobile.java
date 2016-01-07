/** Esercizio 1.2
 * Progettare un DFA che riconosca il linguaggio delle costanti numeriche in virgola
 * mobile. Esempi di tali costanti sono: 123 123.5 .567 +7.5 -.7 67e10 1e-2 -.7e2
 * Realizzare il DFA in Java seguendo la costruzione vista in Figura 2, assicurarsi 
 * che l'implementazione riconosca il linguaggio desiderato.
 */

import static java.lang.Character.isDigit;

public class VirgolaMobile {
	public static boolean scan(String s) {
		int state = 0;
		int i = 0;
		while (state >= 0 && i < s.length()) {
			final char ch = s.charAt(i++);
			switch (state) {
			case 0:
				if (Character.isDigit(ch))
					state = 1;
				else if (ch == '+' || ch == '-')
					state = 2;
				else if (ch == '.')
					state = 3;
				else
					state = -1;
				break;
			case 1:
				if (Character.isDigit(ch))
					state = 1;
				else if (ch == 'e')
					state = 4;
				else if (ch == '.')
					state = 3;
				else
					state = -1;
				break;
			case 2:
				if (Character.isDigit(ch))
					state = 1;
				else if (ch == '.')
					state = 7;
				else
					state = -1;
				break;
			case 3:
				if (Character.isDigit(ch))
					state = 8;
				else
					state = -1;
				break;
			case 4:
				if (Character.isDigit(ch))
					state = 5;
				else if (ch == '+' || ch == '-')
					state = 6;
				else
					state = -1;
				break;	
			case 5:
				if (Character.isDigit(ch))
					state = 5;
				else
					state = -1;
				break;	
			case 6:
				if (Character.isDigit(ch))
					state = 5;
				else
					state = -1;
				break;	
			case 7:
				if (Character.isDigit(ch))
					state = 8;
				else
					state = -1;
				break;	
			case 8:
				if (Character.isDigit(ch))
					state = 8;
				else if (ch == 'e')
					state = 4;
				else
					state = -1;
				break;	
			}
		}
		return (state == 1 || state == 5 || state == 8);
	}

	public static void main(String[] args) {
		String[] test = {"123", "123.5", ".567", "+7.5", "-.7", "67e10", "1e-2", "-.7e2","+345+345",".34.","e532","345e","234-234-234","324e32e76"};
		for(int i=0;i< test.length;i++){
			System.out.println(test[i] + " e' una costante numerica in virgola mobile accettata: " + (scan(test[i]) ? "SI" : "NO"));
		}
		
	}
}