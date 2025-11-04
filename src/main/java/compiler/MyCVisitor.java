package compiler;

import gen.CBaseVisitor;
import gen.CParser;

public class MyCVisitor extends CBaseVisitor<Type> {

    private SymbolTable currentScope;
    private Symbol currentFunction; // 1. O NOSSO NOVO CAMPO

    public MyCVisitor() {
        this.currentScope = new SymbolTable(null);
        this.currentFunction = null; // Começamos fora de qualquer função
    }

    @Override
    public Type visitFunctionDeclaration(CParser.FunctionDeclarationContext ctx) {
        
        String functionName = ctx.ID().getText();
        String returnTypeName = ctx.type().getText();
        Type functionType = new Type(returnTypeName);
        Symbol functionSymbol = new Symbol(functionName, functionType);

        this.currentScope.put(functionName, functionSymbol);
        System.out.println("Registrando nova função no escopo: " + functionSymbol);

        // 2. ATUALIZAÇÃO DA LÓGICA DE ESCOPO
        Symbol oldFunction = this.currentFunction;  // Salva a função anterior (para aninhamento)
        this.currentFunction = functionSymbol;      // Define a função atual

        SymbolTable functionScope = new SymbolTable(this.currentScope);
        this.currentScope = functionScope;

        visitChildren(ctx);

        this.currentScope = this.currentScope.getParent();
        this.currentFunction = oldFunction; // Restaura a função anterior

        return null;
    }

    @Override
    public Type visitDeclaration(CParser.DeclarationContext ctx) {
        
        String typeName = ctx.type().getText();
        String varName = ctx.ID().getText();

        if (ctx.INT() != null && !ctx.INT().isEmpty()) {
            typeName += "[]";
        }
        
        Type varType = new Type(typeName);
        Symbol varSymbol = new Symbol(varName, varType);

        this.currentScope.put(varName, varSymbol);
        System.out.println("   Registrando nova variável no escopo local: " + varSymbol);

        // 3. Verificação de tipo na inicialização
        if (ctx.expr() != null) {
            Type exprType = visit(ctx.expr()); // Visita a expressão e obtém o seu tipo
            if (!varType.equals(exprType)) {
                System.err.println("ERRO SEMÂNTICO: Tipos incompatíveis. Não é possível atribuir " + exprType.name + " a " + varType.name);
            }
        }

        return null; // Não retorna tipo
    }
    
    /**
     * Método chamado ao encontrar uma atribuição (ex: x = 10;)
     * A regra na gramática é: assignment: unaryExpr '=' expr ';'
     */
    @Override
    public Type visitAssignment(CParser.AssignmentContext ctx) {
        
        // 1. Visitar o lado esquerdo (LHS) da atribuição e obter o seu tipo.
        //    Na nossa gramática, o LHS é um 'unaryExpr'.
        Type lhsType = visit(ctx.unaryExpr());

        // 2. Visitar o lado direito (RHS) da atribuição e obter o seu tipo.
        //    Na nossa gramática, o RHS é uma 'expr'.
        Type rhsType = visit(ctx.expr());

        // 3. Comparar os tipos.
        //    (Ignoramos se algum tipo for 'error', pois já reportámos esse erro)
        if (lhsType != null && rhsType != null && 
            !lhsType.name.equals("error") && !rhsType.name.equals("error")) 
        {
            if (!lhsType.equals(rhsType)) {
                System.err.println("ERRO SEMÂNTICO: Tipos incompatíveis. Não é possível atribuir " + rhsType.name + " a " + lhsType.name);
            }
        }

        return null; // Atribuição é um 'statement', não retorna tipo.
    }

    @Override
    public Type visitPrimary(CParser.PrimaryContext ctx) {
        
        if (ctx.ID() != null) {
            String varName = ctx.ID().getText();
            Symbol symbol = this.currentScope.get(varName);

            if (symbol == null) {
                System.err.println("ERRO SEMÂNTICO: A variável '" + varName + "' não foi declarada.");
                return new Type("error"); // Retorna um tipo de erro
            } else {
                System.out.println("   Variável '" + varName + "' encontrada no escopo. Tipo: " + symbol.type.name);
                // 4. MUDANÇA IMPORTANTE: Retorna o tipo da variável
                return symbol.type;
            }
        
        // 5. MUDANÇA IMPORTANTE: Retorna o tipo dos literais
        } else if (ctx.INT() != null) {
            return new Type("int");
        } else if (ctx.FLOAT() != null) {
            return new Type("float");
        } else if (ctx.CHAR() != null) {
            return new Type("char");
        } else if (ctx.STRING() != null) {
            return new Type("string"); // (Vamos assumir um tipo 'string' por agora)
        }

        // Se for uma '(expr)', visita o que está dentro
        return visitChildren(ctx);
    }

