import java.io.*;

public class Parser {
	private Lexer lex;
	private BufferedReader pbr;
	private Token look;

	public Parser(Lexer l, BufferedReader br) {
		lex = l;
		pbr = br;
		move();
	}

	void move() {
		look = lex.lexical_scan(pbr);
		System.err.println("token = " + look);
	}

	void error(String s) {
		throw new Error("near line " + Lexer.line + ": " + s);
	}

	void match(int t) {
		if (look.tag == t) {
			if (look.tag != Tag.EOF)
				move();
		} else
			error("match syntax error: " + look.tag);
	}

	public void start() {
		if(look.tag == '(' || look.tag == Tag.NUM){
			expr();
			match(Tag.EOF);
		}else{
			error("start: erroneus character: " + look.tag);
		}
	}

	private void expr() {
		if(look.tag == '(' || look.tag == Tag.NUM){
			term();
			exprp();
		}else{
			error("expr: erroneus character: " + look.tag);
		}
	}

	private void exprp() {
		switch (look.tag) {
		case '+':
			match('+');
			term();
			exprp();
			break;
		case '-':
			match('-');
			term();
			exprp();
			break;
		case ')':
			break;
		case Tag.EOF:
			break;
		default:
			error("exprp: erroneus character: " + look.tag);
			break;
		}
	}

	private void term() {
		if(look.tag == '(' || look.tag == Tag.NUM) {
			fact();
			termp();
		}else{
			error("term: erroneus character: " + look.tag);
		}
	}

	private void termp() {
		if(look.tag == '*') {
			match('*');
			fact();
			termp();
		}else if(look.tag == '/') {
			match('/');
			fact();
			termp();
		}else if(look.tag != '+' && look.tag != '-' && look.tag != ')' && look.tag != Tag.EOF){
			error("termp: erroneus character: " + look.tag);
		}
	}

	private void fact() {
		switch (look.tag) {
		case '(':
			match('(');
			expr();
			match(')');
			break;
		case Tag.NUM:
			match(Tag.NUM);
			break;
		default:
			error("Fact: erroneus character: " + look.tag);
			break;
		}
	}

//	public static void main(String[] args) {
//		Lexer lex = new Lexer();
//		String path = "code/demo.txt"; // il percorso del file da leggere
//		try {
//			BufferedReader br = new BufferedReader(new FileReader(path));
//			Parser parser = new Parser(lex, br);
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