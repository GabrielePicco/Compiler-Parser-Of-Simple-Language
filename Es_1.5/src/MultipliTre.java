/**
 *  Esercizio 1.5. 
 * Progettare e implementare un DFA che riconosca il linguaggio dei numeri binari
 * (stringhe di 0 e 1) il cui valore e multiplo di 3. Per esempio, 110 e 1001 sono stringhe del
 * linguaggio (rappresentano rispettivamente i numeri 6 e 9), mentre 10 e 111 no (rappresentano
 * rispettivamente i numeri 2 e 7). Suggerimento: usare tre stati per rappresentare il resto 
 * della divisione per 3 del numero. (esercizio svolto considerando 0 come multiplo di 3 
 * e non accettando la stringa vuota)
 */

public class MultipliTre {    
	
	public static boolean scan(String s) {
		
		int state = 0;
		int i = 0;
		
		while (state >= 0 && i < s.length()) {  
			final char ch = s.charAt(i++);    // a ogni nuovo ciclo prendo il carattere
                                             // successivo di s
			switch (state) {
			case 0:
				if (ch == '0')
					state = 1;
				else if (ch == '1')
					state = 2;
				else
					state = -1;
				break;
			case 1:
				if (ch == '0')
					state = 1;
				else if (ch == '1')
					state = 2;
				else
					state = -1;
				break;
			case 2:
				if (ch == '0')
					state = 3;
				else if (ch == '1')
					state = 1;
				else
					state = -1;
				break;
			case 3:
				if (ch == '0')
					state = 2;
				else if (ch == '1')
					state = 3;
				else
					state = -1;
				break;
			}
		}
		return state == 1;
	}

	public static void main(String[] args) {
		for(int i=0;i <= 333;i+=33){
			System.out.println("bin: " + Integer.toBinaryString(i) + " " + i + " e' un multiplo di tre: " + (scan(Integer.toBinaryString(i)) ? "SI" : "NO"));
		}
		for(int i=57;i <= 254;i+=22){
			System.out.println(i + " e' un multiplo di tre: " + (scan(Integer.toBinaryString(i)) ? "SI" : "NO"));
		}
	}
}