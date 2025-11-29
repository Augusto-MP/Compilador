# Compilador C para LLVM IR

Trabalho final da disciplina de Compiladores (2025).
Este projeto implementa um compilador para um subconjunto da linguagem C, gerando código intermediário LLVM IR (Low Level Virtual Machine Intermediate Representation).

## Tecnologias Utilizadas
- Linguagem de Desenvolvimento: Java 21
- Gerador de Parser: ANTLR 4.13.1
- Gerenciador de Dependências: Maven
- Linguagem Alvo: LLVM IR (.ll)

## Arquitetura do Compilador

O projeto foi dividido em quatro etapas principais:

1.  Análise Léxica e Sintática:
    - Definição da gramática (C.g4) para reconhecer estruturas da linguagem C (variáveis, funções, laços, estruturas, etc.).
    - Uso do ANTLR para gerar a árvore de derivação (Parse Tree).

2.  Tabela de Símbolos:
    - Implementação de escopos aninhados (Global -> Função -> Bloco).
    - Suporte a sombreamento de variáveis e verificação de existência.

3.  Análise Semântica:
    - Verificação de tipos incompatíveis (ex: atribuir string a int).
    - Validação de estruturas de controle (ex: switch aceita apenas inteiros).
    - Verificação de declaração e inicialização de variáveis.

4.  Geração de Código (Back-end):
    - Tradução da árvore sintática para instruções lineares LLVM IR.
    - Gerenciamento de memória com instruções alloca, load e store.
    - Implementação de ponteiros e chamadas de função externas (printf, scanf).

## Como Executar

### Pré-requisitos
- Java JDK 21 instalado.
- Maven instalado.

### Compilando o Projeto
Na raiz do projeto, execute:
```bash
mvn clean compile
