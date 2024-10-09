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

  //int count = 0 ;
  /* Simulate machine instructions */
  while (1)
  {
    // if(count==9)break;
    // count++;
    //printState(&state); // Print state before executing instruction

    int instruction = fetch(&state); // Fetch instruction

    if (instruction == 0)
    { // Assuming '0' represents the halt instruction
      halt();
      break;
    }

    int opcode = (instruction >> 22) & 0x7; // 22-24
    int regA = (instruction >> 19) & 0x7;   // 19-21
    int regB = (instruction >> 16) & 0x7;   // 16-18
    int destReg, offset;

    switch (opcode)
    {
    case 0:                        // ADD
      destReg = instruction & 0x7; // rd is in last 3 bits
      state.reg[destReg] = state.reg[regA] + state.reg[regB];
      break;

    case 1:                        // SUB
      destReg = instruction & 0x7; // rd is in last 3 bits
      state.reg[destReg] = state.reg[regA] - state.reg[regB];
      break;

    case 2: // LW
      offset = instruction & 0xFFFF;
      if (offset & (1 << 15))
      {
        offset -= (1 << 16);
      }
      state.reg[regB] = state.mem[state.reg[regA] + offset];
      break;

    case 3: // SW
      offset = instruction & 0xFFFF;
      printf("Opcode SW detected. Offset before sign extension: %d\n", offset);

      if (offset & (1 << 15))
      {
        offset -= (1 << 16);
      }

      printf("Offset after sign extension: %d\n", offset);

      state.mem[state.reg[regA] + offset] = state.reg[regB];
      state.numMemory= state.numMemory+=state.reg[7]; //update size for print more mem
      break;


    case 4:  // BEQ
      offset = instruction & 0xFFFF;
      if (offset & (1 << 15)) {
        offset -= (1 << 16);  // Sign-extend the offset
      }
      if (state.reg[regA] == state.reg[regB]) {
        if (offset == 0) {
            printf("warning: BEQ has zero offset, preventing infinite loop\n");
            state.pc += 1; // Move to next instruction to avoid looping
        } else {
            state.pc += offset;  // Apply the branch if condition is met
        }
      } else {
        state.pc += 1; // No branch, move to next instruction
      }
      break;

      case 5:  // JALR
        if (state.pc == state.reg[regB]) {
          printf("Warning - Jalr infinity loop, Please check you code\n");
          return 0;
        }
        state.reg[regB] = state.pc + 1;
        state.pc = state.reg[regA]-1;  // Jump to address in regA-1 then updatePC
        break;

    case 6: // HALT
      printf("machine halted\n");
      printf("total of %d instructions executed\n", state.pc);
      return 0;

    case 7: // NOOP
      break;

    default:
      printf("error: illegal opcode %d\n", opcode);
      return 1;
    }
    printf("____opcode is %d",opcode,"_____\n");
    printState(&state);
    updatePC(&state); // Update PC after executing instruction
  }

  //printState(&state); // Print state before exiting
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
}

/* Print the current state of the machine */
void printState(MachineState *statePtr)
{
  int i;
  printf("\n@@@\nstate:\n");
  printf("\tpc %d\n", statePtr->pc);
  printf("\tmemory:\n");
  for (i = 0; i < statePtr->numMemory; i++)
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