@.str0 = private unnamed_addr constant [23 x i8] c"Iniciando loop FOR...\0A\00"
@.str1 = private unnamed_addr constant [10 x i8] c"  i = %d\0A\00"
@.str2 = private unnamed_addr constant [38 x i8] c"Soma total do FOR (esperado 10): %d\0A\0A\00"
@.str3 = private unnamed_addr constant [25 x i8] c"Iniciando loop WHILE...\0A\00"
@.str4 = private unnamed_addr constant [16 x i8] c"  Contagem: %d\0A\00"
@.str5 = private unnamed_addr constant [36 x i8] c"Valor final de j (esperado 0): %d\0A\0A\00"
@.str6 = private unnamed_addr constant [28 x i8] c"Iniciando loop DO-WHILE...\0A\00"
@.str7 = private unnamed_addr constant [90 x i8] c"  Executou o corpo com k = %d (mesmo a condicao sendo falsa inicialmente se fosse while)\0A\00"
@.str8 = private unnamed_addr constant [17 x i8] c"Fim dos testes.\0A\00"
; --- Declarações Externas (stdio.h) ---
declare i32 @printf(i8*, ...)
declare i32 @scanf(i8*, ...)
declare i32 @puts(i8*)
declare i8* @gets(i8*)


define i32 @main() {
entry:
  %i_ptr = alloca i32
  %soma_ptr = alloca i32
  store i32 0, i32* %soma_ptr
  %t0 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([23 x i8], [23 x i8]* @.str0, i64 0, i64 0))
  %t1 = load i32, i32* %i_ptr
  br label %L2
L2:
  %t6 = load i32, i32* %i_ptr
  %t7 = icmp slt i32 %t6, 5
  %t8 = zext i1 %t7 to i32
  %t9 = icmp ne i32 %t8, 0
  br i1 %t9, label %L3, label %L5
L3:
  %t10 = load i32, i32* %i_ptr
  %t11 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([10 x i8], [10 x i8]* @.str1, i64 0, i64 0), i32 %t10)
  %t12 = load i32, i32* %soma_ptr
  %t13 = load i32, i32* %i_ptr
  %t14 = add i32 %t12, %t13
  store i32 %t14, i32* %soma_ptr
  br label %L4
L4:
  %t15 = load i32, i32* %i_ptr
  %t16 = load i32, i32* %i_ptr
  %t17 = add i32 %t16, 1
  br label %L2
L5:
  %t18 = load i32, i32* %soma_ptr
  %t19 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([38 x i8], [38 x i8]* @.str2, i64 0, i64 0), i32 %t18)
  %t20 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([25 x i8], [25 x i8]* @.str3, i64 0, i64 0))
  %j_ptr = alloca i32
  store i32 3, i32* %j_ptr
  br label %L21
L21:
  %t24 = load i32, i32* %j_ptr
  %t25 = icmp sgt i32 %t24, 0
  %t26 = zext i1 %t25 to i32
  %t27 = icmp ne i32 %t26, 0
  br i1 %t27, label %L22, label %L23
L22:
  %t28 = load i32, i32* %j_ptr
  %t29 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([16 x i8], [16 x i8]* @.str4, i64 0, i64 0), i32 %t28)
  %t30 = load i32, i32* %j_ptr
  %t31 = sub i32 %t30, 1
  store i32 %t31, i32* %j_ptr
  br label %L21
L23:
  %t32 = load i32, i32* %j_ptr
  %t33 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([36 x i8], [36 x i8]* @.str5, i64 0, i64 0), i32 %t32)
  %t34 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([28 x i8], [28 x i8]* @.str6, i64 0, i64 0))
  %k_ptr = alloca i32
  store i32 100, i32* %k_ptr
  br label %L35
L35:
  %t38 = load i32, i32* %k_ptr
  %t39 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([90 x i8], [90 x i8]* @.str7, i64 0, i64 0), i32 %t38)
  %t40 = load i32, i32* %k_ptr
  %t41 = add i32 %t40, 1
  store i32 %t41, i32* %k_ptr
  br label %L36
L36:
  %t42 = load i32, i32* %k_ptr
  %t43 = icmp slt i32 %t42, 5
  %t44 = zext i1 %t43 to i32
  %t45 = icmp ne i32 %t44, 0
  br i1 %t45, label %L35, label %L37
L37:
  %t46 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([17 x i8], [17 x i8]* @.str8, i64 0, i64 0))
  ret i32 0
}
