%struct.Ponto = type { i32, i32 }
@.str0 = private unnamed_addr constant [16 x i8] c"Valor de a: %d\0A\00"
@.str1 = private unnamed_addr constant [24 x i8] c"Valor via ponteiro: %d\0A\00"
@.str2 = private unnamed_addr constant [28 x i8] c"Ponto original: x=%d, y=%d\0A\00"
@.str3 = private unnamed_addr constant [36 x i8] c"Ponto alterado: x=%d (esperado 10)\0A\00"
@.str4 = private unnamed_addr constant [33 x i8] c"Soma final: y=%d (esperado 210)\0A\00"
; --- Declarações Externas (stdio.h) ---
declare i32 @printf(i8*, ...)
declare i32 @scanf(i8*, ...)
declare i32 @puts(i8*)
declare i8* @gets(i8*)


define i32 @main() {
entry:
  %a_ptr = alloca i32
  store i32 10, i32* %a_ptr
  %ptr_ptr = alloca i32*
  %0 = load i32, i32* %a_ptr
  store i32* %a_ptr, i32** %ptr_ptr
  %1 = load i32, i32* %a_ptr
  %2 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([16 x i8], [16 x i8]* @.str0, i64 0, i64 0), i32 %1)
  %3 = load i32*, i32** %ptr_ptr
  %4 = load i32, i32* %3
  %5 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([24 x i8], [24 x i8]* @.str1, i64 0, i64 0), i32 %4)
  %p_ptr = alloca %struct.Ponto
  %6 = getelementptr inbounds %struct.Ponto, %struct.Ponto* %p_ptr, i32 0, i32 0
  store i32 100, i32* %6
  %7 = getelementptr inbounds %struct.Ponto, %struct.Ponto* %p_ptr, i32 0, i32 1
  store i32 200, i32* %7
  %8 = getelementptr inbounds %struct.Ponto, %struct.Ponto* %p_ptr, i32 0, i32 0
  %9 = load i32, i32* %8
  %10 = getelementptr inbounds %struct.Ponto, %struct.Ponto* %p_ptr, i32 0, i32 1
  %11 = load i32, i32* %10
  %12 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([28 x i8], [28 x i8]* @.str2, i64 0, i64 0), i32 %9, i32 %11)
  %13 = getelementptr inbounds %struct.Ponto, %struct.Ponto* %p_ptr, i32 0, i32 0
  %14 = load i32*, i32** %ptr_ptr
  %15 = load i32, i32* %14
  store i32 %15, i32* %13
  %16 = getelementptr inbounds %struct.Ponto, %struct.Ponto* %p_ptr, i32 0, i32 0
  %17 = load i32, i32* %16
  %18 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([36 x i8], [36 x i8]* @.str3, i64 0, i64 0), i32 %17)
  %19 = getelementptr inbounds %struct.Ponto, %struct.Ponto* %p_ptr, i32 0, i32 1
  %20 = getelementptr inbounds %struct.Ponto, %struct.Ponto* %p_ptr, i32 0, i32 0
  %21 = load i32, i32* %20
  %22 = getelementptr inbounds %struct.Ponto, %struct.Ponto* %p_ptr, i32 0, i32 1
  %23 = load i32, i32* %22
  %24 = add i32 %21, %23
  store i32 %24, i32* %19
  %25 = getelementptr inbounds %struct.Ponto, %struct.Ponto* %p_ptr, i32 0, i32 1
  %26 = load i32, i32* %25
  %27 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([33 x i8], [33 x i8]* @.str4, i64 0, i64 0), i32 %26)
  ret i32 0
}
