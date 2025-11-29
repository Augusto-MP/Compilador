package compiler;

import java.util.ArrayList; // Precisamos de Listas agora
import java.util.List;

import gen.CBaseVisitor;
import gen.CParser;

public class MyCVisitor extends CBaseVisitor<Type> {

// Tabela de símbolos (Mantemos! É essencial para saber variáveis e tipos)
    private SymbolTable currentScope;
    
    // --- NOVIDADE LLVM ---
    // Onde iremos escrever as instruções geradas (ex: "add i32 ...")
    private StringBuilder llvmCode = new StringBuilder(); 
    
    // Contador para os registos temporários do LLVM (%1, %2, %3...)
    private int tempCounter = 1; 

    private List<String> globalDefs = new ArrayList<>();
    
    private String createGlobalString(String content) {
        String name = "@.str" + (tempCounter++);
        int len = content.length() + 1; // +1 para o caracter nulo \00
        // Substitui \n por \0A (quebra de linha em Hex) para o LLVM não reclamar
        String fmt = content.replace("\n", "\\0A"); 
        
        String def = name + " = private unnamed_addr constant [" + len + " x i8] c\"" + fmt + "\\00\"";
        globalDefs.add(def);
        return name;
    }

    // Para saber em que função estamos (útil para o return)
    private Symbol currentFunction;

    public MyCVisitor() {
        this.currentScope = new SymbolTable(null);
        this.currentFunction = null;
    }
    
    // Método auxiliar para gerar o próximo nome temporário (ex: %1)
    private String nextTemp() {
        return "%" + (tempCounter++);
    }
    
    // Método auxiliar para escrever uma linha de código no buffer
    private void emit(String code) {
        llvmCode.append(code + "\n");
    }
    
    public String getLLVMCode() {
        StringBuilder sb = new StringBuilder();
        // Globais primeiro
        for (String s : globalDefs) {
            sb.append(s).append("\n");
        }
        // Depois o código das funções
        sb.append(llvmCode);
        return sb.toString();
    }

    @Override
    public Type visitFunctionDeclaration(CParser.FunctionDeclarationContext ctx) {
        String funcName = ctx.ID().getText();
        String returnType = toLLVMType(new Type(ctx.type().getText()));
        
        // 1. Preparar os Parâmetros para o Cabeçalho (ex: "i32 %0, float %1")
        StringBuilder paramsLLVM = new StringBuilder();
        List<String> paramNames = new ArrayList<>();
        List<String> paramTypesLLVM = new ArrayList<>();
        
        // Novo escopo para a função
        SymbolTable functionScope = new SymbolTable(this.currentScope);
        
        if (ctx.parameterList() != null) {
            int i = 0;
            for (CParser.ParameterContext param : ctx.parameterList().parameter()) {
                String type = toLLVMType(new Type(param.type().getText()));
                String name = param.ID().getText();
                
                if (i > 0) paramsLLVM.append(", ");
                paramsLLVM.append(type).append(" %").append(i);
                
                paramNames.add(name);
                paramTypesLLVM.add(type);
                
                // Guardamos no escopo, mas o valor (endereço) será definido logo a seguir
                // Usamos a variável 'type' que já contém "i32" ou "float" convertidos
                functionScope.put(name, new Symbol(name, new Type(type)));
                i++;
            }
        }

        // 2. Escrever o Cabeçalho da Função
        // Ex: define i32 @main(...) {
        emit("");
        emit("define " + returnType + " @" + funcName + "(" + paramsLLVM + ") {");
        emit("entry:"); // Label obrigatório de entrada

        // 3. Alocar memória para os parâmetros (para serem variáveis mutáveis)
        this.currentScope = functionScope; // Entra no escopo
        
        for (int i = 0; i < paramNames.size(); i++) {
            String name = paramNames.get(i);
            String type = paramTypesLLVM.get(i);
            String valArg = "%" + i;       // O valor que veio do argumento
            String ptrVar = "%" + name + "_ptr"; // O endereço na memória local
            
            // aloca espaço: %x_ptr = alloca i32
            emit("  " + ptrVar + " = alloca " + type);
            // guarda o valor inicial: store i32 %0, i32* %x_ptr
            emit("  store " + type + " " + valArg + ", " + type + "* " + ptrVar);
            
            // Atualiza a tabela: agora o símbolo 'x' sabe que mora em '%x_ptr'
            Symbol s = this.currentScope.get(name);
            s.value = ptrVar; // Guardamos o ENDEREÇO (registo) no campo value
        }

        // 4. Visitar o corpo da função
        visit(ctx.block());

        // 5. Garantir retorno para Void (segurança)
        if (returnType.equals("void")) {
            emit("  ret void");
        } else if (funcName.equals("main")) {
            // Se for main e o utilizador esqueceu o return, devolvemos 0
            emit("  ret i32 0");
        }
        
        emit("}"); // Fecha a função
        
        this.currentScope = this.currentScope.getParent(); // Sai do escopo
        return null;
    }
    