    /**
     * Método chamado ao visitar uma expressão aditiva (ex: a + b ou a - b)
     */
    @Override
    public Type visitAdditiveExpr(CParser.AdditiveExprContext ctx) {
        // A regra é: multiplicativeExpr (('+' | '-') multiplicativeExpr)*
        
        // 1. Visita o lado esquerdo (sempre existe)
        Type lhsType = visit(ctx.multiplicativeExpr(0));

        // 2. Verifica se o lado direito existe (se não existir, é apenas um termo)
        if (ctx.multiplicativeExpr().size() > 1) {
            // Visita o lado direito
            Type rhsType = visit(ctx.multiplicativeExpr(1));

            // 3. Verificação de tipo (simplificada por agora)
            if (lhsType.name.equals("error") || rhsType.name.equals("error")) {
                return new Type("error");
            }
            
            // Regra simples: ambos têm de ser iguais e numéricos (int/float)
            if (lhsType.equals(rhsType) && (lhsType.name.equals("int") || lhsType.name.equals("float"))) {
                // O tipo resultante é o mesmo (ex: int + int = int)
                return lhsType;
            } else {
                System.err.println("ERRO SEMÂNTICO: Tipos incompatíveis para operação aritmética: " + lhsType.name + " e " + rhsType.name);
                return new Type("error");
            }
        }
        
        // 4. Se não houver lado direito, o tipo da expressão é o tipo do lado esquerdo
        return lhsType;
    }

    /**
     * Método chamado ao visitar uma expressão multiplicativa (ex: a * b, a / b, a % b)
     */
    @Override
    public Type visitMultiplicativeExpr(CParser.MultiplicativeExprContext ctx) {
        // A regra é: unaryExpr (('*' | '/' | '%') unaryExpr)*
        
        // 1. Visita o lado esquerdo (sempre existe)
        Type lhsType = visit(ctx.unaryExpr(0));

        // 2. Verifica se o lado direito existe
        if (ctx.unaryExpr().size() > 1) {
            // Visita o lado direito
            Type rhsType = visit(ctx.unaryExpr(1));

            // 3. Verificação de tipo (simplificada por agora)
            if (lhsType.name.equals("error") || rhsType.name.equals("error")) {
                return new Type("error");
            }
            
            // Regra simples: ambos têm de ser iguais e numéricos (int/float)
            if (lhsType.equals(rhsType) && (lhsType.name.equals("int") || lhsType.name.equals("float"))) {
                return lhsType;
            } else {
                System.err.println("ERRO SEMÂNTICO: Tipos incompatíveis para operação aritmética: " + lhsType.name + " e " + rhsType.name);
                return new Type("error");
            }
        }
        
        // 4. Se não houver lado direito, o tipo da expressão é o tipo do lado esquerdo
        return lhsType;
    }

    /**
     * Método chamado ao visitar uma expressão relacional (ex: a > b, a <= b)
     */
    @Override
    public Type visitRelationalExpr(CParser.RelationalExprContext ctx) {
        // A regra é: additiveExpr (('<' | '>' | '<=' | '>=') additiveExpr)*
        
        // 1. Visita o lado esquerdo (sempre existe)
        Type lhsType = visit(ctx.additiveExpr(0));

        // 2. Verifica se o lado direito existe (se não, é apenas uma expressão aditiva)
        if (ctx.additiveExpr().size() > 1) {
            Type rhsType = visit(ctx.additiveExpr(1));

            // 3. Verificação de tipo
            if (lhsType.name.equals("error") || rhsType.name.equals("error")) {
                return new Type("error");
            }

            // Regra: Ambos os lados devem ser numéricos (int ou float)
            boolean isNumeric = (lhsType.name.equals("int") || lhsType.name.equals("float"));
            boolean areCompatible = isNumeric && lhsType.equals(rhsType);

            if (areCompatible) {
                // Operações relacionais resultam num booleano (int em C)
                return new Type("int");
            } else {
                System.err.println("ERRO SEMÂNTICO: Tipos incompatíveis para operação relacional: " + lhsType.name + " e " + rhsType.name);
                return new Type("error");
            }
        }
        
        // 4. Se não houver lado direito, o tipo é o da expressão aditiva
        return lhsType;
    }

    /**
     * Método chamado ao visitar uma expressão de igualdade (ex: a == b, a != b)
     */
    @Override
    public Type visitEqualityExpr(CParser.EqualityExprContext ctx) {
        // A regra é: relationalExpr (('==' | '!=') relationalExpr)*

        // 1. Visita o lado esquerdo (sempre existe)
        Type lhsType = visit(ctx.relationalExpr(0));

        // 2. Verifica se o lado direito existe
        if (ctx.relationalExpr().size() > 1) {
            Type rhsType = visit(ctx.relationalExpr(1));

            // 3. Verificação de tipo
            if (lhsType.name.equals("error") || rhsType.name.equals("error")) {
                return new Type("error");
            }
            
            // Regra: Ambos os lados devem ser do mesmo tipo (simplificado)
            if (lhsType.equals(rhsType)) {
                // Operações de igualdade resultam num booleano (int em C)
                return new Type("int");
            } else {
                System.err.println("ERRO SEMÂNTICO: Tipos incompatíveis para operação de igualdade: " + lhsType.name + " e " + rhsType.name);
                return new Type("error");
            }
        }
        
        // 4. Se não houver lado direito, o tipo é o da expressão relacional
        return lhsType;
    }

