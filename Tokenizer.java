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
        UNKNOWN // Unrecognized tokens
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
            } else if (isInstruction(part.toUpperCase())) {
                tokens.add(new Token(part.toUpperCase(), TokenType.INSTRUCTION));
            } else if (isRegister(part.toLowerCase())) {
                tokens.add(new Token(part.toLowerCase(), TokenType.REGISTER));
            } else if (isNumber(part)) {
                tokens.add(new Token(part, TokenType.NUMBER));
            } else if (part.equals(",") || part.equals("(") || part.equals(")")) {
                tokens.add(new Token(part, TokenType.SEPARATOR));
            } else if (part.matches("[A-Za-z_][A-Za-z0-9_]*:")) {
                tokens.add(new Token(part.toUpperCase(), TokenType.LABEL));
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
                "ADD|SUB|SLL|XOR|SRL|SRA|OR|AND|LR.D|SC.D|LB|LH|LW|LD|LBU|LHU|LWU|ADDI|SLLI|XORI|SRLI|SRAI|ORI|ANDI|JALR|SB|SH|SW|SD|BEQ|BNE|BLT|BGE|BLTU|BGEU|LUI|JAL");
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
        String assemblyCode = "BEQ x1, x2, LABEL";

        List<Token> tokens = tokenizer.tokenize(assemblyCode);
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
