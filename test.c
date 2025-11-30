#include <stdio.h>

// --- 8. Diretivas do Pré-processador ---
#define CONSTANTE 100

// --- 7. Estruturas e Unions ---
struct Ponto {
    int x;
    int y;
};

// Adicionado para teste: Union
union Valor {
    int i;
    float f;
};

// --- 6. Funções ---
int soma(int a, int b) {
    return a + b;
}

// Recursão (Fatorial)
int fatorial(int n) {
    if (n <= 1) return 1;
    return n * fatorial(n - 1);
}

void funcao_void() {
    puts("  [Funcao] Executou funcao void com sucesso.");
}

int main() {
    printf("=== INICIO DOS TESTES DO COMPILADOR ===\n\n");

    // --- 1. Declaração e Inicialização ---
    printf("--- 1. Variaveis e Tipos ---\n");
    int x = 10;
    float y = 5.5;
    char c = 'A';
    printf("  int x = %d (esperado 10)\n", x);
    printf("  float y = %f (esperado 5.5)\n", y);
    printf("  char c = %c (esperado A)\n", c);

    int arr[5];
    arr[0] = 10;
    arr[1] = 20;
    printf("  Vetor arr[0]=%d, arr[1]=%d (esperado 10, 20)\n", arr[0], arr[1]);


    // --- 4. Operadores ---
    printf("\n--- 2. Operadores Matematicos ---\n");
    printf("  Soma: 10 + 5 = %d\n", x + 5);
    printf("  Subtracao: 10 - 5 = %d\n", x - 5);
    printf("  Multiplicacao: 10 * 2 = %d\n", x * 2);
    printf("  Divisao: 10 / 2 = %d\n", x / 2);
    printf("  Modulo: 10 %% 3 = %d (esperado 1)\n", x % 3);


    // --- 2. Estruturas de Controle (If, Else, Lógicos) ---
    printf("\n--- 3. Controle de Fluxo (If/Else/Logicos) ---\n");
    if (x > 5 && x < 20) {
        printf("  [SUCESSO] Teste E Logico (&&): x > 5 E x < 20\n");
    }
    
    if (x == 10 || x == 999) {
        printf("  [SUCESSO] Teste OU Logico (||): x == 10 OU x == 999\n");
    }

    if (!(x == 0)) {
        printf("  [SUCESSO] Teste NAO Logico (!): !(x == 0)\n");
    }

    if (x != 10) {
        printf("  [ERRO] Este print nao deveria aparecer (x != 10)\n");
    } else {
        printf("  [SUCESSO] Teste Else: x eh igual a 10\n");
    }


    // --- 2. Estruturas de Controle (Switch) ---
    printf("\n--- 4. Switch Case ---\n");
    int opcao = 2;
    switch(opcao) {
        case 1:
            printf("  [ERRO] Entrou no case 1\n");
            break;
        case 2:
            printf("  [SUCESSO] Entrou no case 2\n");
            break;
        default:
            printf("  [ERRO] Entrou no default\n");
    }


    // --- 2. Estruturas de Controle (Loops) ---
    printf("\n--- 5. Loops ---\n");
    
    printf("  For (0 a 2):\n");
    int i;
    for(i = 0; i < 3; i = i + 1) {
        printf("    i = %d\n", i);
    }

    printf("  While (contagem 3 a 1):\n");
    int w = 3;
    while(w > 0) {
        printf("    w = %d\n", w);
        w = w - 1;
    }

    printf("  Do-While (executa 1 vez):\n");
    int d = 0;
    do {
        printf("    d = %d\n", d);
    } while(d > 0);


    // --- 5. Ponteiros ---
    printf("\n--- 6. Ponteiros ---\n");
    int *ptr;
    ptr = &x;
    printf("  Valor de x via ponteiro: %d\n", *ptr);
    *ptr = 100;
    printf("  Alterando *ptr para 100. Novo valor de x: %d (esperado 100)\n", x);


    // --- 7. Structs ---
    printf("\n--- 7. Structs ---\n");
    struct Ponto p;
    p.x = 50;
    p.y = 60;
    printf("  Struct Ponto: x=%d, y=%d\n", p.x, p.y);


    // --- 6. Funções e Recursão ---
    printf("\n--- 8. Funcoes ---\n");
    funcao_void();
    int res = soma(30, 20);
    printf("  Chamada soma(30, 20) = %d (esperado 50)\n", res);
    
    int fat = fatorial(5);
    printf("  Recursao Fatorial(5) = %d (esperado 120)\n", fat);


    // --- 3. Entrada e Saída & Pré-processador ---
    printf("\n--- 9. Extras (IO e Define) ---\n");
    puts("  Teste de puts(): Ola Mundo!");
    printf("  Constante via define: %d (esperado 100)\n", CONSTANTE);

    // --- 10. Unions (NOVO TESTE) ---
    printf("\n--- 10. Unions ---\n");
    union Valor u;
    
    // Teste 1: Atribui inteiro
    u.i = 42;
    printf("  [Union] Atribuido u.i = 42. Valor lido: %d\n", u.i);
    
    // Teste 2: Atribui float (deve sobrescrever a memória do int)
    u.f = 3.14;
    printf("  [Union] Atribuido u.f = 3.14. Valor lido: %f\n", u.f);
    
    // Teste 3: Verifica corrupção de memória (o int deve ter mudado)
    printf("  [Union] Lendo u.i apos alterar u.f: %d (Deve ser diferente de 42)\n", u.i);


    printf("\n=== FIM DOS TESTES ===\n");
    return 0;
}