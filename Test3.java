import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.FileWriter;

public class Test3 {
  public static void main(String[] args) {
    try {
      // Path to the text file
      String filePath = "assembly/test.txt";

      // Read all lines from the file and join them with '\n'
      String assemblyCode = String.join("\n", Files.readAllLines(Paths.get(filePath)));
      System.out.println(assemblyCode);
    } catch (IOException e) {
      System.err.println("An error occurred while reading the file.");
      System.out.println("Process finished with exit code 1");
      System.exit(1);
    }
    System.out.println("Process finished with exit code 0");
    System.exit(0);
  }
}