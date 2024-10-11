#include <iostream>
#include <fstream>
#include <vector>
#include <iomanip>

using namespace std;

// ขนาดของ memory และจำนวน register ของ SMC
const int NUM_REGISTERS = 8;
const int MEMORY_SIZE = 65536;

// โครงสร้างสำหรับเก็บสถานะของ machine (state ของ SMC)
struct State
{
  int pc = 0;                         // Program Counter (PC)
  int memory[MEMORY_SIZE] = {0};      // Memory
  int registers[NUM_REGISTERS] = {0}; // Registers
  int numMemory = 0;                  // จำนวนคำสั่งใน memory
};

int instructionCount = 0;
/* Simulate machine instructions */
while (1)
{
  if (instructionCount == 5000)
    break;
  printState(&state); // Print state before executing instruction

  int instruction = fetch(&state); // Fetch instruction

  if (instruction == 0)
  { // Assuming '0' represents the halt instruction
    halt();
    break;
  }
  cout << "\tregisters:" << endl;
  for (int i = 0; i < NUM_REGISTERS; ++i)
  {
    cout << "\t\treg[ " << i << " ] " << state.registers[i] << endl;
  }
  cout << "end state" << endl;
}

// ฟังก์ชันแปลง 16-bit offsetField เป็น 32-bit integer
int convertNum(int num)
{
  if (num & (1 << 15))
  {
    num -= (1 << 16);
  }
  return num;
}

