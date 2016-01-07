/**
 *  Esercizio 4.2 facoltativo
 * Type checker: programma per controllare i tipi di un’espressione aritmetico-logica semplice
 * (cioe' per segnalare se l’espressione contiene un’incompatibilita'
 *  rispetto ai tipi delle sue sotto-espressioni)
 */

import java.io.*;

public class ValutatoreFacoltativo {
	private Lexer lex;
	private BufferedReader pbr;
	private Token look;

	public ValutatoreFacoltativo(Lexer l, BufferedReader br) {
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
		if(look.tag == '(' || look.tag == Tag.NUM || look.tag == Tag.TRUE || look.tag == Tag.FALSE){
			int val = andexpr();
			match(Tag.EOF);
			if(val == Tag.NUM){
				System.out.println("NUMERIC");
			}else{
				System.out.println("BOOLEAN");
			}
		}else{
			error("start: erroneus character",look.tag);
		}
	}

	private int andexpr() {
		int andexpr_type = -1;
		if(look.tag == '(' || look.tag == Tag.NUM || look.tag == Tag.TRUE || look.tag == Tag.FALSE){
			int andterm_type = andterm();
			andexpr_type = andexprp(andterm_type);
		}else{
			error("start: erroneus character",look.tag);
		}
		return andexpr_type;
	}

	private int andexprp(int i) {
		int andexprp_val = -1;	
		if(look.tag == Tag.AND){
			int andterm_val;
			match(Tag.AND);
			andterm_val = andterm();
			andexprp_val = andexprp(andterm_val);
			if(i != Tag.BOOLEAN || andexprp_val != Tag.BOOLEAN)andexprp_val = -1;
		}else if(look.tag == ')' || look.tag == Tag.EOF){
			andexprp_val = i;
		}else{
			error("start: erroneus character",look.tag);
		}
		if(andexprp_val == -1){
			error("type error operation: BOOLEAN - INTEGER",look.tag);
		}
		return andexprp_val;
	}

	private int andterm() {
		int andterm_type = -1;
		if(look.tag == '(' || look.tag == Tag.NUM || look.tag == Tag.TRUE || look.tag == Tag.FALSE){
			int sumexpr_val = sumexpr();
			andterm_type = andtermp(sumexpr_val);
		}else{
			error("start: erroneus character",look.tag);
		}
		return andterm_type;
	}

	private int andtermp(int i) {
		int andtermp_type = -1;
		if(look.tag == Tag.EQ){
			match(Tag.EQ);
			int sumexpr_val = sumexpr();
			if(i != sumexpr_val){
				sumexpr_val = -1;
			}else{
				andtermp_type = Tag.BOOLEAN;
			}
		}else if(look.tag == ')' || look.tag == Tag.EOF || look.tag == Tag.END){
			andtermp_type = i;
		}else{
			error("start: erroneus character",look.tag);
		}
		if(andtermp_type == -1){
			error("type error operation: BOOLEAN -INTEGER",look.tag);
		}
		return andtermp_type;
	}

	private int sumexpr() {
		int sumexpr_type = -1;
		if(look.tag == '(' || look.tag == Tag.NUM || look.tag == Tag.TRUE || look.tag == Tag.FALSE){
			int sumterm_val = sumterm();
			sumexpr_type = sumexprp(sumterm_val);
		}else{
			error("start: erroneus character",look.tag);
		}
		return sumexpr_type;
	}
	
	private int sumexprp(int i) {
		int sumexprp_type = -1;	
		if(look.tag == '+'){
			match('+');
			int sumterm_val = sumterm();
			sumexprp_type = sumexprp(sumterm_val);
			if(i != Tag.NUM || sumexprp_type != Tag.NUM){
				sumexprp_type = -1;
			}
		}else if(look.tag == ')' || look.tag == Tag.EOF || look.tag == Tag.EQ || look.tag == Tag.END){
			sumexprp_type = i;
		}else{
			error("start: erroneus character",look.tag);
		}
		if(sumexprp_type == -1){
			error("type error operation: BOOLEAN - INTEGER",look.tag);
		}
		return sumexprp_type;
	}
	
	private int sumterm(){
		int sumterm_type = -1;
		if(look.tag == '('){
			match('(');
			sumterm_type = andexpr();
			match(')');
		}else if(look.tag == Tag.NUM){
			match(Tag.NUM);
			sumterm_type = Tag.NUM;
		}else if(look.tag == Tag.TRUE){
			match(Tag.TRUE);
			sumterm_type = Tag.BOOLEAN;
		}else if(look.tag == Tag.FALSE){
			match(Tag.FALSE);
			sumterm_type = Tag.BOOLEAN;
		}else{
			error("start: erroneus character",look.tag);
		}
		return sumterm_type;
	}

	public static void main(String[] args) {
		Lexer lex = new Lexer();
		String path = "code/demo2.txt"; // il percorso del file da leggere
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			ValutatoreFacoltativo parser = new ValutatoreFacoltativo(lex, br);
			try{
				parser.start();
			} catch (Error e) {
				e.printStackTrace();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}