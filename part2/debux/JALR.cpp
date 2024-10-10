#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "Simulator.h"

int main() {
    MachineState state;
    state.numMemory = 10;
    state.highestNumMemory = 10;
    state.pc = 0; // Initialize the program counter

    // Initialize registers
    int regA = 0;  // Register A index
    int regB = 1;  // Register B index
    state.reg[regA] = 100;  // Base address in register A
    state.reg[regB] = 50;   // Value to store in memory

    // Print initial state
    printf("Before JALR: PC = %d, regA = %d, regB = %d\n", state.pc, state.reg[regA], state.reg[regB]);

    // Simulate JALR behavior
    if (state.reg[regA] == state.reg[regB]) {
        int tempPC = state.pc + 1;
        state.pc = state.reg[regA];  // Jump to address in regA
        state.reg[regB] = tempPC;     // Store PC+1 in regB
    } else {
        state.reg[regB] = state.pc + 1; // Store PC+1 in regB
        int targetAddress = state.reg[regA] - 1; // Adjust target address
        state.pc = targetAddress; // Jump to adjusted address
    }

    // Print the updated state
    printf("After JALR: PC = %d, regA = %d, regB = %d\n", state.pc, state.reg[regA], state.reg[regB]);

    return 0;
}
