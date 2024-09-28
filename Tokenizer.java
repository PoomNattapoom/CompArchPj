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

    // Method to tokenize a line of assembly code
    public List<Token> tokenize(String line) {
        List<Token> tokens = new ArrayList<>();
        String[] parts = line.split("\\s+|(?=[,()])|(?<=[,()])"); // Splits by spaces, commas, and parentheses

        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
                // } else if (part.equals("\n")) {
                // tokens.add(new Token(part, TokenType.NEWLINE));
            } else if (isInstruction(part.toUpperCase())) {
                tokens.add(new Token(part.toUpperCase(), TokenType.INSTRUCTION));
            } else if (isRegister(part.toLowerCase())) {
                tokens.add(new Token(part.toLowerCase(), TokenType.REGISTER));
            } else if (isNumber(part)) {
                tokens.add(new Token(part, TokenType.NUMBER));
            } else if (part.equals(",")) {
                tokens.add(new Token(part, TokenType.SEPARATOR));
            } else if (part.matches("[A-Za-z_][A-Za-z0-9_]*:")) {
                tokens.add(new Token(part.toUpperCase(), TokenType.LABEL));
            } else if (part.equals("(") || part.equals(")")) {
                tokens.add(new Token(part, TokenType.SEPARATOR));
            } else {
                tokens.add(new Token(part, TokenType.UNKNOWN));
            }
        }

        return tokens;

    }

    // Helper methods to identify token types
    private boolean isInstruction(String token) {
        // Add more instructions as necessary
        return token.matches(
                "ADD|NAND|LW|SW|BEQ|JALR|HALT|NOOP");
    }

    private boolean isRegister(String token) {
        return token.matches("x[0-9]+");
    }

    private boolean isNumber(String token) {
        return token.matches("-?\\d+") || token.matches("0[xX][0-9a-fA-F]+");
    }

    // Main method for testing
    public static void main(String[] args) {
        Tokenizer tokenizer = new Tokenizer();

        // String assemblyCode = "ADD x1, x2, x3";
        // String assemblyCode = "ADD x1 x2 x3";
        String assemblyCode = "ADD x1 x2 x1\nHALT";

        List<Token> tokens = tokenizer.tokenize(assemblyCode);
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