    /**
     * Método chamado ao visitar uma expressão 'E' lógico (ex: a && b)
     */
    @Override
    public Type visitLogicalAndExpr(CParser.LogicalAndExprContext ctx) {
        // A regra é: equalityExpr ('&&' equalityExpr)*

        // 1. Visita o lado esquerdo (sempre existe)
        Type lhsType = visit(ctx.equalityExpr(0));

        // 2. Verifica se o lado direito existe
        if (ctx.equalityExpr().size() > 1) {
            Type rhsType = visit(ctx.equalityExpr(1));

            // 3. Verificação de tipo
            if (lhsType.name.equals("error") || rhsType.name.equals("error")) {
                return new Type("error");
            }

            // Regra: Ambos os lados devem ser 'int' (booleano em C)
            if (lhsType.name.equals("int") && rhsType.name.equals("int")) {
                // Operações lógicas resultam num int (booleano)
                return new Type("int");
            } else {
                System.err.println("ERRO SEMÂNTICO: Tipos incompatíveis para operação lógica '&&': " + lhsType.name + " e " + rhsType.name);
                return new Type("error");
            }
        }
        
        // 4. Se não houver lado direito, o tipo é o da expressão de igualdade
        return lhsType;
    }

    /**
     * Método chamado ao visitar uma expressão 'OU' lógico (ex: a || b)
     */
    @Override
    public Type visitLogicalOrExpr(CParser.LogicalOrExprContext ctx) {
        // A regra é: logicalAndExpr ('||' logicalAndExpr)*

        // 1. Visita o lado esquerdo (sempre existe)
        Type lhsType = visit(ctx.logicalAndExpr(0));

        // 2. Verifica se o lado direito existe
        if (ctx.logicalAndExpr().size() > 1) {
            Type rhsType = visit(ctx.logicalAndExpr(1));

            // 3. Verificação de tipo
            if (lhsType.name.equals("error") || rhsType.name.equals("error")) {
                return new Type("error");
            }

            // Regra: Ambos os lados devem ser 'int' (booleano em C)
            if (lhsType.name.equals("int") && rhsType.name.equals("int")) {
                // Operações lógicas resultam num int (booleano)
                return new Type("int");
            } else {
                System.err.println("ERRO SEMÂNTICO: Tipos incompatíveis para operação lógica '||': " + lhsType.name + " e " + rhsType.name);
                return new Type("error");
            }
        }
        
        // 4. Se não houver lado direito, o tipo é o da expressão 'and'
        return lhsType;
    }

    @Override
    public Type visitIfStatement(CParser.IfStatementContext ctx) {
        Type conditionType = visit(ctx.expr());
        if (conditionType != null && !conditionType.name.equals("int") && !conditionType.name.equals("error")) {
            System.err.println("ERRO SEMÂNTICO: A condição do 'if' deve ser do tipo 'int', mas é '" + conditionType.name + "'.");
        }
        return visitChildren(ctx);
    }

    @Override
    public Type visitWhileStatement(CParser.WhileStatementContext ctx) {
        Type conditionType = visit(ctx.expr());
        if (conditionType != null && !conditionType.name.equals("int") && !conditionType.name.equals("error")) {
            System.err.println("ERRO SEMÂNTICO: A condição do 'while' deve ser do tipo 'int', mas é '" + conditionType.name + "'.");
        }
        return visitChildren(ctx);
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
    
    // --- O NOSSO NOVO MÉTODO ---

    /**
     * Método chamado ao visitar um 'return' statement
     */
    @Override
    public Type visitReturnStatement(CParser.ReturnStatementContext ctx) {
        // A regra é: 'return' expr? ';'

        Type expectedReturnType = this.currentFunction.type;
        Type actualReturnType;

        if (ctx.expr() != null) {
            // 1. Caso: return expr;
            actualReturnType = visit(ctx.expr());
        } else {
            // 2. Caso: return; (sem expressão)
            actualReturnType = new Type("void");
        }

        // 3. Verificação de Tipo
        if (actualReturnType != null && !actualReturnType.name.equals("error")) {
            if (!expectedReturnType.equals(actualReturnType)) {
                System.err.println("ERRO SEMÂNTICO: Tipo de retorno incompatível. A função '" + this.currentFunction.name + 
                                   "' espera '" + expectedReturnType.name + "' mas o 'return' é do tipo '" + actualReturnType.name + "'.");
            }
        }
        
        return null; // É um statement, não retorna tipo
    }
}