/** Esercizio 5.1
 * Classe con semplice enumerazione dei nomi mnemonici
 */

public enum OpCode { 
    ldc , imul ,ineg , idiv , iadd , 
    isub , istore ,ior, iand , iload ,
    if_icmpeq , if_icmple , if_icmplt , if_icmpne, if_icmpge , 
    if_icmpgt , ifne , GOto , invokestatic , label
}