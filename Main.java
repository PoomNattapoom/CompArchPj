import java.util.List;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
  public static void main(String[] args) {

    Tokenizer tokenizer = new Tokenizer();

    try {
      // Path to the text file
      String filePath = "assemblyCode.txt";

      // Read all lines from the file and join them with '\n'
      String assemblyCode = String.join("\n", Files.readAllLines(Paths.get(filePath)));

      List<Tokenizer.Token> tokens = tokenizer.tokenize(assemblyCode);
      // List<Tokenizer.Token> tokens2 = tokenizer.tokenize(assemblyCode);

      Parser parser = new Parser(tokens);
      // Parser parser2 = new Parser(tokens2);

      List<List<ASTNode>> ast1 = parser.parseProgram();

      // Generate machine code from AST
      CodeGenerator codeGen = new CodeGenerator();

      codeGen.setAddressMap(parser.getAddressMap());

      for (List<ASTNode> nodes : ast1) {
        for (ASTNode node : nodes) {
          // System.out.println(node.toString());
          node.firstAccept(codeGen);
        }
      }

      for (List<ASTNode> nodes : ast1) {
        for (ASTNode node : nodes) {
          node.accept(codeGen);
        }
      }

      // Output the generated machine code
      System.out.println("Generated Machine Code:");
      System.out.println(codeGen.getDecMachineCode());
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
