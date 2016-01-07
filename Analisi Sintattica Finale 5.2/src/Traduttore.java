/** Esercizio 5.2
 * Modificare il programma del esercizio precedente in modo da tradurre programmi
 * ben tipati scritti nel frammento del linguaggio P che permette non solo il comando print e le
 * espressione aritmetiche-logici ma anche l�utilizzo di variabili. Piu precisamente, 
 * il frammento permette (1) la dichiarazione di variabili, (2) l�assegnazione di valori 
 * di espressioni a variabili, e l�utilizzo di variabili in espressioni.
 */

import java.io.*;

public class Traduttore {

	private Lexer lex;
	private BufferedReader pbr;
	private Token look;
	private CodeGenerator code = new CodeGenerator();
	private SymbolTable sb = new SymbolTable();

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

		if (look.tag == Tag.INTEGER || look.tag == Tag.BOOLEAN
				|| look.tag == Tag.ID || look.tag == Tag.PRINT
				|| look.tag == Tag.BEGIN) {
			declist();
			stat();
			match(Tag.EOF);

			// Creazione Output.j
			try {
				code.toJasmin();
			} catch (IOException e) {
				throw new Error("Errore di traduzione");
			}
		} else
			error("syntax error in function prog" + look.tag);
	}

	private void declist() {
		if (look.tag == Tag.INTEGER || look.tag == Tag.BOOLEAN) {
			dec();
			match(';');
			declist();
		} else if ((look.tag != Tag.ID && look.tag != Tag.PRINT && look.tag != Tag.BEGIN)) {
			error("syntax error in function declist" + look.tag);
		}
	}

	private void dec() {
		if (look.tag == Tag.INTEGER || look.tag == Tag.BOOLEAN) {
			Type dec_type = type();
			// inserimento nella tabella dei simboli
			sb.insert(((Word) look).getLexeme(), dec_type, sb.getAddress());
			match(Tag.ID);
			idList(dec_type);
		}
	}

	private Type statlist() {
		Type statlist_type = Type.NONE;
		if (look.tag == Tag.ID || look.tag == Tag.PRINT
				|| look.tag == Tag.BEGIN) {
			Type stat_type = stat();
			statlist_type = statlist_p(stat_type);
		} else {
			error("syntax error in function statlist: no match 'ID' or 'PRINT' or 'BEGIN': "
					+ look.tag);
		}

		return statlist_type;
	}

	private Type statlist_p(Type statlist_i) {
		Type statlist_p_type = Type.NONE;
		if (look.tag == ';') {
			match(';');
			Type stat_type = stat();
			statlist_p_type = statlist_p(stat_type);
		} else if (look.tag == Tag.END) {
			statlist_p_type = statlist_i;
		} else {
			error("syntax error in function statlist_p: no match ';' or 'END': "
					+ look.tag);
		}
		return statlist_p_type;
	}

	private Type idList(Type idList_i) {
		Type idList_type = Type.NONE;

		if (look.tag == ',') {
			match(',');
			// inserimento nella tabella dei simboli
			sb.insert(((Word) look).getLexeme(), idList_i, sb.getAddress());

			match(Tag.ID);
			idList_type = idList(idList_i);
		} else if (look.tag == ';') {
			idList_type = idList_i;
		} else
			error("syntax error in function idList" + look.tag);

		return idList_type;
	}

	private Type type() {
		Type type_type = Type.NONE;

		if (look.tag == Tag.INTEGER || look.tag == Tag.BOOLEAN) {
			int tmp = look.tag;
			move();
			if (tmp == Tag.INTEGER) {
				type_type = Type.INTEGER;
			} else if (tmp == Tag.BOOLEAN) {
				type_type = Type.BOOLEAN;
			}
		} else
			error("syntax error in function type" + look.tag);

		return type_type;
	}

	private Type stat() {
		Type stat_type = Type.NONE;

		if (look.tag == Tag.ID) {
			// salvo indirizzo
			int tmp_addr = sb.lookupAddress(((Word) look).getLexeme());
			// salvo il tipo
			Type tmp_type = sb.lookupType(((Word) look).getLexeme());
			match(Tag.ID);
			match(Tag.ASSIGN);
			Type orE_type = orE();
			if (orE_type == tmp_type) {
				code.emit(OpCode.istore, tmp_addr);
			} else
				error("syntax error in function type: ELEMENTS NON EQUAL"
						+ look.tag);
		} else if (look.tag == Tag.PRINT) {
			match(Tag.PRINT);
			match('(');
			stat_type = orE();
			match(')');
			// Istruzione per salto condizionato in base a valore di
			// prog_type (int o bool);
			code.emit(OpCode.invokestatic, stat_type.getValue());
		} else if (look.tag == Tag.BEGIN) {
			match(Tag.BEGIN);
			stat_type = statlist();
			match(Tag.END);
		} else
			error("syntax error in function type" + look.tag);

		return stat_type;
	}

	private Type orE() {
		Type orE_type = Type.NONE;
		if (look.tag == '(' || look.tag == Tag.NUM || look.tag == Tag.TRUE
				|| look.tag == Tag.FALSE || look.tag == Tag.ID) {
			Type andE_type = andE();
			orE_type = orE_p(andE_type);
		} else
			error("syntax error in function oreE" + look.tag);

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
		} else if (look.tag == ')' || look.tag == Tag.END || look.tag == ';') {
			orE_p_type = orE_i;
		} else
			error("syntax error in function orE_p" + look.tag);

		return orE_p_type;
	}

	private Type andE() {
		Type andE_type = Type.NONE;
		if (look.tag == '(' || look.tag == Tag.NUM || look.tag == Tag.TRUE
				|| look.tag == Tag.FALSE || look.tag == Tag.ID) {
			Type relE_type = relE();
			andE_type = andE_p(relE_type);
		} else
			error("syntax error in function andE" + look.tag);

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
		} else if (look.tag == ')' || look.tag == Tag.OR || look.tag == Tag.END
				|| look.tag == ';') {
			andE_p_type = andE_i;
		} else
			error("syntax error in function andE_p" + look.tag);

		return andE_p_type;
	}

	private Type relE() {
		Type relE_type = Type.NONE;
		if (look.tag == '(' || look.tag == Tag.NUM || look.tag == Tag.TRUE
				|| look.tag == Tag.FALSE || look.tag == Tag.ID) {
			Type addE_type = addE();
			relE_type = relE_p(addE_type);
		} else
			error("syntax error in function relE" + look.tag);

		return relE_type;
	}

	private Type relE_p(Type relE_i) {
		Type relE_p_type = Type.NONE;
		if (look.tag == Tag.EQ || look.tag == Tag.NE || look.tag == Tag.LE
				|| look.tag == Tag.GE || look.tag == '<' || look.tag == '>') {

			int oprel_tag = oprel();

			Type addE_type = addE();
			relE_p_type = addE_type;
			int ltrue = code.newLabel();
			int lnext = code.newLabel();
			if (relE_i != addE_type) {
				error("relE_p: Type not equal" + look.tag);
				relE_p_type = Type.NONE;
			} else {
				if (oprel_tag == Tag.EQ) {
					code.emit(OpCode.if_icmpeq, ltrue);
				} else if (oprel_tag == Tag.NE) {
					code.emit(OpCode.if_icmpne, ltrue);
				} else if (oprel_tag == Tag.LE) {
					code.emit(OpCode.if_icmple, ltrue);
				} else if (oprel_tag == Tag.GE) {
					code.emit(OpCode.if_icmpge, ltrue);
				} else if (oprel_tag == '<') {
					code.emit(OpCode.if_icmplt, ltrue);
				} else if (oprel_tag == '>') {
					code.emit(OpCode.if_icmpgt, ltrue);
				}
				code.emit(OpCode.ldc, 0);
				code.emit(OpCode.GOto, lnext);
				code.emitLabel(ltrue);
				code.emit(OpCode.ldc, 1);
				code.emitLabel(lnext);
			}
		} else if (look.tag == Tag.AND || look.tag == Tag.OR || look.tag == ')'
				|| look.tag == Tag.END || look.tag == ';') {
			relE_p_type = relE_i;
		} else
			error("syntax error in function relE_p" + look.tag);

		return relE_p_type;
	}

	/* Metodo per parte facoltativa esercizio 5.1 */
	private int oprel() {
		int oprel_tag = -1;
		if (look.tag == Tag.EQ || look.tag == Tag.NE || look.tag == Tag.LE
				|| look.tag == Tag.GE || look.tag == '<' || look.tag == '>') {
			oprel_tag = look.tag;
			move();
		}

		return oprel_tag;
	}

	private Type addE() {
		Type addE_type = Type.NONE;

		if (look.tag == '(' || look.tag == Tag.NUM || look.tag == Tag.TRUE
				|| look.tag == Tag.FALSE || look.tag == Tag.ID) {
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
				|| look.tag == ')' || look.tag == Tag.END || look.tag == ';') {
			addE_p_type = addE_p_i;
		} else
			error("syntax error in function addE_p" + look.tag);

		return addE_p_type;
	}

	private Type multE() {
		Type multE_type = Type.NONE;

		if (look.tag == '(' || look.tag == Tag.NUM || look.tag == Tag.TRUE
				|| look.tag == Tag.FALSE || look.tag == Tag.ID) {
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
				|| look.tag == Tag.OR || look.tag == Tag.AND || look.tag == ')'
				|| look.tag == Tag.END || look.tag == ';') {
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
		} else if (look.tag == Tag.ID) {
			// carica sulla pila l'indirizzo della variabile
			code.emit(OpCode.iload, sb.lookupAddress(((Word) look).getLexeme()));
			// restituisci il tipo della variabile
			fact_type = sb.lookupType(((Word) look).getLexeme());
			match(Tag.ID);
		} else if (look.tag == Tag.NUM) {
			// carica sulla pila la costante
			code.emit(OpCode.ldc, ((Number) look).lexVal());
			match(Tag.NUM);
			fact_type = Type.INTEGER;
		} else if (look.tag == Tag.TRUE) {
			// carica sulla pila la costante 1 (TRUE)
			code.emit(OpCode.ldc, 1);
			match(Tag.TRUE);
			fact_type = Type.BOOLEAN;
		} else if (look.tag == Tag.FALSE) {
			// carica sulla pila la costante 0 (FALSE)
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