    @Override
    public Type visitStructDeclaration(CParser.StructDeclarationContext ctx) {
        String structName = ctx.ID().getText();
        String typeName = "struct " + structName;

        // 1. Criar o Tipo da Struct e a sua tabela de membros
        Type structType = new Type(typeName);
        structType.members = new SymbolTable(null); 

        // 2. Mudar temporariamente o escopo atual para a tabela de membros
        //    Assim, as declarações dentro da struct (int x;) ficam guardadas nela.
        SymbolTable previousScope = this.currentScope;
        this.currentScope = structType.members;

        // 3. Visitar todas as declarações dentro da struct
        for (CParser.DeclarationContext decl : ctx.declaration()) {
            visit(decl);
        }

        // 4. Restaurar o escopo anterior
        this.currentScope = previousScope;

        // 5. Registrar o tipo da struct no escopo atual (global)
        //    Guardamos como um Símbolo para que 'visitDeclaration' o encontre depois.
        this.currentScope.put(typeName, new Symbol(typeName, structType));
        
        System.out.println("Definição de struct registrada: " + typeName);

        return null;
    }

    @Override
    public Type visitUnionDeclaration(CParser.UnionDeclarationContext ctx) {
        String unionName = ctx.ID().getText();
        String typeName = "union " + unionName;

        // 1. Criar o Tipo da Union e a sua tabela de membros
        Type unionType = new Type(typeName);
        unionType.members = new SymbolTable(null); 

        // 2. Mudar temporariamente o escopo para capturar os membros na tabela da union
        SymbolTable previousScope = this.currentScope;
        this.currentScope = unionType.members;

        // 3. Visitar as declarações dentro da union
        for (CParser.DeclarationContext decl : ctx.declaration()) {
            visit(decl);
        }

        // 4. Restaurar o escopo anterior
        this.currentScope = previousScope;

        // 5. Registrar a union no escopo atual (global) para uso futuro
        this.currentScope.put(typeName, new Symbol(typeName, unionType));
        
        System.out.println("Definição de union registrada: " + typeName);

        return null;
    }
    
    @Override
    public Type visitDeclaration(CParser.DeclarationContext ctx) {
        // 1. Descobrir o nome e o tipo base
        String varName = ctx.ID().getText();
        String cTypeName = ctx.type().getText(); // ex: "int"
        
        // Converter para tipo LLVM (ex: "i32")
        String llvmType = toLLVMType(new Type(cTypeName));
        
        // 2. Verificar se é um Array (ex: int arr[5])
        boolean isArray = !ctx.INT().isEmpty();
        if (isArray) {
            String size = ctx.INT(0).getText();
            // Em LLVM, array é: [5 x i32]
            llvmType = "[" + size + " x " + llvmType + "]";
        }

        // 3. Gerar instrução de alocação de memória (alloca)
        // Criamos um nome único para o ponteiro: %nome_ptr
        String ptrVar = "%" + varName + "_ptr";
        emit("  " + ptrVar + " = alloca " + llvmType);

        // 4. Registrar na Tabela de Símbolos
        // O símbolo 'x' agora sabe que o seu endereço na memória é '%x_ptr'
        Type varType = new Type(cTypeName + (isArray ? "[]" : ""));
        Symbol s = new Symbol(varName, varType);
        s.value = ptrVar; // IMPORTANTE: Guardamos o endereço no campo value
        s.initialized = (ctx.expr() != null); 
        this.currentScope.put(varName, s);

        // 5. Tratar Inicialização (int x = 10;)
        if (ctx.expr() != null) {
            if (isArray) {
                // (Opcional) Inicialização de array é mais complexa, podemos ignorar por agora
                System.err.println("Aviso: Inicialização de array na declaração não suportada ainda.");
            } else {
                // Visitamos a expressão para obter o valor (ex: retorna um Type com value="10" ou value="%2")
                Type valType = visit(ctx.expr());
                String valReg = valType.value.toString();
                
                // Gera o store: store i32 %2, i32* %x_ptr
                emit("  store " + llvmType + " " + valReg + ", " + llvmType + "* " + ptrVar);
            }
        }
        
        return null;
    }
    
