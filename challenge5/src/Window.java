import java.util.TreeMap;

/**
 * An abstract class that contains code to make all outputs generate look like they are in a
 * graphical window.
 */
abstract class Window {
  protected int smallestX; // The smallest x value of the set of points.
  protected int largestX; // The largest x value of the set of points.

  /**
   * Create a set of points to then print @return The set of points, a TreeMap is used so that it
   * can be sorted.
   */
  abstract TreeMap<Coordinate, Character> generate();

  /** Adds the window header to stdout. @param name the name of the header */
  private void windowHeader(String name) {
    // Making sure there is enough space for everything.
    if ((largestX - name.length()) / 2 - 5 <= 0) {
      name = "";
    }
    if (largestX < 5) {
      largestX = 5;
    }
    System.out.println("_".repeat(largestX + 2));
    System.out.println(
        "|"
            + " ".repeat((largestX - name.length()) / 2)
            + name
            + " ".repeat((largestX - name.length()) / 2 - 5)
            + "- ☐ x"
            + " |");
    System.out.println("|" + "‾".repeat(largestX) + " " + "|");
  }

  /**
   * Draws a set of points onto stdout @param outputs the set of points. We output stuff by getting
   * the first point and then repeating a space until the coordinate can be placed. It used to go
   * through height*width times but this was very slow.
   */
  private void draw(TreeMap<Coordinate, Character> outputs) {
    windowHeader("Spirograph");
    System.out.print("|");
    int x = 0;
    int y = outputs.firstKey().y;
    int xTo;
    int yTo;
    for (Coordinate coordinate : outputs.keySet()) {
      xTo = coordinate.x - smallestX;
      yTo = coordinate.y;
      for (; y < yTo; y++) {
        System.out.print(" ".repeat(largestX - x + 1) + "|\n|");
        x = 0;
      }
      System.out.print(" ".repeat(xTo - x));
      x = xTo + 1;
      System.out.print(outputs.get(coordinate));
    }
    System.out.println(" ".repeat(largestX - x + 1) + "|\n" + "‾".repeat(largestX + 2));
  }

  public void run() {
    draw(generate());
  }
}
