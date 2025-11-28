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
        // MUDANÇA AQUI
        String returnTypeName = getTypeName(ctx.type());
        Type functionType = new Type(returnTypeName);
        
        List<Type> paramTypes = new ArrayList<>();
        SymbolTable functionScope = new SymbolTable(this.currentScope);

        if (ctx.parameterList() != null) {
            for (CParser.ParameterContext paramCtx : ctx.parameterList().parameter()) {
                // MUDANÇA AQUI TAMBÉM
                String paramTypeName = getTypeName(paramCtx.type());
                String paramName = paramCtx.ID().getText();
                Type paramType = new Type(paramTypeName);

                paramTypes.add(paramType);
                functionScope.put(paramName, new Symbol(paramName, paramType));
            }
        }
        
        Symbol functionSymbol = new Symbol(functionName, functionType, paramTypes);
        this.currentScope.put(functionName, functionSymbol);
        System.out.println("Registrando nova função no escopo: " + functionSymbol);

        Symbol oldFunction = this.currentFunction;
        this.currentFunction = functionSymbol;      
        this.currentScope = functionScope;

        visit(ctx.block());

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
        // MUDANÇA AQUI: Usar getTypeName em vez de getText
        String typeName = getTypeName(ctx.type());
        String varName = ctx.ID().getText();
        boolean isArray = false;

        if (ctx.INT() != null && !ctx.INT().isEmpty()) {
            typeName += "[]";
            isArray = true;
        }
        
        Type varType;
        Symbol typeSymbol = this.currentScope.get(typeName);
        
        if (typeSymbol != null && !isArray) {
            varType = typeSymbol.type;
        } else {
            varType = new Type(typeName);
        }

        Symbol varSymbol = new Symbol(varName, varType);
        this.currentScope.put(varName, varSymbol);
        
        System.out.println("   Registrando variável: " + varName + " (" + typeName + ")");

        if (ctx.expr() != null) {
            Type exprType = visit(ctx.expr());
            if (exprType != null && !exprType.name.equals("error") && !isCompatible(varType, exprType)) {
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
            if (!isCompatible(lhsType, rhsType)) {
                System.err.println("ERRO SEMÂNTICO: Tipos incompatíveis. Não é possível atribuir " + rhsType.name + " a " + lhsType.name);
            }
        }
        return null;
    }

    @Override
    public Type visitPostfixExpr(CParser.PostfixExprContext ctx) {
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
            if (functionSymbol == null) { return new Type("error"); }

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
                    // CORREÇÃO AQUI: Usar isCompatible em vez de equals
                    if (!isCompatible(expectedType, actualType)) {
                        System.err.println("ERRO SEMÂNTICO: Argumento " + (i+1) + " da função '" + primaryName + 
                                           "' é inválido. Esperava '" + expectedType.name + "' mas recebeu '" + actualType.name + "'.");
                    }
                }
            }
            return functionSymbol.type;
        }

        // 3. Acesso a Membro de Struct
        if (!ctx.ID().isEmpty()) {
            String memberName = ctx.ID(0).getText();
            if (primaryType.members == null) {
                System.err.println("ERRO SEMÂNTICO: A variável '" + primaryName + "' (" + primaryType.name + ") não é uma struct/union, não pode aceder a '" + memberName + "'.");
                return new Type("error");
            }
            Symbol member = primaryType.members.get(memberName);
            if (member == null) {
                System.err.println("ERRO SEMÂNTICO: O campo '" + memberName + "' não existe em '" + primaryType.name + "'.");
                return new Type("error");
            }
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

    private boolean isCompatible(Type targetType, Type sourceType) {
        if (targetType.equals(sourceType)) {
            return true;
        }

        // Regra especial para Strings em C
        if (sourceType.name.equals("string")) {
            return targetType.name.equals("char*") || targetType.name.equals("char[]");
        }

        return false;
    }
       
    private String getTypeName(CParser.TypeContext ctx) {
        String text = ctx.baseType().getText();
        
        // Se for struct ou union, forçamos o espaço
        if (ctx.baseType().getChild(0).getText().equals("struct")) {
            text = "struct " + ctx.baseType().ID().getText();
        } else if (ctx.baseType().getChild(0).getText().equals("union")) {
            text = "union " + ctx.baseType().ID().getText();
        }
        
        // Adicionar ponteiros (*) se houver
        // (Os filhos do TypeContext são: baseType e depois zero ou mais '*')
        for (int i = 1; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i).getText().equals("*")) {
                text += "*";
            }
        }
        
        return text;
    }
}