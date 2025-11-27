package compiler;

import gen.CBaseVisitor;
import gen.CParser;
import java.util.ArrayList; // Precisamos de Listas agora
import java.util.List;

public class MyCVisitor extends CBaseVisitor<Type> {

    private SymbolTable currentScope;
    private Symbol currentFunction;

    public MyCVisitor() {
        this.currentScope = new SymbolTable(null);
        this.currentFunction = null;
    }

    @Override
    public Type visitFunctionDeclaration(CParser.FunctionDeclarationContext ctx) {
        
        String functionName = ctx.ID().getText();
        String returnTypeName = ctx.type().getText();
        Type functionType = new Type(returnTypeName);
        
        // --- INÍCIO DA NOVA LÓGICA ---

        // 1. Criar a lista para guardar os tipos dos parâmetros
        List<Type> paramTypes = new ArrayList<>();
        
        // 2. Criar o novo escopo para esta função (ANTES de processar os parâmetros)
        SymbolTable functionScope = new SymbolTable(this.currentScope);

        // 3. Processar a lista de parâmetros (se ela existir)
        if (ctx.parameterList() != null) {
            for (CParser.ParameterContext paramCtx : ctx.parameterList().parameter()) {
                // Obter o tipo e nome do parâmetro
                String paramTypeName = paramCtx.type().getText();
                String paramName = paramCtx.ID().getText();
                Type paramType = new Type(paramTypeName);

                // Adicionar o tipo à nossa lista
                paramTypes.add(paramType);

                // Adicionar o parâmetro como um símbolo no NOVO escopo da função
                functionScope.put(paramName, new Symbol(paramName, paramType));
            }
        }
        
        // 4. Criar o símbolo da função usando o NOVO construtor
        Symbol functionSymbol = new Symbol(functionName, functionType, paramTypes);

        // 5. Adicionar a função ao escopo ATUAL (global)
        this.currentScope.put(functionName, functionSymbol);
        System.out.println("Registrando nova função no escopo: " + functionSymbol);

        // --- FIM DA NOVA LÓGICA ---

        // 6. Lógica de gestão de escopo (alterada para visitar apenas o bloco)
        Symbol oldFunction = this.currentFunction;
        this.currentFunction = functionSymbol;      
        this.currentScope = functionScope; // Entrar no escopo da função

        visit(ctx.block()); // 7. Visitamos apenas o BLOCO (e não os filhos todos)

        this.currentScope = this.currentScope.getParent();
        this.currentFunction = oldFunction;

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

        // 5. Registar o tipo da struct no escopo atual (global)
        //    Guardamos como um Símbolo para que 'visitDeclaration' o encontre depois.
        this.currentScope.put(typeName, new Symbol(typeName, structType));
        
        System.out.println("Definição de struct registada: " + typeName);

        return null;
    }

    @Override
    public Type visitDeclaration(CParser.DeclarationContext ctx) {
        String typeName = ctx.type().getText();
        String varName = ctx.ID().getText();
        boolean isArray = false;

        // Verifica se é array
        if (ctx.INT() != null && !ctx.INT().isEmpty()) {
            typeName += "[]";
            isArray = true;
        }
        
        Type varType;

        // NOVO: Tentar recuperar o tipo da Tabela de Símbolos
        // Se for 'struct Ponto', queremos o objeto Type que já tem os 'members' preenchidos.
        Symbol typeSymbol = this.currentScope.get(typeName);
        
        if (typeSymbol != null && !isArray) {
            // Encontrámos a definição da struct! Usamos esse Type especial.
            varType = typeSymbol.type;
        } else {
            // É um tipo primitivo (int) ou um array (que tratamos de forma simples por enquanto)
            varType = new Type(typeName);
        }

        Symbol varSymbol = new Symbol(varName, varType);
        this.currentScope.put(varName, varSymbol);
        
        System.out.println("   Registando variável: " + varName + " (" + typeName + ")");

        // Verificação de inicialização
        if (ctx.expr() != null) {
            Type exprType = visit(ctx.expr());
            if (exprType != null && !exprType.name.equals("error") && !varType.equals(exprType)) {
                System.err.println("ERRO SEMÂNTICO: Tipos incompatíveis. Não é possível atribuir " + exprType.name + " a " + varType.name);
            }
        }

        return null;
    }

