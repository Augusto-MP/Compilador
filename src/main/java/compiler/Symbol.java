package compiler;

import java.util.List;

public class Symbol {
    public String name;
    public Type type;
    
    public Object value; 

    public List<Type> paramTypes; // Para funções
    public SymbolTable members;   // Para structs/unions
    public boolean isConstant = false; 
    public boolean initialized = false;
    public int memoryIndex = -1;

    public Symbol(String name, Type type) {
        this.name = name;
        this.type = type;
        this.value = null; // Variáveis começam sem valor
    }
    
    public Symbol(String name, Type type, List<Type> paramTypes) {
        this.name = name;
        this.type = type;
        this.paramTypes = paramTypes;
        this.initialized = true;
    }

    public boolean isFunction() {
        return paramTypes != null;
    }

    @Override
    public String toString() {
        // Atualizamos o toString para mostrar o valor (ajuda muito no debug!)
        return "Symbol [name=" + name + ", type=" + (type != null ? type.name : "null") + ", value=" + value + "]";
    }
}