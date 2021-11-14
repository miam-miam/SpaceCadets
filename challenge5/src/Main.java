import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

  /**
   * @param args Currently unused.
   * @throws Exception If a pen offset is larger than the moving radius.
   */
  public static void main(String[] args) throws Exception {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    BMPReader myWelcome = new BMPReader("res/welcome.bmp");
    myWelcome.run();
    int fixedRadius = readIntegerFromCmd(br, "fixed radius");
    int movingRadius = readIntegerFromCmd(br, "moving radius");
    int penOffset = readIntegerFromCmd(br, "pen offset");
    BMPReader myGenerating = new BMPReader("res/generating.bmp");
    myGenerating.run();
    Spirograph mySpirograph = new Spirograph(fixedRadius, movingRadius, penOffset);
    mySpirograph.run();
  }

  /**
   * Reads integers from a bufferedReader (most likely stdin). Will repeat until an integer is
   * given.
   *
   * @param br The reader from which to get the integer.
   * @param name The name of the integer (used to tell the user what info they should give)
   * @return The Integer the user gave.
   */
  private static Integer readIntegerFromCmd(BufferedReader br, String name) {
    while (true) {
      try {
        System.out.println("Enter your " + name + ":");
        String input = br.readLine();
        return Integer.valueOf(input);
      } catch (NumberFormatException | IOException ignored) {
      }
    }
  }
}
