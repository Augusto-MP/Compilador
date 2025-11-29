#include <stdio.h>

struct Ponto {
    int x;
    int y;
};

int main() {
    // 1. Teste de Variáveis e Ponteiros
    int a = 10;
    int *ptr;
    ptr = &a;
    
    printf("Valor de a: %d\n", a);
    printf("Valor via ponteiro: %d\n", *ptr);

    // 2. Teste de Structs
    struct Ponto p;
    p.x = 100;
    p.y = 200;

    printf("Ponto original: x=%d, y=%d\n", p.x, p.y);

    // 3. Teste de Ponteiro para Struct (Atribuição Indireta)
    // Aqui testamos se o compilador calcula bem os endereços
    p.x = *ptr; // p.x recebe 10
    
    printf("Ponto alterado: x=%d (esperado 10)\n", p.x);

    // 4. Teste de Matemática com Struct
    p.y = p.x + p.y; // 10 + 200 = 210
    
    printf("Soma final: y=%d (esperado 210)\n", p.y);
    
    return 0;
}