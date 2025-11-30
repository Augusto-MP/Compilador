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
@.str41 = private unnamed_addr constant [17 x i8] c"Teste de Scanf:\0A\00"
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
  %t5 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([31 x i8], [31 x i8]* @.str4, i64 0, i64 0), float %t4)
  %t6 = load i8, i8* %c_ptr
  %t7 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([28 x i8], [28 x i8]* @.str5, i64 0, i64 0), i8 %t6)
  %arr_ptr = alloca [5 x i32]
  %t8 = getelementptr inbounds [5 x i32], [5 x i32]* %arr_ptr, i32 0, i32 0
  store i32 10, i32* %t8
  %t9 = getelementptr inbounds [5 x i32], [5 x i32]* %arr_ptr, i32 0, i32 1
  store i32 20, i32* %t9
  %t10 = getelementptr inbounds [5 x i32], [5 x i32]* %arr_ptr, i32 0, i32 0
  %t11 = load i32, i32* %t10
  %t12 = getelementptr inbounds [5 x i32], [5 x i32]* %arr_ptr, i32 0, i32 1
  %t13 = load i32, i32* %t12
  %t14 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([48 x i8], [48 x i8]* @.str6, i64 0, i64 0), i32 %t11, i32 %t13)
  %t15 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([36 x i8], [36 x i8]* @.str7, i64 0, i64 0))
  %t16 = load i32, i32* %x_ptr
  %t17 = add i32 %t16, 5
  %t18 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([21 x i8], [21 x i8]* @.str8, i64 0, i64 0), i32 %t17)
  %t19 = load i32, i32* %x_ptr
  %t20 = sub i32 %t19, 5
  %t21 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([26 x i8], [26 x i8]* @.str9, i64 0, i64 0), i32 %t20)
  %t22 = load i32, i32* %x_ptr
  %t23 = mul i32 %t22, 2
  %t24 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([30 x i8], [30 x i8]* @.str10, i64 0, i64 0), i32 %t23)
  %t25 = load i32, i32* %x_ptr
  %t26 = sdiv i32 %t25, 2
  %t27 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([24 x i8], [24 x i8]* @.str11, i64 0, i64 0), i32 %t26)
  %t28 = load i32, i32* %x_ptr
  %t29 = srem i32 %t28, 3
  %t30 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([37 x i8], [37 x i8]* @.str12, i64 0, i64 0), i32 %t29)
  %t31 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([49 x i8], [49 x i8]* @.str13, i64 0, i64 0))
  %t32 = load i32, i32* %x_ptr
  %t33 = icmp sgt i32 %t32, 5
  %t34 = zext i1 %t33 to i32
  %t35 = load i32, i32* %x_ptr
  %t36 = icmp slt i32 %t35, 20
  %t37 = zext i1 %t36 to i32
  %t38 = icmp ne i32 %t34, 0
  %t39 = icmp ne i32 %t37, 0
  %t40 = and i1 %t38, %t39
  %t41 = zext i1 %t40 to i32
  %t42 = icmp ne i32 %t41, 0
  br i1 %t42, label %L43, label %L45
L43:
  %t46 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([49 x i8], [49 x i8]* @.str14, i64 0, i64 0))
  br label %L45
L45:
  %t47 = load i32, i32* %x_ptr
  %t48 = icmp eq i32 %t47, 10
  %t49 = zext i1 %t48 to i32
  %t50 = load i32, i32* %x_ptr
  %t51 = icmp eq i32 %t50, 999
  %t52 = zext i1 %t51 to i32
  %t53 = icmp ne i32 %t49, 0
  %t54 = icmp ne i32 %t52, 0
  %t55 = or i1 %t53, %t54
  %t56 = zext i1 %t55 to i32
  %t57 = icmp ne i32 %t56, 0
  br i1 %t57, label %L58, label %L60
L58:
  %t61 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([55 x i8], [55 x i8]* @.str15, i64 0, i64 0))
  br label %L60
L60:
  %t62 = load i32, i32* %x_ptr
  %t63 = icmp eq i32 %t62, 0
  %t64 = zext i1 %t63 to i32
  %t65 = icmp eq i32 %t64, 0
  %t66 = zext i1 %t65 to i32
  %t67 = icmp ne i32 %t66, 0
  br i1 %t67, label %L68, label %L70
