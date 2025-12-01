%struct.Ponto = type { i32, i32 }
%union.Valor = type { i32, float }
@.str0 = private unnamed_addr constant [45 x i8] c"  [Funcao] Executou funcao void com sucesso.\00"
@.str1 = private unnamed_addr constant [42 x i8] c"=== INICIO DOS TESTES DO COMPILADOR ===\0A\0A\00"
@.str2 = private unnamed_addr constant [30 x i8] c"--- 1. Variaveis e Tipos ---\0A\00"
@.str3 = private unnamed_addr constant [28 x i8] c"  int x = %d (esperado 10)\0A\00"
@.str4 = private unnamed_addr constant [31 x i8] c"  float y = %f (esperado 5.5)\0A\00"
@.str5 = private unnamed_addr constant [28 x i8] c"  char c = %c (esperado A)\0A\00"
@.str6 = private unnamed_addr constant [48 x i8] c"  Vetor arr[0]=%d, arr[1]=%d (esperado 10, 20)\0A\00"
@.str7 = private unnamed_addr constant [36 x i8] c"\0A--- 2. Operadores Matematicos ---\0A\00"
@.str8 = private unnamed_addr constant [21 x i8] c"  Soma: 10 + 5 = %d\0A\00"
@.str9 = private unnamed_addr constant [26 x i8] c"  Subtracao: 10 - 5 = %d\0A\00"
@.str10 = private unnamed_addr constant [30 x i8] c"  Multiplicacao: 10 * 2 = %d\0A\00"
@.str11 = private unnamed_addr constant [24 x i8] c"  Divisao: 10 / 2 = %d\0A\00"
@.str12 = private unnamed_addr constant [37 x i8] c"  Modulo: 10 %% 3 = %d (esperado 1)\0A\00"
@.str13 = private unnamed_addr constant [49 x i8] c"\0A--- 3. Controle de Fluxo (If/Else/Logicos) ---\0A\00"
@.str14 = private unnamed_addr constant [49 x i8] c"  [SUCESSO] Teste E Logico (&&): x > 5 E x < 20\0A\00"
@.str15 = private unnamed_addr constant [55 x i8] c"  [SUCESSO] Teste OU Logico (||): x == 10 OU x == 999\0A\00"
@.str16 = private unnamed_addr constant [45 x i8] c"  [SUCESSO] Teste NAO Logico (!): !(x == 0)\0A\00"
@.str17 = private unnamed_addr constant [52 x i8] c"  [ERRO] Este print nao deveria aparecer (x != 10)\0A\00"
@.str18 = private unnamed_addr constant [41 x i8] c"  [SUCESSO] Teste Else: x eh igual a 10\0A\00"
@.str19 = private unnamed_addr constant [25 x i8] c"\0A--- 4. Switch Case ---\0A\00"
@.str20 = private unnamed_addr constant [27 x i8] c"  [ERRO] Entrou no case 1\0A\00"
@.str21 = private unnamed_addr constant [30 x i8] c"  [SUCESSO] Entrou no case 2\0A\00"
@.str22 = private unnamed_addr constant [28 x i8] c"  [ERRO] Entrou no default\0A\00"
@.str23 = private unnamed_addr constant [19 x i8] c"\0A--- 5. Loops ---\0A\00"
@.str24 = private unnamed_addr constant [16 x i8] c"  For (0 a 2):\0A\00"
@.str25 = private unnamed_addr constant [12 x i8] c"    i = %d\0A\00"
@.str26 = private unnamed_addr constant [27 x i8] c"  While (contagem 3 a 1):\0A\00"
@.str27 = private unnamed_addr constant [12 x i8] c"    w = %d\0A\00"
@.str28 = private unnamed_addr constant [29 x i8] c"  Do-While (executa 1 vez):\0A\00"
@.str29 = private unnamed_addr constant [12 x i8] c"    d = %d\0A\00"
@.str30 = private unnamed_addr constant [23 x i8] c"\0A--- 6. Ponteiros ---\0A\00"
@.str31 = private unnamed_addr constant [31 x i8] c"  Valor de x via ponteiro: %d\0A\00"
@.str32 = private unnamed_addr constant [63 x i8] c"  Alterando *ptr para 100. Novo valor de x: %d (esperado 100)\0A\00"
@.str33 = private unnamed_addr constant [21 x i8] c"\0A--- 7. Structs ---\0A\00"
@.str34 = private unnamed_addr constant [28 x i8] c"  Struct Ponto: x=%d, y=%d\0A\00"
@.str35 = private unnamed_addr constant [21 x i8] c"\0A--- 8. Funcoes ---\0A\00"
@.str36 = private unnamed_addr constant [43 x i8] c"  Chamada soma(30, 20) = %d (esperado 50)\0A\00"
@.str37 = private unnamed_addr constant [44 x i8] c"  Recursao Fatorial(5) = %d (esperado 120)\0A\00"
@.str38 = private unnamed_addr constant [34 x i8] c"\0A--- 9. Extras (IO e Define) ---\0A\00"
@.str39 = private unnamed_addr constant [30 x i8] c"  Teste de puts(): Ola Mundo!\00"
@.str40 = private unnamed_addr constant [43 x i8] c"  Constante via define: %d (esperado 100)\0A\00"
@.str41 = private unnamed_addr constant [44 x i8] c"Teste de scanf, digite um n\C3\BAmero inteiro:\0A\00"
@.str42 = private unnamed_addr constant [3 x i8] c"%d\00"
@.str43 = private unnamed_addr constant [16 x i8] c"Li o valor: %d\0A\00"
@.str44 = private unnamed_addr constant [21 x i8] c"\0A--- 10. Unions ---\0A\00"
@.str45 = private unnamed_addr constant [46 x i8] c"  [Union] Atribuido u.i = 42. Valor lido: %d\0A\00"
@.str46 = private unnamed_addr constant [48 x i8] c"  [Union] Atribuido u.f = 3.14. Valor lido: %f\0A\00"
@.str47 = private unnamed_addr constant [69 x i8] c"  [Union] Lendo u.i apos alterar u.f: %d (Deve ser diferente de 42)\0A\00"
@.str48 = private unnamed_addr constant [25 x i8] c"\0A=== FIM DOS TESTES ===\0A\00"
; --- External Declarations (stdio.h) ---
declare i32 @printf(i8*, ...)
declare i32 @scanf(i8*, ...)
declare i32 @puts(i8*)
declare i8* @gets(i8*)


