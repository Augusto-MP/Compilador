#include <stdio.h>

int main() {
    int i;
    int soma;

    // -------------------------------------------------
    // 1. TESTE DO FOR
    // Objetivo: Somar números de 0 a 4.
    // Verifica: Inicialização, Condição e Incremento.
    // -------------------------------------------------
    soma = 0;
    printf("Iniciando loop FOR...\n");
    
    for (i = 0; i < 5; i = i + 1) {
        printf("  i = %d\n", i);
        soma = soma + i;
    }
    printf("Soma total do FOR (esperado 10): %d\n\n", soma);


    // -------------------------------------------------
    // 2. TESTE DO WHILE
    // Objetivo: Contagem regressiva de 3 a 1.
    // Verifica: Teste da condição ANTES de executar.
    // -------------------------------------------------
    printf("Iniciando loop WHILE...\n");
    int j = 3;
    
    while (j > 0) {
        printf("  Contagem: %d\n", j);
        j = j - 1;
    }
    printf("Valor final de j (esperado 0): %d\n\n", j);


    // -------------------------------------------------
    // 3. TESTE DO DO-WHILE
    // Objetivo: Executar pelo menos uma vez.
    // Verifica: Execução do corpo e depois o teste.
    // -------------------------------------------------
    printf("Iniciando loop DO-WHILE...\n");
    int k = 100; // Valor inicial que já falha na condição (k < 5)
    
    do {
        printf("  Executou o corpo com k = %d (mesmo a condicao sendo falsa inicialmente se fosse while)\n", k);
        k = k + 1;
    } while (k < 5); // Condição falsa imediatamente, mas deve ter rodado 1 vez
    
    printf("Fim dos testes.\n");

    return 0;
}