L68:
  %t71 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([45 x i8], [45 x i8]* @.str16, i64 0, i64 0))
  br label %L70
L70:
  %t72 = load i32, i32* %x_ptr
  %t73 = icmp ne i32 %t72, 10
  %t74 = zext i1 %t73 to i32
  %t75 = icmp ne i32 %t74, 0
  br i1 %t75, label %L76, label %L77
L76:
  %t79 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([52 x i8], [52 x i8]* @.str17, i64 0, i64 0))
  br label %L78
L77:
  %t80 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([41 x i8], [41 x i8]* @.str18, i64 0, i64 0))
  br label %L78
L78:
  %t81 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([25 x i8], [25 x i8]* @.str19, i64 0, i64 0))
  %opcao_ptr = alloca i32
  store i32 2, i32* %opcao_ptr
  %t82 = load i32, i32* %opcao_ptr
  switch i32 %t82, label %L87 [
    i32 1, label %L85
    i32 2, label %L86
  ]
L85:
  %t88 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([27 x i8], [27 x i8]* @.str20, i64 0, i64 0))
  br label %L83
  br label %L83
L86:
  %t89 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([30 x i8], [30 x i8]* @.str21, i64 0, i64 0))
  br label %L83
  br label %L83
L87:
  %t90 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([28 x i8], [28 x i8]* @.str22, i64 0, i64 0))
  br label %L83
L83:
  %t91 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([19 x i8], [19 x i8]* @.str23, i64 0, i64 0))
  %t92 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([16 x i8], [16 x i8]* @.str24, i64 0, i64 0))
  %i_ptr = alloca i32
  store i32 0, i32* %i_ptr
  br label %L93
L93:
  %t97 = load i32, i32* %i_ptr
  %t98 = icmp slt i32 %t97, 3
  %t99 = zext i1 %t98 to i32
  %t100 = icmp ne i32 %t99, 0
  br i1 %t100, label %L94, label %L96
L94:
  %t101 = load i32, i32* %i_ptr
  %t102 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([12 x i8], [12 x i8]* @.str25, i64 0, i64 0), i32 %t101)
  br label %L95
L95:
  %t103 = load i32, i32* %i_ptr
  %t104 = add i32 %t103, 1
  store i32 %t104, i32* %i_ptr
  br label %L93
L96:
  %t105 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([27 x i8], [27 x i8]* @.str26, i64 0, i64 0))
  %w_ptr = alloca i32
  store i32 3, i32* %w_ptr
  br label %L106
L106:
  %t109 = load i32, i32* %w_ptr
  %t110 = icmp sgt i32 %t109, 0
  %t111 = zext i1 %t110 to i32
  %t112 = icmp ne i32 %t111, 0
  br i1 %t112, label %L107, label %L108
L107:
  %t113 = load i32, i32* %w_ptr
  %t114 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([12 x i8], [12 x i8]* @.str27, i64 0, i64 0), i32 %t113)
  %t115 = load i32, i32* %w_ptr
  %t116 = sub i32 %t115, 1
  store i32 %t116, i32* %w_ptr
  br label %L106
L108:
  %t117 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([29 x i8], [29 x i8]* @.str28, i64 0, i64 0))
  %d_ptr = alloca i32
  store i32 0, i32* %d_ptr
  br label %L118
L118:
  %t121 = load i32, i32* %d_ptr
  %t122 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([12 x i8], [12 x i8]* @.str29, i64 0, i64 0), i32 %t121)
  br label %L119
L119:
  %t123 = load i32, i32* %d_ptr
  %t124 = icmp sgt i32 %t123, 0
  %t125 = zext i1 %t124 to i32
  %t126 = icmp ne i32 %t125, 0
  br i1 %t126, label %L118, label %L120
