import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Test {
  public static void main(String[] args) {
    try {
      // Path to the text file
      String filePath = "assemblyCode.txt";

      // Read all lines from the file and join them with '\n'
      String content = String.join("\n", Files.readAllLines(Paths.get(filePath)));

      // Print the resulting string
      System.out.println(content);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
