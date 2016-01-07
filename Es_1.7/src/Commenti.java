public class Commenti {
	public static boolean scan(String s) {
		int state = 0;
		int i = 0;
		while (state >= 0 && i < s.length()) {
			final char ch = s.charAt(i++);
			switch (state) {
			case 0:
				if (ch == 'a' || ch == '*')
					state = 0;
				else if(ch == '/')
					state = 1;
				else
					state = -1;
				break;
			case 1:
				if (ch == 'a')
					state = 0;
				else if (ch == '*')
					state = 2;
				else
					state = -1;
				break;
			case 2:
				if (ch == 'a')
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
				if (ch == 'a' || ch == '*')
					state = 5;
				else
					state = -1;
				break;
			case 5:
				if (ch == 'a' || ch == '*')
					state = 5;
				else
					state = -1;
				break;
			}
		}
		return state == 4 || state == 5 || state == 0;
	}

	public static void main(String[] args) {
		String test[] = {"aaa/****/aa","aa/*a*a*/","aaaa","/****/","/*aa*/","*/a","a/**/***a","aaa/*/aa","a/**/***/a","aa/*aa"};
		for(int i=0;i < test.length;i++){
			System.out.println(test[i] + " e' accettata: " + (scan(test[i]) ? "SI" : "NO"));
		}
		
	}
}