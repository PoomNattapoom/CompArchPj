import java.util.*;

public class CodeGenerator implements NodeVisitor {
  private StringBuilder machineCode = new StringBuilder();

  // Mapping for instruction encodings
  private Map<String, String> opcodeMap = new HashMap<>();
  private Map<String, String> registerMap = new HashMap<>();

  private Map<String, Integer> variableMap = new HashMap<>();
  private Map<String, String> addressMap = new HashMap<>();

  public Map<String, Integer> getVariableMap() {
    return variableMap;
  }

  public CodeGenerator() {

    // ADD, NAND, LW, SW, BEQ, JALR, HALT, NOOP

    // Example opcode mapping
    opcodeMap.put("ADD", "000");
    opcodeMap.put("NAND", "001");
    opcodeMap.put("LW", "010");
    opcodeMap.put("SW", "011");
    opcodeMap.put("BEQ", "100");
    opcodeMap.put("JALR", "101");
    opcodeMap.put("HALT", "110");
    opcodeMap.put("NOOP", "111");

    // Register mapping
    registerMap.put("x0", "000");
    registerMap.put("x1", "001");
    registerMap.put("x2", "010");
    registerMap.put("x3", "011");
    registerMap.put("x4", "100");
    registerMap.put("x5", "101");
    registerMap.put("x6", "110");
    registerMap.put("x7", "111");
  }

  public String getMachineCode() {
    return machineCode.toString();
  }

  public void firstVisit(InstructionNode node) {
    if (node.getInstruction().equals(".FILL")) {
      fillFirstVisit(node);
    }
  }

  @Override
  public void visit(InstructionNode node) {

    // System.out.println("Visiting instruction: " + node.getInstruction());

    String opCode = opcodeMap.getOrDefault(node.getInstruction(), "????");

    // R-format instructions
    if (opCode.equals("000") || opCode.equals("001")) { // AND NAND
      rTypeVisit(node, opCode);
    }

    // Handle I-format
    else if (opCode.equals("010") || opCode.equals("011") || opCode.equals("100")) { // LW SW BEQ
      iTypeVisit(node, opCode);
    }

    // Handle J-format
    else if (opCode.equals("101")) { // JALR
      jTypeVisit(node, opCode);
    }

    // Handle O-format
    else if (opCode.equals("110") || opCode.equals("111")) { // HALT NOOP
      oTypeVisit(node, opCode);
    } else if (node.getInstruction().equals(".FILL")) {
      fillVisit(node);
    } else {
      throw new IllegalArgumentException("Unknown instruction: " + node.getInstruction());
    }

    machineCode.append("\n");

  }

  public void fillFirstVisit(InstructionNode node) {
    // Get the label

    String name = null;
    if (node.getOperands().get(0) instanceof LabelNode) {
      name = node.getOperands().get(0).getValue();
    } else {
      throw new IllegalArgumentException("Expected a label for .FILL instruction");
    }

    // Get the value
    if (node.getOperands().get(1) instanceof LabelNode) {
      String value = node.getOperands().get(1).getValue();
      addressMap.put(name, value);
      // machineCode.append(value);
    } else if (node.getOperands().get(1) instanceof NumberNode) {
      NumberNode immediateNode = (NumberNode) node.getOperands().get(1);
      int value = immediateNode.getNumber();
      variableMap.put(name, value); //
      // machineCode.append(value);
    } else {
      throw new IllegalArgumentException("Expected a number or label for .FILL instruction");
    }
  }

  public void fillVisit(InstructionNode node) {
    // Get the label

    String name = null;
    if (node.getOperands().get(0) instanceof LabelNode) {
      name = node.getOperands().get(0).getValue();
    } else {
      throw new IllegalArgumentException("Expected a label for .FILL instruction");
    }

    // Get the value
    if (node.getOperands().get(1) instanceof LabelNode) {
      String value = node.getOperands().get(1).getValue();
      addressMap.put(name, value);
      machineCode.append(value);
    } else if (node.getOperands().get(1) instanceof NumberNode) {
      NumberNode immediateNode = (NumberNode) node.getOperands().get(1);
      int value = immediateNode.getNumber();
      variableMap.put(name, value); //
      machineCode.append(value);
    } else {
      throw new IllegalArgumentException("Expected a number or label for .FILL instruction");
    }

  }

