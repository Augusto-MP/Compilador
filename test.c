#include <stdio.h>

int main() {
    int x = 10;
    int *ptr;

    // 1. Teste SUCESSO: Operador de endereço (&)
    // 'ptr' espera um (int*) e '&x' retorna exatamente um (int*)
    ptr = &x;
    printf("Sucesso: Ponteiro recebeu o endereco de x");

    // 2. Teste SUCESSO: Operador de desreferência (*)
    // 'y' espera um (int) e '*ptr' acessa o valor apontado (int)
    int y = *ptr;
    printf("Sucesso: Valor lido via ponteiro");

    // 3. Teste SUCESSO: Aninhamento (& e *)
    // &y gera (int*), depois *(&y) volta para (int). Deve funcionar.
    int z = *(&y);

    // --- A PARTIR DAQUI SÃO ERROS PROPOSITADOS ---

    // 4. Teste ERRO: Atribuir valor (int) a ponteiro (int*)
    int *pErro = x; 

    // 5. Teste ERRO: Atribuir endereço (int*) a variável comum (int)
    int valErro = &x;

    // 6. Teste ERRO: Tentar usar pointer (*) em variável comum
    int w = *x;
}