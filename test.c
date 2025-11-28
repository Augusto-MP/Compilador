// Declaração com corpo para satisfazer a gramática
int printf(char *s) {
    return 0;
}

int main() {
    // 1. Teste SUCESSO: Inicialização de char* com string literal
    char *texto = "Ola Mundo";
    
    // 2. Teste SUCESSO: Chamada de função passando variável char*
    printf(texto);

    // 3. Teste SUCESSO: Passando literal string (agora deve funcionar!)
    printf("Teste direto");

    // 4. Teste ERRO: Atribuição incompatível (deve falhar)
    int x = "Isso nao deve funcionar";
}