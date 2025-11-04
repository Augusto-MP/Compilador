#include <stdio.h>

int main() {
    // Teste 1: SUCESSO (função 'int' a retornar 'int')
    return 0; 
}

void outraFuncao() {
    // Teste 2: SUCESSO (função 'void' a retornar 'void')
    return;
}

int funcaoErrada1() {
    // Teste 3: ERRO (função 'int' a tentar retornar 'float')
    return 5.5; 
}

void funcaoErrada2() {
    // Teste 4: ERRO (função 'void' a tentar retornar 'int')
    return 10;
}