    @Override
    public Type visitAssignment(CParser.AssignmentContext ctx) {
        String varName = ctx.unaryExpr().getText();
        Symbol symbol = this.currentScope.get(varName);

        if (symbol != null && symbol.isConstant) {
            System.err.println("ERRO SEMÂNTICO: Tentativa de atribuir valor à constante '" + varName + "'.");
            return new Type("error");
        }

        // 1. Calcular o valor da expressão (RHS)
        // Isso vai gerar o código para calcular o valor e retornar o registo onde ele está (ex: %2)
        Type rhsType = visit(ctx.expr());
        String valReg = rhsType.value.toString();
        
        // 2. Obter o endereço da variável (LHS)
        String ptrVar = symbol.value.toString(); // O endereço %x_ptr que guardamos na declaração
        String llvmType = toLLVMType(symbol.type);

        // 3. Gerar o Store: store i32 %2, i32* %x_ptr
        emit("  store " + llvmType + " " + valReg + ", " + llvmType + "* " + ptrVar);
        
        // Em C, uma atribuição retorna o valor atribuído
        return rhsType;
    }
    
    @Override
    public Type visitPostfixExpr(CParser.PostfixExprContext ctx) {
        // Verifica se é uma chamada de função
        // A gramática é: primary ( '(' argumentList? ')' )* ...
        // Se a lista de argumentos não estiver vazia, assumimos que é uma chamada
        if (!ctx.argumentList().isEmpty()) {
            
            // O nome da função está no 'primary' (ex: printf)
            String funcName = ctx.primary().getText();
            
            // 1. Verificar se a função existe na Tabela
            Symbol funcSymbol = this.currentScope.get(funcName);
            if (funcSymbol == null) {
                System.err.println("ERRO: Função '" + funcName + "' não declarada.");
                return new Type("error");
            }

            // 2. Processar Argumentos
            StringBuilder argsLLVM = new StringBuilder();
            
            // ATENÇÃO: Pegamos o primeiro (0) conjunto de argumentos
            List<CParser.ExprContext> argsCtx = new ArrayList<>();
            if (ctx.argumentList(0).expr() != null) {
                argsCtx = ctx.argumentList(0).expr();
            }
            
            for (int i = 0; i < argsCtx.size(); i++) {
                Type argType = visit(argsCtx.get(i));
                String argVal = argType.value.toString();
                String llvmType = toLLVMType(argType);

                if (i > 0) argsLLVM.append(", ");

                if (argType.name.equals("string")) {
                    String globalVar = createGlobalString(argVal);
                    int size = argVal.length() + 1;
                    String strPtr = "getelementptr inbounds ([" + size + " x i8], [" + size + " x i8]* " + globalVar + ", i64 0, i64 0)";
                    argsLLVM.append("i8* ").append(strPtr);
                } else {
                    argsLLVM.append(llvmType).append(" ").append(argVal);
                }
            }

            // 3. Gerar o CALL
            String returnTypeLLVM = toLLVMType(funcSymbol.type);
            String callReg = "";
            
            if (!returnTypeLLVM.equals("void")) {
                callReg = nextTemp(); 
                emit("  " + callReg + " = call " + returnTypeLLVM + " (" + (funcName.equals("printf") ? "i8*, ..." : "") + ") @" + funcName + "(" + argsLLVM + ")");
            } else {
                emit("  call " + returnTypeLLVM + " @" + funcName + "(" + argsLLVM + ")");
            }
            
            return new Type(funcSymbol.type.name, callReg);
        }
        
        return visitChildren(ctx);
    }
    
