import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;

import java.util.TreeMap;

public class Spirograph {
  private final TreeMap<Coordinate, Character> outputs = new TreeMap<>();
  private final int fixedRadius;
  private final int movingRadius;
  private final int penOffset;
  private final int max;
  public Spirograph(int fixedRadius, int movingRadius, int penOffset) {
    this.fixedRadius = fixedRadius;
    this.movingRadius = movingRadius;
    this.penOffset = penOffset;
    max = 2 * (Math.abs(fixedRadius - movingRadius) + Math.abs(penOffset));
  }

  public void generate() {
    int x;
    int y;
    for (double t = 0; t < Math.PI * 2; t+=0.000001) {
      x = (int)round((fixedRadius-movingRadius)*cos(t) + penOffset *cos(((fixedRadius-movingRadius)/(float)movingRadius)*t));
      y = (int)round((fixedRadius-movingRadius)*sin(t) - penOffset *sin(((fixedRadius-movingRadius)/(float)movingRadius)*t));
      Coordinate coordinate = new Coordinate(x + max/2, y + max/2, max);

      outputs.put(coordinate, 'O');
    }

    x = 0;
    y = outputs.firstKey().y;
    int xTo;
    int yTo;
    for (Coordinate coordinate : outputs.keySet()) {
      xTo = coordinate.x;
      yTo = coordinate.y;
      for(; y < yTo; y++) {
        System.out.println();
        x = 0;
      }
      System.out.print(" ".repeat(xTo - x));
      x = xTo + 1;
      System.out.print(outputs.get(coordinate));
    }
    System.out.println();
  }
}
