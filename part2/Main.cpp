#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "Simulator.h"

#define MAXLINELENGTH 1000

int main(int argc, char *argv[])
{
  if (argc != 2)
  {
    printf("error : usage : %s <machine-code file>\n", argv[0]);
    exit(1);
  }

  /* Initialize simulator and load machine code */
  MachineState state;
  initMachineStates(&state);
  loadMemory(&state, argv[1]);

  int instructionCount = 0 ;
  /* Simulate machine instructions */
  while (1)
  {
    if(instructionCount==5000)break;
    printState(&state); // Print state before executing instruction
    
    // printf("Press Enter to continue...\n");
    // getchar(); // Wait for user to press Enter


    int instruction = fetch(&state); // Fetch instruction

    if (instruction == 0)
    { // Assuming '0' represents the halt instruction
      halt();
      printf("total of %d instructions executed\n", instructionCount+1);
      break;
    }

    int opcode = (instruction >> 22) & 0x7; // 22-24
    int regA = (instruction >> 19) & 0x7;   // 19-21
    int regB = (instruction >> 16) & 0x7;   // 16-18
    int destReg = 0, offset = 0;

    switch (opcode)
    {
    case 0:                        // ADD
      destReg = instruction & 0x7; // rd is in last 3 bits
      // state.reg[destReg] = state.reg[regA] + state.reg[regB];
      state.reg[regA] = state.reg[destReg] + state.reg[regB];
      break;

    case 1:                        // NAND
      destReg = instruction & 0x7; // Destination register is in the last 3 bits
      state.reg[destReg] = ~(state.reg[regA] & state.reg[regB]); // NAND operation
      break;


    case 2: // LW
      offset = instruction & 0xFFFF;
      printf("Opcode LW detected. Offset before sign extension: %d\n", state.reg[regA] + offset);

      if (offset & (1 << 15))
      {
        offset -= (1 << 16);
      }
      printf("Offset after sign extension: %d\n", offset);
      printf("regA = %d, regB = %d, regA+offset = %d\n",state.reg[regA],state.reg[regB],state.reg[regA] + offset);
      state.reg[regB] = state.mem[state.reg[regA] + offset];
      break;

    case 3: // SW
      offset = instruction & 0xFFFF;
      printf("Opcode SW detected. Offset before sign extension: %d\n", state.reg[regA] + offset);
      printf("regA = %d, regA+offset = %d\n",state.reg[regA],state.reg[regA] + offset);

      if (offset & (1 << 15))
      {
        offset -= (1 << 16);
      }
      printf("Offset after sign extension: %d\n", offset);
      state.mem[state.reg[regA] + offset] = state.reg[regB];
      if (state.numMemory + state.reg[regA] > state.highestNumMemory) {
        state.highestNumMemory = state.numMemory + state.reg[regA];
      } //update size for print more mem
      break;


    case 4: // beq (branch if equal)
      offset = instruction & 0xFFFF;
      printf("Opcode BEQ detected. Offset before sign extension: %d\n", state.reg[regA] + offset);

      if (offset & (1 << 15)) {
        offset -= (1 << 16);
      }
      printf("Offset after sign extension: %d\n", offset);
      printf("BEQ Check: regA = %d, regB = %d\n", state.reg[regA], state.reg[regB]);
      printf("PC = %d, Offset = %d\n", state.pc, offset);

      if (state.reg[regA] == state.reg[regB]) {
        printf("Jumping to address: %d\n", state.pc + offset);
        state.pc = state.pc + offset;
      } else {
        printf("Not jumping.\n");
      }
      break;


    case 5:  // JALR
      if (regA == regB) {
        int tempPC = state.pc + 1;
        state.pc = state.reg[regA];  // Jump to address in regA
        state.reg[regB] = tempPC;     // Store PC+1 in regB
      } else {
        state.reg[regB] = state.pc + 1; // Store PC+1 in regB
        int targetAddress = state.reg[regA] - 1; // Adjust target address
        state.pc = targetAddress; // Jump to adjusted address
      }
      break;


    case 6: // HALT
      printf("machine halted\n");
      printf("total of %d instructions executed\n", instructionCount+1);
      return 0;

    case 7: // NOOP
      break;

    default:
      printf("error: illegal opcode %d\n", opcode);
      return 1;
    }
    printf("____opcode is %d",opcode,"_____\n");
    //printState(&state);
    instructionCount++;
    updatePC(&state); // Update PC after executing instruction
    //test


  }

  printState(&state); // Print state before exiting
  return 0;
  
}

/* Initialize machine state: Set PC to 0 and registers to 0 */
void initMachineStates(MachineState *state)
{
  state->pc = 0;
  memset(state->reg, 0, sizeof(state->reg));
}

/* Load machine code into memory from file */
void loadMemory(MachineState *state, char *filename)
{
  FILE *filePtr = fopen(filename, "r");
  char line[MAXLINELENGTH];

  if (filePtr == NULL)
  {
    printf("error : can't open file %s\n", filename);
    perror("fopen");
    exit(1);
  }

  /* Read the entire machine-code file into memory */
  for (state->numMemory = 0; fgets(line, MAXLINELENGTH, filePtr) != NULL;
       state->numMemory++)
  {
    if (sscanf(line, "%d", &state->mem[state->numMemory]) != 1)
    {
      printf("error in reading address %d\n", state->numMemory);
      exit(1);
    }
    printf("memory[%d]=%d\n", state->numMemory, state->mem[state->numMemory]);
  }

  fclose(filePtr);
  state->highestNumMemory = state->numMemory;
}

/* Print the current state of the machine */
void printState(MachineState *statePtr)
{
  int i;
  printf("\n@@@\nstate:\n");
  printf("\tpc %d\n", statePtr->pc);
  printf("\tmemory:\n");
  for (i = 0; i < statePtr->highestNumMemory; i++)
  {
    printf("\t\tmem[ %d ] %d\n", i, statePtr->mem[i]);
  }
  printf("\tregisters:\n");
  for (i = 0; i < NUMREGS; i++)
  {
    printf("\t\treg[ %d ] %d\n", i, statePtr->reg[i]);
  }
  printf("end state\n\n");
}

/* Fetch the instruction at the current PC */
int fetch(MachineState *state)
{
  return state->mem[state->pc]; // return the instruction at PC
}

/* Halt the simulator */
void halt()
{
  printf("Halt instruction encountered. Stopping simulation. \n");
}

/* Increment the PC to point to the next instruction */
void updatePC(MachineState *state)
{
  state->pc += 1; // increment PC
}