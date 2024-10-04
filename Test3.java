public class Test3 {
  public static void main(String[] args) {
    int number = -3; // your decimal number

    // Check if the number fits in 16-bit signed range
    if (number < -32768 || number > 32767) {
      System.out.println("Number out of 16-bit range");
      return;
    }

    // Convert to 2's complement 16-bit representation
    short twosComplement = (short) number; // short is 16-bit in Java

    // Print the result in 16-bit binary format
    String binaryString = String.format("%16s", Integer.toBinaryString(twosComplement & 0xFFFF))
        .replace(' ', '0');

    System.out.println(binaryString);
  }
}