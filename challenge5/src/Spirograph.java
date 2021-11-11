import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

public class Spirograph {
  private final HashMap<Coordinate, Character> output = new HashMap<>();
  private final int fixedRadius;
  private final int movingRadius;
  private final int offset;
  public Spirograph(int fixedRadius, int movingRadius, int offset) {
    this.fixedRadius = fixedRadius;
    this.movingRadius = movingRadius;
    this.offset = offset;
  }

  public void generate() {
    int x;
    int y;
    for (double t = 0; t < Math.PI * 2; t+=0.0000001) {
      x = (int)round((fixedRadius-movingRadius)*cos(t) + offset*cos(((fixedRadius-movingRadius)/(float)movingRadius)*t));
      y = (int)round((fixedRadius-movingRadius)*sin(t) - offset*sin(((fixedRadius-movingRadius)/(float)movingRadius)*t));
      Coordinate coordinate = new Coordinate(x + fixedRadius, y + fixedRadius, fixedRadius*2);
      output.put(coordinate, 'O');
    }
    for (int yy = 0; yy < fixedRadius * 2; yy++) {
      StringBuilder res = new StringBuilder();
      for (int xx = 0; xx < fixedRadius * 2; xx++) {
        res.append(output.getOrDefault(new Coordinate(xx,yy, fixedRadius * 2), ' '));
      }
      System.out.println(res);
    }
  }
}
