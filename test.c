#include <stdio.h>

int main() {
    int x = 2;
    
    printf("Testando switch com x = %d...\n", x);

    switch(x) {
        case 1:
            printf("Caso 1\n");
            break;
        case 2:
            printf("Caso 2 (Correto)\n");
            break;
        default:
            printf("Default\n");
    }
    
    printf("Fim do switch.\n");
    return 0;
}