import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

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

  public static Integer readIntegerFromCmd(BufferedReader br, String name) {
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
