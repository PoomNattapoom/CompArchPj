// import java.util.ArrayList;
// import java.util.List;

// public class Tokenizer {

//   // Enum for different token types
//   public enum TokenType {
//     INSTRUCTION, // Assembly instructions like ADD, BEQ
//     REGISTER, // Register names like x0, x1, etc.
//     LABEL, // Labels like LOOP, EXIT
//     NUMBER, // Immediate values or constants
//     SEPARATOR, // Symbols like commas, parentheses
//     WHITESPACE, // Spaces, tabs (to be ignored)
//     PARENTHESES, // Parentheses
//     UNKNOWN, // Unrecognized tokens
//     NEWLINE // Newline character
//   }

//   // Class to represent a token
//   public static class Token {
//     private String value;
//     private TokenType type;

//     public Token(String value, TokenType type) {
//       this.value = value;
//       this.type = type;
//     }

//     public String getValue() {
//       return value;
//     }

//     public TokenType getType() {
//       return type;
//     }

//     @Override
//     public String toString() {
//       return "Token{" + "value='" + value + '\'' + ", type=" + type + '}';
//     }
//   }

//   // Method to tokenize a line of assembly code
//   public List<Token> tokenize(String line) {
//     List<Token> tokens = new ArrayList<>();
//     String[] parts = line.split("\\s+");
//     for (String part : parts) {
//       if (part.isEmpty()) {
//         continue;
//       } else if (part.equals("\n") || part.equals("\r")) { // Actual newline characters
//         tokens.add(new Token(part, TokenType.NEWLINE));
//       } else if (isInstruction(part.toUpperCase())) {
//         tokens.add(new Token(part.toUpperCase(), TokenType.INSTRUCTION));
//       } else if (isRegister(part.toLowerCase())) {
//         tokens.add(new Token(part.toLowerCase(), TokenType.REGISTER));
//       } else if (isNumber(part)) {
//         tokens.add(new Token(part, TokenType.NUMBER));
//       } else if (part.matches("[A-Za-z0-9_]*")) {
//         tokens.add(new Token(part, TokenType.LABEL));
//       } else {
//         tokens.add(new Token(part, TokenType.UNKNOWN));
//       }
//     }

//     return tokens;

//   }

//   // Helper methods to identify token types
//   private boolean isInstruction(String token) {
//     // Add more instructions as necessary
//     return token.matches(
//         "ADD|NAND|LW|SW|BEQ|JALR|HALT|NOOP|.FILL");
//   }

//   private boolean isRegister(String token) {
//     return token.matches("x[0-9]+");
//   }

//   private boolean isNumber(String token) {
//     return token.matches("-?\\d+") || token.matches("0[xX][0-9a-fA-F]+");
//   }

//   // Main method for testing
//   public static void main(String[] args) {
//     Tokenizer tokenizer = new Tokenizer();

//     // String assemblyCode = "ADD x1, x2, x3";
//     // String assemblyCode = "ADD x1 x2 x3";
//     // String assemblyCode = "start ADD x1 x2 x1 .FILL";
//     // String assemblyCode = ".FILL 20 10";
//     // String assemblyCode = "num .FILL 10";
//     String assemblyCode = "ADD x1 x2 x1 \n HALT \n num .FILL 10";

//     List<Token> tokens = tokenizer.tokenize(assemblyCode);
//     for (Token token : tokens) {
//       System.out.println(token);
//     }
//   }
// }

import java.util.ArrayList;
import java.util.List;

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
        // } else if (isRegister(part.toLowerCase())) {
        // tokens.add(new Token(part.toLowerCase(), TokenType.REGISTER));
      } else if (isNumber(part)) {
        tokens.add(new Token(part, TokenType.NUMBER));
      } else if (part.matches("[A-Za-z0-9_]*")) {
        tokens.add(new Token(part, TokenType.LABEL));
      } else {
        tokens.add(new Token(part, TokenType.UNKNOWN));
      }
    }

    return tokens;
  }

  // Helper methods to identify token types
  private boolean isInstruction(String word) {
    // Add more instructions as necessary
    return word.matches("ADD|NAND|LW|SW|BEQ|JALR|HALT|NOOP|.FILL");
  }

  private boolean isRegister(String word) {
    return word.matches("x[0-9]+");
  }

  private boolean isNumber(String word) {
    return word.matches("-?\\d+") || word.matches("0[xX][0-9a-fA-F]+");
  }

  // Main method for testing
  public static void main(String[] args) {
    Tokenizer tokenizer = new Tokenizer();

    // Test input with newlines
    // String assemblyCode = "ADD x1 x2 x1\nHALT\nnum .FILL 10";
    // String assemblyCode = "ADD x1 x2 x1 hello world";
    // String assemblyCode = "HALT";
    // String assemblyCode = "num .FILL 10";
    String assemblyCode = "start    add     1        2        1        decrement reg1";

    List<Token> tokens = tokenizer.tokenize(assemblyCode);
    for (Token token : tokens) {
      System.out.println(token);
    }
  }
}