define i32 @soma(i32 %arg0, i32 %arg1) {
entry:
  %a_ptr = alloca i32
  store i32 %arg0, i32* %a_ptr
  %b_ptr = alloca i32
  store i32 %arg1, i32* %b_ptr
  %t2 = load i32, i32* %a_ptr
  %t3 = load i32, i32* %b_ptr
  %t4 = add i32 %t2, %t3
  ret i32 %t4
}

define i32 @fatorial(i32 %arg0) {
entry:
  %n_ptr = alloca i32
  store i32 %arg0, i32* %n_ptr
  %t1 = load i32, i32* %n_ptr
  %t2 = icmp sle i32 %t1, 1
  %t3 = zext i1 %t2 to i32
  %t4 = icmp ne i32 %t3, 0
  br i1 %t4, label %L5, label %L7
L5:
  ret i32 1
  br label %L7
L7:
  %t8 = load i32, i32* %n_ptr
  %t9 = load i32, i32* %n_ptr
  %t10 = sub i32 %t9, 1
  %t11 = call i32 @fatorial(i32 %t10)
  %t12 = mul i32 %t8, %t11
  ret i32 %t12
}

define void @funcao_void() {
entry:
  %t0 = call i32 @puts(i8* getelementptr inbounds ([45 x i8], [45 x i8]* @.str0, i64 0, i64 0))
  ret void
}

