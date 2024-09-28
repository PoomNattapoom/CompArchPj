#include "Simulator.h"

/* Increment the PC to point to the next instruction */
void updatePC(MachineState *state){
    state->pc += 1; //increment PC
}