    @Override
    public Type visitPrimary(CParser.PrimaryContext ctx) {
        if (ctx.ID() != null) {
            String varName = ctx.ID().getText();
            Symbol symbol = this.currentScope.get(varName);
            
            if (symbol == null) {
                System.err.println("ERRO SEMÂNTICO: A variável '" + varName + "' não foi declarada.");
                return new Type("error");
            }

            // LLVM: Carregar o valor da variável
            String ptrVar = symbol.value.toString();
            String llvmType = toLLVMType(symbol.type);
            String tempReg = nextTemp(); 
            
            emit("  " + tempReg + " = load " + llvmType + ", " + llvmType + "* " + ptrVar);
            
            return new Type(symbol.type.name, tempReg);
            
        } else if (ctx.INT() != null) {
            return new Type("int", ctx.INT().getText());
            
        } else if (ctx.FLOAT() != null) {
            return new Type("float", ctx.FLOAT().getText());
            
        } else if (ctx.STRING() != null) {
            return new Type("string", ctx.STRING().getText());
            
        } else if (ctx.expr() != null) {
            // --- CORREÇÃO AQUI ---
            // Se for ( expr ), visitamos a expressão interna e retornamos o resultado dela
            // em vez de deixar o visitChildren devolver null por causa do ')'
            return visit(ctx.expr());
        }
        
        return visitChildren(ctx);
    }
    
    @Override
    public Type visitAdditiveExpr(CParser.AdditiveExprContext ctx) {
        // Visita o primeiro operando (ex: '10' em "10 + 5")
        // Retorna um Type com value="%1" (se for variável) ou value="10" (se for literal)
        Type result = visit(ctx.multiplicativeExpr(0));

        // Percorre o resto da expressão (ex: ... + 5 - 2)
        for (int i = 1; i < ctx.multiplicativeExpr().size(); i++) {
            String op = ctx.getChild(2 * i - 1).getText(); // Pega o operador (+ ou -)
            Type next = visit(ctx.multiplicativeExpr(i));  // Pega o próximo operando

            // Decidir a instrução LLVM baseada no tipo
            String llvmOp = "";
            String typeCode = toLLVMType(result);
            
            if (result.name.equals("int")) {
                llvmOp = op.equals("+") ? "add" : "sub";
            } else if (result.name.equals("float")) {
                llvmOp = op.equals("+") ? "fadd" : "fsub";
            } else {
                // Simplificação: ignorar strings ou erros por agora
                return result;
            }

            // Gerar novo registo temporário para o resultado
            String tempReg = nextTemp();
            
            // Escreve: %3 = add i32 %1, %2
            emit("  " + tempReg + " = " + llvmOp + " " + typeCode + " " + result.value + ", " + next.value);
            
            // O resultado desta operação torna-se a base para a próxima (se houver)
            result = new Type(result.name, tempReg);
        }
        return result;
    }

    @Override
    public Type visitMultiplicativeExpr(CParser.MultiplicativeExprContext ctx) {
        Type result = visit(ctx.unaryExpr(0));

        for (int i = 1; i < ctx.unaryExpr().size(); i++) {
            String op = ctx.getChild(2 * i - 1).getText();
            Type next = visit(ctx.unaryExpr(i));

            String llvmOp = "";
            String typeCode = toLLVMType(result);

            if (result.name.equals("int")) {
                if (op.equals("*")) llvmOp = "mul";
                else if (op.equals("/")) llvmOp = "sdiv"; // sdiv = signed division
                else if (op.equals("%")) llvmOp = "srem"; // srem = signed remainder (módulo)
            } else if (result.name.equals("float")) {
                if (op.equals("*")) llvmOp = "fmul";
                else if (op.equals("/")) llvmOp = "fdiv";
                else llvmOp = "frem";
            }

            String tempReg = nextTemp();
            
            // Escreve: %4 = mul i32 %3, %2
            emit("  " + tempReg + " = " + llvmOp + " " + typeCode + " " + result.value + ", " + next.value);
            
            result = new Type(result.name, tempReg);
        }
        return result;
    }
    
