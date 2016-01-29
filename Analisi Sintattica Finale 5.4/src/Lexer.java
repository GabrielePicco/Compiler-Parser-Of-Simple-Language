/**
 *  Esercizio 2.1
 * Analizzatore lessicale di comandi semplici
 */

import java.io.*;
import java.util.*;

public class Lexer {
	
	public static int line = 1;
	private char peek = ' ';
	Hashtable<String, Word> words = new Hashtable<String, Word>();

	void reserve(Word w) {
		words.put(w.lexeme, w);
	}

	public Lexer() {
		reserve(new Word(Tag.VAR, "var"));
		reserve(new Word(Tag.INTEGER, "integer"));
		reserve(new Word(Tag.BOOLEAN, "boolean"));
		reserve(new Word(Tag.TRUE, "true"));
		reserve(new Word(Tag.FALSE, "false"));
		reserve(new Word(Tag.PRINT, "print"));
		reserve(new Word(Tag.NOT, "not"));
		reserve(new Word(Tag.IF, "if"));
		reserve(new Word(Tag.THEN, "then"));
		reserve(new Word(Tag.ELSE, "else"));
		reserve(new Word(Tag.WHILE, "while"));
		reserve(new Word(Tag.DO, "do"));
		reserve(new Word(Tag.BEGIN, "begin"));
		reserve(new Word(Tag.END, "end"));
	}

	private void readch(BufferedReader br) {
		try {
			peek = (char) br.read();
		} catch (IOException exc) {
			peek = (char) -1; // ERROR
		}
	}

	public Token lexical_scan(BufferedReader br) {
		while (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
			if (peek == '\n')
				line++;
			readch(br);
		}
		switch (peek) {
		case ',':
			peek = ' ';
			return Token.comma;
		case ':':
			readch(br);
			if (peek == '=') {
				peek = ' ';
				return Word.assign;
			} else {
				return Token.colon;
			}
		case ';':
			peek = ' ';
			return Token.semicolon;
		case '(':
			peek = ' ';
			return Token.lpar;
		case ')':
			peek = ' ';
			return Token.rpar;
		case '+':
			peek = ' ';
			return Token.plus;
		case '-':
			peek = ' ';
			return Token.minus;
		case '*':
			peek = ' ';
			return Token.mult;
		case '/':
			peek = ' ';
			return Token.div;
		case '<':
			readch(br);
			if (peek == '=') {
				peek = ' ';
				return Word.le;
			} else if (peek == '>') {
				peek = ' ';
				return Word.ne;
			} else {
				return Token.lt;
			}
		case '>':
			readch(br);
			if (peek == '=') {
				peek = ' ';
				return Word.ge;
			} else {
				return Token.gt;
			}
		case '&':
			readch(br);
			if (peek == '&') {
				peek = ' ';
				return Word.and;
			} else {
				System.err.println("Erroneous character" + " after & : " + peek);
				return null;
			}
		case '|':
			readch(br);
			if (peek == '|') {
				peek = ' ';
				return Word.or;
			} else {
				System.err.println("Erroneous character" + " after | : " + peek);
				return null;
			}
		case '=':
			readch(br);
			if (peek == '=') {
				peek = ' ';
				return Word.eq;
			} else {
				System.err.println("Erroneous character" + " after = : " + peek);
				return null;
			}
		default:
			if (Character.isLetter(peek) || peek == '_') {
				String s = "";
				int state = 0;
				do {
					s += peek;
					switch (state) {
					case 0:
						if (peek == '_')
							state = 1;
						else if (Character.isLetter(peek))
							state = 2;
						else
							state = -1;
						break;
					case 1:
						if (peek == '_')
							state = 1;
						else if (Character.isLetter(peek) || Character.isDigit(peek))
							state = 2;
						else
							state = -1;
						break;
					case 2:
						if (peek == '_' || Character.isLetter(peek) || Character.isDigit(peek))
							state = 2;
						else
							state = -1;
						break;
					}
					readch(br);
				} while (Character.isDigit(peek) || Character.isLetter(peek) || peek == '_');
				if ((Word) words.get(s) != null)
					return (Word) words.get(s);
				else {
					if(state == 2){ // se lo stato è 2 è finale ed s è un identificatore
						Word w = new Word(Tag.ID, s);
						words.put(s, w);
						return w;
					}else{
						System.err.println("\nErroneous indentifier: " + s);
						return null;
					}
					
				}
			} else {
				if (Character.isDigit(peek)) {
					int val = 0;
					do {
						val = val * 10 + Character.getNumericValue(peek);
						readch(br);
					} while (Character.isDigit(peek));
					if(peek != '_' && Character.isLetter(peek) == false){
						Number num = new Number(Tag.NUM, val);
						return num;
					}else{
						System.err.println("Erroneous character in number: " + peek);
						return null;
					}
				}
				if (peek == (char)-1) {
					return new Token(Tag.EOF);
				} else {
					System.err.println("Erroneous character: " + peek);
					return null;
				}
			}
		}
	}
}