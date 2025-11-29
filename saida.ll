@.str2 = private unnamed_addr constant [44 x i8] c""Sucesso: Ponteiro recebeu o endereco de x"\00"
@.str5 = private unnamed_addr constant [35 x i8] c""Sucesso: Valor lido via ponteiro"\00"
; --- Declarações Externas (stdio.h) ---
declare i32 @printf(i8*, ...)
declare i32 @scanf(i8*, ...)
declare i32 @puts(i8*)
declare i8* @gets(i8*)


define i32 @main() {
entry:
  %x_ptr = alloca i32
  store i32 10, i32* %x_ptr
  %ptr_ptr = alloca i32*
  %1 = load i32, i32* %x_ptr
  store i32* %x_ptr, i32** %ptr_ptr
  call void @printf(i8* getelementptr inbounds ([44 x i8], [44 x i8]* @.str2, i64 0, i64 0))
  %y_ptr = alloca i32
  %3 = load i32*, i32** %ptr_ptr
  %4 = load i32, i32* %3
  store i32 %4, i32* %y_ptr
  call void @printf(i8* getelementptr inbounds ([35 x i8], [35 x i8]* @.str5, i64 0, i64 0))
  %z_ptr = alloca i32
  %6 = load i32, i32* %y_ptr
  %7 = load i32, i32* %y_ptr
  store i32 %7, i32* %z_ptr
  ret i32 0
}