define i32 @main() {
entry:
  %t0 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([42 x i8], [42 x i8]* @.str1, i64 0, i64 0))
  %t1 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([30 x i8], [30 x i8]* @.str2, i64 0, i64 0))
  %x_ptr = alloca i32
  store i32 10, i32* %x_ptr
  %y_ptr = alloca float
  store float 0x4016000000000000, float* %y_ptr
  %c_ptr = alloca i8
  store i8 65, i8* %c_ptr
  %t2 = load i32, i32* %x_ptr
  %t3 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([28 x i8], [28 x i8]* @.str3, i64 0, i64 0), i32 %t2)
  %t4 = load float, float* %y_ptr
  %t5 = fpext float %t4 to double
  %t6 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([31 x i8], [31 x i8]* @.str4, i64 0, i64 0), double %t5)
  %t7 = load i8, i8* %c_ptr
  %t8 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([28 x i8], [28 x i8]* @.str5, i64 0, i64 0), i8 %t7)
  %arr_ptr = alloca [5 x i32]
  %t9 = getelementptr inbounds [5 x i32], [5 x i32]* %arr_ptr, i32 0, i32 0
  store i32 10, i32* %t9
  %t10 = getelementptr inbounds [5 x i32], [5 x i32]* %arr_ptr, i32 0, i32 1
  store i32 20, i32* %t10
  %t11 = getelementptr inbounds [5 x i32], [5 x i32]* %arr_ptr, i32 0, i32 0
  %t12 = load i32, i32* %t11
  %t13 = getelementptr inbounds [5 x i32], [5 x i32]* %arr_ptr, i32 0, i32 1
  %t14 = load i32, i32* %t13
  %t15 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([48 x i8], [48 x i8]* @.str6, i64 0, i64 0), i32 %t12, i32 %t14)
  %t16 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([36 x i8], [36 x i8]* @.str7, i64 0, i64 0))
  %t17 = load i32, i32* %x_ptr
  %t18 = add i32 %t17, 5
  %t19 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([21 x i8], [21 x i8]* @.str8, i64 0, i64 0), i32 %t18)
  %t20 = load i32, i32* %x_ptr
  %t21 = sub i32 %t20, 5
  %t22 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([26 x i8], [26 x i8]* @.str9, i64 0, i64 0), i32 %t21)
  %t23 = load i32, i32* %x_ptr
  %t24 = mul i32 %t23, 2
  %t25 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([30 x i8], [30 x i8]* @.str10, i64 0, i64 0), i32 %t24)
  %t26 = load i32, i32* %x_ptr
  %t27 = sdiv i32 %t26, 2
  %t28 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([24 x i8], [24 x i8]* @.str11, i64 0, i64 0), i32 %t27)
  %t29 = load i32, i32* %x_ptr
  %t30 = srem i32 %t29, 3
  %t31 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([37 x i8], [37 x i8]* @.str12, i64 0, i64 0), i32 %t30)
  %t32 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([49 x i8], [49 x i8]* @.str13, i64 0, i64 0))
  %t33 = load i32, i32* %x_ptr
  %t34 = icmp sgt i32 %t33, 5
  %t35 = zext i1 %t34 to i32
  %t36 = load i32, i32* %x_ptr
  %t37 = icmp slt i32 %t36, 20
  %t38 = zext i1 %t37 to i32
  %t39 = icmp ne i32 %t35, 0
  %t40 = icmp ne i32 %t38, 0
  %t41 = and i1 %t39, %t40
  %t42 = zext i1 %t41 to i32
  %t43 = icmp ne i32 %t42, 0
  br i1 %t43, label %L44, label %L46
L44:
  %t47 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([49 x i8], [49 x i8]* @.str14, i64 0, i64 0))
  br label %L46
L46:
  %t48 = load i32, i32* %x_ptr
  %t49 = icmp eq i32 %t48, 10
  %t50 = zext i1 %t49 to i32
  %t51 = load i32, i32* %x_ptr
  %t52 = icmp eq i32 %t51, 999
  %t53 = zext i1 %t52 to i32
  %t54 = icmp ne i32 %t50, 0
  %t55 = icmp ne i32 %t53, 0
  %t56 = or i1 %t54, %t55
  %t57 = zext i1 %t56 to i32
  %t58 = icmp ne i32 %t57, 0
  br i1 %t58, label %L59, label %L61
L59:
  %t62 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([55 x i8], [55 x i8]* @.str15, i64 0, i64 0))
  br label %L61
