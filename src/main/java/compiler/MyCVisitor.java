package compiler;

import java.util.ArrayList; // Precisamos de Listas agora
import java.util.List;

import gen.CBaseVisitor;
import gen.CParser;

public class MyCVisitor extends CBaseVisitor<Type> {

    private SymbolTable currentScope;
    private boolean isProcessingLHS = false;
    private StringBuilder llvmCode = new StringBuilder(); 
    private int tempCounter = 0;
    private int globalCounter = 0; 
    private List<String> globalDefs = new ArrayList<>();
    private Symbol currentFunction;

    private String createGlobalString(String content) {
        String name = "@.str" + (globalCounter++);
        
        // CORREÇÃO: Substituir o literal "\n" (2 chars) pelo código hexa do LLVM "\0A"
        String fmt = content.replace("\\n", "\\0A");
        
        // Calcular tamanho real: O tamanho original, menos 1 para cada \n que virou byte único
        int originalLen = content.length(); 
        int slashNCount = (content.length() - content.replace("\\n", "").length()) / 2;
        int realLen = originalLen - slashNCount + 1; // +1 para o \00
        
        String def = name + " = private unnamed_addr constant [" + realLen + " x i8] c\"" + fmt + "\\00\"";
        globalDefs.add(def);
        return name;
    }

    public MyCVisitor() {
        this.currentScope = new SymbolTable(null);
        this.currentFunction = null;
    }
    
    private String nextTemp() {
        return "%t" + (tempCounter++);
    }

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
        String returnTypeStr = ctx.type().getText();
        String returnTypeLLVM = toLLVMType(new Type(returnTypeStr));
        
        StringBuilder paramsLLVM = new StringBuilder();
        List<String> paramNames = new ArrayList<>();
        List<String> paramTypesLLVM = new ArrayList<>();
        
        SymbolTable functionScope = new SymbolTable(this.currentScope);
        
        int argCount = 0;
        if (ctx.parameterList() != null) {
            
            for (CParser.ParameterContext param : ctx.parameterList().parameter()) {
                String type = toLLVMType(new Type(param.type().getText()));
                String name = param.ID().getText();
                
                if (argCount > 0) paramsLLVM.append(", ");
                paramsLLVM.append(type).append(" %").append(argCount);
                
                paramNames.add(name);
                paramTypesLLVM.add(type);
                
                functionScope.put(name, new Symbol(name, new Type(param.type().getText())));
                argCount++;
            }
        }
        this.tempCounter = argCount;
        // --- CORREÇÃO: Definir a currentFunction para o return usar ---
        Symbol functionSymbol = new Symbol(funcName, new Type(returnTypeStr));
        Symbol oldFunction = this.currentFunction; // Salva a anterior (caso haja)
        this.currentFunction = functionSymbol;
        // -------------------------------------------------------------

        // 2. Escrever o Cabeçalho
        emit("");
        emit("define " + returnTypeLLVM + " @" + funcName + "(" + paramsLLVM + ") {");
        emit("entry:"); 

        // 3. Alocar memória para os parâmetros
        this.currentScope = functionScope; 
        
        for (int i = 0; i < paramNames.size(); i++) {
            String name = paramNames.get(i);
            String type = paramTypesLLVM.get(i);
            String valArg = "%" + i;
            String ptrVar = "%" + name + "_ptr";
            
            emit("  " + ptrVar + " = alloca " + type);
            emit("  store " + type + " " + valArg + ", " + type + "* " + ptrVar);
            
            Symbol s = this.currentScope.get(name);
            s.value = ptrVar; 
        }

        // 4. Visitar o corpo
        visit(ctx.block());

        // 5. Garantir retorno para Void ou Main
        if (returnTypeLLVM.equals("void")) {
            emit("  ret void");
        } 
        
        emit("}"); 
        