// ฟังก์ชันสำหรับจำลองการทำงานของ machine
void simulate(State &state)
{
  bool halted = false;
  int cycle = 0;                                             // นับจำนวนรอบของการจำลอง
  int instructionCount = 0;                                  // ตัวนับจำนวนคำสั่งที่ถูก execute

<<<<<<< HEAD
case 1:                                                      // NAND
  destReg = instruction & 0x7;                               // Destination register is in the last 3 bits
  state.reg[destReg] = ~(state.reg[regA] & state.reg[regB]); // NAND operation
  break;

case 2: // LW
  offset = instruction & 0xFFFF;
  printf("Opcode SW detected. Offset before sign extension: %d\n", state.reg[regA] + offset);

  if (offset & (1 << 15))
  {
    offset -= (1 << 16);
  }
  printf("Offset after sign extension: %d\n", offset);
  state.reg[regB] = state.mem[state.reg[regA] + offset];
  break;

case 3: // SW
  offset = instruction & 0xFFFF;
  printf("Opcode SW detected. Offset before sign extension: %d\n", state.reg[regA] + offset);

  if (offset & (1 << 15))
  {
    offset -= (1 << 16);
  }
  printf("Offset after sign extension: %d\n", offset);
  state.mem[state.reg[regA] + offset] = state.reg[regB];
  if (state.numMemory + state.reg[7] > state.highestNumMemory)
  {
    state.highestNumMemory = state.numMemory + state.reg[7];
  } // update size for print more mem
  break;

case 4: // BEQ
  offset = instruction & 0xFFFF;
  if (offset & (1 << 15))
  {
    offset -= (1 << 16); // Sign-extend the offset
  }
  if (state.reg[regA] == state.reg[regB])
  {
    if (offset == 0)
    {
      printf("warning: BEQ has zero offset, Please check you input\n");
      return 0;
    }
    else
    {
      state.pc += offset; // Apply the branch if condition is met
    }
  }
  else
  {
    state.pc += 1; // No branch, move to next instruction
  }
  break;

case 5: // JALR
  if (regA == regB)
  {
    int tempPC = state.pc + 1;
    state.pc = state.reg[regA]; // Jump to address in regA
    state.reg[regB] = tempPC;   // Store PC+1 in regB
  }
  else
  {
    state.reg[regB] = state.pc + 1;          // Store PC+1 in regB
    int targetAddress = state.reg[regA] - 1; // Adjust target address
    state.pc = targetAddress;                // Jump to adjusted address
  }
  break;

case 6: // HALT
  printf("machine halted\n");
  printf("total of %d instructions executed\n", instructionCount + 1);
  return 0;
  == == == =
               while (!halted)
  {
    if (cycle++ > 5000)
    { // จำกัดจำนวนรอบเพื่อป้องกัน Infinite Loop
      cerr << "Error: Infinite loop detected, exiting simulation after 5000 cycles." << endl;
      break;
    }

    // ตรวจสอบว่า PC อยู่ในขอบเขตของ memory หรือไม่
    if (state.pc < 0 || state.pc >= state.numMemory)
    {
      cerr << "Error: Program counter out of bounds, exiting simulation." << endl;
      break;
    }

    printState(state); // Print state ก่อนที่จะ execute คำสั่ง

    int instruction = state.memory[state.pc];
    int opcode = (instruction >> 22) & 0x7; // ดึง opcode จาก bits 24-22
    int regA = (instruction >> 19) & 0x7;   // ดึงค่า regA จาก bits 21-19
    int regB = (instruction >> 16) & 0x7;   // ดึงค่า regB จาก bits 18-16
    int destReg = instruction & 0x7;        // ดึงค่า destReg จาก bits 2-0
    int offsetField = instruction & 0xFFFF; // ดึงค่า offsetField จาก bits 15-0
>>>>>>> 4596391484dc352a7fa764f5a8bf6178d75467f5

    switch (opcode)
    {
    case 0: // add
      state.registers[destReg] = state.registers[regA] + state.registers[regB];
      instructionCount++; // Increment instruction count
      break;
    case 1: // nand
      state.registers[destReg] = ~(state.registers[regA] & state.registers[regB]);
      instructionCount++; // Increment instruction count
      break;
    case 2: // lw (load word)
      state.registers[regB] = state.memory[state.registers[regA] + convertNum(offsetField)];
      instructionCount++; // Increment instruction count
      break;
    case 3: // sw (store word)
      state.memory[state.registers[regA] + convertNum(offsetField)] = state.registers[regB];
      instructionCount++; // Increment instruction count
      break;
    case 4: // beq (branch if equal)
      if (state.registers[regA] == state.registers[regB])
      {
        int newPC = state.pc + convertNum(offsetField);
        if (newPC >= 0 && newPC < state.numMemory)
        {
          state.pc = newPC; // กระโดดไปยังตำแหน่งที่กำหนด
        }
        else
        {
          cerr << "Error: Branch target out of bounds." << endl;
        }
        continue; // ทำให้ไม่เพิ่มค่า PC
      }
      instructionCount++; // Increment instruction count even if not branching
      break;
    case 5: // jalr (jump and link register)
      state.registers[regB] = state.pc + 1;
      {
        int targetPC = state.registers[regA] - 1; // PC จะเพิ่มขึ้นที่ด้านล่าง
        if (targetPC >= 0 && targetPC < state.numMemory)
        {
          state.pc = targetPC; // กระโดดไปยังตำแหน่งที่กำหนด
        }
        else
        {
          cerr << "Error: Jump target out of bounds." << endl;
        }
      }
      continue; // ทำให้ไม่เพิ่มค่า PC
    case 6:     // halt
      halted = true;
      cout << "Program halted." << endl; // เพิ่มข้อความแสดงเมื่อโปรแกรมหยุดทำงาน
      break;
    case 7: // noop
      // No operation
      instructionCount++; // Increment instruction count
      break;
    default:
      cerr << "Unknown opcode: " << opcode << endl;
      exit(1);
    }

    // เพิ่มค่า PC หากไม่ใช่คำสั่ง jump
    if (opcode != 4 && opcode != 5)
    { // หลีกเลี่ยงการเพิ่ม PC เมื่อใช้คำสั่ง beq หรือ jalr
      state.pc++;
    }
  }
<<<<<<< HEAD
  printf("____opcode is %d", opcode, "_____\n");
  // printState(&state);
  instructionCount++;
  updatePC(&state); // Update PC after executing instruction
}

printState(&state); // Print state before exiting
return 0;
== == == =

             // Print state สุดท้ายก่อน exit
    printState(state);
cout << "Total instructions executed: " << instructionCount << endl; // Print instruction count
>>>>>>> 4596391484dc352a7fa764f5a8bf6178d75467f5
}

int main(int argc, char *argv[])
{
  if (argc != 2)
  {
    cerr << "Usage: " << argv[0] << " <machine-code-file>" << endl;
    return 1;
  }

  ifstream infile(argv[1]);
  if (!infile)
  {
    cerr << "Error: Cannot open file " << argv[1] << endl;
    return 1;
  }

  State state;

  // อ่าน machine code จากไฟล์และเก็บใน memory
  int instruction;
  while (infile >> instruction)
  {
    if (state.numMemory < MEMORY_SIZE)
    {
      state.memory[state.numMemory++] = instruction;
    }
    else
    {
      cerr << "Error: Memory overflow, too many instructions." << endl;
      return 1;
    }
  }

  // ทำการ simulate program
  simulate(state);

  return 0;
}
