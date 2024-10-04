import java.util.*;

public class Test2 {
  public static void main(String[] args) {
    // Example input as a StringBuilder (25-bit binary strings and decimal numbers)
    StringBuilder input = new StringBuilder(
        "0100000010000000000000111\n" +
            "0100010100000000000000011\n" +
            "0000010100000000000000001\n" +
            "1000000010000000000000010\n" +
            "1000000000000000000000010\n" +
            "1110000000000000000000000\n" +
            "1100000000000000000000000\n" +
            "5\n" +
            "-1\n" +
            "2");

    // Process and print the output
    StringBuilder output = convertBinaryStream(input);
    System.out.println(output.toString()); // Output the result
  }

  // Function to convert binary strings to decimal and retain decimals as is
  public static StringBuilder convertBinaryStream(StringBuilder input) {
    StringBuilder output = new StringBuilder();

    // Split the input by newline to get individual lines
    String[] lines = input.toString().split("\n");

    for (String line : lines) {
      if (isBinaryString(line)) {
        // Convert binary string to decimal and append to output
        long decimalValue = Long.parseLong(line, 2);
        output.append(decimalValue).append("\n");
      } else {
        // Retain decimal numbers as is and append to output
        output.append(line).append("\n");
      }
    }

    return output;
  }

  // Function to check if a string is a valid 25-bit binary string
  public static boolean isBinaryString(String input) {
    // Check if the input contains exactly 25 characters and only '0' or '1'
    return input.length() == 25 && input.matches("[01]+");
  }
}
