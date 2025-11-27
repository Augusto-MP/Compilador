package compiler;

public class Type {
    public String name; // ex: "int", "float", "struct Ponto"
    
    // Tabela de símbolos para guardar os membros (se for uma struct)
    // Se não for struct, isto fica null.
    public SymbolTable members; 

    public Type(String name) {
        this.name = name;
        this.members = null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Type type = (Type) obj;
        return name.equals(type.name);
    }
}