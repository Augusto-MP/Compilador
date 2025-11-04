package compiler;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa um único escopo (ex: global, ou de uma função).
 * Sabe quem é o seu "pai" (o escopo que o contém).
 */
public class SymbolTable {

    // O mapa de símbolos para este escopo específico.
    private Map<String, Symbol> symbols = new HashMap<>();
    
    // A referência para o escopo pai (ex: o escopo global).
    private SymbolTable parent;

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    /**
     * Adiciona um novo símbolo (variável, função) a este escopo.
     */
    public void put(String name, Symbol symbol) {
        symbols.put(name, symbol);
    }

    /**
     * Procura por um símbolo.
     * Primeiro, procura neste escopo. Se não encontrar,
     * procura no escopo pai, e assim sucessivamente.
     */
    public Symbol get(String name) {
        Symbol symbol = symbols.get(name);
        
        if (symbol != null) {
            // Encontrou o símbolo neste escopo
            return symbol;
        }

        // Se não encontrou aqui, procura no pai (se houver um pai)
        if (parent != null) {
            return parent.get(name);
        }

        // Se não há pai e não encontrou, o símbolo não existe
        return null;
    }

    // Retorna o escopo pai
    public SymbolTable getParent() {
        return parent;
    }
}