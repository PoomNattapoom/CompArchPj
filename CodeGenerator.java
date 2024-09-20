import java.util.*;

public class CodeGenerator implements NodeVisitor {
  private StringBuilder machineCode = new StringBuilder();

  // Mapping for instruction encodings
  private Map<String, String> opcodeMap = new HashMap<>();
  private Map<String, String> funct7Map = new HashMap<>();
  private Map<String, String> funct3Map = new HashMap<>();
  private Map<String, String> registerMap = new HashMap<>();

  public CodeGenerator() {
    // Example opcode mapping

    // R-format instructions
    opcodeMap.put("ADD", "0110011");
    opcodeMap.put("SUB", "0110011");
    opcodeMap.put("SLL", "0110011");
    opcodeMap.put("XOR", "0110011");
    opcodeMap.put("SRL", "0110011");
    opcodeMap.put("SRA", "0110011");
    opcodeMap.put("OR", "0110011");
    opcodeMap.put("AND", "0110011");
    opcodeMap.put("LR.D", "0110011");
    opcodeMap.put("SC.D", "0110011");

    // I-format instructions
    opcodeMap.put("LB", "0000011");
    opcodeMap.put("LH", "0000011");
    opcodeMap.put("LW", "0000011");
    opcodeMap.put("LD", "0000011");
    opcodeMap.put("LBU", "0000011");
    opcodeMap.put("LHU", "0000011");
    opcodeMap.put("LWU", "0000011");
    opcodeMap.put("ADDI", "0010011");
    opcodeMap.put("SLLI", "0010011");
    opcodeMap.put("XORI", "0010011");
    opcodeMap.put("SRLI", "0010011");
    opcodeMap.put("SRAI", "0010011");
    opcodeMap.put("ORI", "0010011");
    opcodeMap.put("ANDI", "0010011");
    opcodeMap.put("JALR", "1100111");

    // S-format instructions
    opcodeMap.put("SB", "0100011");
    opcodeMap.put("SH", "0100011");
    opcodeMap.put("SW", "0100011");
    opcodeMap.put("SD", "0100011");

    // SB-format instructions
    opcodeMap.put("BEQ", "1100011");
    opcodeMap.put("BNE", "1100011");
    opcodeMap.put("BLT", "1100011");
    opcodeMap.put("BGE", "1100011");
    opcodeMap.put("BLTU", "1100011");
    opcodeMap.put("BGEU", "1100011");

    // U-format instructions
    opcodeMap.put("LUI", "0110111");
    opcodeMap.put("JAL", "1101111");

    // Mapping funct7 fields for different instructions
    funct7Map.put("ADD", "0000000");
    funct7Map.put("SUB", "0100000");
    funct7Map.put("ADDI", "0000000");

    // Mapping funct3 fields for different instructions
    funct3Map.put("ADD", "000");
    funct3Map.put("SUB", "000");
    funct3Map.put("ADDI", "000");
    funct3Map.put("SD", "011");

    // Register mapping
    registerMap.put("x0", "00000");
    registerMap.put("x1", "00001");
    registerMap.put("x2", "00010");
    registerMap.put("x3", "00011");
    registerMap.put("x4", "00100");
    registerMap.put("x5", "00101");
    registerMap.put("x6", "00110");
    registerMap.put("x7", "00111");
  }

  public String getMachineCode() {
    return machineCode.toString();
  }

  @Override
  public void visit(InstructionNode node) {

    // System.out.println("Visiting instruction: " + node.getInstruction());
    // for (ASTNode operand : node.getOperands()) {
    // System.out.println("Operand type: " + operand.getClass().getSimpleName());
    // }

    String opCode = opcodeMap.getOrDefault(node.getInstruction(), "????");

    // R-format instructions (e.g., ADD, SUB)
    if (opCode.equals("0110011")) {
      String funct7 = funct7Map.getOrDefault(node.getInstruction(), "????");
      String funct3 = funct3Map.getOrDefault(node.getInstruction(), "????");

      machineCode.append(funct7);
      // Operand 2 (rs2)
      ((RegisterNode) node.getOperands().get(2)).accept(this);
      // Operand 1 (rs1)
      ((RegisterNode) node.getOperands().get(1)).accept(this);
      // funct3
      machineCode.append(" ").append(funct3);
      // Destination register (rd)
      ((RegisterNode) node.getOperands().get(0)).accept(this);
      machineCode.append(" ").append(opCode).append("\n");
    }

    // Handle S-format (e.g., SW)
    else if (opCode.equals("0100011")) {
      String funct3 = funct3Map.getOrDefault(node.getInstruction(), "???");

      // The correct operand order for S-format instructions is:
      // 1. Immediate (NumberNode)
      // 2. Base register (RegisterNode)
      // 3. Source register (RegisterNode)

      // Safely check types to avoid ClassCastException
      if (node.getOperands().get(0) instanceof NumberNode &&
          node.getOperands().get(1) instanceof RegisterNode &&
          node.getOperands().get(2) instanceof RegisterNode) {

        NumberNode immediateNode = (NumberNode) node.getOperands().get(0); // Immediate (index 0)
        RegisterNode rs1Node = (RegisterNode) node.getOperands().get(1); // Base address register (rs1)
        RegisterNode rs2Node = (RegisterNode) node.getOperands().get(2); // Source register (rs2)

        // Convert the immediate to a 12-bit binary string
        int immediateValue = immediateNode.getNumber() & 0xFFF; // Mask to get lower 12 bits
        String immediate = String.format("%012d", Integer.parseInt(Integer.toBinaryString(immediateValue)));

        // Generate the machine code for S-format instruction
        machineCode.append(immediate.substring(0, 7)); // First 7 bits of immediate (funct7 part)
        rs2Node.accept(this); // Source register (rs2)
        rs1Node.accept(this); // Base address register (rs1)
        machineCode.append(" ").append(funct3).append(" "); // funct3
        machineCode.append(immediate.substring(7)).append(" "); // Last 5 bits of immediate
        machineCode.append(opCode).append("\n"); // opcode
      } else {
        throw new IllegalArgumentException("Operand types are incorrect for SD instruction.");
      }
    }

    // Handle I-format (e.g., ADDI)
    else if (opCode.equals("0010011") || opCode.equals("0000011")) {
      String funct3 = funct3Map.getOrDefault(node.getInstruction(), "???");

      // Expecting the immediate to be the first operand
      NumberNode immediateNode = (NumberNode) node.getOperands().get(2);
      RegisterNode rs1Node = (RegisterNode) node.getOperands().get(1);
      RegisterNode rdNode = (RegisterNode) node.getOperands().get(0);

      // Get the immediate value
      int immediateValue = immediateNode.getNumber() & 0xFFF; // Mask to get the lower 12 bits
      String immediate = String.format("%012d", Integer.parseInt(Integer.toBinaryString(immediateValue)));

      // Build the machine code
      machineCode.append(immediate); // immediate
      rs1Node.accept(this); // Source register (rs1)
      machineCode.append(" ").append(funct3); // funct3
      rdNode.accept(this); // Destination register (rd)
      machineCode.append(" ").append(opCode).append("\n"); // opcode
    }

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
}
