package compiler;

public class Type {
    public String name; // ex: "int", "float", "struct Ponto"
    public Object value;
    public SymbolTable members; 

    public Type(String name) {
        this.name = name;
        this.members = null;
        this.value = null;
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