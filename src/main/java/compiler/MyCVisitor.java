package compiler;

import gen.CBaseVisitor;
import gen.CParser;

// Agora que estamos a usar as classes, removemos os imports redundantes.
// (Esta linha é um comentário para si, não para o código)

// 1. MUDANÇA IMPORTANTE: O nosso visitor agora retorna objetos 'Type'
public class MyCVisitor extends CBaseVisitor<Type> {

    private SymbolTable currentScope;

    public MyCVisitor() {
        this.currentScope = new SymbolTable(null);
    }

    // 2. Todos os métodos @Override agora devem retornar 'Type'
    //    Para 'visit' de 'statements' (que não retornam valor),
    //    podemos simplesmente retornar 'null'.

    @Override
    public Type visitFunctionDeclaration(CParser.FunctionDeclarationContext ctx) {
        
        String functionName = ctx.ID().getText();
        String returnTypeName = ctx.type().getText();
        Type functionType = new Type(returnTypeName);
        Symbol functionSymbol = new Symbol(functionName, functionType);

        this.currentScope.put(functionName, functionSymbol);
        System.out.println("Registando nova função no escopo: " + functionSymbol);

        SymbolTable functionScope = new SymbolTable(this.currentScope);
        this.currentScope = functionScope;

        visitChildren(ctx);

        this.currentScope = this.currentScope.getParent();
        return null; // Não retorna tipo
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
        System.out.println("   Registando nova variável no escopo local: " + varSymbol);

        // 3. Verificação de tipo na inicialização
        if (ctx.expr() != null) {
            Type exprType = visit(ctx.expr()); // Visita a expressão e obtém o seu tipo
            if (!varType.equals(exprType)) {
                System.err.println("ERRO SEMÂNTICO: Tipos incompatíveis. Não é possível atribuir " + exprType.name + " a " + varType.name);
            }
        }

        return null; // Não retorna tipo
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
}