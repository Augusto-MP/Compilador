# Compilador C para LLVM IR

**Trabalho Final de Compiladores (2025)**

Este projeto implementa um compilador robusto para um subconjunto da linguagem C, capaz de gerar código intermediário **LLVM IR** (*Low Level Virtual Machine Intermediate Representation*). O código gerado pode ser executado diretamente pelo interpretador `lli` ou compilado para código de máquina via `clang`.

## Funcionalidades Suportadas

O compilador abrange grande parte da especificação da linguagem C, incluindo recursos avançados de gerenciamento de memória e tipos:

### 1. Tipos de Dados e Memória
* **Tipos Primitivos:** `int`, `float` (com precisão IEEE 754), `char`, `void`.
* **Structs:** Declaração e acesso a membros (layout sequencial).
* **Unions:** Implementação real com compartilhamento de memória (uso de `bitcast` no LLVM).
* **Ponteiros:** Suporte completo para referenciar (`&`), desreferenciar (`*`) e atribuição indireta (L-Value vs R-Value).
* **Vetores:** Declaração e acesso indexado.
* **Escopo:** Distinção automática entre variáveis globais (alocação estática) e locais (alocação na pilha via `alloca`).

### 2. Controle de Fluxo
* **Condicionais:** `if`, `else`.
* **Laços:** `for`, `while`, `do-while`.
* **Seleção:** `switch` / `case` / `default`.
* **Desvios:** `break`, `return`.

### 3. Funções e Pré-processador
* **Funções:** Declaração, chamadas, parâmetros e suporte a **recursão**.
* **Pré-processador:**
    * `#include <stdio.h>` (Simula a biblioteca padrão).
    * `#define CONSTANTE valor`.

### 4. Entrada e Saída (via stdio.h simulada)
* `printf`: Suporte a strings de formatação e argumentos variáveis (promoção automática de `float` para `double`).
* `scanf`: Leitura de dados via ponteiros.
* `puts`, `gets`.

---

## Tecnologias Utilizadas

* **Linguagem de Implementação:** Java 21.
* **Análise Léxica/Sintática:** ANTLR 4.13.1 (Padrão Visitor).
* **Gerenciamento de Dependências:** Maven.
* **Target:** LLVM IR (.ll).

---

## Arquitetura do Compilador

O projeto segue o pipeline clássico de compilação:

1.  **Frontend (ANTLR):**
    * Gramática `C.g4` define a estrutura léxica e sintática.
    * Gera a Árvore de Análise Sintática (Parse Tree).

2.  **Tabela de Símbolos:**
    * Gerencia escopos aninhados (Global -> Função -> Bloco).
    * Armazena metadados de tipos, ponteiros para registradores LLVM e definições de Structs/Unions.

3.  **Backend (Geração de Código - `MyCVisitor.java`):**
    * **Estratégia SSA:** Gera registradores temporários únicos (`%t0`, `%t1`...) para conformidade com a *Static Single Assignment* do LLVM.
    * **Tratamento de Floats:** Converte literais float para representação hexadecimal (ex: `0x4016000000000000` para `5.5`) para garantir precisão binária.
    * **Lógica L-Value/R-Value:** Diferencia quando carregar o valor de uma variável (`load`) ou usar seu endereço (`store`) em atribuições e ponteiros.

---

## Como Compilar o Projeto

Certifique-se de ter **Java JDK 21+** e **Maven** instalados.

Na raiz do projeto, execute:

```bash
mvn clean compile
## Como Executar

### Pré-requisitos
- Java JDK 21 instalado.
- Maven instalado.

### Compilando o Projeto
Na raiz do projeto, execute:
```bash
mvn clean compile
```

## Executando o Compilador

### Opção A: Windows 
Utilize o script `compilador.bat`:

```
DOS
.\compilador.bat <entrada.c> <saida.ll>
```

**Exemplo:**
```
DOS
.\compilador.bat test.c saida.ll
```

### Opção B: Direto via Maven
Caso não utilize Windows, pode executar diretamente pelo Maven:

```
Bash
mvn -q exec:java -Dexec.mainClass="Main.Main" -Dexec.args="<entrada.c> <saida.ll>"
```

**Exemplo:**
```
Bash
mvn -q exec:java -Dexec.mainClass="Main.Main" -Dexec.args="test.c saida.ll"
```

### Resultado
O resultado será um arquivo `.ll` (ex: `saida.ll`) contendo o código LLVM IR pronto para ser executado ou compilado por ferramentas LLVM (como `lli` ou `clang`).
