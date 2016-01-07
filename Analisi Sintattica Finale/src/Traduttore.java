import java.io.*;

public class Traduttore {
	public enum Type {
		BOOLEAN(0), INTEGER(1), NONE(-1);

		private final int value;

		private Type(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	};

	private Lexer lex;
	private BufferedReader pbr;
	private Token look;
	private CodeGenerator code = new CodeGenerator();

	public Traduttore(Lexer l, BufferedReader br) {
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
			error("syntax error in function match" + look.tag);
	}

	private void prog() {

		if (look.tag == Tag.PRINT) {
			match(Tag.PRINT);
			match('(');
			Type prog_type = orE();
			match(')');
			match(Tag.EOF);
			code.emit(OpCode.invokestatic, prog_type.getValue());

			// Stampa
			try {
				code.toJasmin();
			} catch (IOException e) {
				throw new Error("Errore di traduzione");
			}
		} else
			error("syntax error in function prog" + look.tag);
	}

	private Type orE() {
		Type orE_type = Type.NONE;
		if (look.tag == '(' || look.tag == Tag.NUM || look.tag == Tag.TRUE
				|| look.tag == Tag.FALSE) {
			Type andE_type = andE();
			orE_type = orE_p(andE_type);
		} else {
			error("syntax error in function oreE" + look.tag);
		}
		return orE_type;
	}

	private Type orE_p(Type orE_i) {
		Type orE_p_type = Type.NONE;
		if (look.tag == Tag.OR) {
			match(Tag.OR);
			Type andE_type = andE();
			if (andE_type != Type.BOOLEAN || orE_i != Type.BOOLEAN) {
				error("syntax error in function orE_p: NOT BOOL" + look.tag);
			} else {
				code.emit(OpCode.ior);
				orE_p_type = orE_p(andE_type);
			}
		} else if (look.tag == ')') {
			orE_p_type = orE_i;
		} else {
			error("syntax error in function orE_p" + look.tag);
		}

		return orE_p_type;
	}

	private Type andE() {
		Type andE_type = Type.NONE;
		if (look.tag == '(' || look.tag == Tag.NUM || look.tag == Tag.TRUE
				|| look.tag == Tag.FALSE) {
			Type relE_type = relE();
			andE_type = andE_p(relE_type);
		} else {
			error("syntax error in function andE" + look.tag);
		}
		return andE_type;
	}

	private Type andE_p(Type andE_i) {
		Type andE_p_type = Type.NONE;
		if (look.tag == Tag.AND) {
			match(Tag.AND);
			Type relE_type = relE();
			if (relE_type != Type.BOOLEAN || andE_i != Type.BOOLEAN) {
				error("syntax error in function andE_p: NOT BOOL" + look.tag);
			} else {
				code.emit(OpCode.iand);
				andE_p_type = andE_p(relE_type);
			}
		} else if (look.tag == ')' || look.tag == Tag.OR) {
			andE_p_type = andE_i;
		} else {
			error("syntax error in function andE_p" + look.tag);
		}

		return andE_p_type;
	}

	private Type relE() {
		Type relE_type = Type.NONE;
		if (look.tag == '(' || look.tag == Tag.NUM || look.tag == Tag.TRUE
				|| look.tag == Tag.FALSE) {
			Type addE_type = addE();
			relE_type = relE_p(addE_type);
		} else {
			error("syntax error in function relE" + look.tag);
		}
		return relE_type;
	}

	private Type relE_p(Type relE_i) {
		Type relE_p_type = Type.NONE;
		if (look.tag == Tag.EQ || look.tag == Tag.NE || look.tag == Tag.LE
				|| look.tag == Tag.GE || look.tag == '<' || look.tag == '>') {
			int tmpTag = look.tag;
			move();
			Type addE_type = addE();
			relE_p_type = addE_type;
			int ltrue = code.newLabel();
			int lnext = code.newLabel();
			if (relE_i != addE_type) {
				error("relE_p: Type not equal" + look.tag);
				relE_p_type = Type.NONE;
			} else {
				if (tmpTag == Tag.EQ) {
					code.emit(OpCode.if_icmpeq, ltrue);
				} else if (tmpTag == Tag.NE) {
					code.emit(OpCode.if_icmpne, ltrue);
				} else if (tmpTag == Tag.LE) {
					code.emit(OpCode.if_icmple, ltrue);
				} else if (tmpTag == Tag.GE) {
					code.emit(OpCode.if_icmpge, ltrue);
				} else if (tmpTag == '<') {
					code.emit(OpCode.if_icmplt, ltrue);
				} else if (tmpTag == '>') {
					code.emit(OpCode.if_icmpgt, ltrue);
				}
				code.emit(OpCode.ldc, 0);
				code.emit(OpCode.GOto, lnext);
				code.emitLabel(ltrue);
				code.emit(OpCode.ldc, 1);
				code.emitLabel(lnext);
			}
		} else if (look.tag == Tag.AND || look.tag == Tag.OR || look.tag == ')') {
			relE_p_type = relE_i;
		} else {
			error("syntax error in function relE_p" + look.tag);
		}

		return relE_p_type;
	}