    @Override
    public Type visitRelationalExpr(CParser.RelationalExprContext ctx) {
        Type lhsType = visit(ctx.additiveExpr(0));
        if (ctx.additiveExpr().size() > 1) {
            Type rhsType = visit(ctx.additiveExpr(1));
            if (lhsType.name.equals("error") || rhsType.name.equals("error")) {
                return new Type("error");
            }
            boolean isNumeric = (lhsType.name.equals("int") || lhsType.name.equals("float"));
            boolean areCompatible = isNumeric && lhsType.equals(rhsType);
            if (areCompatible) {
                return new Type("int");
            } else {
                System.err.println("ERRO SEMÂNTICO: Tipos incompatíveis para operação relacional: " + lhsType.name + " e " + rhsType.name);
                return new Type("error");
            }
        }
        return lhsType;
    }

    @Override
    public Type visitEqualityExpr(CParser.EqualityExprContext ctx) {
        Type lhsType = visit(ctx.relationalExpr(0));
        if (ctx.relationalExpr().size() > 1) {
            Type rhsType = visit(ctx.relationalExpr(1));
            if (lhsType.name.equals("error") || rhsType.name.equals("error")) {
                return new Type("error");
            }
            if (lhsType.equals(rhsType)) {
                return new Type("int");
            } else {
                System.err.println("ERRO SEMÂNTICO: Tipos incompatíveis para operação de igualdade: " + lhsType.name + " e " + rhsType.name);
                return new Type("error");
            }
        }
        return lhsType;
    }

    @Override
    public Type visitLogicalAndExpr(CParser.LogicalAndExprContext ctx) {
        Type lhsType = visit(ctx.equalityExpr(0));
        if (ctx.equalityExpr().size() > 1) {
            Type rhsType = visit(ctx.equalityExpr(1));
            if (lhsType.name.equals("error") || rhsType.name.equals("error")) {
                return new Type("error");
            }
            if (lhsType.name.equals("int") && rhsType.name.equals("int")) {
                return new Type("int");
            } else {
                System.err.println("ERRO SEMÂNTICO: Tipos incompatíveis para operação lógica '&&': " + lhsType.name + " e " + rhsType.name);
                return new Type("error");
            }
        }
        return lhsType;
    }

    @Override
    public Type visitLogicalOrExpr(CParser.LogicalOrExprContext ctx) {
        Type lhsType = visit(ctx.logicalAndExpr(0));
        if (ctx.logicalAndExpr().size() > 1) {
            Type rhsType = visit(ctx.logicalAndExpr(1));
            if (lhsType.name.equals("error") || rhsType.name.equals("error")) {
                return new Type("error");
            }
            if (lhsType.name.equals("int") && rhsType.name.equals("int")) {
                return new Type("int");
            } else {
                System.err.println("ERRO SEMÂNTICO: Tipos incompatíveis para operação lógica '||': " + lhsType.name + " e " + rhsType.name);
                return new Type("error");
            }
        }
        return lhsType;
    }

    @Override
    public Type visitIfStatement(CParser.IfStatementContext ctx) {
        Type conditionType = visit(ctx.expr());
        if (conditionType != null && !conditionType.name.equals("int") && !conditionType.name.equals("error")) {
            System.err.println("ERRO SEMÂNTICO: A condição do 'if' deve ser do tipo 'int', mas é '" + conditionType.name + "'.");
        }
        visit(ctx.statement(0)); // Visita o 'then'
        if (ctx.statement().size() > 1) {
            visit(ctx.statement(1)); // Visita o 'else'
        }
        return null;
    }

    @Override
    public Type visitWhileStatement(CParser.WhileStatementContext ctx) {
        Type conditionType = visit(ctx.expr());
        if (conditionType != null && !conditionType.name.equals("int") && !conditionType.name.equals("error")) {
            System.err.println("ERRO SEMÂNTICO: A condição do 'while' deve ser do tipo 'int', mas é '" + conditionType.name + "'.");
        }
        visit(ctx.statement());
        return null;
    }

    @Override
    public Type visitDoWhileStatement(CParser.DoWhileStatementContext ctx) {
        visit(ctx.statement());
        Type conditionType = visit(ctx.expr());
        if (conditionType != null && !conditionType.name.equals("int") && !conditionType.name.equals("error")) {
            System.err.println("ERRO SEMÂNTICO: A condição do 'do-while' deve ser do tipo 'int', mas é '" + conditionType.name + "'.");
        }
        return null;
    }

