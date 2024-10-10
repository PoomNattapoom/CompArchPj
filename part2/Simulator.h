#ifndef SIMULATOR_H
#define SIMULATOR_H

#define NUMMEMORY 65536 /* maximum number of words in memory */
#define NUMREGS 8 /* number of machine registers */

/* Define class-like structure for MachineState */
typedef struct {
    int pc;
    int mem[NUMMEMORY];
    int reg[NUMREGS];
    int numMemory;
    int highestNemMemory;
} MachineState;

/* Function prototypes */
void initMachineStates(MachineState *state);
void loadMemory(MachineState *state, char *filename);
int fetch(MachineState *state);
void updatePC(MachineState *state);
void halt();
void printState(MachineState *state);

#endif