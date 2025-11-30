@.str0 = private unnamed_addr constant [31 x i8] c"Testando switch com x = %d...\0A\00"
@.str1 = private unnamed_addr constant [8 x i8] c"Caso 1\0A\00"
@.str2 = private unnamed_addr constant [18 x i8] c"Caso 2 (Correto)\0A\00"
@.str3 = private unnamed_addr constant [9 x i8] c"Default\0A\00"
@.str4 = private unnamed_addr constant [16 x i8] c"Fim do switch.\0A\00"
; --- Declarações Externas (stdio.h) ---
declare i32 @printf(i8*, ...)
declare i32 @scanf(i8*, ...)
declare i32 @puts(i8*)
declare i8* @gets(i8*)


define i32 @main() {
entry:
  %x_ptr = alloca i32
  store i32 2, i32* %x_ptr
  %t0 = load i32, i32* %x_ptr
  %t1 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([31 x i8], [31 x i8]* @.str0, i64 0, i64 0), i32 %t0)
  %t2 = load i32, i32* %x_ptr
  switch i32 %t2, label %L7 [
    i32 1, label %L5
    i32 2, label %L6
  ]
L5:
  %t8 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([8 x i8], [8 x i8]* @.str1, i64 0, i64 0))
  br label %L3
  br label %L3
L6:
  %t9 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([18 x i8], [18 x i8]* @.str2, i64 0, i64 0))
  br label %L3
  br label %L3
L7:
  %t10 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([9 x i8], [9 x i8]* @.str3, i64 0, i64 0))
  br label %L3
L3:
  %t11 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([16 x i8], [16 x i8]* @.str4, i64 0, i64 0))
  ret i32 0
}
