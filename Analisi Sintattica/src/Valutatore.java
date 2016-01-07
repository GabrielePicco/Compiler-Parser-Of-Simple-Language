import java.io.*;

public class Valutatore {
	private Lexer lex;
	private BufferedReader pbr;
	private Token look;

	public Valutatore(Lexer l, BufferedReader br) {
		lex = l;
		pbr = br;
		move();
	}

	void move() {
		look = lex.lexical_scan(pbr);
		System.err.println("token = " + look);
	}

	void error(String s,int tag) {
		if(tag >= 0 && tag <= 255){
			throw new Error("near line " + Lexer.line + ": " + s + ": " + (char)tag);
		}else{
			throw new Error("near line " + Lexer.line + ": " + s + " with tag : " + tag);
		}
	}

	void match(int t) {
		if (look.tag == t) {
			if (look.tag != Tag.EOF)
				move();
		} else
			error("match syntax error",look.tag);
	}

	public void start() {
		if(look.tag == '(' || look.tag == Tag.NUM){
			int val = expr();
			match(Tag.EOF);
			System.out.println("Valore espressione: " + val);
		}else{
			error("start: erroneus character",look.tag);
		}
	}

	private int expr() {
		if(look.tag == '(' || look.tag == Tag.NUM){
			int valTerm;
			valTerm = term();
			return exprp(valTerm);
		}else{
			error("expr: erroneus character",look.tag);
			return -1;
		}
	}

	

	private int exprp(int i) {
		int val;
		switch (look.tag) {
		case '+':
			match('+');
			val = term();
			return exprp(i+val);
		case '-':
			match('-');
			val = term();
			return exprp(i-val);
		case ')':
			return i;
		case Tag.EOF:
			return i;
		default:
			error("exprp: erroneus character",look.tag);
			return -1;
		}
	}

	private int term() {
		if(look.tag == '(' || look.tag == Tag.NUM) {
			int val;
			val = fact();
			return termp(val);
		}else{
			error("term: erroneus character",look.tag);
			return -1;
		}
	}

	private int termp(int i) {
		if(look.tag == '*') {
			int val;
			match('*');
			val = fact();
			return termp(i*val);
		}else if(look.tag == '/') {
			int val;
			match('/');
			val = fact();
			return termp(i/val);
		}else if(look.tag == '+' || look.tag == '-' || look.tag == ')' || look.tag == Tag.EOF){
			return i;
		}else{
			error("termp: erroneus character",look.tag);
			return -1;
		}
	}

	private int fact() {
		int val;
		switch (look.tag) {
		case '(':
			match('(');
			val = expr();
			match(')');
			return val;
		case Tag.NUM:
			val = ((Number)look).lexVal();
			match(Tag.NUM);
			return val;
		default:
			error("Fact: erroneus character",look.tag);
			return -1;
		}
	}

//	public static void main(String[] args) {
//		Lexer lex = new Lexer();
//		String path = "code/demo.txt"; // il percorso del file da leggere
//		try {
//			BufferedReader br = new BufferedReader(new FileReader(path));
//			Valutatore parser = new Valutatore(lex, br);
//			try{
//				parser.start();
//			} catch (Error e) {
//				e.printStackTrace();
//			}
//			br.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}