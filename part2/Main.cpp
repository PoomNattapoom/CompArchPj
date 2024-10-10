#include <iostream>
#include <fstream>
#include <vector>
#include <iomanip>

using namespace std;

// ขนาดของ memory และจำนวน register ของ SMC
const int NUM_REGISTERS = 8;
const int MEMORY_SIZE = 65536;

// โครงสร้างสำหรับเก็บสถานะของ machine (state ของ SMC)
struct State {
    int pc = 0;                          // Program Counter (PC)
    int memory[MEMORY_SIZE] = {0};       // Memory
    int registers[NUM_REGISTERS] = {0};  // Registers
    int numMemory = 0;                   // จำนวนคำสั่งใน memory
};

void printState(const State& state) {
    cout << "@@@ state before cycle " << endl;
    cout << "\tpc " << state.pc << endl;
    cout << "\tmemory:" << endl;
    for (int i = 0; i < state.numMemory; ++i) {
        cout << "\t\tmem[ " << i << " ] " << state.memory[i] << endl;
    }
    cout << "\tregisters:" << endl;
    for (int i = 0; i < NUM_REGISTERS; ++i) {
        cout << "\t\treg[ " << i << " ] " << state.registers[i] << endl;
    }
    cout << "end state" << endl;
}

// ฟังก์ชันแปลง 16-bit offsetField เป็น 32-bit integer
int convertNum(int num) {
    if (num & (1 << 15)) {
        num -= (1 << 16);
    }
    return num;
}

// ฟังก์ชันสำหรับจำลองการทำงานของ machine
void simulate(State& state) {
    bool halted = false;
    int cycle = 0; // นับจำนวนรอบของการจำลอง
    int instructionCount = 0; // ตัวนับจำนวนคำสั่งที่ถูก execute

    while (!halted) {
        if (cycle++ > 5000) { // จำกัดจำนวนรอบเพื่อป้องกัน Infinite Loop
            cerr << "Error: Infinite loop detected, exiting simulation after 5000 cycles." << endl;
            break;
        }

        // ตรวจสอบว่า PC อยู่ในขอบเขตของ memory หรือไม่
        if (state.pc < 0 || state.pc >= state.numMemory) {
            cerr << "Error: Program counter out of bounds, exiting simulation." << endl;
            break;
        }

        printState(state);  // Print state ก่อนที่จะ execute คำสั่ง

        int instruction = state.memory[state.pc];
        int opcode = (instruction >> 22) & 0x7;  // ดึง opcode จาก bits 24-22
        int regA = (instruction >> 19) & 0x7;   // ดึงค่า regA จาก bits 21-19
        int regB = (instruction >> 16) & 0x7;   // ดึงค่า regB จาก bits 18-16
        int destReg = instruction & 0x7;        // ดึงค่า destReg จาก bits 2-0
        int offsetField = instruction & 0xFFFF; // ดึงค่า offsetField จาก bits 15-0

        switch (opcode) {
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
                if (state.registers[regA] == state.registers[regB]) {
                    int newPC = state.pc + convertNum(offsetField);
                    if (newPC >= 0 && newPC < state.numMemory) {
                        state.pc = newPC; // กระโดดไปยังตำแหน่งที่กำหนด
                    } else {
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
                    if (targetPC >= 0 && targetPC < state.numMemory) {
                        state.pc = targetPC; // กระโดดไปยังตำแหน่งที่กำหนด
                    } else {
                        cerr << "Error: Jump target out of bounds." << endl;
                    }
                }
                continue; // ทำให้ไม่เพิ่มค่า PC
            case 6: // halt
                halted = true;
                cout << "Program halted." << endl;  // เพิ่มข้อความแสดงเมื่อโปรแกรมหยุดทำงาน
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
        if (opcode != 4 && opcode != 5) { // หลีกเลี่ยงการเพิ่ม PC เมื่อใช้คำสั่ง beq หรือ jalr
            state.pc++;
        }
    }

    // Print state สุดท้ายก่อน exit
    printState(state);
    cout << "Total instructions executed: " << instructionCount << endl; // Print instruction count
}

int main(int argc, char* argv[]) {
    if (argc != 2) {
        cerr << "Usage: " << argv[0] << " <machine-code-file>" << endl;
        return 1;
    }

    ifstream infile(argv[1]);
    if (!infile) {
        cerr << "Error: Cannot open file " << argv[1] << endl;
        return 1;
    }

    State state;

    // อ่าน machine code จากไฟล์และเก็บใน memory
    int instruction;
    while (infile >> instruction) {
        if (state.numMemory < MEMORY_SIZE) {
            state.memory[state.numMemory++] = instruction;
        } else {
            cerr << "Error: Memory overflow, too many instructions." << endl;
            return 1;
        }
    }

    // ทำการ simulate program
    simulate(state);

    return 0;
}
