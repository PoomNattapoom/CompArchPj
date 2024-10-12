        lw      0       1       mcand   # Load multiplicand (mcand) into $1
        lw      0       2       mplier  # Load multiplier (mplier) into $2
        lw      0       6       pos1    # Load 1 into $6 
loop    beq     4       2       done    # If $4 == mplier, done
        add     3       1       3       # result = result + mcand
        add     4       6       4       # $4 = $4 + 1
        beq     0       0       loop    # Jump back to loop 
done    halt                            # Stop execution
pos1    .fill   1     
mcand   .fill   56    # Multiplicand 
mplier  .fill   40  # Multiplier