    @Override
    public Type visitForStatement(CParser.ForStatementContext ctx) {
        if (ctx.forInit() != null) {
            visit(ctx.forInit());
        }
        if (ctx.forCond() != null) {
            Type conditionType = visit(ctx.forCond());
            if (conditionType != null && !conditionType.name.equals("int") && !conditionType.name.equals("error")) {
                System.err.println("ERRO SEMÂNTICO: A condição do 'for' deve ser do tipo 'int', mas é '" + conditionType.name + "'.");
            }
        }
        if (ctx.forUpdate() != null) {
            visit(ctx.forUpdate());
        }
        visit(ctx.statement());
        return null;
    }

    @Override
    public Type visitSwitchStatement(CParser.SwitchStatementContext ctx) {
        // 1. Avalia a expressão do switch (ex: switch(x) -> avalia x)
        Type exprType = visit(ctx.expr());
        
        if (exprType != null && !exprType.name.equals("error")) {
            // Verifica se é um tipo inteiro válido
            if (!exprType.name.equals("int") && !exprType.name.equals("char")) {
                System.err.println("ERRO SEMÂNTICO: A expressão do 'switch' deve ser 'int' ou 'char', mas é '" + exprType.name + "'.");
            }
        }

        // 2. Visita todos os blocos 'case' e 'default' internos
        for (CParser.CaseBlockContext caseCtx : ctx.caseBlock()) {
            visit(caseCtx);
        }
        return null;
    }

    @Override
    public Type visitCaseBlock(CParser.CaseBlockContext ctx) {
        // Se existir uma expressão (é um 'case' e não 'default')
        if (ctx.expr() != null) {
            Type caseType = visit(ctx.expr());
            
            // O valor do case também deve ser compatível com inteiros
            if (caseType != null && !caseType.name.equals("error")) {
                if (!caseType.name.equals("int") && !caseType.name.equals("char")) {
                    System.err.println("ERRO SEMÂNTICO: O valor do 'case' deve ser 'int' ou 'char'.");
                }
            }
        }
        
        // Visita as instruções dentro deste case
        for (CParser.StatementContext stmt : ctx.statement()) {
            visit(stmt);
        }
        return null;
    }
    
    @Override
    public Type visitUnaryExpr(CParser.UnaryExprContext ctx) {
        // 1. Visitar o operando primeiro (isso gera o código de LOAD se for uma variável)
        // Ex: Para '&x', isso carrega o valor de x em %1 (o que vamos ignorar no caso do &)
        Type currentType = visit(ctx.postfixExpr());

        // 2. Processar operadores da direita para a esquerda
        for (int i = ctx.getChildCount() - 2; i >= 0; i--) {
            String operator = ctx.getChild(i).getText();
            
            if (operator.equals("&")) {
                // --- OPERADOR DE ENDEREÇO (&x) ---
                // O 'currentType' tem o valor carregado, mas para '&' queremos o ENDEREÇO.
                // Precisamos "espreitar" qual é a variável original para pegar o ponteiro dela.
                
                if (ctx.postfixExpr().primary() != null && ctx.postfixExpr().primary().ID() != null) {
                    String varName = ctx.postfixExpr().primary().ID().getText();
                    Symbol symbol = currentScope.get(varName);
                    
                    if (symbol != null) {
                        // AQUI ESTÁ O FIX: Pegamos o endereço (%x_ptr) que está guardado no Símbolo
                        String ptrVar = symbol.value.toString();
                        currentType = new Type(currentType.name + "*", ptrVar);
                    }
                } else {
                     System.err.println("Erro: '&' suportado apenas para variáveis simples neste compilador.");
                }
            } 
            else if (operator.equals("*")) {
                // --- OPERADOR DE DESREFERÊNCIA (*ptr) ---
                // O 'currentType' é um ponteiro (int*), e o value é o endereço (%1).
                // Queremos ler o valor que está lá dentro.
                
                if (currentType.name.endsWith("*")) {
                    // Remove o asterisco do tipo: int* -> int
                    String typeName = currentType.name.substring(0, currentType.name.length() - 1);
                    String llvmType = toLLVMType(new Type(typeName));
                    
                    String ptrReg = currentType.value.toString(); // O endereço
                    String tempReg = nextTemp(); // Novo registo para o valor lido
                    
                    // Gera: %2 = load i32, i32* %1
                    emit("  " + tempReg + " = load " + llvmType + ", " + llvmType + "* " + ptrReg);
                    
                    // Retorna o tipo base com o novo valor temporário
                    currentType = new Type(typeName, tempReg);
                } else {
                     System.err.println("Erro: Tentativa de usar '*' em algo que não é ponteiro.");
                }
            }
        }
        return currentType;
    }
    
