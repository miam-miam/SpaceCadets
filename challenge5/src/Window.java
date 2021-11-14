import java.util.TreeMap;

public abstract class Window {
  protected int smallestX;
  protected int largestX;

  TreeMap<Coordinate, Character> generate() {
    TreeMap<Coordinate, Character> output = new TreeMap<>();
    output.put(new Coordinate(0, 0, 0), 'O');
    return output;
  }

  private void windowHeader(String name) {
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