	private Type addE() {
		Type addE_type = Type.NONE;

		if (look.tag == '(' || look.tag == Tag.NUM || look.tag == Tag.TRUE
				|| look.tag == Tag.FALSE) {
			Type multE_type = multE();
			addE_type = addE_p(multE_type);
		} else
			error("syntax error in function addE" + look.tag);

		return addE_type;
	}

	private Type addE_p(Type addE_p_i) {
		Type addE_p_type = Type.NONE;

		if (look.tag == '+' || look.tag == '-') {
			int tmp = look.tag;
			move();
			Type multE_type = multE();
			if (multE_type == Type.INTEGER && addE_p_i == Type.INTEGER) {
				if (tmp == '+') {
					code.emit(OpCode.iadd);
				} else if (tmp == '-') {
					code.emit(OpCode.isub);
				}
				addE_p_type = addE_p(multE_type);
			} else
				error("syntax error in function addE_p: NOT INTEGER" + look.tag);
		} else if (look.tag == Tag.EQ || look.tag == Tag.NE
				|| look.tag == Tag.LE || look.tag == Tag.GE || look.tag == '<'
				|| look.tag == '>' || look.tag == Tag.OR || look.tag == Tag.AND
				|| look.tag == ')') {
			addE_p_type = addE_p_i;
		} else
			error("syntax error in function addE_p" + look.tag);

		return addE_p_type;
	}

	private Type multE() {
		Type multE_type = Type.NONE;

		if (look.tag == '(' || look.tag == Tag.NUM || look.tag == Tag.TRUE
				|| look.tag == Tag.FALSE) {
			Type fact_type = fact();
			multE_type = multE_p(fact_type);
		} else
			error("syntax error in function multE" + look.tag);

		return multE_type;
	}

	private Type multE_p(Type multE_p_i) {
		Type multE_p_type = Type.NONE;

		if (look.tag == '*' || look.tag == '/') {
			int tmp = look.tag;
			move();
			Type fact_type = fact();
			if (fact_type == Type.INTEGER && multE_p_i == Type.INTEGER) {
				if (tmp == '*') {
					code.emit(OpCode.imul);
				} else if (tmp == '/') {
					code.emit(OpCode.idiv);
				}
				multE_p_type = multE_p(fact_type);
			} else
				error("syntax error in function multE_p: NOT INTEGER"
						+ look.tag);
		} else if (look.tag == '+' || look.tag == '-' || look.tag == Tag.EQ
				|| look.tag == Tag.NE || look.tag == Tag.LE
				|| look.tag == Tag.GE || look.tag == '<' || look.tag == '>'
				|| look.tag == Tag.OR || look.tag == Tag.AND || look.tag == ')') {
			multE_p_type = multE_p_i;
		} else
			error("syntax error in function multE_p" + look.tag);

		return multE_p_type;
	}

	private Type fact() {
		Type fact_type = Type.NONE;
		if (look.tag == '(') {
			match('(');
			fact_type = orE();
			match(')');
		} else if (look.tag == Tag.NUM) {
			code.emit(OpCode.ldc, ((Number) look).lexVal());
			match(Tag.NUM);
			fact_type = Type.INTEGER;
		} else if (look.tag == Tag.TRUE) {
			code.emit(OpCode.ldc, 1);
			match(Tag.TRUE);
			fact_type = Type.BOOLEAN;
		} else if (look.tag == Tag.FALSE) {
			code.emit(OpCode.ldc, 0);
			match(Tag.FALSE);
			fact_type = Type.BOOLEAN;
		} else {
			error("syntax error in function fact" + look.tag);
		}
		return fact_type;
	}

	public static void main(String[] args) {
		Lexer lex = new Lexer();
		String path = "Code/demo.pas"; // il percorso del file da leggere
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			Traduttore parser = new Traduttore(lex, br);
			parser.prog();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}