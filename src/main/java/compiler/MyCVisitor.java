package compiler;

import gen.CBaseVisitor; // Importa o Visitor base que o ANTLR gerou
import gen.CParser;     // Importa as definições das regras do Parser

// Vamos herdar da classe CBaseVisitor.
// Usamos <Object> como tipo de retorno genérico por enquanto,
// pois os nossos métodos de visita podem retornar coisas diferentes.
public class MyCVisitor extends CBaseVisitor<Object> {

    /**
     * Este método é chamado automaticamente quando o Visitor encontra
     * uma regra de 'functionDeclaration' na árvore.
     */
    @Override
    public Object visitFunctionDeclaration(CParser.FunctionDeclarationContext ctx) {
        
        // 'ctx' (contexto) é o nó da árvore. Podemos usá-lo para
        // extrair informações.
        
        // Vamos obter o nome da função (que é um ID na gramática)
        String functionName = ctx.ID().getText(); 
        
        System.out.println("Visitando a função: " + functionName);

        // É crucial chamar visitChildren() para que o Visitor
        // continue a caminhar para dentro da função (para o seu 'block').
        return visitChildren(ctx);
    }
}