    @Override
    public Type visitAssignment(CParser.AssignmentContext ctx) {
        // ... (código existente sem alterações)
        Type lhsType = visit(ctx.unaryExpr());
        Type rhsType = visit(ctx.expr());
        if (lhsType != null && rhsType != null && 
            !lhsType.name.equals("error") && !rhsType.name.equals("error")) 
        {
            if (!lhsType.equals(rhsType)) {
                System.err.println("ERRO SEMÂNTICO: Tipos incompatíveis. Não é possível atribuir " + rhsType.name + " a " + lhsType.name);
            }
        }
        return null;
    }

    @Override
    public Type visitPostfixExpr(CParser.PostfixExprContext ctx) {
        // A regra é: primary ('.' ID | '[' expr ']' | '(' argumentList? ')')*

        Type primaryType = visit(ctx.primary());
        String primaryName = ctx.primary().getText();

        // 1. Acesso a Array
        if (ctx.expr() != null && !ctx.expr().isEmpty()) {
            if (!primaryType.name.endsWith("[]")) {
                System.err.println("ERRO SEMÂNTICO: A variável '" + primaryName + "' não é um array e não pode ser acedida com [].");
                return new Type("error");
            }
            Type indexType = visit(ctx.expr(0));
            if (indexType != null && !indexType.name.equals("int") && !indexType.name.equals("error")) {
                System.err.println("ERRO SEMÂNTICO: O índice do array '" + primaryName + "' deve ser um 'int', mas é '" + indexType.name + "'.");
                return new Type("error");
            }
            String baseTypeName = primaryType.name.replace("[]", "");
            return new Type(baseTypeName);
        }

        // 2. Chamada de Função
        if (!ctx.argumentList().isEmpty()) {
            Symbol functionSymbol = this.currentScope.get(primaryName);
            if (functionSymbol == null) { return new Type("error"); } // Já reportado no visitPrimary

            if (!functionSymbol.isFunction()) {
                System.err.println("ERRO SEMÂNTICO: '" + primaryName + "' não é uma função e não pode ser chamada.");
                return new Type("error");
            }

            List<Type> expectedParams = functionSymbol.paramTypes;
            List<CParser.ExprContext> actualParams;
            CParser.ArgumentListContext argListCtx = ctx.argumentList(0);
            
            if (argListCtx != null) {
                actualParams = argListCtx.expr();
            } else {
                actualParams = new ArrayList<>();
            }

            if (expectedParams.size() != actualParams.size()) {
                System.err.println("ERRO SEMÂNTICO: A função '" + primaryName + "' espera " + expectedParams.size() + 
                                   " argumentos, mas recebeu " + actualParams.size() + ".");
                return new Type("error");
            }

            for (int i = 0; i < actualParams.size(); i++) {
                Type expectedType = expectedParams.get(i);
                Type actualType = visit(actualParams.get(i));
                if (actualType != null && !actualType.name.equals("error")) {
                    if (!expectedType.equals(actualType)) {
                        System.err.println("ERRO SEMÂNTICO: Argumento " + (i+1) + " da função '" + primaryName + 
                                           "' é inválido. Esperava '" + expectedType.name + "' mas recebeu '" + actualType.name + "'.");
                    }
                }
            }
            return functionSymbol.type;
        }

        // 3. NOVO: Acesso a Membro de Struct (ex: p.x)
        if (!ctx.ID().isEmpty()) {
            // Obtém o nome do campo (o ID após o ponto)
            String memberName = ctx.ID(0).getText();

            // Verifica se a variável principal é uma struct (tem tabela de membros?)
            if (primaryType.members == null) {
                System.err.println("ERRO SEMÂNTICO: A variável '" + primaryName + "' (" + primaryType.name + ") não é uma struct/union, não pode aceder a '" + memberName + "'.");
                return new Type("error");
            }

            // Verifica se o campo existe na struct
            Symbol member = primaryType.members.get(memberName);
            if (member == null) {
                System.err.println("ERRO SEMÂNTICO: O campo '" + memberName + "' não existe em '" + primaryType.name + "'.");
                return new Type("error");
            }

            // Retorna o tipo do campo (ex: int)
            return member.type;
        }

        return primaryType;
    }

