import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Test {

  public static int BinaryToDecimal(String bin) {
    int decimalNumber = 0, i = 0;
    long remainder;
    long num = Long.parseLong(bin);
    while (num != 0) {
      remainder = num % 10;
      num /= 10;
      decimalNumber += remainder * Math.pow(2, i);
      ++i;
    }

    return decimalNumber;
  }

  public static void main(String[] args) {
    String bin = "1010101010101010101010101";
    // System.out.println(BinaryToDecimal(bin));
    long dec = Long.parseLong(bin, 2);
    System.out.println(dec);
  }
}
