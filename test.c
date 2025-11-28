#include <stdio.h>

struct Ponto {
    int x;
    int y;
};

int main() {
    struct Ponto p;
    int a;
    float b;

    // 1. Teste SUCESSO: Acesso correto e tipos compatíveis
    p.x = 10;
    a = p.y;

    // 2. Teste ERRO: Campo inexistente
    p.z = 20;

    // 3. Teste ERRO: Acesso em variável não-struct
    a.x = 10;
    
    // 4. Teste ERRO: Tipo incompatível na atribuição
    b = p.x; 
}