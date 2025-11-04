package compiler;

public class Type {
    public String name; // ex: "int", "float", "struct Ponto", "int*"

    public Type(String name) {
        this.name = name;
    }

    // Usaremos este método em breve para comparar tipos
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Type type = (Type) obj;
        return name.equals(type.name);
    }
}