  public void rTypeVisit(InstructionNode node, String opCode) {

    machineCode.append(opCode); // Bits 24-22 opcode

    ((RegisterNode) node.getOperands().get(2)).accept(this); // Bits 21-19 regA (rs)

    ((RegisterNode) node.getOperands().get(1)).accept(this); // Bits 18-16 regB (rt)

    machineCode.append("0000000000000"); // Bits 15-3 Not used

    // Bits 2-0 Destination register (rd)
    ((RegisterNode) node.getOperands().get(0)).accept(this);

  }

  public void iTypeVisit(InstructionNode node, String opCode) {
    int imm;
    // Expecting the immediate to be the first operand
    if (node.getOperands().get(2) instanceof LabelNode) {
      imm = (variableMap.get(node.getOperands().get(2).getValue())) & 0xFFF;

    } else {
      NumberNode immediateNode = (NumberNode) node.getOperands().get(2);
      imm = immediateNode.getNumber() & 0xFFF; // Mask to get the lower 12 bits
    }

    RegisterNode rsNode = (RegisterNode) node.getOperands().get(1);
    RegisterNode rtNode = (RegisterNode) node.getOperands().get(0);

    // Get the immediate value
    String immediate = String.format("%016d", Integer.parseInt(Integer.toBinaryString(imm)));

    // Build the machine code
    machineCode.append(opCode); // immediate
    rtNode.accept(this); // Destination register (rd)
    rsNode.accept(this);
    machineCode.append(" ").append(immediate); // opcode
  }

  public void oTypeVisit(InstructionNode node, String opCode) {
    // Bits 24-22 opcode
    // Bits 21-0 Not Used
    machineCode.append(opCode).append(" 0000000000000000000000");
  }

  public void jTypeVisit(InstructionNode node, String opCode) {
    // Bits 24-22 opcode
    // Bits 21-19 reg A (rs)
    // Bits 18-16 reg B (rd)
    // Bits 15-0 Not Used
    machineCode.append(opCode);
    ((RegisterNode) node.getOperands().get(0)).accept(this); // Bits 18-16 regB (rt)
    ((RegisterNode) node.getOperands().get(1)).accept(this); // Bits 18-16 regB (rt)
    machineCode.append(" 0000000000000000");
  }

  @Override
  public void visit(RegisterNode node) {
    // Lookup the machine code for the register
    String registerCode = registerMap.getOrDefault(node.getRegister(), "????");
    machineCode.append(" ").append(registerCode);
  }

  @Override
  public void visit(LabelNode node) {
    // machineCode.append(" LABEL(").append(node.getLabel()).append(")");
  }

  @Override
  public void visit(NumberNode node) {
    machineCode.append(" ").append(node.getNumber());
  }

  public static void main(String[] args) {
    Tokenizer tokenizer = new Tokenizer();
    String assemblyCode = "add x1 x2 x3 \n num .FILL 10";
    // String assemblyCode = "HALT";
    // String assemblyCode = "num .FILL 10";

    List<Tokenizer.Token> tokens = tokenizer.tokenize(assemblyCode);
    Parser parser = new Parser(tokens);
    List<List<ASTNode>> ast = parser.parseProgram();

    // Generate machine code from AST
    CodeGenerator codeGen = new CodeGenerator();
    for (List<ASTNode> nodes : ast) {
      for (ASTNode node : nodes) {
        // System.out.println(node.toString());
        node.accept(codeGen);
      }
    }

    // Output the generated machine code
    System.out.println("Generated Machine Code:");
    System.out.println(codeGen.getMachineCode());

  }
}
