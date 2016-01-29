.class public Output 
.super java/lang/Object

.method public <init>()V
 aload_0
 invokenonvirtual java/lang/Object/<init>()V
 return
.end method

.method public static printBool(I)V
 .limit stack 3
 getstatic java/lang/System/out Ljava/io/PrintStream;
 iload_0 
 bipush 1
 if_icmpeq Ltrue
 ldc "false"
 goto Lnext
 Ltrue:
 ldc "true"
 Lnext:
 invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
 return
.end method

.method public static printInt(I)V
 .limit stack 2
 getstatic java/lang/System/out Ljava/io/PrintStream;
 iload_0 
 invokestatic java/lang/Integer/toString(I)Ljava/lang/String;
 invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
 return
.end method

.method public static run()V
 .limit stack 1024
 .limit locals 256
<<<<<<< HEAD:Analisi Sintattica Finale 5.4/Output.j
 ldc 1
 istore 0
 ldc 2
 istore 1
 iload 0
 iload 1
 if_icmpgt L2
=======
 ldc 0
 istore 2
 ldc 2
 istore 0
 ldc 3
 istore 1
 iload 0
 iload 1
 if_icmpgt L2
 ldc 0
 goto L3
L2:
 ldc 1
L3:
 ldc 0
 if_icmpeq L0
 ldc 2
 ldc 3
 if_icmpgt L4
 ldc 0
 goto L5
L4:
 ldc 1
L5:
 istore 2
 iload 2
 invokestatic Output/printBool(I)V
 goto L1
L0:
 iload 0
 ldc 3
 if_icmpeq L6
 ldc 0
 goto L7
L6:
 ldc 1
L7:
 ldc 30
 ldc 1
 idiv 
 ldc 3
 if_icmpgt L8
>>>>>>> origin/master:Analisi Sintattica Finale 5.3/Output.j
 ldc 0
 goto L9
L8:
 ldc 1
<<<<<<< HEAD:Analisi Sintattica Finale 5.4/Output.j
L3:
 ldc 0
 if_icmpeq L0
 iload 0
 invokestatic Output/printInt(I)V
 goto L1
L0:
 iload 1
 invokestatic Output/printInt(I)V
L1:
=======
L9:
 iand 
 istore 2
 iload 2
 invokestatic Output/printBool(I)V
 iload 1
 invokestatic Output/printInt(I)V
L1:
 iload 0
 invokestatic Output/printInt(I)V
>>>>>>> origin/master:Analisi Sintattica Finale 5.3/Output.j
 return
.end method

.method public static main([Ljava/lang/String;)V
 invokestatic Output/run()V
 return
.end method

