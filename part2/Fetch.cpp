#include "Simulator.h"

/* Fetch the instruction at the current PC */
int fetch(Machine *state){
    return state->mem[state->pc]; //return the instruction at PC
}