L120:
  %t127 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([23 x i8], [23 x i8]* @.str30, i64 0, i64 0))
  %ptr_ptr = alloca i32*
  %t128 = load i32, i32* %x_ptr
  store i32* %x_ptr, i32** %ptr_ptr
  %t129 = load i32*, i32** %ptr_ptr
  %t130 = load i32, i32* %t129
  %t131 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([31 x i8], [31 x i8]* @.str31, i64 0, i64 0), i32 %t130)
  %t132 = load i32*, i32** %ptr_ptr
  store i32 100, i32* %t132
  %t133 = load i32, i32* %x_ptr
  %t134 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([63 x i8], [63 x i8]* @.str32, i64 0, i64 0), i32 %t133)
  %t135 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([21 x i8], [21 x i8]* @.str33, i64 0, i64 0))
  %p_ptr = alloca %struct.Ponto
  %t136 = getelementptr inbounds %struct.Ponto, %struct.Ponto* %p_ptr, i32 0, i32 0
  store i32 50, i32* %t136
  %t137 = getelementptr inbounds %struct.Ponto, %struct.Ponto* %p_ptr, i32 0, i32 1
  store i32 60, i32* %t137
  %t138 = getelementptr inbounds %struct.Ponto, %struct.Ponto* %p_ptr, i32 0, i32 0
  %t139 = load i32, i32* %t138
  %t140 = getelementptr inbounds %struct.Ponto, %struct.Ponto* %p_ptr, i32 0, i32 1
  %t141 = load i32, i32* %t140
  %t142 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([28 x i8], [28 x i8]* @.str34, i64 0, i64 0), i32 %t139, i32 %t141)
  %t143 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([21 x i8], [21 x i8]* @.str35, i64 0, i64 0))
  call void @funcao_void()
  %res_ptr = alloca i32
  %t144 = call i32 @soma(i32 30, i32 20)
  store i32 %t144, i32* %res_ptr
  %t145 = load i32, i32* %res_ptr
  %t146 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([43 x i8], [43 x i8]* @.str36, i64 0, i64 0), i32 %t145)
  %fat_ptr = alloca i32
  %t147 = call i32 @fatorial(i32 5)
  store i32 %t147, i32* %fat_ptr
  %t148 = load i32, i32* %fat_ptr
  %t149 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([44 x i8], [44 x i8]* @.str37, i64 0, i64 0), i32 %t148)
  %t150 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([34 x i8], [34 x i8]* @.str38, i64 0, i64 0))
  %t151 = call i32 @puts(i8* getelementptr inbounds ([30 x i8], [30 x i8]* @.str39, i64 0, i64 0))
  %t152 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([43 x i8], [43 x i8]* @.str40, i64 0, i64 0), i32 100)
  %t153 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([17 x i8], [17 x i8]* @.str41, i64 0, i64 0))
  %leitura_ptr = alloca i32
  %t154 = load i32, i32* %leitura_ptr
  %t155 = call i32 (i8*, ...) @scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.str42, i64 0, i64 0), i32* %leitura_ptr)
  %t156 = load i32, i32* %leitura_ptr
  %t157 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([16 x i8], [16 x i8]* @.str43, i64 0, i64 0), i32 %t156)
  %t158 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([21 x i8], [21 x i8]* @.str44, i64 0, i64 0))
  %u_ptr = alloca %union.Valor
  %t159 = bitcast %union.Valor* %u_ptr to i32*
  store i32 42, i32* %t159
  %t160 = bitcast %union.Valor* %u_ptr to i32*
  %t161 = load i32, i32* %t160
  %t162 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([46 x i8], [46 x i8]* @.str45, i64 0, i64 0), i32 %t161)
  %t163 = bitcast %union.Valor* %u_ptr to float*
  store float 0x40091EB860000000, float* %t163
  %t164 = bitcast %union.Valor* %u_ptr to float*
  %t165 = load float, float* %t164
  %t166 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([48 x i8], [48 x i8]* @.str46, i64 0, i64 0), float %t165)
  %t167 = bitcast %union.Valor* %u_ptr to i32*
  %t168 = load i32, i32* %t167
  %t169 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([69 x i8], [69 x i8]* @.str47, i64 0, i64 0), i32 %t168)
  %t170 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([25 x i8], [25 x i8]* @.str48, i64 0, i64 0))
  ret i32 0
  ret i32 0
}
