package Main;

import java.io.IOException;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import compiler.MyCVisitor;

// Importa as classes do nosso novo pacote 'gen'
import gen.CLexer;
import gen.CParser;

public class Main {
    public static void main(String[] args) {
        // Verifica se temos exatamente dois argumentos
        if (args.length != 2) {
            System.err.println("Error: Invalid arguments.");
            System.err.println("Usage: java -jar compiler.jar <source_file.c> <output_file.exe>");
            return;
        }

        String sourceFile = args[0];
        String outputFile = args[1];

        try {
            System.out.println("Compiling " + sourceFile + "...");

            // Cria o fluxo de caracteres a partir do ficheiro de código
            CharStream input = CharStreams.fromFileName(sourceFile);

            // Cria o analisador léxico
            CLexer lexer = new CLexer(input);

            // Cria o fluxo de tokens
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // Cria o analisador sintático
            CParser parser = new CParser(tokens);

            // Inicia a análise pela regra 'program' e obtém a árvore
            ParseTree tree = parser.program();

            // Cria uma instância do nosso Visitor
            MyCVisitor visitor = new MyCVisitor();

            // Inicia a caminhada pela árvore a partir do nó raiz ('tree')
            visitor.visit(tree);

            // Se chegámos aqui, a análise sintática foi bem-sucedida
            System.out.println("Parsing completed successfully.");
            System.out.println("Output will be generated at: " + outputFile);

            // O próximo passo será "caminhar" pela árvore ('tree') para fazer a análise
            // semântica e gerar o código.

        } catch (IOException e) {
            System.err.println("Error: Could not read source file '" + sourceFile + "'");
        }
    }
}