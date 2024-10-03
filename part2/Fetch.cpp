#include "Simulator.h"

/* Fetch the instruction at the current PC */
int fetch(MachineState *state){
    return state->mem[state->pc]; //return the instruction at PC
}