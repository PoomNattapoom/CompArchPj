#ifndef SIMULATOR_H
#define SIMULATOR_H

#define NUMMEMORY 65536 /* maximum number of words in memory */
#define NUMREGS 8 /* number of machine registers */

/* Define class-like structure for MachineState */
typedef struck {
    int pc;
    int mem[NUMMEMORY];
    int reg[NUMREGS];
    int numMemory;
} machineState;

/* Function prototypes */
void initMachineStates(MachineState *state);
void loadMemory(Machine *state, char *filename);
int fetch(MachineState *state);
void UpdatePC(Machine *state);
void halt();
void printState(MachineState *state);

#endif