import java.util.*;

public class Parser {
  private List<Tokenizer.Token> tokens;
  private Iterator<Tokenizer.Token> iterator;
  private Tokenizer.Token currentToken;
  private Map<String, Label> labels = new HashMap<>();
  private Map<String, Integer> variables = new HashMap<>();
  private int currentAddress = 0;

  public Parser(List<Tokenizer.Token> tokens) {
    this.tokens = tokens;
    this.iterator = tokens.iterator();
    advance(); // Move to the first token
  }

  // Helper method to advance to the next token
  private void advance() {
    if (iterator.hasNext()) {
      currentToken = iterator.next();
    } else {
      currentToken = null;
    }
  }

  // Parse the entire program (could be multiple instructions)
  public List<ASTNode> parseProgram() {
    List<ASTNode> program = new ArrayList<>();
    while (currentToken != null) {
      if (currentToken.getType() == Tokenizer.TokenType.LABEL) {
        String labelName = currentToken.getValue();
        defineLabel(labelName, currentAddress);
        advance(); // Move to the next token
      } else {
        ASTNode instruction = parseInstruction();
        if (instruction != null) {
          program.add(instruction);
          currentAddress++; // Increment address for each instruction parsed
        }
      }
      // Skip over newlines or extra spaces between instructions
      while (currentToken != null && currentToken.getType() == Tokenizer.TokenType.WHITESPACE) {
        advance(); // Move past spaces or newlines to get to the next instruction
      }
    }
    return program;
  }

  private void defineLabel(String name, int address) {
    Label label = new Label(name);
    label.setAddress(address);
    labels.put(name, label);
  }

  public Integer getLabelAddress(String name) {
    Label label = labels.get(name);
    return (label != null && label.isDefined()) ? label.getAddress() : null;
  }

  private ASTNode parseInstruction() {
    // Normal Instruction
    if (currentToken == null)
      return null;

    if (currentToken.getType() == Tokenizer.TokenType.INSTRUCTION) {
      InstructionNode instructionNode = new InstructionNode(currentToken.getValue());
      advance(); // Move to the first operand

      while (currentToken != null && currentToken.getType() != Tokenizer.TokenType.INSTRUCTION) {
        if (currentToken.getType() == Tokenizer.TokenType.NUMBER) {
          instructionNode.addOperand(new NumberNode(Integer.parseInt(currentToken.getValue())));
        } else if (currentToken.getType() == Tokenizer.TokenType.REGISTER) {
          instructionNode.addOperand(new RegisterNode(currentToken.getValue()));
        } else if (currentToken.getType() == Tokenizer.TokenType.SEPARATOR) {
          if (currentToken.getValue().equals("(")) {
            advance(); // Move to base register
            if (currentToken != null && currentToken.getType() == Tokenizer.TokenType.REGISTER) {
              instructionNode.addOperand(new RegisterNode(currentToken.getValue())); // Base register
              advance(); // Move past base register
            }
            if (currentToken != null && currentToken.getValue().equals(")")) {
              advance(); // Skip closing parenthesis
            }
            break; // Exit the loop after processing base register
          }
        }
        advance(); // Move to the next token
      }

      return instructionNode;

      // Case .fill
      // } else if (currentToken.getType() == Tokenizer.TokenType.LABEL) {
      // LabelNode labelNode = new LabelNode(currentToken.getValue());
      // advance();
      // if (currentToken.getValue() != ".FILL") {
      // return null;
      // }
      // advance();
      // NumberNode numberNode = new
      // NumberNode(Integer.parseInt(currentToken.getValue()));
      // InstructionNode instructionNode = new InstructionNode(".FILL");

      // instructionNode.addOperand(numberNode);
      // instructionNode.addOperand(new NumberNode(50));

      // advance();

      // return instructionNode;

    } else {
      throw new IllegalArgumentException("Invalid instruction: " + currentToken.getValue());
    }
  }

  public static void main(String[] args) {
    Tokenizer tokenizer = new Tokenizer();
    // String assemblyCode = "ADD x1 x2 x1\nHALT\nHALT";
    String assemblyCode = "num .FILL 10";

    List<Tokenizer.Token> tokens = tokenizer.tokenize(assemblyCode);
    Parser parser = new Parser(tokens);
    List<ASTNode> ast = parser.parseProgram();

    // Generate machine code from AST
    CodeGenerator codeGen = new CodeGenerator();
    for (ASTNode node : ast) {
      // node.accept(codeGen);
      System.out.println(node.toString());
    }

    // Output the generated machine code
    // System.out.println("Generated Machine Code:");
    // System.out.println(codeGen.getMachineCode());
  }
}