    @Override
    public Type visitPrimary(CParser.PrimaryContext ctx) {
        // ... (código existente sem alterações)
        if (ctx.ID() != null) {
            String varName = ctx.ID().getText();
            Symbol symbol = this.currentScope.get(varName);
            if (symbol == null) {
                System.err.println("ERRO SEMÂNTICO: A variável '" + varName + "' não foi declarada.");
                return new Type("error");
            } else {
                System.out.println("   Variável '" + varName + "' encontrada no escopo. Tipo: " + symbol.type.name);
                return symbol.type;
            }
        } else if (ctx.INT() != null) {
            return new Type("int");
        } else if (ctx.FLOAT() != null) {
            return new Type("float");
        } else if (ctx.CHAR() != null) {
            return new Type("char");
        } else if (ctx.STRING() != null) {
            return new Type("string");
        }
        return visitChildren(ctx);
    }

    @Override
    public Type visitAdditiveExpr(CParser.AdditiveExprContext ctx) {
        // ... (código existente sem alterações)
        Type lhsType = visit(ctx.multiplicativeExpr(0));
        if (ctx.multiplicativeExpr().size() > 1) {
            Type rhsType = visit(ctx.multiplicativeExpr(1));
            if (lhsType.name.equals("error") || rhsType.name.equals("error")) {
                return new Type("error");
            }
            if (lhsType.equals(rhsType) && (lhsType.name.equals("int") || lhsType.name.equals("float"))) {
                return lhsType;
            } else {
                System.err.println("ERRO SEMÂNTICO: Tipos incompatíveis para operação aritmética: " + lhsType.name + " e " + rhsType.name);
                return new Type("error");
            }
        }
        return lhsType;
    }

    @Override
    public Type visitMultiplicativeExpr(CParser.MultiplicativeExprContext ctx) {
        // ... (código existente sem alterações)
        Type lhsType = visit(ctx.unaryExpr(0));
        if (ctx.unaryExpr().size() > 1) {
            Type rhsType = visit(ctx.unaryExpr(1));
            if (lhsType.name.equals("error") || rhsType.name.equals("error")) {
                return new Type("error");
            }
            if (lhsType.equals(rhsType) && (lhsType.name.equals("int") || lhsType.name.equals("float"))) {
                return lhsType;
            } else {
                System.err.println("ERRO SEMÂNTICO: Tipos incompatíveis para operação aritmética: " + lhsType.name + " e " + rhsType.name);
                return new Type("error");
            }
        }
        return lhsType;
    }

    @Override
    public Type visitRelationalExpr(CParser.RelationalExprContext ctx) {
        // ... (código existente sem alterações)
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
        // ... (código existente sem alterações)
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
        // ... (código existente sem alterações)
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
        // ... (código existente sem alterações)
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
        // ... (código existente sem alterações)
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
        // ... (código existente sem alterações)
        Type conditionType = visit(ctx.expr());
        if (conditionType != null && !conditionType.name.equals("int") && !conditionType.name.equals("error")) {
            System.err.println("ERRO SEMÂNTICO: A condição do 'while' deve ser do tipo 'int', mas é '" + conditionType.name + "'.");
        }
        visit(ctx.statement());
        return null;
    }

    @Override
    public Type visitDoWhileStatement(CParser.DoWhileStatementContext ctx) {
        // ... (código existente sem alterações)
        visit(ctx.statement());
        Type conditionType = visit(ctx.expr());
        if (conditionType != null && !conditionType.name.equals("int") && !conditionType.name.equals("error")) {
            System.err.println("ERRO SEMÂNTICO: A condição do 'do-while' deve ser do tipo 'int', mas é '" + conditionType.name + "'.");
        }
        return null;
    }

    @Override
    public Type visitForStatement(CParser.ForStatementContext ctx) {
        // ... (código existente sem alterações)
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
    public Type visitReturnStatement(CParser.ReturnStatementContext ctx) {
        // ... (código existente sem alterações)
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
}