    @Override
    public Type visitReturnStatement(CParser.ReturnStatementContext ctx) {
        Type expectedReturnType = this.currentFunction.type;
        Type actualReturnType;
        if (ctx.expr() != null) {
            actualReturnType = visit(ctx.expr());
        } else {
            actualReturnType = new Type("void");
        }
        if (actualReturnType != null && !actualReturnType.name.equals("error")) {
            if (!expectedReturnType.equals(actualReturnType)) {
                System.err.println("ERRO SEMÂNTICO: Tipo de retorno incompatível. A função '" + this.currentFunction.name + 
                                   "' espera '" + expectedReturnType.name + "' mas o 'return' é do tipo '" + actualReturnType.name + "'.");
            }
        }
        return null;
    }

    @Override
    public Type visitDefineDirective(CParser.DefineDirectiveContext ctx) {
        String name = ctx.ID().getText();
        
        Type valueType = visit(ctx.expr()); 
        
        if (valueType == null) {
            valueType = new Type("error");
        }

        Symbol s = new Symbol(name, valueType);
        s.isConstant = true;

        this.currentScope.put(name, s);

        System.out.println("   Define registrado (constante): " + name + " : " + valueType.name);
        
        return null;
    }

    @Override
    public Type visitIncludeDirective(CParser.IncludeDirectiveContext ctx) {
        // Verifica se é <stdio.h>
        if (ctx.libraryPath() != null && ctx.libraryPath().getText().equals("<stdio.h>")) {
            
            // --- PARTE 1: Mantemos a lógica antiga para a Análise Semântica ---
            // (Isso impede que o compilador dê erro dizendo que printf não existe)
            Type voidType = new Type("void");
            Type intType = new Type("int");
            Type stringType = new Type("string");

            List<Type> printfParams = new ArrayList<>();
            printfParams.add(stringType); 
            this.currentScope.put("printf", new Symbol("printf", voidType, printfParams));

            List<Type> scanfParams = new ArrayList<>();
            scanfParams.add(stringType);
            this.currentScope.put("scanf", new Symbol("scanf", intType, scanfParams));

            this.currentScope.put("gets", new Symbol("gets", stringType, new ArrayList<>()));
            
            List<Type> putsParams = new ArrayList<>();
            putsParams.add(stringType);
            this.currentScope.put("puts", new Symbol("puts", voidType, putsParams));

            
            // --- PARTE 2: NOVIDADE LLVM - Geração de Código ---
            // Escrevemos no arquivo as declarações reais que o LLVM precisa
            emit("; --- Declarações Externas (stdio.h) ---");
            emit("declare i32 @printf(i8*, ...)");
            emit("declare i32 @scanf(i8*, ...)");
            emit("declare i32 @puts(i8*)");
            emit("declare i8* @gets(i8*)");
            emit(""); // Linha em branco para organizar
            
            System.out.println("   <stdio.h> processado: Declarações LLVM geradas.");
        }
        return null;
    }    

    // Converter tipos do C (int, void, int*) para LLVM (i32, void, i32*)
    private String toLLVMType(Type t) {
        if (t == null) return "void";
        
        // --- NOVIDADE: Suporte a Ponteiros ---
        // Se o tipo terminar em '*', removemos o asterisco, convertemos a base e adicionamos o '*' de volta.
        // Ex: "int*" -> base "int" -> converte para "i32" -> resultado "i32*"
        if (t.name.endsWith("*")) {
            String baseTypeName = t.name.substring(0, t.name.length() - 1);
            return toLLVMType(new Type(baseTypeName)) + "*";
        }
        // -------------------------------------

        if (t.name.equals("int")) return "i32";
        if (t.name.equals("float")) return "float";
        if (t.name.equals("void")) return "void";
        if (t.name.equals("string") || t.name.equals("char*")) return "i8*";
        
        return "i32"; // Padrão de segurança
    }

}