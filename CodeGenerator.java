import java.util.*;

public class CodeGenerator implements NodeVisitor {
  private StringBuilder machineCode = new StringBuilder();

  // Mapping for instruction encodings
  private Map<String, String> opcodeMap = new HashMap<>();
  private Map<String, String> registerMap = new HashMap<>();

  private Map<String, Integer> variableMap = new HashMap<>();

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

  @Override
  public void visit(InstructionNode node) {

    System.out.println("Visiting instruction: " + node.getInstruction());

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

    } else {
      throw new IllegalArgumentException("Unknown instruction: " + node.getInstruction());
    }

    machineCode.append("\n");

  }

  public void fillVisit(InstructionNode node) {
    // Get the label
    LabelNode labelNode = (LabelNode) node.getOperands().get(0);
    String name = labelNode.getLabel();

    // Get the value
    NumberNode valueNode = (NumberNode) node.getOperands().get(1);
    int value = valueNode.getNumber();

    // Store the value in the variable map
    variableMap.put(name, value);

    // Output the machine code
    machineCode.append(" ").append(value);
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

    // Expecting the immediate to be the first operand
    NumberNode immediateNode = (NumberNode) node.getOperands().get(2);
    RegisterNode rsNode = (RegisterNode) node.getOperands().get(1);
    RegisterNode rtNode = (RegisterNode) node.getOperands().get(0);

    // Get the immediate value
    int immediateValue = immediateNode.getNumber() & 0xFFF; // Mask to get the lower 12 bits
    String immediate = String.format("%016d", Integer.parseInt(Integer.toBinaryString(immediateValue)));

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
    machineCode.append(" LABEL(").append(node.getLabel()).append(")");
  }

  @Override
  public void visit(NumberNode node) {
    machineCode.append(" ").append(node.getNumber());
  }

  @Override
  public void visit(FillNode node) {

    // Get the label
    LabelNode labelNode = (LabelNode) node.getName();
    String name = labelNode.getLabel();

    // Get the value
    NumberNode valueNode = (NumberNode) node.getValue();
    int value = valueNode.getNumber();

    // Store the value in the variable map
    variableMap.put(name, value);

    // Output the machine code
    machineCode.append(value);
  }
}
