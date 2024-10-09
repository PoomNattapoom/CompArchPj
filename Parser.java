import java.util.*;

public class Parser {
  private List<Tokenizer.Token> tokens;
  private Iterator<Tokenizer.Token> iterator;
  private Tokenizer.Token currentToken;
  private Map<String, Integer> addressMap = new HashMap<>();
  private Map<String, Integer> variables = new HashMap<>();
  private int currentAddress = 0;

  public Parser(List<Tokenizer.Token> tokens) {
    this.tokens = tokens;
    this.iterator = tokens.iterator();
    advance(); // Move to the first token
  }

  private Map<String, Integer> getVariables() {
    return variables;
  }

  public Map<String, Integer> getAddressMap() {
    return addressMap;
  }

  private void setVariables(Map<String, Integer> variables) {
    this.variables = variables;
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
  public List<List<ASTNode>> parseProgram() {

    List<List<ASTNode>> program = new ArrayList<>();
    List<ASTNode> line = parseLine();
    while (line != null) {
      program.add(line);
      line = parseLine();
    }
    return program;
  }

  private List<ASTNode> parseLine() {

    if (currentToken == null) {
      return null;
    }

    String label = null;

    // Parse Label first
    List<ASTNode> line = new ArrayList<>();

    if (currentToken.getType() == Tokenizer.TokenType.LABEL
        && !currentToken.getValue().toUpperCase().matches("ADD|NAND|LW|SW|BEQ|JALR|HALT|NOOP|.FILL")) {
      // System.out.println("Label: " + currentToken.getValue());
      label = currentToken.getValue();
      if (addressMap.containsKey(label)) {
        throw new IllegalArgumentException("Label already defined: " + label);
      }
      defineLabel(label, currentAddress);
      advance(); // Move to the next token
    }

    if (currentToken.getValue().equals(".FILL")) {
      advance(); // Move to the number after .FILL
      if (currentToken.getType() == Tokenizer.TokenType.NUMBER) {
        NumberNode numberNode = new NumberNode(Integer.parseInt(currentToken.getValue()));
        variables.put(label, numberNode.getNumber());
        ASTNode instruction = new InstructionNode(".FILL", new LabelNode(label), numberNode);
        line.add(instruction);
        advance();
      } else if (currentToken.getType() == Tokenizer.TokenType.LABEL) {
        LabelNode labelNode = new LabelNode(currentToken.getValue());
        ASTNode instruction = new InstructionNode(".FILL", new LabelNode(label), labelNode);
        line.add(instruction);
        advance();
      } else {
        throw new IllegalArgumentException("Expected a number after .FILL");
      }
    } else if (currentToken.getType() == Tokenizer.TokenType.INSTRUCTION) {
      ASTNode instruction = parseInstruction();
      if (instruction != null)
        line.add(instruction);

    } else {
      throw new IllegalArgumentException("Invalid instruction: " + currentToken.getValue());
    }

    // Handle comments
    while (currentToken != null) {
      if (currentToken.getType() == Tokenizer.TokenType.NEWLINE) {
        advance();
        currentAddress++;
        return line;
      }
      advance();
    }

    advance();
    currentAddress++; // Increment address for each line parsed

    return line;
  }

  private void defineLabel(String name, int address) {
    addressMap.put(name, address);
  }

  public Integer getSymbolicInteger(String name) {
    return variables.get(name);
  }

  private ASTNode parseInstruction() {
    // Normal Instruction
    if (currentToken == null)
      return null;

    if (currentToken.getType() != Tokenizer.TokenType.INSTRUCTION) {
      throw new IllegalArgumentException("Invalid instruction: " + currentToken.getValue());
    }

    InstructionNode instructionNode = new InstructionNode(currentToken.getValue());

    // Handle HALT and NOOP instructions (0 operands)
    if (currentToken.getValue().equals("HALT") || currentToken.getValue().equals("NOOP")) {
      // System.out.println("HALT or NOOP instruction");
      advance();
      return instructionNode;

      // Handle JALR instuctions (2 operands)
    }

    if (currentToken.getValue().equals("JALR")) {
      advance(); // Move to the first operand
      for (int i = 0; i < 2; i++) {
        if (currentToken == null) {
          throw new IllegalArgumentException(
              "Expected 2 operands for instruction: " + instructionNode.getInstruction());
        }
        if (currentToken.getType() == Tokenizer.TokenType.NUMBER) {
          instructionNode.addOperand(new NumberNode(Integer.parseInt(currentToken.getValue())));
        } else if (currentToken.getType() == Tokenizer.TokenType.REGISTER) {
          instructionNode.addOperand(new RegisterNode(currentToken.getValue()));
        } else if (currentToken.getType() == Tokenizer.TokenType.LABEL) {
          instructionNode.addOperand(new LabelNode(currentToken.getValue()));
        } else {
          throw new IllegalArgumentException("Invalid operand: " + currentToken.getValue());
        }
        advance(); // Move to the next token
      }

      // Handle Other Instructions (3 Operands)
    } else if (currentToken.getValue().equals("ADD") || currentToken.getValue().equals("NAND")) {
      advance(); // Move to the first operand
      for (int i = 0; i < 3; i++) {
        if (currentToken == null) {
          throw new IllegalArgumentException(
              "Expected 2 operands for instruction: " + instructionNode.getInstruction());
        }
        if (currentToken.getType() == Tokenizer.TokenType.NUMBER) {
          instructionNode.addOperand(new NumberNode(Integer.parseInt(currentToken.getValue())));
        } else if (currentToken.getType() == Tokenizer.TokenType.REGISTER) {
          instructionNode.addOperand(new RegisterNode(currentToken.getValue()));
        } else {
          throw new IllegalArgumentException("Invalid operand: " + currentToken.getValue());
        }
        advance(); // Move to the next token
      }

    } else {
      advance(); // Move to the first operand
      for (int i = 0; i < 3; i++) {
        if (currentToken == null) {
          throw new IllegalArgumentException(
              "Expected 2 operands for instruction: " + instructionNode.getInstruction());
        }
        if (currentToken.getType() == Tokenizer.TokenType.NUMBER) {
          instructionNode.addOperand(new NumberNode(Integer.parseInt(currentToken.getValue())));
        } else if (currentToken.getType() == Tokenizer.TokenType.REGISTER) {
          instructionNode.addOperand(new RegisterNode(currentToken.getValue()));
        } else if (currentToken.getType() == Tokenizer.TokenType.LABEL) {
          String name = currentToken.getValue();
          instructionNode.addOperand(new LabelNode(name));
        } else {
          throw new IllegalArgumentException("Invalid operand: " + currentToken.getValue());
        }

        advance(); // Move to the next token
      }
    }

    return instructionNode;
  }

  public static void main(String[] args) {

    Tokenizer tokenizer = new Tokenizer();
    String assemblyCode = "add 1 2 1";
    // String assemblyCode = "HALT";
    // String assemblyCode = "num .FILL 10\n HALT\n start ADD x1 x2 x3
    // dsadasdasdasdsdsad asdas";
    // String assemblyCode = "ADD x1 x2 x1 dsadsa dsa das das d as\n HALT";
    // String assemblyCode = "start add x1 x2 x3";
    // String assemblyCode = "lw x0 x1 five\nfive .fill 5 ";
    // String assemblyCode = "lw x0 x1 five load reg1 with 5 (uses symbolic address)
    // \n five .fill 5";
    // String assemblyCode = "five .fill 5 \n lw x0 x1 five";
    // String assemblyCode = "five .fill 5";

    List<Tokenizer.Token> tokens1 = tokenizer.tokenize(assemblyCode);
    // List<Tokenizer.Token> tokens2 = tokenizer.tokenize(assemblyCode);

    Parser parser1 = new Parser(tokens1);
    // Parser parser2 = new Parser(tokens2);

    List<List<ASTNode>> ast1 = parser1.parseProgram();

    // Generate machine code from AST
    CodeGenerator codeGen = new CodeGenerator();
    for (List<ASTNode> nodes : ast1) {
      for (ASTNode node : nodes) {
        // System.out.println(node.toString());
        node.firstAccept(codeGen);
      }
    }

    // parser1.setVariables(codeGen.getVariableMap());
    // parser2.setVariables(parser1.getVariables());
    // List<List<ASTNode>> ast2 = parser2.parseProgram();

    for (List<ASTNode> nodes : ast1) {
      for (ASTNode node : nodes) {
        // System.out.println(node.toString());
        node.accept(codeGen);
      }
    }

    /* Morph codeGen.variableMap to parser.variables */
    // System.out.println(codeGen.getVariableMap().get("five"));
    // System.out.println(parser.getSymbolicInteger("five"));

    // Output the generated machine code
    System.out.println("Generated Machine Code:");
    System.out.println(codeGen.getMachineCode());

  }
}