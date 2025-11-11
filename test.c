#include <stdio.h>

int main() {
    int arr[5];
    int x = 10;
    float y = 0.0;
    int w;

    // 1. Teste de SUCESSO: Acesso a array com índice int
    w = arr[0];

    // 2. Teste de ERRO: Acesso a não-array
    w = x[0];

    // 3. Teste de ERRO: Acesso a array com índice float
    w = arr[y];
}

void outraFuncao() {}