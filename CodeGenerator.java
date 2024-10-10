import java.util.*;

public class CodeGenerator implements NodeVisitor {

  private StringBuilder machineCode = new StringBuilder();

  // Mapping for instruction encodings
  private Map<String, String> opcodeMap = new HashMap<>();

  private Map<String, Integer> variableMap = new HashMap<>();
  private Map<String, Integer> addressMap = new HashMap<>();

  public Map<String, Integer> getVariableMap() {
    return variableMap;
  }

  public void setAddressMap(Map<String, Integer> addressMap) {
    this.addressMap = addressMap;
  }

  private int currentAddress = 0;

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

  }

  public String getMachineCode() {
    return machineCode.toString();
  }

  public String getDecMachineCode() {
    return convertBinaryStream(new StringBuilder(machineCode.toString())).toString();
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

  // Function to convert a decimal number to 2's complement 16-bit binary string
  public static String toTwosComplement16Bit(int number) {
    // Check if the number fits in 16-bit signed range
    if (number < -32768 || number > 32767) {
      throw new IllegalArgumentException("Number out of 16-bit range: " + number);
    }

    // Convert to 2's complement 16-bit representation
    short twosComplement = (short) number; // short is 16-bit in Java

    // Return the result in 16-bit binary format
    return String.format("%16s", Integer.toBinaryString(twosComplement & 0xFFFF))
        .replace(' ', '0');
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
    currentAddress++;
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
      String label = node.getOperands().get(1).getValue();
      if (addressMap.get(label) == null) {
        throw new IllegalArgumentException("Unknown label: " + node.getOperands().get(1).getValue());
      }
      int value = addressMap.get(label);
      variableMap.put(name, value);
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
      String label = node.getOperands().get(1).getValue();
      if (addressMap.get(label) == null) {
        throw new IllegalArgumentException("Unknown label: " + node.getOperands().get(1).getValue());
      }
      int value = addressMap.get(label);
      variableMap.put(name, value);
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

    ((NumberNode) node.getOperands().get(2)).accept(this); // Bits 21-19 regA (rs)

    ((NumberNode) node.getOperands().get(1)).accept(this); // Bits 18-16 regB (rt)

    machineCode.append("0000000000000"); // Bits 15-3 Not used

    // Bits 2-0 Destination register (rd)
    ((NumberNode) node.getOperands().get(0)).accept(this);

  }

  public void iTypeVisit(InstructionNode node, String opCode) {

    long imm;
    String immediate;
    // Expecting the immediate to be the first operand
    if (node.getOperands().get(2) instanceof LabelNode) {
      String label = node.getOperands().get(2).getValue();
      if (addressMap.get(label) != null) {
        if (node.getInstruction().equals("LW") || node.getInstruction().equals("SW")) {
          imm = addressMap.get(label) & 0xFFF;
        } else {
          // BEQ
          // Calculate the relative offset
          imm = (addressMap.get(label) - currentAddress - 1);
        }

      } else {
        throw new IllegalArgumentException("Unknown label: " + node.getOperands().get(2).getValue());
      }

    } else if (node.getOperands().get(2) instanceof NumberNode) {
      NumberNode immediateNode = (NumberNode) node.getOperands().get(2);
      imm = immediateNode.getNumber(); // Mask to get the lower 12 bits
    } else {
      throw new IllegalArgumentException("Expected a number or label for immediate value");
    }

    immediate = toTwosComplement16Bit((int) imm);

    NumberNode rsNode = (NumberNode) node.getOperands().get(1);
    NumberNode rtNode = (NumberNode) node.getOperands().get(0);

    // Get the immediate value
    // String immediate = String.format("%016d",
    // Long.parseLong(Long.toBinaryString(imm)));

    // Build the machine code
    machineCode.append(opCode); // immediate
    rtNode.accept(this); // Destination register (rd)
    rsNode.accept(this);
    machineCode.append("").append(immediate); // opcode

  }

  public void oTypeVisit(InstructionNode node, String opCode) {
    // Bits 24-22 opcode
    // Bits 21-0 Not Used
    machineCode.append(opCode).append("0000000000000000000000");
  }

  public void jTypeVisit(InstructionNode node, String opCode) {
    // Bits 24-22 opcode
    // Bits 21-19 reg A (rs)
    // Bits 18-16 reg B (rd)
    // Bits 15-0 Not Used
    machineCode.append(opCode);
    ((NumberNode) node.getOperands().get(0)).accept(this); // Bits 18-16 regB (rt)
    ((NumberNode) node.getOperands().get(1)).accept(this); // Bits 18-16 regB (rt)
    machineCode.append("0000000000000000");
  }

  @Override
  public void visit(LabelNode node) {
    // machineCode.append(" LABEL(").append(node.getLabel()).append(")");
  }

  @Override
  public void visit(NumberNode node) {
    String value = String.format("%03d", Integer.parseInt(Integer.toBinaryString(node.getNumber())));
    machineCode.append("").append(value);
  }

  public static int BinaryToDecimal(String bin) {
    int decimalNumber = 0, i = 0;
    long remainder;
    long num = Long.parseLong(bin);
    while (num != 0) {
      remainder = num % 10;
      num /= 10;
      decimalNumber += remainder * Math.pow(2, i);
      ++i;
    }

    return decimalNumber;
  }

  public static void main(String[] args) {

    Tokenizer tokenizer = new Tokenizer();
    // String assemblyCode = "add 1 2 1";
    // String assemblyCode = "HALT";
    String assemblyCode = "halt";

    List<Tokenizer.Token> tokens = tokenizer.tokenize(assemblyCode);

    Parser parser = new Parser(tokens);

    List<List<ASTNode>> ast1 = parser.parseProgram();
    CodeGenerator codeGen = new CodeGenerator();
    codeGen.setAddressMap(parser.getAddressMap());

    // Generate machine code from AST

    for (List<ASTNode> nodes : ast1) {
      for (ASTNode node : nodes) {
        node.firstAccept(codeGen);
      }
    }

    for (List<ASTNode> nodes : ast1) {
      for (ASTNode node : nodes) {
        node.accept(codeGen);
      }
    }
    System.out.println("Generated Machine Code:");
    System.out.println(codeGen.getMachineCode());

    // System.out.println(codeGen.getVariableMap().get("stAddr"));

  }
}
