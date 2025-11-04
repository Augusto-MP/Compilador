package compiler;

public class Symbol {
    public String name; // ex: "x", "main"
    public Type type;   // O 'Type' que acabámos de criar

    public Symbol(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return "<'" + name + "', " + type.name + ">";
    }
}