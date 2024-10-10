import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.FileWriter;

public class Tokenizer {

  // Enum for different token types
  public enum TokenType {
    INSTRUCTION, // Assembly instructions like ADD, BEQ
    REGISTER, // Register names like x0, x1, etc.
    LABEL, // Labels like LOOP, EXIT
    NUMBER, // Immediate values or constants
    SEPARATOR, // Symbols like commas, parentheses
    WHITESPACE, // Spaces, tabs (to be ignored)
    PARENTHESES, // Parentheses
    UNKNOWN, // Unrecognized tokens
    COMMENT, // Comments
    NEWLINE // Newline character
  }

  // Class to represent a token
  public static class Token {
    private String value;
    private TokenType type;

    public Token(String value, TokenType type) {
      this.value = value;
      this.type = type;
    }

    public String getValue() {
      return value;
    }

    public TokenType getType() {
      return type;
    }

    @Override
    public String toString() {
      return "Token{" + "value='" + value + '\'' + ", type=" + type + '}';
    }
  }

  // Method to tokenize a string of assembly code
  public List<Token> tokenize(String input) {
    List<Token> tokens = new ArrayList<>();
    // Split the input by newlines to preserve them
    String[] words = input.split("(?<=\n)|(?=\n)"); // Split while preserving newlines

    for (String word : words) {
      if (word.equals("\n") || word.equals("\r") || word.equals("\r\n")) {
        tokens.add(new Token(word, TokenType.NEWLINE)); // Add newline as a token
      } else {
        tokens.addAll(tokenizeLine(word));
      }
    }

    return tokens;
  }

  // Method to tokenize each line of assembly code
  public List<Token> tokenizeLine(String word) {
    List<Token> tokens = new ArrayList<>();
    String[] parts = word.split("\\s+|(?=[,()])|(?<=[,()])"); // Splits by spaces, commas, and parentheses

    for (String part : parts) {
      if (part.isEmpty()) {
        continue;
      } else if (isInstruction(part.toUpperCase())) {
        tokens.add(new Token(part.toUpperCase(), TokenType.INSTRUCTION));
      } else if (isNumber(part)) {
        tokens.add(new Token(part, TokenType.NUMBER));
      } else if (part.matches("[A-Za-z0-9_]*")) {
        tokens.add(new Token(part, TokenType.LABEL));
      }

    }

    // for (int i = 0; i < parts.length; i++) {
    // if (i == 0 && !isInstruction(parts[i].toUpperCase())) {
    // tokens.add(new Token(parts[i], TokenType.LABEL));
    // } else if (parts[i].isEmpty()) {
    // continue;
    // } else if (isInstruction(parts[i].toUpperCase())) {
    // tokens.add(new Token(parts[i].toUpperCase(), TokenType.INSTRUCTION));
    // } else if (isNumber(parts[i])) {
    // tokens.add(new Token(parts[i], TokenType.NUMBER));
    // } else if (parts[i].matches("[A-Za-z0-9_]*")) {
    // tokens.add(new Token(parts[i], TokenType.COMMENT));
    // } else {
    // tokens.add(new Token(parts[i], TokenType.UNKNOWN));
    // }
    // }

    return tokens;
  }

  // Helper methods to identify token types
  private boolean isInstruction(String word) {
    // Add more instructions as necessary
    return word.matches("ADD|NAND|LW|SW|BEQ|JALR|HALT|NOOP|.FILL");
  }

  private boolean isNumber(String word) {
    return word.matches("-?\\d+") || word.matches("0[xX][0-9a-fA-F]+");
  }

  // Main method for testing
  public static void main(String[] args) {
    String assemblyCode = "start add 1 2 1 hello world \n add x1 x2 x3\n five .fill 5 dasd";

    Tokenizer tokenizer = new Tokenizer();
    List<Token> tokens = tokenizer.tokenize(assemblyCode);

    for (Token token : tokens) {
      System.out.println(token);
    }

  }
}
