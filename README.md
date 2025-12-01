# Compilador C para LLVM IR

**Trabalho Final de Compiladores (2025)**
**Universidade Estadual do Norte do Paraná**

Este projeto implementa um compilador completo para um subconjunto da linguagem C, gerando código intermediário **LLVM IR** (*Low Level Virtual Machine Intermediate Representation*). O compilador foi projetado para gerar código compatível com a infraestrutura moderna do LLVM, permitindo tanto a interpretação via `lli` quanto a compilação para executáveis nativos via `clang`.

---

## 🚀 Funcionalidades Implementadas

O compilador suporta uma ampla gama de recursos da linguagem C, com destaque para o gerenciamento correto de memória e tipos:

### 1. Tipos de Dados e Memória
* **Tipos Primitivos:** `int`, `float` (padrão IEEE 754), `char`, `void`.
* **Ponteiros:** Suporte completo para referenciar (`&`), desreferenciar (`*`) e aritmética básica.
    * *Destaque:* Lógica robusta para diferenciar L-Values (endereço de escrita) de R-Values (valor de leitura) em operações como `*ptr = 100`.
* **Structs:** Tipos compostos com alinhamento sequencial de memória.
* **Unions:** Implementação real de compartilhamento de memória.
    * *Técnica:* Utiliza instruções `bitcast` do LLVM para permitir que múltiplos tipos acessem o mesmo endereço base.
* **Vetores:** Declaração e acesso indexado (ex: `arr[0]`).
* **Escopo:** Suporte a variáveis globais (alocação estática com `@var`) e locais (alocação na pilha com `%var = alloca`).

### 2. Controle de Fluxo
* **Condicionais:** `if`, `else` e operadores lógicos (`&&`, `||`, `!`).
* **Laços:** `for`, `while`, `do-while`.
* **Seleção:** `switch` / `case` / `default`.
* **Funções:** Declaração, chamadas com argumentos, retorno de valores e **recursividade** (ex: cálculo de Fatorial).

### 3. Entrada e Saída (IO)
Integração com a biblioteca padrão C (`stdio.h`) via declarações externas no LLVM:
* `printf`: Suporte a strings de formatação e promoção automática de tipos (ex: `float` para `double` em funções variádicas).
* `scanf`: Leitura de dados do terminal para variáveis via ponteiros.
* `puts`, `gets`.

### 4. Pré-processador
* `#define`: Substituição de constantes em tempo de compilação.
* `#include`: Suporte básico para inclusão de bibliotecas (simulado).

---

## 🛠️ Arquitetura Técnica

O projeto utiliza o padrão **Visitor** sobre a árvore sintática gerada pelo ANTLR para emitir o código LLVM.

1.  **Frontend (ANTLR 4):**
    * Gramática `C.g4` para análise léxica e sintática.
2.  **Tabela de Símbolos:**
    * Gerenciamento de escopos aninhados.
    * Resolução de tipos para Structs e Unions.
3.  **Backend (Geração de Código):**
    * **Static Single Assignment (SSA):** Geração de registradores temporários únicos (`%t1`, `%t2`...).
    * **Tratamento de Floats:** Conversão de literais para hexadecimal (ex: `0x40B00000` para `5.5`) para garantir precisão binária no LLVM.
    * **Type Casting:** Uso de `zext` (zero extension) para operações lógicas e `fpext` para chamadas de função variádicas.

---

## 📦 Como Executar

### Pré-requisitos
1.  **Java JDK 21+** e **Maven** instalados.
2.  **LLVM (Clang)** instalado e configurado no PATH do sistema.

### Passo 1: Compilar o Projeto
Na raiz do projeto, gere o arquivo `.jar` e compile as classes:
```
bash
mvn clean compile
```

### Passo 2: Gerar Código Intermediário (.ll)
Utilize o script facilitador para compilar seu arquivo C:
```
DOS
.\compilador.bat test.c saida.ll
```

### Passo 3: Criar Executável Nativo
Use o clang para compilar o arquivo .ll gerado em um executável (.exe). Nota: No Windows, pode ser necessário linkar as definições legadas de stdio.
```
PowerShell
clang saida.ll -o programa.exe -llegacy_stdio_definitions
```
### Passo 4: Rodar!
```
PowerShell
.\programa.exe
```