L61:
  %t63 = load i32, i32* %x_ptr
  %t64 = icmp eq i32 %t63, 0
  %t65 = zext i1 %t64 to i32
  %t66 = icmp eq i32 %t65, 0
  %t67 = zext i1 %t66 to i32
  %t68 = icmp ne i32 %t67, 0
  br i1 %t68, label %L69, label %L71
L69:
  %t72 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([45 x i8], [45 x i8]* @.str16, i64 0, i64 0))
  br label %L71
L71:
  %t73 = load i32, i32* %x_ptr
  %t74 = icmp ne i32 %t73, 10
  %t75 = zext i1 %t74 to i32
  %t76 = icmp ne i32 %t75, 0
  br i1 %t76, label %L77, label %L78
L77:
  %t80 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([52 x i8], [52 x i8]* @.str17, i64 0, i64 0))
  br label %L79
L78:
  %t81 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([41 x i8], [41 x i8]* @.str18, i64 0, i64 0))
  br label %L79
L79:
  %t82 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([25 x i8], [25 x i8]* @.str19, i64 0, i64 0))
  %opcao_ptr = alloca i32
  store i32 2, i32* %opcao_ptr
  %t83 = load i32, i32* %opcao_ptr
  switch i32 %t83, label %L88 [
    i32 1, label %L86
    i32 2, label %L87
  ]
L86:
  %t89 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([27 x i8], [27 x i8]* @.str20, i64 0, i64 0))
  br label %L84
  br label %L84
L87:
  %t90 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([30 x i8], [30 x i8]* @.str21, i64 0, i64 0))
  br label %L84
  br label %L84
L88:
  %t91 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([28 x i8], [28 x i8]* @.str22, i64 0, i64 0))
  br label %L84
L84:
  %t92 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([19 x i8], [19 x i8]* @.str23, i64 0, i64 0))
  %t93 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([16 x i8], [16 x i8]* @.str24, i64 0, i64 0))
  %i_ptr = alloca i32
  store i32 0, i32* %i_ptr
  br label %L94
L94:
  %t98 = load i32, i32* %i_ptr
  %t99 = icmp slt i32 %t98, 3
  %t100 = zext i1 %t99 to i32
  %t101 = icmp ne i32 %t100, 0
  br i1 %t101, label %L95, label %L97
L95:
  %t102 = load i32, i32* %i_ptr
  %t103 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([12 x i8], [12 x i8]* @.str25, i64 0, i64 0), i32 %t102)
  br label %L96
L96:
  %t104 = load i32, i32* %i_ptr
  %t105 = add i32 %t104, 1
  store i32 %t105, i32* %i_ptr
  br label %L94
L97:
  %t106 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([27 x i8], [27 x i8]* @.str26, i64 0, i64 0))
  %w_ptr = alloca i32
  store i32 3, i32* %w_ptr
  br label %L107
L107:
  %t110 = load i32, i32* %w_ptr
  %t111 = icmp sgt i32 %t110, 0
  %t112 = zext i1 %t111 to i32
  %t113 = icmp ne i32 %t112, 0
  br i1 %t113, label %L108, label %L109
L108:
  %t114 = load i32, i32* %w_ptr
  %t115 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([12 x i8], [12 x i8]* @.str27, i64 0, i64 0), i32 %t114)
  %t116 = load i32, i32* %w_ptr
  %t117 = sub i32 %t116, 1
  store i32 %t117, i32* %w_ptr
  br label %L107
L109:
  %t118 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([29 x i8], [29 x i8]* @.str28, i64 0, i64 0))
  %d_ptr = alloca i32
  store i32 0, i32* %d_ptr
  br label %L119
L119:
  %t122 = load i32, i32* %d_ptr
  %t123 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([12 x i8], [12 x i8]* @.str29, i64 0, i64 0), i32 %t122)
  br label %L120
L120:
  %t124 = load i32, i32* %d_ptr
  %t125 = icmp sgt i32 %t124, 0
  %t126 = zext i1 %t125 to i32
  %t127 = icmp ne i32 %t126, 0
  br i1 %t127, label %L119, label %L121
