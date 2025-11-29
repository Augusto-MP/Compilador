#include <stdio.h>

int main() {
    int x = 10;
    int *ptr;

    // 1. Teste SUCESSO: Endereço
    ptr = &x;
    printf("Sucesso: Ponteiro recebeu o endereco de x");

    // 2. Teste SUCESSO: Desreferência
    int y = *ptr;
    printf("Sucesso: Valor lido via ponteiro");

    // 3. Teste SUCESSO: Aninhamento
    int z = *(&y);
}