import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.min;
import static java.lang.Math.round;
import static java.lang.Math.sin;

import java.util.TreeMap;

class Spirograph extends Window {
  private final float fixedRadius;
  private final float movingRadius;
  private final float penOffset;
  private final int max;

  /**
   * Creates a spirograph that can then be drawn.
   *
   * @param fixedRadius the radius of the fixed circle.
   * @param movingRadius the radius of the moving circle.
   * @param penOffset the offset of the pen in the moving circle.
   * @throws Exception If abs(penOffset) > abs(movingRadius)
   */
  public Spirograph(int fixedRadius, int movingRadius, int penOffset) throws Exception {
    if (abs(penOffset) > abs(movingRadius)) {
      throw new Exception(
          "Cannot construct spirograph that has a bigger pen offset than a radius to contain it.");
    }
    this.fixedRadius = fixedRadius;
    this.movingRadius = movingRadius;
    this.penOffset = penOffset;
    // The max is simply calculated by looking at the maximum possible
    // values of the parametric equations, this may not actually be the
    // maximum value that is produced by the equation.
    max = 2 * (Math.abs(fixedRadius - movingRadius) + Math.abs(penOffset));
  }

  /** @return The tree map containing all the points that the spirograph produces. */
  @Override
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
    // I don't think there is a way to know exactly the period of the functions
    // (correct me if I am wrong). This works for most cases and doesn't try to overestimate too
    // much.

    double t;
    for (int i = 0; i <= stepNum; i++) {
      t = i * step;
      // The parametric eqs, max/2 is used to ensure x and y are always positive.
      x =
          (int) round(fixedMinusMoving * cos(t) + penOffset * cos(fixedMinusMovingDivMoving * t))
              + max / 2;
      y =
          (int) round(fixedMinusMoving * sin(t) - penOffset * sin(fixedMinusMovingDivMoving * t))
              + max / 2;
      Coordinate coordinate = new Coordinate(x, y, max);
      smallestX = min(smallestX, x);
      largestX = Math.max(largestX, x);
      outputs.put(coordinate, '8');
    }
    return outputs;
  }
}