L121:
  %t128 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([23 x i8], [23 x i8]* @.str30, i64 0, i64 0))
  %ptr_ptr = alloca i32*
  %t129 = load i32, i32* %x_ptr
  store i32* %x_ptr, i32** %ptr_ptr
  %t130 = load i32*, i32** %ptr_ptr
  %t131 = load i32, i32* %t130
  %t132 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([31 x i8], [31 x i8]* @.str31, i64 0, i64 0), i32 %t131)
  %t133 = load i32*, i32** %ptr_ptr
  store i32 100, i32* %t133
  %t134 = load i32, i32* %x_ptr
  %t135 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([63 x i8], [63 x i8]* @.str32, i64 0, i64 0), i32 %t134)
  %t136 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([21 x i8], [21 x i8]* @.str33, i64 0, i64 0))
  %p_ptr = alloca %struct.Ponto
  %t137 = getelementptr inbounds %struct.Ponto, %struct.Ponto* %p_ptr, i32 0, i32 0
  store i32 50, i32* %t137
  %t138 = getelementptr inbounds %struct.Ponto, %struct.Ponto* %p_ptr, i32 0, i32 1
  store i32 60, i32* %t138
  %t139 = getelementptr inbounds %struct.Ponto, %struct.Ponto* %p_ptr, i32 0, i32 0
  %t140 = load i32, i32* %t139
  %t141 = getelementptr inbounds %struct.Ponto, %struct.Ponto* %p_ptr, i32 0, i32 1
  %t142 = load i32, i32* %t141
  %t143 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([28 x i8], [28 x i8]* @.str34, i64 0, i64 0), i32 %t140, i32 %t142)
  %t144 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([21 x i8], [21 x i8]* @.str35, i64 0, i64 0))
  call void @funcao_void()
  %res_ptr = alloca i32
  %t145 = call i32 @soma(i32 30, i32 20)
  store i32 %t145, i32* %res_ptr
  %t146 = load i32, i32* %res_ptr
  %t147 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([43 x i8], [43 x i8]* @.str36, i64 0, i64 0), i32 %t146)
  %fat_ptr = alloca i32
  %t148 = call i32 @fatorial(i32 5)
  store i32 %t148, i32* %fat_ptr
  %t149 = load i32, i32* %fat_ptr
  %t150 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([44 x i8], [44 x i8]* @.str37, i64 0, i64 0), i32 %t149)
  %t151 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([34 x i8], [34 x i8]* @.str38, i64 0, i64 0))
  %t152 = call i32 @puts(i8* getelementptr inbounds ([30 x i8], [30 x i8]* @.str39, i64 0, i64 0))
  %t153 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([43 x i8], [43 x i8]* @.str40, i64 0, i64 0), i32 100)
  %t154 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([44 x i8], [44 x i8]* @.str41, i64 0, i64 0))
  %leitura_ptr = alloca i32
  %t155 = load i32, i32* %leitura_ptr
  %t156 = call i32 (i8*, ...) @scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.str42, i64 0, i64 0), i32* %leitura_ptr)
  %t157 = load i32, i32* %leitura_ptr
  %t158 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([16 x i8], [16 x i8]* @.str43, i64 0, i64 0), i32 %t157)
  %t159 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([21 x i8], [21 x i8]* @.str44, i64 0, i64 0))
  %u_ptr = alloca %union.Valor
  %t160 = bitcast %union.Valor* %u_ptr to i32*
  store i32 42, i32* %t160
  %t161 = bitcast %union.Valor* %u_ptr to i32*
  %t162 = load i32, i32* %t161
  %t163 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([46 x i8], [46 x i8]* @.str45, i64 0, i64 0), i32 %t162)
  %t164 = bitcast %union.Valor* %u_ptr to float*
  store float 0x40091EB860000000, float* %t164
  %t165 = bitcast %union.Valor* %u_ptr to float*
  %t166 = load float, float* %t165
  %t167 = fpext float %t166 to double
  %t168 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([48 x i8], [48 x i8]* @.str46, i64 0, i64 0), double %t167)
  %t169 = bitcast %union.Valor* %u_ptr to i32*
  %t170 = load i32, i32* %t169
  %t171 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([69 x i8], [69 x i8]* @.str47, i64 0, i64 0), i32 %t170)
  %t172 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([25 x i8], [25 x i8]* @.str48, i64 0, i64 0))
  ret i32 0
  ret i32 0
}
