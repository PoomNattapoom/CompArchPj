import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.FileWriter;

public class Main {

  public static void writeStringToFile(String content, String filePath) {
    FileWriter writer = null;
    try {
      writer = new FileWriter(filePath);
      writer.write(content);
      writer.flush(); // Ensures the content is flushed to the file
      System.out.println("Successfully wrote to the file.");
    } catch (IOException e) {
      System.out.println("An error occurred while writing to the file.");
      e.printStackTrace();
    } finally {
      if (writer != null) {
        try {
          writer.close(); // Ensure the writer is closed to prevent leaks
        } catch (IOException e) {
          System.out.println("Error while closing the writer.");
          e.printStackTrace();
        }
      }
    }
  }

  public static void main(String[] args) {

    Tokenizer tokenizer = new Tokenizer();

    try {
      // Path to the text file
      String filePath = "assembly/combineCode.txt";

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

      writeStringToFile(codeGen.getDecMachineCode(),
          "C:\\Users\\iDeapad GM\\Documents\\Year3-1\\261304\\CompArchPj\\assembly\\output.txt");

    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
