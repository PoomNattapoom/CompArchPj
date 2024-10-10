#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "Simulator.h"

int main() {
    MachineState state;
    state.numMemory = 10;
    state.highestNumMemory = 10;

    int regA = 0;
    int regB = 1;
    int offset = 4;
    state.reg[regA] = 100;  // Base address in register A
    state.reg[regB] = 50;   // Value to store in memory

    printf("Before SW, regA: %d, regB: %d, offset: %d\n", state.reg[regA], state.reg[regB], offset);

    // Simulate SW instruction
    if (offset & (1 << 15)) {
        offset -= (1 << 16);  // Sign extend
    }
    printf("Offset after sign extension: %d\n", offset);

    state.mem[state.reg[regA] + offset] = state.reg[regB];  // Store regB at memory[regA + offset]

    if (state.numMemory + state.reg[7] > state.highestNumMemory) {
        state.highestNumMemory = state.numMemory + state.reg[7];  // Update highest memory index used
    }

    printf("After SW, memory[%d]: %d\n", state.reg[regA] + offset, state.mem[state.reg[regA] + offset]);

    return 0;
}
