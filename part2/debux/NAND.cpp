#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "Simulator.h"

int main() {
    // Create and initialize the machine state
    MachineState state;
    memset(&state, 0, sizeof(MachineState));  // Initialize registers to 0
    
    int opcode = 1; 
    int regA = 0;
    int regB = 1; 
    int destReg = 4;

    state.reg[regA] = 0xF0F0F0F0;  // Binary 11110000111100001111000011110000
    state.reg[regB] = 0x0F0F0F0F;  // Binary 00001111000011110000111100001111

    printf("Before NAND: regA = 0x%X, regB = 0x%X\n", state.reg[regA], state.reg[regB]);

    // awser should be 00000000000000000000000000000000 or 0xFFFFFFFF
    state.reg[destReg] = ~(state.reg[regA] & state.reg[regB]);  // NAND operation

    printf("After NAND: reg[%d] = 0x%X\n", destReg, state.reg[destReg]);

    return 0;
}
