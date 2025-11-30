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
  %0 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([23 x i8], [23 x i8]* @.str0, i64 0, i64 0))
  %1 = load i32, i32* %i_ptr
  br label %L2
L2:
  %6 = load i32, i32* %i_ptr
  %7 = icmp slt i32 %6, 5
  %8 = zext i1 %7 to i32
  %9 = icmp ne i32 %8, 0
  br i1 %9, label %L3, label %L5
L3:
  %10 = load i32, i32* %i_ptr
  %11 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([10 x i8], [10 x i8]* @.str1, i64 0, i64 0), i32 %10)
  %12 = load i32, i32* %soma_ptr
  %13 = load i32, i32* %i_ptr
  %14 = add i32 %12, %13
  store i32 %14, i32* %soma_ptr
  br label %L4
L4:
  %15 = load i32, i32* %i_ptr
  %16 = load i32, i32* %i_ptr
  %17 = add i32 %16, 1
  br label %L2
L5:
  %18 = load i32, i32* %soma_ptr
  %19 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([38 x i8], [38 x i8]* @.str2, i64 0, i64 0), i32 %18)
  %20 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([25 x i8], [25 x i8]* @.str3, i64 0, i64 0))
  %j_ptr = alloca i32
  store i32 3, i32* %j_ptr
  br label %L21
L21:
  %24 = load i32, i32* %j_ptr
  %25 = icmp sgt i32 %24, 0
  %26 = zext i1 %25 to i32
  %27 = icmp ne i32 %26, 0
  br i1 %27, label %L22, label %L23
L22:
  %28 = load i32, i32* %j_ptr
  %29 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([16 x i8], [16 x i8]* @.str4, i64 0, i64 0), i32 %28)
  %30 = load i32, i32* %j_ptr
  %31 = sub i32 %30, 1
  store i32 %31, i32* %j_ptr
  br label %L21
L23:
  %32 = load i32, i32* %j_ptr
  %33 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([36 x i8], [36 x i8]* @.str5, i64 0, i64 0), i32 %32)
  %34 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([28 x i8], [28 x i8]* @.str6, i64 0, i64 0))
  %k_ptr = alloca i32
  store i32 100, i32* %k_ptr
  br label %L35
L35:
  %38 = load i32, i32* %k_ptr
  %39 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([90 x i8], [90 x i8]* @.str7, i64 0, i64 0), i32 %38)
  %40 = load i32, i32* %k_ptr
  %41 = add i32 %40, 1
  store i32 %41, i32* %k_ptr
  br label %L36
L36:
  %42 = load i32, i32* %k_ptr
  %43 = icmp slt i32 %42, 5
  %44 = zext i1 %43 to i32
  %45 = icmp ne i32 %44, 0
  br i1 %45, label %L35, label %L37
L37:
  %46 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([17 x i8], [17 x i8]* @.str8, i64 0, i64 0))
  ret i32 0
}
