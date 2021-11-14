import static java.lang.Math.cos;
import static java.lang.Math.min;
import static java.lang.Math.round;
import static java.lang.Math.sin;

import java.util.TreeMap;

public class Spirograph extends Window {
  private final float fixedRadius;
  private final float movingRadius;
  private final float penOffset;
  private final int max;

  public Spirograph(int fixedRadius, int movingRadius, int penOffset) throws Exception {
    if (penOffset > movingRadius) {
      throw new Exception(
          "Cannot construct spirograph that has a bigger pen offset than a radius to contain it.");
    }
    this.fixedRadius = fixedRadius;
    this.movingRadius = movingRadius;
    this.penOffset = penOffset;
    max = 2 * (Math.abs(fixedRadius - movingRadius) + Math.abs(penOffset));
  }

  TreeMap<Coordinate, Character> generate() {
    TreeMap<Coordinate, Character> outputs = new TreeMap<>();
    smallestX = max;
    largestX = -max;
    int x;
    int y;
    int stepNum = 30000000;
    // I don't trust Java enough to optimise these out.
    double fixedMinusMoving = fixedRadius - movingRadius;
    double fixedMinusMovingDivMoving = fixedMinusMoving / movingRadius;
    double step =
        ((Math.PI * 4)
                * (Math.max(movingRadius, fixedRadius) / Math.min(movingRadius, fixedRadius)))
            / stepNum;
    double t;
    for (int i = 0; i <= stepNum; i++) {
      t = i * step;
      x = (int) round(fixedMinusMoving * cos(t) + penOffset * cos(fixedMinusMovingDivMoving * t));
      y = (int) round(fixedMinusMoving * sin(t) - penOffset * sin(fixedMinusMovingDivMoving * t));
      Coordinate coordinate = new Coordinate(x + max / 2, y + max / 2, max);
      smallestX = min(smallestX, x + max / 2);
      largestX = Math.max(largestX, x + max / 2);
      outputs.put(coordinate, 'O');
    }
    return outputs;
  }
}