        this.currentScope = this.currentScope.getParent();
        this.currentFunction = oldFunction; // Restaura a anterior
        return null;
    }

    @Override
    public Type visitStructDeclaration(CParser.StructDeclarationContext ctx) {
        String structName = ctx.ID().getText();
        String typeName = "struct " + structName;

        // 1. Configurar Tabela de Símbolos (Mantém lógica existente)
        Type structType = new Type(typeName);
        structType.members = new SymbolTable(null); 
        SymbolTable previousScope = this.currentScope;
        this.currentScope = structType.members;

        // 2. Construir a lista de tipos do LLVM
        StringBuilder llvmBody = new StringBuilder();
        int index = 0;

        for (CParser.DeclarationContext decl : ctx.declaration()) {
            // Analisa o tipo do campo
            String fieldTypeName = decl.type().getText();
            Type fieldType = new Type(fieldTypeName); // Pode precisar de ajuste para arrays/ponteiros
            String llvmType = toLLVMType(fieldType);
            
            if (index > 0) llvmBody.append(", ");
            llvmBody.append(llvmType);

            // Regista o campo no escopo com o seu índice
            String fieldName = decl.ID().getText();
            Symbol fieldSymbol = new Symbol(fieldName, fieldType);
            fieldSymbol.memoryIndex = index++; // Guarda 0, 1, 2...
            
            this.currentScope.put(fieldName, fieldSymbol);
        }

        this.currentScope = previousScope;
        
        // 3. Emitir a definição global no LLVM
        // %struct.Ponto = type { i32, i32 }
        String definition = "%struct." + structName + " = type { " + llvmBody + " }";
        globalDefs.add(definition); // Adiciona ao topo do arquivo

        // Regista o tipo da struct no escopo global
        this.currentScope.put(typeName, new Symbol(typeName, structType));
        
        System.out.println("Definição LLVM registrada: " + definition);
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
        String varName = ctx.ID().getText();
        String cTypeName = getTypeName(ctx.type());
        
        Type typeObj = new Type(cTypeName); 
        String llvmType = toLLVMType(typeObj);
        
        boolean isArray = !ctx.INT().isEmpty();
        int sizeInt = 0; // Variável para guardar o tamanho capturado

        if (isArray) {
            String sizeStr = ctx.INT(0).getText();
            sizeInt = Integer.parseInt(sizeStr); // 1. Converte String para int
            llvmType = "[" + sizeStr + " x " + llvmType + "]";
        }

        String ptrVar = "%" + varName + "_ptr";
        emit("  " + ptrVar + " = alloca " + llvmType);

        Type varType = new Type(cTypeName + (isArray ? "[]" : ""));
        
        // 2. Salva o tamanho no objeto Type para uso posterior (ex: no visitPostfixExpr)
        if (isArray) {
            varType.arraySize = sizeInt;
        }
        
        Symbol typeSymbol = this.currentScope.get(cTypeName);
        if (typeSymbol != null && typeSymbol.type.members != null) {
            varType.members = typeSymbol.type.members;
        }

        Symbol s = new Symbol(varName, varType);
        s.value = ptrVar; // Guardamos o endereço %..._ptr
        s.initialized = (ctx.expr() != null); 
        this.currentScope.put(varName, s);

        if (ctx.expr() != null) {
            if (isArray) {
                System.err.println("Aviso: Inicialização de array na declaração não suportada ainda.");
            } else {
                Type valType = visit(ctx.expr());
                String valReg = valType.value.toString();
                emit("  store " + llvmType + " " + valReg + ", " + llvmType + "* " + ptrVar);
            }
        }
        
        return null;
    }
    
    
    @Override
    public Type visitAssignment(CParser.AssignmentContext ctx) {
        // 1. Avisar que estamos no Lado Esquerdo (LHS)
        // Isso sinaliza para o visitPrimary/Postfix retornarem o ENDEREÇO, não o valor.
        this.isProcessingLHS = true;
        Type lhsType = visit(ctx.unaryExpr());
        this.isProcessingLHS = false; // Desliga imediatamente
        
        if (lhsType == null || lhsType.name.equals("error")) {
            return new Type("error");
        }

        // lhsType.value contém o endereço de memória (ex: %x_ptr ou %elem_ptr)
        String ptrVar = lhsType.value.toString(); 
        String llvmType = toLLVMType(lhsType);

        // 2. Visitar o Lado Direito (RHS) para pegar o VALOR
        Type rhsType = visit(ctx.expr());
        String valReg = rhsType.value.toString();

        // 3. Gerar o Store
        emit("  store " + llvmType + " " + valReg + ", " + llvmType + "* " + ptrVar);
        
        return rhsType;
    }
    
    @Override
    public Type visitAssignmentNoSemi(CParser.AssignmentNoSemiContext ctx) {
        // Lógica idêntica ao visitAssignment, mas adaptada para o contexto sem ponto e vírgula
        
        // 1. Processa o Lado Esquerdo (LHS) para pegar o endereço
        this.isProcessingLHS = true;
        Type lhsType = visit(ctx.unaryExpr());
        this.isProcessingLHS = false;
        
        if (lhsType == null || lhsType.name.equals("error")) {
            return new Type("error");
        }

        String ptrVar = lhsType.value.toString(); 
        String llvmType = toLLVMType(lhsType);

        // 2. Processa o Lado Direito (RHS) para pegar o valor
        Type rhsType = visit(ctx.expr());
        String valReg = rhsType.value.toString();

        // 3. Gera o STORE para salvar o valor na variável
        emit("  store " + llvmType + " " + valReg + ", " + llvmType + "* " + ptrVar);
        
        return rhsType;
    }
    
    @Override
    public Type visitPostfixExpr(CParser.PostfixExprContext ctx) {
        
        if (ctx.expr() != null && !ctx.expr().isEmpty()) {
            Type indexType = visit(ctx.expr(0));
            String indexVal = indexType.value.toString();

            boolean oldLhs = this.isProcessingLHS;
            this.isProcessingLHS = true;
            Type arrayType = visit(ctx.primary());
            this.isProcessingLHS = oldLhs;

            String arrayPtr = arrayType.value.toString();
            String elemPtr = nextTemp();

            int size = arrayType.arraySize;

            String llvmArrayType = "[" + size + " x i32]";

            emit("  " + elemPtr + " = getelementptr inbounds " + llvmArrayType + ", " + llvmArrayType + "* " + arrayPtr + ", i32 0, i32 " + indexVal);

            if (oldLhs) {
                return new Type("int", elemPtr);
            } else {
                String val = nextTemp();
                emit("  " + val + " = load i32, i32* " + elemPtr);
                return new Type("int", val);
            }
        }
        
        if (!ctx.argumentList().isEmpty()) {
            String funcName = ctx.primary().getText();
            Symbol funcSymbol = this.currentScope.get(funcName);
            
            if (funcSymbol == null) {
                System.err.println("ERRO: Função '" + funcName + "' não declarada.");
                return new Type("error");
            }

            StringBuilder argsLLVM = new StringBuilder();
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
                    int slashNCount = (argVal.length() - argVal.replace("\\n", "").length()) / 2;
                    int size = argVal.length() - slashNCount + 1;
                    String strPtr = "getelementptr inbounds ([" + size + " x i8], [" + size + " x i8]* " + globalVar + ", i64 0, i64 0)";
                    argsLLVM.append("i8* ").append(strPtr);
                } else {
                    argsLLVM.append(llvmType).append(" ").append(argVal);
                }
            }

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

        if (!ctx.ID().isEmpty()) {
            String memberName = ctx.ID(0).getText();
            boolean originalLHS = this.isProcessingLHS;
            this.isProcessingLHS = true;
            Type primaryType = visit(ctx.primary());
            this.isProcessingLHS = originalLHS; // Restaura
            
            String primaryName = ctx.primary().getText();

            if (primaryType.members == null) {
                System.err.println("ERRO: '" + primaryName + "' não é struct.");
                return new Type("error");
            }
            
            Symbol member = primaryType.members.get(memberName);
            if (member == null) {
                System.err.println("ERRO: Campo '" + memberName + "' não existe.");
                return new Type("error");
            }

            String structPtr = primaryType.value.toString();
            String structLLVMType = toLLVMType(primaryType);
            int fieldIndex = member.memoryIndex;
            String ptrMember = nextTemp();
            
            emit("  " + ptrMember + " = getelementptr inbounds " + structLLVMType + ", " + structLLVMType + "* " + structPtr + ", i32 0, i32 " + fieldIndex);


            if (originalLHS) {
                return new Type(member.type.name, ptrMember);
            } 
            else {
                String fieldLLVMType = toLLVMType(member.type);
                String valReg = nextTemp();
                emit("  " + valReg + " = load " + fieldLLVMType + ", " + fieldLLVMType + "* " + ptrMember);
                return new Type(member.type.name, valReg);
            }
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

            // Recupera o endereço guardado no Símbolo
            String ptrVar = symbol.value.toString();
            String llvmType = toLLVMType(symbol.type);
            
            Type t; // Vamos preparar o objeto de retorno

            // Se estamos atribuindo (LHS), retornamos o endereço direto
            if (isProcessingLHS) {
                t = new Type(symbol.type.name, ptrVar);
            } 
            // Se estamos usando (RHS), fazemos o load
            else {
                String tempReg = nextTemp(); 
                emit("  " + tempReg + " = load " + llvmType + ", " + llvmType + "* " + ptrVar);
                t = new Type(symbol.type.name, tempReg);
            }


            t.members = symbol.type.members; 
            t.arraySize = symbol.type.arraySize;

            return t;
            
        } else if (ctx.INT() != null) {
            return new Type("int", ctx.INT().getText());
        } else if (ctx.FLOAT() != null) {
            return new Type("float", ctx.FLOAT().getText());
        } else if (ctx.STRING() != null) {
            String textoOriginal = ctx.STRING().getText();
            String textoLimpo = textoOriginal.substring(1, textoOriginal.length() - 1);
            return new Type("string", textoLimpo);
        } else if (ctx.expr() != null) {
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
        Type lhs = visit(ctx.additiveExpr(0));
        
        for (int i = 1; i < ctx.additiveExpr().size(); i++) {
            String op = ctx.getChild(2 * i - 1).getText(); 
            Type rhs = visit(ctx.additiveExpr(i));
            
            String tempReg = nextTemp();
            String instr = "";
            
            // Seleciona a instrução correta do LLVM
            if (op.equals("<")) instr = "icmp slt";
            else if (op.equals(">")) instr = "icmp sgt";
            else if (op.equals("<=")) instr = "icmp sle";
            else if (op.equals(">=")) instr = "icmp sge";

            // Gera: %3 = icmp slt i32 %1, %2
            emit("  " + tempReg + " = " + instr + " i32 " + lhs.value + ", " + rhs.value);

            // Converte o resultado de 1 bit (i1) para 32 bits (i32) para o C usar
            String zextReg = nextTemp();
            emit("  " + zextReg + " = zext i1 " + tempReg + " to i32");

            lhs = new Type("int", zextReg);
        }
        return lhs;
    }

    @Override
    public Type visitEqualityExpr(CParser.EqualityExprContext ctx) {
        Type lhs = visit(ctx.relationalExpr(0));

        for (int i = 1; i < ctx.relationalExpr().size(); i++) {
            String op = ctx.getChild(2 * i - 1).getText();
            Type rhs = visit(ctx.relationalExpr(i));

            String tempReg = nextTemp();
            String instr = op.equals("==") ? "icmp eq" : "icmp ne";

            emit("  " + tempReg + " = " + instr + " i32 " + lhs.value + ", " + rhs.value);

            String zextReg = nextTemp();
            emit("  " + zextReg + " = zext i1 " + tempReg + " to i32");

            lhs = new Type("int", zextReg);
        }
        return lhs;
    }

    @Override
    public Type visitLogicalAndExpr(CParser.LogicalAndExprContext ctx) {
        Type lhs = visit(ctx.equalityExpr(0));

        for (int i = 1; i < ctx.equalityExpr().size(); i++) {
            Type rhs = visit(ctx.equalityExpr(i));

            // Compara se LHS e RHS são diferentes de zero (verdadeiros)
            String t1 = nextTemp();
            emit("  " + t1 + " = icmp ne i32 " + lhs.value + ", 0");
            
            String t2 = nextTemp();
            emit("  " + t2 + " = icmp ne i32 " + rhs.value + ", 0");

            // Faz o AND lógico (bit a bit em i1)
            String tAnd = nextTemp();
            emit("  " + tAnd + " = and i1 " + t1 + ", " + t2);

            // Estende para i32
            String tFinal = nextTemp();
            emit("  " + tFinal + " = zext i1 " + tAnd + " to i32");

            lhs = new Type("int", tFinal);
        }
        return lhs;
    }

    @Override
    public Type visitLogicalOrExpr(CParser.LogicalOrExprContext ctx) {
        Type lhs = visit(ctx.logicalAndExpr(0));

        for (int i = 1; i < ctx.logicalAndExpr().size(); i++) {
            Type rhs = visit(ctx.logicalAndExpr(i));

            String t1 = nextTemp();
            emit("  " + t1 + " = icmp ne i32 " + lhs.value + ", 0");
            
            String t2 = nextTemp();
            emit("  " + t2 + " = icmp ne i32 " + rhs.value + ", 0");

            String tOr = nextTemp();
            emit("  " + tOr + " = or i1 " + t1 + ", " + t2);

            String tFinal = nextTemp();
            emit("  " + tFinal + " = zext i1 " + tOr + " to i32");

            lhs = new Type("int", tFinal);
        }
        return lhs;
    }

    @Override
    public Type visitIfStatement(CParser.IfStatementContext ctx) {
        // 1. Avalia a expressão da condição
        Type condType = visit(ctx.expr());
        String condReg = condType.value.toString();

        // 2. O LLVM precisa de um tipo i1 (booleano) para o desvio (br).
        // Como nosso C usa i32, comparamos se é != 0.
        String boolReg = nextTemp();
        emit("  " + boolReg + " = icmp ne i32 " + condReg + ", 0");

        // 3. Gera nomes únicos para os labels (usando o contador global para não repetir)
        String labelThen = "L" + (tempCounter++);
        String labelElse = "L" + (tempCounter++);
        String labelMerge = "L" + (tempCounter++); // O ponto de encontro após o if/else

        boolean hasElse = ctx.statement().size() > 1;
        
        // Se tiver else, pula para o labelElse, senão pula direto para o fim (Merge)
        String labelFalse = hasElse ? labelElse : labelMerge;

        // 4. Instrução de Branch (Desvio Condicional)
        emit("  br i1 " + boolReg + ", label %" + labelThen + ", label %" + labelFalse);

        // --- Bloco THEN ---
        emit(labelThen + ":");       // Escreve o rótulo no código
        visit(ctx.statement(0));     // Gera o código de dentro do if
        emit("  br label %" + labelMerge); // Pula para o fim (para não cair no else)

        // --- Bloco ELSE (Opcional) ---
        if (hasElse) {
            emit(labelElse + ":");
            visit(ctx.statement(1));
            emit("  br label %" + labelMerge);
        }

        // --- Bloco MERGE (Fim) ---
        emit(labelMerge + ":");

        return null;
    }

    @Override
    public Type visitWhileStatement(CParser.WhileStatementContext ctx) {
        String labelStart = "L" + (tempCounter++);
        String labelBody = "L" + (tempCounter++);
        String labelEnd = "L" + (tempCounter++);

        // 1. Pula para o início (onde a condição é testada)
        emit("  br label %" + labelStart);
        emit(labelStart + ":");

        // 2. Avalia a condição
        Type condType = visit(ctx.expr());
        String condReg = condType.value.toString();
        
        // Verifica se é verdadeiro (!= 0)
        String boolReg = nextTemp();
        emit("  " + boolReg + " = icmp ne i32 " + condReg + ", 0");

        // 3. Se true -> entra no corpo; Se false -> vai para o fim
        emit("  br i1 " + boolReg + ", label %" + labelBody + ", label %" + labelEnd);

        // 4. Corpo do While
        emit(labelBody + ":");
        visit(ctx.statement());
        emit("  br label %" + labelStart); // O PULO DO GATO: Volta para o início!

        // 5. Fim do loop
        emit(labelEnd + ":");

        return null;
    }

    @Override
    public Type visitDoWhileStatement(CParser.DoWhileStatementContext ctx) {
        String labelBody = "L" + (tempCounter++);
        String labelCond = "L" + (tempCounter++);
        String labelEnd = "L" + (tempCounter++);

        // 1. No do-while, pulamos direto para o corpo (sem testar antes)
        emit("  br label %" + labelBody);

        // 2. Corpo do Loop
        emit(labelBody + ":");
        visit(ctx.statement()); 
        emit("  br label %" + labelCond); // Após o corpo, vai para o teste

        // 3. Teste da Condição
        emit(labelCond + ":");
        Type condType = visit(ctx.expr());
        String condReg = condType.value.toString();
        
        // Verifica se é verdadeiro (!= 0)
        String boolReg = nextTemp();
        emit("  " + boolReg + " = icmp ne i32 " + condReg + ", 0");

        // 4. Se true -> volta para o corpo; Se false -> sai
        emit("  br i1 " + boolReg + ", label %" + labelBody + ", label %" + labelEnd);

        // 5. Rótulo de Fim
        emit(labelEnd + ":");

        return null;
    }

    @Override
    public Type visitForStatement(CParser.ForStatementContext ctx) {
        // 1. Inicialização (executa apenas uma vez antes de tudo)
        if (ctx.forInit() != null) {
            visit(ctx.forInit());
        }

        String labelCond = "L" + (tempCounter++);
        String labelBody = "L" + (tempCounter++);
        String labelUpdate = "L" + (tempCounter++);
        String labelEnd = "L" + (tempCounter++);

        emit("  br label %" + labelCond);
        
        // 2. Rótulo da Condição
        emit(labelCond + ":");
        if (ctx.forCond() != null) {
            Type condType = visit(ctx.forCond());
            String condReg = condType.value.toString();
            String boolReg = nextTemp();
            emit("  " + boolReg + " = icmp ne i32 " + condReg + ", 0");
            emit("  br i1 " + boolReg + ", label %" + labelBody + ", label %" + labelEnd);
        } else {
            // for(;;) sem condição é um loop infinito
            emit("  br label %" + labelBody);
        }

        // 3. Corpo do For
        emit(labelBody + ":");
        visit(ctx.statement());
        emit("  br label %" + labelUpdate); // Vai para o update, não para a condição

        // 4. Update (o incremento, ex: i++)
        emit(labelUpdate + ":");
        if (ctx.forUpdate() != null) {
            visit(ctx.forUpdate());
        }
        emit("  br label %" + labelCond); // Agora sim, volta para testar a condição

        // 5. Fim
        emit(labelEnd + ":");

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
        // Se houver uma expressão (ex: return 0; ou return x + 1;)
        if (ctx.expr() != null) {
            Type result = visit(ctx.expr());
            String valReg = result.value.toString();
            String typeCode = toLLVMType(result);
            
            // Gera: ret i32 %10
            emit("  ret " + typeCode + " " + valReg);
        } else {
            // Ex: return;
            emit("  ret void");
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
            this.currentScope.put("printf", new Symbol("printf", intType, printfParams));

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

    private String toLLVMType(Type t) {
        if (t == null) return "void";
        
        if (t.name.endsWith("*")) {
            String baseTypeName = t.name.substring(0, t.name.length() - 1);
            return toLLVMType(new Type(baseTypeName)) + "*";
        }

        if (t.name.equals("int")) return "i32";
        if (t.name.equals("float")) return "float";
        if (t.name.equals("void")) return "void";
        if (t.name.equals("string") || t.name.equals("char*")) return "i8*";
        
        // --- NOVIDADE: Structs ---
        if (t.name.startsWith("struct ")) {
            String structName = t.name.replace("struct ", "");
            return "%struct." + structName;
        }
        
        return "i32"; 
    }

    // Método auxiliar para garantir que "struct Ponto" tenha o espaço correto
    private String getTypeName(CParser.TypeContext ctx) {
        String text = ctx.baseType().getText();
        
        // Se for struct, forçamos o espaço: "struct" + " " + "Nome"
        if (ctx.baseType().getChild(0).getText().equals("struct")) {
            text = "struct " + ctx.baseType().ID().getText();
        }
        
        // Adiciona os ponteiros (*) se houver
        for (int i = 1; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i).getText().equals("*")) {
                text += "*";
            }
        }
        return text;
    }
}