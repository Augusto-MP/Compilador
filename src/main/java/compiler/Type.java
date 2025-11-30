package compiler;

public class Type {
    public String name; // ex: "int", "float", "struct Ponto"
    public SymbolTable members; 
    public Object value;
    public int arraySize = 0; // 0 significa que não é array ou tamanho desconhecido

    public Type(String name) {
        this.name = name;
        this.members = null;
        this.value = null;
    }

    public Type(String name, Object value) {
        this.name = name;
        this.members = null;
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Type type = (Type) obj;
        return name.equals(type.name);
    }
    @Override
    public String toString() {
        return name + (value != null ? (" (value=" + value + ")") : "");
    }
}