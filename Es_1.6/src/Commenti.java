// Esercizio 1.6. Progettare e implementare un DFA con l’alfabeto {/, *, a} che riconosca il linguaggio
//   di stringhe che contengono almeno 4 caratteri che iniziano con /*, che finiscono con */
// e che contengono una solo occorrenza della sequenza */
// quella finale. Verificare che il DFA accetti le stringhe:
// /****/, /*a*a*/, /*a/**/, /**a///a/a**/ e /**/ ma non /*/ oppure /**/***/.

public class Commenti {
	
	public static boolean scan(String s) {
		
		int state = 0;
		int i = 0;
		
		while (state >= 0 && i < s.length()) {
			final char ch = s.charAt(i++);
			switch (state) {
			case 0:
				if (ch == '/')
					state = 1;
				else
					state = -1;
				break;
			case 1:
				if (ch == '*')
					state = 2;
				else
					state = -1;
				break;
			case 2:
				if (ch == 'a' || ch == '/')
					state = 2;
				else if (ch == '*')
					state = 3;
				else
					state = -1;
				break;
			case 3:
				if (ch == '*')
					state = 3;
				else if (ch == 'a')
					state = 2;
				else if (ch == '/')
					state = 4;
				else
					state = -1;
				break;
			case 4:
				state = -1;
				break;
			}
		}
		return state == 4;
	}

	public static void main(String[] args) {
		String test[] = {"/****/", "/*a*a*/", "/*a/**/", "/**a///a/a**/","/**/","/*/","/**/***/"};
		for(int i=0;i < test.length;i++){
			System.out.println(test[i] + " e' accettata: " + (scan(test[i]) ? "SI" : "NO"));
		}
		
	}
}