package Main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import gen.CLexer;
import gen.CParser;
import compiler.MyCVisitor;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java -jar compiler.jar <source_file.c> <output_file.ll>");
            return;
        }

        String sourceFile = args[0];
        String outputFile = args[1];

        try {
            // 1. Inicializar Lexer e Parser
            CharStream input = CharStreams.fromFileName(sourceFile);
            CLexer lexer = new CLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            CParser parser = new CParser(tokens);

            // 2. Analisar a Árvore Sintática
            ParseTree tree = parser.program();

            // 3. Rodar o Visitor (Compilador)
            MyCVisitor visitor = new MyCVisitor();
            visitor.visit(tree);

            // 4. Salvar o Código Gerado em Arquivo
            try (PrintWriter out = new PrintWriter(new FileWriter(outputFile))) {
                out.print(visitor.getLLVMCode());
            }

            System.out.println("Compilacao concluida com sucesso!");
            System.out.println("Codigo LLVM gerado em: " + outputFile);

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}