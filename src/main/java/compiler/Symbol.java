package compiler;

import java.util.List;

public class Symbol {
    public String name;
    public Type type;
    public List<Type> paramTypes; // Para funções
    public SymbolTable members;   // Para structs/unions
    public boolean isConstant = false; 
    public boolean initialized = false;

    public Symbol(String name, Type type) {
        this.name = name;
        this.type = type;
        this.isConstant = false;
        this.initialized = false;
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
}