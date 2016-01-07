public class Deterministico {
	public static boolean scan(String s) {
		int state = 0;
		int i = 0;
		while (state >= 0 && i < s.length()) {
			final char ch = s.charAt(i++);
			switch (state) {
			case 0:
				if (ch == 'a')
					state = 1;
				else if(ch == 'b')
					state = 2;
				else
					state = -1;
				break;
			case 1:
				if (ch == 'a')
					state = 1;
				else if (ch == 'b')
					state = 3;
				else
					state = -1;
				break;
			case 2:
				if (ch == 'a')
					state = 4;
				else
					state = -1;
				break;
			case 3:
				state = -1;
				break;
			case 4:
				if (ch == 'a')
					state = 4;
				else
					state = -1;
				break;
			}
		}
		return state == 3 || state == 4;
	}

	public static void main(String[] args) {
		String test[] = {"ab","ba","aaab","baaaa","baba","bababa","a"};
		for(int i=0;i < test.length;i++){
			System.out.println(test[i] + " e' accettata: " + (scan(test[i]) ? "SI" : "NO"));
		}
		
	}
}