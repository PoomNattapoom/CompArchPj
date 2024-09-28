import java.util.HashMap;
import java.util.Map;

public class Assembler {

  // Symbol table to store label addresses
  private static Map<String, Integer> symbolTable = new HashMap<>();
  private static int programCounter = 0;

  public static void main(String[] args) {
    // Sample assembly program
    String[] assemblyProgram = {
        "lw 0 1 five",
        "lw 1 2 3",
        "start add 1 2 1",
        "beq 0 1 2",
        "beq 0 0 start",
        "noop",
        "done halt",
        "five .fill 5",
        "neg1 .fill -1",
        "stAddr .fill start"
    };

    // First pass: Build the symbol table
    firstPass(assemblyProgram);

    // Second pass: Generate machine code
    secondPass(assemblyProgram);
  }

  // Instruction set for the assembler
  private static final String[] INSTRUCTIONS = { "add", "nand", "lw", "sw", "beq", "jalr", "halt", "noop", ".fill" };

  private static void firstPass(String[] program) {
    for (String line : program) {
      String[] parts = line.split("\\s+");

      // Check if the first part is a label (it shouldn't be an instruction)
      if (isLabel(parts[0]) && !isInstruction(parts[0])) {
        String label = parts[0];
        if (symbolTable.containsKey(label)) {
          throw new RuntimeException("Error: Duplicate label - " + label);
        }
        symbolTable.put(label, programCounter);
      }
      programCounter++;
    }
    programCounter = 0; // Reset for second pass
  }

  // Check if the part is an instruction
  private static boolean isInstruction(String part) {
    for (String instruction : INSTRUCTIONS) {
      if (instruction.equals(part)) {
        return true;
      }
    }
    return false;
  }

  // Check if the part is a label
  private static boolean isLabel(String part) {
    return Character.isLetter(part.charAt(0));
  }

  // Second pass to generate machine code
  private static void secondPass(String[] program) {
    for (String line : program) {
      String[] parts = line.split("\\s+");
      String instruction = isLabel(parts[0]) ? parts[1] : parts[0];

      switch (instruction) {
        case "add":
          generateRType(parts);
          break;
        case "nand":
          generateRType(parts);
          break;
        case "lw":
        case "sw":
        case "beq":
          generateIType(parts);
          break;
        case "jalr":
          generateJType(parts);
          break;
        case "halt":
        case "noop":
          generateOType(parts);
          break;
        case ".fill":
          generateFill(parts);
          break;
        default:
          throw new RuntimeException("Error: Unknown instruction - " + instruction);
      }
    }
  }

  private static void generateRType(String[] parts) {
    // R-type format: opcode (7 bits) + regA (5 bits) + regB (5 bits) + destReg (5
    // bits) + function (10 bits)
    int opcode = parts[0].equals("add") ? 0 : 1; // Example: 0 for add, 1 for nand
    int regA = Integer.parseInt(parts[1]);
    int regB = Integer.parseInt(parts[2]);
    int destReg = Integer.parseInt(parts[3]);
    int machineCode = (opcode << 25) | (regA << 20) | (regB << 15) | (destReg << 10);
    System.out.println(machineCode);
  }

  private static void generateIType(String[] parts) {
    // I-type format: opcode (7 bits) + regA (5 bits) + regB (5 bits) + offset (15
    // bits)
    int opcode = getOpcode(parts[0]); // Example: lw, sw, beq
    int regA = Integer.parseInt(parts[1]);
    int regB = Integer.parseInt(parts[2]);
    int offset;

    if (isNumeric(parts[3])) {
      offset = Integer.parseInt(parts[3]);
    } else {
      offset = symbolTable.get(parts[3]) - programCounter - 1;
    }

    if (offset < -32768 || offset > 32767) {
      throw new RuntimeException("Error: Offset out of bounds");
    }

    int machineCode = (opcode << 25) | (regA << 20) | (regB << 15) | (offset & 0xFFFF);
    System.out.println(machineCode);
  }

  private static void generateJType(String[] parts) {
    // J-type format: opcode (7 bits) + regA (5 bits) + regB (5 bits)
    int opcode = 4; // Example: opcode for jalr
    int regA = Integer.parseInt(parts[1]);
    int regB = Integer.parseInt(parts[2]);
    int machineCode = (opcode << 25) | (regA << 20) | (regB << 15);
    System.out.println(machineCode);
  }

  private static void generateOType(String[] parts) {
    // O-type format: opcode (7 bits)
    int opcode = parts[0].equals("halt") ? 6 : 7; // Example: opcode for halt and noop
    int machineCode = (opcode << 25);
    System.out.println(machineCode);
  }

  private static void generateFill(String[] parts) {
    // Fill instruction stores a number or the address of a label
    if (isNumeric(parts[1])) {
      System.out.println(Integer.parseInt(parts[1]));
    } else {
      System.out.println(symbolTable.get(parts[1]));
    }
  }

  private static int getOpcode(String instruction) {
    switch (instruction) {
      case "lw":
        return 2;
      case "sw":
        return 3;
      case "beq":
        return 4;
      default:
        throw new RuntimeException("Error: Unknown instruction - " + instruction);
    }
  }

  private static boolean isNumeric(String str) {
    return str.matches("-?\\d+");
  }
}
