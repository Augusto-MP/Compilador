package compiler;

// 1. Precisamos de imports para Listas
import java.util.ArrayList;
import java.util.List;

public class Symbol {
    public String name; // ex: "x", "main"
    public Type type;   // Tipo da variável OU tipo de retorno da função
    
    // 2. NOVO: Lista para guardar os tipos dos parâmetros (se for uma função)
    public List<Type> paramTypes;

    // Construtor antigo (para variáveis)
    // Este construtor continua a funcionar para as nossas variáveis.
    public Symbol(String name, Type type) {
        this.name = name;
        this.type = type;
        this.paramTypes = null; // Indica que não é uma função
    }

    // 3. NOVO: Construtor para funções
    public Symbol(String name, Type returnType, List<Type> paramTypes) {
        this.name = name;
        this.type = returnType; // 'type' agora significa o tipo de retorno
        this.paramTypes = paramTypes;
    }

    // 4. NOVO: Método para verificar se este símbolo é uma função
    public boolean isFunction() {
        return this.paramTypes != null;
    }

    /**
     * Atualizámos o toString para mostrar a assinatura da função
     */
    @Override
    public String toString() {
        if (isFunction()) {
            // Formato para função: <'main', int(param1_tipo, param2_tipo)>
            List<String> typeNames = new ArrayList<>();
            for (Type t : paramTypes) {
                typeNames.add(t.name);
            }
            return "<'" + name + "', " + type.name + "(" + String.join(", ", typeNames) + ")>";
        } else {
            // Formato para variável: <'x', int>
            return "<'" + name + "', " + type.name + ">";
        }
    }
}