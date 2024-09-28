#include <studio.h>
#include <stlib.h>
#include <string.h>
#include "Simulator.h"

#define MAXLINELENGTH 1000

int main (int argc, char *argv) {
    if (argc != 2) {
        printf("error : usage : %s <machine-code file>\n", argv[0]);
        exit(1);
    }

    /* Initialize simulator and load machine code */
    MachineState state;
    initMachineStates(&state);
    loadMemory(&state, argv[1]);

    /* Simulate machine instructions */
    while(1) {
        printState(&state); // Print state before executing instruction

        int instruction = fetch(&state); // Fetch instruction

        if (instruction == 0) { // Assuming '0' represents the halt instruction
            halt();
            break;
        }

        UpdatePC(&state); // Update PC after executing instruction
    }

    printState(&state); // Print state before exiting
    return 0;
}

/* Initialize machine state: Set PC to 0 and registers to 0 */
void initMachineState(MachineState *state) {
    state->pc = 0;
    memset(state->reg, sizeof(state->reg));
}

/* Load machine code into memory from file */
void loadMemory(MachineState *state, char *filename) {
    File *filePtr = fopen(filename, "r");
    char line[MAXLINELENGTH];

    if(filePtr == null) {
        printf("error : can't open file %s\n", filename);
        perror("fopen");
        exit(1);
    }

    /* Read the entire machine-code file into memory */
      for (state->numMemory = 0; fgets(line, MAXLINELENGTH, filePtr) != NULL;
         state->numMemory++) {
        if (sscanf(line, "%d", &state->mem[state->numMemory]) != 1) {
            printf("error in reading address %d\n", state->numMemory);
            exit(1);
        }
        printf("memory[%d]=%d\n", state->numMemory, state->mem[state->numMemory]);
    }

    fclose(filePtr);
}

/* Print the current state of the machine */
void printState(MachineState *statePtr) {
    int i;
    printf("\n@@@\nstate:\n");
    printf("\tpc %d\n", statePtr->pc);
    printf("\tmemory:\n");
    for(i=0; i<statePtr->numMemory; i++) {
        printf("\t\tmem[ %d ] %d\n", i, statePtr->mem[i]);
    }
    printf("\tregisters:\n");
    for(i=0; i<NUMREGS; i++) {
        printf("\t\treg[ %d ] %d\n", i, statePtr->reg[i]);
    }
    printf("end state\n");
}
